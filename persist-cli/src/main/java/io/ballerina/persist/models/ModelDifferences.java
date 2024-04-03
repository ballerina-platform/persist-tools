/*
 *  Copyright (c) 2024 WSO2 LLC. (http://www.wso2.com).
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package io.ballerina.persist.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class to process and store migrate differences.
 *
 *
 */
public class ModelDifferences {
    private final List<String> addedEntities = new ArrayList<>();
    private final List<NameMapping> renamedEntities = new ArrayList<>();
    private final List<String> removedEntities = new ArrayList<>();
    private final HashMap<String, List<EntityField>> addedFields = new HashMap<>();
    private final HashMap<String, List<NameMapping>> renamedFields = new HashMap<>();
    private final HashMap<String, List<String>> removedFields = new HashMap<>();
    private final HashMap<String, List<EntityField>> changedFieldTypes = new HashMap<>();
    private final Set<String> primaryKeyChangedEntities = new HashSet<>();
    private final HashMap<String, List<ForeignKey>> addedForeignKeys = new HashMap<>();
    private final HashMap<String, List<ForeignKey>> removedForeignKeys = new HashMap<>();
    private final List<String> differences = new ArrayList<>();
    public record NameMapping(String oldName, String newName) { }

    public void addTable(String tableName) {
        differences.add("Table " + tableName + " has been added");
        addedEntities.add(tableName);
    }

    public void removeTable(String tableName) {
        differences.add("Table " + tableName + " has been removed");
        removedEntities.add(tableName);
    }

    public void renameTable(String oldName, String newName) {
        differences.add("Table " + oldName + " has been renamed to " + newName);
        renamedEntities.add(new NameMapping(oldName, newName));
    }

    public void changePrimaryKey(String tableName) {
        differences.add("Primary key of table " + tableName + " has changed");
        primaryKeyChangedEntities.add(tableName);
    }

    public void addColumn(String tableName, EntityField field, boolean isPrimary) {
        differences.add("Column " + field.getFieldColumnName() + " of type " +
                field.getFieldType() + " has been added to table " +
                tableName + (isPrimary ? " as a primary key" : ""));
        if (isPrimary) {
            primaryKeyChangedEntities.add(tableName);
        }
        addOrModifyColumn(tableName, field, addedFields);
    }

    public void modifyColumn(String tableName, EntityField previousModelField, EntityField currentModelField) {
        differences.add("Data type of column " + previousModelField.getFieldColumnName() +
                " in table " + tableName + " has changed to " + currentModelField.getFieldType() +
                (currentModelField.getSqlType() != null ? " " +  currentModelField.getSqlType().getTypeName() : "") +
                " and is now " + (currentModelField.isOptionalType() ? "optional" : "required"));
        addOrModifyColumn(tableName, currentModelField, changedFieldTypes);
    }

    private void addOrModifyColumn(String tableName, EntityField field, HashMap<String, List<EntityField>> fieldMap) {
        if (!fieldMap.containsKey(tableName)) {
            List<EntityField> initialData = new ArrayList<>();
            initialData.add(field);
            fieldMap.put(tableName, initialData);
        } else {
            List<EntityField> existingData = fieldMap.get(tableName);
            existingData.add(field);
            fieldMap.put(tableName, existingData);
        }
    }

    public void removeColumn(String tableName, String columnName) {
        differences.add("Column " + columnName + " has been removed from table " + tableName);
        if (!removedFields.containsKey(tableName)) {
            List<String> initialData = new ArrayList<>();
            initialData.add(columnName);
            removedFields.put(tableName, initialData);
        } else {
            List<String> existingData = removedFields.get(tableName);
            existingData.add(columnName);
            removedFields.put(tableName, existingData);
        }
    }

    public void recreateForeignKey(String tableName, EntityField previousModelField, EntityField currentModelField) {
        differences.add("Relation " + previousModelField.getFieldColumnName() +
                " in table " + tableName + " has changed");
        removeForeignKey(tableName, previousModelField);
        createForeignKeys(tableName, currentModelField);
    }

