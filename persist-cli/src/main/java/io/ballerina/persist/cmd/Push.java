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
import io.ballerina.persist.configuration.PersistConfiguration;
import io.ballerina.persist.nodegenerator.SyntaxTreeGenerator;
import io.ballerina.persist.objects.BalException;
import io.ballerina.persist.objects.Entity;
import io.ballerina.persist.objects.EntityMetaData;
import io.ballerina.persist.objects.PersistToolsConstants;
import io.ballerina.persist.utils.BalProjectUtils;
import io.ballerina.persist.utils.JdbcDriverLoader;
import io.ballerina.persist.utils.ScriptRunner;
import io.ballerina.persist.utils.SqlScriptGenerationUtils;
import io.ballerina.projects.DependencyGraph;
import io.ballerina.projects.Module;
import io.ballerina.projects.Package;
import io.ballerina.projects.Project;
import io.ballerina.projects.ProjectException;
import io.ballerina.projects.ResolvedPackageDependency;
import io.ballerina.projects.directory.BuildProject;
import io.ballerina.projects.directory.ProjectLoader;
import picocli.CommandLine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

import static io.ballerina.persist.nodegenerator.BalFileConstants.JDBC_URL_WITHOUT_DATABASE;
import static io.ballerina.persist.nodegenerator.BalFileConstants.JDBC_URL_WITH_DATABASE;
import static io.ballerina.persist.objects.PersistToolsConstants.BALLERINA_MYSQL_DRIVER_NAME;
import static io.ballerina.persist.objects.PersistToolsConstants.COMPONENT_IDENTIFIER;
import static io.ballerina.persist.objects.PersistToolsConstants.MYSQL_CONNECTOR_NAME_PREFIX;
import static io.ballerina.persist.objects.PersistToolsConstants.MYSQL_DRIVER_CLASS;
import static io.ballerina.persist.objects.PersistToolsConstants.PASSWORD;
import static io.ballerina.persist.objects.PersistToolsConstants.PERSIST_DIRECTORY;
import static io.ballerina.persist.objects.PersistToolsConstants.PERSIST_TOML_FILE;
import static io.ballerina.persist.objects.PersistToolsConstants.PLATFORM;
import static io.ballerina.persist.objects.PersistToolsConstants.PROPERTY_KEY_PATH;
import static io.ballerina.persist.objects.PersistToolsConstants.USER;
import static io.ballerina.persist.utils.BalProjectUtils.getBuildProject;
import static io.ballerina.persist.utils.BalProjectUtils.getEntityModule;

/**
 * Class to implement "persist push" command for ballerina.
 *
 * @since 0.1.0
 */

@CommandLine.Command(
        name = "push",
        description = "Create database tables corresponding to user-defined entities")
public class Push implements BLauncherCmd {

    private static final String CREATE_DATABASE_SQL_FORMAT = "CREATE DATABASE IF NOT EXISTS %s";
    private final PrintStream errStream = System.err;
    private static final String COMMAND_IDENTIFIER = "persist-db-push";
    private final String sourcePath;
    @CommandLine.Option(names = {"-h", "--help"}, hidden = true)
    private boolean helpFlag;

    public Push() {
        this("");
    }

