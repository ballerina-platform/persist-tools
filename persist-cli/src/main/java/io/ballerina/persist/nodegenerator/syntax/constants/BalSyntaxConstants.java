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
package io.ballerina.persist.nodegenerator.syntax.constants;

/**
 * Class encapsulating all the syntax/code lines related to generation scripts.
 *
 * @since 0.1.0
 */
public class BalSyntaxConstants {

    private BalSyntaxConstants() {}

    /**
     * Constants related to persist client type inheritance.
     */
    public static class InheritedTypeReferenceConstants {
        private InheritedTypeReferenceConstants() {
        }

        public static final String PERSIST_MODULE_NAME = "persist";
        public static final String ABSTRACT_PERSIST_CLIENT = "AbstractPersistClient";
    }
    public static final String DOUBLE_QUOTE = "\"";
    public static final String BACK_SLASH = "/";
    public static final String EMPTY_STRING = "";
    public static final String SPACE = " ";
    public static final String UNDERSCORE = "_";
    public static final String SINGLE_QUOTE = "'";
    public static final String COMMA_WITH_SPACE = ", ";
    public static final String COLON = ":";
    public static final String SEMICOLON = ";";
    public static final String EQUAL = "=";
    public static final String VALUE = "value";
    public static final String PERSIST_MODULE = "persist";
    public static final String ENUM = "ENUM";
    public static final String PERSIST_ERROR = "persist:Error";
    public static final String GET_G_SHEET_PERSIST_CLIENT = "googleSheetsClient = self.persistClients.get(%s);";
    public static final String G_SHEET_CLIENT_DECLARATION = "googlesheets:GoogleSheetsClient googleSheetsClient;";
    public static final String G_SHEET_CREATE_SQL_RESULTS = "_ = check googleSheetsClient.runBatchInsertQuery(data);";
    public static final String SQL_CLIENT_DECLARATION = "psql:SQLClient sqlClient;";
    public static final String CREATE_SQL_RESULTS = "_ = check sqlClient.runBatchInsertQuery(data);";
    public static final String CREATE_SQL_RESULTS_AUTO_INCREMENT =
            "sql:ExecutionResult[] result = check sqlClient.runBatchInsertQuery(data);";
    public static final String GET_PERSIST_CLIENT = "sqlClient = self.persistClients.get(%s);";

    public static final String CREATE_ARRAY_VAR = "%s keys = [];";
    public static final String POST_RETURN = "return keys;";
    public static final String HAS_KEY = "\tif %sTable.hasKey(%s) {";
    public static final String VARIABLE_TYPE = "%s[]";
    public static final String FIELD = "value.%s";
    public static final String FIELD_WITH_KEY = "%s: value.%s";

    public static final String FOREACH_STMT_START = "foreach %s value in data {" + System.lineSeparator();
    public static final String HAS_NOT_KEY = "!%sTable.hasKey(%s)";
    public static final String UPDATE_RECORD_FIELD_VALUE = "foreach var [k, v] in value.clone().entries() {" +
            System.lineSeparator() + "        %s[k] = v;" + System.lineSeparator() +
            "    }" + System.lineSeparator();
    public static final String HAS_KEY_ERROR = "\t\treturn persist:getAlreadyExistsError(\"%s\", %s);"
            + System.lineSeparator() + "\t}" + System.lineSeparator();
    public static final String HAS_NOT_KEY_ERROR = "\t\treturn persist:getNotFoundError(\"%s\", %s);";
    public static final String PUSH_VALUES = System.lineSeparator() + "\tkeys.push(%s);" + System.lineSeparator();
    public static final String GET_UPDATE_RECORD = "%s %s = %sTable.get(%s);" + System.lineSeparator();
    public static final String PUT_VALUE_TO_MAP = "%sTable.put(%s);";
    public static final String RETURN_STATEMENT = "return %s.clone();";

