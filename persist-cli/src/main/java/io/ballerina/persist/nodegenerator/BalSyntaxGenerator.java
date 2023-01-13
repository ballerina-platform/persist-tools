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

package io.ballerina.persist.nodegenerator;

import io.ballerina.compiler.syntax.tree.AbstractNodeFactory;
import io.ballerina.compiler.syntax.tree.AnnotationNode;
import io.ballerina.compiler.syntax.tree.ArrayTypeDescriptorNode;
import io.ballerina.compiler.syntax.tree.BuiltinSimpleNameReferenceNode;
import io.ballerina.compiler.syntax.tree.ExpressionNode;
import io.ballerina.compiler.syntax.tree.IdentifierToken;
import io.ballerina.compiler.syntax.tree.ImportDeclarationNode;
import io.ballerina.compiler.syntax.tree.ImportOrgNameNode;
import io.ballerina.compiler.syntax.tree.ImportPrefixNode;
import io.ballerina.compiler.syntax.tree.ListConstructorExpressionNode;
import io.ballerina.compiler.syntax.tree.MappingConstructorExpressionNode;
import io.ballerina.compiler.syntax.tree.MappingFieldNode;
import io.ballerina.compiler.syntax.tree.MetadataNode;
import io.ballerina.compiler.syntax.tree.MinutiaeList;
import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;
import io.ballerina.compiler.syntax.tree.ModulePartNode;
import io.ballerina.compiler.syntax.tree.Node;
import io.ballerina.compiler.syntax.tree.NodeFactory;
import io.ballerina.compiler.syntax.tree.NodeList;
import io.ballerina.compiler.syntax.tree.NodeParser;
import io.ballerina.compiler.syntax.tree.QualifiedNameReferenceNode;
import io.ballerina.compiler.syntax.tree.RecordFieldNode;
import io.ballerina.compiler.syntax.tree.RecordFieldWithDefaultValueNode;
import io.ballerina.compiler.syntax.tree.RecordTypeDescriptorNode;
import io.ballerina.compiler.syntax.tree.SeparatedNodeList;
import io.ballerina.compiler.syntax.tree.SimpleNameReferenceNode;
import io.ballerina.compiler.syntax.tree.SpecificFieldNode;
import io.ballerina.compiler.syntax.tree.SyntaxKind;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.compiler.syntax.tree.Token;
import io.ballerina.compiler.syntax.tree.TypeDefinitionNode;
import io.ballerina.compiler.syntax.tree.TypeDescriptorNode;
import io.ballerina.persist.BalException;
import io.ballerina.persist.PersistToolsConstants;
import io.ballerina.persist.components.Class;
import io.ballerina.persist.components.Enum;
import io.ballerina.persist.components.Function;
import io.ballerina.persist.components.IfElse;
import io.ballerina.persist.components.TypeDescriptor;
import io.ballerina.persist.models.AutoIncrement;
import io.ballerina.persist.models.Entity;
import io.ballerina.persist.models.EntityField;
import io.ballerina.persist.models.Module;
import io.ballerina.persist.models.Relation;
import io.ballerina.tools.text.TextDocument;
import io.ballerina.tools.text.TextDocuments;
import org.ballerinalang.formatter.core.Formatter;
import org.ballerinalang.formatter.core.FormatterException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.ballerina.persist.PersistToolsConstants.ON_DELETE;
import static io.ballerina.persist.PersistToolsConstants.ON_UPDATE;
import static io.ballerina.persist.PersistToolsConstants.UNIQUE_CONSTRAINTS;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.ANYDATASTREAM_IS_STREAM_TYPE;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.ANYDATA_KEYWORD;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.ANYDATA_STREAM_NEXT;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.ANYDATA_STREAM_STATEMENT;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.APOSTROPHE;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.ARRAY_TYPE;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.AUTO_GENERATED_COMMENT;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.AUTO_GENERATED_COMMENT_WITH_REASON;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.CAST_ANYDATA_STREAM;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.CHECK_EXISTENCE;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.CHECK_RESULT;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.CHECK_UPDATE_STATEMENT;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.COLON;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.COMMA_SPACE;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.CREATE_CLIENT;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.DB_CLIENT_IS_DB_CLIENT;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.DEFILE_INCLUDE_MANY;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.DEFINE_PERSIST_CLIENTS_MANY;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.DOUBLE_QUOTE;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.EMPTY_STRING;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.END_RECORD;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.ENTITY_RELATIONS_ARRAY;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.ENUM_NAME;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.ERR_IS_ERROR;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.EXIST_CHECK_INVALID;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.EXIST_READ_BY_KEY;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.FIELD_ACCESSS;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.GET_ENTITY_CLIENT;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.GET_ENTITY_RECORD;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.GET_MANY_RELATIONS;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.GET_NEW_CLIENT;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.INCLUDE;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.INIT_INCLUDE_MANY;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.INIT_PERSIST_CLIENT_MANY;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.IS_SQL_ERROR;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.KEY;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.KEYWORD_AUTOINCREMENT;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.KEYWORD_BALLERINAX;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.KEYWORD_BOOLEAN;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.KEYWORD_CLIENT_CLASS;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.KEYWORD_ENTITY;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.KEYWORD_ENTITY_NAME;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.KEYWORD_ERR;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.KEYWORD_KEYFIELDS;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.KEYWORD_PARAMETERIZED_QUERY;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.KEYWORD_PERSIST_CLIENT;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.KEYWORD_PERSIST_SQL_CLIENT;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.KEYWORD_REFERENCE;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.KEYWORD_RELATION;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.KEYWORD_SQL;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.KEYWORD_SQL_CLIENT;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.KEYWORD_STREAM;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.KEYWORD_TABLE_NAME;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.KEYWORD_VALUE;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.KEY_COLUMNS;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.MYSQL_DRIVER;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.NOT_EXIST;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.NULLABLE_ANYDATA_STREAM_TYPE;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.NULLABLE_ERROR_STATEMENT;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.PERSIST_ERROR;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.PERSIST_MODULE;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.READ_BY_KEY_RETURN;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.READ_BY_KEY_RETURN_RELATION;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.RECORD_CHECK;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.RESULT_IS_ERROR;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.RETURN_CASTED_ERROR;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.RETURN_FALSE;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.RETURN_NILL;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.RETURN_PERSIST_ERROR_CLOSE_STREAM;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.RETURN_PERSIST_ERROR_FROM_DBCLIENT;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.RETURN_RESULT;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.RETURN_TRUE;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.RETURN_VAUE;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.SELF_ERR;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.SEMICOLON;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.SINGLE_QUOTE;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.SPACE;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.SPECIFIC_ERROR;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.START_RECORD;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.VALUE;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.VALUE_TYPE_CHECK;
import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.VAR_ENTITY_RELATION;
import static io.ballerina.persist.nodegenerator.SyntaxTokenConstants.SYNTAX_TREE_SEMICOLON;

/**
 * Class containing methods to create and read ballerina files as syntax trees.
 *
 * @since 0.1.0
 */
public class BalSyntaxGenerator {

    private BalSyntaxGenerator() {
    }

