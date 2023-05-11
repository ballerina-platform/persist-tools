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
    public static final String COMMA_SPACE = ", ";
    public static final String COLON = ":";
    public static final String SEMICOLON = ";";
    public static final String EQUAL = "=";
    public static final String VALUE = "value";
    public static final String PERSIST_MODULE = "persist";
    public static final String PERSIST_ERROR = "persist:Error";
    public static final String CREATE_SQL_RESULTS = "_ = check " +
            "self.persistClients.get(%s).runBatchInsertQuery(data);";
    public static final String CREATE_ARRAY_VAR = "%s keys = [];";
    public static final String POST_RETURN = "return keys;";
    public static final String HAS_KEY = "\tif self.%s.hasKey(%s) {";
    public static final String VARIABLE_TYPE = "%s[]";
    public static final String FIELD = "value.%s";
    public static final String FOREACH_STMT_START = "foreach %s value in data {" + System.lineSeparator();
    public static final String HAS_NOT_KEY = "!self.%s.hasKey(%s)";
    public static final String UPDATE_RECORD_FIELD_VALUE = "foreach var [k, v] in value.entries() {" +
            System.lineSeparator() + "        %s[k] = v;" + System.lineSeparator() +
            "    }" + System.lineSeparator();

    public static final String HAS_KEY_ERROR = "\t\treturn <persist:DuplicateKeyError>error(\"Duplicate key: \" + " +
            "%s.toString());" + System.lineSeparator() + "\t}" + System.lineSeparator();
    public static final String HAS_NOT_KEY_ERROR = "return <persist:InvalidKeyError>error(\"Not found: \" + " +
            "%s.toString());";
    public static final String PUSH_VALUES = System.lineSeparator() + "\tkeys.push(%s);" + System.lineSeparator();
    public static final String GET_UPDATE_RECORD = "%s %s = self.%s.get(%s);" + System.lineSeparator();
    public static final String PUT_VALUE_TO_MAP = "self.%s.put(%s);";
    public static final String RETURN_STATEMENT = "return %s;";

    public static final String RETURN_CREATED_KEY = "return from  %s inserted in data" + System.lineSeparator();
    public static final String SELECT_WITH_SPACE = "\t\t\tselect ";
    public static final String UPDATE_RUN_UPDATE_QUERY = "_ = check self.persistClients.get(%s).runUpdateQuery" +
            "(%s, value);";
    public static final String UPDATE_RETURN_UPDATE_QUERY = "return self->%s.get();";
    public static final String DELETE_RUN_DELETE_QUERY = "_ = check self.persistClients.get(%s)." +
            "runDeleteQuery(%s);";
    public static final String RETURN_DELETED_OBJECT = "return result;";
    public static final String DELETED_OBJECT = "return self.%s.remove(%s);";
    public static final String GET_OBJECT_QUERY = "%s result = check self->%s.get();";

    public static final String CONFIGURABLE_PORT = "configurable int port = ?;";
    public static final String CONFIGURABLE_HOST = "configurable string host = ?;";
    public static final String CONFIGURABLE_USER = "configurable string user = ?;";
    public static final String CONFIGURABLE_PASSWORD = "configurable string password = ?;";
    public static final String CONFIGURABLE_DATABASE = "configurable string database = ?;";
    public static final String CONFIGURABLE_OPTIONS = "configurable mysql:Options connectionOptions = {};";
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
    public static final String KEYWORD_PUBLIC = "public";
    public static final String KEYWORD_PRIVATE = "private";
    public static final String KEYWORD_ISOLATED = "isolated";
    public static final String KEYWORD_RESOURCE = "resource";

    public static final String GENERATED_SOURCE_DIRECTORY = "generated";
    public static final String PATH_CONFIGURATION_BAL_FILE = "persist_db_config.bal";
    public static final String PERSIST_DEPENDENCY = "platform.java11.dependency";
    public static final String KEYWORD_JBALLERINA_JAVA_PREFIX = "jballerina.java";
    public static final String KEYWORD_READONLY = "readonly";
    public static final String JDBC_URL_WITHOUT_DATABASE = "jdbc:%s://%s:%s";
    public static final String JDBC_URL_WITH_DATABASE = "jdbc:%s://%s:%s/%s";
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


    public static final String MYSQL_DRIVER = "mysql.driver";
    public static final String BAL_EXTENTION = ".bal";
    public static final String INIT_DB_CLIENT = "private final mysql:Client dbClient;";
    public static final String INIT_DB_CLIENT_MAP = "private final map<persist:SQLClient> persistClients;";
    public static final String INIT_IN_MEMORY_CLIENT = "private final map<persist:InMemoryClient> persistClients;";
    public static final String METADATA_RECORD_ENTITY_NAME_TEMPLATE = "entityName: \"%s\", " + System.lineSeparator();
    public static final String METADATA_RECORD_TABLE_NAME_TEMPLATE = "tableName: `%s`, " + System.lineSeparator();
    public static final String METADATA_RECORD_FIELD_TEMPLATE = "%s: {columnName: \"%s\"}";
    public static final String METADATA_KEY_FIELDS_TEMPLATE = "keyFields: [%s], " + System.lineSeparator();
    public static final String METADATA_QUERY_TEMPLATE = "query: self.query%s, " + System.lineSeparator();
    public static final String METADATA_QUERY_ONE_TEMPLATE = "queryOne: self.queryOne%s,";
    public static final String METADATA_ASSOCIATIONS_METHODS_TEMPLATE = "%s: self.query%s";
    public static final String QUERY_RETURN = "stream<record{}, persist:Error?>";
    public static final String QUERY_STATEMENT = "return from record{} 'object in self.%s";
    public static final String QUERY_ONE_RETURN = "record {}|persist:InvalidKeyError";
    public static final String QUERY_ONE_RETURN_STATEMENT = "return <persist:InvalidKeyError>error(" +
            "\"Invalid key: \" + key.toString());";
    public static final String QUERY_ONE_FROM_STATEMENT = "from record{} 'object in self.%s";
    public static final String QUERY_ONE_WHERE_CLAUSE = "    where self.persistClients.get(%s).getKey('object) == key";
    public static final String QUERY_OUTER_JOIN = "    outer join var %s in self.%s";
    public static final String ON = " on ";
    public static final String SELECT_QUERY = System.lineSeparator() + "select persist:filterRecord({" +
            System.lineSeparator() +
            "      ...'object" +
            "      %s" + System.lineSeparator() +
            "   }, fields);";

    public static final String DO_QUERY = System.lineSeparator() + "do {" + System.lineSeparator() +
            "        return {" +
            "           ...'object" +
            "           %s" + System.lineSeparator() +
            "        };" + System.lineSeparator() +
            "    };";
    public static final String RETURN_STATEMENT_FOR_RELATIONAL_ENTITY = "return from record{} 'object in self.%s" +
            System.lineSeparator() +
            "            where %s" + System.lineSeparator() +
            "            select persist:filterRecord({" + System.lineSeparator() +
            "                ...'object" + System.lineSeparator() +
            "            }, fields);";
    public static final String OBJECT_FIELD = "'object.%s";
    public static final String VALUES = "%s?.%s";
    public static final String EQUALS = " equals ";
    public static final String AND = " && ";
    public static final String COMMA = ", ";
    public static final String CONDITION_STATEMENT = "'object.%s == value[\"%s\"] ";
    public static final String VARIABLE = "\"%s\": %s,";
    public static final String ASSOCIATED_FIELD_TEMPLATE = ".%s\": {relation: {entityName: \"%s\", refField: \"%s\"}}";
    public static final String FIELD_METADATA_TEMPLATE = "fieldMetadata: {%s}";
    public static final String JOIN_METADATA_TEMPLATE = "joinMetadata: {%s}";

    public static final String JOIN_METADATA_FIELD_TEMPLATE =
            "%s: {entity: %s, fieldName: \"%s\", refTable: \"%s\", refColumns: [%s], joinColumns: [%s], 'type: %s}";

    public static final String COLUMN_ARRAY_ENTRY_TEMPLATE = "\"%s\"";
    public static final String METADATA_RECORD_KEY_FIELD_TEMPLATE = "keyFields: [%s]";
    public static final String METADATA_RECORD_ELEMENT_TEMPLATE = "[%s]: {%s}";
    public static final String METADATA_RECORD_TEMPLATE =
            "private final record {|persist:SQLMetadata...;|} metadata = {%s};";
    public static final String IN_MEMORY_METADATA_MAP_TEMPLATE =
            "final map<persist:TableMetadata> metadata = {%s};";
    public static final String IN_MEMORY_ASSOC_METHODS_TEMPLATE = "associationsMethods: {%s}";
    public static final String INIT_DB_CLIENT_WITH_PARAMS = "mysql:Client|error dbClient = new (host = host, " +
            "user = user, password = password, database = database, port = port, options = connectionOptions);" +
            System.lineSeparator();
    public static final String TABLE_PARAMETER_INIT_TEMPLATE = "table<%s> key(%s) %s = %s;";
    public static final String PERSIST_CLIENT_MAP_ELEMENT = "[%s]: check new (self.dbClient, self.metadata.get(%s))";
    public static final String PERSIST_IN_MEMORY_CLIENT_MAP_ELEMENT = "[%s]: check new (metadata.get(%s))";
    public static final String PERSIST_CLIENT_TEMPLATE = "self.persistClients = {%s};";
    public static final String PERSIST_CLIENT_CLOSE_STATEMENT = "error? result = self.dbClient.close();";
    public static final String REGEX_FOR_SPLIT_BY_CAPITOL_LETTER = "(?=\\p{Upper})";
    public static final String OPEN_BRACE = "{";
    public static final String CLOSE_BRACE = "}";
    public static final String OPEN_BRACKET = "[";
    public static final String CLOSE_BRACKET = "]";
    public static final String ARRAY = "[]";
    public static final String QUESTION_MARK = "?";
    public static final String COMMA_WITH_NEWLINE = "," + System.lineSeparator();

    public static final String EXTERNAL_GET_BY_KEY_METHOD_TEMPLATE = "isolated resource function get %s/%s(" +
            "%sTargetType targetType = <>) returns targetType|persist:Error = @java:Method {"
            + System.lineSeparator()
            + "'class: \"io.ballerina.stdlib.persist.datastore.%s\"," + System.lineSeparator() +
             " name: \"queryOne\"} external;";

    public static final String EXTERNAL_GET_METHOD_TEMPLATE = "isolated resource function get %s(" +
            "%sTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {"
            + System.lineSeparator()
            + "'class: \"io.ballerina.stdlib.persist.datastore.%s\"," + System.lineSeparator() +
            " name: \"query\"} external;";

}

