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

import io.ballerina.persist.models.Entity;
import io.ballerina.persist.models.EntityField;
import io.ballerina.persist.models.Relation;
import io.ballerina.persist.nodegenerator.syntax.utils.BalSyntaxUtils;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * An entity that the data service exposes, with the fields of its persist-generated types.
 */
public final class ExposedEntity {

    private final String name;
    private final String resourceName;
    private final EnumSet<Operation> operations;
    private final List<ServiceField> fields;
    private final List<ServiceField> insertFields;
    private final List<ServiceField> updateFields;
    private final List<String> doc;
    private final List<Column> columns;
    private final List<ExposedRelation> relations;

    private ExposedEntity(String name, String resourceName, EnumSet<Operation> operations,
                          List<ServiceField> fields, List<ServiceField> insertFields,
                          List<ServiceField> updateFields, List<String> doc, List<Column> columns,
                          List<ExposedRelation> relations) {
        this.name = name;
        this.resourceName = resourceName;
        this.operations = EnumSet.copyOf(operations);
        this.fields = fields;
        this.insertFields = insertFields;
        this.updateFields = updateFields;
        this.doc = doc;
        this.columns = columns;
        this.relations = relations;
    }

    /**
     * Builds the exposed entity from a persist entity, deriving the fields as persist does for its types.
     *
     * @param entity     the persist entity
     * @param operations the actions the entity allows
     * @param doc        the lines of the entity's doc comment in the model, without the parameter lines
     * @param rules      the rule of each field that has one, as `FieldRules` resolves them
     * @return the exposed entity
     */
    public static ExposedEntity of(Entity entity, EnumSet<Operation> operations, List<String> doc,
                                   Map<String, FieldRules.Applied> rules) {
        List<ServiceField> fields = new ArrayList<>();
        List<ServiceField> insertFields = new ArrayList<>();
        List<ServiceField> updateFields = new ArrayList<>();
        List<Column> columns = new ArrayList<>();
        for (EntityField field : entity.getFields()) {
            boolean isKey = entity.getKeys().stream().anyMatch(key -> key == field);
            if (field.getRelation() != null) {
                if (field.getRelation().isOwner()) {
                    for (Relation.Key key : field.getRelation().getKeyColumns()) {
                        FieldRules.Applied rule = rules.get(key.getField());
                        fields.add(new ServiceField(key.getField(), key.getType(), false, false, false)
                                .withRule(rule));
                        insertFields.add(new ServiceField(key.getField(), key.getType(), false, false, false)
                                .withRule(rule));
                        updateFields.add(new ServiceField(key.getField(), key.getType(), false, true, false)
                                .withRule(rule));
                        if (!isInternal(rule)) {
                            columns.add(new Column(unescape(key.getField()), unescape(key.getColumnName()),
                                    Column.typeOf(key.getType(), false), false, false, false));
                        }
                    }
                }
                continue;
            }
            String type = field.getFieldType() + (field.isArrayType() ? "[]" : "");
            FieldRules.Applied rule = rules.get(field.getFieldName());
            fields.add(new ServiceField(field.getFieldName(), type, !isKey && field.isOptionalType(),
                    field.isOptionalField(), isKey).withRule(rule));
            if (!isInternal(rule)) {
                columns.add(new Column(unescape(field.getFieldName()), unescape(field.getFieldColumnName()),
                        Column.typeOf(type, field.getEnum() != null), !isKey && field.isOptionalType(), isKey,
                        field.isDbGenerated()));
            }
            if (!isKey) {
                insertFields.add(new ServiceField(field.getFieldName(), type, field.isOptionalType(),
                        field.isOptionalField(), false).withRule(rule));
                updateFields.add(new ServiceField(field.getFieldName(), type, field.isOptionalType(), true, false)
                        .withRule(rule));
            }
        }
        boolean generated = entity.getFields().stream().anyMatch(EntityField::isDbGenerated);
        return new ExposedEntity(entity.getEntityName(), entity.getClientResourceName(), operations,
                List.copyOf(fields), generated ? List.copyOf(insertFields) : null, List.copyOf(updateFields),
                List.copyOf(doc), List.copyOf(columns), List.of());
    }

