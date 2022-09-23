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
import io.ballerina.persist.nodegenerator.SyntaxTreeGenerator;
import io.ballerina.persist.objects.BalException;
import io.ballerina.persist.utils.JdbcDriverLoader;
import io.ballerina.projects.Project;
import io.ballerina.projects.ProjectEnvironmentBuilder;
import io.ballerina.projects.ProjectException;
import io.ballerina.projects.directory.BuildProject;
import io.ballerina.projects.directory.ProjectLoader;
import picocli.CommandLine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Properties;

import static io.ballerina.persist.PersistToolsConstants.COMPONENT_IDENTIFIER;
import static io.ballerina.persist.PersistToolsConstants.DATABASE;
import static io.ballerina.persist.PersistToolsConstants.HOST;
import static io.ballerina.persist.PersistToolsConstants.MYSQL;
import static io.ballerina.persist.PersistToolsConstants.MYSQL_CLASS;
import static io.ballerina.persist.PersistToolsConstants.PASSWORD;
import static io.ballerina.persist.PersistToolsConstants.PORT;
import static io.ballerina.persist.PersistToolsConstants.SQL_PATH;
import static io.ballerina.persist.PersistToolsConstants.TARGET;
import static io.ballerina.persist.PersistToolsConstants.USER;
import static io.ballerina.persist.nodegenerator.BalFileConstants.JDBC_URL_WITHOUT_DATABASE;
import static io.ballerina.persist.nodegenerator.BalFileConstants.JDBC_URL_WITH_DATABASE;

/**
 * Class to implement "persist push" command for ballerina.
 *
 * @since 0.1.0
 */

@CommandLine.Command(
        name = "push",
        description = "Generate and run SQL scripts.")

public class Push implements BLauncherCmd {

    private final PrintStream errStream = System.err;
    private static final String COMMAND_IDENTIFIER = "persist-push";
    public ProjectEnvironmentBuilder projectEnvironmentBuilder;
    Project balProject;
    public String sourcePath = "";
    public String configPath = "Config.toml";
    public Path driverPath = Paths.get("target", "platform-libs");
    Driver driver;
    HashMap<String, String> configurations;
    @CommandLine.Option(names = {"-h", "--help"}, hidden = true)
    private boolean helpFlag;

