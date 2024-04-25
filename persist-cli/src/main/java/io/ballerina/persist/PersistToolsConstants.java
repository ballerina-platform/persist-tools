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
package io.ballerina.persist;

import io.ballerina.tools.diagnostics.DiagnosticSeverity;

import java.util.Set;

/**
 * Persist Tool constants class.
 *
 * @since 0.1.0
 */
public class PersistToolsConstants {

    public static final String BAL_PERSIST_ADD_CMD = "add";

    private PersistToolsConstants() {}

    public static final String COMPONENT_IDENTIFIER = "persist";

    public static final String EMPTY_VALUE = "";
    public static final String KEYWORD_PACKAGE = "package";
    public static final String KEYWORD_NAME = "name";
    public static final String KEYWORD_SHEET_ID = "spreadsheetId";
    public static final String KEYWORD_CLIENT_ID = "clientId";
    public static final String KEYWORD_CLIENT_SECRET = "clientSecret";
    public static final String KEYWORD_REFRESH_TOKEN = "refreshToken";

    public static final String CONFIG_SCRIPT_FILE = "Config.toml";
    public static final String PASSWORD = "password";
    public static final String USER = "user";
    public static final String MYSQL_DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    public static final String MSSQL_DRIVER_CLASS = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    public static final String POSTGRESQL_DRIVER_CLASS = "org.postgresql.Driver";
    public static final String PERSIST_CONFIG_PATTERN = "persist.model.storage";
    public static final String SQL_SCHEMA_FILE = "script.sql";
    public static final String GOOGLE_SHEETS_SCHEMA_FILE = "script.gs";
    public static final String PERSIST_DIRECTORY = "persist";
    public static final String TARGET_DIRECTORY = "target";
    public static final String GENERATE_CMD_FILE = "Persist.toml";
    public static final String PERSIST_TOOL_CONFIG = "tool.persist";
    public static final String GENERATED_DIRECTORY = "generated";
    public static final String TARGET_MODULE = "targetModule";
    public static final String MIGRATIONS = "migrations";
    public static final String BALLERINA_MYSQL_DRIVER_NAME = "ballerinax/mysql.driver";
    public static final String BALLERINA_MSSQL_DRIVER_NAME = "ballerinax/mssql.driver";
    public static final String BALLERINA_POSTGRESQL_DRIVER_NAME = "ballerinax/postgresql.driver";
    public static final String PROPERTY_KEY_PATH = "path";
    public static final String MYSQL_CONNECTOR_NAME_PREFIX = "mysql-connector";
    public static final String MSSQL_CONNECTOR_NAME_PREFIX = "mssql-jdbc";
    public static final String POSTGRESQL_CONNECTOR_NAME_PREFIX = "postgresql";
    public static final String SCHEMA_FILE_NAME = "model";
    public static final String CACHE_FILE = "persist-cache.txt";
    public static final String UNSUPPORTED_TYPE = "Unsupported";
    public static final String UNSUPPORTED_TYPE_COMMENT_START = "//" + UNSUPPORTED_TYPE + "[";

    public static final Set<String> SUPPORTED_DB_PROVIDERS =
           Set.of(SupportedDataSources.MYSQL_DB, SupportedDataSources.MSSQL_DB, SupportedDataSources.IN_MEMORY_TABLE,
                   SupportedDataSources.GOOGLE_SHEETS, SupportedDataSources.POSTGRESQL_DB, SupportedDataSources.REDIS);
    public static final Set<String> SUPPORTED_NOSQL_DB_PROVIDERS = 
            Set.of(SupportedDataSources.IN_MEMORY_TABLE, SupportedDataSources.GOOGLE_SHEETS, 
            SupportedDataSources.REDIS);

    /**
     * Constants related to Ballerina types.
     */
    public static final class BallerinaTypes {

        private BallerinaTypes() {}

        public static final String INT = "int";
        public static final String STRING = "string";
        public static final String BOOLEAN = "boolean";
        public static final String DECIMAL = "decimal";
        public static final String FLOAT = "float";
        public static final String DATE = "time:Date";
        public static final String TIME_OF_DAY = "time:TimeOfDay";
        public static final String UTC = "time:Utc";
        public static final String CIVIL = "time:Civil";
        public static final String BYTE = "byte";
    }

    /**
     * Constants related to SQL types.
     */
    public static final class SqlTypes {
        private SqlTypes() {}

        public static final String INT = "INT";
        public static final String BIGINT = "BIGINT";
        public static final String TINYINT = "TINYINT";
        public static final String SMALLINT = "SMALLINT";
        public static final String MEDIUMINT = "MEDIUMINT";
        public static final String INTEGER = "INTEGER";
        public static final String BIT = "BIT";
        public static final String BOOLEAN = "BOOLEAN";
        public static final String DECIMAL = "DECIMAL";
        public static final String DOUBLE = "DOUBLE";
        public static final String FLOAT = "FLOAT";
        public static final String VARCHAR = "VARCHAR";
        public static final String CHAR = "CHAR";
        public static final String TINYTEXT = "TINYTEXT";
        public static final String TEXT = "TEXT";
        public static final String MEDIUMTEXT = "MEDIUMTEXT";
        public static final String LONGTEXT = "LONGTEXT";
        public static final String DATE = "DATE";
        public static final String TIME = "TIME";
        public static final String TIME_STAMP = "TIMESTAMP";
        public static final String DATE_TIME = "DATETIME";
        public static final String DATE_TIME2 = "DATETIME2";
        public static final String LONG_BLOB = "LONGBLOB";
        public static final String MEDIUM_BLOB = "MEDIUMBLOB";
        public static final String TINY_BLOB = "TINYBLOB";
        public static final String BLOB = "BLOB";
        public static final String VARBINARY_WITH_MAX = "VARBINARY(MAX)";
        public static final String BINARY = "BINARY";
        public static final String VARBINARY = "VARBINARY";
        public static final String BYTEA = "BYTEA";
        public static final String BOOLEAN_ALT = "tinyint(1)";
    }