    public void removeForeignKey(String tableName, EntityField field) {
        differences.add("Relation " + field.getFieldColumnName() + " has been removed from table " + tableName);
        addOrRemoveForeignKey(tableName, field, removedForeignKeys);
    }

    public void renameColumn(String tableName, String oldName, String newName) {
        differences.add("Column " + oldName + " in table " + tableName + " has been renamed to " + newName);
        if (!renamedFields.containsKey(tableName)) {
            List<NameMapping> initialData = new ArrayList<>();
            initialData.add(new NameMapping(oldName, newName));
            renamedFields.put(tableName, initialData);
        } else {
            List<NameMapping> existingData = renamedFields.get(tableName);
            existingData.add(new NameMapping(oldName, newName));
            renamedFields.put(tableName, existingData);
        }
    }

    private void addOrRemoveForeignKey(String tableName, EntityField field, Map<String, List<ForeignKey>> map) {
        if (field.getRelation() == null) {
            return;
        }
        String removeKeyName = String.format("FK_%s_%s", tableName,
                field.getRelation().getAssocEntity().getTableName());
        ForeignKey foreignKey = new ForeignKey(removeKeyName,
                field.getRelation().getKeyColumns().stream().map(Relation.Key::getColumnName).toList(),
                field.getRelation().getAssocEntity().getTableName(),
                field.getRelation().getKeyColumns().stream().map(Relation.Key::getReferenceColumnName).toList());

        if (!map.containsKey(tableName)) {
            List<ForeignKey> initialData = new ArrayList<>();
            initialData.add(foreignKey);
            map.put(tableName, initialData);
        } else {
            List<ForeignKey> existingData = map.get(tableName);
            existingData.add(foreignKey);
            map.put(tableName, existingData);
        }
    }

    public void createForeignKeys(String tableName, EntityField currentModelField) {

        for (Relation.Key key : currentModelField.getRelation().getKeyColumns()) {
            differences.add("Column " + key.getColumnName() + " of type " + key.getType() +
                    " has been added to table " + tableName
                    + " as a foreign key");
        }

        createForeignKeyColumn(tableName, currentModelField);

        differences.add("Relation " + currentModelField.getFieldName() + " of type " +
                currentModelField.getFieldType() + " has been added to table " +
                tableName);
        addOrRemoveForeignKey(tableName, currentModelField, addedForeignKeys);
    }

    private void createForeignKeyColumn(String tableName, EntityField field) {
        for (Relation.Key key : field.getRelation().getKeyColumns()) {
            EntityField primaryKey = field.getRelation().getAssocEntity()
                    .getFieldByColumnName(key.getReferenceColumnName());
            EntityField.Builder customFkBuilder = EntityField.newBuilder(key.getField());
            customFkBuilder.setFieldColumnName(key.getColumnName());
            customFkBuilder.setType(primaryKey.getFieldType());
            customFkBuilder.setArrayType(false);
            customFkBuilder.setSqlType(primaryKey.getSqlType());

            addOrModifyColumn(tableName, customFkBuilder.build(), addedFields);
        }
    }

    public boolean isEntityRenamed(String tableName) {
        return renamedEntities.stream().anyMatch(entry -> entry.newName().equals(tableName));
    }

    public List<String> getAddedEntities() {
        return addedEntities;
    }

    public List<NameMapping> getRenamedEntities() {
        return renamedEntities;
    }

    public List<String> getRemovedEntities() {
        return removedEntities;
    }

    public HashMap<String, List<EntityField>> getAddedFields() {
        return addedFields;
    }

    public HashMap<String, List<NameMapping>> getRenamedFields() {
        return renamedFields;
    }

    public HashMap<String, List<String>> getRemovedFields() {
        return removedFields;
    }

    public HashMap<String, List<EntityField>> getChangedFieldTypes() {
        return changedFieldTypes;
    }

    public Set<String> getPrimaryKeyChangedEntities() {
        return primaryKeyChangedEntities;
    }

    public HashMap<String, List<ForeignKey>> getAddedForeignKeys() {
        return addedForeignKeys;
    }

    public HashMap<String, List<ForeignKey>> getRemovedForeignKeys() {
        return removedForeignKeys;
    }

    public List<String> getDifferences() {
        return differences;
    }
}
