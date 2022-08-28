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
import java.util.HashMap;

/**
 * Class to store persist entities.
 */
public class Entity {

    public String module;
    public String[] keys;
    public String tableName;

    public String entityName;

    public ArrayList<HashMap> fields = new ArrayList<>();
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

    public String getEntityName() {
        return this.entityName;
    }

    public ArrayList<HashMap> getFields() {
        return this.fields;
    }

}