    /**
     * method to read ballerina files.
     */
    public static void populateEntities(Module.Builder moduleBuilder, SyntaxTree balSyntaxTree) throws IOException,
            BalException {
        ModulePartNode rootNote = balSyntaxTree.rootNode();
        NodeList<ModuleMemberDeclarationNode> nodeList = rootNote.members();
        Entity.Builder entityBuilder = null;
        for (ModuleMemberDeclarationNode moduleNode : nodeList) {
            if (moduleNode.kind() != SyntaxKind.TYPE_DEFINITION || ((TypeDefinitionNode) moduleNode)
                    .metadata().isEmpty()) {
                continue;
            }
            TypeDefinitionNode typeDefinitionNode = (TypeDefinitionNode) moduleNode;
            if (typeDefinitionNode.metadata().isEmpty()) {
                continue;
            }
            for (AnnotationNode annotationNode : typeDefinitionNode.metadata().get().annotations()) {
                Node annotReference = annotationNode.annotReference();
                if (annotReference.kind() != SyntaxKind.QUALIFIED_NAME_REFERENCE) {
                    continue;
                }
                QualifiedNameReferenceNode qualifiedNameRef = (QualifiedNameReferenceNode) annotReference;
                if (qualifiedNameRef.identifier().text().equals(KEYWORD_ENTITY) && qualifiedNameRef
                        .modulePrefix().text().equals(PERSIST_MODULE) && annotationNode.annotValue()
                        .isPresent()) {
                    entityBuilder = Entity.newBuilder(typeDefinitionNode.typeName().text().trim());
                    entityBuilder.setDeclarationNode(moduleNode);
                    for (MappingFieldNode fieldNode : annotationNode.annotValue().get().fields()) {
                        if (fieldNode.kind() != SyntaxKind.SPECIFIC_FIELD) {
                            continue;
                        }
                        SpecificFieldNode specificField = (SpecificFieldNode) fieldNode;
                        if (specificField.fieldName().kind() != SyntaxKind.IDENTIFIER_TOKEN ||
                                specificField.valueExpr().isEmpty()) {
                            continue;
                        }
                        ExpressionNode valueNode = specificField.valueExpr().get();
                        if (((Token) specificField.fieldName()).text().equals(KEY)) {
                            List<String> keyArray = ((ListConstructorExpressionNode) valueNode)
                                    .expressions().stream().map(node -> node.toSourceCode().trim().replaceAll(
                                            DOUBLE_QUOTE, EMPTY_STRING)).collect(Collectors.toList());
                            entityBuilder.setKeys(keyArray);
                        } else if (((Token) specificField.fieldName()).text().equals(UNIQUE_CONSTRAINTS)) {
                            for (Node node : ((ListConstructorExpressionNode) valueNode).expressions()) {
                                List<String> keyArray = ((ListConstructorExpressionNode) node)
                                        .expressions().stream().map(uniqueNode ->
                                                uniqueNode.toSourceCode().trim().replaceAll(
                                                        DOUBLE_QUOTE, EMPTY_STRING)).collect(Collectors.toList());
                                entityBuilder.addUniqueKeys(keyArray);
                            }
                        }
                    }
                }
            }
            // If the record is not an entity, no need to process further
            if (entityBuilder == null) {
                continue;
            }

            RecordTypeDescriptorNode recordDesc = (RecordTypeDescriptorNode) ((TypeDefinitionNode) moduleNode)
                    .typeDescriptor();
            for (Node node : recordDesc.fields()) {
                EntityField.Builder fieldBuilder;
                if (node.kind() == SyntaxKind.RECORD_FIELD_WITH_DEFAULT_VALUE) {
                    RecordFieldWithDefaultValueNode fieldNode = (RecordFieldWithDefaultValueNode) node;
                    fieldBuilder = EntityField.newBuilder(fieldNode.fieldName().text().trim());
                    String fType;
                    TypeDescriptorNode type;
                    if (fieldNode.typeName().kind().equals(SyntaxKind.ARRAY_TYPE_DESC)) {
                        type = ((ArrayTypeDescriptorNode) fieldNode.typeName()).memberTypeDesc();
                        fieldBuilder.setArrayType(true);
                    } else {
                        type = (TypeDescriptorNode) fieldNode.typeName();
                    }
                    fType = getType(type, fieldNode.fieldName().text().trim());
                    fieldBuilder.setType(fType);
                    Optional<MetadataNode> metadata = fieldNode.metadata();
                    if (metadata.isPresent()) {
                        MetadataNode metadataNode = metadata.get();
                        processFieldAnnotations(fieldBuilder, metadataNode);
                    }
                    entityBuilder.addField(fieldBuilder.build());
                } else if (node.kind() == SyntaxKind.RECORD_FIELD) {
                    RecordFieldNode fieldNode = (RecordFieldNode) node;
                    fieldBuilder = EntityField.newBuilder(fieldNode.fieldName().text().trim());
                    String fType;
                    TypeDescriptorNode type;
                    if (fieldNode.typeName().kind().equals(SyntaxKind.ARRAY_TYPE_DESC)) {
                        type = ((ArrayTypeDescriptorNode) fieldNode.typeName()).memberTypeDesc();
                        fieldBuilder.setArrayType(true);
                    } else {
                        type = (TypeDescriptorNode) fieldNode.typeName();
                    }
                    fType = getType(type, fieldNode.fieldName().text().trim());
                    fieldBuilder.setType(fType);
                    RecordFieldNode recordFieldNode = (RecordFieldNode) node;
                    if (recordFieldNode.metadata().isPresent()) {
                        MetadataNode metadataNode = recordFieldNode.metadata().get();
                        processFieldAnnotations(fieldBuilder, metadataNode);
                    }
                    entityBuilder.addField(fieldBuilder.build());
                }
            }
            Entity entity = entityBuilder.build();
            moduleBuilder.addEntity(entity.getEntityName(), entity);
        }
    }

    private static String getType(TypeDescriptorNode typeDesc, String fieldName) throws BalException {
        switch (typeDesc.kind()) {
            case INT_TYPE_DESC:
            case BOOLEAN_TYPE_DESC:
            case DECIMAL_TYPE_DESC:
            case FLOAT_TYPE_DESC:
            case STRING_TYPE_DESC:
            case BYTE_TYPE_DESC:
                return ((BuiltinSimpleNameReferenceNode) typeDesc).name().text();
            case QUALIFIED_NAME_REFERENCE:
                QualifiedNameReferenceNode qualifiedName = (QualifiedNameReferenceNode) typeDesc;
                String modulePrefix = qualifiedName.modulePrefix().text();
                String identifier = qualifiedName.identifier().text();
                return modulePrefix + COLON + identifier;
            case SIMPLE_NAME_REFERENCE:
                return ((SimpleNameReferenceNode) typeDesc).name().text();
            default:
                throw new BalException(String.format("Unsupported data type found for the field `%s`", fieldName));
        }
    }

    private static void processFieldAnnotations(EntityField.Builder fieldBuilder, MetadataNode metadataNode) {
        NodeList<AnnotationNode> annotations = metadataNode.annotations();
        for (AnnotationNode annotation : annotations) {
            Node annotReference = annotation.annotReference();
            if (annotReference.kind() != SyntaxKind.QUALIFIED_NAME_REFERENCE) {
                continue;
            }
            QualifiedNameReferenceNode qualifiedNameRef = (QualifiedNameReferenceNode) annotReference;
            if (qualifiedNameRef.identifier().text().equals(KEYWORD_AUTOINCREMENT) && qualifiedNameRef
                    .modulePrefix().text().equals(PERSIST_MODULE)) {
                AutoIncrement.Builder aiBuilder = AutoIncrement.newBuilder();
                Optional<MappingConstructorExpressionNode> annotationFields = annotation.annotValue();
                if (annotationFields.isPresent()) {
                    for (MappingFieldNode mappingFieldNode : annotationFields.get().fields()) {
                        SpecificFieldNode specificFieldNode = (SpecificFieldNode) mappingFieldNode;
                        if (specificFieldNode.fieldName().toSourceCode().trim().
                                equals(PersistToolsConstants.START_VALUE)) {
                            Optional<ExpressionNode> valueExpr = specificFieldNode.valueExpr();
                            valueExpr.ifPresent(expressionNode ->
                                    aiBuilder.setStartValue(Integer.parseInt(expressionNode.toSourceCode().trim())));
                        }
                        if (specificFieldNode.fieldName().toSourceCode().trim().
                                equals(PersistToolsConstants.INCREMENT)) {
                            Optional<ExpressionNode> valueExpr = specificFieldNode.valueExpr();
                            valueExpr.ifPresent(expressionNode ->
                                    aiBuilder.setInterval(Integer.parseInt(expressionNode.toSourceCode().trim())));
                        }
                    }
                }
                fieldBuilder.setAutoGenerated(aiBuilder.build());
            } else if (qualifiedNameRef.identifier().text().trim().equals(PersistToolsConstants.STRING) &&
                    qualifiedNameRef.modulePrefix().text().trim().equals(PersistToolsConstants.CONSTRAINT)) {
                Optional<MappingConstructorExpressionNode> annotationFields = annotation.annotValue();
                if (annotationFields.isPresent()) {
                    for (MappingFieldNode mappingFieldNode : annotationFields.get().fields()) {
                        SpecificFieldNode specificFieldNode = (SpecificFieldNode) mappingFieldNode;
                        String fieldName = specificFieldNode.fieldName().toSourceCode().trim();
                        if (fieldName.equals(PersistToolsConstants.MAX_LENGTH)) {
                            Optional<ExpressionNode> valueExpr = specificFieldNode.valueExpr();
                            valueExpr.ifPresent(expressionNode ->
                                    fieldBuilder.setMaxLength(
                                            Integer.parseInt(expressionNode.toSourceCode().trim())));
                        } else if (fieldName.equals(PersistToolsConstants.LENGTH)) {
                            Optional<ExpressionNode> valueExpr = specificFieldNode.valueExpr();
                            if (valueExpr.isPresent()) {
                                valueExpr.ifPresent(expressionNode -> fieldBuilder.setMaxLength(
                                        Integer.parseInt(expressionNode.toSourceCode().trim())));
                            }
                        }
                    }
                }
            } else if (qualifiedNameRef.identifier().text().equals(KEYWORD_RELATION) && qualifiedNameRef.modulePrefix()
                    .text().equals(PERSIST_MODULE)) {
                Relation.Builder relationBuilder = Relation.newBuilder();
                if (annotation.annotValue().isPresent()) {
                    for (MappingFieldNode fieldNode : annotation.annotValue().get().fields()) {
                        if (fieldNode.kind() != SyntaxKind.SPECIFIC_FIELD) {
                            continue;
                        }
                        SpecificFieldNode specificField = (SpecificFieldNode) fieldNode;
                        if (specificField.fieldName().kind() != SyntaxKind.IDENTIFIER_TOKEN ||
                                specificField.valueExpr().isEmpty()) {
                            continue;
                        }
                        ExpressionNode valueNode = specificField.valueExpr().get();
                        String fieldName = ((SpecificFieldNode) fieldNode).fieldName().toString().trim();
                        switch (fieldName) {
                            case KEY_COLUMNS: {
                                List<String> keys = ((ListConstructorExpressionNode) valueNode)
                                        .expressions().stream().map(node -> node.toSourceCode().replaceAll(
                                                DOUBLE_QUOTE, EMPTY_STRING)).collect(Collectors.toList());
                                relationBuilder.setKeys(keys);
                                break;
                            }
                            case KEYWORD_REFERENCE: {
                                List<String> references = ((ListConstructorExpressionNode) valueNode)
                                        .expressions().stream().map(node -> node.toSourceCode().replaceAll(
                                                DOUBLE_QUOTE, EMPTY_STRING)).collect(Collectors.toList());
                                relationBuilder.setReferences(references);
                                break;
                            }
                            case ON_DELETE:
                                relationBuilder.setOnDeleteAction(valueNode.toSourceCode());
                                break;
                            case ON_UPDATE:
                                relationBuilder.setOnUpdateAction(valueNode.toSourceCode());
                                break;
                            default:
                                // Do nothing.
                        }
                    }
                }
                relationBuilder.setOwner(true);
                fieldBuilder.setRelation(relationBuilder.build());
            }
        }
    }

