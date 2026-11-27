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

import io.ballerina.persist.BalException;
import io.ballerina.persist.models.Module;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates the sources of a data service for the protocol in its options.
 */
public interface DataServiceGenerator {

    String SERVICE_FILE = "dataservice.bal";
    String TYPES_FILE = "dataservice_types.bal";
    String CONFIG_FILE = "dataservice_config.bal";

    /**
     * Writes `dataservice.bal`, `dataservice_types.bal` and `dataservice_config.bal` to a directory.
     *
     * @param dir the directory of the generated sources
     * @throws BalException if a source cannot be formatted or its directory cannot be created
     * @throws IOException  if a file cannot be written or deleted
     */
    void write(Path dir) throws BalException, IOException;

    /**
     * Checks the exposed entities against the rules of a protocol, before anything is generated.
     *
     * @param protocol     the protocol
     * @param entities     the exposed entities, in model order
     * @param entityModule the persist model
     * @throws BalException with every problem found, one per line
     */
    static void validate(String protocol, List<ExposedEntity> entities, Module entityModule) throws BalException {
        if (DataServiceOptions.GRAPHQL.equals(protocol)) {
            GraphqlDataServiceGenerator.validate(entities, entityModule);
        } else {
            HttpDataServiceGenerator.validate(entities, entityModule);
        }
    }

    /**
     * Lists the key fields whose names the generated code of a protocol uses for its own parameters and variables.
     *
     * @param protocol the protocol
     * @param keyNames the names of an entity's key fields
     * @return the unescaped names of those key fields
     */
    static List<String> reservedKeys(String protocol, List<String> keyNames) {
        List<String> reserved = new ArrayList<>(DataServiceModel.reservedKeys(keyNames));
        if (DataServiceOptions.GRAPHQL.equals(protocol)) {
            reserved.addAll(GraphqlDataServiceGenerator.reservedKeys(keyNames));
        }
        return reserved;
    }

    /**
     * Returns the types a protocol declares for an entity that every operation exposes, as `pull` exposes it.
     *
     * @param protocol   the protocol
     * @param entityName the entity name
     * @return the type names
     */
    static List<String> pulledTypeNames(String protocol, String entityName) {
        return DataServiceOptions.GRAPHQL.equals(protocol) ? GraphqlDataServiceGenerator.pulledTypeNames(entityName) :
                HttpDataServiceGenerator.pulledTypeNames(entityName);
    }

    /**
     * Creates the generator of the protocol in the options.
     *
     * @param entities     the exposed entities, in model order
     * @param options      the data service options
     * @param packageName  the name of the package
     * @param modelName    the name of the persist model, as persist writes it in its file headers
     * @param datastore    the datastore of the persist client, such as `mysql`
     * @param userOwned    whether the files are written for the user to own, by `bal persist generate`
     * @param eagerLoading whether the client's collection resources return arrays, as `options.eagerLoading` sets
     * @return the generator
     */
    static DataServiceGenerator of(List<ExposedEntity> entities, DataServiceOptions options, String packageName,
                                   String modelName, String datastore, boolean userOwned, boolean eagerLoading) {
        if (DataServiceOptions.GRAPHQL.equals(options.protocol())) {
            return new GraphqlDataServiceGenerator(entities, options, modelName, datastore, userOwned,
                    eagerLoading);
        }
        return new HttpDataServiceGenerator(entities, options, packageName, modelName, datastore, userOwned,
                eagerLoading);
    }
}
