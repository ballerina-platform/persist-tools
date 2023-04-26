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
import io.ballerina.persist.configuration.PersistConfiguration;
import io.ballerina.persist.models.Module;
import io.ballerina.persist.nodegenerator.syntax.constants.BalSyntaxConstants;
import io.ballerina.persist.nodegenerator.syntax.utils.TomlSyntaxUtils;
import io.ballerina.persist.utils.BalProjectUtils;
import io.ballerina.persist.utils.JdbcDriverLoader;
import io.ballerina.persist.utils.ScriptRunner;
import io.ballerina.projects.DependencyGraph;
import io.ballerina.projects.Package;
import io.ballerina.projects.Project;
import io.ballerina.projects.ProjectException;
import io.ballerina.projects.ResolvedPackageDependency;
import io.ballerina.projects.directory.BuildProject;
import io.ballerina.projects.util.ProjectUtils;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.ballerina.persist.PersistToolsConstants.BALLERINA_MYSQL_DRIVER_NAME;
import static io.ballerina.persist.PersistToolsConstants.COMPONENT_IDENTIFIER;
import static io.ballerina.persist.PersistToolsConstants.MYSQL_CONNECTOR_NAME_PREFIX;
import static io.ballerina.persist.PersistToolsConstants.MYSQL_DRIVER_CLASS;
import static io.ballerina.persist.PersistToolsConstants.PASSWORD;
import static io.ballerina.persist.PersistToolsConstants.PERSIST_DIRECTORY;
import static io.ballerina.persist.PersistToolsConstants.PLATFORM;
import static io.ballerina.persist.PersistToolsConstants.PROPERTY_KEY_PATH;
import static io.ballerina.persist.PersistToolsConstants.SQL_SCHEMA_FILE;
import static io.ballerina.persist.PersistToolsConstants.USER;
import static io.ballerina.persist.nodegenerator.syntax.constants.BalSyntaxConstants.JDBC_URL_WITHOUT_DATABASE;
import static io.ballerina.persist.nodegenerator.syntax.constants.BalSyntaxConstants.JDBC_URL_WITH_DATABASE;
import static io.ballerina.persist.nodegenerator.syntax.utils.TomlSyntaxUtils.readBallerinaTomlConfig;
import static io.ballerina.persist.nodegenerator.syntax.utils.TomlSyntaxUtils.readPackageName;
import static io.ballerina.persist.utils.BalProjectUtils.validateBallerinaProject;
import static io.ballerina.projects.util.ProjectConstants.BALLERINA_TOML;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

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
    private static final String COMMAND_IDENTIFIER = "persist-push";
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
            validateBallerinaProject(Paths.get(this.sourcePath));
        } catch (BalException e) {
            errStream.println(e.getMessage());
            return;
        }

        Path persistDir = Paths.get(this.sourcePath, PERSIST_DIRECTORY);
        if (!Files.isDirectory(persistDir, NOFOLLOW_LINKS)) {
            errStream.println("ERROR: the persist directory inside the Ballerina project does not exist. " +
                    "run `bal persist init` to initiate the project before generation");
            return;
        }

        List<Path> schemaFilePaths;
        try (Stream<Path> stream = Files.list(persistDir)) {
            schemaFilePaths = stream.filter(file -> !Files.isDirectory(file))
                    .filter(file -> file.toString().toLowerCase(Locale.ENGLISH).endsWith(".bal"))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            errStream.printf("ERROR: failed to list the model definition files in the persist directory. %s%n",
                    e.getMessage());
            return;
        }

        if (schemaFilePaths.isEmpty()) {
            errStream.println("ERROR: the persist directory does not contain any model definition file. " +
                    "run `bal persist init` to initiate the project before generation.");
            return;
        }

        String packageName;
        try {
            packageName = readPackageName(this.sourcePath);
        } catch (BalException e) {
            errStream.println(e.getMessage());
            return;
        }
        // Load Ballerina project to get DB driver path.
        Project balProject;
        try {
            balProject = BuildProject.load(Paths.get(this.sourcePath).toAbsolutePath());
        } catch (ProjectException e) {
            errStream.println("ERROR: failed to load the Ballerina project. " + e.getMessage());
            return;
        }
        schemaFilePaths.forEach(file -> {
            String submodule = "";
            Module entityModule;
            Path generatedSourceDirPath;
            try {
                BalProjectUtils.validateSchemaFile(file);
                entityModule = BalProjectUtils.getEntities(file);
                generatedSourceDirPath = Paths.get(this.sourcePath, BalSyntaxConstants.GENERATED_SOURCE_DIRECTORY);
                HashMap<String, String> persistConfig = readBallerinaTomlConfig(
                        Paths.get(this.sourcePath, "Ballerina.toml"));
                if (!persistConfig.get("module").equals(packageName)) {
                    if (!persistConfig.get("module").startsWith(packageName + ".")) {
                        errStream.println("ERROR: invalid module name : '" + persistConfig.get("module") + "' :\n" +
                                "module name should follow the template <package_name>.<module_name>");
                        return;
                    }
                    submodule = persistConfig.get("module").split("\\.")[1];
                    if (!ProjectUtils.validateModuleName(submodule)) {
                        errStream.println("ERROR: invalid module name : '" + submodule + "' :\n" +
                                "module name can only contain alphanumerics, underscores and periods");
                        return;
                    } else if (!ProjectUtils.validateNameLength(submodule)) {
                        errStream.println("ERROR: invalid module name : '" + submodule + "' :\n" +
                                "maximum length of module name is 256 characters");
                        return;
                    }
                    generatedSourceDirPath = generatedSourceDirPath.resolve(submodule);
                }
                if (entityModule.getEntityMap().isEmpty()) {
                    errStream.printf("ERROR: the model definition file(%s) does not contain any valid entity.%n",
                            file.getFileName());
                    return;
                }
            } catch (BalException e) {
                errStream.printf("ERROR: failed to read entity definitions. %s%n", e.getMessage());
                return;
            }

            PersistConfiguration persistConfigurations;
            try {
                Path ballerinaTomlPath = Paths.get(this.sourcePath, BALLERINA_TOML);
                persistConfigurations = TomlSyntaxUtils.readDatabaseConfigurations(ballerinaTomlPath);
            } catch (BalException e) {
                errStream.printf("ERROR: failed to load db configurations for the data model(%s). %s%n ",
                        entityModule.getModuleName(), e.getMessage());
                return;
            }

            try (JdbcDriverLoader driverLoader = getJdbcDriverLoader(balProject)) {
                Driver driver = getJdbcDriver(driverLoader);
                String query = String.format(CREATE_DATABASE_SQL_FORMAT,
                        persistConfigurations.getDbConfig().getDatabase());
                try (Connection connection = getDBConnection(driver, persistConfigurations, false)) {
                    ScriptRunner sr = new ScriptRunner(connection);
                    sr.runQuery(query);
                } catch (SQLException e) {
                    errStream.printf("ERROR: failed to create the database(%s). %s%n",
                            persistConfigurations.getDbConfig().getDatabase(), e.getMessage());
                    return;
                }
                errStream.printf("Created database '%s'.%n", persistConfigurations.getDbConfig().getDatabase());

                String sqlFilePath = generatedSourceDirPath.resolve(SQL_SCHEMA_FILE).toAbsolutePath().toString();
                try (Connection connection = getDBConnection(driver, persistConfigurations, true);
                     Reader fileReader = new BufferedReader(new FileReader(sqlFilePath,
                             StandardCharsets.UTF_8))) {
                    ScriptRunner sr = new ScriptRunner(connection);
                    sr.runScript(fileReader);
                } catch (IOException e) {
                    errStream.printf("ERROR: failed to read SQL schema file(%s). %s%n ",
                            sqlFilePath, e.getMessage());
                    return;
                } catch (Exception e) {
                    errStream.printf("ERROR: failed to read the SQL schema file(%s). %s%n", sqlFilePath,
                            e.getMessage());
                    return;
                }
                errStream.printf("Created tables for definition in %s in the database '%s'. %n", file.getFileName(),
                        persistConfigurations.getDbConfig().getDatabase());
            } catch (BalException e) {
                errStream.printf("ERROR: failed to execute the SQL scripts for the definition file(%s). %s%n",
                        file.getFileName(), e.getMessage());
            } catch (IOException e) {
                errStream.printf("ERROR: failed to load the database driver. %s%n", e.getMessage());
            }
        });


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
        if (user != null) {
            props.put(USER, user);
        }
        if (password != null) {
            props.put(PASSWORD, password);
        }
        return driver.connect(url, props);
    }

    private JdbcDriverLoader getJdbcDriverLoader(Project balProject) throws BalException {
        JdbcDriverLoader driverLoader = null;
        Path driverDirectoryPath = getDriverPath(balProject).getParent();
        if (Objects.nonNull(driverDirectoryPath)) {
            Path driverPath = driverDirectoryPath.toAbsolutePath();
            URL[] urls = {};
            try {
                driverLoader = new JdbcDriverLoader(urls, driverPath);
            } catch (IOException e) {
                throw new BalException("could not load the driver from the driver path. " + e.getMessage());
            }
        }
        return driverLoader;
    }

    private Driver getJdbcDriver(JdbcDriverLoader driverLoader) throws BalException {
        Driver driver;
        try {
            Class<?> drvClass = driverLoader.loadClass(MYSQL_DRIVER_CLASS);
            driver = (Driver) drvClass.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            throw new BalException("required database driver class not found. " + e.getMessage());
        } catch (InstantiationException | InvocationTargetException e) {
            throw new BalException("the database driver instantiation is failed. " + e.getMessage());
        } catch (IllegalAccessException e) {
            throw new BalException("access denied while trying to instantiation the database driver. " +
                    e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new BalException("method not found while trying to instantiate jdbc driver. "
                    + e.getMessage());
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
        throw new BalException("failed to retrieve MySQL driver path in the local cache.");
    }
}
