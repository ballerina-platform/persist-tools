/*
 * Copyright (c) 2026, WSO2 LLC. (http://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.ballerina.persist.dataservice;

/**
 * The messages that the persist tool prints for a data service.
 */
public final class DataServiceMessages {

    public static final String SUPPORTED_PROTOCOLS = "http";

    public static final String PROTOCOL_REQUIRED =
            "'options.dataservice.protocol' is required. Supported values: " + SUPPORTED_PROTOCOLS + ".";
    public static final String PROTOCOL_NOT_SUPPORTED_YET =
            "the protocol '%s' is not supported yet. Supported values: " + SUPPORTED_PROTOCOLS + ".";
    public static final String PROTOCOL_INVALID =
            "invalid 'options.dataservice.protocol' '%s'. Supported values: " + SUPPORTED_PROTOCOLS + ".";
    public static final String BASE_PATH_NO_LEADING_SLASH =
            "invalid 'options.dataservice.basePath' '%s': the base path must start with '/'.";
    public static final String BASE_PATH_INVALID_SEGMENT = "invalid 'options.dataservice.basePath' '%s': each path " +
            "segment must be non-empty and contain only letters, digits, '_', '-' and '.'.";
    public static final String TARGET_MODULE = "a data service is generated only into the default module '%s'. " +
            "Remove 'targetModule' or set it to '%s'.";
    public static final String WITH_INIT_PARAMS = "'options.withInitParams' cannot be used with a data service. " +
            "Remove it or set it to false.";
    public static final String DATASTORE_NOT_SQL = "the datastore '%s' does not support a data service. Use " +
            "mysql, mssql, postgresql or h2.";
    public static final String DATASERVICE_FLAG_INVALID =
            "invalid '--dataservice' value '%s'. Supported values: " + SUPPORTED_PROTOCOLS + ".";
    public static final String FLAG_WITH_DATASERVICE = "'%s' cannot be used with '--dataservice'. Remove '%s'.";
    public static final String NAMED_MODEL = "a data service is not supported for a named persist model. " +
            "Use 'persist/model.bal' or remove 'options.dataservice'.";
    public static final String NO_EXPOSED_ENTITY = "the data service exposes no entity. Add the " +
            "@dataservice:Expose annotation to the entities to expose: %s.";
    public static final String KEY_NAME_RESERVED = "the key field '%s' of the entity '%s' uses a name reserved by " +
            "the data service. Rename the field, or remove the entity's @dataservice:Expose annotation.";
    public static final String COLLECTION_CLASH = "the entities '%s' and '%s' map to the same collection '%s' in " +
            "the data service. Rename one of them, or remove its @dataservice:Expose annotation.";
    public static final String TYPE_NAME_CLASH = "the entity '%s' has the name of a type that the data service " +
            "generates for the entity '%s'. Rename one of them, or remove the @dataservice:Expose annotation of '%s'.";
    public static final String ENUM_NAME_CLASH = "the enum '%s' has the name of a type that the data service " +
            "generates for the entity '%s'. Rename one of them, or remove the @dataservice:Expose annotation of '%s'.";
    public static final String GENERATED_TYPE_CLASH = "the data service generates the type '%s' for both the " +
            "entity '%s' and the entity '%s'. Rename one of them, or remove its @dataservice:Expose annotation.";
    public static final String MODEL_ENTRIES_WITH_DATASERVICE = "the persist model persist/model.bal has more " +
            "than one [[tool.persist]] entry, and a data service needs one. Remove the other entries, or remove " +
            "'options.dataservice'.";
    public static final String ENTRY_WITH_DATASERVICE = "a data service needs the only [[tool.persist]] entry of " +
            "persist/model.bal, and Ballerina.toml already has one. Change that entry instead of adding another.";
    public static final String UNSUPPORTED_ENTITY = "the entity '%s' has an unsupported data type and cannot be " +
            "exposed. Remove its @dataservice:Expose annotation.";
    public static final String SCOPES_WITHOUT_AUTH = "the @dataservice:Expose annotation of the entity '%s' sets " +
            "scopes, but 'options.dataservice.auth' is not true. Set it to true, or remove the scopes.";
    public static final String ANNOTATIONS_WITHOUT_OPTIONS = "the model has @dataservice:Expose annotations, but no " +
            "data service is generated. Set 'options.dataservice.protocol' in the [[tool.persist]] entry to " +
            "generate one.";
    public static final String PULL_SKIPS_ENTITY =
            "the table '%s' has an unsupported data type, so its entity '%s' is not exposed.";
    public static final String GENERATE_WITHOUT_OPTION = "the model has @dataservice:Expose annotations. Use the " +
            "'--dataservice' option to also generate a data service.";

    public static final String PULL_SKIPS_RESERVED_KEY = "the key field '%s' of the table '%s' uses a name " +
            "reserved by the data service, so its entity '%s' is not exposed.";
    public static final String PULL_SKIPS_COLLECTION_CLASH = "the entity '%s' of the table '%s' maps to the " +
            "collection '%s' of the entity '%s', so it is not exposed.";

    public static final String EXPOSE_TABLES_EMPTY =
            "'--expose-tables' names no table. Give the tables to expose, separated by commas.";
    public static final String EXPOSE_TABLE_RESERVED_KEY = "the table '%s' in '--expose-tables' has the key " +
            "field '%s', whose name the data service reserves, so its entity '%s' cannot be exposed.";
    public static final String PULL_SKIPS_TYPE_NAME = "the entity '%s' of the table '%s' would generate the " +
            "type '%s', which is the entity of the table '%s', so it is not exposed.";
    public static final String PULL_SKIPS_GENERATED_TYPE = "the entity '%s' of the table '%s' would generate the " +
            "type '%s', which the data service also generates for the entity '%s', so it is not exposed.";
    public static final String EXPOSE_TABLE_GENERATED_TYPE = "the table '%s' in '--expose-tables' would generate " +
            "the type '%s', which the data service also generates for the table '%s'. Expose only one of them.";
    public static final String EXPOSE_TABLE_TYPE_NAME = "the table '%s' in '--expose-tables' would generate the " +
            "type '%s' for its entity '%s', which is the entity of the table '%s'. Remove the table from " +
            "'--expose-tables'.";
    public static final String EXPOSE_TABLE_COLLECTION_CLASH = "the table '%s' in '--expose-tables' maps to the " +
            "collection '%s' of the table '%s'. Expose only one of them.";
    public static final String EXPOSE_TABLES_WITHOUT_DATASERVICE =
            "the '--expose-tables' option requires the '--dataservice' option.";
    public static final String EXPOSE_TABLE_NOT_PULLED = "the table '%s' in '--expose-tables' is not a pulled table.";
    public static final String EXPOSE_TABLE_UNSUPPORTED = "the table '%s' in '--expose-tables' has an unsupported " +
            "data type, so its entity '%s' cannot be exposed.";

    public static final String ADD_ANNOTATE_STEP =
            "2. Add the @dataservice:Expose annotation to each entity to expose.";
    public static final String ADD_BUILD_STEP = "3. Execute `bal build` to generate the persist client and the %s " +
            "data service during package build.";
    public static final String PULL_DATASERVICE_EXISTS = "the [[tool.persist]] entry of persist/model.bal already " +
            "has an 'options.dataservice' group. Remove it from Ballerina.toml and run the command again, or run " +
            "'bal persist pull' without '--dataservice'.";
    public static final String PULL_ENABLED = "enabled the %s data service in the [[tool.persist]] entry of " +
            "persist/model.bal in Ballerina.toml.";

    private DataServiceMessages() {
    }
}
