/*
 *  Copyright (c) 2022, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
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
import io.ballerina.persist.introspect.MySQLIntrospector;
import io.ballerina.persist.models.Entity;
import io.ballerina.persist.models.Module;
import io.ballerina.persist.nodegenerator.DriverResolver;
import io.ballerina.persist.nodegenerator.SourceGenerator;
import io.ballerina.persist.nodegenerator.syntax.utils.TomlSyntaxUtils;
import io.ballerina.persist.utils.DatabaseConnector;
import io.ballerina.persist.utils.JdbcDriverLoader;
import io.ballerina.projects.Project;
import picocli.CommandLine;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static io.ballerina.persist.PersistToolsConstants.MYSQL_DRIVER_CLASS;
import static io.ballerina.persist.PersistToolsConstants.PERSIST_DIRECTORY;
import static io.ballerina.persist.nodegenerator.syntax.constants.BalSyntaxConstants.JDBC_URL_WITH_DATABASE_MYSQL;
import static io.ballerina.persist.nodegenerator.syntax.utils.TomlSyntaxUtils.readPackageName;
import static io.ballerina.persist.utils.BalProjectUtils.validateBallerinaProject;
import static io.ballerina.projects.util.ProjectConstants.BALLERINA_TOML;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

@CommandLine.Command(
        name = "pull",
        description = "Create model.bal file according to given database schema")
public class Pull implements BLauncherCmd {
    private final PrintStream errStream = System.err;

    private final String sourcePath;

    private static final String COMMAND_IDENTIFIER = "persist-pull";

    DatabaseConnector databaseConnector;

    public Pull() {
        this("");
    }

    public Pull(String sourcePath) {
        this.sourcePath = sourcePath;
    }


    @Override
    public void execute() {

        errStream.println("Introspecting database schema...");

        DriverResolver driverResolver = new DriverResolver(this.sourcePath);
        Project driverProject;
        try {
            driverProject = driverResolver.resolveDriverDependencies();
        } catch (BalException e) {
            errStream.println(e.getMessage());
            return;
        }

        try {
            validateBallerinaProject(Paths.get(this.sourcePath));
        } catch (BalException e) {
            errStream.println(e.getMessage());
            return;
        }

        String datastore;
        try {
            HashMap<String, String> ballerinaTomlConfig = TomlSyntaxUtils.readBallerinaTomlConfig(
                    Paths.get(this.sourcePath, "Ballerina.toml"));
            datastore = ballerinaTomlConfig.get("datastore").trim();
        } catch (BalException e) {
            errStream.printf("ERROR: failed to locate Ballerina.toml: %s%n",
                    e.getMessage());
            return;
        }

        if (datastore.equals(PersistToolsConstants.SupportedDataSources.MYSQL_DB)) {
            this.databaseConnector = new DatabaseConnector(JDBC_URL_WITH_DATABASE_MYSQL, MYSQL_DRIVER_CLASS,
                    this.sourcePath, datastore);
        } else {
            errStream.printf("ERROR: unsupported data store: '%s'%n", datastore);
            return;
        }

        Path persistDir = Paths.get(this.sourcePath, PERSIST_DIRECTORY);
        if (!Files.isDirectory(persistDir, NOFOLLOW_LINKS)) {
            errStream.println("ERROR: the persist directory inside the Ballerina project does not exist. " +
                    "run `bal persist init` to initiate the project before generation");
            return;
        }

        //check if model.bal file exists, if exists throw warning
//        List<Path> schemaFilePaths;
//        try (Stream<Path> stream = Files.list(persistDir)) {
//            schemaFilePaths = stream.filter(file -> !Files.isDirectory(file))
//                    .filter(file -> file.toString().toLowerCase(Locale.ENGLISH).endsWith(".bal"))
//                    .collect(Collectors.toList());
//        } catch (IOException e) {
//            errStream.printf("ERROR: failed to list the model definition files in the persist directory. %s%n",
//                    e.getMessage());
//            return;
//        }

        String packageName;
        try {
            packageName = readPackageName(this.sourcePath);
        } catch (BalException e) {
            errStream.println(e.getMessage());
            return;
        }

        PersistConfiguration persistConfigurations;
        try {
            Path ballerinaTomlPath = Paths.get(this.sourcePath, BALLERINA_TOML);
            persistConfigurations = TomlSyntaxUtils.readDatabaseConfigurations(ballerinaTomlPath);
        } catch (BalException e) {
            errStream.printf("ERROR: failed to load db configurations. %s ", e.getMessage());
            return;
        }

        Module entityModule;

        try (JdbcDriverLoader driverLoader = databaseConnector.getJdbcDriverLoader(driverProject)) {
            Driver driver = databaseConnector.getJdbcDriver(driverLoader);

            try (Connection connection = databaseConnector.getConnection(driver, persistConfigurations, true)) {

                Introspector introspector = new MySQLIntrospector(connection,
                        persistConfigurations.getDbConfig().getDatabase());

                Map<String, Entity> entityMap = introspector.introspectDatabase();

                Module.Builder moduleBuilder = Module.newBuilder(packageName);

                entityMap.forEach(moduleBuilder::addEntity);
                // add enums and imports
                entityModule = moduleBuilder.build();

                if (entityModule == null) {
                    throw new BalException("ERROR: failed to generate entity module.");
                }

            } catch (SQLException e) {
                errStream.printf("ERROR: failed to connect to the database. %s%n", e.getMessage());
                return;
            }

        } catch (BalException e) {
            errStream.printf("ERROR: database introspection failed. %s%n",
                     e.getMessage());
            return;
        } catch (IOException e) {
            errStream.printf("ERROR: failed to load the database driver. %s%n", e.getMessage());
            return;
        }

        SourceGenerator sourceGenerator = new SourceGenerator(sourcePath,
                Paths.get(sourcePath, PERSIST_DIRECTORY),
                "prismaTest", entityModule);

        try {
            sourceGenerator.createDbModel();
        } catch (BalException e) {
            errStream.printf(String.format("ERROR: failed to generate model for introspected database: %s%n",
                     e.getMessage()));
            return;
        }

        try {
            driverResolver.deleteDriverFile();
        } catch (BalException e) {
            errStream.println(e.getMessage());
            return;
        }
        errStream.println("Introspection complete! model.bal file created successfully.");
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



}
