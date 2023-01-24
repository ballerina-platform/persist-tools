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
package io.ballerina.persist.nodegenerator;

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

    public static final String PERSIST_CLIENTS = "persistClients";
    public static final String RETURN_RECORD_VARIABLE = "return {%s};";
    public static final String RETURN_VARIABLE = "return %s;";
    public static final String RETURN_TRUE = "return true;";
    public static final String RETURN_FALSE = "return false;";
    public static final String CLOSE_RECORD_VARIABLE = "record {|%s|}";
    public static final String DOUBLE_QUOTE = "\"";
    public static final String BACK_SLASH = "/";
    public static final String EMPTY_STRING = "";
    public static final String SPACE = " ";
    public static final String COMMA_SPACE = ", ";
    public static final String COLON = ":";
    public static final String SEMICOLON = ";";
    public static final String VALUE = "value";
    public static final String RETURN_NEXT_RECORD = "return nextRecord;";
    public static final String PERSIST_MODULE = "persist";
    public static final String PERSIST_ERROR = "persist:Error";
    public static final String CREATE_SQL_RESULTS = "_ = check " +
            "self.persistClients.get(\"%s\").runBatchInsertQuery(data);";
    public static final String RETURN_CREATED_KEY = "return from  %s inserted in data\n";
    public static final String SELECT_WITH_SPACE = "\t\t\tselect ";
    public static final String READ_BY_KEY_RETURN = "return (check " +
            "self.persistClients.get(\"%s\").runReadByKeyQuery(%s, %s)).cloneWithType(%s);";
    public static final String READ_RUN_READ_QUERY = "stream<anydata, sql:Error?>|persist:Error result" +
            " = self.persistClients.get(\"%s\").runReadQuery(%s);";
    public static final String READ_RETURN_STREAM_WHEN_ERROR = "return new stream<%s, persist:Error?>" +
            "(new %sStream((), result));";
    public static final String READ_RETURN_STREAM_WHEN_NOT_ERROR = "return new stream<%s, persist:Error?>" +
            "(new %sStream(result));";
    public static final String UPDATE_RUN_UPDATE_QUERY = "_ = check self.persistClients.get(\"%s\").runUpdateQuery" +
            "(%s, value);";
    public static final String UPDATE_RETURN_UPDATE_QUERY = "return self->%s.get();";
    public static final String DELETE_RUN_DELETE_QUERY = "_ = check self.persistClients.get(\"%s\")." +
            "runDeleteQuery(%s);";
    public static final String RETURN_DELETED_OBJECT = "return 'object;";
    public static final String GET_OBJECT_QUERY = "%s 'object = check self->%s.get();";
    public static final String INIT_STREAM_STATEMENT = "self.anydataStream = anydataStream;";
    public static final String NEXT_STREAM_RETURN_TYPE = "record {|%s value;|}|persist:Error?";

    public static final String NEXT_STREAM_IF_STATEMENT = "streamValue is ()";
    public static final String NEXT_STREAM_ELSE_IF_STATEMENT = "(streamValue is sql:Error)";
    public static final String NEXT_STREAM_RETURN_STREAM_VALUE = "return streamValue;";
    public static final String NEXT_STREAM_RETURN_STREAM_VALUE_ERROR =
            "return <persist:Error>error(streamValue.message());";
    public static final String NEXT_STREAM_ELSE_STATEMENT = "record {|%s value;|} nextRecord = " +
            "{value: check streamValue.value.cloneWithType(%s)};";
    public static final String RELATION_ENUM_ARRAY_CHECK = "self.include is %sRelations[]";
    public static final String CLOSE_STREAM_STATEMENT = "sql:Error? e = anydataStream.close();";

    public static final String CONFIGURABLE_PORT = "configurable int port = ?;";
    public static final String CONFIGURABLE_HOST = "configurable string host = ?;";
    public static final String CONFIGURABLE_USER = "configurable string user = ?;";
    public static final String CONFIGURABLE_PASSWORD = "configurable string password = ?;";
    public static final String CONFIGURABLE_DATABASE = "configurable string database = ?;";
    public static final String INIT = "init";
    public static final String GET = "get";
    public static final String POST = "post";
    public static final String DELETE = "delete";
    public static final String PUT = "put";
    public static final String CLOSE = "close";
    public static final String NEXT = "next";

    public static final String SPECIFIC_ERROR = "Error";
    public static final String RETURN_NILL = "return ();";
    public static final String KEYWORD_BALLERINA = "ballerina";
    public static final String KEYWORD_VALUE = "data";
    public static final String KEYWORD_STREAM = "Stream";
    public static final String KEYWORD_ERR = "err";
    public static final String KEYWORD_SQL = "sql";
    public static final String KEYWORD_BALLERINAX = "ballerinax";
    public static final String KEYWORD_CLIENT = "client";
    public static final String KEYWORD_PUBLIC = "public";
    public static final String KEYWORD_ISOLATED = "isolated";
    public static final String KEYWORD_RESOURCE = "resource";

    public static final String GENERATED_SOURCE_DIRECTORY = "generated";
    public static final String PATH_CONFIGURATION_BAL_FILE = "database_configuration.bal";
    public static final String KEYWORD_MYSQL = "mysql";
    public static final String KEYWORD_TIME_PREFIX = "time";
    public static final String KEYWORD_ENTITY = "Entity";
    public static final String JDBC_URL_WITHOUT_DATABASE = "jdbc:%s://%s:%s";
    public static final String JDBC_URL_WITH_DATABASE = "jdbc:%s://%s:%s/%s";
    public static final String ANYDATA_STREAM_STATEMENT = "private stream<anydata, sql:Error?>? anydataStream;";
    public static final String NULLABLE_ERROR_STATEMENT = "private persist:Error? err;";
    public static final String SELF_ERR = "self.err = err;";

    public static final String ANYDATA_KEYWORD = "anydataStream";

    public static final String ERR_IS_ERROR = "self.err is persist:Error";
    public static final String RETURN_CASTED_ERROR = "return <persist:Error>self.err;";
    public static final String ANYDATASTREAM_IS_STREAM_TYPE = "self.anydataStream is stream<anydata, sql:Error?>";
    public static final String CAST_ANYDATA_STREAM = "var anydataStream = <stream<anydata, " +
            "sql:Error?>>self.anydataStream;";
    public static final String ANYDATA_STREAM_NEXT = "var streamValue = anydataStream.next();";
    public static final String RESULT_IS_ERROR = "result is persist:Error";
    public static final String NULLABLE_ANYDATA_STREAM_TYPE = "stream<anydata, sql:Error?>?";
    public static final String IS_SQL_ERROR = "e is sql:Error";
    public static final String RETURN_PERSIST_ERROR_CLOSE_STREAM = "return <persist:Error>error(e.message());";
    public static final String AUTOGENERATED_FILE_COMMENT = "// AUTO-GENERATED FILE. DO NOT MODIFY.";

    public static final String AUTO_GENERATED_COMMENT =
            "// This file is an auto-generated file by Ballerina persistence layer.";

    public static final String AUTO_GENERATED_COMMENT_WITH_REASON =
            "// This file is an auto-generated file by Ballerina persistence layer for %s.";

    public static final String COMMENT_SHOULD_NOT_BE_MODIFIED = "// It should not be modified by hand.";


    public static final String MYSQL_DRIVER = "mysql.driver";
    public static final String BAL_EXTENTION = ".bal";
    public static final String INIT_DB_CLIENT = "private final mysql:Client dbClient;";
    public static final String INIT_PERSIST_CLIENT_MAP = "private final map<persist:SQLClient> persistClients;";
    public static final String METADATAMAP_ENTITY_NAME_TEMPLATE = "entityName: \"%s\",";
    public static final String METADATAMAP_TABLE_NAME_TEMPLATE = "tableName: '%s',";
    public static final String METADATAMAP_FIELD_TEMPLATE = "%s: {columnName: \"%s\", 'type: %s}";
    public static final String METADATAMAP_KEY_FIELD_TEMPLATE = "keyFields: [%s]";
    public static final String METADATAMAP_ELEMENT_TEMPLATE = "%s: {%s}";
    public static final String METADATAMAP_TEMPLATE = "private final map<persist:Metadata> metadata = {%s};";
    public static final String INIT_DBCLIENT = "self.dbClient = check new (host = host, user = user, " +
            "password = password, database = database, port = port);\n";
    public static final String PERSIST_CLIENT_MAP_ELEMENT = "%s: check new (self.dbClient," +
            "self.metadata.get(\"%s\").entityName," +
            "self.metadata.get(\"%s\").tableName," +
            "self.metadata.get(\"%s\").keyFields," +
            "self.metadata.get(\"%s\").fieldMetadata)";
    public static final String PERSIST_CLIENT_TEMPLATE = "self.persistClients = {%s};";
    public static final String PERSIST_CLIENT_CLOSE_STATEMENT = "sql:Error? e = self.dbClient.close();";
    public static final String CHECK_E_FOR_ERRORS = "e is sql:Error";
    public static final String RETURN_PERSIST_ERROR = "return <persist:Error>error(e.message());";
    public static final String PLACEHOLDER_FOR_MAP_FIELD = "%s:%s";
    public static final String PLACEHOLDER_FOR_TYPE_DEFINITION = "%s %s";

}