    public static void inferRelationDetails(Module entityModule) {
        Map<String, Entity> entityMap = entityModule.getEntityMap();
        for (Entity entity : entityMap.values()) {
            List<EntityField> fields = entity.getFields();
            fields.stream().filter(field -> entityMap.get(field.getFieldType()) != null)
                    .forEach(field -> {
                        String fieldType = field.getFieldType();
                        Entity assocEntity = entityMap.get(fieldType);
                        if (field.getRelation() == null) {
                            // this branch only handles one-to-many or many-to-many with no relation annotations
                            assocEntity.getFields().stream().filter(assocfield -> assocfield.getFieldType()
                                            .equals(entity.getEntityName()))
                                    .filter(assocfield -> assocfield.getRelation() == null).forEach(assocfield -> {
                                        // one-to-many or many-to-many with no relation annotations
                                        if (!field.isArrayType() && !assocfield.isArrayType()) {
                                            throw new RuntimeException("Couldn't find the relation owner between " +
                                                    entity.getEntityName() + " and " + assocEntity.getEntityName());
                                        }
                                        field.setRelation(computeRelation(field, entity, assocEntity));
                                        assocfield.setRelation(computeRelation(assocfield, assocEntity, entity));
                                    });
                        } else if (field.getRelation() != null && field.getRelation().isOwner()) {
                            field.getRelation().setRelationType(field.isArrayType() ?
                                    Relation.RelationType.MANY : Relation.RelationType.ONE);
                            field.getRelation().setAssocEntity(assocEntity);
                            List<String> keyColumns = field.getRelation().getKeyColumns();
                            if (keyColumns == null || keyColumns.size() == 0) {
                                keyColumns = assocEntity.getKeys().stream().map(key ->
                                        assocEntity.getEntityName().toLowerCase(Locale.getDefault())
                                                + key.substring(0, 1).toUpperCase(Locale.getDefault())
                                                + key.substring(1)).collect(Collectors.toList());
                                field.getRelation().setKeyColumns(keyColumns);
                            }
                            List<String> references = field.getRelation().getReferences();
                            if (references == null || references.size() == 0) {
                                field.getRelation().setReferences(assocEntity.getKeys());
                            }

                            // create bidirectional mapping for associated entity
                            Relation.Builder assocRelBuilder = Relation.newBuilder();
                            assocRelBuilder.setOwner(false);
                            assocRelBuilder.setAssocEntity(entity);
                            assocRelBuilder.setKeys(assocEntity.getKeys());
                            assocRelBuilder.setReferences(keyColumns);
                            assocEntity.getFields().stream().filter(assocfield -> assocfield.getFieldType()
                                    .equals(entity.getEntityName())).forEach(
                                    assocField -> {
                                        assocRelBuilder.setRelationType(assocField.isArrayType() ?
                                                Relation.RelationType.MANY : Relation.RelationType.ONE);
                                        assocField.setRelation(assocRelBuilder.build());
                                    });
                        }
                    });
        }
    }

    private static Relation computeRelation(EntityField field, Entity entity, Entity assocEntity) {
        Relation.Builder relBuilder = new Relation.Builder();
        relBuilder.setAssocEntity(assocEntity);
        if (field.isArrayType()) {
            List<String> refColumns = entity.getKeys().stream().map(key ->
                    entity.getEntityName().toLowerCase(Locale.getDefault())
                            + key.substring(0, 1).toUpperCase(Locale.getDefault())
                            + key.substring(1)).collect(Collectors.toList());
            relBuilder.setOwner(false);
            relBuilder.setRelationType(Relation.RelationType.MANY);
            relBuilder.setKeys(entity.getKeys());
            relBuilder.setReferences(refColumns);
        } else {
            List<String> keyColumns = assocEntity.getKeys().stream().map(key ->
                    assocEntity.getEntityName().toLowerCase(Locale.getDefault())
                            + key.substring(0, 1).toUpperCase(Locale.getDefault())
                            + key.substring(1)).collect(Collectors.toList());
            relBuilder.setOwner(true);
            relBuilder.setRelationType(Relation.RelationType.ONE);
            relBuilder.setKeys(keyColumns);
            relBuilder.setReferences(assocEntity.getKeys());
        }
        return relBuilder.build();
    }

