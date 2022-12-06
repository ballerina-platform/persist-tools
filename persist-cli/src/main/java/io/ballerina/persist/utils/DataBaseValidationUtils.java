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

    private DataBaseValidationUtils(){}

    public static String validateDatabaseInput(String inputDatabase) throws BalException {
        if (inputDatabase == null) {
            throw new BalException("Database name is null. ");
        }
        String database = inputDatabase.trim();
        if (database.length() > 64) {
            throw new BalException("Database name length exceeds the limit. ");
        } else if (database.length() == 0) {
            throw new BalException("Database name is empty. ");
        } else {
            Pattern regex = Pattern.compile("[^A-Za-z0-9$_]");
            Matcher matcher = regex.matcher(database);
            boolean hasSpecialChars = matcher.find();
            if (hasSpecialChars) {
                throw new BalException("Database name contains illegal characters. ");
            }
            return database;
        }
    }
}