    /**
     * Constants related to SQL script comments.
     */
    public static final class SqlScriptComments {
        private SqlScriptComments() {}

        public static final String AUTOGENERATED_FILE_COMMENT = "-- AUTO-GENERATED FILE.";

        public static final String AUTO_GENERATED_COMMENT_WITH_REASON =
                "-- This file is an auto-generated file by Ballerina persistence layer for %s.";
        public static final String COMMENT_SHOULD_BE_VERIFIED_AND_EXECUTED =
                "-- Please verify the generated scripts and execute them against the target DB server.";
    }

    /**
     * Constants related to AppScript script comments.
     */
    public static final class AppScriptComments {
        private AppScriptComments() {}

        public static final String AUTOGENERATED_FILE_COMMENT = "// AUTO-GENERATED FILE.";

        public static final String AUTO_GENERATED_COMMENT_WITH_REASON =
                "// This file is an auto-generated file by Ballerina persistence layer for %s.";
        public static final String COMMENT_SHOULD_BE_VERIFIED_AND_EXECUTED =
                "// Please verify the generated scripts and execute them against the target DB server.";
    }

    /**
     * Constants related to the length of SQL types.
     */
    public static final class DefaultMaxLength {

        private DefaultMaxLength() {}

        public static final int VARCHAR_LENGTH = 191;
        public static final int DECIMAL_PRECISION_MYSQL = 65;
        public static final int DECIMAL_PRECISION_MSSQL = 38;
        public static final int DECIMAL_PRECISION_POSTGRESQL = 65;
        public static final int DECIMAL_SCALE = 30;
    }

    /**
     * Constants related to the ballerina.toml file.
     */
    public static final class TomlFileConstants {
        private TomlFileConstants() {}

        public static final String VERSION_PROPERTIES_FILE = "version.properties";
        public static final String KEYWORD_GROUP_ID = "groupId";
        public static final String PERSIST_GROUP_ID = "io.ballerina.stdlib";
        public static final String PERSIST_LIB_GROUP_ID = "io.ballerina.lib";
        public static final String KEYWORD_ARTIFACT_ID = "artifactId";
        public static final String ARTIFACT_ID = "%s-native";
        public static final String PERSIST_SQL_VERSION = "persistSqlVersion";
        public static final String PERSIST_IN_MEMORY_VERSION = "persistInMemoryVersion";
        public static final String PERSIST_GOOGLE_SHEETS_VERSION = "persistGoogleSheetsVersion";
        public static final String PERSIST_REDIS_VERSION = "persistRedisVersion";
        public static final String KEYWORD_VERSION = "version";
    }

    /**
     * Constants related to the data sources.
     */
    public static final class SupportedDataSources {
        private SupportedDataSources() {}

        public static final String MYSQL_DB = "mysql";
        public static final String MSSQL_DB = "mssql";
        public static final String GOOGLE_SHEETS = "googlesheets";
        public static final String IN_MEMORY_TABLE = "inmemory";
        public static final String POSTGRESQL_DB = "postgresql";
        public static final String REDIS = "redis";
    }

    /**
     * Constants related to the database configurations.
     */
    public static final class DBConfigs {

        private DBConfigs() {}

        public static final String KEY_USER = "user";
        public static final String KEY_PORT = "port";
        public static final String KEY_PASSWORD = "password";
        public static final String KEY_DATABASE = "database";
        public static final String KEY_HOST = "host";
        public static final String KEY_CONNECTION = "connection";
        public static final String KEY_MAX_AGE = "maxAge";

        /**
         * Constants related to the MySQL configurations.
         */
        public static final class MySQL {
            private MySQL() {}

            public static final String DEFAULT_HOST = "localhost";
            public static final String DEFAULT_PORT = "3306";
            public static final String DEFAULT_USER = "root";

        }

        /**
         * Constants related to the MSSQL configurations.
         */
        public static final class MSSQL {
            private MSSQL() {}

            public static final String DEFAULT_HOST = "localhost";
            public static final String DEFAULT_PORT = "1433";
            public static final String DEFAULT_USER = "sa";

        }

        /**
         * Constants related to the PostgreSQL configurations.
         */
        public static final class POSTGRESQL {
            private POSTGRESQL() {}

            public static final String DEFAULT_HOST = "localhost";
            public static final String DEFAULT_PORT = "5432";
            public static final String DEFAULT_USER = "postgres";

        }

        /**
         * Constants related to the PostgreSQL configurations.
         */
        public static final class REDIS {
            private REDIS() {}

            public static final String CONNECTION_URI = "redis://localhost:6379";
            public static final String MAX_AGE = "-1";

        }
    }

    /**
     * Enum class for containing diagnostic messages.
     */
    public enum DiagnosticMessages {
        INVALID_MODULE_NAME("PERSIST_CLIENT_01", "invalid module name : `%s`" + System.lineSeparator() +
                "module name should follow the template \"<package_name>.<module_name>\"",
                DiagnosticSeverity.ERROR),
        ERROR_WHILE_GENERATING_CLIENT("PERSIST_CLIENT_02", "unexpected error occurred while generating the client"
                + System.lineSeparator() + "%s",
                DiagnosticSeverity.ERROR);

        private final String code;
        private final String description;
        private final DiagnosticSeverity severity;

        DiagnosticMessages(String code, String description, DiagnosticSeverity severity) {
            this.code = code;
            this.description = description;
            this.severity = severity;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public DiagnosticSeverity getSeverity() {
            return severity;
        }
    }
}
