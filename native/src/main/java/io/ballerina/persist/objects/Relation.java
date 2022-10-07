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
 * Class to store entity relations.
 *
 * @since 0.1.0
 */

public class Relation {

    private final ArrayList<String> keyColumns;
    private final ArrayList<String> references;

    private ArrayList<FieldMetaData> relatedFields = new ArrayList<>();
    private final boolean isChild;
    private String relatedType;
    private String refTable;
    private String relatedInstance;
    private boolean parentIncluded = false;
    private Optional<String> relatedModule;

    private String modulePrefix = "entities";

    public Relation(String relatedType, String relatedInstance, ArrayList<String> keyColumns,
                    ArrayList<String> references, boolean isChild) {
        this.keyColumns = keyColumns;
        this.references = references;
        this.relatedType = relatedType;
        this.isChild = isChild;
        this.relatedInstance = relatedInstance;
    }

    public String getClientName() {
        String className = relatedType;
        if (relatedModule.isPresent()) {
            className = relatedModule.get().substring(0, 1).toUpperCase() +
                    relatedModule.get().substring(1)
                    + relatedType;
        }
        return className;
    }

    public String getRefTable() {
        return this.refTable;
    }

    public void setRefTable(String refTable) {
        this.refTable = refTable;
    }

    public String getRelatedType() {
        return this.relatedType;
    }
    public void setRelatedType(String relatedType) {
        this.relatedType = relatedType;
    }

    public String getRelatedInstance() {
        return relatedInstance;
    }

    public void setRelatedInstance(String relatedInstance) {
        this.relatedInstance = relatedInstance;
    }

    public ArrayList<String> getKeyColumns() {
        return keyColumns;
    }

    public ArrayList<String> getReferences() {
        return references;
    }

    public ArrayList<FieldMetaData> getRelatedFields() {
        return relatedFields;
    }

    public Optional<String> getRelatedModule() {
        return this.relatedModule;
    }

    public void setRelatedFields(ArrayList<FieldMetaData> relatedFields) {
        this.relatedFields = relatedFields;
    }

    public boolean isChild() {
        return isChild;
    }

    public void setRelatedModule(Optional<String> relatedModule) {
        this.relatedModule = relatedModule;
    }

    public boolean isParentIncluded() {
        return this.parentIncluded;
    }
    public void setParentIncluded(boolean parentIncluded) {
        this.parentIncluded = parentIncluded;
    }

    public void setModulePrefix(String modulePrefix) {
        this.modulePrefix = modulePrefix;
    }

    public String getModulePrefix() {
        return this.modulePrefix;
    }


}
