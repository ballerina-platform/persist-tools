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
import io.ballerina.persist.models.Module;
import io.ballerina.persist.nodegenerator.DriverResolver;
import io.ballerina.persist.nodegenerator.SourceGenerator;
import io.ballerina.persist.nodegenerator.syntax.utils.TomlSyntaxUtils;
import io.ballerina.persist.utils.DatabaseConnector;
import io.ballerina.persist.utils.JdbcDriverLoader;
import io.ballerina.projects.Project;
import io.ballerina.projects.util.ProjectUtils;
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Scanner;

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
        String packageName;
        String moduleNameWithPackageName;
        String datastore;

        errStream.println("Introspecting database schema...");



        try {
            validateBallerinaProject(Paths.get(this.sourcePath));
        } catch (BalException e) {
            errStream.println(e.getMessage());
            return;
        }

        try {
            packageName = TomlSyntaxUtils.readPackageName(this.sourcePath);
        } catch (BalException e) {
            errStream.println(e.getMessage());
            return;
        }

        try {
            HashMap<String, String> ballerinaTomlConfig = TomlSyntaxUtils.readBallerinaTomlConfig(
                    Paths.get(this.sourcePath, "Ballerina.toml"));
            moduleNameWithPackageName = ballerinaTomlConfig.get("module").trim();
            if (!moduleNameWithPackageName.equals(packageName)) {
                if (!moduleNameWithPackageName.startsWith(packageName + ".")) {
                    errStream.println("ERROR: invalid module name : '" + ballerinaTomlConfig.get("module") + "' :\n" +
                            "module name should follow the template <package_name>.<module_name>");
                    return;
                }
                String moduleName = moduleNameWithPackageName.replace(packageName + ".", "");
                if (!ProjectUtils.validateModuleName(moduleName)) {
                    errStream.println("ERROR: invalid module name : '" + moduleName + "' :\n" +
                            "module name can only contain alphanumerics, underscores and periods");
                    return;
                } else if (!ProjectUtils.validateNameLength(moduleName)) {
                    errStream.println("ERROR: invalid module name : '" + moduleName + "' :\n" +
                            "maximum length of module name is 256 characters");
                    return;
                }
            }
            datastore = ballerinaTomlConfig.get("datastore").trim();
        } catch (BalException e) {
            errStream.printf("ERROR: failed to introspect database according to definition file (%s). %s%n",
                    "Ballerina.toml", e.getMessage());
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

        boolean modelFile = Files.exists(Path.of(String.valueOf(persistDir), "model.bal"));
        if (modelFile) {
            String yellowColor = "\u001B[33m";
            String resetColor = "\u001B[0m";
            errStream.print(yellowColor + "A model.bal file already exists. " +
                    "Continuing would overwrite it. Do you wish to continue? (y/n) " + resetColor);
            Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
            String input = scanner.nextLine();
            if (!input.toLowerCase(Locale.ENGLISH).equals("y")) {
                errStream.println("Introspection aborted.");
                return;
            }
            errStream.println("Continuing...");
        }

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

                Introspector introspector = new MySQLIntrospector(connection,
                        persistConfigurations.getDbConfig().getDatabase(), packageName);

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
            errStream.printf("ERROR: database introspection failed. %s%n",
                     e.getMessage());
            deleteDriverFile(driverResolver);
            return;
        } catch (IOException e) {
            errStream.printf("ERROR: failed to load the database driver. %s%n", e.getMessage());
            deleteDriverFile(driverResolver);
            return;
        }

        SourceGenerator sourceGenerator = new SourceGenerator(sourcePath,
                Paths.get(sourcePath, PERSIST_DIRECTORY),
                moduleNameWithPackageName, entityModule);

        try {
            sourceGenerator.createDbModel();
        } catch (BalException e) {
            errStream.printf(String.format("ERROR: failed to generate model for introspected database: %s%n",
                     e.getMessage()));
            deleteDriverFile(driverResolver);
            return;
        }

        deleteDriverFile(driverResolver);
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

    public void deleteDriverFile(DriverResolver driverResolver) {
        try {
            driverResolver.deleteDriverFile();
        } catch (BalException e) {
            errStream.println(e.getMessage());
        }
    }

}
