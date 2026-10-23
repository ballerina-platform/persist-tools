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
import io.ballerina.persist.models.Entity;
import io.ballerina.persist.models.Module;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * Resolves the entities a data service exposes, from the model and its annotations.
 */
public final class DataServiceModel {

    private DataServiceModel() {
    }

    /**
     * Resolves the exposed entities.
     *
     * @param entityModule the persist model
     * @param annotations  the actions of each annotated entity, as `ExposeAnnotations` reads them
     * @param datastore    the datastore of the persist client
     * @return the exposed entities, in model order
     * @throws BalException if an annotated entity cannot be exposed, or no entity is annotated
     */
    public static List<ExposedEntity> resolve(Module entityModule, Map<String, EnumSet<Operation>> annotations,
                                              String datastore) throws BalException {
        List<ExposedEntity> exposed = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        for (Entity entity : entityModule.getEntityMap().values()) {
            EnumSet<Operation> operations = annotations.get(entity.getEntityName());
            if (operations == null) {
                continue;
            }
            if (entity.containsUnsupportedTypes()) {
                errors.add(String.format(DataServiceMessages.UNSUPPORTED_ENTITY, entity.getEntityName()));
            } else {
                exposed.add(ExposedEntity.of(entity, operations, datastore));
            }
        }
        if (!errors.isEmpty()) {
            throw new BalException(String.join(System.lineSeparator(), errors));
        }
        if (exposed.isEmpty()) {
            List<String> candidates = entityModule.getEntityMap().values().stream()
                    .filter(entity -> !entity.containsUnsupportedTypes()).map(Entity::getEntityName).toList();
            throw new BalException(String.format(DataServiceMessages.NO_EXPOSED_ENTITY,
                    String.join(", ", candidates)));
        }
        return exposed;
    }
}
