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
import io.ballerina.persist.PersistToolsConstants;
import io.ballerina.persist.nodegenerator.SyntaxTreeGenerator;
import io.ballerina.persist.objects.BalException;
import io.ballerina.persist.objects.Entity;
import io.ballerina.persist.objects.EntityMetaData;
import io.ballerina.persist.utils.BalProjectUtils;
import io.ballerina.persist.utils.DataBaseValidationUtils;
import io.ballerina.persist.utils.JdbcDriverLoader;
import io.ballerina.persist.utils.SqlScriptGenerationUtils;
import io.ballerina.projects.DependencyGraph;
import io.ballerina.projects.Package;
import io.ballerina.projects.Project;
import io.ballerina.projects.ProjectException;
import io.ballerina.projects.ResolvedPackageDependency;
import io.ballerina.projects.directory.BuildProject;
import io.ballerina.projects.directory.ProjectLoader;
import io.ballerina.toml.syntax.tree.TableNode;
import org.apache.ibatis.jdbc.ScriptRunner;
import picocli.CommandLine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Pattern;

import static io.ballerina.persist.PersistToolsConstants.BALLERINA_MYSQL_DRIVER_NAME;
import static io.ballerina.persist.PersistToolsConstants.COMPONENT_IDENTIFIER;
import static io.ballerina.persist.PersistToolsConstants.CONFIG_SCRIPT_FILE;
import static io.ballerina.persist.PersistToolsConstants.DATABASE;
import static io.ballerina.persist.PersistToolsConstants.DATABASE_CONFIGURATION_BAL;
import static io.ballerina.persist.PersistToolsConstants.HOST;
import static io.ballerina.persist.PersistToolsConstants.KEYWORD_CLIENTS;
import static io.ballerina.persist.PersistToolsConstants.MYSQL;
import static io.ballerina.persist.PersistToolsConstants.MYSQL_CONNECTOR_NAME_PREFIX;
import static io.ballerina.persist.PersistToolsConstants.MYSQL_DRIVER_CLASS;
import static io.ballerina.persist.PersistToolsConstants.PASSWORD;
import static io.ballerina.persist.PersistToolsConstants.PERSIST_TOML_FILE;
import static io.ballerina.persist.PersistToolsConstants.PLATFORM;
import static io.ballerina.persist.PersistToolsConstants.PORT;
import static io.ballerina.persist.PersistToolsConstants.PROPERTY_KEY_PATH;
import static io.ballerina.persist.PersistToolsConstants.SUBMODULE_FOLDER;
import static io.ballerina.persist.PersistToolsConstants.SUBMODULE_PERSIST;
import static io.ballerina.persist.PersistToolsConstants.USER;
import static io.ballerina.persist.nodegenerator.BalFileConstants.JDBC_URL_WITHOUT_DATABASE;
import static io.ballerina.persist.nodegenerator.BalFileConstants.JDBC_URL_WITH_DATABASE;
import static io.ballerina.persist.nodegenerator.BalFileConstants.PERSIST;
import static io.ballerina.persist.nodegenerator.BalFileConstants.PLACEHOLDER_PATTERN;
import static io.ballerina.persist.nodegenerator.SyntaxTreeGenerator.populateConfigurations;

/**
 * Class to implement "persist push" command for ballerina.
 *
 * @since 0.1.0
 */

@CommandLine.Command(
        name = "push",
        description = "Create database tables corresponding to user-defined entities")
public class Push implements BLauncherCmd {

    private final PrintStream errStream = System.err;
    private final PrintStream stdStream = System.out;
    private static final String COMMAND_IDENTIFIER = "persist-db-push";
    Project balProject;
    public String sourcePath = "";
    Driver driver;
    HashMap<String, String> persistConfigurations;
    @CommandLine.Option(names = {"-h", "--help"}, hidden = true)
    private boolean helpFlag;

    public Push() {}

