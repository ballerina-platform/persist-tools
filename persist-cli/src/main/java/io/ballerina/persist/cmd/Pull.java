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
import io.ballerina.persist.configuration.DatabaseConfiguration;
import io.ballerina.persist.configuration.PersistConfiguration;
import io.ballerina.persist.introspect.Introspector;
import io.ballerina.persist.introspect.MsSqlInstrospector;
import io.ballerina.persist.introspect.MySqlIntrospector;
import io.ballerina.persist.introspect.PostgreSqlIntrospector;
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

@CommandLine.Command(
        name = "pull",
        description = "Create model.bal file according to given database schema")
public class Pull implements BLauncherCmd {
    private static final PrintStream errStream = System.err;

    private final String sourcePath;

    private static final String COMMAND_IDENTIFIER = "persist-pull";

    public Pull() {
        this("");
    }

    public Pull(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    @CommandLine.Option(names = {"--datastore"})
    private String datastore = "mysql";

    @CommandLine.Option(names = {"--host"})
    private String host;

    @CommandLine.Option(names = {"--port"})
    private String port;

    @CommandLine.Option(names = {"--user"})
    private String user;

    @CommandLine.Option(names = {"--database"})
    private String database;

    @CommandLine.Option(names = {"--tables"}, arity = "0..1",
                        description = "Enable table selection. Accepts comma-separated table names " +
                                "or triggers interactive mode if no value provided")
    private String tables;

    @CommandLine.Option(names = { "-h", "--help" }, hidden = true)
    private boolean helpFlag;

    @Override
    public void execute() {
        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
        if (helpFlag) {
            String commandUsageInfo = BLauncherCmd.getCommandUsageInfo(COMMAND_IDENTIFIER, Pull.class.getClassLoader());
            errStream.println(commandUsageInfo);
            return;
        }

        Introspector introspector;
        switch (this.datastore) {
            case PersistToolsConstants.SupportedDataSources.MYSQL_DB:
                introspector = new MySqlIntrospector();
                if (Objects.isNull(port)) {
                    port = "3306";
                    errStream.println("MySQL database introspection operates on the default port 3306");
                }
                break;
            case PersistToolsConstants.SupportedDataSources.POSTGRESQL_DB:
                introspector = new PostgreSqlIntrospector();
                if (Objects.isNull(port)) {
                    port = "5432";
                    errStream.println("PostgreSQL database introspection operates on the default port 5432");
                }
                break;
            case PersistToolsConstants.SupportedDataSources.MSSQL_DB:
                introspector = new MsSqlInstrospector();
                if (Objects.isNull(port)) {
                    port = "1433";
                    errStream.println("MSSQL database introspection operates on the default port 1433");
                }
                break;
            default:
                errStream.printf("ERROR: unsupported data store: '%s'%n", datastore);
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
        if (!Files.exists(persistDir)) {
            try {
                Files.createDirectory(persistDir.toAbsolutePath());
            } catch (IOException e) {
                errStream.println("ERROR: failed to create the persist directory. " + e.getMessage());
                return;
            }
        }

        boolean modelFile = Files.exists(Path.of(String.valueOf(persistDir), "model.bal"));
        if (modelFile) {
            String yellowColor = "\u001B[33m";
            String resetColor = "\u001B[0m";
            errStream.print(yellowColor + "WARNING A model.bal file already exists. " +
                    "Continuing would overwrite it. Do you wish to continue? (y/n) " + resetColor);
            String input = scanner.nextLine();
            if (!(input.toLowerCase(Locale.ENGLISH).equals("y") || input.toLowerCase(Locale.ENGLISH).equals("yes"))) {
                errStream.println("Introspection aborted.");
                return;
            }
            errStream.println("Continuing...");
        }

        PersistConfiguration persistConfigurations = new PersistConfiguration();
        persistConfigurations.setProvider(datastore);
        persistConfigurations.setSourcePath(this.sourcePath);
        
        try {
            persistConfigurations.setDbConfig(new DatabaseConfiguration(this.host, this.user, password, this.port,
                    this.database));
        } catch (BalException e) {
            errStream.println(e.getMessage());
            return;
        }

        // Handle table selection: --tables flag controls the behavior
        if (this.tables != null) {
            // --tables flag is present
            if (!this.tables.trim().isEmpty()) {
                // Tables specified via --tables=table1,table2
                persistConfigurations.setSelectedTables(this.tables);
            } else {
                // --tables with no value: trigger interactive mode
                try {
                    String[] availableTables = introspector.getAvailableTables(persistConfigurations);
                    if (availableTables.length == 0) {
                        errStream.println("ERROR: No tables found in the database.");
                        return;
                    }
                    
                    String selectedTablesInput = promptForTableSelection(scanner, availableTables);
                    if (selectedTablesInput == null) {
                        errStream.println("Introspection aborted.");
                        return;
                    }
                    
                    if (!selectedTablesInput.trim().isEmpty() && 
                        !selectedTablesInput.trim().equalsIgnoreCase("all")) {
                        persistConfigurations.setSelectedTables(selectedTablesInput);
                    }
                    // If "all" or empty, proceed with all tables (no filter set)
                } catch (BalException e) {
                    errStream.printf("ERROR: failed to fetch available tables: %s%n", e.getMessage());
                    return;
                }
            }
        }
        // If --tables is not provided, proceed with all tables (default behavior)

        Module entityModule = null;
        try {
            entityModule = introspector.introspectDatabase(persistConfigurations);
        } catch (BalException e) {
            errStream.printf("ERROR: failed to introspect database: %s%n", e.getMessage());
            return;
        }

        SourceGenerator sourceGenerator = new SourceGenerator(sourcePath, Paths.get(sourcePath, PERSIST_DIRECTORY),
                "Introspect.db", entityModule);

        try {
            sourceGenerator.createDbModel();
        } catch (BalException e) {
            errStream.printf(String.format("ERROR: failed to generate model for introspected database: %s%n",
                    e.getMessage()));
            return;
        }
        errStream.println("Introspection complete! The model.bal file created successfully.");
    }

    /**
     * Prompts the user to select tables from the available tables list.
     * Displays up to 50 tables and allows users to enter table names, indices, or "all".
     *
     * @param scanner the Scanner for reading user input
     * @param availableTables array of all available table names
     * @return comma-separated string of selected table names, "all", or null if aborted
     */
    private String promptForTableSelection(Scanner scanner, String[] availableTables) {
        String cyanColor = "\u001B[36m";
        String yellowColor = "\u001B[33m";
        String resetColor = "\u001B[0m";
        
        int totalTables = availableTables.length;
        int displayLimit = 50;
        boolean isLimited = totalTables > displayLimit;
        
        errStream.println(cyanColor + "\nAvailable tables in the database:" + resetColor);
        errStream.println("──────────────────────────────────");
        
        for (int i = 0; i < Math.min(totalTables, displayLimit); i++) {
            errStream.printf("  %d. %s%n", i + 1, availableTables[i]);
        }
        
        if (isLimited) {
            errStream.println(yellowColor + "  ... and " + (totalTables - displayLimit) + 
                            " more tables" + resetColor);
        }
        
        errStream.println("──────────────────────────────────");
        errStream.printf("Total: %d table%s%n%n", totalTables, totalTables == 1 ? "" : "s");
        
        errStream.println("Select tables to introspect:");
        errStream.println("  • Enter table names separated by commas (e.g., users,orders,products)");
        errStream.println("  • Enter table numbers separated by commas (e.g., 1,3,5)");
        errStream.println("  • Enter 'all' to introspect all tables");
        errStream.println("  • Press Enter without input to introspect all tables");
        errStream.println("  • Enter 'q' or 'quit' to abort");
        errStream.print("\nYour selection: ");
        
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
     * Parses comma-separated table indices and returns the corresponding table names.
     *
     * @param input comma-separated indices (e.g., "1,3,5")
     * @param availableTables array of available table names
     * @return comma-separated table names
     */
    private String parseTableIndices(String input, String[] availableTables) {
        String yellowColor = "\u001B[33m";
        String resetColor = "\u001B[0m";
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
