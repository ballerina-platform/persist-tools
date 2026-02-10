/*
 *  Copyright (c) 2024, WSO2 LLC. (http://www.wso2.com).
 *
 *  WSO2 LLC. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package io.ballerina.persist.cmd;

import io.ballerina.cli.BLauncherCmd;
import io.ballerina.persist.BalException;
import io.ballerina.persist.PersistToolsConstants;
import io.ballerina.persist.configuration.PersistConfiguration;
import io.ballerina.persist.introspect.Introspector;
import io.ballerina.persist.introspect.IntrospectorBuilder;
import io.ballerina.persist.models.Module;
import io.ballerina.persist.nodegenerator.SourceGenerator;
import picocli.CommandLine;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;

import static io.ballerina.persist.PersistToolsConstants.COMPONENT_IDENTIFIER;
import static io.ballerina.persist.PersistToolsConstants.PERSIST_DIRECTORY;
import static io.ballerina.persist.utils.BalProjectUtils.validateBallerinaProject;
import static io.ballerina.persist.utils.BalProjectUtils.validatePullCommandOptions;
import static io.ballerina.persist.utils.DatabaseConnector.readDatabasePassword;

@CommandLine.Command(name = "pull", description = "Create model.bal file according to given database schema")
public class Pull implements BLauncherCmd {
    private static final PrintStream errStream = System.err;
    public static final String YELLOW_COLOR = "\u001B[33m";
    public static final String RESET_COLOR = "\u001B[0m";
    public static final String CYAN_COLOR = "\u001B[36m";

    private final String sourcePath;

    private static final String COMMAND_IDENTIFIER = "persist-pull";

    // Table selection prompt message
    private static final String TABLE_SELECTION_PROMPT =
            "Select tables to introspect:" + System.lineSeparator() +
            "  • Enter table names separated by commas (e.g., users,orders,products)" + System.lineSeparator() +
            "  • Enter table numbers separated by commas (e.g., 1,3,5)" + System.lineSeparator() +
            "  • Enter 'all' to introspect all tables" + System.lineSeparator() +
            "  • Press Enter without input to introspect all tables" + System.lineSeparator() +
            "  • Enter 'q' or 'quit' to abort" + System.lineSeparator() +
            System.lineSeparator() + "Your selection: ";

    public Pull() {
        this("");
    }

    public Pull(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    @CommandLine.Option(names = { "--datastore" })
    private String datastore = "mysql";

    @CommandLine.Option(names = { "--host" })
    private String host;

    @CommandLine.Option(names = { "--port" })
    private String port;

    @CommandLine.Option(names = { "--user" })
    private String user;

    @CommandLine.Option(names = { "--database" })
    private String database;

    @CommandLine.Option(names = { "--tables" }, arity = "0..1")
    private String tables;

    @CommandLine.Option(names = { "-h", "--help" }, hidden = true)
    private boolean helpFlag;

    @CommandLine.Option(names = { "--model" })
    private String model;

    @Override
    public void execute() {
        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
        if (helpFlag) {
            String commandUsageInfo = BLauncherCmd.getCommandUsageInfo(COMMAND_IDENTIFIER, Pull.class.getClassLoader());
            errStream.println(commandUsageInfo);
            return;
        }

        try {
            validatePullCommandOptions(datastore, host, port, user, database);
        } catch (BalException e) {
            errStream.println("ERROR: invalid option(s): " + System.lineSeparator() + e.getMessage());
            return;
        }

        String password = readDatabasePassword(scanner, errStream);

        try {
            validateBallerinaProject(Paths.get(this.sourcePath));
        } catch (BalException e) {
            errStream.println(e.getMessage());
            return;
        }

        Path persistDir = Paths.get(this.sourcePath, PERSIST_DIRECTORY);
        Path targetModelDir = persistDir;
        String modelFileName = "model.bal";
        String modelDisplayPath = "persist/model.bal";

        if (!Files.exists(persistDir)) {
            try {
                Files.createDirectory(persistDir.toAbsolutePath());
            } catch (IOException e) {
                errStream.println("ERROR: failed to create the persist directory. " + e.getMessage());
                return;
            }
        }

        // Handle model-specific directory structure
        if (model != null && !model.isBlank()) {
            try {
                io.ballerina.persist.utils.BalProjectUtils.validateModelName(model);
                targetModelDir = persistDir.resolve(model);
                modelDisplayPath = "persist/" + model + "/model.bal";

                if (!Files.exists(targetModelDir)) {
                    Files.createDirectory(targetModelDir.toAbsolutePath());
                }
            } catch (BalException e) {
                errStream.println("ERROR: " + e.getMessage());
                return;
            } catch (IOException e) {
                errStream.println("ERROR: failed to create the model directory '" + model + "'. " + e.getMessage());
                return;
            }
        }

        boolean modelFile = Files.exists(targetModelDir.resolve(modelFileName));
        if (modelFile) {
            errStream.print(YELLOW_COLOR + "WARNING A " + modelDisplayPath + " file already exists. " +
                    "Continuing would overwrite it. Do you wish to continue? (y/n) " + RESET_COLOR);
            String input = scanner.nextLine();
            if (!(input.toLowerCase(Locale.ENGLISH).equals("y") || input.toLowerCase(Locale.ENGLISH).equals("yes"))) {
                errStream.println("Introspection aborted.");
                return;
            }
            errStream.println("Continuing...");
        }

        // Build introspector and configuration using the builder pattern
        Introspector introspector;
        try {
            IntrospectorBuilder builder = IntrospectorBuilder.newBuilder()
                    .withDatastore(datastore)
                    .withHost(host)
                    .withPort(port)
                    .withUser(user)
                    .withPassword(password)
                    .withDatabase(database)
                    .withSourcePath(sourcePath);

            // Add tables if specified
            if (this.tables != null && !this.tables.trim().isEmpty()) {
                builder.withTables(this.tables);
            }

            introspector = builder.build();

            // Log default port usage if applicable
            if (Objects.isNull(port)) {
                switch (datastore) {
                    case PersistToolsConstants.SupportedDataSources.MYSQL_DB:
                        errStream.println("MySQL database introspection operates on the default port 3306");
                        break;
                    case PersistToolsConstants.SupportedDataSources.POSTGRESQL_DB:
                        errStream.println("PostgreSQL database introspection operates on the default port 5432");
                        break;
                    case PersistToolsConstants.SupportedDataSources.MSSQL_DB:
                        errStream.println("MSSQL database introspection operates on the default port 1433");
                        break;
                    default:
                        // No default port message for unsupported datastores
                        break;
                }
            }
        } catch (BalException e) {
            errStream.println("ERROR: " + e.getMessage());
            return;
        }

        PersistConfiguration persistConfigurations = introspector.getPersistConfiguration();

        // Handle table selection: --tables flag controls the behavior
        if (this.tables != null && this.tables.trim().isEmpty()) {
            // --tables with no value: trigger interactive mode
            if (!handleInteractiveTableSelection(scanner, introspector, persistConfigurations)) {
                return;
            }
        }

        Module entityModule = null;
        try {
            entityModule = introspector.introspectDatabase();
        } catch (BalException e) {
            errStream.printf("ERROR: failed to introspect database: %s%n", e.getMessage());
            return;
        }

        SourceGenerator sourceGenerator = new SourceGenerator(sourcePath, targetModelDir,
                "Introspect.db", entityModule);

        try {
            sourceGenerator.createDbModel();
        } catch (BalException e) {
            errStream.printf(String.format("ERROR: failed to generate model for introspected database: %s%n",
                    e.getMessage()));
            return;
        }
        errStream.println("Introspection complete! The " + modelDisplayPath + " file created successfully.");
    }

    /**
     * Handles interactive table selection when --tables flag is present without a value.
     * Fetches available tables, prompts the user, and updates the configuration.
     *
     * @param scanner               the Scanner for reading user input
     * @param introspector          the database introspector
     * @param persistConfigurations the persist configuration to update
     * @return true if selection was successful, false if operation should abort
     */
    private boolean handleInteractiveTableSelection(Scanner scanner, Introspector introspector,
                                                     PersistConfiguration persistConfigurations) {
        try {
            String[] availableTables = introspector.getAvailableTables();
            if (availableTables.length == 0) {
                errStream.println("ERROR: No tables found in the database.");
                return false;
            }

            String selectedTablesInput = promptForTableSelection(scanner, availableTables);
            if (selectedTablesInput == null) {
                errStream.println("Introspection aborted.");
                return false;
            }

            if (selectedTablesInput.trim().isEmpty()) {
                errStream.println("ERROR: No valid tables selected. Please provide valid table names or indices.");
                return false;
            }

            if (!selectedTablesInput.equalsIgnoreCase("all")) {
                persistConfigurations.setSelectedTables(selectedTablesInput);
            }
            return true;
        } catch (BalException e) {
            errStream.printf("ERROR: failed to fetch available tables: %s%n", e.getMessage());
            return false;
        }
    }

    /**
     * Prompts the user to select tables from the available tables list.
     * Displays up to 50 tables and allows users to enter table names, indices, or
     * "all".
     *
     * @param scanner         the Scanner for reading user input
     * @param availableTables array of all available table names
     * @return comma-separated string of selected table names, "all", or null if
     *         aborted
     */
    private String promptForTableSelection(Scanner scanner, String[] availableTables) {
        int totalTables = availableTables.length;
        int displayLimit = 50;
        boolean isLimited = totalTables > displayLimit;

        errStream.println(CYAN_COLOR + "\nAvailable tables in the database:" + RESET_COLOR);
        errStream.println("──────────────────────────────────");

        for (int i = 0; i < Math.min(totalTables, displayLimit); i++) {
            errStream.printf("  %d. %s%n", i + 1, availableTables[i]);
        }

        if (isLimited) {
            errStream.println(YELLOW_COLOR + "  ... and " + (totalTables - displayLimit) +
                    " more tables" + RESET_COLOR);
        }

        errStream.println("──────────────────────────────────");
        errStream.printf("Total: %d table%s%n%n", totalTables, totalTables == 1 ? "" : "s");

        errStream.print(TABLE_SELECTION_PROMPT);

        String input = scanner.nextLine().trim();

        // Handle abort
        if (input.equalsIgnoreCase("q") || input.equalsIgnoreCase("quit")) {
            return null;
        }

        // Handle empty input or "all" - means select all tables
        if (input.isEmpty() || input.equalsIgnoreCase("all")) {
            return "all";
        }

        // Check if input contains numbers (indices)
        if (input.matches("[0-9,\\s]+")) {
            return parseTableIndices(input, availableTables);
        }

        // Otherwise, treat as table names
        return input;
    }

    /**
     * Parses comma-separated table indices and returns the corresponding table
     * names.
     *
     * @param input           comma-separated indices (e.g., "1,3,5")
     * @param availableTables array of available table names
     * @return comma-separated table names
     */
    private String parseTableIndices(String input, String[] availableTables) {
        String yellowColor = YELLOW_COLOR;
        String resetColor = RESET_COLOR;
        String[] indices = input.split(",");
        List<String> selectedTables = new ArrayList<>();

        for (String indexStr : indices) {
            try {
                int index = Integer.parseInt(indexStr.trim());
                if (index >= 1 && index <= availableTables.length) {
                    selectedTables.add(availableTables[index - 1]);
                } else {
                    errStream.println(yellowColor + "WARNING: Index " + index +
                            " is out of range. Skipping." + resetColor);
                }
            } catch (NumberFormatException e) {
                errStream.println(yellowColor + "WARNING: Invalid index '" + indexStr.trim() +
                        "'. Skipping." + resetColor);
            }
        }

        return String.join(",", selectedTables);
    }

    @Override
    public String getName() {
        return COMPONENT_IDENTIFIER;
    }

    @Override
    public void printLongDesc(StringBuilder out) {
        out.append("Generate model definition by introspecting the database").append(System.lineSeparator());
        out.append(System.lineSeparator());
    }

    @Override
    public void printUsage(StringBuilder stringBuilder) {
        stringBuilder.append("  ballerina " + COMPONENT_IDENTIFIER +
                " pull").append(System.lineSeparator());
    }

    @Override
    public void setParentCmdParser(CommandLine commandLine) {

    }

}
