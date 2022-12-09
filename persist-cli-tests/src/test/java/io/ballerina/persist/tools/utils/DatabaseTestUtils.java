/*
 *  Copyright (c) 2022, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package io.ballerina.persist.tools.utils;

import io.ballerina.persist.configuration.PersistConfiguration;
import io.ballerina.persist.nodegenerator.SyntaxTreeGenerator;
import io.ballerina.persist.objects.BalException;
import org.testng.Assert;

import java.io.PrintStream;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;

import static io.ballerina.persist.objects.PersistToolsConstants.PERSIST_DIRECTORY;
import static io.ballerina.persist.objects.PersistToolsConstants.PERSIST_TOML_FILE;
import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.GENERATED_SOURCES_DIRECTORY;

/**
 * Test util functions related to Push command.
 */
public class DatabaseTestUtils {

    private static final PrintStream errStream = System.err;
    private static final String table = "TABLE";
    private static final String columnName = "COLUMN_NAME";
    private static final String columnType = "TYPE_NAME";
    private static final String autoincrement = "IS_AUTOINCREMENT";
    private static final String nullable = "IS_NULLABLE";

    public static void assertCreateDatabaseTables(String subDir, ArrayList<PersistTable> tables) throws BalException {
        String osName = System.getProperty("os.name");
        if (osName.toLowerCase(Locale.getDefault()).contains("windows")) {
            return;
        }
        PersistConfiguration configuration = SyntaxTreeGenerator.readPersistToml(
                Paths.get(GENERATED_SOURCES_DIRECTORY, subDir, PERSIST_DIRECTORY, PERSIST_TOML_FILE));
        String username = configuration.getDbConfig().getUsername();
        String password = configuration.getDbConfig().getPassword();
        String database = configuration.getDbConfig().getDatabase();
        String host = configuration.getDbConfig().getHost();
        int port = configuration.getDbConfig().getPort();

        String url = String.format("jdbc:mysql://%s:%s", host, port);

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            errStream.println("Failed to create database connection: " + e.getMessage());
        }

        try {
            assert connection != null;
            Assert.assertTrue(databaseExists(connection, database));
        } catch (SQLException e) {
            errStream.println("Failed to check if database exists: " + e.getMessage());
        }

        for (PersistTable persistTable : tables) {
            try {
                Assert.assertTrue(tableExists(connection, persistTable.getTableName()));

                validateTable(connection, persistTable);
            } catch (SQLException e) {
                errStream.println("Failed to check if table exists: " + e.getMessage());
            }
        }
    }

    private static boolean databaseExists(Connection connection, String databaseName) throws SQLException {

        boolean exists = false;
        ResultSet resultSet = connection.getMetaData().getCatalogs();

        while (resultSet.next()) {
            if (resultSet.getString(1).equals(databaseName)) {
                exists = true;
            }
        }
        resultSet.close();
        return exists;
    }

    private static boolean tableExists(Connection connection, String tableName) throws SQLException {
        boolean exists;
        DatabaseMetaData meta = connection.getMetaData();
        ResultSet resultSet = meta.getTables(null, null, tableName, new String[] {table});

        exists =  resultSet.next();
        resultSet.close();
        return exists;
    }

    private static void validateTable(Connection connection, PersistTable persistTable) throws SQLException {

        DatabaseMetaData databaseMetaData = connection.getMetaData();
        ResultSet columns = databaseMetaData.getColumns(null, null, persistTable.getTableName(), null);

        ResultSet primaryKeys = databaseMetaData.getPrimaryKeys(null, null, persistTable.getTableName());
        while (primaryKeys.next()) {
            Assert.assertEquals(persistTable.getPrimaryKey(), primaryKeys.getString(columnName));
        }

        while (columns.next()) {
            Assert.assertTrue(persistTable.hasColumn(columns.getString(columnName)));

            Assert.assertEquals(persistTable.getColumnType(columns.getString(columnName)),
                    columns.getString(columnType));
            Assert.assertEquals(persistTable.isAutoIncrement(columns.getString(columnName)),
                    columns.getString(autoincrement));
            Assert.assertEquals(persistTable.isNullable(columns.getString(columnName)),
                    columns.getString(nullable));
        }
    }
}
