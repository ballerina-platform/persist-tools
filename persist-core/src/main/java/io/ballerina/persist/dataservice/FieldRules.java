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
import io.ballerina.compiler.syntax.tree.BasicLiteralNode;
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
import io.ballerina.compiler.syntax.tree.SimpleNameReferenceNode;
import io.ballerina.compiler.syntax.tree.SpecificFieldNode;
import io.ballerina.compiler.syntax.tree.SyntaxKind;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.compiler.syntax.tree.Token;
import io.ballerina.compiler.syntax.tree.TypeDefinitionNode;
import io.ballerina.compiler.syntax.tree.UnaryExpressionNode;
import io.ballerina.persist.BalException;
import io.ballerina.persist.models.Entity;
import io.ballerina.persist.models.EntityField;
import io.ballerina.persist.models.Enum;
import io.ballerina.persist.models.EnumMember;
import io.ballerina.persist.models.Module;
import io.ballerina.persist.models.Relation;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Reads and checks the field rules of a persist model, `@dataservice:ReadOnly`, `@dataservice:Immutable` and
 * `@dataservice:Internal`, from its syntax tree.
 */
public final class FieldRules {

    private static final String SQL_ORG = "ballerinax";
    private static final String GENERATED = "Generated";
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
     * @param rules    per entity, the rule of each field that has one. The owning side of a relation also has an
     *                 entry per foreign key field, with that field's part of the value
     * @param warnings the warnings to print
     */
    public record Result(Map<String, Map<String, Applied>> rules, List<String> warnings) {

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
     * Reads and checks the field rules of a model.
     *
     * @param model   the syntax tree of the model
     * @param module  the persist model, with its relations inferred
     * @param exposed the actions of each exposed entity
     * @return the rules and the warnings
     * @throws BalException if a rule is invalid, with one line per invalid rule
     */
    public static Result resolve(SyntaxTree model, Module module, Map<String, EnumSet<Operation>> exposed)
            throws BalException {
        ModulePartNode root = model.rootNode();
        Map<String, Map<String, Applied>> rules = new LinkedHashMap<>();
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        Optional<String> prefix = ExposeAnnotations.importPrefix(root, "ballerina", "dataservice");
        if (prefix.isEmpty()) {
            return new Result(rules, warnings);
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
                if (annotations.isEmpty()) {
                    continue;
                }
                String name = field.get().name();
                String entityName = entity.getEntityName();
                if (annotations.size() > 1) {
                    errors.add(String.format(DataServiceMessages.RULE_MORE_THAN_ONE, name, entityName));
                    continue;
                }
                FieldRule rule = ruleOf(annotations.get(0));
                Optional<ExpressionNode> value = value(annotations.get(0));
                if (declaredKeys.containsKey(name)) {
                    errors.add(String.format(DataServiceMessages.RULE_ON_DECLARED_FK, rule.annotation(), name,
                            entityName, declaredKeys.get(name)));
                    continue;
                }
                EntityField entityField = entity.getFieldByName(name);
                if (entityField == null) {
                    continue;
                }
                if (field.get().key() && !(rule == FieldRule.READ_ONLY && entityField.isDbGenerated())) {
                    errors.add(String.format(DataServiceMessages.RULE_ON_KEY, name, entityName, rule.annotation()));
                    continue;
                }
                Relation relation = entityField.getRelation();
                if (relation != null && !relation.isOwner()) {
                    if (rule != FieldRule.INTERNAL) {
                        errors.add(String.format(DataServiceMessages.RULE_ON_RELATION_WITHOUT_FK, rule.annotation(),
                                name, entityName));
                        continue;
                    }
                } else if (!checkValue(rule, value, entityField, module, entityName, errors)) {
                    continue;
                }
                Applied applied = new Applied(rule, value.map(expression -> expression.toSourceCode().trim()));
                entityRules.put(name, applied);
                if (relation != null && relation.isOwner()) {
                    List<Relation.Key> keys = relation.getKeyColumns();
                    for (int i = 0; i < keys.size(); i++) {
                        Optional<String> part = value.isEmpty() ? Optional.empty() : Optional.of(
                                (keys.size() > 1 && value.get() instanceof ListConstructorExpressionNode list ?
                                        list.expressions().get(i) : value.get()).toSourceCode().trim());
                        entityRules.put(keys.get(i).getField(), new Applied(rule, part));
                    }
                }
                EnumSet<Operation> operations = exposed.get(entityName);
                if (operations != null && rule.affected().stream().noneMatch(operations::contains)) {
                    warnings.add(String.format(DataServiceMessages.RULE_NO_EFFECT, rule.annotation(), name,
                            entityName, rule.affected().stream().map(Operation::value)
                                    .collect(Collectors.joining("' or '"))));
                }
            }
            if (!entityRules.isEmpty()) {
                rules.put(entity.getEntityName(), entityRules);
            }
        }
        if (!errors.isEmpty()) {
            throw new BalException(String.join(System.lineSeparator(), errors));
        }
        return new Result(rules, warnings);
    }