    public static SyntaxTree generateClientSyntaxTree(Entity entity, ArrayList<ImportDeclarationNode> importsArray) {
        boolean keyAutoInc = false;
        Enum relationsEnum = null;
        HashMap<String, String> keys = new HashMap<>();
        String keyType = BalSyntaxConstants.KEYWORD_INT;
        NodeList<ImportDeclarationNode> imports = AbstractNodeFactory.createEmptyNodeList();
        NodeList<ModuleMemberDeclarationNode> moduleMembers = AbstractNodeFactory.createEmptyNodeList();
        List<Node> subFields = new ArrayList<>();
        List<Node> joinSubFields = new ArrayList<>();
        boolean timeImport = false;
        boolean inclusions = false;
        boolean manyRelation = false;
        for (EntityField field : entity.getFields()) {
            if (field.getFieldType().contains(BalSyntaxConstants.KEYWORD_TIME)) {
                timeImport = true;
            }

            for (String key : entity.getKeys()) {
                if (field.getFieldName().equals(key)) {
                    keys.put(field.getFieldName(), field.getFieldType());
                }
            }

            if (entity.getKeys().size() == 1) {
                if (field.getFieldName().equals(entity.getKeys().get(0))) {
                    keyType = field.getFieldType();
                }
            }
            if (field.getRelation() == null) {
                // process non-relational fields in the entity
                if (!subFields.isEmpty()) {
                    subFields.add(NodeFactory.createBasicLiteralNode(SyntaxKind.STRING_LITERAL,
                            AbstractNodeFactory.createLiteralValueToken(SyntaxKind.STRING_LITERAL, COMMA_SPACE
                                            + System.lineSeparator(), NodeFactory.createEmptyMinutiaeList(),
                                    NodeFactory.createEmptyMinutiaeList())));
                }

                if (field.getAutoGenerated() != null) {
                    subFields.add(NodeFactory.createSpecificFieldNode(null,
                            AbstractNodeFactory.createIdentifierToken(field.getFieldName()),
                            SyntaxTokenConstants.SYNTAX_TREE_COLON, NodeParser.parseExpression(String.format(
                                    BalSyntaxConstants.FIELD_FORMAT_WITH_AUTO_G,
                                    field.getFieldName().trim().replaceAll(
                                            SINGLE_QUOTE, EMPTY_STRING),
                                    field.getFieldType().trim().replaceAll(SPACE,
                                            EMPTY_STRING),
                                    String.valueOf(field.getAutoGenerated() != null).trim()))));
                    for (String key : entity.getKeys()) {
                        if (field.getFieldName().equals(key)) {
                            keyAutoInc = true;
                            break;
                        }
                    }
                } else {
                    subFields.add(NodeFactory.createSpecificFieldNode(null,
                            AbstractNodeFactory.createIdentifierToken(field.getFieldName()),
                            SyntaxTokenConstants.SYNTAX_TREE_COLON,
                            NodeParser.parseExpression(String.format(BalSyntaxConstants.FIELD_FORMAT_WITHOUT_AUTO_G,
                                    field.getFieldName().trim().replaceAll(
                                            SINGLE_QUOTE, EMPTY_STRING),
                                    field.getFieldType().trim().replaceAll(SPACE,
                                            EMPTY_STRING)
                            ))));
                }
            } else {
                // process relational fields in the entity
                if (relationsEnum == null) {
                    relationsEnum = new Enum(String.format(ENUM_NAME, entity.getEntityName()));
                }
                Relation relation = field.getRelation();
                StringBuilder sReferenceKeys = new StringBuilder();
                StringBuilder sForeignKeys = new StringBuilder();

                for (int i = 0; i < relation.getKeyColumns().size(); i++) {
                    if (sReferenceKeys.length() != 0) {
                        sReferenceKeys.append(COMMA_SPACE);
                        sForeignKeys.append(COMMA_SPACE);
                    }
                    sReferenceKeys.append(relation.getReferences().get(i));
                    sForeignKeys.append(relation.getKeyColumns().get(i));
                }

                if (!joinSubFields.isEmpty()) {
                    joinSubFields.add(NodeFactory.createBasicLiteralNode(SyntaxKind.STRING_LITERAL,
                            AbstractNodeFactory.createLiteralValueToken(SyntaxKind.STRING_LITERAL, COMMA_SPACE
                                            + System.lineSeparator(), NodeFactory.createEmptyMinutiaeList(),
                                    NodeFactory.createEmptyMinutiaeList())));
                }

                if (relation.getRelationType() == Relation.RelationType.ONE) {
                    joinSubFields.add(NodeFactory.createSpecificFieldNode(null,
                            AbstractNodeFactory.createIdentifierToken(field.getFieldName()),
                            SyntaxTokenConstants.SYNTAX_TREE_COLON,
                            NodeParser.parseExpression(String.format(BalSyntaxConstants.FIELD_FORMAT_JOIN_FIELD,
                                    field.getFieldType(), field.getFieldName(),
                                    relation.getAssocEntity().getTableName(), sReferenceKeys, sForeignKeys))));
                } else {
                    joinSubFields.add(NodeFactory.createSpecificFieldNode(null,
                            AbstractNodeFactory.createIdentifierToken(field.getFieldName()),
                            SyntaxTokenConstants.SYNTAX_TREE_COLON,
                            NodeParser.parseExpression(String.format(BalSyntaxConstants.FIELD_FORMAT_JOIN_FIELD_MANY,
                                    field.getFieldType(), field.getFieldName(),
                                    relation.getAssocEntity().getTableName(), sReferenceKeys, sForeignKeys))));
                }


                if (relation.isOwner()) {
                    relationsEnum.addMember(NodeParser.parseExpression(field.getFieldName()));
                    inclusions = true;
                    for (EntityField entityField : relation.getAssocEntity().getFields()) {
                        if (entityField.getRelation() != null) {
                            continue;
                        }
                        if (entityField.getFieldType().contains(BalSyntaxConstants.KEYWORD_TIME)) {
                            timeImport = true;
                        }
                        if (!subFields.isEmpty()) {
                            subFields.add(NodeFactory.createBasicLiteralNode(SyntaxKind.STRING_LITERAL,
                                    AbstractNodeFactory.createLiteralValueToken(SyntaxKind.STRING_LITERAL,
                                            COMMA_SPACE + System.lineSeparator(),
                                            NodeFactory.createEmptyMinutiaeList(),
                                            NodeFactory.createEmptyMinutiaeList())));
                        }
                        if (relation.getReferences().contains(entityField.getFieldName())) {
                            int index = relation.getReferences().indexOf(entityField.getFieldName());
                            subFields.add(NodeFactory.createSpecificFieldNode(null,
                                    AbstractNodeFactory.createIdentifierToken(DOUBLE_QUOTE +
                                            field.getFieldName() + FIELD_ACCESSS
                                            + entityField.getFieldName()
                                            + DOUBLE_QUOTE), SyntaxTokenConstants.SYNTAX_TREE_COLON,
                                    NodeParser.parseExpression(String.format(BalSyntaxConstants
                                                    .FIELD_FORMAT_RELATED_PARENT_FIELD, relation.getKeyColumns()
                                                    .get(index), entityField.getFieldType(), field.getFieldName(),
                                            relation.getAssocEntity().getTableName(), entityField.getFieldName()
                                    ))));
                        } else {
                            subFields.add(NodeFactory.createSpecificFieldNode(null,
                                    AbstractNodeFactory.createIdentifierToken(DOUBLE_QUOTE +
                                            field.getFieldName() + FIELD_ACCESSS
                                            + entityField.getFieldName()
                                            + DOUBLE_QUOTE), SyntaxTokenConstants.SYNTAX_TREE_COLON,
                                    NodeParser.parseExpression(String.format(
                                            BalSyntaxConstants.FIELD_FORMAT_RELATED_PARENT_FIELD_WOUT_COLUMN_NAME,
                                            entityField.getFieldType(), field.getFieldName(),
                                            relation.getAssocEntity().getTableName(),
                                            entityField.getFieldName().replaceAll(SINGLE_QUOTE, EMPTY_STRING)
                                    ))));
                        }
                    }
                } else {
                    relationsEnum.addMember(NodeParser.parseExpression(field.getFieldName()));
                    inclusions = true;
                    for (EntityField entityField : relation.getAssocEntity().getFields()) {
                        if (entityField.getRelation() != null) {
                            continue;
                        }
                        if (entityField.getFieldType().contains(BalSyntaxConstants.KEYWORD_TIME)) {
                            timeImport = true;
                        }
                        if (!subFields.isEmpty()) {
                            subFields.add(NodeFactory.createBasicLiteralNode(SyntaxKind.STRING_LITERAL,
                                    AbstractNodeFactory.createLiteralValueToken(
                                            SyntaxKind.STRING_LITERAL, COMMA_SPACE
                                                    + System.lineSeparator(), NodeFactory.createEmptyMinutiaeList(),
                                            NodeFactory.createEmptyMinutiaeList())));
                        }
                        String fieldAccesssString = FIELD_ACCESSS;
                        if (relation.getRelationType() == Relation.RelationType.MANY) {
                            fieldAccesssString = ARRAY_TYPE + FIELD_ACCESSS;
                            manyRelation = true;
                        }
                        subFields.add(NodeFactory.createSpecificFieldNode(null,
                                AbstractNodeFactory.createIdentifierToken(DOUBLE_QUOTE +
                                        field.getFieldName() + fieldAccesssString + entityField.
                                        getFieldName() +
                                        DOUBLE_QUOTE), SyntaxTokenConstants.SYNTAX_TREE_COLON,
                                NodeParser.parseExpression(String.format(
                                        BalSyntaxConstants.FIELD_FORMAT_RELATED_CHILD_FIELD,
                                        entityField.getFieldType().trim().replaceAll(
                                                SINGLE_QUOTE, EMPTY_STRING),
                                        field.getFieldName(), relation.getAssocEntity().getTableName(),
                                        entityField.getFieldName().replaceAll(SINGLE_QUOTE, EMPTY_STRING)
                                ))));
                    }
                }
            }
        }
        MinutiaeList commentMinutiaeList = crateCommentMinutiaeList(String.format(AUTO_GENERATED_COMMENT_WITH_REASON,
                entity.getEntityName()));
        imports = imports.add(getImportDeclarationNodeWithAutogeneratedComment(BalSyntaxConstants.KEYWORD_BALLERINAX,
                BalSyntaxConstants.KEYWORD_MYSQL, commentMinutiaeList, null));
        imports = imports.add(getImportDeclarationNode(BalSyntaxConstants.KEYWORD_BALLERINA,
                PERSIST_MODULE, null));
        imports = imports.add(getImportDeclarationNode(BalSyntaxConstants.KEYWORD_BALLERINA,
                KEYWORD_SQL, null));

        if (timeImport) {
            if (importsArray.isEmpty()) {
                importsArray.add(getImportDeclarationNode(BalSyntaxConstants.KEYWORD_BALLERINA,
                        BalSyntaxConstants.KEYWORD_TIME, null));
            }
            imports = imports.add(getImportDeclarationNode(BalSyntaxConstants.KEYWORD_BALLERINA,
                    BalSyntaxConstants.KEYWORD_TIME, null));
        }
        Class client = createClientClass(entity, entity.getEntityName(), subFields, joinSubFields,
                keys, keyType, keyAutoInc, inclusions);

        moduleMembers = moduleMembers.add(client.getClassDefinitionNode());

        if (inclusions) {
            moduleMembers = moduleMembers.add(relationsEnum.getEnumDeclarationNode());
        }

        Class clientStream = createClientStreamClass(entity, entity.getEntityName(), manyRelation);

        moduleMembers = moduleMembers.add(clientStream.getClassDefinitionNode());

        Token eofToken = AbstractNodeFactory.createIdentifierToken(EMPTY_STRING);
        ModulePartNode modulePartNode = NodeFactory.createModulePartNode(imports, moduleMembers, eofToken);
        TextDocument textDocument = TextDocuments.from(EMPTY_STRING);
        SyntaxTree balTree = SyntaxTree.from(textDocument);

        return balTree.modifyWith(modulePartNode);
    }


