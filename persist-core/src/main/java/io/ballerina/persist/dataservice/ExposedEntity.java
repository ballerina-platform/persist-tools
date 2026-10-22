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

import io.ballerina.persist.PersistToolsConstants;
import io.ballerina.persist.models.Entity;
import io.ballerina.persist.models.EntityField;
import io.ballerina.persist.models.Relation;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
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

    private ExposedEntity(String name, String resourceName, EnumSet<Operation> operations,
                          List<ServiceField> fields, List<ServiceField> insertFields,
                          List<ServiceField> updateFields) {
        this.name = name;
        this.resourceName = resourceName;
        this.operations = EnumSet.copyOf(operations);
        this.fields = fields;
        this.insertFields = insertFields;
        this.updateFields = updateFields;
    }

    /**
     * Builds the exposed entity from a persist entity, deriving the fields as persist does for its types.
     *
     * @param entity     the persist entity
     * @param operations the actions the entity allows
     * @param datastore  the datastore, which decides whether `<Entity>Insert` holds the key
     * @return the exposed entity
     */
    public static ExposedEntity of(Entity entity, EnumSet<Operation> operations, String datastore) {
        List<ServiceField> fields = new ArrayList<>();
        List<ServiceField> insertFields = new ArrayList<>();
        List<ServiceField> updateFields = new ArrayList<>();
        for (EntityField field : entity.getFields()) {
            boolean isKey = entity.getKeys().stream().anyMatch(key -> key == field);
            if (field.getRelation() != null) {
                if (field.getRelation().isOwner()) {
                    for (Relation.Key key : field.getRelation().getKeyColumns()) {
                        fields.add(new ServiceField(key.getField(), key.getType(), false, false, false));
                        insertFields.add(new ServiceField(key.getField(), key.getType(), false, false, false));
                        updateFields.add(new ServiceField(key.getField(), key.getType(), false, true, false));
                    }
                }
                continue;
            }
            String type = field.getFieldType() + (field.isArrayType() ? "[]" : "");
            fields.add(new ServiceField(field.getFieldName(), type, !isKey && field.isOptionalType(),
                    field.isOptionalField(), isKey));
            if (!isKey) {
                insertFields.add(new ServiceField(field.getFieldName(), type, field.isOptionalType(),
                        field.isOptionalField(), false));
                updateFields.add(new ServiceField(field.getFieldName(), type, field.isOptionalType(), true, false));
            }
        }
        boolean generated = entity.getFields().stream().anyMatch(EntityField::isDbGenerated);
        boolean aliasInsert = !generated || PersistToolsConstants.SUPPORTED_NOSQL_DB_PROVIDERS.contains(datastore);
        return new ExposedEntity(entity.getEntityName(), entity.getClientResourceName(), operations,
                List.copyOf(fields), aliasInsert ? null : List.copyOf(insertFields), List.copyOf(updateFields));
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
     * Returns the key fields, in declaration order.
     *
     * @return the key fields
     */
    public List<ServiceField> keys() {
        return fields.stream().filter(ServiceField::key).toList();
    }

    /**
     * Tells whether the entity needs wire types, because a field has a date, time or `byte[]` type.
     *
     * @return whether the entity has wire types
     */
    public boolean hasWireTypes() {
        return fields.stream().anyMatch(field -> field.conversion().isPresent());
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
}
