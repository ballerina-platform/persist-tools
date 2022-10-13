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
import java.util.Optional;

/**
 * Class to store persist entities.
 *
 * @since 0.1.0
 */
public class Entity {

    private final Optional<String> module;
    private final String[] keys;
    public String modulePrefix = "entities";
    private String tableName;

    private String entityName;

    private String packageName;

    private ArrayList<Relation> relations = new ArrayList<>();

    private final ArrayList<FieldMetaData> fields = new ArrayList<>();
    public Entity(String[] keys, String tableName, Optional<String> module) {

        this.keys = keys;
        this.tableName = tableName;
        this.module = module;
    }

    public String getNamePrefix(boolean withTail) {
        if (!module.isEmpty() && withTail) {
            return module.get() + "Entities";
        } else if (!module.isEmpty() && !withTail) {
            return module.get().substring(0, 1).toUpperCase() + module.get().substring(1);
        } else if (module.isEmpty() && !withTail) {
            return "";
        }
        return modulePrefix;
    }

    public String[] getKeys() {
        return this.keys;
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

    public Optional<String> getModule() {
        return this.module;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageName() {
        return this.packageName;
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