    // Checks that a rule that sets the field on create has a value it can set, of the field's type.
    private static boolean checkValue(FieldRule rule, Optional<ExpressionNode> value, EntityField field,
                                      Module module, String entity, List<String> errors) {
        boolean nullable = field.isOptionalType();
        if (value.isEmpty()) {
            if (rule != FieldRule.IMMUTABLE && !nullable && !field.isDbGenerated()) {
                errors.add(String.format(DataServiceMessages.RULE_NEEDS_VALUE, field.getFieldName(), entity,
                        rule.annotation()));
                return false;
            }
            return true;
        }
        boolean matches;
        String typeName;
        Relation relation = field.getRelation();
        if (relation != null) {
            List<String> keyTypes = relation.getKeyColumns().stream().map(Relation.Key::getType).toList();
            typeName = (keyTypes.size() == 1 ? keyTypes.get(0) : "[" + String.join(", ", keyTypes) + "]") +
                    (nullable ? "?" : "");
            if (keyTypes.size() == 1 || isNil(value.get())) {
                matches = matches(value.get(), keyTypes.get(0), nullable, module);
            } else {
                matches = value.get() instanceof ListConstructorExpressionNode list
                        && list.expressions().size() == keyTypes.size();
                for (int i = 0; matches && i < keyTypes.size(); i++) {
                    matches = matches((ExpressionNode) ((ListConstructorExpressionNode) value.get()).expressions()
                            .get(i), keyTypes.get(i), false, module);
                }
            }
        } else {
            typeName = field.getFieldType() + (field.isArrayType() ? "[]" : "") + (nullable ? "?" : "");
            matches = !field.isArrayType() && matches(value.get(), field.getFieldType(), nullable, module);
        }
        if (!matches) {
            errors.add(String.format(DataServiceMessages.RULE_VALUE_TYPE, rule.annotation(), field.getFieldName(),
                    entity, typeName));
        }
        return matches;
    }

    // Only literals and enum members are checked; any other expression is reported as not of the type.
    private static boolean matches(ExpressionNode value, String type, boolean nullable, Module module) {
        if (isNil(value)) {
            return nullable;
        }
        if (value instanceof UnaryExpressionNode unary && unary.unaryOperator().kind() == SyntaxKind.MINUS_TOKEN
                && unary.expression().kind() == SyntaxKind.NUMERIC_LITERAL) {
            return matches(unary.expression(), type, false, module);
        }
        Enum enumType = module.getEnumMap().get(type);
        if (value instanceof BasicLiteralNode literal) {
            Token token = literal.literalToken();
            return switch (literal.kind()) {
                case STRING_LITERAL -> "string".equals(type) || (enumType != null && enumType.getMembers().stream()
                        .anyMatch(member -> memberValue(member).equals(unquote(token.text()))));
                case BOOLEAN_LITERAL -> "boolean".equals(type);
                case NUMERIC_LITERAL -> numericMatches(token, type);
                default -> false;
            };
        }
        if (value instanceof SimpleNameReferenceNode reference && enumType != null) {
            return enumType.getMembers().stream()
                    .anyMatch(member -> member.getIdentifier().equals(reference.name().text().trim()));
        }
        return false;
    }

    private static boolean numericMatches(Token token, String type) {
        String text = token.text().trim();
        if (token.kind() == SyntaxKind.DECIMAL_INTEGER_LITERAL_TOKEN
                || token.kind() == SyntaxKind.HEX_INTEGER_LITERAL_TOKEN) {
            return "int".equals(type) || "float".equals(type) || "decimal".equals(type);
        }
        char suffix = Character.toLowerCase(text.charAt(text.length() - 1));
        if (suffix == 'f') {
            return "float".equals(type);
        }
        if (suffix == 'd') {
            return "decimal".equals(type);
        }
        return "float".equals(type) || "decimal".equals(type);
    }

    private static boolean isNil(ExpressionNode value) {
        return value.kind() == SyntaxKind.NIL_LITERAL || value.kind() == SyntaxKind.NULL_LITERAL;
    }

    private static String memberValue(EnumMember member) {
        return member.getValue() == null ? member.getIdentifier() : unquote(member.getValue());
    }

    private static String unquote(String text) {
        return text.length() >= 2 && text.startsWith("\"") && text.endsWith("\"") ?
                text.substring(1, text.length() - 1) : text;
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

    private record RuleField(String name, boolean key, Optional<MetadataNode> metadata) {

        private static Optional<RuleField> of(Node node) {
            if (node instanceof RecordFieldNode field) {
                return Optional.of(new RuleField(field.fieldName().text().trim(), field.readonlyKeyword().isPresent(),
                        field.metadata()));
            }
            if (node instanceof RecordFieldWithDefaultValueNode field) {
                return Optional.of(new RuleField(field.fieldName().text().trim(), field.readonlyKeyword().isPresent(),
                        field.metadata()));
            }
            return Optional.empty();
        }
    }
}