    private static Class createClientClass(Entity entity, String className, List<Node> subFields,
                                           List<Node> joinSubFields, HashMap<String, String> keys,
                                           String keyType, boolean keyAutoInc, boolean inclusions) {
        Class client = new Class(className + KEYWORD_CLIENT_CLASS, true);
        client.addQualifiers(new String[]{BalSyntaxConstants.KEYWORD_CLIENT});
        client.addMember(NodeFactory.createTypeReferenceNode(
                AbstractNodeFactory.createToken(SyntaxKind.ASTERISK_TOKEN),
                NodeFactory.createQualifiedNameReferenceNode(
                        NodeFactory.createIdentifierToken(
                                BalSyntaxConstants.InheritedTypeReferenceConstants.PERSIST_MODULE_NAME),
                        AbstractNodeFactory.createToken(SyntaxKind.COLON_TOKEN),
                        NodeFactory.createIdentifierToken(
                                BalSyntaxConstants.InheritedTypeReferenceConstants.ABSTRACT_PERSIST_CLIENT)
                ),
                AbstractNodeFactory.createToken(SyntaxKind.SEMICOLON_TOKEN)), false);
        client.addMember(NodeFactory.createBasicLiteralNode(SyntaxKind.STRING_LITERAL,
                AbstractNodeFactory.createLiteralValueToken(SyntaxKind.STRING_LITERAL, SPACE,
                        NodeFactory.createEmptyMinutiaeList(), NodeFactory.createEmptyMinutiaeList())), false);
        client.addMember(TypeDescriptor.getObjectFieldNode(BalSyntaxConstants.KEYWORD_PRIVATE,
                        new String[]{BalSyntaxConstants.KEYWORD_FINAL},
                        TypeDescriptor.getBuiltinSimpleNameReferenceNode(BalSyntaxConstants.KEYWORD_STRING),
                        KEYWORD_ENTITY_NAME, NodeFactory.createBasicLiteralNode(SyntaxKind.STRING_LITERAL,
                                AbstractNodeFactory.createLiteralValueToken(SyntaxKind.STRING_LITERAL,
                                        DOUBLE_QUOTE + entity.getEntityName()
                                                + DOUBLE_QUOTE,
                                        NodeFactory.createEmptyMinutiaeList(),
                                        NodeFactory.createEmptyMinutiaeList()))),
                true);
        client.addMember(TypeDescriptor.getObjectFieldNode(BalSyntaxConstants.KEYWORD_PRIVATE,
                        new String[]{BalSyntaxConstants.KEYWORD_FINAL},
                        TypeDescriptor.getQualifiedNameReferenceNode(KEYWORD_SQL, KEYWORD_PARAMETERIZED_QUERY),
                        KEYWORD_TABLE_NAME, NodeFactory.createBasicLiteralNode(SyntaxKind.STRING_LITERAL,
                                AbstractNodeFactory.createLiteralValueToken(SyntaxKind.STRING_LITERAL,
                                        APOSTROPHE + entity.getTableName() + APOSTROPHE,
                                        NodeFactory.createEmptyMinutiaeList(),
                                        NodeFactory.createEmptyMinutiaeList()))),
                false);

        client.addMember(TypeDescriptor.getObjectFieldNode(BalSyntaxConstants.KEYWORD_PRIVATE,
                new String[]{BalSyntaxConstants.KEYWORD_FINAL},
                TypeDescriptor.getSimpleNameReferenceNode(BalSyntaxConstants.TYPE_FIELD_METADATA_MAP),
                BalSyntaxConstants.TAG_FIELD_METADATA, NodeFactory.createMappingConstructorExpressionNode(
                        SyntaxTokenConstants.SYNTAX_TREE_OPEN_BRACE, AbstractNodeFactory
                                .createSeparatedNodeList(subFields),
                        SyntaxTokenConstants.SYNTAX_TREE_CLOSE_BRACE)), true);

        StringBuilder keysString = new StringBuilder();
        for (String key : entity.getKeys()) {
            if (keysString.length() > 0) {
                keysString.append(COMMA_SPACE);
            }
            keysString.append(DOUBLE_QUOTE).append(key).append(DOUBLE_QUOTE);
        }

        client.addMember(TypeDescriptor.getObjectFieldNode(BalSyntaxConstants.KEYWORD_PRIVATE, new String[]{},
                TypeDescriptor.getArrayTypeDescriptorNode(BalSyntaxConstants.KEYWORD_STRING),
                KEYWORD_KEYFIELDS, NodeFactory.createListConstructorExpressionNode(
                        SyntaxTokenConstants.SYNTAX_TREE_OPEN_BRACKET, AbstractNodeFactory
                                .createSeparatedNodeList(NodeFactory.createBasicLiteralNode(SyntaxKind.STRING_LITERAL,
                                        AbstractNodeFactory.createLiteralValueToken(SyntaxKind.STRING_LITERAL,
                                                keysString.toString(), NodeFactory.createEmptyMinutiaeList(),
                                                NodeFactory.createEmptyMinutiaeList())))
                        , SyntaxTokenConstants.SYNTAX_TREE_CLOSE_BRACKET)
        ), false);
        if (!joinSubFields.isEmpty()) {
            client.addMember(TypeDescriptor.getObjectFieldNode(BalSyntaxConstants.KEYWORD_PRIVATE,
                    new String[]{BalSyntaxConstants.KEYWORD_FINAL},
                    TypeDescriptor.getSimpleNameReferenceNode(BalSyntaxConstants.TYPE_JOIN_METADATA_MAP),
                    BalSyntaxConstants.TAG_JOIN_METADATA, NodeFactory.createMappingConstructorExpressionNode(
                            SyntaxTokenConstants.SYNTAX_TREE_OPEN_BRACE, AbstractNodeFactory
                                    .createSeparatedNodeList(joinSubFields),
                            SyntaxTokenConstants.SYNTAX_TREE_CLOSE_BRACE)), true);
        }
        client.addMember(TypeDescriptor.getObjectFieldNodeWithoutExpression(BalSyntaxConstants.KEYWORD_PRIVATE,
                        new String[]{},
                        TypeDescriptor.getQualifiedNameReferenceNode(PERSIST_MODULE, KEYWORD_SQL_CLIENT),
                        BalSyntaxConstants.PERSIST_CLIENT),
                true);

        Function init = getInitMethod(joinSubFields);
        client.addMember(init.getFunctionDefinitionNode(), true);
        Function create = getCreateMethod(entity, joinSubFields, keys, keyAutoInc, keyType);
        client.addMember(create.getFunctionDefinitionNode(), true);
        StringBuilder keyString = new StringBuilder();
        keyString.append(KEY);
        if (keys.size() > 1) {
            keyString = new StringBuilder();
            for (Map.Entry<String, String> entry : keys.entrySet()) {
                keyString.append(keys.get(entry.getKey()));
                keyString.append(SPACE);
                keyString.append(entry.getKey());
                keyString.append(SEMICOLON);
            }
        }
        Function readByKey = getReadByKeyMethod(entity, keys, inclusions, keyType, keyString);
        client.addMember(readByKey.getFunctionDefinitionNode(), true);

        Function read = getReadMethod(entity, inclusions, className);
        client.addMember(read.getFunctionDefinitionNode(), true);

//        Remove advance filter support for phase 1.
//        Function execute = getExecuteMethod(entity, className);
//        client.addMember(execute.getFunctionDefinitionNode(), true);

        Function update = getUpdateMethod(entity, joinSubFields);

        client.addMember(update.getFunctionDefinitionNode(), true);


        Function delete = getDeleteMethod(entity);
        client.addMember(delete.getFunctionDefinitionNode(), true);

        StringBuilder keyStringExist;
        if (keys.size() > 1) {
            keyStringExist = new StringBuilder();
            keyStringExist.append(START_RECORD);
            for (String key : keys.keySet()) {
                if (!keyStringExist.toString().trim().equals(START_RECORD)) {
                    keyStringExist.append(COMMA_SPACE);
                }
                keyStringExist.append(String.format(key));
                keyStringExist.append(COLON);
                keyStringExist.append(SPACE);
                keyStringExist.append(entity.getEntityName().substring(0, 1).toLowerCase(Locale.getDefault()));
                keyStringExist.append(entity.getEntityName().substring(1));
                keyStringExist.append(FIELD_ACCESSS);
                keyStringExist.append(key);
            }
            keyStringExist.append(END_RECORD);

        } else {
            keyStringExist = new StringBuilder();
            keyStringExist.append(entity.getEntityName().substring(0, 1).toLowerCase(Locale.getDefault()));
            keyStringExist.append(entity.getEntityName().substring(1));
            keyStringExist.append(FIELD_ACCESSS);
            for (String key : keys.keySet()) {
                keyStringExist.append(key);
            }
        }
        Function exists = getExistMethod(entity, keyStringExist);
        client.addMember(exists.getFunctionDefinitionNode(), true);
        Function close = getCloseMethod();
        client.addMember(close.getFunctionDefinitionNode(), true);
        return client;
    }

