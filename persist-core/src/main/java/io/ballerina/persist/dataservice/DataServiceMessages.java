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
    public static final String WITH_INIT_PARAMS = "a data service cannot be generated with " +
            "'options.withInitParams', because the service creates the client without arguments.";
    public static final String NAMED_MODEL = "a data service is not supported for a named persist model. " +
            "Use 'persist/model.bal' or remove 'options.dataservice'.";
    public static final String NO_EXPOSED_ENTITY = "the data service exposes no entity. Add the " +
            "@dataservice:Expose annotation to the entities to expose: %s.";
    public static final String UNSUPPORTED_ENTITY = "the entity '%s' has an unsupported data type and cannot be " +
            "exposed. Remove its @dataservice:Expose annotation.";
    public static final String EXPOSE_PLACEMENT =
            "the @dataservice:Expose annotation is allowed only on a record type in the persist model.";
    public static final String OPERATIONS_EMPTY =
            "'operations' in the @dataservice:Expose annotation of the entity '%s' cannot be empty.";
    public static final String OPERATIONS_NOT_LIST =
            "'operations' in the @dataservice:Expose annotation of the entity '%s' must be a list of operations.";
    public static final String OPERATION_UNKNOWN = "invalid operation '%s' in the @dataservice:Expose annotation " +
            "of the entity '%s'. Supported values: dataservice:READ, dataservice:CREATE, dataservice:UPDATE, " +
            "dataservice:DELETE.";
    public static final String OPERATION_DUPLICATE =
            "duplicate operation '%s' in the @dataservice:Expose annotation of the entity '%s'.";
    public static final String ANNOTATIONS_WITHOUT_OPTIONS = "the model has @dataservice:Expose annotations, but no " +
            "data service is generated. Set 'options.dataservice.protocol' in the [[tool.persist]] entry to " +
            "generate one.";
    public static final String PULL_SKIPS_ENTITY =
            "the table '%s' has an unsupported data type, so its entity '%s' is not exposed.";
    public static final String PULL_ANNOTATES_ENTITIES = "The exposed entities allow every action. Set " +
            "'operations' in their @dataservice:Expose annotations to restrict them.";
    public static final String GENERATE_WITHOUT_OPTION = "the model has @dataservice:Expose annotations. Use the " +
            "'--dataservice' option to also generate a data service.";

    public static final String EXPOSE_TABLES_WITHOUT_DATASERVICE =
            "the '--expose-tables' option requires the '--dataservice' option.";
    public static final String EXPOSE_TABLE_NOT_PULLED = "the table '%s' in '--expose-tables' is not a pulled table.";
    public static final String EXPOSE_TABLE_UNSUPPORTED = "the table '%s' in '--expose-tables' has an unsupported " +
            "data type, so its entity '%s' cannot be exposed.";
    public static final String PULL_ENTITY_NAME = "The table '%s' is exposed as the entity '%s'.";

    public static final String ADD_ANNOTATE_STEP =
            "2. Add the @dataservice:Expose annotation to each entity to expose.";
    public static final String ADD_BUILD_STEP = "3. Execute `bal build` to generate the persist client and the %s " +
            "data service during package build.";
    public static final String PULL_ENABLED = "Enabled the %s data service in the [[tool.persist]] entry of " +
            "persist/model.bal in Ballerina.toml.";
    public static final String PULL_BUILD_STEP = "Execute `bal build` to generate the persist client and the %s " +
            "data service.";

    private DataServiceMessages() {
    }
}
