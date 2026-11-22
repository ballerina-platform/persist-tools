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

import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.persist.BalException;
import io.ballerina.persist.models.Entity;
import io.ballerina.persist.models.EntityField;
import io.ballerina.persist.models.Module;
import io.ballerina.persist.models.Relation;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.ballerina.persist.dataservice.DataServiceUtils.isInternal;

/**
 * Resolves the entities a data service exposes, from the model and its annotations.
 */
public final class DataServiceModel {

    /**
     * The names the generated resources give their parameters and variables. A key field becomes a path parameter of
     * the same resources, so it cannot take one of these names.
     */
    public static final Set<String> RESERVED_KEY_NAMES = Set.of("broken", "converted", "created", "ctx", "entries",
            "entry", "err", "fields", "filter", "forbidden", "hasMore", "i", "include", "included", "includedMore",
            "includedRows", "invalidFields", "invalidKey", "item", "itemKey", "itemPayload", "items", "key",
            "keyValues", "keys", "next", "offset", "partial", "partials", "payload", "query", "readable", "related",
            "row", "rows", "size", "sort", "sources", "stored", "tooMany", "value", "values");

    private DataServiceModel() {
    }

    /**
     * The exposed entities of a model, and the warnings found while resolving them.
     *
     * @param entities the exposed entities, in model order
     * @param warnings the warnings to print
     */
    public record Resolution(List<ExposedEntity> entities, List<String> warnings) {
    }

    /**
     * Resolves the exposed entities of a model file, with their annotations, doc comments and field rules.
     *
     * @param modelPath    the model file
     * @param entityModule the persist model read from it
     * @param auth         whether the service has auth, without which scopes are warned about
     * @return the exposed entities and the warnings
     * @throws BalException if an annotated entity cannot be exposed, or no entity is annotated
     */
    public static Resolution resolve(Path modelPath, Module entityModule, boolean auth) throws BalException {
        SyntaxTree model = ExposeAnnotations.parse(modelPath);
        Map<String, EnumSet<Operation>> annotations = ExposeAnnotations.read(model);
        FieldRules.Result rules = FieldRules.resolve(model, entityModule);
        Map<String, Map<Operation, List<String>>> scopes = ExposeAnnotations.scopes(model);
        Map<String, Map<String, String>> fieldDocs = ExposeAnnotations.fieldDocs(model);
        List<ExposedEntity> entities = resolve(entityModule, annotations, ExposeAnnotations.docs(model), rules)
                .stream().map(entity -> entity.withScopes(scopes.getOrDefault(entity.name(), Map.of()))
                        .withFieldDocs(fieldDocs.getOrDefault(entity.name(), Map.of()))).toList();
        return new Resolution(entities, scopeWarnings(entities, auth));
    }