    private static Class createClientStreamClass(Entity entity, String className, boolean hasManyRelation) {
        Class clientStream = new Class(className + KEYWORD_STREAM, true);

        clientStream.addMember(NodeFactory.createBasicLiteralNode(SyntaxKind.STRING_LITERAL,
                AbstractNodeFactory.createLiteralValueToken(SyntaxKind.STRING_LITERAL, SPACE,
                        NodeFactory.createEmptyMinutiaeList(), NodeFactory.createEmptyMinutiaeList())), true);

        clientStream.addMember(NodeParser.parseStatement(ANYDATA_STREAM_STATEMENT), false);
        clientStream.addMember(NodeParser.parseStatement(NULLABLE_ERROR_STATEMENT), false);
        if (hasManyRelation) {
            clientStream.addMember(NodeParser.parseStatement(String.format(DEFILE_INCLUDE_MANY,
                    className)), false);
            clientStream.addMember(NodeParser.parseStatement(DEFINE_PERSIST_CLIENTS_MANY), false);
        }

        Function initStream = new Function(BalSyntaxConstants.INIT);
        initStream.addQualifiers(new String[]{BalSyntaxConstants.KEYWORD_PUBLIC, BalSyntaxConstants.KEYWORD_ISOLATED});
        initStream.addStatement(NodeParser.parseStatement(BalSyntaxConstants.INIT_STREAM_STATEMENT));
        initStream.addStatement(NodeParser.parseStatement(SELF_ERR));
        initStream.addRequiredParameter(NodeParser.parseTypeDescriptor(NULLABLE_ANYDATA_STREAM_TYPE), ANYDATA_KEYWORD);
        initStream.addRequiredParameterWithDefault(TypeDescriptor.getOptionalTypeDescriptorNode(EMPTY_STRING,
                PERSIST_ERROR), KEYWORD_ERR, Function.Bracket.PAREN);
        if (hasManyRelation) {
            initStream.addRequiredParameterWithDefault(TypeDescriptor.getOptionalTypeDescriptorNode(EMPTY_STRING,
                    String.format(VAR_ENTITY_RELATION, className)), INCLUDE, Function.Bracket.PAREN);
            initStream.addRequiredParameterWithDefault(TypeDescriptor.getOptionalTypeDescriptorNode(EMPTY_STRING,
                    KEYWORD_PERSIST_SQL_CLIENT), KEYWORD_PERSIST_CLIENT, Function.Bracket.PAREN);
            initStream.addStatement(NodeParser.parseStatement(INIT_INCLUDE_MANY));
            initStream.addStatement(NodeParser.parseStatement(INIT_PERSIST_CLIENT_MANY));
        }
        clientStream.addMember(initStream.getFunctionDefinitionNode(), true);

        Function nextStream = new Function(BalSyntaxConstants.NEXT);
        nextStream.addQualifiers(new String[]{BalSyntaxConstants.KEYWORD_PUBLIC, BalSyntaxConstants.KEYWORD_ISOLATED});
        nextStream.addReturns(NodeParser.parseTypeDescriptor(String.format(
                BalSyntaxConstants.NEXT_STREAM_RETURN_TYPE, entity.getEntityName())));

        IfElse errorCheck = new IfElse(NodeParser.parseExpression(ERR_IS_ERROR));
        errorCheck.addIfStatement(NodeParser.parseStatement(RETURN_CASTED_ERROR));
        IfElse streamCheck = new IfElse(NodeParser.parseExpression(ANYDATASTREAM_IS_STREAM_TYPE));

        streamCheck.addIfStatement(NodeParser.parseStatement(CAST_ANYDATA_STREAM));
        streamCheck.addIfStatement(NodeParser.parseStatement(ANYDATA_STREAM_NEXT));

        IfElse streamValueNilCheck = new IfElse(NodeParser.parseExpression(
                BalSyntaxConstants.NEXT_STREAM_IF_STATEMENT));
        streamValueNilCheck.addIfStatement(NodeParser.parseStatement(
                BalSyntaxConstants.NEXT_STREAM_RETURN_STREAM_VALUE));
        IfElse streamValueErrorCheck = new IfElse(NodeParser.parseExpression(
                BalSyntaxConstants.NEXT_STREAM_ELSE_IF_STATEMENT));
        streamValueErrorCheck.addIfStatement(NodeParser.parseStatement(
                BalSyntaxConstants.NEXT_STREAM_RETURN_STREAM_VALUE_ERROR));
        streamValueErrorCheck.addElseStatement(NodeParser.parseStatement(String.format(
                BalSyntaxConstants.NEXT_STREAM_ELSE_STATEMENT, entity.getEntityName(), entity.getEntityName())));
        if (hasManyRelation) {
            IfElse clientRelationsCheck = new IfElse(NodeParser.parseExpression(String.format(
                    BalSyntaxConstants.RELATION_ENUM_ARRAY_CHECK, entity.getEntityName())));
            clientRelationsCheck.addIfStatement(NodeParser.parseStatement(String.format(GET_MANY_RELATIONS,
                    className)));
            streamValueErrorCheck.addElseStatement(clientRelationsCheck.getIfElseStatementNode());
        }
        streamValueErrorCheck.addElseStatement(NodeParser.parseStatement(BalSyntaxConstants.RETURN_NEXT_RECORD));
        streamValueNilCheck.addElseBody(streamValueErrorCheck);
        streamCheck.addIfStatement(streamValueNilCheck.getIfElseStatementNode());
        streamCheck.addElseStatement(NodeParser.parseStatement(RETURN_NILL));
        errorCheck.addElseBody(streamCheck);
        nextStream.addIfElseStatement(errorCheck.getIfElseStatementNode());
        clientStream.addMember(nextStream.getFunctionDefinitionNode(), true);

        Function closeStream = new Function(BalSyntaxConstants.CLOSE);
        closeStream.addQualifiers(new String[]{BalSyntaxConstants.KEYWORD_PUBLIC, BalSyntaxConstants.KEYWORD_ISOLATED});
        closeStream.addReturns(TypeDescriptor.getOptionalTypeDescriptorNode(EMPTY_STRING,
                PERSIST_ERROR));
        streamCheck = new IfElse(NodeParser.parseExpression(ANYDATASTREAM_IS_STREAM_TYPE));
        streamCheck.addIfStatement(NodeParser.parseStatement(CAST_ANYDATA_STREAM));
        streamCheck.addIfStatement(NodeParser.parseStatement(BalSyntaxConstants.CLOSE_STREAM_STATEMENT));
        IfElse sqlErrorCheck = new IfElse(NodeParser.parseExpression(IS_SQL_ERROR));
        sqlErrorCheck.addIfStatement(NodeParser.parseStatement(RETURN_PERSIST_ERROR_CLOSE_STREAM));
        streamCheck.addIfStatement(sqlErrorCheck.getIfElseStatementNode());
        closeStream.addIfElseStatement(streamCheck.getIfElseStatementNode());
        clientStream.addMember(closeStream.getFunctionDefinitionNode(), true);
        return clientStream;
    }

    private static Function getInitMethod(List<Node> joinSubFields) {
        Function init = new Function(BalSyntaxConstants.INIT);
        init.addQualifiers(new String[]{BalSyntaxConstants.KEYWORD_PUBLIC});
        init.addReturns(TypeDescriptor.getOptionalTypeDescriptorNode(EMPTY_STRING,
                PERSIST_ERROR));
        init.addStatement(NodeParser.parseStatement(BalSyntaxConstants.INIT_MYSQL_CLIENT));
        IfElse errorCheck = new IfElse(NodeParser.parseExpression(DB_CLIENT_IS_DB_CLIENT));
        errorCheck.addIfStatement(NodeParser.parseStatement(RETURN_PERSIST_ERROR_FROM_DBCLIENT));
        init.addIfElseStatement(errorCheck.getIfElseStatementNode());
        if (!joinSubFields.isEmpty()) {
            init.addStatement(NodeParser.parseStatement(BalSyntaxConstants.INIT_PERSIST_CLIENT_RELATED));
        } else {
            init.addStatement(NodeParser.parseStatement(BalSyntaxConstants.INIT_PERSIST_CLIENT));
        }
        return init;
    }