    public static final String RETURN_CREATED_KEY = "return from  %s inserted in data" + System.lineSeparator();
    public static final String RETURN_CREATED_KEY_AUTO_INCREMENT =
            "return from  %s inserted in result" + System.lineSeparator();
    public static final String RETURN_FILTERED_AUTO_INCREMENT_KEYS =
            "where inserted.lastInsertId != ()" + System.lineSeparator() +
                    "select <%s>inserted.lastInsertId;" + System.lineSeparator();
    public static final String SELECT_WITH_SPACE = "\t\t\tselect ";
    public static final String UPDATE_RUN_UPDATE_QUERY = "_ = check sqlClient.runUpdateQuery(%s, value);";
    public static final String G_SHEET_UPDATE_RUN_UPDATE_QUERY = "_ = check googleSheetsClient." +
            "runUpdateQuery(%s, value);";
    public static final String UPDATE_RETURN_UPDATE_QUERY = "return self->%s.get();";
    public static final String G_SHEET_DELETE_RUN_DELETE_QUERY = "_ = check googleSheetsClient." +
            "runDeleteQuery(%s);";
    public static final String DELETE_RUN_DELETE_QUERY = "_ = check sqlClient.runDeleteQuery(%s);";
    public static final String RETURN_DELETED_OBJECT = "return result;";
    public static final String DELETED_OBJECT = "return %sTable.remove(%s).clone();";
    public static final String GET_OBJECT_QUERY = "%s result = check self->%s.get();";

    public static final String CONFIGURABLE_PORT = "configurable int port = ?;";
    public static final String CONFIGURABLE_HOST = "configurable string host = ?;";
    public static final String CONFIGURABLE_USER = "configurable string user = ?;";
    public static final String CONFIGURABLE_PASSWORD = "configurable string password = ?;";
    public static final String CONFIGURABLE_DATABASE = "configurable string database = ?;";
    public static final String CONFIGURABLE_CLIENT_ID = "configurable string clientId = ?;" + System.lineSeparator();
    public static final String CONFIGURABLE_CLIENT_SECRET = "configurable string clientSecret = ?;" +
            System.lineSeparator();
    public static final String CONFIGURABLE_REFRESH_TOKEN = "configurable string refreshToken = ?;" +
            System.lineSeparator();
    public static final String CONFIGURABLE_WORK_SHEET_ID = "configurable string spreadsheetId = ?;" +
            System.lineSeparator();
    public static final String CONFIGURABLE_OPTIONS = "configurable %s:Options & readonly connectionOptions = {};";
    public static final String INIT = "init";
    public static final String POST = "post";
    public static final String DELETE = "delete";
    public static final String PUT = "put";
    public static final String CLOSE = "close";
    public static final String QUERY = "query%s";
    public static final String QUERY_ONE = "queryOne%s";
    public static final String INSERT_RECORD = "%sInsert";
    public static final String UPDATE_RECORD = "%sUpdate";
    public static final String SPECIFIC_ERROR = "Error";
    public static final String KEYWORD_BALLERINA = "ballerina";
    public static final String KEYWORD_VALUE = "data";
    public static final String KEYWORD_FIELDS = "fields";
    public static final String KEYWORD_KEY = "key";
    public static final String KEYWORD_PERSIST = "persist";
    public static final String KEYWORD_BALLERINAX = "ballerinax";
    public static final String KEYWORD_CLIENT = "client";
    public static final String ERROR = "error";
    public static final String KEYWORD_PUBLIC = "public";
    public static final String KEYWORD_PRIVATE = "private";
    public static final String KEYWORD_ISOLATED = "isolated";
    public static final String KEYWORD_RESOURCE = "resource";
    public static final String ANY_DATA = "anydata";

