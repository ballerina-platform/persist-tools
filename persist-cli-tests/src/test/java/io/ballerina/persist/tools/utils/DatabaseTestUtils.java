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

import io.ballerina.persist.BalException;
import io.ballerina.persist.PersistToolsConstants;
import io.ballerina.persist.configuration.DatabaseConfiguration;
import io.ballerina.persist.configuration.PersistConfiguration;
import io.ballerina.persist.nodegenerator.syntax.utils.TomlSyntaxUtils;
import org.testng.Assert;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Locale;

import static io.ballerina.persist.nodegenerator.syntax.constants.BalSyntaxConstants.CREATE_DATABASE_SQL_FORMAT;
import static io.ballerina.persist.nodegenerator.syntax.constants.BalSyntaxConstants.DROP_DATABASE_SQL_FORMAT;
import static io.ballerina.persist.nodegenerator.syntax.constants.BalSyntaxConstants.JDBC_URL_WITH_DATABASE_MSSQL;
import static io.ballerina.persist.nodegenerator.syntax.constants.BalSyntaxConstants.JDBC_URL_WITH_DATABASE_MYSQL;
import static io.ballerina.persist.nodegenerator.syntax.constants.BalSyntaxConstants.JDBC_URL_WITH_DATABASE_POSTGRESQL;
import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.GENERATED_SOURCES_DIRECTORY;
import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.INPUT_RESOURCES_DIRECTORY;
import static io.ballerina.projects.util.ProjectConstants.BALLERINA_TOML;

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

    public static void assertCreateDatabaseTables(String packageName, String datasource,
                                                  ArrayList<PersistTable> tables) throws BalException {
        String osName = System.getProperty("os.name");
        if (osName.toLowerCase(Locale.getDefault()).contains("windows")) {
            return;
        }
        PersistConfiguration configuration = TomlSyntaxUtils.readDatabaseConfigurations(
                Paths.get(GENERATED_SOURCES_DIRECTORY, packageName, BALLERINA_TOML));
        String username = configuration.getDbConfig().getUsername();
        String password = configuration.getDbConfig().getPassword();
        String database = configuration.getDbConfig().getDatabase();
        String host = configuration.getDbConfig().getHost();
        int port = configuration.getDbConfig().getPort();

        String url;
        if (datasource.equals(PersistToolsConstants.SupportedDataSources.MSSQL_DB)) {
            url = String.format("jdbc:sqlserver://%s:%s", host, port);
        } else if (datasource.equals(PersistToolsConstants.SupportedDataSources.POSTGRESQL_DB)) {
            url = String.format("jdbc:postgresql://%s:%s/", host, port);
        } else {
            url = String.format("jdbc:mysql://%s:%s", host, port);
        }

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

        if (datasource.equals(PersistToolsConstants.SupportedDataSources.MSSQL_DB)) {
            url = String.format("jdbc:sqlserver://%s:%s/%s", host, port, database);
        } else if (datasource.equals(PersistToolsConstants.SupportedDataSources.POSTGRESQL_DB)) {
            url = String.format("jdbc:postgresql://%s:%s/%s", host, port, database);
        } else {
            url = String.format("jdbc:mysql://%s:%s/%s", host, port, database);
        }
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            errStream.println("Failed to create database connection: " + e.getMessage());
        }

        for (PersistTable persistTable : tables) {
            try {
                Assert.assertTrue(tableExists(connection, database, persistTable.getTableName()));

                validateTable(connection, persistTable);
            } catch (SQLException e) {
                errStream.println("Failed to check if table exists: " + e.getMessage());
            }
        }
    }

    public static void assertCreatedDatabaseNegative(String packageName, String modelName) throws BalException {
        String osName = System.getProperty("os.name");
        if (osName.toLowerCase(Locale.getDefault()).contains("windows")) {
            return;
        }
        PersistConfiguration configuration = TomlSyntaxUtils.readDatabaseConfigurations(
                Paths.get(GENERATED_SOURCES_DIRECTORY, packageName, BALLERINA_TOML));
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
            Assert.assertFalse(databaseExists(connection, database));
        } catch (SQLException e) {
            errStream.println("Failed to check if database exists: " + e.getMessage());
        }
    }
    
    private static boolean databaseExists(Connection connection, String databaseName) throws SQLException {

        boolean exists = false;
        ResultSet resultSet;

        if (connection.getMetaData().getURL().contains("postgresql")) {
            String sql = "SELECT datname FROM pg_database WHERE datistemplate = false;";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                resultSet = preparedStatement.executeQuery();
            }
        } else {
            resultSet = connection.getMetaData().getCatalogs();
        }

        while (resultSet.next()) {
            String database = resultSet.getString(1);
            if (database.equals(databaseName)) {
                exists = true;
                break;
            }
        }
        resultSet.close();
        return exists;
    }

    private static boolean tableExists(Connection connection, String databaseName, String tableName)
            throws SQLException {
        boolean exists;
        DatabaseMetaData meta = connection.getMetaData();
        ResultSet resultSet = meta.getTables(databaseName, null, tableName, new String[] {table});

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

    public static void resetPostgreSqlDatabase(DatabaseConfiguration dbConfig, boolean recreate) {

        String url = String.format(JDBC_URL_WITH_DATABASE_POSTGRESQL, "postgresql",  dbConfig.getHost(),
                dbConfig.getPort(), "postgres");
        resetDatabase(dbConfig, recreate, url);
    }

    public static void resetMsSqlDatabase(DatabaseConfiguration dbConfig, boolean recreate) {

        String url = String.format(JDBC_URL_WITH_DATABASE_MSSQL, "sqlserver",  dbConfig.getHost(),
                dbConfig.getPort(), "master");
        resetDatabase(dbConfig, recreate, url);
    }

    private static void resetDatabase(DatabaseConfiguration dbConfig, boolean recreate, String url) {
        try (Connection connection = DriverManager.getConnection(url, dbConfig.getUsername(), dbConfig.getPassword())) {
            try (Statement statement = connection.createStatement()) {
                statement.execute(String.format(DROP_DATABASE_SQL_FORMAT, dbConfig.getDatabase()));
                if (recreate) {
                    statement.execute(String.format(CREATE_DATABASE_SQL_FORMAT, dbConfig.getDatabase()));
                }
                statement.executeBatch();
                PrintStream outStream = System.out;
                outStream.println("Database reset successfully");
            }
        } catch (SQLException e) {
            errStream.println("Failed to reset database: " + e.getMessage());
        }
    }

    public static void createFromDatabaseScript(String packageName, String datastore,
                                                DatabaseConfiguration dbConfig) {
        Path sourcePath = Paths.get(INPUT_RESOURCES_DIRECTORY, packageName);

        String url;
        if (datastore.equals(PersistToolsConstants.SupportedDataSources.MSSQL_DB)) {
            url = String.format(JDBC_URL_WITH_DATABASE_MSSQL, "sqlserver", dbConfig.getHost(), dbConfig.getPort(),
                    "master");
        } else if (datastore.equals(PersistToolsConstants.SupportedDataSources.POSTGRESQL_DB)) {
            url = String.format(JDBC_URL_WITH_DATABASE_POSTGRESQL, "postgresql",  dbConfig.getHost(),
                    dbConfig.getPort(), dbConfig.getDatabase());
        } else {
            url = String.format(JDBC_URL_WITH_DATABASE_MYSQL, "mysql", dbConfig.getHost(), dbConfig.getPort(), "mysql");
        }

        try (Connection connection = DriverManager.getConnection(url, dbConfig.getUsername(), dbConfig.getPassword())) {
            Path scriptFilePath = sourcePath.resolve("script.sql");
            //15 lines skipped to avoid license headers
            String scriptContent = Files.lines(scriptFilePath).skip(15).reduce("", String::concat);
            try (Statement statement = connection.createStatement()) {
                String sql = scriptContent.replace("\r\n", "\n");
                String[] statements = sql.split(";");
                for (String statementStr : statements) {
                    statement.addBatch(statementStr);
                }
                statement.executeBatch();
            }
        } catch (SQLException e) {
            errStream.println("Failed to create database connection: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