    private static Function getCreateMethod(Entity entity, List<Node> joinSubFields,
                                            HashMap<String, String> keys, boolean keyAutoInc, String keyType) {
        Function create = new Function(BalSyntaxConstants.CREATE);
        create.addRequiredParameter(
                TypeDescriptor.getSimpleNameReferenceNode(entity.getEntityName()), KEYWORD_VALUE);
        create.addQualifiers(new String[]{BalSyntaxConstants.KEYWORD_REMOTE});
        create.addReturns(TypeDescriptor.getUnionTypeDescriptorNode(
                TypeDescriptor.getSimpleNameReferenceNode(entity.getEntityName()),
                TypeDescriptor.getQualifiedNameReferenceNode(PERSIST_MODULE, SPECIFIC_ERROR)));
        if (joinSubFields.isEmpty()) {
            StringBuilder retRecord = new StringBuilder();
            create.addStatement(NodeParser.parseStatement(BalSyntaxConstants.CREATE_SQL_RESULTS));
            if (keys.size() > 1) {
                for (EntityField entityField : entity.getFields()) {
                    if (retRecord.length() > 0) {
                        retRecord.append(COMMA_SPACE);
                    }
                    if (entityField.getAutoGenerated() != null) {
                        retRecord.append(String.format(BalSyntaxConstants.RECORD_FIELD_LAST_INSERT_ID,
                                entityField.getFieldName(), BalSyntaxConstants.KEYWORD_INT));
                    } else {
                        retRecord.append(String.format(BalSyntaxConstants.RECORD_FIELD_VALUE,
                                entityField.getFieldName(),
                                entityField.getFieldName()));
                    }
                }
                if (keyAutoInc) {
                    create.addStatement(NodeParser.parseStatement(
                            String.format(BalSyntaxConstants.RETURN_RECORD_VARIABLE, retRecord)));
                } else {
                    create.addStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.RETURN_VARIABLE,
                            VALUE)));
                }
            } else {
                for (EntityField entityField : entity.getFields()) {
                    if (retRecord.length() > 0) {
                        retRecord.append(COMMA_SPACE);
                    }
                    if (entityField.getFieldName().equals(entity.getKeys().get(0))) {
                        retRecord.append(String.format(BalSyntaxConstants.RECORD_FIELD_LAST_INSERT_ID,
                                entityField.getFieldName(), keyType));
                    } else {
                        retRecord.append(String.format(BalSyntaxConstants.RECORD_FIELD_VALUE,
                                entityField.getFieldName(),
                                entityField.getFieldName()));
                    }
                }
                if (!keyAutoInc) {
                    IfElse valueNilCheck = new IfElse(NodeParser.parseExpression(
                            BalSyntaxConstants.LAST_RETURN_ID_NULL_CHECK));
                    valueNilCheck.addIfStatement(NodeParser.parseStatement(BalSyntaxConstants.RETURN_VALUE));
                    create.addIfElseStatement(valueNilCheck.getIfElseStatementNode());

                }
                create.addStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.RETURN_RECORD_VARIABLE,
                        retRecord)));
            }
        } else {
            for (EntityField entityField : entity.getFields()) {
                if (entityField.getRelation() != null && entityField.getRelation().isOwner()) {
                    IfElse valueCheck = new IfElse(NodeParser.parseExpression(String.format(VALUE_TYPE_CHECK,
                            entityField.getFieldName(), entityField.getFieldType())));
                    valueCheck.addIfStatement(NodeParser.parseStatement(String.format(GET_NEW_CLIENT,
                            entityField.getFieldType(), entityField.getFieldType().substring(0, 1)
                                    .toLowerCase(Locale.getDefault()) +
                                    entityField.getFieldType().substring(1), entityField.getFieldType())));
                    valueCheck.addIfStatement(NodeParser.parseStatement(String.format(CHECK_EXISTENCE,
                            entityField.getFieldType().substring(0, 1).toLowerCase(Locale.getDefault())
                                    + entityField.getFieldType().substring(1),
                            entityField.getFieldType(), entityField.getFieldName())));
                    IfElse checkExistence = new IfElse(NodeParser.parseExpression(NOT_EXIST));
                    checkExistence.addIfStatement(NodeParser.parseStatement(String.format(CREATE_CLIENT,
                            entityField.getFieldName(), entityField.getFieldType().substring(0, 1)
                                    .toLowerCase(Locale.getDefault())
                                    + entityField.getFieldType().substring(1), entityField.getFieldType(),
                            entityField.getFieldName())));
                    valueCheck.addIfStatement(checkExistence.getIfElseStatementNode());
                    create.addIfElseStatement(valueCheck.getIfElseStatementNode());
                }
            }
            create.addStatement(NodeParser.parseStatement(BalSyntaxConstants.CREATE_SQL_RESULTS_RELATION));
            create.addStatement(NodeParser.parseStatement(RETURN_VAUE));
        }
        return create;
    }

    private static Function getReadByKeyMethod(Entity entity, HashMap<String, String> keys, boolean inclusions,
                                               String keyType, StringBuilder keyString) {
        Function readByKey = new Function(BalSyntaxConstants.READ_BY_KEY);
        if (keys.size() > 1) {
            readByKey.addRequiredParameter(
                    NodeParser.parseTypeDescriptor(String.format(BalSyntaxConstants.CLOSE_RECORD_VARIABLE, keyString)),
                    KEY);
        } else {
            readByKey.addRequiredParameter(
                    TypeDescriptor.getBuiltinSimpleNameReferenceNode(keyType), KEY);
        }
        readByKey.addQualifiers(new String[]{BalSyntaxConstants.KEYWORD_REMOTE});
        readByKey.addReturns(TypeDescriptor.getUnionTypeDescriptorNode(
                TypeDescriptor.getSimpleNameReferenceNode(entity.getEntityName()),
                TypeDescriptor.getQualifiedNameReferenceNode(PERSIST_MODULE, SPECIFIC_ERROR)));

        if (inclusions) {
            readByKey.addRequiredParameterWithDefault(NodeParser.parseTypeDescriptor(String.format(
                            ENTITY_RELATIONS_ARRAY, entity.getEntityName())), INCLUDE,
                    Function.Bracket.SQUARE);
            readByKey.addStatement(NodeParser.parseStatement(String.format(READ_BY_KEY_RETURN_RELATION,
                    entity.getEntityName(), entity.getEntityName())));
        } else {
            readByKey.addStatement(NodeParser.parseStatement(String.format(READ_BY_KEY_RETURN,
                    entity.getEntityName(), entity.getEntityName())));

        }
        return readByKey;
    }

    private static Function getReadMethod(Entity entity, boolean inclusions, String className) {
        Function read = new Function(BalSyntaxConstants.READ);
        read.addQualifiers(new String[]{BalSyntaxConstants.KEYWORD_REMOTE});
        read.addReturns(TypeDescriptor.getStreamTypeDescriptorNode(
                TypeDescriptor.getSimpleNameReferenceNode(entity.getEntityName()),
                TypeDescriptor.getOptionalTypeDescriptorNode(EMPTY_STRING,
                        PERSIST_ERROR)));
        if (inclusions) {
            read.addRequiredParameterWithDefault(NodeParser.parseTypeDescriptor(String.format(
                            ENTITY_RELATIONS_ARRAY, entity.getEntityName())),
                    INCLUDE, Function.Bracket.SQUARE);
            read.addStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.READ_RUN_READ_QUERY_RELATED,
                    entity.getEntityName())));

        } else {
            read.addStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.READ_RUN_READ_QUERY,
                    entity.getEntityName())));
        }
        IfElse errorCheck = new IfElse(NodeParser.parseExpression(RESULT_IS_ERROR));
        errorCheck.addIfStatement(NodeParser.parseStatement(String.format(
                BalSyntaxConstants.READ_RETURN_STREAM_WHEN_ERROR, entity.getEntityName(), className)));
        errorCheck.addElseStatement(NodeParser.parseStatement(String.format(
                BalSyntaxConstants.READ_RETURN_STREAM_WHEN_NOT_ERROR, entity.getEntityName(), className)));

        read.addIfElseStatement(errorCheck.getIfElseStatementNode());
        return read;
    }

    // Advance filter query support is de-prioritized and removed from phase 1
