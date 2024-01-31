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

import io.ballerina.compiler.syntax.tree.AnnotationNode;
import io.ballerina.compiler.syntax.tree.NodeList;

import static io.ballerina.persist.nodegenerator.syntax.constants.BalSyntaxConstants.COLON;

/**
 * Client Entity fieldMetaData class.
 *
 * @since 0.1.0
 *
 */
public class EntityField {
    private final String fieldName;
    private final String fieldResourceName;
    private final String fieldType;
    private SQLType sqlType;
    private final boolean arrayType;
    private final boolean optionalType;
    private Relation relation;
    private Enum enumValue;
    private final NodeList<AnnotationNode> annotationNodes;

    EntityField(String fieldName, String fieldType, boolean arrayType, boolean optionalType,
                        NodeList<AnnotationNode> annotationNodes) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.arrayType = arrayType;
        this.optionalType = optionalType;
        this.annotationNodes = annotationNodes;
        this.fieldResourceName = "";
    }

    EntityField(String fieldName, String fieldResourceName, String fieldType, boolean arrayType, boolean optionalType,
                NodeList<AnnotationNode> annotationNodes, SQLType sqlType) {
        this.fieldName = fieldName;
        this.fieldResourceName = fieldResourceName;
        this.fieldType = fieldType;
        this.arrayType = arrayType;
        this.optionalType = optionalType;
        this.annotationNodes = annotationNodes;
        this.sqlType = sqlType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldResourceName() {
        return fieldResourceName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public Relation getRelation() {
        return relation;
    }

    public SQLType getSqlType() {
        return sqlType;
    }

    public NodeList<AnnotationNode> getAnnotation() {
        return annotationNodes;
    }

    public void setRelation(Relation relation) {
        this.relation = relation;
    }

    public void setEnum(Enum enumValue) {
        this.enumValue = enumValue;
    }

    public Enum getEnum() {
        return enumValue;
    }

    public boolean isArrayType() {
        return arrayType;
    }

    public boolean isOptionalType() {
        return optionalType;
    }

    public static EntityField.Builder newBuilder(String fieldName) {
        return new EntityField.Builder(fieldName);
    }

    /**
     * Entity Field Definition.Builder.
     */
    public static class Builder {
        String fieldName;
        String resourceFieldName;
        String fieldType;

        boolean arrayType = false;
        boolean optionalType = false;

        SQLType sqlType;
        private NodeList<AnnotationNode> annotationNodes = null;

        Builder(String fieldName) {
            this.fieldName = fieldName;
        }

        public void setType(String fieldType) {
            if (fieldType.contains(COLON) && !fieldType.startsWith("time:")) {
                fieldType = fieldType.split(COLON, 2)[1];
            }
            this.fieldType = fieldType;
        }

        public void setSqlType(SQLType sqlType) {
            this.sqlType = sqlType;
        }
        public void setResourceFieldName(String resourceFieldName) {
            this.resourceFieldName = resourceFieldName;
        }


        public void setArrayType(boolean arrayType) {
            this.arrayType = arrayType;
        }

        public void setOptionalType(boolean optionalType) {
            this.optionalType = optionalType;
        }
        public void setAnnotation(NodeList<AnnotationNode> annotationNodes) {
            this.annotationNodes = annotationNodes;
        }

        public EntityField build() {
            if (sqlType != null) {
                return new EntityField
                        (fieldName, resourceFieldName, fieldType, arrayType, optionalType, annotationNodes, sqlType);
            }
            return new EntityField(fieldName, fieldType, arrayType, optionalType, annotationNodes);
        }
    }
}