    public static final String GENERATED_SOURCE_DIRECTORY = "generated";
    public static final String MODULES_SOURCE_DIRECTORY = "modules";
    public static final String PATH_DB_CONFIGURATION_BAL_FILE = "persist_db_config.bal";
    public static final String PATH_SHEET_CONFIGURATION_BAL_FILE = "persist_sheet_config.bal";
    public static final String PERSIST_DEPENDENCY = "platform.java17.dependency";
    public static final String KEYWORD_JBALLERINA_JAVA_PREFIX = "jballerina.java";
    public static final String KEYWORD_HTTP = "http";
    public static final String KEYWORD_READONLY = "readonly";
    public static final String JDBC_URL_WITHOUT_DATABASE = "jdbc:%s://%s:%s/";
    public static final String JDBC_URL_WITH_DATABASE_MYSQL = "jdbc:%s://%s:%s/%s";
    public static final String JDBC_URL_WITH_DATABASE_MSSQL = "jdbc:%s://%s:%s;databaseName=%s";
    public static final String JDBC_URL_WITH_DATABASE_POSTGRESQL = "jdbc:%s://%s:%s/%s";
    public static final String CREATE_DATABASE_SQL_FORMAT_MYSQL = "CREATE DATABASE IF NOT EXISTS %s";
    public static final String DROP_DATABASE_SQL_FORMAT_MYSQL = "DROP DATABASE IF EXISTS %s";
    public static final String CREATE_DATABASE_SQL_FORMAT_MSSQL =
            "IF NOT EXISTS(SELECT name FROM sys.databases WHERE name = '%1$s') CREATE DATABASE %1$s;";
    public static final String CREATE_DATABASE_SQL_FORMAT_POSTGRESQL = "CREATE DATABASE %s;";
    public static final String RESULT_IS_BALLERINA_ERROR = "%s is error";
    public static final String RESULT = "result";
    public static final String DB_CLIENT = "dbClient";
    public static final String RETURN_ERROR = "return <persist:Error>error(%s.message());";
    public static final String RETURN_RESULT = "return result;";
    public static final String RETURN_NIL = "return ();";
    public static final String ADD_CLIENT = "self.dbClient = dbClient;";
    public static final String AUTOGENERATED_FILE_COMMENT = "// AUTO-GENERATED FILE. DO NOT MODIFY.";

    public static final String AUTO_GENERATED_COMMENT =
            "// This file is an auto-generated file by Ballerina persistence layer.";

    public static final String AUTO_GENERATED_COMMENT_WITH_REASON =
            "// This file is an auto-generated file by Ballerina persistence layer for %s.";

    public static final String COMMENT_SHOULD_NOT_BE_MODIFIED = "// It should not be modified by hand.";
    public static final String ERROR_MSG = "ERROR: failed to generate/update source file/s for the %s. %s" +
            System.lineSeparator();

