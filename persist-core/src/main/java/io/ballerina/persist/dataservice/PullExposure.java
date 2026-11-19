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
import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Selects the entities that `bal persist pull --dataservice` annotates.
 *
 * @param exposed  the names of the entities to annotate
 * @param warnings the warnings to print, for entities that are skipped
 */
public record PullExposure(Set<String> exposed, List<String> warnings) {

    /**
     * Selects the entities to annotate.
     *
     * @param entityModule the introspected model
     * @param exposeTables the value of `--expose-tables`, or null to expose every supported entity
     * @return the selection
     * @throws BalException if `--expose-tables` names no table, or a table that cannot be exposed, with one line per
     *                      table
     */
    public static PullExposure select(Module entityModule, String exposeTables) throws BalException {
        Set<String> exposed = new LinkedHashSet<>();
        List<String> warnings = new ArrayList<>();
        // The entity that has taken each collection, by collection.
        Map<String, Entity> collections = new LinkedHashMap<>();
        if (exposeTables == null) {
            for (Entity entity : entityModule.getEntityMap().values()) {
                if (entity.containsUnsupportedTypes()) {
                    warnings.add(String.format(DataServiceMessages.PULL_SKIPS_ENTITY, entity.getTableName(),
                            entity.getEntityName()));
                    continue;
                }
                ExposedEntity candidate = ExposedEntity.of(entity, EnumSet.allOf(Operation.class), List.of(),
                        Map.of());
                List<String> reserved = DataServiceModel.reservedKeys(candidate);
                Entity other = collections.get(candidate.collection());
                if (!reserved.isEmpty()) {
                    warnings.add(String.format(DataServiceMessages.PULL_SKIPS_RESERVED_KEY, reserved.get(0),
                            entity.getTableName(), entity.getEntityName()));
                } else if (other != null) {
                    warnings.add(String.format(DataServiceMessages.PULL_SKIPS_COLLECTION_CLASH,
                            entity.getEntityName(), entity.getTableName(), candidate.collection(),
                            other.getEntityName()));
                } else {
                    collections.put(candidate.collection(), entity);
                    exposed.add(entity.getEntityName());
                }
            }
            return new PullExposure(exposed, warnings);
        }
        List<String> errors = new ArrayList<>();
        // Names are parsed and matched as `--tables` parses and matches them.
        List<String> tables = Arrays.stream(exposeTables.split(",")).map(String::trim)
                .filter(table -> !table.isEmpty()).toList();
        if (tables.isEmpty()) {
            throw new BalException(DataServiceMessages.EXPOSE_TABLES_EMPTY);
        }
        for (String table : tables) {
            Entity entity = entityModule.getEntityMap().values().stream()
                    .filter(candidate -> table.equals(candidate.getTableName())).findFirst().orElse(null);
            if (entity == null) {
                errors.add(String.format(DataServiceMessages.EXPOSE_TABLE_NOT_PULLED, table));
                continue;
            }
            if (entity.containsUnsupportedTypes()) {
                errors.add(String.format(DataServiceMessages.EXPOSE_TABLE_UNSUPPORTED, table,
                        entity.getEntityName()));
                continue;
            }
            ExposedEntity candidate = ExposedEntity.of(entity, EnumSet.allOf(Operation.class), List.of(), Map.of());
            List<String> reserved = DataServiceModel.reservedKeys(candidate);
            Entity other = collections.get(candidate.collection());
            if (!reserved.isEmpty()) {
                errors.add(String.format(DataServiceMessages.EXPOSE_TABLE_RESERVED_KEY, table, reserved.get(0),
                        entity.getEntityName()));
            } else if (other != null && other != entity) {
                errors.add(String.format(DataServiceMessages.EXPOSE_TABLE_COLLECTION_CLASH, table,
                        candidate.collection(), other.getTableName()));
            } else {
                collections.put(candidate.collection(), entity);
                exposed.add(entity.getEntityName());
            }
        }
        if (!errors.isEmpty()) {
            throw new BalException(String.join(System.lineSeparator(), errors));
        }
        return new PullExposure(exposed, warnings);
    }
}
