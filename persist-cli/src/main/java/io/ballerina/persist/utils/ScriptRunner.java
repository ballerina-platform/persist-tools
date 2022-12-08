package io.ballerina.persist.utils;

import io.ballerina.persist.objects.BalException;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

/**
 * Sql script runner!.
 *
 * @since 0.1.0
 */
public class ScriptRunner {

    private final PrintStream stdStream = System.out;

    private final Connection connection;
    public ScriptRunner(Connection connection) {
        this.connection = connection;
    }

    public void runScript(String[] sqlScript) throws BalException, SQLException {
        setAutoCommit();
        try {
            executeFullScript(sqlScript);
        } finally {
            rollbackConnection();
        }
    }

    public void runQuery(String sqlQuery) throws SQLException {
        setAutoCommit();
        try {
            executeStatement(sqlQuery);
        } finally {
            rollbackConnection();
        }
    }

    private void executeFullScript(String[] sqlScript) throws BalException {
        try {
            for (String sqlLine : sqlScript) {
                executeStatement(sqlLine);
            }
            commitConnection();
        } catch (SQLWarning e) {
            stdStream.println("Warning: " + e.getMessage());
        } catch (SQLException e) {
            throw new BalException(e.getMessage());
        }
    }
    private void commitConnection() {
        try {
            if (!connection.getAutoCommit()) {
                connection.commit();
            }
        } catch (Throwable t) {
            throw new RuntimeException("Could not commit transaction. Cause: " + t, t);
        }
    }

    private void rollbackConnection() throws SQLException {
        setAutoCommit();
        try {
            if (!connection.getAutoCommit()) {
                connection.rollback();
            }
        } catch (SQLWarning w) {
            stdStream.println(w.getMessage());
        }
    }

    private void executeStatement(String command) throws SQLException {
        Statement statement = connection.createStatement();
        try {
            statement.execute(command);
        } finally {
            statement.close();
        }
    }

    private void setAutoCommit() {
        try {
            if (!connection.getAutoCommit()) {
                connection.setAutoCommit(true);
            }
        } catch (Throwable t) {
            throw new RuntimeException("Could not set AutoCommit to " + "true" + ". Cause: " + t, t);
        }
    }


}
