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
import io.ballerina.persist.PersistToolsConstants;
import io.ballerina.persist.models.Entity;
import io.ballerina.persist.models.EntityField;
import io.ballerina.persist.models.Module;

import java.util.ArrayList;
import java.util.Arrays;
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
     * @param protocol     the protocol of the data service, whose generated code reserves key and type names
     * @param exposeTables the value of `--expose-tables`, or null to expose every supported entity
     * @return the selection
     * @throws BalException if `--expose-tables` names no table, or a table that cannot be exposed, with one line per
     *                      table
     */
    public static PullExposure select(Module entityModule, String protocol, String exposeTables)
            throws BalException {
        Set<String> exposed = new LinkedHashSet<>();
        List<String> warnings = new ArrayList<>();
        // The entity that has taken each collection, by collection.
        Map<String, Entity> collections = new LinkedHashMap<>();
        // The selected entity that each generated type is declared for, by type name.
        Map<String, Entity> generated = new LinkedHashMap<>();
        if (exposeTables == null) {
            for (Entity entity : entityModule.getEntityMap().values()) {
                if (hasUnsupportedTypes(entity)) {
                    warnings.add(String.format(DataServiceMessages.PULL_SKIPS_ENTITY, entity.getTableName(),
                            entity.getEntityName()));
                    continue;
                }
                String collection = ExposedEntity.collectionOf(entity.getEntityName());
                List<String> reserved = reservedKeys(protocol, entity);
                Entity other = collections.get(collection);
                Entity named = namedByGeneratedType(protocol, entity, entityModule);
                if (!reserved.isEmpty()) {
                    warnings.add(String.format(DataServiceMessages.PULL_SKIPS_RESERVED_KEY, reserved.get(0),
                            entity.getTableName(), entity.getEntityName()));
                } else if (named != null) {
                    warnings.add(String.format(DataServiceMessages.PULL_SKIPS_TYPE_NAME, entity.getEntityName(),
                            entity.getTableName(), named.getEntityName(), named.getTableName()));
                } else if (sharedType(protocol, entity, generated) != null) {
                    String typeName = sharedType(protocol, entity, generated);
                    warnings.add(String.format(DataServiceMessages.PULL_SKIPS_GENERATED_TYPE, entity.getEntityName(),
                            entity.getTableName(), typeName, generated.get(typeName).getEntityName()));
                } else if (other != null) {
                    warnings.add(String.format(DataServiceMessages.PULL_SKIPS_COLLECTION_CLASH,
                            entity.getEntityName(), entity.getTableName(), collection,
                            other.getEntityName()));
                } else {
                    collections.put(collection, entity);
                    select(protocol, entity, generated);
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
            if (hasUnsupportedTypes(entity)) {
                errors.add(String.format(DataServiceMessages.EXPOSE_TABLE_UNSUPPORTED, table,
                        entity.getEntityName()));
                continue;
            }
            String collection = ExposedEntity.collectionOf(entity.getEntityName());
            List<String> reserved = reservedKeys(protocol, entity);
            Entity other = collections.get(collection);
            Entity named = namedByGeneratedType(protocol, entity, entityModule);
            if (!reserved.isEmpty()) {
                errors.add(String.format(DataServiceMessages.EXPOSE_TABLE_RESERVED_KEY, table, reserved.get(0),
                        entity.getEntityName()));
            } else if (named != null) {
                errors.add(String.format(DataServiceMessages.EXPOSE_TABLE_TYPE_NAME, table, named.getEntityName(),
                        entity.getEntityName(), named.getTableName()));
            } else if (sharedType(protocol, entity, generated) != null) {
                String typeName = sharedType(protocol, entity, generated);
                errors.add(String.format(DataServiceMessages.EXPOSE_TABLE_GENERATED_TYPE, table, typeName,
                        generated.get(typeName).getTableName()));
            } else if (other != null && other != entity) {
                errors.add(String.format(DataServiceMessages.EXPOSE_TABLE_COLLECTION_CLASH, table,
                        collection, other.getTableName()));
            } else {
                collections.put(collection, entity);
                select(protocol, entity, generated);
                exposed.add(entity.getEntityName());
            }
        }
        if (!errors.isEmpty()) {
            throw new BalException(String.join(System.lineSeparator(), errors));
        }
        return new PullExposure(exposed, warnings);
    }

    private static boolean hasUnsupportedTypes(Entity entity) {
        // Introspection flags only a table without a primary key; a column of an unsupported type is a field typed
        // as `Unsupported`.
        return entity.containsUnsupportedTypes() || entity.getFields().stream()
                .anyMatch(field -> PersistToolsConstants.UNSUPPORTED_TYPE.equals(field.getFieldType()));
    }

    // The introspected model has not resolved its relations yet, so only the entity's own key fields are read.
    private static List<String> reservedKeys(String protocol, Entity entity) {
        return DataServiceGenerator.reservedKeys(protocol,
                entity.getKeys().stream().map(EntityField::getFieldName).toList());
    }

    // The pulled entity whose name the data service would declare as a type of this entity, if any.
    private static Entity namedByGeneratedType(String protocol, Entity entity, Module entityModule) {
        for (String typeName : DataServiceGenerator.pulledTypeNames(protocol, entity.getEntityName())) {
            Entity named = entityModule.getEntityMap().get(typeName);
            if (named != null) {
                return named;
            }
        }
        return null;
    }

    // A type that another selected entity also generates, if any. The relations are not resolved yet, so every type
    // an entity can generate is compared.
    private static String sharedType(String protocol, Entity entity, Map<String, Entity> generated) {
        return DataServiceGenerator.pulledTypeNames(protocol, entity.getEntityName()).stream()
                .filter(typeName -> generated.containsKey(typeName) && generated.get(typeName) != entity)
                .findFirst().orElse(null);
    }

    private static void select(String protocol, Entity entity, Map<String, Entity> generated) {
        DataServiceGenerator.pulledTypeNames(protocol, entity.getEntityName())
                .forEach(typeName -> generated.put(typeName, entity));
    }
}
