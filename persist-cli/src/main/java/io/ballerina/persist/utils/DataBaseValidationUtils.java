package io.ballerina.persist.utils;

import io.ballerina.persist.objects.BalException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Sql script validator!.
 *
 * @since 0.1.0
 */
public class DataBaseValidationUtils {

    public static final String REGEX_DB_NAME_PATTERN = "[^A-Za-z\\d$_]";

    private DataBaseValidationUtils(){}

    public static String validateDatabaseInput(String databaseName) throws BalException {
        if (databaseName == null || databaseName.isEmpty() || databaseName.isBlank()) {
            throw new BalException("Database name cannot be empty");
        }
        String database = databaseName.trim();
        if (database.length() > 64) {
            throw new BalException("Database name should be less than or equal 64 characters");
        } else {
            Pattern regex = Pattern.compile(REGEX_DB_NAME_PATTERN);
            Matcher matcher = regex.matcher(database);
            boolean illegalCharExists = matcher.find();
            if (illegalCharExists) {
                throw new BalException("Database name contains illegal characters. "); // Add illegal character here.
            }
            return database;
        }
    }
}