    @Override
    public void execute() {
        String name;
        if (helpFlag) {
            String commandUsageInfo = BLauncherCmd.getCommandUsageInfo(COMMAND_IDENTIFIER);
            errStream.println(commandUsageInfo);
            return;
        }
        boolean isTest = (projectEnvironmentBuilder != null);
        try  {
            URL[] urls = {};
            JdbcDriverLoader driverLoader;
            if (!isTest) {
                balProject = ProjectLoader.loadProject(Paths.get(""));
            } else {
                balProject = ProjectLoader.loadProject(Paths.get(sourcePath), projectEnvironmentBuilder);
            }
            driverLoader = new JdbcDriverLoader(urls, driverPath.toAbsolutePath());
            Class drvClass = driverLoader.loadClass(MYSQL_CLASS);
            driver = (Driver) drvClass.getDeclaredConstructor().newInstance();
            name = balProject.currentPackage().descriptor().org().value() + "." + balProject.currentPackage()
                    .descriptor().name().value();
        } catch (ProjectException e) {
            errStream.println("The current directory is not a Ballerina project!");
            return;
        } catch (ClassNotFoundException e) {
            errStream.println("Driver Not Found");
            return;
        } catch (InstantiationException | InvocationTargetException e) {
            errStream.println("Error instantiation the jdbc driver");
            return;
        } catch (IllegalAccessException e) {
            errStream.println("Access denied trying to instantiation the jdbc driver");
            return;
        } catch (NoSuchMethodException e) {
            errStream.println("Method not fount error while trying to instantiate jdbc driver : " + e.getMessage());
            return;
        } catch (MalformedURLException e) {
            errStream.println("Error in jdbc driver path : " + e.getMessage());
            return;
        }
        try {
            if (projectEnvironmentBuilder == null) {
                balProject = BuildProject.load(Paths.get(sourcePath).toAbsolutePath());
            } else {
                balProject = BuildProject.load(projectEnvironmentBuilder, Paths.get(sourcePath).toAbsolutePath());
            }
        } catch (ProjectException e) {
            errStream.println(e.getMessage());
            return;
        }
        try {
            balProject.currentPackage().getCompilation();
        } catch (ProjectException e) {
            errStream.println(e.getMessage());
            return;
        }
        String sValue;
        StringBuffer stringBuffer = new StringBuffer();
        configurations = new HashMap<>();
        String[] sqlLines;
        Connection connection;
        Statement statement;
        try {
            configurations = SyntaxTreeGenerator.readToml(
                    Paths.get(this.sourcePath, this.configPath), name);

            Path path = Paths.get(this.sourcePath, TARGET, SQL_PATH);

            FileReader fileReader = new FileReader(path.toAbsolutePath().toString());
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while ((sValue = bufferedReader.readLine()) != null) {
                stringBuffer.append(sValue);
            }
            bufferedReader.close();
            sqlLines = stringBuffer.toString().split(";");

        } catch (BalException e) {
            errStream.println(e.getMessage());
            return;
        } catch (IOException e) {
            errStream.println("Error occurred while reading generated SQL scripts!");
            return;
        }
        String url = String.format(JDBC_URL_WITHOUT_DATABASE, "mysql",
                configurations.get(HOST).replaceAll("\"", ""), configurations.get(PORT));
        String user = configurations.get(USER).replaceAll("\"", "");
        String password = configurations.get(PASSWORD).replaceAll("\"", "");
        String database = configurations.get(DATABASE).replaceAll("\"", "");
        Properties props = new Properties();
        props.put("user", user);
        props.put("password", password);
        try {
            connection = driver.connect(url, props);
            ResultSet resultSet = connection.getMetaData().getCatalogs();
            boolean databaseExists = false;
            while (resultSet.next()) {

                if (resultSet.getString(1).trim().equals(database)) {
                    databaseExists = true;
                    break;
                }
            }
            if (!databaseExists) {
                statement = connection.createStatement();
                String query = String.format("CREATE DATABASE %s", database);
                statement.executeUpdate(query);
                errStream.println("Creating Database : " + database);
            }
            resultSet.close();
            connection.close();
            String databaseUrl = String.format(JDBC_URL_WITH_DATABASE, MYSQL,
                    configurations.get(HOST).replaceAll("\"", ""), configurations.get(PORT),
                    configurations.get(DATABASE).replaceAll("\"", ""));
            if (!isTest) {
                connection = driver.connect(databaseUrl, props);
            } else {
                connection = DriverManager.getConnection(databaseUrl, user, password);
            }
            statement = connection.createStatement();

            for (int line = 0; line < sqlLines.length; line++) {
                if (!sqlLines[line].trim().equals("")) {
                    statement.executeUpdate(sqlLines[line]);
                }
            }
            statement.close();
            connection.close();
        } catch (SQLException e) {
            errStream.println("*** Error : " + e.getMessage());
            errStream.println("*** ");
        }
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }
    public void setEnvironmentBuilder(ProjectEnvironmentBuilder projectEnvironmentBuilder) {
        this.projectEnvironmentBuilder = projectEnvironmentBuilder;
    }

    public void setDriverPath(Path path) {
        this.driverPath = path;
    }

    public HashMap<String, String> getConfigurations() {
        return this.configurations;
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
        out.append("Generate database configurations file inside the Ballerina project").append(System.lineSeparator());
        out.append(System.lineSeparator());
    }
    @Override
    public void printUsage(StringBuilder stringBuilder) {
        stringBuilder.append("  ballerina " + COMPONENT_IDENTIFIER +
                " init").append(System.lineSeparator());
    }
}
