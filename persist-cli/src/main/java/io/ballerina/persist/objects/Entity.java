/*
 *  Copyright (c) 2022, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package io.ballerina.persist.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to store persist entities.
 *
 * @since 0.1.0
 */
public class Entity {

    private final String[] keys;

    private final List<List<String>> uniqueConstraints;
    private String tableName;

    private String entityName;

    private String packageName;

    private ArrayList<Relation> relations = new ArrayList<>();

    private final ArrayList<FieldMetaData> fields = new ArrayList<>();
    public Entity(String[] keys, String tableName, List<List<String>> uniqueConstraints) {

        this.keys = keys;
        this.tableName = tableName;
        this.uniqueConstraints = uniqueConstraints;
    }

    public String[] getKeys() {
        return this.keys;
    }

    public List<List<String>> getUniqueConstraints() {
        return this.uniqueConstraints;
    }

    public String getTableName() {
        if (this.tableName == null) {
            this.tableName = entityName;
        }
        return this.tableName;
    }
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getEntityName() {
        return this.entityName;
    }
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public ArrayList<FieldMetaData> getFields() {
        return this.fields;
    }

    public void addField(FieldMetaData field) {
        this.fields.add(field);
    }

    public ArrayList<Relation> getRelations() {
        return this.relations;
    }
}
