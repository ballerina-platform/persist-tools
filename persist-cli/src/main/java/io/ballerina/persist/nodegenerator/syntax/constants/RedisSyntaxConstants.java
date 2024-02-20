package io.ballerina.persist.nodegenerator.syntax.constants;

public class RedisSyntaxConstants {
    public static final String REDIS_PROCESSOR = "RedisProcessor";
    public static final String INIT_DB_CLIENT_WITH_PARAMS = "%s:Client|error dbClient = new (config = { " +
        "host: string `${host}:${port.toString()}`, password = password, options = connectionOptions);" +
        System.lineSeparator();
    public static final String INIT_REDIS_CLIENT_MAP = "private final map<predis:RedisClient> persistClients;";
    public static final String EXTERNAL_REDIS_GET_METHOD_TEMPLATE = "isolated resource function get %s(" +
        "%sTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {"
        + System.lineSeparator()
        + "'class: \"io.ballerina.stdlib.persist.%s.datastore.%s\"," + System.lineSeparator() +
        " name: \"query\"} external;";
    public static final String PERSIST_CLIENT_CLOSE_STATEMENT = "error? result = self.dbClient.stop();";
    public static final String REDIS = "redis";
    public static final String PERSIST_CLIENT_MAP_ELEMENT =
            "[%s]: check new (dbClient, self.metadata.get(%s))";
    public static final String REDIS_CLIENT_DECLARATION = "predis:RedisClient redisClient;";
    public static final String GET_PERSIST_CLIENT = "redisClient = self.persistClients.get(%s);";
    public static final String CREATE_REDIS_RESULTS = "_ = check redisClient.runBatchInsertQuery(data);";
    public static final String UPDATE_RUN_UPDATE_QUERY = "_ = check redisClient.runUpdateQuery(%s, value);";
    public static final String DELETE_RUN_DELETE_QUERY = "_ = check redisClient.runDeleteQuery(%s);";

    public static final String METADATA_RECORD_COLLECTION_NAME_TEMPLATE = "collectionName: \"%s\", " + System.lineSeparator();
    public static final String METADATA_RECORD_FIELD_TEMPLATE = "%s: {fieldName: \"%s\", fieldDataType: predis:%s}";
    public static final String REFERENCE_METADATA_TEMPLATE = "refMetadata: {%s}";
    public static final String METADATA_RECORD_TEMPLATE =
            "private final record {|predis:RedisMetadata...;|} & readonly metadata = {%s};";
    public static final String ASSOCIATED_FIELD_TEMPLATE = ".%s\": {relation: {entityName: \"%s\", refField: \"%s\", refFieldDataType: predis:%s}}";
    public static final String JOIN_METADATA_FIELD_TEMPLATE =
            "%s: {entity: %s, fieldName: \"%s\", refCollection: \"%s\", refFields: [%s], joinFields: [%s], 'type: %s}";
    public static final String ONE_TO_ONE = "predis:ONE_TO_ONE";
    public static final String ONE_TO_MANY = "predis:ONE_TO_MANY";
    public static final String MANY_TO_ONE = "predis:MANY_TO_ONE";
    public static final String MANY_TO_MANY = "predis:MANY_TO_MANY";
}