//    private static Function getExecuteMethod(Entity entity, String className) {
//        Function execute = new Function(BalFileConstants.EXECUTE);
//        execute.addQualifiers(new String[]{BalFileConstants.KEYWORD_REMOTE});
//        execute.addRequiredParameter(TypeDescriptor.getQualifiedNameReferenceNode(KEYWORD_SQL,
//                KEYWORD_PARAMETERIZED_QUERY), KEYWORD_FILTER_CLAUSE);
//        execute.addReturns(TypeDescriptor.getStreamTypeDescriptorNode(
//                TypeDescriptor.getSimpleNameReferenceNode(entity.getEntityName()),
//                TypeDescriptor.getOptionalTypeDescriptorNode(BalFileConstants.EMPTY_STRING,
//                        BalFileConstants.PERSIST_ERROR)));
//        execute.addStatement(NodeParser.parseStatement(String.format(BalFileConstants.EXECUTE_RUN_EXECUTE_QUERY,
//                entity.getEntityName())));
//
//        IfElse errorCheck = new IfElse(NodeParser.parseExpression(RESULT_IS_ERROR));
//        errorCheck.addIfStatement(NodeParser.parseStatement(String.format(
//                BalFileConstants.READ_RETURN_STREAM_WHEN_ERROR, entity.getEntityName(),
//                className)));
//        errorCheck.addElseStatement(NodeParser.parseStatement(String.format(
//                BalFileConstants.READ_RETURN_STREAM_WHEN_NOT_ERROR, entity.getEntityName(),
//                className)));
//        execute.addIfElseStatement(errorCheck.getIfElseStatementNode());
//        return execute;
//    }

    private static Function getUpdateMethod(Entity entity, List<Node> joinSubFields) {
        Function update = new Function(BalSyntaxConstants.UPDATE);
        update.addQualifiers(new String[]{BalSyntaxConstants.KEYWORD_REMOTE});
        update.addReturns(TypeDescriptor.getOptionalTypeDescriptorNode(EMPTY_STRING,
                PERSIST_ERROR));
        update.addStatement(NodeParser.parseStatement(BalSyntaxConstants.UPDATE_RUN_UPDATE_QUERY));
        update.addRequiredParameter(TypeDescriptor.getSimpleNameReferenceNode(entity.getEntityName()), VALUE);
        if (!joinSubFields.isEmpty()) {
            for (EntityField entityField : entity.getFields()) {
                if (entityField.getRelation() != null && entityField.getRelation().isOwner()) {
                    IfElse typeCheck = new IfElse(NodeParser.parseExpression(String.format(RECORD_CHECK,
                            entityField.getFieldName())));
                    typeCheck.addIfStatement(NodeParser.parseStatement(String.format(GET_ENTITY_RECORD,
                            entityField.getFieldType(), entityField.getFieldType().substring(0, 1)
                                    .toLowerCase(Locale.getDefault())
                                    + entityField.getFieldType().substring(1), entityField.getFieldType(),
                            entityField.getFieldName())));
                    typeCheck.addIfStatement(NodeParser.parseStatement(String.format(GET_ENTITY_CLIENT,
                            entityField.getFieldType(), entityField.getFieldType().substring(0, 1)
                                    .toLowerCase(Locale.getDefault())
                                    + entityField.getFieldType().substring(1), entityField.getFieldType())));
                    typeCheck.addIfStatement(NodeFactory.createExpressionStatementNode(SyntaxKind.ACTION_STATEMENT,
                            NodeFactory.createCheckExpressionNode(
                                    SyntaxKind.CHECK_ACTION,
                                    SyntaxTokenConstants.SYNTAX_TREE_KEYWORD_CHECK,
                                    NodeParser.parseActionOrExpression(String.format(CHECK_UPDATE_STATEMENT,
                                            entityField.getFieldType().substring(0, 1)
                                                    .toLowerCase(Locale.getDefault())
                                                    + entityField.getFieldType().substring(1),
                                            entityField.getFieldType().substring(0, 1).toLowerCase(Locale.getDefault())
                                                    + entityField.getFieldType().substring(1)
                                    ))
                            ), SYNTAX_TREE_SEMICOLON));
                    update.addIfElseStatement(typeCheck.getIfElseStatementNode());
                }
            }
        }
        return update;
    }

    private static Function getDeleteMethod(Entity entity) {
        Function delete = new Function(BalSyntaxConstants.DELETE);
        delete.addRequiredParameter(TypeDescriptor.getSimpleNameReferenceNode(entity.getEntityName()), KEYWORD_VALUE);
        delete.addQualifiers(new String[]{BalSyntaxConstants.KEYWORD_REMOTE});
        delete.addReturns(TypeDescriptor.getOptionalTypeDescriptorNode(EMPTY_STRING,
                PERSIST_ERROR));
        delete.addStatement(NodeParser.parseStatement(BalSyntaxConstants.DELETE_RUN_DELETE_QUERY));
        return delete;
    }

    private static Function getExistMethod(Entity entity, StringBuilder keyString) {
        Function exists = new Function(BalSyntaxConstants.EXISTS);
        exists.addRequiredParameter(TypeDescriptor.getSimpleNameReferenceNode(entity.getEntityName()),
                entity.getEntityName().substring(0, 1).toLowerCase(Locale.getDefault())
                        + entity.getEntityName().substring(1));
        exists.addQualifiers(new String[]{BalSyntaxConstants.KEYWORD_REMOTE});
        exists.addReturns(TypeDescriptor.getUnionTypeDescriptorNode(
                TypeDescriptor.getBuiltinSimpleNameReferenceNode(KEYWORD_BOOLEAN),
                TypeDescriptor.getQualifiedNameReferenceNode(PERSIST_MODULE, SPECIFIC_ERROR)));
        exists.addStatement(NodeParser.parseStatement(String.format(EXIST_READ_BY_KEY, entity.getEntityName(),
                keyString)));
        IfElse typeCheck = new IfElse(NodeParser.parseExpression(String.format(CHECK_RESULT, entity.getEntityName())));
        typeCheck.addIfStatement(NodeParser.parseStatement(RETURN_TRUE));
        IfElse invalidKeyCheck = new IfElse(NodeParser.parseExpression(EXIST_CHECK_INVALID));
        invalidKeyCheck.addIfStatement(NodeParser.parseStatement(RETURN_FALSE));
        invalidKeyCheck.addElseStatement(NodeParser.parseStatement(RETURN_RESULT));
        typeCheck.addElseBody(invalidKeyCheck);
        exists.addIfElseStatement(typeCheck.getIfElseStatementNode());
        return exists;
    }

    private static Function getCloseMethod() {
        Function close = new Function(BalSyntaxConstants.CLOSE);
        close.addQualifiers(new String[]{BalSyntaxConstants.KEYWORD_PUBLIC});
        close.addReturns(TypeDescriptor.getOptionalTypeDescriptorNode(EMPTY_STRING,
                PERSIST_ERROR));
        close.addStatement(NodeParser.parseStatement(BalSyntaxConstants.CLOSE_PERSIST_CLIENT));
        return close;
    }

    public static String generateDatabaseConfigSyntaxTree() throws FormatterException {
        NodeList<ImportDeclarationNode> imports = AbstractNodeFactory.createEmptyNodeList();
        NodeList<ModuleMemberDeclarationNode> moduleMembers = AbstractNodeFactory.createEmptyNodeList();

        MinutiaeList commentMinutiaeList = crateCommentMinutiaeList(String.format(AUTO_GENERATED_COMMENT));
        ImportPrefixNode prefix = NodeFactory.createImportPrefixNode(SyntaxTokenConstants.SYNTAX_TREE_AS,
                AbstractNodeFactory.createToken(SyntaxKind.UNDERSCORE_KEYWORD));
        imports = imports.add(getImportDeclarationNodeWithAutogeneratedComment(KEYWORD_BALLERINAX, MYSQL_DRIVER,
                commentMinutiaeList, prefix));
        moduleMembers = moduleMembers.add(NodeParser.parseModuleMemberDeclaration(
                BalSyntaxConstants.CONFIGURABLE_PORT));
        moduleMembers = moduleMembers.add(NodeParser.parseModuleMemberDeclaration(
                BalSyntaxConstants.CONFIGURABLE_HOST));
        moduleMembers = moduleMembers.add(NodeParser.parseModuleMemberDeclaration(
                BalSyntaxConstants.CONFIGURABLE_USER));
        moduleMembers = moduleMembers.add(NodeParser.parseModuleMemberDeclaration(
                BalSyntaxConstants.CONFIGURABLE_DATABASE));
        moduleMembers = moduleMembers.add(NodeParser.parseModuleMemberDeclaration(
                BalSyntaxConstants.CONFIGURABLE_PASSWORD));

        Token eofToken = AbstractNodeFactory.createIdentifierToken(EMPTY_STRING);
        ModulePartNode modulePartNode = NodeFactory.createModulePartNode(imports, moduleMembers, eofToken);
        TextDocument textDocument = TextDocuments.from(EMPTY_STRING);
        SyntaxTree balTree = SyntaxTree.from(textDocument);

        // output cannot be SyntaxTree as it will overlap with Toml Syntax Tree in Init Command
        return Formatter.format(balTree.modifyWith(modulePartNode).toSourceCode());
    }

    private static ImportDeclarationNode getImportDeclarationNode(String orgName, String moduleName,
                                                                 ImportPrefixNode prefix) {
        Token orgNameToken = AbstractNodeFactory.createIdentifierToken(orgName);
        ImportOrgNameNode importOrgNameNode = NodeFactory.createImportOrgNameNode(
                orgNameToken,
                SyntaxTokenConstants.SYNTAX_TREE_SLASH
        );
        Token moduleNameToken = AbstractNodeFactory.createIdentifierToken(moduleName);
        SeparatedNodeList<IdentifierToken> moduleNodeList =
                AbstractNodeFactory.createSeparatedNodeList(moduleNameToken);

        return NodeFactory.createImportDeclarationNode(
                SyntaxTokenConstants.SYNTAX_TREE_KEYWORD_IMPORT,
                importOrgNameNode,
                moduleNodeList,
                prefix,
                SYNTAX_TREE_SEMICOLON
        );
    }

    private static MinutiaeList crateCommentMinutiaeList(String comment) {
        return  NodeFactory.createMinutiaeList(
                AbstractNodeFactory.createCommentMinutiae(BalSyntaxConstants.AUTOGENERATED_FILE_COMMENT),
                AbstractNodeFactory.createEndOfLineMinutiae(System.lineSeparator()),
                AbstractNodeFactory.createEndOfLineMinutiae(System.lineSeparator()),
                AbstractNodeFactory.createCommentMinutiae(comment),
                AbstractNodeFactory.createEndOfLineMinutiae(System.lineSeparator()),
                AbstractNodeFactory.createCommentMinutiae(BalSyntaxConstants.COMMENT_SHOULD_NOT_BE_MODIFIED),
                AbstractNodeFactory.createEndOfLineMinutiae(System.lineSeparator()),
                AbstractNodeFactory.createEndOfLineMinutiae(System.lineSeparator()));
    }

    private static ImportDeclarationNode getImportDeclarationNodeWithAutogeneratedComment(
            String orgName, String moduleName, MinutiaeList commentMinutiaeList, ImportPrefixNode prefix) {
        Token orgNameToken = AbstractNodeFactory.createIdentifierToken(orgName);
        ImportOrgNameNode importOrgNameNode = NodeFactory.createImportOrgNameNode(
                orgNameToken,
                SyntaxTokenConstants.SYNTAX_TREE_SLASH
        );
        Token moduleNameToken = AbstractNodeFactory.createIdentifierToken(moduleName);
        SeparatedNodeList<IdentifierToken> moduleNodeList =
                AbstractNodeFactory.createSeparatedNodeList(moduleNameToken);
        Token importToken = NodeFactory.createToken(SyntaxKind.IMPORT_KEYWORD,
                commentMinutiaeList, NodeFactory.createMinutiaeList(AbstractNodeFactory
                        .createWhitespaceMinutiae(SPACE)));
        return NodeFactory.createImportDeclarationNode(
                importToken,
                importOrgNameNode,
                moduleNodeList,
                prefix,
                SYNTAX_TREE_SEMICOLON
        );
    }
}
