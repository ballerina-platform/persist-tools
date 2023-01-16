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

import java.util.List;

/**
 * Class to store entity relations.
 *
 * @since 0.1.0
 */

public class Relation {

    /**
     * Represents persist relation type.
     */
    public enum RelationType {
        MANY,
        ONE,
        NONE
    }

    private List<Key> keyColumns;
    private List<String> references;

    private final boolean owner;
    private RelationType relationType;
    private final String onDelete;
    private final String onUpdate;

    private Entity assocEntity;


    private Relation(List<Key> keyColumns,
                     List<String> references, String onDelete, String onUpdate, RelationType relationType,
                     Entity assocEntity, boolean owner) {
        this.keyColumns = keyColumns;
        this.references = references;
        this.onDelete = onDelete;
        this.onUpdate = onUpdate;
        this.relationType = relationType;
        this.assocEntity = assocEntity;
        this.owner = owner;
    }

    public List<Key> getKeyColumns() {
        return keyColumns;
    }

    public List<String> getReferences() {
        return references;
    }

    public String getOnDelete() {
        return onDelete;
    }

    public String getOnUpdate() {
        return onUpdate;
    }

    public boolean isOwner() {
        return owner;
    }

    public RelationType getRelationType() {
        return relationType;
    }

    public void setKeyColumns(List<Key> keyColumns) {
        this.keyColumns = keyColumns;
    }

    public void setReferences(List<String> references) {
        this.references = references;
    }

    public void setRelationType(RelationType relationType) {
        this.relationType = relationType;
    }

    public Entity getAssocEntity() {
        return assocEntity;
    }

    public void setAssocEntity(Entity assocEntity) {
        this.assocEntity = assocEntity;
    }

    public static Relation.Builder newBuilder() {
        return new Relation.Builder();
    }

    /**
     * Entity Field Relation Definition.Builder.
     */
    public static class Builder {
        List<Key> keys = null;
        List<String> references = null;
        String onDeleteAction;
        String onUpdateAction;
        Entity assocEntity = null;

        RelationType relationType = RelationType.ONE;

        boolean owner;

        public void setKeys(List<Key> keys) {
            this.keys = keys;
        }

        public void setReferences(List<String> references) {
            this.references = references;
        }

        public void setOnDeleteAction(String onDeleteAction) {
            this.onDeleteAction = onDeleteAction;
        }

        public void setOnUpdateAction(String onUpdateAction) {
            this.onUpdateAction = onUpdateAction;
        }

        public void setOwner(boolean owner) {
            this.owner = owner;
        }

        public void setAssocEntity(Entity assocEntity) {
            this.assocEntity = assocEntity;
        }

        public void setRelationType(RelationType relationType) {
            this.relationType = relationType;
        }

        public Relation build() {
            return new Relation(keys, references, onDeleteAction, onUpdateAction, relationType, assocEntity, owner);
        }
    }

    /**
     * Entity Field Relation Key Definition.
     */
    public static class Key {
        private final String field;
        private final String reference;
        private final String type;

        public Key(String field, String reference, String type) {
            this.field = field;
            this.reference = reference;
            this.type = type;
        }

        public String getField() {
            return field;
        }

        public String getReference() {
            return reference;
        }

        public String getType() {
            return type;
        }
    }
}