    public static final String MYSQL_DRIVER = "mysql.driver";
    public static final String MSSQL_DRIVER = "mssql.driver";
    public static final String POSTGRESQL_DRIVER = "postgresql.driver";
    public static final String GOOGLE_API_SHEET = "googleapis.sheets";
    public static final String HTTP = "http";
    public static final String GOOGLE_SHEETS = "googlesheets";
    public static final String SQL = "sql";
    public static final String PERSIST_IN_MEMORY = "inmemory";
    public static final String BAL_EXTENSION = ".bal";
    public static final String INIT_DB_CLIENT = "private final %s:Client dbClient;";
    public static final String GOOGLE_SHEET_CLIENT = "private final sheets:Client googleSheetClient;";
    public static final String HTTP_CLIENT = "private final http:Client httpClient;";
    public static final String INIT_GOOGLE_SHEET_CLIENT_MAP = "private final map<googlesheets:GoogleSheetsClient> " +
            "persistClients;";
    public static final String INIT_SQL_CLIENT_MAP = "private final map<psql:SQLClient> persistClients;";
    public static final String INIT_IN_MEMORY_CLIENT_MAP = "private final map<inmemory:InMemoryClient> persistClients;";
    public static final String METADATA_RECORD_ENTITY_NAME_TEMPLATE = "entityName: \"%s\", " + System.lineSeparator();
    public static final String TABLE_NAME_TEMPLATE = "tableName: \"%s\", " + System.lineSeparator();
    public static final String METADATA_RECORD_TABLE_NAME_TEMPLATE = "tableName: \"%s\", " + System.lineSeparator();
    public static final String METADATA_RECORD_FIELD_TEMPLATE = "%s: {columnName: \"%s\"}";
    public static final String METADATA_KEY_FIELDS_TEMPLATE = "keyFields: [%s], " + System.lineSeparator();
    public static final String G_SHEET_FIELD_METADATA_TEMPLATE = "%s: {columnName: \"%s\", columnId: \"%s\"}";
    public static final String FIELD_TYPE = "%s: \"%s\"";
    public static final String DATA_TYPE = "dataTypes: {%s},";
    public static final String RANGE_TEMPLATE = "range: \"A:%s\", " + System.lineSeparator();
    public static final String G_SHEET_METADATA_QUERY_TEMPLATE = "query: self.query%s, " + System.lineSeparator();
    public static final String G_SHEET_METADATA_QUERY_ONE_TEMPLATE = "queryOne: self.queryOne%s,";
    public static final String G_SHEET_METADATA_ASSOCIATIONS_METHODS_TEMPLATE = "%s: self.query%s";
    public static final String METADATA_QUERY_TEMPLATE = "query: query%s, " + System.lineSeparator();
    public static final String METADATA_QUERY_ONE_TEMPLATE = "queryOne: queryOne%s,";
    public static final String METADATA_ASSOCIATIONS_METHODS_TEMPLATE = "%s: query%s";
    public static final String QUERY_RETURN = "stream<record{}, persist:Error?>";
    public static final String G_SHEET_QUERY_STATEMENT = "%s from record{} 'object in %s%s";
    public static final String G_SHEET_WHERE_CLAUSE = System.lineSeparator() +
            "where persist:getKey('object, [%s]) == key";
    public static final String QUERY_STATEMENT = "return from record{} 'object in %sClonedTable" +
            System.lineSeparator();
    public static final String QUERY_ONE_RETURN_GSHEET = "record {}|persist:Error";
    public static final String QUERY_ONE_RETURN_IN_MEMORY = "record {}|persist:NotFoundError";
    public static final String QUERY_ONE_RETURN_STATEMENT = "return persist:getNotFoundError(\"%s\", key);";
    public static final String G_SHEET_QUERY_OUTER_JOIN = "    outer join var %s in %s%s";
    public static final String STREAM_PARAM_INIT = "stream<%s, persist:Error?> %sStream = self.query%sStream();";
    public static final String QUERY_ONE_FROM_STATEMENT = "from record{} 'object in %sClonedTable";
    public static final String QUERY_ONE_WHERE_CLAUSE = "    where persist:getKey('object, [%s]) == key" +
            System.lineSeparator();
    public static final String QUERY_OUTER_JOIN = "    outer join var %s in %sClonedTable";
    public static final String ON = " on ";
    public static final String SELECT_QUERY = "select persist:filterRecord({" +
            System.lineSeparator() +
            "      ...'object" +
            "      %s" + System.lineSeparator() +
            "   }, fields);";
    public static final String IF_STATEMENT = "if unionResult is error {" + System.lineSeparator() +
            "            return error persist:Error(unionResult.message());" + System.lineSeparator() +
            "        }" + System.lineSeparator();

    public static final String DO_QUERY = "do {" + System.lineSeparator() +
            "        return {" +
            "           ...'object" +
            "           %s" + System.lineSeparator() +
            "        };" + System.lineSeparator() +
            "    };";
    public static final String G_SHEET_RETURN_STATEMENT_FOR_RELATIONAL_ENTITY = "return from record{} 'object in %s%s" +
            System.lineSeparator() +
            "            where %s" + System.lineSeparator() +
            "            select persist:filterRecord({" + System.lineSeparator() +
            "                ...'object" + System.lineSeparator() +
            "            }, fields);";
    public static final String RETURN_STATEMENT_FOR_RELATIONAL_ENTITY =
            "return from record{} 'object in %sClonedTable" +
                    System.lineSeparator() +
                    "            where %s" + System.lineSeparator() +
                    "            select persist:filterRecord({" + System.lineSeparator() +
                    "                ...'object" + System.lineSeparator() +
                    "            }, fields);";
    public static final String OBJECT_FIELD = "'object.%s";
    public static final String VALUES = "%s?.%s";
    public static final String EQUALS = " equals ";
    public static final String AND = " && ";
    public static final String COMMA = ",";
    public static final String CONDITION_STATEMENT = "'object.%s == value[\"%s\"] ";
    public static final String VARIABLE = "\"%s\": %s,";
    public static final String ASSOCIATED_FIELD_TEMPLATE = ".%s\": {relation: {entityName: \"%s\", refField: \"%s\"}}";
    public static final String CONSTRAINT_ANNOTATION = "@constraint:String {" + System.lineSeparator() +
            "        %s" + System.lineSeparator() +
            "    }";

