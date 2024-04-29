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
import io.ballerina.persist.introspect.MySqlIntrospector;
import io.ballerina.persist.introspect.PostgreSqlIntrospector;
import io.ballerina.persist.models.Module;
import io.ballerina.persist.nodegenerator.SourceGenerator;
import io.ballerina.persist.nodegenerator.syntax.constants.BalSyntaxConstants;
import picocli.CommandLine;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    @CommandLine.Option(names = { "-h", "--help" }, hidden = true)
    private boolean helpFlag;

    @Override
    public void execute() {
        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
        if (helpFlag) {
            String commandUsageInfo = BLauncherCmd.getCommandUsageInfo(COMMAND_IDENTIFIER);
            errStream.println(commandUsageInfo);
            return;
        }

        Introspector introspector;
        switch (this.datastore) {
            case PersistToolsConstants.SupportedDataSources.MYSQL_DB:
                introspector = new MySqlIntrospector();
                if (Objects.isNull(port)) {
                    port = "3306";
                    errStream.println("INFO default port 3306 is used for MySQL database");
                }
                break;
            case PersistToolsConstants.SupportedDataSources.POSTGRESQL_DB:
                introspector = new PostgreSqlIntrospector();
                if (Objects.isNull(port)) {
                    port = "5432";
                    errStream.println("INFO default port 5432 is used for PostgreSQL database");
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
        errStream.printf(BalSyntaxConstants.EXPERIMENTAL_NOTICE, "The support for database introspection is " +
                "currently an experimental feature, and its behavior may be subject to change in future releases.");

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
