/*
 * Copyright (c) 2022, WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.ballerina.persist.models;

import io.ballerina.persist.inflector.Pluralizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Client to store persist entities.
 *
 * @since 0.1.0
 */
public class Entity {

    private final List<EntityField> keys;
    private final String tableName;
    private final String schemaName;
    private final String entityName;
    private List<EntityField> fields;
    private final List<Index> indexes;
    private final List<Index> uniqueIndexes;
    private final boolean containsUnsupportedTypes;
    private Entity(String entityName, List<EntityField> keys, String resourceName, String schemaName,
                   List<EntityField> fields, List<Index> indexes, List<Index> uniqueIndexes,
                   boolean containsUnsupportedTypes) {
        this.entityName = entityName;
        this.keys = Collections.unmodifiableList(keys);
        this.tableName = resourceName;
        this.schemaName = schemaName;
        this.fields = Collections.unmodifiableList(fields);
        this.indexes = Collections.unmodifiableList(indexes);
        this.uniqueIndexes = Collections.unmodifiableList(uniqueIndexes);
        this.containsUnsupportedTypes = containsUnsupportedTypes;
    }

    public List<EntityField> getKeys() {
        return this.keys;
    }

    public String getTableName() {
        return this.tableName;
    }

    public String getEntityName() {
        return this.entityName;
    }

    public List<EntityField> getFields() {
        return this.fields;
    }

    public String getClientResourceName() {
        return Pluralizer.pluralize(entityName.toLowerCase(Locale.ENGLISH));
    }

    public List<Index> getIndexes() {
        return this.indexes;
    }

    public List<Index> getUniqueIndexes() {
        return this.uniqueIndexes;
    }

    public EntityField getFieldByName(String fieldName) {
        for (EntityField field : fields) {
            if (field.getFieldName().equals(fieldName)) {
                return field;
            }
        }
        return null;
    }

    public boolean containsUnsupportedTypes() {
        return containsUnsupportedTypes;
    }

    public EntityField getFieldByColumnName(String columnName) {
        for (EntityField field : fields) {
            if (field.getFieldColumnName().equals(columnName)) {
                return field;
            }
        }
        return null;
    }

    public boolean shouldTableMappingGenerated() {
        if (tableName == null || tableName.isBlank()) {
            return false;
        }
        return !entityName.equals(tableName);
    }

    public void removeField(String fieldName) {
        List<EntityField> newFields = new ArrayList<>(this.fields);
        newFields.removeIf(field -> field.getFieldName().equals(fieldName));
        this.fields = Collections.unmodifiableList(newFields);
    }

    public static Entity.Builder newBuilder(String entityName) {
        return new Entity.Builder(entityName);
    }

    public String getSchemaName() {
        return this.schemaName;
    }

    /**
     * Entity Definition.Builder.
     */
    public static class Builder {

        String entityName;
        String tableName = null;
        String schemaName = null;
        List<EntityField> keys;
        List<EntityField> fieldList = null;
        List<Index> indexes;
        List<Index> uniqueIndexes;
        boolean containsUnsupportedTypes = false;

        private Builder(String entityName) {
            this.entityName = entityName;
            this.indexes = new ArrayList<>();
            this.uniqueIndexes = new ArrayList<>();
        }

        public void setTableName(String tableName) {
            this.tableName = tableName;
        }

        public void setSchemaName(String schemaName) {
            this.schemaName = schemaName;
        }

        public void setKeys(List<EntityField> keys) {
            this.keys = keys;
        }

        public void addField(EntityField field) {
            if (fieldList == null) {
                this.fieldList = new ArrayList<>();
            }
            fieldList.add(field);
        }

        public void addIndex(Index index) {
            indexes.add(index);
        }

        public void upsertIndex(String indexName, EntityField field) {
            List<EntityField> fields = new ArrayList<>();
            fields.add(field);
            Index existingIndex = this.indexes.stream()
                    .filter(i -> i.getIndexName().equals(indexName)).findFirst().orElse(null);
            if (existingIndex != null) {
                existingIndex.addField(field);
            } else {
                Index index = new Index(indexName, fields, false);
                indexes.add(index);
            }
        }

        public void upsertUniqueIndex(String indexName, EntityField field) {
            List<EntityField> fields = new ArrayList<>();
            fields.add(field);
            Index existingIndex = this.uniqueIndexes.stream()
                    .filter(i -> i.getIndexName().equals(indexName)).findFirst().orElse(null);
            if (existingIndex != null) {
                existingIndex.addField(field);
            } else {
                Index index = new Index(indexName, fields, true);
                uniqueIndexes.add(index);
            }
        }

        public void addUniqueIndex(Index index) {
            uniqueIndexes.add(index);
        }

        public void setContainsUnsupportedTypes(boolean containsUnsupportedTypes) {
            this.containsUnsupportedTypes = containsUnsupportedTypes;
        }

        public Entity build() {
            return new Entity(entityName, keys, tableName, schemaName, fieldList, indexes, uniqueIndexes,
                    containsUnsupportedTypes);
        }

        public String getEntityName() {
            return entityName;
        }

        public List<EntityField> getKeys() {
            return Collections.unmodifiableList(keys);
        }

    }
}
