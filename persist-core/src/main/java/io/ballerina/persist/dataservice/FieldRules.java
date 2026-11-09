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

import io.ballerina.compiler.syntax.tree.AnnotationNode;
import io.ballerina.compiler.syntax.tree.ExpressionNode;
import io.ballerina.compiler.syntax.tree.ListConstructorExpressionNode;
import io.ballerina.compiler.syntax.tree.MappingFieldNode;
import io.ballerina.compiler.syntax.tree.MetadataNode;
import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;
import io.ballerina.compiler.syntax.tree.ModulePartNode;
import io.ballerina.compiler.syntax.tree.Node;
import io.ballerina.compiler.syntax.tree.QualifiedNameReferenceNode;
import io.ballerina.compiler.syntax.tree.RecordFieldNode;
import io.ballerina.compiler.syntax.tree.RecordFieldWithDefaultValueNode;
import io.ballerina.compiler.syntax.tree.RecordTypeDescriptorNode;
import io.ballerina.compiler.syntax.tree.SpecificFieldNode;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.compiler.syntax.tree.TypeDefinitionNode;
import io.ballerina.persist.models.Entity;
import io.ballerina.persist.models.EntityField;
import io.ballerina.persist.models.Module;
import io.ballerina.persist.models.Relation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.ballerina.persist.dataservice.DataServiceUtils.unquote;

/**
 * Reads the field rules of a persist model, `@dataservice:ReadOnly`, `@dataservice:Immutable` and
 * `@dataservice:Internal`, from its syntax tree.
 */
public final class FieldRules {

    private static final String SQL_ORG = "ballerinax";
    private static final String RELATION = "Relation";
    private static final String KEYS = "keys";
    private static final String VALUE = "value";

    /**
     * The rule of a field.
     *
     * @param rule  the rule
     * @param value the Ballerina source of the value the service sets on create, if the annotation gives one
     */
    public record Applied(FieldRule rule, Optional<String> value) {
    }

    /**
     * The rules of a model.
     *
     * @param rules per entity, the rule of each field that has one, foreign key fields included
     */
    public record Result(Map<String, Map<String, Applied>> rules) {

        /**
         * Returns the rules of an entity.
         *
         * @param entity the entity name
         * @return the rule of each field that has one
         */
        public Map<String, Applied> of(String entity) {
            return rules.getOrDefault(entity, Map.of());
        }
    }

    private FieldRules() {
    }

    /**
     * Reads the field rules of a model. The `ballerina/dataservice` compiler plugin checks them when the model is
     * compiled, before this runs.
     *
     * @param model  the syntax tree of the model
     * @param module the persist model, with its relations inferred
     * @return the rules
     */
    public static Result resolve(SyntaxTree model, Module module) {
        ModulePartNode root = model.rootNode();
        Map<String, Map<String, Applied>> rules = new LinkedHashMap<>();
        Optional<String> prefix = ExposeAnnotations.importPrefix(root, "ballerina", "dataservice");
        if (prefix.isEmpty()) {
            return new Result(rules);
        }
        Optional<String> sqlPrefix = ExposeAnnotations.importPrefix(root, SQL_ORG, "persist", "sql");
        for (ModuleMemberDeclarationNode member : root.members()) {
            if (!(member instanceof TypeDefinitionNode type)
                    || !(type.typeDescriptor() instanceof RecordTypeDescriptorNode record)) {
                continue;
            }
            Entity entity = module.getEntityMap().get(type.typeName().text().trim());
            if (entity == null) {
                continue;
            }
            Map<String, String> declaredKeys = declaredForeignKeys(record, sqlPrefix);
            Map<String, Applied> entityRules = new LinkedHashMap<>();
            for (Node node : record.fields()) {
                Optional<RuleField> field = RuleField.of(node);
                if (field.isEmpty()) {
                    continue;
                }
                List<AnnotationNode> annotations = ruleAnnotations(field.get().metadata(), prefix.get());
                String name = field.get().name();
                boolean foreignKey = declaredKeys.containsKey(name)
                        && foreignKey(entity, declaredKeys.get(name), name).isPresent();
                if (annotations.size() != 1 || (entity.getFieldByName(name) == null && !foreignKey)) {
                    continue;
                }
                Optional<ExpressionNode> value = value(annotations.get(0));
                entityRules.put(name, new Applied(ruleOf(annotations.get(0)),
                        value.map(expression -> expression.toSourceCode().trim())));
            }
            if (!entityRules.isEmpty()) {
                rules.put(entity.getEntityName(), entityRules);
            }
        }
        return new Result(rules);
    }

