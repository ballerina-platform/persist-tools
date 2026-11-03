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

/**
 * Resolves the entities a data service exposes, from the model and its annotations.
 */
public final class DataServiceModel {

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
     * @return the exposed entities and the warnings
     * @throws BalException if an annotation or a field rule is invalid, or no entity is annotated
     */
    public static Resolution resolve(Path modelPath, Module entityModule) throws BalException {
        SyntaxTree model = ExposeAnnotations.parse(modelPath);
        Map<String, EnumSet<Operation>> annotations = ExposeAnnotations.read(model);
        FieldRules.Result rules = FieldRules.resolve(model, entityModule, annotations);
        Map<String, Map<Operation, List<String>>> scopes = ExposeAnnotations.scopes(model);
        Map<String, Map<String, String>> fieldDocs = ExposeAnnotations.fieldDocs(model);
        return new Resolution(resolve(entityModule, annotations, ExposeAnnotations.docs(model), rules).stream()
                .map(entity -> entity.withScopes(scopes.getOrDefault(entity.name(), Map.of()))
                        .withFieldDocs(fieldDocs.getOrDefault(entity.name(), Map.of()))).toList(),
                rules.warnings());
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
                exposed.add(ExposedEntity.of(entity, operations, docs.getOrDefault(entity.getEntityName(), List.of()),
                        rules.of(entity.getEntityName())));
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

    /**
     * Returns the warnings about scopes set while the service has no auth.
     *
     * @param entities the exposed entities
     * @param auth     whether the service has auth
     * @return the warnings, in model order
     */
    public static List<String> scopeWarnings(List<ExposedEntity> entities, boolean auth) {
        if (auth) {
            return List.of();
        }
        return entities.stream().filter(ExposedEntity::hasScopes)
                .map(entity -> String.format(DataServiceMessages.SCOPES_WITHOUT_AUTH, entity.name())).toList();
    }

    /**
     * Tells whether the entity's `read` scopes cover those of a related entity, so that every caller allowed to read
     * the entity is allowed to read the related entity, and reading the relation needs no check of its own.
     *
     * @param entity   the entity
     * @param relation one of its readable relations
     * @param entities the exposed entities
     * @return whether the relation is covered
     */
    public static boolean covered(ExposedEntity entity, ExposedRelation relation, List<ExposedEntity> entities) {
        List<String> own = entity.scopes(Operation.READ);
        List<String> related = entities.stream().filter(target -> target.name().equals(relation.target()))
                .findFirst().orElseThrow().scopes(Operation.READ);
        return related.isEmpty() || (!own.isEmpty() && related.containsAll(own));
    }

    /**
     * Tells whether the entity's `read` scopes cover an action's, so that every caller allowed the action is allowed
     * to read the record it changed, and returning it needs no check of its own.
     *
     * @param entity the entity
     * @param action the action, `update` or `delete`
     * @return whether the action is covered
     */
    public static boolean readCovers(ExposedEntity entity, Operation action) {
        List<String> read = entity.scopes(Operation.READ);
        List<String> scopes = entity.scopes(action);
        return read.isEmpty() || (!scopes.isEmpty() && read.containsAll(scopes));
    }

    private static boolean isInternal(FieldRules.Applied rule) {
        return rule != null && rule.rule() == FieldRule.INTERNAL;
    }
}
