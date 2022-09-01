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

/**
 * Class to store persist entities.
 */
public class Entity {

    private final String module;
    private final String[] keys;
    private String tableName;

    private String entityName;

    private String packageName;

    private final ArrayList<FieldMetaData> fields = new ArrayList<>();
    public Entity(String[] keys, String tableName, String module) {

        this.keys = keys;
        this.tableName = tableName;
        this.module = module;
    }

    public String[] getKeys() {
        return this.keys;
    }

    public String getTableName() {
        return this.tableName;
    }
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getModule() {
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

}
