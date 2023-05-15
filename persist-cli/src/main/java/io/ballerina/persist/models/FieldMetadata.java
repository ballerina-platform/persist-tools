/*
 *  Copyright (c) 2023, WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 LLC. licenses this file to you under the Apache License,
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

/**
 * FieldMetaData class.
 *
 * @since 0.4.0
 */
public class FieldMetadata {
    private String name;
    private String dataType;
    private Boolean arrayType;

    public FieldMetadata(String name, String dataType, Boolean arrayType) {
        this.name = name;
        this.dataType = dataType;
        this.arrayType = arrayType;
    }

    public FieldMetadata(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getDataType() {
        return dataType;
    }

    public Boolean isArrayType() {
        return arrayType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public void setArrayType(Boolean arrayType) {
        this.arrayType = arrayType;
    }
}
