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
        return new Resolution(resolve(entityModule, annotations, ExposeAnnotations.docs(model), rules),
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

    // A relation is readable when both entities allow read and no Internal rule hides it. Internal on the owning
    // side hides both sides, because the other side filters on the hidden foreign key.
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
            if (!relation.isOwner() && target.getFields().stream().anyMatch(owning -> owning.getRelation() != null
                    && owning.getRelation().isOwner()
                    && owning.getRelation().getAssocEntity().getEntityName().equals(entity.getEntityName())
                    && owning.getRelation().getKeyColumns().stream().map(Relation.Key::getField).toList()
                            .equals(targetFields)
                    && isInternal(rules.of(target.getEntityName()).get(owning.getFieldName())))) {
                continue;
            }
            relations.add(new ExposedRelation(field.getFieldName(), target.getEntityName(), field.isArrayType(),
                    relation.isOwner(), fields, targetFields));
        }
        return relations;
    }

    private static boolean isInternal(FieldRules.Applied rule) {
        return rule != null && rule.rule() == FieldRule.INTERNAL;
    }
}