    public Push(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    @Override
    public void execute() {
        if (helpFlag) {
            String commandUsageInfo = BLauncherCmd.getCommandUsageInfo(COMMAND_IDENTIFIER);
            errStream.println(commandUsageInfo);
            return;
        }

        try {
            ProjectLoader.loadProject(Paths.get(this.sourcePath));
        } catch (ProjectException e) {
            errStream.println("Not a Ballerina project (or any parent up to mount point)\n" +
                    "You should run this command inside a Ballerina project.");
            return;
        }

        Path projectPath = Paths.get(this.sourcePath).toAbsolutePath();
        Path persistTomlPath = Paths.get(this.sourcePath, PERSIST_DIRECTORY, PERSIST_TOML_FILE);

        if (!Files.exists(persistTomlPath)) {
            errStream.println("Persist project is not initiated. Please run `bal persist init` " +
                    "to initiate the project before the database schema generation. ");
            return;
        }

        try {
            BuildProject buildProject = getBuildProject(projectPath);
            Module module = getEntityModule(buildProject);
            EntityMetaData retEntityMetaData = BalProjectUtils.getEntities(module);
            ArrayList<Entity> entityArray = retEntityMetaData.entityArray;
            String[] sqlScripts = SqlScriptGenerationUtils.generateSqlScript(entityArray);
            SqlScriptGenerationUtils.writeScriptFile(sqlScripts,
                    Paths.get(projectPath.toString(), PERSIST_DIRECTORY));
        } catch (ProjectException | BalException e) {
            errStream.println("Error occurred while generating SQL schema. " + e.getMessage());
            return;
        }

        Driver driver;
        PersistConfiguration persistConfigurations;
        try {
            Project balProject = BuildProject.load(projectPath); // refer the value from the project path
            balProject.currentPackage().getCompilation();
            driver = getJdbcDriver(balProject);
            persistConfigurations = SyntaxTreeGenerator.readPersistToml(persistTomlPath);
        } catch (BalException e) {
            errStream.println("Error occurred while loading db configurations and driver. " + e.getMessage());
            return;
        }

        String query = String.format(CREATE_DATABASE_SQL_FORMAT,
                persistConfigurations.getDbConfig().getDatabase());
        try (Connection connection = getDBConnection(driver, persistConfigurations, false)) {
            ScriptRunner sr = new ScriptRunner(connection);
            sr.runQuery(query);
        } catch (SQLException e) {
            errStream.println("Error occurred while creating the database, " +
                    persistConfigurations.getDbConfig().getDatabase() + "." + e.getMessage());
            return;
        }

        String sqlFilePath = Paths.get(this.sourcePath,
                PERSIST_DIRECTORY, PersistToolsConstants.SQL_SCHEMA_FILE).toAbsolutePath().toString();
        try (Connection connection = getDBConnection(driver, persistConfigurations, true);
             Reader fileReader = new BufferedReader(new FileReader(sqlFilePath,
                     StandardCharsets.UTF_8))) {
            ScriptRunner sr = new ScriptRunner(connection);
            sr.runScript(fileReader);
        } catch (IOException e) {
            errStream.println("Error occurred while reading SQL schema file, " + sqlFilePath + "." + e.getMessage());
            return;
        } catch (Exception e) {
            errStream.println("Error occurred while executing SQL schema file, " + sqlFilePath + "." + e.getMessage());
            return;
        }
        errStream.println("Created tables for entities in the database " +
                persistConfigurations.getDbConfig().getDatabase() + ".");
    }

    private Connection getDBConnection(Driver driver, PersistConfiguration persistConfigurations, boolean withDB)
            throws SQLException {
        String host = persistConfigurations.getDbConfig().getHost();
        int port = persistConfigurations.getDbConfig().getPort();
        String user = persistConfigurations.getDbConfig().getUsername();
        String password = persistConfigurations.getDbConfig().getPassword();
        String database = persistConfigurations.getDbConfig().getDatabase();
        String provider = persistConfigurations.getProvider();
        String url;
        if (withDB) {
            url = String.format(JDBC_URL_WITH_DATABASE, provider,
                    host, port,
                    database);
        } else {
            url = String.format(JDBC_URL_WITHOUT_DATABASE, provider, host, port);
        }
        Properties props = new Properties();
        props.put(USER, user);
        props.put(PASSWORD, password);
        return driver.connect(url, props);
    }

    private Driver getJdbcDriver(Project balProject) throws BalException {
        Driver driver = null;
        Path driverDirectoryPath = getDriverPath(balProject).getParent();
        if (Objects.nonNull(driverDirectoryPath)) {
            Path driverPath = driverDirectoryPath.toAbsolutePath();
            URL[] urls = {};
            try (JdbcDriverLoader driverLoader = new JdbcDriverLoader(urls, driverPath)) {
                Class<?> drvClass = driverLoader.loadClass(MYSQL_DRIVER_CLASS);
                driver = (Driver) drvClass.getDeclaredConstructor().newInstance();
            } catch (ProjectException e) {
                throw new BalException("Not a Ballerina project (or any parent up to mount point)\n" +
                        "You should run this command inside a Ballerina project.");
            } catch (ClassNotFoundException e) {
                throw new BalException("Required database driver class not found. " + e.getMessage());
            } catch (InstantiationException | InvocationTargetException e) {
                throw new BalException("Error instantiation the jdbc driver. " + e.getMessage());
            } catch (IllegalAccessException e) {
                throw new BalException("Access denied while trying to instantiation the database driver. " +
                        e.getMessage());
            } catch (NoSuchMethodException e) {
                throw new BalException("Method not found while trying to instantiate jdbc driver. "
                        + e.getMessage());
            } catch (IOException e) {
                throw new BalException("Error in jdbc driver path : " + e.getMessage());
            }
        }
        return driver;
    }

    @Override
    public void setParentCmdParser(CommandLine parentCmdParser) {
    }

    @Override
    public String getName() {
        return COMPONENT_IDENTIFIER;
    }

    @Override
    public void printLongDesc(StringBuilder out) {
        out.append("Create databases and tables for the entity records defined in the Ballerina project")
                .append(System.lineSeparator());
        out.append(System.lineSeparator());
    }

    @Override
    public void printUsage(StringBuilder stringBuilder) {
        stringBuilder.append("  ballerina " + COMPONENT_IDENTIFIER + " db push").append(System.lineSeparator());
    }

    private Path getDriverPath(Project balProject) throws BalException {
        String relativeLibPath;

        DependencyGraph<ResolvedPackageDependency> resolvedPackageDependencyDependencyGraph =
                balProject.currentPackage().getResolution().dependencyGraph();

        ResolvedPackageDependency root = resolvedPackageDependencyDependencyGraph.getRoot();

        Optional<ResolvedPackageDependency> mysqlDriverDependency = resolvedPackageDependencyDependencyGraph
                .getDirectDependencies(root).stream().
                filter(resolvedPackageDependency -> resolvedPackageDependency.packageInstance().
                        descriptor().toString().contains(BALLERINA_MYSQL_DRIVER_NAME)).findFirst();

        if (mysqlDriverDependency.isPresent()) {
            Package mysqlDriverPackage = mysqlDriverDependency.get().packageInstance();
            List<Map<String, Object>> dependencies = mysqlDriverPackage.manifest().platform(PLATFORM).dependencies();

            for (Map<String, Object> dependency : dependencies) {
                if (dependency.get(PROPERTY_KEY_PATH).toString().contains(MYSQL_CONNECTOR_NAME_PREFIX)) {
                    relativeLibPath = dependency.get(PROPERTY_KEY_PATH).toString();
                    return mysqlDriverPackage.project().sourceRoot().resolve(relativeLibPath);
                }
            }
        }
        // Unreachable code since the driver jar is pulled from the central and stored in the local cache
        // when the project is being built prior to this function.
        throw new BalException("Failed to retrieve MySQL driver path in the local cache.");
    }
}
