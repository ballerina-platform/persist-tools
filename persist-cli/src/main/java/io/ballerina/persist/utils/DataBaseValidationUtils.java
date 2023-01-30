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

import io.ballerina.persist.BalException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Sql script validator!.
 *
 * @since 0.1.0
 */
public class DataBaseValidationUtils {

    private static final String REGEX_DB_NAME_PATTERN = "[^A-Za-z\\d$_]";

    private DataBaseValidationUtils(){}

    public static String validateDatabaseInput(String databaseName) throws BalException {
        if (databaseName == null || databaseName.isEmpty() || databaseName.isBlank()) {
            throw new BalException("database name cannot be empty");
        }
        String database = databaseName.trim();
        if (database.length() > 64) {
            throw new BalException("database name should be less than or equal to 64 characters");
        } else {
            Pattern regex = Pattern.compile(REGEX_DB_NAME_PATTERN);
            Matcher matcher = regex.matcher(database);
            boolean illegalCharExists = matcher.find();
            if (illegalCharExists) {
                throw new BalException("database name contains illegal characters. "); // Add illegal character here.
            }
            return database;
        }
    }
}
