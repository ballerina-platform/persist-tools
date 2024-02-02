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
import io.ballerina.persist.introspect.Introspector;
import io.ballerina.persist.introspect.MySQLIntrospector;
import io.ballerina.persist.models.Entity;
import io.ballerina.persist.models.Module;
import io.ballerina.persist.nodegenerator.SourceGenerator;
import io.ballerina.persist.utils.DatabaseConnector;
import io.ballerina.persist.utils.JdbcDriverLoader;
import io.ballerina.projects.Project;
import io.ballerina.projects.ProjectException;
import io.ballerina.projects.directory.BuildProject;
import picocli.CommandLine;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Map;

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
        databaseConnector = new DatabaseConnector();
    }


    @Override
    public void execute() {
        Module entityModule = null;
        Path schemaFilePath;


        // Load Ballerina project to get DB driver path.
        Project balProject;
        try {
            balProject = BuildProject.load(Paths.get(this.sourcePath).toAbsolutePath());
        } catch (ProjectException e) {
            errStream.println("ERROR: failed to load the Ballerina project. " + e.getMessage());
            return;
        }

        try (JdbcDriverLoader driverLoader = databaseConnector.getJdbcDriverLoader(balProject)) {
            Driver driver = databaseConnector.getJdbcDriver(driverLoader);

            try (Connection connection = databaseConnector.getConnection(driver)) {

                Introspector introspector = new MySQLIntrospector(connection, "prismaTest");

                Map<String, Entity> entityMap = introspector.introspectDatabase();

                Module.Builder moduleBuilder = Module.newBuilder("prismaTest");

                entityMap.forEach(moduleBuilder::addEntity);
                // add enums and imports
                entityModule = moduleBuilder.build();

                if (entityModule == null) {
                    throw new BalException("ERROR: failed to generate the client object for the entity.");
                }

            } catch (SQLException e) {
                errStream.printf("ERROR: failed to connect to the database. %s%n", e.getMessage());
            }

        } catch (BalException e) {
            errStream.printf("ERROR: failed to execute the SQL scripts for the definition file. %s%n",
                     e.getMessage());
        } catch (IOException e) {
            errStream.printf("ERROR: failed to load the database driver. %s%n", e.getMessage());
        }

        SourceGenerator sourceGenerator = new SourceGenerator(sourcePath,
                Paths.get(sourcePath, PersistToolsConstants.PERSIST_DIRECTORY),
                "prismaTest", entityModule);

        try {
            sourceGenerator.createDbModel();
        } catch (BalException e) {
            errStream.printf(String.format("ERROR: failed to generate model for introspected database: %s%n",
                     e.getMessage()));
            return;
        }
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
