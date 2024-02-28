/*
 *  Copyright (c) 2024 WSO2 LLC. (http://www.wso2.com) All Rights Reserved.
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

import java.util.Collections;
import java.util.List;

public class Index {
    private final String indexName;
    private List<EntityField> fields;
    private boolean unique;

    public Index(String indexName, List<EntityField> fields, boolean unique) {
        this.indexName = indexName;
        this.fields = fields;
        this.unique = unique;
    }

    public String getIndexName() {
        return indexName;
    }

    public List<EntityField> getFields() {
        return Collections.unmodifiableList(fields);
    }

    public void addField(EntityField field) {
        fields.add(field);
    }


    public boolean isUnique() {
        return unique;
    }

}