    @Override
    public void execute() {
        if (helpFlag) {
            String commandUsageInfo = BLauncherCmd.getCommandUsageInfo(COMMAND_IDENTIFIER);
            errStream.println(commandUsageInfo);
            return;
        }

        String[] sqlScripts;
        Path absoluteSourcePath = Paths.get(this.sourcePath).toAbsolutePath();
        Path persistTomlPath = Paths.get(this.sourcePath, SUBMODULE_PERSIST, PERSIST_TOML_FILE);
        Path databaseConfigurationBalPath = Paths.get(this.sourcePath, SUBMODULE_FOLDER, KEYWORD_CLIENTS,
                DATABASE_CONFIGURATION_BAL);
        File persistToml = new File(persistTomlPath.toString());
        File databaseConfigurationsBal = new File(databaseConfigurationBalPath.toString());
        if (!persistToml.exists() || !databaseConfigurationsBal.exists()) {
            errStream.println("Persist project is not initiated. Please run `bal persist init` " +
                    "to initiate the project before the database schema generation. ");
            return;
        }

        try  {
            balProject = ProjectLoader.loadProject(Paths.get(sourcePath));
            balProject = BuildProject.load(Paths.get(sourcePath).toAbsolutePath());
            balProject.currentPackage().getCompilation();
            persistConfigurations = SyntaxTreeGenerator.readPersistToml(Paths.get(this.sourcePath, PERSIST,
                    PERSIST_TOML_FILE));
            HashMap<String, String> templatedEntry = new HashMap<>();
            for (Map.Entry<String, String> entry : persistConfigurations.entrySet()) {
                if (Pattern.matches(PLACEHOLDER_PATTERN, persistConfigurations.get(entry.getKey()))) {
                    templatedEntry.put(entry.getKey(), entry.getValue());
                }
            }
            if (!templatedEntry.isEmpty()) {
                HashMap<String, String> resolvedEntries = populatePlaceHolder(templatedEntry);
                persistConfigurations.putAll(resolvedEntries);
            }
            EntityMetaData retEntityMetaData = BalProjectUtils.getEntitiesInBalFiles(this.sourcePath);
            ArrayList<Entity> entityArray = retEntityMetaData.entityArray;
            sqlScripts = SqlScriptGenerationUtils.generateSqlScript(entityArray);
            SqlScriptGenerationUtils.writeScriptFile(sqlScripts,
                    Paths.get(absoluteSourcePath.toString(), PERSIST));
            loadJdbcDriver(balProject);
        } catch (ProjectException | BalException  e) {
            errStream.println(e.getMessage());
            return;
        }
        String url = String.format(JDBC_URL_WITHOUT_DATABASE, MYSQL,
                persistConfigurations.get(HOST).replaceAll("\"", ""), persistConfigurations.get(PORT));
        String user = persistConfigurations.get(USER).replaceAll("\"", "");
        String password = persistConfigurations.get(PASSWORD).replaceAll("\"", "");
        String database = persistConfigurations.get(DATABASE).replaceAll("\"", "");

        Properties props = new Properties();
        props.put(USER, user);
        props.put(PASSWORD, password);
        try {
            Connection connection = driver.connect(url, props);
            ResultSet resultSet = connection.getMetaData().getCatalogs(); // have to remove
            boolean databaseExists = false;
            while (resultSet.next()) {

                if (resultSet.getString(1).trim().equals(database)) {
                    databaseExists = true;
                    break;
                }
            }
            if (!databaseExists) {
                String validatedDatabase = DataBaseValidationUtils.validateDatabaseInput(database);
                String query = String.format("CREATE DATABASE %s", validatedDatabase);
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                try {
                    preparedStatement.executeUpdate();
                } catch (Exception e) {
                    throw new BalException(e.getMessage());
                } finally {
                    preparedStatement.close();
                    connection.close();
                }
                stdStream.println("Created Database : " + database + ".");
            }
        } catch (SQLException | BalException e) {
            errStream.println("Error occurred while creating the database. " + e.getMessage());
            return;
        }

        String databaseUrl = String.format(JDBC_URL_WITH_DATABASE, MYSQL,
                    persistConfigurations.get(HOST).replaceAll("\"", ""), persistConfigurations.get(PORT),
                persistConfigurations.get(DATABASE).replaceAll("\"", ""));

        try {
            Connection connection = driver.connect(databaseUrl, props);
            try {
                ScriptRunner sr = new ScriptRunner(connection);
                try (Reader fileReader = new BufferedReader(new FileReader(Paths.get(this.sourcePath,
                        PERSIST, PersistToolsConstants.FILE_NAME).toAbsolutePath().toString(),
                        StandardCharsets.UTF_8))) {
                    sr.runScript(fileReader);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            errStream.println("Error while creating the tables in the database " + database + ". " +  e.getMessage());
            return;
        }
        stdStream.println("Created tables for entities in the database " + database + ".");
    }
    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public HashMap<String, String> getConfigurations() {
        return this.persistConfigurations;
    }



    private void loadJdbcDriver(Project balProject) throws BalException {
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
    }
    
    private HashMap<String, String> populatePlaceHolder(HashMap<String, String> templatedEntry)
            throws BalException {
        HashMap<String, TableNode> configs = SyntaxTreeGenerator.getConfigs(Paths.get(
                this.sourcePath, CONFIG_SCRIPT_FILE).toAbsolutePath());

        return populateConfigurations(templatedEntry, configs);
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

        Package mysql = resolvedPackageDependencyDependencyGraph.getDirectDependencies(root).stream().
                filter(resolvedPackageDependency -> resolvedPackageDependency.packageInstance().
                descriptor().toString().contains(BALLERINA_MYSQL_DRIVER_NAME)).findFirst().get().packageInstance();

        List<Map<String, Object>> dependencies = mysql.manifest().platform(PLATFORM).dependencies();

        for (Map<String, Object> dependency : dependencies) {
            if (dependency.get(PROPERTY_KEY_PATH).toString().contains(MYSQL_CONNECTOR_NAME_PREFIX)) {
                relativeLibPath = dependency.get(PROPERTY_KEY_PATH).toString();
                return mysql.project().sourceRoot().resolve(relativeLibPath);
            }
        }
        // Unreachable code since the driver jar is pulled from the central and stored in the local cache
        // when the project is being built prior to this function.
        throw new BalException("Failed to retrieve MySQL driver path in the local cache. ");
    }
}