    private static Optional<Relation.Key> foreignKey(Entity entity, String relation, String field) {
        EntityField relationField = entity.getFieldByName(relation);
        if (relationField == null || relationField.getRelation() == null) {
            return Optional.empty();
        }
        return relationField.getRelation().getKeyColumns().stream().filter(key -> key.getField().equals(field))
                .findFirst();
    }

    // The foreign key fields declared with @sql:Relation, and the relation field each belongs to.
    private static Map<String, String> declaredForeignKeys(RecordTypeDescriptorNode record,
                                                           Optional<String> sqlPrefix) {
        Map<String, String> keys = new HashMap<>();
        if (sqlPrefix.isEmpty()) {
            return keys;
        }
        for (Node node : record.fields()) {
            Optional<RuleField> field = RuleField.of(node);
            if (field.isEmpty() || field.get().metadata().isEmpty()) {
                continue;
            }
            for (AnnotationNode annotation : field.get().metadata().get().annotations()) {
                if (!isAnnotation(annotation, sqlPrefix.get(), RELATION) || annotation.annotValue().isEmpty()) {
                    continue;
                }
                for (MappingFieldNode mappingField : annotation.annotValue().get().fields()) {
                    if (mappingField instanceof SpecificFieldNode specific
                            && KEYS.equals(specific.fieldName().toSourceCode().trim())
                            && specific.valueExpr().isPresent()
                            && specific.valueExpr().get() instanceof ListConstructorExpressionNode list) {
                        list.expressions().forEach(key -> keys.put(unquote(key.toSourceCode().trim()),
                                field.get().name()));
                    }
                }
            }
        }
        return keys;
    }

    private static List<AnnotationNode> ruleAnnotations(Optional<MetadataNode> metadata, String prefix) {
        List<AnnotationNode> annotations = new ArrayList<>();
        metadata.ifPresent(node -> node.annotations().forEach(annotation -> {
            for (FieldRule rule : FieldRule.values()) {
                if (isAnnotation(annotation, prefix, rule.annotation())) {
                    annotations.add(annotation);
                }
            }
        }));
        return annotations;
    }

    private static FieldRule ruleOf(AnnotationNode annotation) {
        String name = ((QualifiedNameReferenceNode) annotation.annotReference()).identifier().text().trim();
        for (FieldRule rule : FieldRule.values()) {
            if (rule.annotation().equals(name)) {
                return rule;
            }
        }
        throw new IllegalArgumentException(name);
    }

    private static Optional<ExpressionNode> value(AnnotationNode annotation) {
        if (annotation.annotValue().isEmpty()) {
            return Optional.empty();
        }
        for (MappingFieldNode field : annotation.annotValue().get().fields()) {
            if (field instanceof SpecificFieldNode specific && VALUE.equals(specific.fieldName().toSourceCode().trim())
                    && specific.valueExpr().isPresent()) {
                return specific.valueExpr();
            }
        }
        return Optional.empty();
    }

    private static boolean isAnnotation(AnnotationNode annotation, String prefix, String name) {
        return annotation.annotReference() instanceof QualifiedNameReferenceNode reference
                && prefix.equals(reference.modulePrefix().text()) && name.equals(reference.identifier().text().trim());
    }

    private record RuleField(String name, Optional<MetadataNode> metadata) {

        private static Optional<RuleField> of(Node node) {
            if (node instanceof RecordFieldNode field) {
                return Optional.of(new RuleField(field.fieldName().text().trim(), field.metadata()));
            }
            if (node instanceof RecordFieldWithDefaultValueNode field) {
                return Optional.of(new RuleField(field.fieldName().text().trim(), field.metadata()));
            }
            return Optional.empty();
        }
    }
}