    /**
     * Returns the entity with the relations that callers can read.
     *
     * @param readable the relations, in model order
     * @return the entity with them
     */
    public ExposedEntity withRelations(List<ExposedRelation> readable) {
        return new ExposedEntity(name, resourceName, operations, fields, insertFields, updateFields, doc, columns,
                List.copyOf(readable));
    }

    private static boolean isInternal(FieldRules.Applied rule) {
        return rule != null && rule.rule() == FieldRule.INTERNAL;
    }

    private static String unescape(String name) {
        return BalSyntaxUtils.stripEscapeCharacter(name.startsWith("'") ? name.substring(1) : name);
    }

    /**
     * Returns the entity name.
     *
     * @return the name, as written in the model
     */
    public String name() {
        return name;
    }

    /**
     * Returns the resource name of the entity in the persist client.
     *
     * @return the resource name, such as `employees`
     */
    public String resourceName() {
        return resourceName;
    }

    /**
     * Returns the actions the entity allows.
     *
     * @return a copy of the actions
     */
    public EnumSet<Operation> operations() {
        return EnumSet.copyOf(operations);
    }

    /**
     * Returns the fields of `<Entity>`.
     *
     * @return the fields, in declaration order
     */
    public List<ServiceField> fields() {
        return fields;
    }

    /**
     * Returns the fields of `<Entity>Insert`.
     *
     * @return the fields, or empty when persist declares `<Entity>Insert` as `<Entity>`
     */
    public Optional<List<ServiceField>> insertFields() {
        return Optional.ofNullable(insertFields);
    }

    /**
     * Returns the fields of `<Entity>Update`.
     *
     * @return the fields, in declaration order
     */
    public List<ServiceField> updateFields() {
        return updateFields;
    }

    /**
     * Returns the entity's doc comment in the model.
     *
     * @return the lines of the description, without the parameter lines; empty when the entity has none
     */
    public List<String> doc() {
        return doc;
    }

    /**
     * Returns the fields that callers can see, as the entity description of `ballerina/dataservice` holds them.
     *
     * @return the fields in model order, without the `Internal` ones
     */
    public List<Column> columns() {
        return columns;
    }

    /**
     * Returns the relations that callers can read, by get related and in `fields`.
     *
     * @return the relations in model order
     */
    public List<ExposedRelation> relations() {
        return relations;
    }

    /**
     * Returns the key fields, in declaration order.
     *
     * @return the key fields
     */
    public List<ServiceField> keys() {
        return fields.stream().filter(ServiceField::key).toList();
    }

    /**
     * Tells whether the entity needs wire types, because a field has a date, time or `byte[]` type or a field rule.
     *
     * @return whether the entity has wire types
     */
    public boolean hasWireTypes() {
        return fields.stream().anyMatch(field -> field.conversion().isPresent() || field.rule().isPresent());
    }

    /**
     * Tells whether the entity allows an action.
     *
     * @param operation the action
     * @return whether it is allowed
     */
    public boolean allows(Operation operation) {
        return operations.contains(operation);
    }

    /**
     * A field of the entity description, with its unescaped name and its column name.
     *
     * @param name      the field name
     * @param column    the column name
     * @param type      the `dataservice:FieldType` member
     * @param nullable  whether the field can be nil
     * @param key       whether the field is a key field
     * @param generated whether the database generates the value
     */
    public record Column(String name, String column, String type, boolean nullable, boolean key, boolean generated) {

        static String typeOf(String type, boolean isEnum) {
            if (isEnum) {
                return "ENUM";
            }
            return switch (type) {
                case "int" -> "INT";
                case "float" -> "FLOAT";
                case "decimal" -> "DECIMAL";
                case "boolean" -> "BOOLEAN";
                case "byte[]" -> "BYTES";
                case "time:Date" -> "DATE";
                case "time:TimeOfDay" -> "TIME_OF_DAY";
                case "time:Civil" -> "CIVIL";
                case "time:Utc" -> "UTC";
                default -> "STRING";
            };
        }

        /**
         * Tells whether the field can be used in `filter` and `sort`.
         *
         * @return whether it can
         */
        public boolean queryable() {
            return !"BYTES".equals(type);
        }
    }
}
