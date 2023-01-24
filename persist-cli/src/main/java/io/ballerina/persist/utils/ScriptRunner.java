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

import java.io.BufferedReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

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
            throw new Exception("Could not commit transaction. Message: " + t.getMessage(), t);
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
            throw new Exception("Line missing end-of-line terminator (" + DEFAULT_DELIMITER + ") => " + command);
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
}
