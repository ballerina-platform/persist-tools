/*
 *  Copyright (c) 2024, WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
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
import io.ballerina.persist.models.Module;
import io.ballerina.persist.nodegenerator.DriverResolver;
import io.ballerina.persist.nodegenerator.SourceGenerator;
import io.ballerina.persist.utils.DatabaseConnector;
import io.ballerina.persist.utils.JdbcDriverLoader;
import io.ballerina.projects.Project;
import picocli.CommandLine;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Scanner;

import static io.ballerina.persist.PersistToolsConstants.MYSQL_DRIVER_CLASS;
import static io.ballerina.persist.PersistToolsConstants.PERSIST_DIRECTORY;
import static io.ballerina.persist.nodegenerator.syntax.constants.BalSyntaxConstants.JDBC_URL_WITH_DATABASE_MYSQL;
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

    DatabaseConnector databaseConnector;

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
    private String port = "3306";

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
        try {
            validatePullCommandOptions(datastore, host, port, user, database);
        } catch (BalException e) {
            errStream.println("Invalid Option(s): " + System.lineSeparator() + e.getMessage());
            return;
        }

        String password = readDatabasePassword(scanner, errStream);

        try {
            validateBallerinaProject(Paths.get(this.sourcePath));
        } catch (BalException e) {
            errStream.println(e.getMessage());
            return;
        }

        if (this.datastore.equals(PersistToolsConstants.SupportedDataSources.MYSQL_DB)) {
            this.databaseConnector = new DatabaseConnector(JDBC_URL_WITH_DATABASE_MYSQL, MYSQL_DRIVER_CLASS);
        } else {
            errStream.printf("ERROR: unsupported data store: '%s'%n", datastore);
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
            errStream.print(yellowColor + "A model.bal file already exists. " +
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
        try {
            persistConfigurations.setDbConfig(new DatabaseConfiguration(this.host, this.user, password, this.port,
                    this.database));
        } catch (BalException e) {
            errStream.println(e.getMessage());
            return;
        }

        Module entityModule;
        DriverResolver driverResolver = new DriverResolver(this.sourcePath);
        Project driverProject;
        try {
            driverProject = driverResolver.resolveDriverDependencies();
        } catch (BalException e) {
            errStream.println(e.getMessage());
            deleteDriverFile(driverResolver);
            return;
        }

        try (JdbcDriverLoader driverLoader = databaseConnector.getJdbcDriverLoader(driverProject)) {
            Driver driver = databaseConnector.getJdbcDriver(driverLoader);

            try (Connection connection = databaseConnector.getConnection(driver, persistConfigurations, true)) {

                Introspector introspector = new MySqlIntrospector(connection,
                        persistConfigurations.getDbConfig().getDatabase());

                entityModule = introspector.introspectDatabase();

                if (entityModule == null) {
                    throw new BalException("ERROR: failed to generate entity module.");
                }

            } catch (SQLException e) {
                errStream.printf("ERROR: database failure. %s%n", e.getMessage());
                deleteDriverFile(driverResolver);
                return;
            }
        } catch (BalException e) {
            errStream.printf("ERROR: database introspection failed. %s%n", e.getMessage());
            deleteDriverFile(driverResolver);
            return;
        } catch (IOException e) {
            errStream.printf("ERROR: failed to load the database driver. %s%n", e.getMessage());
            deleteDriverFile(driverResolver);
            return;
        }

        SourceGenerator sourceGenerator = new SourceGenerator(sourcePath, Paths.get(sourcePath, PERSIST_DIRECTORY),
                "Introspect.db", entityModule);

        try {
            sourceGenerator.createDbModel();
        } catch (BalException e) {
            errStream.printf(String.format("ERROR: failed to generate model for introspected database: %s%n",
                    e.getMessage()));
            deleteDriverFile(driverResolver);
            return;
        }

        deleteDriverFile(driverResolver);
        errStream.println("Introspection complete! The model.bal file created successfully.");
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void printLongDesc(StringBuilder stringBuilder) {

    }

    @Override
    public void printUsage(StringBuilder stringBuilder) {

    }

    @Override
    public void setParentCmdParser(CommandLine commandLine) {

    }

    public void deleteDriverFile(DriverResolver driverResolver) {
        try {
            driverResolver.deleteDriverFile();
        } catch (BalException e) {
            errStream.println(e.getMessage());
        }
    }
}
