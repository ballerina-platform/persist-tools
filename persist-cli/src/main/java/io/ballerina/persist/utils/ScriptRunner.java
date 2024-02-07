/*
 * Copyright (c) 2022, WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.ballerina.persist.utils;

import io.ballerina.persist.introspectiondto.SQLColumn;
import io.ballerina.persist.introspectiondto.SQLForeignKey;
import io.ballerina.persist.introspectiondto.SQLIndex;
import io.ballerina.persist.introspectiondto.SQLTable;

import java.io.BufferedReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


/**
 * The script runner class executes SQL query or scripts against DB.
 * @since 0.1.0
 */
public class ScriptRunner {
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private static final String DEFAULT_DELIMITER = ";";

    private final Connection connection;

    public ScriptRunner(Connection connection) {
        this.connection = connection;
    }

    public void runScript(Reader reader) throws Exception {
        try {
            executeLineByLine(reader);
        } finally {
            rollbackConnection();
        }
    }

    public void runQuery(String query) throws SQLException {
        try {
            executeStatement(query);
        } finally {
            rollbackConnection();
        }
    }




    private void executeLineByLine(Reader reader) throws Exception {
        StringBuilder command = new StringBuilder();
        BufferedReader lineReader = new BufferedReader(reader);
        String line;
        while ((line = lineReader.readLine()) != null) {
            handleLine(command, line);
        }
        commitConnection();
        checkForMissingLineTerminator(command);
    }

    private void commitConnection() throws Exception {
        try {
            if (!connection.getAutoCommit()) {
                connection.commit();
            }
        } catch (Throwable t) {
            throw new Exception("could not commit transaction. Message: " + t.getMessage(), t);
        }
    }

    private void rollbackConnection() {
        try {
            if (!connection.getAutoCommit()) {
                connection.rollback();
            }
        } catch (Throwable t) {
            // ignore
        }
    }

    private void checkForMissingLineTerminator(StringBuilder command) throws Exception {
        if (command != null && command.toString().trim().length() > 0) {
            throw new Exception("line missing end-of-line terminator (" + DEFAULT_DELIMITER + ") => " + command);
        }
    }

    private void handleLine(StringBuilder command, String line) throws SQLException {
        String trimmedLine = line.trim();
        if (lineIsComment(trimmedLine)) {
            return;
        }

        if (commandReadyToExecute(trimmedLine)) {
            command.append(line, 0, line.lastIndexOf(DEFAULT_DELIMITER) + 1);
            command.append(LINE_SEPARATOR);
            executeStatement(command.toString());
            command.setLength(0);
        } else if (trimmedLine.length() > 0) {
            command.append(line);
            command.append(LINE_SEPARATOR);
        }
    }

    private boolean lineIsComment(String trimmedLine) {
        return trimmedLine.startsWith("//") || trimmedLine.startsWith("--");
    }

    private boolean commandReadyToExecute(String trimmedLine) {
        return trimmedLine.contains(DEFAULT_DELIMITER);
    }

    private void executeStatement(String command) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String sql = command;
            // remove CR
            sql = sql.replace("\r\n", "\n");
            statement.execute(sql);
        }
    }

    public List<SQLTable> getSQLTables(String query) throws SQLException {
        List<SQLTable> tables = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            try (ResultSet results = statement.executeQuery(query)) {
                while (results.next()) {
                    tables.add(SQLTable.newBuilder(results.getString("table_name"))
                            .setTableComment(results.getString("table_comment"))
                            .setCreateOptions(results.getString("create_options")).build());
                }
                return tables;
            }
        } catch (SQLException e) {
            throw new SQLException("Error while retrieving tables for database: " + e.getMessage());
        } finally {
            rollbackConnection();
        }
    }

    public void readColumnsOfSQLTable(SQLTable table, String query) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            try (ResultSet results = statement.executeQuery(query)) {
                while (results.next()) {
                    SQLColumn column = SQLColumn.newBuilder(results.getString("column_name"))
                            .setTableName(results.getString("table_name"))
                            .setDataType(results.getString("data_type"))
                            .setFullDataType(results.getString("full_data_type"))
                            .setCharacterMaximumLength(results.getString("character_maximum_length"))
                            .setNumericPrecision(results.getString("numeric_precision"))
                            .setNumericScale(results.getString("numeric_scale"))
                            .setDatetimePrecision(results.getString("datetime_precision"))
                            .setColumnDefault(results.getString("column_default"))
                            .setIsNullable(results.getString("is_nullable"))
                            .setExtra(results.getString("extra"))
                            .setColumnComment(results.getString("column_comment"))
                            .setIsPrimaryKey(results.getString("column_key").equals("PRI"))
                            .setIsDbGenerated(results.getBoolean("dbgenerated"))
                            .build();
                    table.addColumn(column);
                    }
            }
        } catch (SQLException e) {
            throw new SQLException("Error while retrieving columns for table: " + e.getMessage());
        } finally {
            rollbackConnection();
        }
    }

    public List<SQLForeignKey> readForeignKeysOfSQLTable(SQLTable table, String query) throws SQLException {
        List<SQLForeignKey> sqlForeignKeys = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            try (ResultSet results = statement.executeQuery(query)) {
                while (results.next()) {
                    String constraintName = results.getString("constraint_name");
                    SQLForeignKey existingForeignKey = table.getSqlForeignKeys().stream().filter(
                                    fKey -> fKey.getConstraintName().equals(constraintName))
                            .findFirst().orElse(null);
                    if (existingForeignKey == null) {
                        SQLForeignKey foreignKey = SQLForeignKey.Builder
                                .newBuilder(results.getString("constraint_name"))
                                .setTableName(results.getString("table_name"))
                                .addColumnName(results.getString("column_name"))
                                .setReferencedTableName(results.getString("referenced_table_name"))
                                .addReferencedColumnName(results.getString("referenced_column_name"))
                                .setUpdateRule(results.getString("update_rule"))
                                .setDeleteRule(results.getString("delete_rule"))
                                .build();
                        table.addForeignKey(foreignKey);
                        sqlForeignKeys.add(foreignKey);
                    } else {
                        existingForeignKey.addColumnName(results.getString("column_name"));
                        existingForeignKey.addReferencedColumnName(results.getString("referenced_column_name"));
                    }
                }
            }
            return sqlForeignKeys;
        } catch (SQLException e) {
            throw new SQLException("Error while retrieving foreign keys for table: " + e.getMessage());
        } finally {
            rollbackConnection();

        }
    }

    public void readIndexesOfSQLTable(SQLTable table, String query) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            try (ResultSet results = statement.executeQuery(query)) {
                while (results.next()) {
                    String indexName = results.getString("index_name");
                    SQLIndex existingIndex = table.getIndexes().stream().filter(
                            index -> index.getIndexName().equals(indexName))
                            .findFirst().orElse(null);
                    if (existingIndex == null) {
                        table.addIndex(SQLIndex.Builder.newBuilder(results.getString("index_name"))
                                .setTableName(results.getString("table_name"))
                                .addColumnName(results.getString("column_name"))
                                .setPartial(results.getString("partial"))
                                .setColumnOrder(results.getString("column_order"))
                                .setNonUnique(results.getString("non_unique"))
                                .setIndexType(results.getString("index_type"))
                                .build()
                        );
                    } else {
                        existingIndex.addColumnName(results.getString("column_name"));
                    }
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error while retrieving indexes for table: " + e.getMessage());
        } finally {
            rollbackConnection();
        }
    }




}