    public static final String SQL_DB_MAPPING_ANNOTATION_NAME = "sql:Mapping";
    public static final String SQL_VARCHAR_MAPPING_ANNOTATION_NAME = "sql:VarChar";
    public static final String SQL_CHAR_MAPPING_ANNOTATION_NAME = "sql:Char";
    public static final String SQL_DECIMAL_MAPPING_ANNOTATION_NAME = "sql:Decimal";
    public static final String SQL_RELATION_MAPPING_ANNOTATION_NAME = "sql:Relation";
    public static final String SQL_INDEX_MAPPING_ANNOTATION_NAME = "sql:Index";
    public static final String SQL_UNIQUE_INDEX_MAPPING_ANNOTATION_NAME = "sql:UniqueIndex";
    public static final String SQL_GENERATED_ANNOTATION_NAME = "sql:Generated";
    public static final String SQL_DB_MAPPING_ANNOTATION =
            String.format("@%s { name: \"%s\" }", SQL_DB_MAPPING_ANNOTATION_NAME, "%s");
    public static final String SQL_VARCHAR_MAPPING_ANNOTATION =
            String.format("@%s { length: %s }", SQL_VARCHAR_MAPPING_ANNOTATION_NAME, "%s");
    public static final String SQL_CHAR_MAPPING_ANNOTATION =
            String.format("@%s{ length: %s }", SQL_CHAR_MAPPING_ANNOTATION_NAME, "%s");
    public static final String SQL_DECIMAL_MAPPING_ANNOTATION =
            String.format("@%s{ precision: [%s,%s] }", SQL_DECIMAL_MAPPING_ANNOTATION_NAME, "%s", "%s");
    public static final String SQL_RELATION_MAPPING_ANNOTATION =
            String.format("@%s{ refs: %s }", SQL_RELATION_MAPPING_ANNOTATION_NAME, "%s");
    public static final String SQL_INDEX_MAPPING_ANNOTATION =
            String.format("@%s { names: %s }", SQL_INDEX_MAPPING_ANNOTATION_NAME, "%s");
    public static final String SQL_UNIQUE_INDEX_MAPPING_ANNOTATION =
            String.format("@%s { names: %s }", SQL_UNIQUE_INDEX_MAPPING_ANNOTATION_NAME, "%s");
    public static final String SQL_GENERATED_ANNOTATION = String.format("@%s", SQL_GENERATED_ANNOTATION_NAME);


    public static final String FIELD_METADATA_TEMPLATE = "fieldMetadata: {%s}";
    public static final String JOIN_METADATA_TEMPLATE = "joinMetadata: {%s}";

    public static final String JOIN_METADATA_FIELD_TEMPLATE =
            "%s: {entity: %s, fieldName: \"%s\", refTable: \"%s\", refColumns: [%s], joinColumns: [%s], 'type: %s}";