    /**
     * Resolves the exposed entities.
     *
     * @param entityModule the persist model
     * @param annotations  the actions of each annotated entity, as `ExposeAnnotations` reads them
     * @param docs         the doc comment lines of each entity, as `ExposeAnnotations.docs` reads them
     * @param rules        the field rules, as `FieldRules` resolves them
     * @return the exposed entities, in model order
     * @throws BalException if an annotated entity cannot be exposed, or no entity is annotated
     */
    public static List<ExposedEntity> resolve(Module entityModule, Map<String, EnumSet<Operation>> annotations,
                                              Map<String, List<String>> docs, FieldRules.Result rules)
            throws BalException {
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
                ExposedEntity exposedEntity = ExposedEntity.of(entity, operations,
                        docs.getOrDefault(entity.getEntityName(), List.of()), rules.of(entity.getEntityName()));
                reservedKeys(exposedEntity).forEach(key -> errors.add(String.format(
                        DataServiceMessages.KEY_NAME_RESERVED, key, entity.getEntityName())));
                exposed.add(exposedEntity);
            }
        }
        Map<String, String> collections = new LinkedHashMap<>();
        for (ExposedEntity entity : exposed) {
            String other = collections.putIfAbsent(entity.collection(), entity.name());
            if (other != null) {
                errors.add(String.format(DataServiceMessages.COLLECTION_CLASH, other, entity.name(),
                        entity.collection()));
            }
        }
        // The entity that each generated type is declared for, by type name.
        Map<String, String> generated = new LinkedHashMap<>();
        for (ExposedEntity entity : exposed) {
            for (String typeName : generatedTypeNames(entity, exposed)) {
                String owner = generated.putIfAbsent(typeName, entity.name());
                if (entityModule.getEntityMap().containsKey(typeName)) {
                    errors.add(String.format(DataServiceMessages.TYPE_NAME_CLASH, typeName, entity.name(),
                            entity.name()));
                } else if (owner != null && !owner.equals(entity.name())) {
                    errors.add(String.format(DataServiceMessages.GENERATED_TYPE_CLASH, typeName, owner,
                            entity.name()));
                }
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
        Map<String, ExposedEntity> byName = new LinkedHashMap<>();
        exposed.forEach(entity -> byName.put(entity.name(), entity));
        return exposed.stream().map(entity -> entity.withRelations(
                readableRelations(entityModule.getEntityMap().get(entity.name()), byName, rules))).toList();
    }

    // A relation is readable when both entities allow read and neither its relation field nor its foreign key is
    // Internal. An Internal foreign key hides both sides, because reading either side filters on it.
    private static List<ExposedRelation> readableRelations(Entity entity, Map<String, ExposedEntity> exposed,
                                                           FieldRules.Result rules) {
        List<ExposedRelation> relations = new ArrayList<>();
        if (!exposed.get(entity.getEntityName()).allows(Operation.READ)) {
            return relations;
        }
        for (EntityField field : entity.getFields()) {
            Relation relation = field.getRelation();
            if (relation == null) {
                continue;
            }
            Entity target = relation.getAssocEntity();
            ExposedEntity exposedTarget = exposed.get(target.getEntityName());
            if (exposedTarget == null || !exposedTarget.allows(Operation.READ)
                    || isInternal(rules.of(entity.getEntityName()).get(field.getFieldName()))) {
                continue;
            }
            List<Relation.Key> keys = relation.getKeyColumns();
            List<String> fields = keys.stream().map(Relation.Key::getField).toList();
            List<String> targetFields = keys.stream().map(Relation.Key::getReference).toList();
            Map<String, FieldRules.Applied> ownerRules = rules.of(relation.isOwner() ? entity.getEntityName() :
                    target.getEntityName());
            List<String> foreignKey = relation.isOwner() ? fields : targetFields;
            if (foreignKey.stream().anyMatch(key -> isInternal(ownerRules.get(key)))) {
                continue;
            }
            relations.add(new ExposedRelation(field.getFieldName(), target.getEntityName(), field.isArrayType(),
                    relation.isOwner(), fields, targetFields));
        }
        return relations;
    }

    private static List<String> scopeWarnings(List<ExposedEntity> entities, boolean auth) {
        if (auth) {
            return List.of();
        }
        return entities.stream().filter(ExposedEntity::hasScopes)
                .map(entity -> String.format(DataServiceMessages.SCOPES_WITHOUT_AUTH, entity.name())).toList();
    }

    // The types that `HttpDataServiceGenerator` declares for an entity, under the same conditions.
    private static List<String> generatedTypeNames(ExposedEntity entity, List<ExposedEntity> exposed) {
        List<String> suffixes = new ArrayList<>();
        if (entity.hasWireTypes()) {
            suffixes.add("Payload");
            if (entity.allows(Operation.CREATE)) {
                suffixes.add("InsertPayload");
            }
            if (entity.allows(Operation.UPDATE)) {
                suffixes.add("UpdatePayload");
            }
        }
        if (entity.allows(Operation.READ) || entity.allows(Operation.CREATE)) {
            suffixes.add("List");
        }
        if (entity.allows(Operation.READ)) {
            suffixes.addAll(List.of("Filter", "Partial", "PartialList"));
            List<ExposedRelation> reaching = exposed.stream().flatMap(other -> other.relations().stream())
                    .filter(relation -> relation.target().equals(entity.name())).toList();
            if (!reaching.isEmpty()) {
                suffixes.add("RelatedPartial");
            }
            if (reaching.stream().anyMatch(ExposedRelation::many)) {
                suffixes.add("RelatedPartialList");
            }
        }
        if (entity.allows(Operation.CREATE)) {
            suffixes.addAll(List.of("Created", "ListCreated"));
        }
        return suffixes.stream().map(suffix -> entity.name() + suffix).toList();
    }

    /**
     * Returns the types the data service declares for an entity that every operation exposes, as `pull` exposes it.
     *
     * @param entityName the entity name
     * @return the type names
     */
    static List<String> pulledTypeNames(String entityName) {
        return List.of("List", "Filter", "Partial", "PartialList", "RelatedPartial", "RelatedPartialList",
                "Created", "ListCreated", "Payload", "InsertPayload", "UpdatePayload").stream()
                .map(suffix -> entityName + suffix).toList();
    }

    // The key fields whose names the generated resources use for their own parameters and variables.
    static List<String> reservedKeys(ExposedEntity entity) {
        return reservedKeys(entity.keys().stream().map(ServiceField::name).toList());
    }

    static List<String> reservedKeys(List<String> keyNames) {
        return keyNames.stream().map(DataServiceUtils::unescape).filter(RESERVED_KEY_NAMES::contains).toList();
    }

}
