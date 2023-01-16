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

import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.DOUBLE_QUOTE;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.EMPTY_STRING;

/**
 * Client to store persist entities.
 *
 * @since 0.1.0
 */
public class Entity {

    private final List<EntityField> keys;
    private final String tableName;

    private final String entityName;

    private final ModuleMemberDeclarationNode node;

    private final List<EntityField> fields;

    private Entity(ModuleMemberDeclarationNode node, String entityName, List<EntityField> keys,
                   String tableName, List<EntityField> fields) {
        this.node = node;
        this.entityName = entityName;
        this.keys = keys;
        this.tableName = tableName;
        this.fields = fields;
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

    public ModuleMemberDeclarationNode getNode() {
        return node;
    }

    public static Entity.Builder newBuilder(String entityName) {
        return new Entity.Builder(entityName);
    }

    /**
     * Entity Definition.Builder.
     */
    public static class Builder {
        String entityName;
        String tableName = null;
        List<EntityField> keys;

        List<EntityField> fieldList = null;

        ModuleMemberDeclarationNode node;

        private Builder(String entityName) {
            this.entityName = entityName;
        }

        public void setKeys(List<EntityField> keys) {
            this.keys = keys;
        }

        public void setTableName(String tableName) {
            this.tableName = tableName.replaceAll(DOUBLE_QUOTE, EMPTY_STRING);
        }

        public void setDeclarationNode(ModuleMemberDeclarationNode node) {
            this.node = node;
        }

        public void addField(EntityField field) {
            if (fieldList == null) {
                this.fieldList = new ArrayList<>();
            }
            fieldList.add(field);
        }

        public Entity build() {
            if (tableName == null) {
                tableName = entityName.toLowerCase(Locale.ENGLISH);
            }
            return new Entity(node, entityName, keys, tableName, fieldList);
        }
    }
}