    public static final String COLUMN_ARRAY_ENTRY_TEMPLATE = "\"%s\"";
    public static final String METADATA_RECORD_KEY_FIELD_TEMPLATE = "keyFields: [%s]";
    public static final String METADATA_RECORD_ELEMENT_TEMPLATE = "[%s]: {%s}";
    public static final String METADATA_RECORD_TEMPLATE =
            "private final record {|psql:SQLMetadata...;|} & readonly metadata = {%s};";
    public static final String SHEET_METADATA_RECORD_TEMPLATE =
            "final record {|googlesheets:SheetMetadata...;|} & readonly metadata = {%s};";
    public static final String SHEET_CLIENT_CONFIG_TEMPLATE = " sheets:ConnectionConfig sheetsClientConfig = {" +
            System.lineSeparator() + "            auth: {" +
            System.lineSeparator() + "                clientId: clientId," +
            System.lineSeparator() + "                clientSecret: clientSecret," +
            System.lineSeparator() + "                refreshUrl: sheets:REFRESH_URL," +
            System.lineSeparator() + "                refreshToken: refreshToken" +
            System.lineSeparator() + "            }" +
            System.lineSeparator() + "        };";
    public static final String HTTP_CLIENT_CONFIG_TEMPLATE = "http:ClientConfiguration httpClientConfiguration = {" +
            System.lineSeparator() + "            auth: {" +
            System.lineSeparator() + "                clientId: clientId," +
            System.lineSeparator() + "                clientSecret: clientSecret," +
            System.lineSeparator() + "                refreshUrl: sheets:REFRESH_URL," +
            System.lineSeparator() + "                refreshToken: refreshToken" +
            System.lineSeparator() + "            }" +
            System.lineSeparator() + "        };";
    public static final String HTTP_CLIENT_INIT_TEMPLATE = "http:Client|error httpClient = new (string " + 
            "`https://sheets.googleapis.com/v4/spreadsheets/${spreadsheetId}/values`, httpClientConfiguration);" 
            + System.lineSeparator();
    public static final String SHEET_CLIENT_INIT_TEMPLATE =
            "sheets:Client|error googleSheetClient = new (sheetsClientConfig);" + System.lineSeparator();
    public static final String SELF_HTTP_CLIENT_INIT_TEMPLATE =
            "self.googleSheetClient = googleSheetClient;" + System.lineSeparator();
    public static final String SELF_SHEET_CLIENT_INIT_TEMPLATE =
            "self.httpClient = httpClient;" + System.lineSeparator();
    public static final String SHEET_IDS_TEMPLATE =
            "map<int> sheetIds = check googlesheets:getSheetIds(self.googleSheetClient, metadata, spreadsheetId);" +
                    System.lineSeparator();
    public static final String IN_MEMORY_METADATA_MAP_TEMPLATE =
            "final map<inmemory:TableMetadata> metadata = {%s};";
    public static final String IN_MEMORY_ASSOC_METHODS_TEMPLATE = "associationsMethods: {%s}";
    public static final String INIT_DB_CLIENT_WITH_PARAMS = "%s:Client|error dbClient = new (host = host, " +
            "user = user, password = password, database = database, port = port, options = connectionOptions);" +
            System.lineSeparator();

    public static final String POSTGRESQL_INIT_DB_CLIENT_WITH_PARAMS = "%s:Client|error dbClient = new (host = host, " +
            "username = user, password = password, database = database, port = port, options = connectionOptions);" +
            System.lineSeparator();
    public static final String GOOGLE_SHEET_CLIENT_MAP = "[%s]: check new (self.googleSheetClient, self.httpClient, " +
            "metadata.get(%s).cloneReadOnly(), spreadsheetId.cloneReadOnly(), sheetIds.get(%s).cloneReadOnly())";
    public static final String TABLE_PARAMETER_INIT_TEMPLATE = "final isolated table<%s> key(%s) %sTable = table[];";
    public static final String CLONED_TABLE_INIT_TEMPLATE = "table<%s> key(%s) %sClonedTable;";
    public static final String CLONED_TABLE_DECLARATION_TEMPLATE = "%sClonedTable = %sTable.clone();";
    public static final String PERSIST_CLIENT_MAP_ELEMENT =
            "[%s]: check new (dbClient, self.metadata.get(%s), %s)";
    public static final String PERSIST_IN_MEMORY_CLIENT_MAP_ELEMENT =
            "[%s]: check new (metadata.get(%s).cloneReadOnly())";
    public static final String PERSIST_CLIENT_TEMPLATE = "self.persistClients = {%s};";
    public static final String LOCK_TEMPLATE = "lock {%s}";
    public static final String LOCK = "lock";
    public static final String NEWLINE = System.lineSeparator();
    public static final String PERSIST_CLIENT_CLOSE_STATEMENT = "error? result = self.dbClient.close();";
    public static final String REGEX_FOR_SPLIT_BY_CAPITOL_LETTER = "(?=\\p{Upper})";
    public static final String OPEN_BRACE = "{";
    public static final String CLOSE_BRACE = "}";
    public static final String OPEN_BRACKET = "[";
    public static final String CLOSE_BRACKET = "]";
    public static final String ARRAY = "[]";
    public static final String QUESTION_MARK = "?";
    public static final String COMMA_WITH_NEWLINE = "," + System.lineSeparator();
    public static final String ONE_TO_ONE = "psql:ONE_TO_ONE";
    public static final String ONE_TO_MANY = "psql:ONE_TO_MANY";
    public static final String MANY_TO_ONE = "psql:MANY_TO_ONE";
    public static final String MANY_TO_MANY = "psql:MANY_TO_MANY";

