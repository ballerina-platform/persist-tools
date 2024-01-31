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
    private final String resourceName;

    private final String entityName;


    private final List<EntityField> fields;

    private final List<Index> indexes;

    private final List<Index> uniqueIndexes;

    private Entity(String entityName, List<EntityField> keys,
                   String resourceName, List<EntityField> fields, List<Index> indexes, List<Index> uniqueIndexes) {
        this.entityName = entityName;
        this.keys = Collections.unmodifiableList(keys);
        this.resourceName = resourceName;
        this.fields = Collections.unmodifiableList(fields);
        this.indexes = Collections.unmodifiableList(indexes);
        this.uniqueIndexes = Collections.unmodifiableList(uniqueIndexes);
    }

    public List<EntityField> getKeys() {
        return this.keys;
    }

    public String getResourceName() {
        return this.resourceName;
    }
    public String getEntityName() {
        return this.entityName;
    }

    public List<EntityField> getFields() {
        return this.fields;
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

    public EntityField getFieldByFieldResourceName(String fieldResourceName) {
        for (EntityField field : fields) {
            if (field.getFieldResourceName().equals(fieldResourceName)) {
                return field;
            }
        }
        return null;
    }

    public static Entity.Builder newBuilder(String entityName) {
        return new Entity.Builder(entityName);
    }

    /**
     * Entity Definition.Builder.
     */
    public static class Builder {
        String entityName;
        String resourceName = null;
        List<EntityField> keys;

        List<EntityField> fieldList = null;

        List<Index> indexes;

        List<Index> uniqueIndexes;

        private Builder(String entityName) {
            this.entityName = entityName;
            this.indexes = new ArrayList<>();
            this.uniqueIndexes = new ArrayList<>();
        }

        public void setResourceName(String resourceName) {
            this.resourceName = resourceName;
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

        public void addUniqueIndex(Index index) {
            uniqueIndexes.add(index);
        }

        public Entity build() {
            if (resourceName == null) {
                resourceName = entityName.toLowerCase(Locale.ENGLISH);
            }
            resourceName = Pluralizer.pluralize(resourceName);
            return new Entity(entityName, keys, resourceName, fieldList, indexes, uniqueIndexes);
        }

        public Entity buildForIntrospection() {
            return new Entity(entityName, keys, resourceName, fieldList, indexes, uniqueIndexes);
        }

        public EntityField getFieldByName(String fieldName) {
            for (EntityField field : fieldList) {
                if (field.getFieldName().equals(fieldName)) {
                    return field;
                }
            }
            return null;
        }

        public String getEntityName() {
            return entityName;
        }

    }
}