    public static final String EXTERNAL_GET_BY_KEY_METHOD_TEMPLATE = "isolated resource function get %s/%s(" +
            "%sTargetType targetType = <>) returns targetType|persist:Error = @java:Method {"
            + System.lineSeparator()
            + "'class: \"io.ballerina.stdlib.persist.%s.datastore.%s\"," + System.lineSeparator() +
             " name: \"queryOne\"} external;";

    public static final String EXTERNAL_GET_METHOD_TEMPLATE = "isolated resource function get %s(" +
            "%sTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {"
            + System.lineSeparator()
            + "'class: \"io.ballerina.stdlib.persist.%s.datastore.%s\"," + System.lineSeparator() +
            " name: \"query\"} external;";
    public static final String EXTERNAL_SQL_GET_METHOD_TEMPLATE = "isolated resource function get %s(" +
            "%sTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, " +
            "sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, " +
            "sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {"
            + System.lineSeparator()
            + "'class: \"io.ballerina.stdlib.persist.%s.datastore.%s\"," + System.lineSeparator() +
            " name: \"query\"} external;";
    public static final String EXTERNAL_QUERY_STREAM_METHOD_TEMPLATE =
            "private isolated function query%sStream(%sTargetType targetType = <>) returns " +
                    "stream<targetType, persist:Error?> = @java:Method {" + System.lineSeparator() +
            "        'class: \"io.ballerina.stdlib.persist.%s.datastore.GoogleSheetsProcessor\"," +
                    System.lineSeparator() +
            "        name: \"queryStream\"" + System.lineSeparator() +
            "    } external;";

    public static final String QUERY_NATIVE_SQL_METHOD_TEMPLATE =
            "remote isolated function " +
                    "queryNativeSQL(sql:ParameterizedQuery sqlQuery, typedesc<record {}> rowType = <>) " +
                    "returns stream<rowType, persist:Error?> = @java:Method {" + System.lineSeparator() +
                    "        'class: \"io.ballerina.stdlib.persist.sql.datastore.%s\""
                    + System.lineSeparator() +
                    "    } external;";

    public static final String EXECUTE_NATIVE_SQL_METHOD_TEMPLATE =
            "remote isolated function executeNativeSQL(sql:ParameterizedQuery sqlQuery) " +
                    "returns psql:ExecutionResult|persist:Error = @java:Method {" + System.lineSeparator() +
                    "        'class: \"io.ballerina.stdlib.persist.sql.datastore.%s\"" + System.lineSeparator() +
                    "    } external;\n";

    public static final String CONSTRAINT_STRING = "constraint:String";
    public static final String CONSTRAINT = "constraint";
    public static final String LENGTH = "length";
    public static final String MAX_LENGTH = "maxLength";
    public static final String VARCHAR_LENGTH = "191";
    public static final String EXPERIMENTAL_NOTICE = System.lineSeparator() + "WARNING %s" + System.lineSeparator();

    public static final String MYSQL_SPECIFICS = "psql:MYSQL_SPECIFICS";
    public static final String MSSQL_SPECIFICS = "psql:MSSQL_SPECIFICS";
    public static final String POSTGRESQL_SPECIFICS = "psql:POSTGRESQL_SPECIFICS";

    public static final String MYSQL_PROCESSOR = "MySQLProcessor";
    public static final String MSSQL_PROCESSOR = "MSSQLProcessor";
    public static final String POSTGRESQL_PROCESSOR = "PostgreSQLProcessor";
}

