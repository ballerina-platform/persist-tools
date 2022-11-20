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

package io.ballerina.persist.nodegenerator;

import io.ballerina.compiler.syntax.tree.AbstractNodeFactory;
import io.ballerina.compiler.syntax.tree.AnnotationNode;
import io.ballerina.compiler.syntax.tree.ArrayTypeDescriptorNode;
import io.ballerina.compiler.syntax.tree.BasicLiteralNode;
import io.ballerina.compiler.syntax.tree.ExpressionNode;
import io.ballerina.compiler.syntax.tree.IdentifierToken;
import io.ballerina.compiler.syntax.tree.ImportDeclarationNode;
import io.ballerina.compiler.syntax.tree.ImportOrgNameNode;
import io.ballerina.compiler.syntax.tree.ListConstructorExpressionNode;
import io.ballerina.compiler.syntax.tree.MappingFieldNode;
import io.ballerina.compiler.syntax.tree.MetadataNode;
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
import io.ballerina.compiler.syntax.tree.SpecificFieldNode;
import io.ballerina.compiler.syntax.tree.SyntaxKind;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.compiler.syntax.tree.Token;
import io.ballerina.compiler.syntax.tree.TypeDefinitionNode;
import io.ballerina.persist.components.Class;
import io.ballerina.persist.components.Enum;
import io.ballerina.persist.components.Function;
import io.ballerina.persist.components.IfElse;
import io.ballerina.persist.components.TypeDescriptor;
import io.ballerina.persist.objects.Entity;
import io.ballerina.persist.objects.EntityMetaData;
import io.ballerina.persist.objects.FieldMetaData;
import io.ballerina.persist.objects.Relation;
import io.ballerina.tools.text.TextDocument;
import io.ballerina.tools.text.TextDocuments;
import org.ballerinalang.formatter.core.Formatter;
import org.ballerinalang.formatter.core.FormatterException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static io.ballerina.persist.nodegenerator.BalFileConstants.ANYDATASTREAM_IS_STREAM_TYPE;
import static io.ballerina.persist.nodegenerator.BalFileConstants.ANYDATA_KETWORD;
import static io.ballerina.persist.nodegenerator.BalFileConstants.ANYDATA_STREAM_NEXT;
import static io.ballerina.persist.nodegenerator.BalFileConstants.ANYDATA_STREAM_STATEMENT;
import static io.ballerina.persist.nodegenerator.BalFileConstants.APOSTROPHE;
import static io.ballerina.persist.nodegenerator.BalFileConstants.ARRAY_TYPE;
import static io.ballerina.persist.nodegenerator.BalFileConstants.CAST_ANYDATA_STREAM;
import static io.ballerina.persist.nodegenerator.BalFileConstants.CHECK_EXISTENCE;
import static io.ballerina.persist.nodegenerator.BalFileConstants.CHECK_RESULT;
import static io.ballerina.persist.nodegenerator.BalFileConstants.CHECK_UPDATE_STATEMENT;
import static io.ballerina.persist.nodegenerator.BalFileConstants.COLON;
import static io.ballerina.persist.nodegenerator.BalFileConstants.COMMA_SPACE;
import static io.ballerina.persist.nodegenerator.BalFileConstants.CREATE_CLIENT;
import static io.ballerina.persist.nodegenerator.BalFileConstants.DB_CLIENT_IS_DB_CLIENT;
import static io.ballerina.persist.nodegenerator.BalFileConstants.DEFILE_INCLUDE_MANY;
import static io.ballerina.persist.nodegenerator.BalFileConstants.DEFINE_PERSIST_CLIENTS_MANY;
import static io.ballerina.persist.nodegenerator.BalFileConstants.DOUBLE_QUOTE;
import static io.ballerina.persist.nodegenerator.BalFileConstants.EMPTY_STRING;
import static io.ballerina.persist.nodegenerator.BalFileConstants.END_CLOSED_RECORD;
import static io.ballerina.persist.nodegenerator.BalFileConstants.END_RECORD;
import static io.ballerina.persist.nodegenerator.BalFileConstants.ENTITY_RELATIONS_ARRAY;
import static io.ballerina.persist.nodegenerator.BalFileConstants.ENUM_ENTRY;
import static io.ballerina.persist.nodegenerator.BalFileConstants.ENUM_NAME;
import static io.ballerina.persist.nodegenerator.BalFileConstants.ERR_IS_ERROR;
import static io.ballerina.persist.nodegenerator.BalFileConstants.EXIST_CHECK_INVALID;
import static io.ballerina.persist.nodegenerator.BalFileConstants.EXIST_READ_BY_KEY;
import static io.ballerina.persist.nodegenerator.BalFileConstants.FIELD_ACCESSS;
import static io.ballerina.persist.nodegenerator.BalFileConstants.GET_ENTITY_CLIENT;
import static io.ballerina.persist.nodegenerator.BalFileConstants.GET_ENTITY_RECORD;
import static io.ballerina.persist.nodegenerator.BalFileConstants.GET_MANY_RELATIONS;
import static io.ballerina.persist.nodegenerator.BalFileConstants.GET_NEW_CLIENT;
import static io.ballerina.persist.nodegenerator.BalFileConstants.INCLUDE;
import static io.ballerina.persist.nodegenerator.BalFileConstants.INIT_INCLUDE_MANY;
import static io.ballerina.persist.nodegenerator.BalFileConstants.INIT_PERSIST_CLIENT_MANY;
import static io.ballerina.persist.nodegenerator.BalFileConstants.IS_SQL_ERROR;
import static io.ballerina.persist.nodegenerator.BalFileConstants.KEY;
import static io.ballerina.persist.nodegenerator.BalFileConstants.KEYWORD_AUTOINCREMENT;
import static io.ballerina.persist.nodegenerator.BalFileConstants.KEYWORD_BOOLEAN;
import static io.ballerina.persist.nodegenerator.BalFileConstants.KEYWORD_CLIENT_CLASS;
import static io.ballerina.persist.nodegenerator.BalFileConstants.KEYWORD_ENTITY;
import static io.ballerina.persist.nodegenerator.BalFileConstants.KEYWORD_ENTITY_NAME;
import static io.ballerina.persist.nodegenerator.BalFileConstants.KEYWORD_ERR;
import static io.ballerina.persist.nodegenerator.BalFileConstants.KEYWORD_FILTER_CLAUSE;
import static io.ballerina.persist.nodegenerator.BalFileConstants.KEYWORD_KEYFIELDS;
import static io.ballerina.persist.nodegenerator.BalFileConstants.KEYWORD_PARAMETERIZED_QUERY;
import static io.ballerina.persist.nodegenerator.BalFileConstants.KEYWORD_PERSIST_CLIENT;
import static io.ballerina.persist.nodegenerator.BalFileConstants.KEYWORD_PERSIST_SQL_CLIENT;
import static io.ballerina.persist.nodegenerator.BalFileConstants.KEYWORD_REFERENCE;
import static io.ballerina.persist.nodegenerator.BalFileConstants.KEYWORD_RELATION;
import static io.ballerina.persist.nodegenerator.BalFileConstants.KEYWORD_SQL;
import static io.ballerina.persist.nodegenerator.BalFileConstants.KEYWORD_SQL_CLIENT;
import static io.ballerina.persist.nodegenerator.BalFileConstants.KEYWORD_STREAM;
import static io.ballerina.persist.nodegenerator.BalFileConstants.KEYWORD_TABLE_NAME;
import static io.ballerina.persist.nodegenerator.BalFileConstants.KEYWORD_VALUE;
import static io.ballerina.persist.nodegenerator.BalFileConstants.KEY_COLUMNS;
import static io.ballerina.persist.nodegenerator.BalFileConstants.MYSQL_DRIVER_IMPORT;
import static io.ballerina.persist.nodegenerator.BalFileConstants.NOT_EXIST;
import static io.ballerina.persist.nodegenerator.BalFileConstants.NULLABLE_ANYDATA_STREAM_TYPE;
import static io.ballerina.persist.nodegenerator.BalFileConstants.NULLABLE_ERROR_STATEMENT;
import static io.ballerina.persist.nodegenerator.BalFileConstants.PERSIST;
import static io.ballerina.persist.nodegenerator.BalFileConstants.PERSIST_ERROR;
import static io.ballerina.persist.nodegenerator.BalFileConstants.READ_BY_KEY_RETURN;
import static io.ballerina.persist.nodegenerator.BalFileConstants.READ_BY_KEY_RETURN_RELATION;
import static io.ballerina.persist.nodegenerator.BalFileConstants.RECORD_CHECK;
import static io.ballerina.persist.nodegenerator.BalFileConstants.RESULT_IS_ERROR;
import static io.ballerina.persist.nodegenerator.BalFileConstants.RETURN_CASTED_ERROR;
import static io.ballerina.persist.nodegenerator.BalFileConstants.RETURN_FALSE;
import static io.ballerina.persist.nodegenerator.BalFileConstants.RETURN_NILL;
import static io.ballerina.persist.nodegenerator.BalFileConstants.RETURN_PERSIST_ERROR_CLOSE_STREAM;
import static io.ballerina.persist.nodegenerator.BalFileConstants.RETURN_PERSIST_ERROR_FROM_DBCLIENT;
import static io.ballerina.persist.nodegenerator.BalFileConstants.RETURN_RESULT;
import static io.ballerina.persist.nodegenerator.BalFileConstants.RETURN_TRUE;
import static io.ballerina.persist.nodegenerator.BalFileConstants.RETURN_VAUE;
import static io.ballerina.persist.nodegenerator.BalFileConstants.SELF_ERR;
import static io.ballerina.persist.nodegenerator.BalFileConstants.SEMICOLON;
import static io.ballerina.persist.nodegenerator.BalFileConstants.SINGLE_QUOTE;
import static io.ballerina.persist.nodegenerator.BalFileConstants.SPACE;
import static io.ballerina.persist.nodegenerator.BalFileConstants.SPECIFIC_ERROR;
import static io.ballerina.persist.nodegenerator.BalFileConstants.START_CLOSED_RECORD;
import static io.ballerina.persist.nodegenerator.BalFileConstants.START_RECORD;
import static io.ballerina.persist.nodegenerator.BalFileConstants.VALUE;
import static io.ballerina.persist.nodegenerator.BalFileConstants.VALUE_TYPE_CHECK;
import static io.ballerina.persist.nodegenerator.BalFileConstants.VAR_ENTITY_RELATION;
import static io.ballerina.persist.nodegenerator.SyntaxTreeConstants.SYNTAX_TREE_SEMICOLON;


/**
 * Class containing methods to create and read ballerina files as syntax trees.
 *
 * @since 0.1.0
 */
public class BalSyntaxTreeGenerator {
    /**
     * method to read ballerina files.
     */
    public static EntityMetaData getEntityRecord(Path filePath)
            throws IOException {
        ArrayList<Entity> entityArray = new ArrayList<>();
        ArrayList<ModuleMemberDeclarationNode> entityMembers = new ArrayList<>();
        int index = -1;
        ArrayList<String> keys = new ArrayList<>();
        String tableName = null;
        int count;
        SyntaxTree balSyntaxTree = SyntaxTree.from(TextDocuments.from(Files.readString(filePath)));
        ModulePartNode rootNote = balSyntaxTree.rootNode();
        NodeList<ModuleMemberDeclarationNode> nodeList = rootNote.members();
        for (ModuleMemberDeclarationNode moduleNode : nodeList) {
            if (moduleNode.kind() != SyntaxKind.TYPE_DEFINITION || ((TypeDefinitionNode) moduleNode)
                    .metadata().isEmpty()) {
                continue;
            }

            for (AnnotationNode annotation : ((TypeDefinitionNode) moduleNode).metadata().get().annotations()) {
                Node annotReference = annotation.annotReference();
                if (annotReference.kind() != SyntaxKind.QUALIFIED_NAME_REFERENCE) {
                    continue;
                }
                QualifiedNameReferenceNode qualifiedNameRef = (QualifiedNameReferenceNode) annotReference;
                if (qualifiedNameRef.identifier().text().equals(KEYWORD_ENTITY) && qualifiedNameRef
                        .modulePrefix().text().equals(BalFileConstants.PERSIST) && annotation.annotValue()
                        .isPresent()) {
                    index += 1;
                    entityMembers.add(moduleNode);
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
                        if (((SpecificFieldNode) fieldNode).fieldName().toString().trim().equals(KEY)) {
                            keys = new ArrayList<>();
                            Iterator<Node> listIterator = ((ListConstructorExpressionNode) valueNode)
                                    .expressions().iterator();
                            count = 0;
                            while (listIterator.hasNext()) {
                                keys.add(count, listIterator.next().toSourceCode());
                                count += 1;
                            }
                        } else if (((SpecificFieldNode) fieldNode).fieldName().toSourceCode().trim()
                                .equals(KEYWORD_TABLE_NAME)) {
                            tableName = ((BasicLiteralNode) valueNode).literalToken().text()
                                    .replaceAll(DOUBLE_QUOTE, BalFileConstants.EMPTY_STRING);
                        }
                    }
                    entityArray.add(index, new Entity(getArray(keys), tableName));
                }
            }

            RecordTypeDescriptorNode recordDesc = (RecordTypeDescriptorNode) ((TypeDefinitionNode) moduleNode)
                    .typeDescriptor();
            entityArray.get(index).setEntityName(((TypeDefinitionNode) moduleNode).typeName().text());
            for (Node node : recordDesc.fields()) {
                if (node.kind() == SyntaxKind.RECORD_FIELD_WITH_DEFAULT_VALUE) {
                    RecordFieldWithDefaultValueNode fieldNode = (RecordFieldWithDefaultValueNode) node;
                    String fName = fieldNode.fieldName().text().trim();
                    String fType;
                    if (fieldNode.typeName().kind().equals(SyntaxKind.ARRAY_TYPE_DESC)) {
                        ArrayTypeDescriptorNode arrayTypeDescriptorNode = (ArrayTypeDescriptorNode)
                                fieldNode.typeName();
                        fType = arrayTypeDescriptorNode.memberTypeDesc().toSourceCode().trim();

                    } else {
                        fType = fieldNode.typeName().toSourceCode().trim();
                    }
                    FieldMetaData field;
                    if (((RecordFieldWithDefaultValueNode) node).metadata().isEmpty()) {
                        field = new FieldMetaData(fName, fType, false);
                    } else {
                        MetadataNode fieldMetaD = ((RecordFieldWithDefaultValueNode) node).metadata().get();
                        Relation relation = readMetaData(fName, fType, fieldMetaD);
                        field = new FieldMetaData(fName, fType, checkAutoIncrement(fieldMetaD));
                        if (relation.getKeyColumns() != null && relation.getReferences() != null) {
                            entityArray.get(index).getRelations().add(relation);
                        }
                    }
                    if (((RecordFieldWithDefaultValueNode) node).typeName().kind() == SyntaxKind.ARRAY_TYPE_DESC) {
                        field.setIsRelationType(Relation.RelationType.MANY);
                    }
                    entityArray.get(index).addField(field);
                } else if (node.kind() == SyntaxKind.RECORD_FIELD) {
                    RecordFieldNode fieldNode = (RecordFieldNode) node;
                    String fName = fieldNode.fieldName().text().trim();
                    String fType;
                    if (fieldNode.typeName().kind().equals(SyntaxKind.ARRAY_TYPE_DESC)) {
                        ArrayTypeDescriptorNode arrayTypeDescriptorNode = (ArrayTypeDescriptorNode)
                                fieldNode.typeName();
                        fType = arrayTypeDescriptorNode.memberTypeDesc().toSourceCode().trim();

                    } else {
                        fType = fieldNode.typeName().toSourceCode().trim();
                    }
                    FieldMetaData field;
                    if (((RecordFieldNode) node).metadata().isEmpty()) {
                        field = new FieldMetaData(fName, fType, false);
                    } else {
                        MetadataNode fieldMetaD = ((RecordFieldNode) node).metadata().get();
                        Relation relation = readMetaData(fName, fType, fieldMetaD);
                        field = new FieldMetaData(fName, fType, checkAutoIncrement(fieldMetaD));
                        if (relation.getKeyColumns() != null && relation.getReferences() != null) {
                            entityArray.get(index).getRelations().add(relation);
                            field.setIsRelationType(Relation.RelationType.ONE);
                        }
                    }
                    if (((RecordFieldNode) node).typeName().kind() == SyntaxKind.ARRAY_TYPE_DESC) {
                        field.setIsRelationType(Relation.RelationType.MANY);
                    }
                    entityArray.get(index).addField(field);
                }
            }
        }
        return new EntityMetaData(entityArray, entityMembers);
    }

    public static SyntaxTree copyEntities(ArrayList<ModuleMemberDeclarationNode> entityMembers,
                                          ArrayList<ImportDeclarationNode> importArray) {
        NodeList<ImportDeclarationNode> imports = AbstractNodeFactory.createEmptyNodeList();
        NodeList<ModuleMemberDeclarationNode> moduleMembers = AbstractNodeFactory.createEmptyNodeList();
        imports = imports.add(getImportDeclarationNode(BalFileConstants.KEYWORD_BALLERINA,
                BalFileConstants.PERSIST));
        for (ImportDeclarationNode impDec : importArray) {
            imports = imports.add(impDec);
        }
        for (ModuleMemberDeclarationNode member : entityMembers) {
            moduleMembers = moduleMembers.add(member);
        }
        Token eofToken = AbstractNodeFactory.createIdentifierToken(BalFileConstants.EMPTY_STRING);
        ModulePartNode modulePartNode = NodeFactory.createModulePartNode(imports, moduleMembers, eofToken);
        TextDocument textDocument = TextDocuments.from(BalFileConstants.EMPTY_STRING);
        SyntaxTree balTree = SyntaxTree.from(textDocument);
        return balTree.modifyWith(modulePartNode);
    }

    public static SyntaxTree generateClientSyntaxTree(Entity entity, ArrayList<ImportDeclarationNode> importsArray) {
        boolean keyAutoInc = false;
        Enum relationsEnum = null;
        HashMap<String, String> keys = new HashMap<>();
        String keyType = BalFileConstants.KEYWORD_INT;
        NodeList<ImportDeclarationNode> imports = AbstractNodeFactory.createEmptyNodeList();
        NodeList<ModuleMemberDeclarationNode> moduleMembers = AbstractNodeFactory.createEmptyNodeList();
        List<Node> subFields = new ArrayList<>();
        List<Node> joinSubFields = new ArrayList<>();
        boolean hasTime = false;
        for (FieldMetaData field : entity.getFields()) {
            if (field.getFieldType().contains(BalFileConstants.KEYWORD_TIME) && !hasTime) {
                hasTime = true;
            }

            for (String key : entity.getKeys()) {
                if (field.getFieldName().equals(key.trim().replaceAll(
                        DOUBLE_QUOTE, BalFileConstants.EMPTY_STRING))) {
                    keys.put(field.getFieldName(),
                            field.getFieldType().trim().replaceAll(SPACE,
                                    BalFileConstants.EMPTY_STRING));
                }
            }

            if (entity.getKeys().length == 1) {
                if (field.getFieldName().equals(entity.getKeys()[0].trim()
                        .replaceAll(DOUBLE_QUOTE, BalFileConstants.EMPTY_STRING))) {
                    keyType = field.getFieldType().trim().replaceAll(SPACE,
                            BalFileConstants.EMPTY_STRING);
                }
            }
            if (!subFields.isEmpty()) {
                subFields.add(NodeFactory.createBasicLiteralNode(SyntaxKind.STRING_LITERAL,
                        AbstractNodeFactory.createLiteralValueToken(SyntaxKind.STRING_LITERAL, COMMA_SPACE
                                        + System.lineSeparator(), NodeFactory.createEmptyMinutiaeList(),
                                NodeFactory.createEmptyMinutiaeList())));
            }
            if (field.isAutoGenerated()) {
                subFields.add(NodeFactory.createSpecificFieldNode(null,
                        AbstractNodeFactory.createIdentifierToken(field.getFieldName()),
                        SyntaxTreeConstants.SYNTAX_TREE_COLON, NodeParser.parseExpression(String.format(
                                BalFileConstants.FIELD_FORMAT_WITH_AUTO_G,
                                field.getFieldName().trim().replaceAll(
                                        SINGLE_QUOTE, BalFileConstants.EMPTY_STRING),
                                field.getFieldType().trim().replaceAll(SPACE,
                                        BalFileConstants.EMPTY_STRING),
                                String.valueOf(field.isAutoGenerated()).trim()))));
                for (String key : entity.getKeys()) {
                    if (field.getFieldName().equals(key.trim().replaceAll(
                            DOUBLE_QUOTE, BalFileConstants.EMPTY_STRING))) {
                        keyAutoInc = true;
                        break;
                    }
                }
            } else {
                subFields.add(NodeFactory.createSpecificFieldNode(null,
                        AbstractNodeFactory.createIdentifierToken(field.getFieldName()),
                        SyntaxTreeConstants.SYNTAX_TREE_COLON,
                        NodeParser.parseExpression(String.format(BalFileConstants.FIELD_FORMAT_WITHOUT_AUTO_G,
                                field.getFieldName().trim().replaceAll(
                                        SINGLE_QUOTE, BalFileConstants.EMPTY_STRING),
                                field.getFieldType().trim().replaceAll(SPACE,
                                        BalFileConstants.EMPTY_STRING)
                        ))));
            }
        }
        imports = imports.add(getImportDeclarationNode(BalFileConstants.KEYWORD_BALLERINA,
                KEYWORD_SQL));
        imports = imports.add(getImportDeclarationNode(BalFileConstants.KEYWORD_BALLERINAX,
                BalFileConstants.KEYWORD_MYSQL));
        String className = entity.getEntityName();
        boolean inclusions = false;
        boolean manyRelation = false;
        if (entity.getRelations().size() != 0) {
            relationsEnum = new Enum(String.format(ENUM_NAME, className));
            for (Relation relation : entity.getRelations()) {
                if (!hasTime) {
                    for (FieldMetaData fieldMetaData :relation.getRelatedFields()) {
                        if (fieldMetaData.getFieldType().contains(BalFileConstants.KEYWORD_TIME)) {
                            hasTime = true;
                            break;
                        }
                    }
                }
                if (relation.getRelatedFields().size() == 0) {
                    continue;
                }
                StringBuilder refFieldsString = new StringBuilder();
                StringBuilder joinColumnsString = new StringBuilder();

                for (int i = 0; i < relation.getKeyColumns().size(); i++) {
                    if (refFieldsString.length() != 0) {
                        refFieldsString.append(COMMA_SPACE);
                        joinColumnsString.append(COMMA_SPACE);
                    }
                    refFieldsString.append(relation.getReferences().get(i));
                    joinColumnsString.append(relation.getKeyColumns().get(i));
                }

                if (!joinSubFields.isEmpty()) {
                    joinSubFields.add(NodeFactory.createBasicLiteralNode(SyntaxKind.STRING_LITERAL,
                            AbstractNodeFactory.createLiteralValueToken(SyntaxKind.STRING_LITERAL, COMMA_SPACE
                                            + System.lineSeparator(), NodeFactory.createEmptyMinutiaeList(),
                                    NodeFactory.createEmptyMinutiaeList())));
                }
                if (relation.relationType == Relation.RelationType.ONE) {
                    joinSubFields.add(NodeFactory.createSpecificFieldNode(null,
                            AbstractNodeFactory.createIdentifierToken(relation.getRelatedInstance()),
                            SyntaxTreeConstants.SYNTAX_TREE_COLON,
                            NodeParser.parseExpression(String.format(BalFileConstants.FIELD_FORMAT_JOIN_FIELD,
                                    relation.getRelatedType(), relation.getRelatedInstance(),
                                    relation.getRefTable(), refFieldsString, joinColumnsString))));
                } else {
                    joinSubFields.add(NodeFactory.createSpecificFieldNode(null,
                            AbstractNodeFactory.createIdentifierToken(relation.getRelatedInstance()),
                            SyntaxTreeConstants.SYNTAX_TREE_COLON,
                            NodeParser.parseExpression(String.format(BalFileConstants.FIELD_FORMAT_JOIN_FIELD_MANY,
                                    relation.getRelatedType(), relation.getRelatedInstance(),
                                    relation.getRefTable(), refFieldsString, joinColumnsString))));
                }
                if (relation.isChild()) {
                    if (relation.isParentIncluded()) {
                        relationsEnum.addMember(NodeParser.parseExpression(String.format(ENUM_ENTRY,
                                relation.getRelatedType(), relation.getRelatedInstance())));
                        inclusions = true;
                    }
                    for (FieldMetaData fieldMetaData : relation.getRelatedFields()) {
                        if (!subFields.isEmpty()) {
                            subFields.add(NodeFactory.createBasicLiteralNode(SyntaxKind.STRING_LITERAL,
                                    AbstractNodeFactory.createLiteralValueToken(
                                            SyntaxKind.STRING_LITERAL, COMMA_SPACE
                                                    + System.lineSeparator(), NodeFactory.createEmptyMinutiaeList(),
                                            NodeFactory.createEmptyMinutiaeList())));
                        }
                        String fieldAccesssString = FIELD_ACCESSS;
                        if (relation.relationType == Relation.RelationType.MANY) {
                            fieldAccesssString = ARRAY_TYPE + FIELD_ACCESSS;
                            manyRelation = true;
                        }
                        subFields.add(NodeFactory.createSpecificFieldNode(null,
                                AbstractNodeFactory.createIdentifierToken(DOUBLE_QUOTE +
                                        relation.getRelatedInstance() + fieldAccesssString + fieldMetaData.
                                        getFieldName() +
                                        DOUBLE_QUOTE), SyntaxTreeConstants.SYNTAX_TREE_COLON,
                                NodeParser.parseExpression(String.format(
                                        BalFileConstants.FIELD_FORMAT_RELATED_CHILD_FIELD,
                                        fieldMetaData.getFieldType().trim().replaceAll(
                                                SINGLE_QUOTE, BalFileConstants.EMPTY_STRING),
                                        relation.getRelatedInstance(), relation.getRefTable(),
                                        fieldMetaData.getFieldName().replaceAll(SINGLE_QUOTE, EMPTY_STRING)
                                ))));
                    }
                } else {
                    relationsEnum.addMember(NodeParser.parseExpression(String.format(ENUM_ENTRY,
                            relation.getRelatedType(), relation.getRelatedInstance())));
                    inclusions = true;
                    for (FieldMetaData fieldMetaData : relation.getRelatedFields()) {
                        if (!subFields.isEmpty()) {
                            subFields.add(NodeFactory.createBasicLiteralNode(SyntaxKind.STRING_LITERAL,
                                    AbstractNodeFactory.createLiteralValueToken(SyntaxKind.STRING_LITERAL,
                                            COMMA_SPACE + System.lineSeparator(),
                                            NodeFactory.createEmptyMinutiaeList(),
                                            NodeFactory.createEmptyMinutiaeList())));
                        }
                        if (relation.getReferences().contains(fieldMetaData.getFieldName())) {
                            int index = relation.getReferences().indexOf(fieldMetaData.getFieldName());
                            subFields.add(NodeFactory.createSpecificFieldNode(null,
                                    AbstractNodeFactory.createIdentifierToken(DOUBLE_QUOTE +
                                            relation.getRelatedInstance() + FIELD_ACCESSS
                                            + fieldMetaData.getFieldName()
                                            + DOUBLE_QUOTE), SyntaxTreeConstants.SYNTAX_TREE_COLON,
                                    NodeParser.parseExpression(String.format(BalFileConstants
                                            .FIELD_FORMAT_RELATED_PARENT_FIELD, relation.getKeyColumns()
                                            .get(index), fieldMetaData.getFieldType(), relation
                                            .getRelatedInstance(), relation.getRefTable(), fieldMetaData.getFieldName()
                                            .replaceAll(SINGLE_QUOTE, EMPTY_STRING)
                                    ))));
                        } else {
                            subFields.add(NodeFactory.createSpecificFieldNode(null,
                                    AbstractNodeFactory.createIdentifierToken(DOUBLE_QUOTE +
                                            relation.getRelatedInstance() + FIELD_ACCESSS
                                            + fieldMetaData.getFieldName()
                                            + DOUBLE_QUOTE), SyntaxTreeConstants.SYNTAX_TREE_COLON,
                                    NodeParser.parseExpression(String.format(
                                            BalFileConstants.FIELD_FORMAT_RELATED_PARENT_FIELD_WOUT_COLUMN_NAME,
                                            fieldMetaData.getFieldType(), relation.getRelatedInstance(),
                                            relation.getRefTable(), fieldMetaData.getFieldName().replaceAll(
                                                    SINGLE_QUOTE, EMPTY_STRING)
                                    ))));
                        }
                    }

                }
            }
        }
        if (hasTime) {
            if (importsArray.isEmpty()) {
                importsArray.add(getImportDeclarationNode(BalFileConstants.KEYWORD_BALLERINA,
                        BalFileConstants.KEYWORD_TIME));
            }
            imports = imports.add(getImportDeclarationNode(BalFileConstants.KEYWORD_BALLERINA,
                    BalFileConstants.KEYWORD_TIME));
        }
        imports = imports.add(getImportDeclarationNode(BalFileConstants.KEYWORD_BALLERINA, BalFileConstants.PERSIST));
        Class client = createClientClass(entity, className, subFields, joinSubFields, keys, keyType, keyAutoInc,
                inclusions);

        moduleMembers = moduleMembers.add(client.getClassDefinitionNode());

        if (inclusions) {
            moduleMembers = moduleMembers.add(relationsEnum.getEnumDeclarationNode());
        }

        Class clientStream = createClientStreamClass(entity, className, manyRelation);

        moduleMembers = moduleMembers.add(clientStream.getClassDefinitionNode());

        Token eofToken = AbstractNodeFactory.createIdentifierToken(BalFileConstants.EMPTY_STRING);
        ModulePartNode modulePartNode = NodeFactory.createModulePartNode(imports, moduleMembers, eofToken);
        TextDocument textDocument = TextDocuments.from(BalFileConstants.EMPTY_STRING);
        SyntaxTree balTree = SyntaxTree.from(textDocument);

        return balTree.modifyWith(modulePartNode);
    }


    private static Class createClientClass(Entity entity, String className, List<Node> subFields,
                                           List<Node> joinSubFields, HashMap<String, String> keys,
                                           String keyType, boolean keyAutoInc, boolean inclusions) {
        Class client = new Class(className + KEYWORD_CLIENT_CLASS, true);
        client.addQualifiers(new String[]{BalFileConstants.KEYWORD_CLIENT});
        client.addMember(NodeFactory.createTypeReferenceNode(
                AbstractNodeFactory.createToken(SyntaxKind.ASTERISK_TOKEN),
                NodeFactory.createQualifiedNameReferenceNode(
                        NodeFactory.createIdentifierToken(BalFileConstants.InheritedTypeReference.PERSIST_MODULE_NAME),
                        AbstractNodeFactory.createToken(SyntaxKind.COLON_TOKEN),
                        NodeFactory.createIdentifierToken(
                                BalFileConstants.InheritedTypeReference.ABSTRACT_PERSIST_CLIENT)
                ),
                AbstractNodeFactory.createToken(SyntaxKind.SEMICOLON_TOKEN)), false);
        client.addMember(NodeFactory.createBasicLiteralNode(SyntaxKind.STRING_LITERAL,
                AbstractNodeFactory.createLiteralValueToken(SyntaxKind.STRING_LITERAL, SPACE,
                        NodeFactory.createEmptyMinutiaeList(), NodeFactory.createEmptyMinutiaeList())), false);
        client.addMember(TypeDescriptor.getObjectFieldNode(BalFileConstants.KEYWORD_PRIVATE,
                        new String[]{BalFileConstants.KEYWORD_FINAL},
                        TypeDescriptor.getBuiltinSimpleNameReferenceNode(BalFileConstants.KEYWORD_STRING),
                        KEYWORD_ENTITY_NAME, NodeFactory.createBasicLiteralNode(SyntaxKind.STRING_LITERAL,
                                AbstractNodeFactory.createLiteralValueToken(SyntaxKind.STRING_LITERAL,
                                        DOUBLE_QUOTE + entity.getEntityName()
                                                + DOUBLE_QUOTE,
                                        NodeFactory.createEmptyMinutiaeList(),
                                        NodeFactory.createEmptyMinutiaeList()))),
                true);
        client.addMember(TypeDescriptor.getObjectFieldNode(BalFileConstants.KEYWORD_PRIVATE,
                        new String[]{BalFileConstants.KEYWORD_FINAL},
                        TypeDescriptor.getQualifiedNameReferenceNode(KEYWORD_SQL, KEYWORD_PARAMETERIZED_QUERY),
                        KEYWORD_TABLE_NAME, NodeFactory.createBasicLiteralNode(SyntaxKind.STRING_LITERAL,
                                AbstractNodeFactory.createLiteralValueToken(SyntaxKind.STRING_LITERAL,
                                        APOSTROPHE + entity.getTableName() + APOSTROPHE,
                                        NodeFactory.createEmptyMinutiaeList(),
                                        NodeFactory.createEmptyMinutiaeList()))),
                false);

        client.addMember(TypeDescriptor.getObjectFieldNode(BalFileConstants.KEYWORD_PRIVATE,
                new String[]{BalFileConstants.KEYWORD_FINAL},
                TypeDescriptor.getSimpleNameReferenceNode(BalFileConstants.TYPE_FIELD_METADATA_MAP),
                BalFileConstants.TAG_FIELD_METADATA, NodeFactory.createMappingConstructorExpressionNode(
                        SyntaxTreeConstants.SYNTAX_TREE_OPEN_BRACE, AbstractNodeFactory
                                .createSeparatedNodeList(subFields),
                        SyntaxTreeConstants.SYNTAX_TREE_CLOSE_BRACE)), true);

        StringBuilder keysString = new StringBuilder();
        for (String key : entity.getKeys()) {
            if (keysString.length() > 0) {
                keysString.append(COMMA_SPACE);
            }
            keysString.append(key);
        }

        client.addMember(TypeDescriptor.getObjectFieldNode(BalFileConstants.KEYWORD_PRIVATE, new String[]{},
                TypeDescriptor.getArrayTypeDescriptorNode(BalFileConstants.KEYWORD_STRING),
                KEYWORD_KEYFIELDS, NodeFactory.createListConstructorExpressionNode(
                        SyntaxTreeConstants.SYNTAX_TREE_OPEN_BRACKET, AbstractNodeFactory
                                .createSeparatedNodeList(NodeFactory.createBasicLiteralNode(SyntaxKind.STRING_LITERAL,
                                        AbstractNodeFactory.createLiteralValueToken(SyntaxKind.STRING_LITERAL,
                                                keysString.toString(), NodeFactory.createEmptyMinutiaeList(),
                                                NodeFactory.createEmptyMinutiaeList())))
                        , SyntaxTreeConstants.SYNTAX_TREE_CLOSE_BRACKET)
        ), false);
        if (!joinSubFields.isEmpty()) {
            client.addMember(TypeDescriptor.getObjectFieldNode(BalFileConstants.KEYWORD_PRIVATE,
                    new String[]{BalFileConstants.KEYWORD_FINAL},
                    TypeDescriptor.getSimpleNameReferenceNode(BalFileConstants.TYPE_JOIN_METADATA_MAP),
                    BalFileConstants.TAG_JOIN_METADATA, NodeFactory.createMappingConstructorExpressionNode(
                            SyntaxTreeConstants.SYNTAX_TREE_OPEN_BRACE, AbstractNodeFactory
                                    .createSeparatedNodeList(joinSubFields),
                            SyntaxTreeConstants.SYNTAX_TREE_CLOSE_BRACE)), true);
        }
        client.addMember(TypeDescriptor.getObjectFieldNodeWithoutExpression(BalFileConstants.KEYWORD_PRIVATE,
                        new String[]{},
                        TypeDescriptor.getQualifiedNameReferenceNode(BalFileConstants.PERSIST, KEYWORD_SQL_CLIENT),
                        BalFileConstants.PERSIST_CLIENT),
                true);

        Function init = getInitMethod(joinSubFields);
        client.addMember(init.getFunctionDefinitionNode(), true);
        Function create = getCreateMethod(entity, joinSubFields, keys, keyAutoInc, keyType);
        client.addMember(create.getFunctionDefinitionNode(), true);
        StringBuilder keyString = new StringBuilder();
        keyString.append(BalFileConstants.KEY);
        if (keys.size() > 1) {
            keyString = new StringBuilder();
            for (String key : keys.keySet()) {
                keyString.append(keys.get(key));
                keyString.append(SPACE);
                keyString.append(key);
                keyString.append(SEMICOLON);
            }
        }
        Function readByKey = getReadByKeyMethod(entity, keys, inclusions, keyType, keyString);
        client.addMember(readByKey.getFunctionDefinitionNode(), true);

        Function read = getReadMethod(entity, inclusions, className);
        client.addMember(read.getFunctionDefinitionNode(), true);

        Function execute = getExecuteMethod(entity, className);
        client.addMember(execute.getFunctionDefinitionNode(), true);

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
                keyStringExist.append(entity.getEntityName().substring(0, 1).toLowerCase());
                keyStringExist.append(entity.getEntityName().substring(1));
                keyStringExist.append(FIELD_ACCESSS);
                keyStringExist.append(key);
            }
            keyStringExist.append(END_RECORD);

        } else {
            keyStringExist = new StringBuilder();
            keyStringExist.append(entity.getEntityName().substring(0, 1).toLowerCase());
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

        Function initStream = new Function(BalFileConstants.INIT);
        initStream.addQualifiers(new String[]{BalFileConstants.KEYWORD_PUBLIC, BalFileConstants.KEYWORD_ISOLATED});
        initStream.addStatement(NodeParser.parseStatement(BalFileConstants.INIT_STREAM_STATEMENT));
        initStream.addStatement(NodeParser.parseStatement(SELF_ERR));
        initStream.addRequiredParameter(NodeParser.parseTypeDescriptor(NULLABLE_ANYDATA_STREAM_TYPE), ANYDATA_KETWORD);
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

        Function nextStream = new Function(BalFileConstants.NEXT);
        nextStream.addQualifiers(new String[]{BalFileConstants.KEYWORD_PUBLIC, BalFileConstants.KEYWORD_ISOLATED});
        nextStream.addReturns(NodeParser.parseTypeDescriptor(String.format(
                BalFileConstants.NEXT_STREAM_RETURN_TYPE, entity.getEntityName())));

        IfElse errorCheck = new IfElse(NodeParser.parseExpression(ERR_IS_ERROR));
        errorCheck.addIfStatement(NodeParser.parseStatement(RETURN_CASTED_ERROR));
        IfElse streamCheck = new IfElse(NodeParser.parseExpression(ANYDATASTREAM_IS_STREAM_TYPE));

        streamCheck.addIfStatement(NodeParser.parseStatement(CAST_ANYDATA_STREAM));
        streamCheck.addIfStatement(NodeParser.parseStatement(ANYDATA_STREAM_NEXT));

        IfElse streamValueNilCheck = new IfElse(NodeParser.parseExpression(
                BalFileConstants.NEXT_STREAM_IF_STATEMENT));
        streamValueNilCheck.addIfStatement(NodeParser.parseStatement(
                BalFileConstants.NEXT_STREAM_RETURN_STREAM_VALUE));
        IfElse streamValueErrorCheck = new IfElse(NodeParser.parseExpression(
                BalFileConstants.NEXT_STREAM_ELSE_IF_STATEMENT));
        streamValueErrorCheck.addIfStatement(NodeParser.parseStatement(
                BalFileConstants.NEXT_STREAM_RETURN_STREAM_VALUE_ERROR));
        streamValueErrorCheck.addElseStatement(NodeParser.parseStatement(String.format(
                BalFileConstants.NEXT_STREAM_ELSE_STATEMENT, entity.getEntityName(), entity.getEntityName())));
        if (hasManyRelation) {
            streamValueErrorCheck.addElseStatement(NodeParser.parseStatement(String.format(GET_MANY_RELATIONS,
                    className)));
        }
        streamValueErrorCheck.addElseStatement(NodeParser.parseStatement(BalFileConstants.RETURN_NEXT_RECORD));
        streamValueNilCheck.addElseBody(streamValueErrorCheck);
        streamCheck.addIfStatement(streamValueNilCheck.getIfElseStatementNode());
        streamCheck.addElseStatement(NodeParser.parseStatement(RETURN_NILL));
        errorCheck.addElseBody(streamCheck);
        nextStream.addIfElseStatement(errorCheck.getIfElseStatementNode());
        clientStream.addMember(nextStream.getFunctionDefinitionNode(), true);

        Function closeStream = new Function(BalFileConstants.CLOSE);
        closeStream.addQualifiers(new String[]{BalFileConstants.KEYWORD_PUBLIC, BalFileConstants.KEYWORD_ISOLATED});
        closeStream.addReturns(TypeDescriptor.getOptionalTypeDescriptorNode(BalFileConstants.EMPTY_STRING,
                PERSIST_ERROR));
        streamCheck = new IfElse(NodeParser.parseExpression(ANYDATASTREAM_IS_STREAM_TYPE));
        streamCheck.addIfStatement(NodeParser.parseStatement(CAST_ANYDATA_STREAM));
        streamCheck.addIfStatement(NodeParser.parseStatement(BalFileConstants.CLOSE_STREAM_STATEMENT));
        IfElse sqlErrorCheck = new IfElse(NodeParser.parseExpression(IS_SQL_ERROR));
        sqlErrorCheck.addIfStatement(NodeParser.parseStatement(RETURN_PERSIST_ERROR_CLOSE_STREAM));
        streamCheck.addIfStatement(sqlErrorCheck.getIfElseStatementNode());
        closeStream.addIfElseStatement(streamCheck.getIfElseStatementNode());
        clientStream.addMember(closeStream.getFunctionDefinitionNode(), true);
        return clientStream;
    }

    private static Function getInitMethod(List<Node> joinSubFields) {
        Function init = new Function(BalFileConstants.INIT);
        init.addQualifiers(new String[]{BalFileConstants.KEYWORD_PUBLIC});
        init.addReturns(TypeDescriptor.getOptionalTypeDescriptorNode(BalFileConstants.EMPTY_STRING,
                BalFileConstants.PERSIST_ERROR));
        init.addStatement(NodeParser.parseStatement(BalFileConstants.INIT_MYSQL_CLIENT));
        IfElse errorCheck = new IfElse(NodeParser.parseExpression(DB_CLIENT_IS_DB_CLIENT));
        errorCheck.addIfStatement(NodeParser.parseStatement(RETURN_PERSIST_ERROR_FROM_DBCLIENT));
        init.addIfElseStatement(errorCheck.getIfElseStatementNode());
        if (!joinSubFields.isEmpty()) {
            init.addStatement(NodeParser.parseStatement(BalFileConstants.INIT_PERSIST_CLIENT_RELATED));
        } else {
            init.addStatement(NodeParser.parseStatement(BalFileConstants.INIT_PERSIST_CLIENT));
        }
        return init;
    }

    private static Function getCreateMethod(Entity entity, List<Node> joinSubFields,
                                            HashMap<String, String> keys, boolean keyAutoInc, String keyType) {
        Function create = new Function(BalFileConstants.CREATE);
        create.addRequiredParameter(
                TypeDescriptor.getSimpleNameReferenceNode(entity.getEntityName()), KEYWORD_VALUE);
        create.addQualifiers(new String[]{BalFileConstants.KEYWORD_REMOTE});
        create.addReturns(TypeDescriptor.getUnionTypeDescriptorNode(
                TypeDescriptor.getSimpleNameReferenceNode(entity.getEntityName()),
                TypeDescriptor.getQualifiedNameReferenceNode(PERSIST, SPECIFIC_ERROR)));
        if (joinSubFields.isEmpty()) {
            StringBuilder retRecord = new StringBuilder();
            create.addStatement(NodeParser.parseStatement(BalFileConstants.CREATE_SQL_RESULTS));
            if (keys.size() > 1) {
                for (FieldMetaData map : entity.getFields()) {
                    if (retRecord.length() > 0) {
                        retRecord.append(COMMA_SPACE);
                    }
                    if (map.isAutoGenerated()) {
                        retRecord.append(String.format(BalFileConstants.RECORD_FIELD_LAST_INSERT_ID,
                                map.getFieldName(), BalFileConstants.KEYWORD_INT));
                    } else {
                        retRecord.append(String.format(BalFileConstants.RECORD_FIELD_VALUE,
                                map.getFieldName(),
                                map.getFieldName()));
                    }
                }
                if (keyAutoInc) {
                    create.addStatement(NodeParser.parseStatement(String.format(BalFileConstants.RETURN_RECORD_VARIABLE,
                            retRecord)));
                } else {
                    create.addStatement(NodeParser.parseStatement(String.format(BalFileConstants.RETURN_VARIABLE,
                            BalFileConstants.VALUE)));
                }
            } else {
                for (FieldMetaData map : entity.getFields()) {
                    if (retRecord.length() > 0) {
                        retRecord.append(COMMA_SPACE);
                    }
                    if (map.getFieldName().equals(entity.getKeys()[0].trim().replaceAll(
                            DOUBLE_QUOTE, BalFileConstants.EMPTY_STRING))) {
                        retRecord.append(String.format(BalFileConstants.RECORD_FIELD_LAST_INSERT_ID,
                                map.getFieldName(), keyType));
                    } else {
                        retRecord.append(String.format(BalFileConstants.RECORD_FIELD_VALUE,
                                map.getFieldName(),
                                map.getFieldName()));
                    }
                }
                if (!keyAutoInc) {
                    IfElse valueNilCheck = new IfElse(NodeParser.parseExpression(
                            BalFileConstants.LAST_RETURN_ID_NULL_CHECK));
                    valueNilCheck.addIfStatement(NodeParser.parseStatement(BalFileConstants.RETURN_VALUE));
                    create.addIfElseStatement(valueNilCheck.getIfElseStatementNode());

                }
                create.addStatement(NodeParser.parseStatement(String.format(BalFileConstants.RETURN_RECORD_VARIABLE,
                        retRecord)));
            }
        } else {
            for (Relation relation : entity.getRelations()) {
                if (relation.isChild()) {
                    continue;
                }
                IfElse valueCheck = new IfElse(NodeParser.parseExpression(String.format(VALUE_TYPE_CHECK,
                        relation.getRelatedInstance(), relation.getRelatedType())));
                valueCheck.addIfStatement(NodeParser.parseStatement(String.format(GET_NEW_CLIENT,
                        relation.getRelatedType(), relation.getRelatedType().substring(0, 1).toLowerCase() +
                                relation.getRelatedType().substring(1), relation.getRelatedType())));
                valueCheck.addIfStatement(NodeParser.parseStatement(String.format(CHECK_EXISTENCE,
                        relation.getRelatedType().substring(0, 1).toLowerCase() +
                                relation.getRelatedType().substring(1),
                        relation.getRelatedType(), relation.getRelatedInstance())));
                IfElse checkExistence = new IfElse(NodeParser.parseExpression(NOT_EXIST));
                checkExistence.addIfStatement(NodeParser.parseStatement(String.format(CREATE_CLIENT,
                        relation.getRelatedInstance(), relation.getRelatedType().substring(0, 1).toLowerCase()
                                + relation.getRelatedType().substring(1), relation.getRelatedType(),
                        relation.getRelatedInstance())));
                valueCheck.addIfStatement(checkExistence.getIfElseStatementNode());
                create.addIfElseStatement(valueCheck.getIfElseStatementNode());
            }
            create.addStatement(NodeParser.parseStatement(BalFileConstants.CREATE_SQL_RESULTS_RELATION));
            create.addStatement(NodeParser.parseStatement(RETURN_VAUE));
        }
        return create;
    }

    private static Function getReadByKeyMethod(Entity entity, HashMap<String, String> keys, boolean inclusions,
                                               String keyType, StringBuilder keyString) {
        Function readByKey = new Function(BalFileConstants.READ_BY_KEY);
        if (keys.size() > 1) {
            readByKey.addRequiredParameter(
                    NodeParser.parseTypeDescriptor(String.format(BalFileConstants.CLOSE_RECORD_VARIABLE, keyString)),
                    BalFileConstants.KEY);
        } else {
            readByKey.addRequiredParameter(
                    TypeDescriptor.getBuiltinSimpleNameReferenceNode(keyType), BalFileConstants.KEY);
        }
        readByKey.addQualifiers(new String[]{BalFileConstants.KEYWORD_REMOTE});
        readByKey.addReturns(TypeDescriptor.getUnionTypeDescriptorNode(
                TypeDescriptor.getSimpleNameReferenceNode(entity.getEntityName()),
                TypeDescriptor.getQualifiedNameReferenceNode(PERSIST, SPECIFIC_ERROR)));

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
        Function read = new Function(BalFileConstants.READ);
        read.addQualifiers(new String[]{BalFileConstants.KEYWORD_REMOTE});
        read.addReturns(TypeDescriptor.getStreamTypeDescriptorNode(
                TypeDescriptor.getSimpleNameReferenceNode(entity.getEntityName()),
                TypeDescriptor.getOptionalTypeDescriptorNode(BalFileConstants.EMPTY_STRING,
                        BalFileConstants.PERSIST_ERROR)));
        if (inclusions) {
            read.addRequiredParameterWithDefault(NodeParser.parseTypeDescriptor(String.format(
                            ENTITY_RELATIONS_ARRAY, entity.getEntityName())),
                    INCLUDE, Function.Bracket.SQUARE);
            read.addStatement(NodeParser.parseStatement(String.format(BalFileConstants.READ_RUN_READ_QUERY_RELATED,
                    entity.getEntityName())));

        } else {
            read.addStatement(NodeParser.parseStatement(String.format(BalFileConstants.READ_RUN_READ_QUERY,
                    entity.getEntityName())));
        }
        IfElse errorCheck = new IfElse(NodeParser.parseExpression(RESULT_IS_ERROR));
        errorCheck.addIfStatement(NodeParser.parseStatement(String.format(
                BalFileConstants.READ_RETURN_STREAM_WHEN_ERROR, entity.getEntityName(), className)));
        errorCheck.addElseStatement(NodeParser.parseStatement(String.format(
                BalFileConstants.READ_RETURN_STREAM_WHEN_NOT_ERROR, entity.getEntityName(), className)));

        read.addIfElseStatement(errorCheck.getIfElseStatementNode());
        return read;
    }

    private static Function getExecuteMethod(Entity entity, String className) {
        Function execute = new Function(BalFileConstants.EXECUTE);
        execute.addQualifiers(new String[]{BalFileConstants.KEYWORD_REMOTE});
        execute.addRequiredParameter(TypeDescriptor.getQualifiedNameReferenceNode(KEYWORD_SQL,
                KEYWORD_PARAMETERIZED_QUERY), KEYWORD_FILTER_CLAUSE);
        execute.addReturns(TypeDescriptor.getStreamTypeDescriptorNode(
                TypeDescriptor.getSimpleNameReferenceNode(entity.getEntityName()),
                TypeDescriptor.getOptionalTypeDescriptorNode(BalFileConstants.EMPTY_STRING,
                        BalFileConstants.PERSIST_ERROR)));
        execute.addStatement(NodeParser.parseStatement(String.format(BalFileConstants.EXECUTE_RUN_EXECUTE_QUERY,
                entity.getEntityName())));

        IfElse errorCheck = new IfElse(NodeParser.parseExpression(RESULT_IS_ERROR));
        errorCheck.addIfStatement(NodeParser.parseStatement(String.format(
                BalFileConstants.READ_RETURN_STREAM_WHEN_ERROR, entity.getEntityName(),
                className)));
        errorCheck.addElseStatement(NodeParser.parseStatement(String.format(
                BalFileConstants.READ_RETURN_STREAM_WHEN_NOT_ERROR, entity.getEntityName(),
                className)));
        execute.addIfElseStatement(errorCheck.getIfElseStatementNode());
        return execute;
    }

    private static Function getUpdateMethod(Entity entity, List<Node> joinSubFields) {
        Function update = new Function(BalFileConstants.UPDATE);
        update.addQualifiers(new String[]{BalFileConstants.KEYWORD_REMOTE});
        update.addReturns(TypeDescriptor.getOptionalTypeDescriptorNode(BalFileConstants.EMPTY_STRING,
                BalFileConstants.PERSIST_ERROR));
        update.addStatement(NodeParser.parseStatement(BalFileConstants.UPDATE_RUN_UPDATE_QUERY));
        update.addRequiredParameter(TypeDescriptor.getSimpleNameReferenceNode(entity.getEntityName()), VALUE);
        if (!joinSubFields.isEmpty()) {
            for (Relation relation : entity.getRelations()) {
                if (relation.isChild()) {
                    continue;
                }
                IfElse typeCheck = new IfElse(NodeParser.parseExpression(String.format(RECORD_CHECK,
                        relation.getRelatedInstance())));
                typeCheck.addIfStatement(NodeParser.parseStatement(String.format(GET_ENTITY_RECORD,
                        relation.getRelatedType(), relation.getRelatedType().substring(0, 1)
                                .toLowerCase() + relation.getRelatedType().substring(1), relation.getRelatedType(),
                        relation.getRelatedInstance())));
                typeCheck.addIfStatement(NodeParser.parseStatement(String.format(GET_ENTITY_CLIENT,
                        relation.getRelatedType(), relation.getRelatedType().substring(0, 1)
                                .toLowerCase() + relation.getRelatedType().substring(1), relation.getRelatedType())));
                typeCheck.addIfStatement(NodeFactory.createExpressionStatementNode(SyntaxKind.ACTION_STATEMENT,
                        NodeFactory.createCheckExpressionNode(
                                SyntaxKind.CHECK_ACTION,
                                SyntaxTreeConstants.SYNTAX_TREE_KEYWORD_CHECK,
                                NodeParser.parseActionOrExpression(String.format(CHECK_UPDATE_STATEMENT,
                                        relation.getRelatedType().substring(0, 1)
                                                .toLowerCase() + relation.getRelatedType().substring(1),
                                        relation.getRelatedType().substring(0, 1).toLowerCase() +
                                                relation.getRelatedType().substring(1)
                                ))
                        ), SYNTAX_TREE_SEMICOLON));
                update.addIfElseStatement(typeCheck.getIfElseStatementNode());
            }
        }
        return update;
    }

    private static Function getDeleteMethod(Entity entity) {
        Function delete = new Function(BalFileConstants.DELETE);
        delete.addRequiredParameter(TypeDescriptor.getSimpleNameReferenceNode(entity.getEntityName()), KEYWORD_VALUE);
        delete.addQualifiers(new String[]{BalFileConstants.KEYWORD_REMOTE});
        delete.addReturns(TypeDescriptor.getOptionalTypeDescriptorNode(BalFileConstants.EMPTY_STRING,
                BalFileConstants.PERSIST_ERROR));
        delete.addStatement(NodeParser.parseStatement(BalFileConstants.DELETE_RUN_DELETE_QUERY));
        return delete;
    }

    private static Function getExistMethod(Entity entity, StringBuilder keyString) {
        Function exists = new Function(BalFileConstants.EXISTS);
        exists.addRequiredParameter(TypeDescriptor.getSimpleNameReferenceNode(entity.getEntityName()),
                entity.getEntityName().substring(0, 1).toLowerCase() + entity.getEntityName().substring(1));
        exists.addQualifiers(new String[]{BalFileConstants.KEYWORD_REMOTE});
        exists.addReturns(TypeDescriptor.getUnionTypeDescriptorNode(
                TypeDescriptor.getBuiltinSimpleNameReferenceNode(KEYWORD_BOOLEAN),
                TypeDescriptor.getQualifiedNameReferenceNode (PERSIST, SPECIFIC_ERROR)));
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
        Function close = new Function(BalFileConstants.CLOSE);
        close.addQualifiers(new String[]{BalFileConstants.KEYWORD_PUBLIC});
        close.addReturns(TypeDescriptor.getOptionalTypeDescriptorNode(BalFileConstants.EMPTY_STRING,
                BalFileConstants.PERSIST_ERROR));
        close.addStatement(NodeParser.parseStatement(BalFileConstants.CLOSE_PERSIST_CLIENT));
        return close;
    }

    public static String generateDatabaseConfigSyntaxTree() throws FormatterException {
        NodeList<ImportDeclarationNode> imports = AbstractNodeFactory.createEmptyNodeList();
        NodeList<ModuleMemberDeclarationNode> moduleMembers = AbstractNodeFactory.createEmptyNodeList();

        imports = imports.add(NodeParser.parseImportDeclaration(MYSQL_DRIVER_IMPORT));
        moduleMembers = moduleMembers.add(NodeParser.parseModuleMemberDeclaration(
                BalFileConstants.CONFIGURABLE_PORT));
        moduleMembers = moduleMembers.add(NodeParser.parseModuleMemberDeclaration(
                BalFileConstants.CONFIGURABLE_HOST));
        moduleMembers = moduleMembers.add(NodeParser.parseModuleMemberDeclaration(
                BalFileConstants.CONFIGURABLE_USER));
        moduleMembers = moduleMembers.add(NodeParser.parseModuleMemberDeclaration(
                BalFileConstants.CONFIGURABLE_DATABASE));
        moduleMembers = moduleMembers.add(NodeParser.parseModuleMemberDeclaration(
                BalFileConstants.CONFIGURABLE_PASSWORD));

        Token eofToken = AbstractNodeFactory.createIdentifierToken(BalFileConstants.EMPTY_STRING);
        ModulePartNode modulePartNode = NodeFactory.createModulePartNode(imports, moduleMembers, eofToken);
        TextDocument textDocument = TextDocuments.from(BalFileConstants.EMPTY_STRING);
        SyntaxTree balTree = SyntaxTree.from(textDocument);
        String content = Formatter.format(balTree.modifyWith(modulePartNode).toSourceCode());

        return content; // output cannot be SyntaxTree as it will overlap with Toml Syntax Tree in Init Command
    }

    private static String[] getArray(ArrayList<String> arrL) {
        String[] output = new String[arrL.size()];
        for (int i = 0; i < arrL.size(); i++) {
            output[i] = arrL.get(i);
        }
        return output;
    }

    public static ImportDeclarationNode getImportDeclarationNode(String orgName, String moduleName) {
        Token orgNameToken = AbstractNodeFactory.createIdentifierToken(orgName);
        ImportOrgNameNode importOrgNameNode = NodeFactory.createImportOrgNameNode(
                orgNameToken,
                SyntaxTreeConstants.SYNTAX_TREE_SLASH
        );
        Token moduleNameToken = AbstractNodeFactory.createIdentifierToken(moduleName);
        SeparatedNodeList<IdentifierToken> moduleNodeList =
                AbstractNodeFactory.createSeparatedNodeList(moduleNameToken);

        return NodeFactory.createImportDeclarationNode(
                SyntaxTreeConstants.SYNTAX_TREE_KEYWORD_IMPORT,
                importOrgNameNode,
                moduleNodeList,
                null,
                SYNTAX_TREE_SEMICOLON
        );
    }

    private static boolean checkAutoIncrement(MetadataNode metaD) {
        NodeList<AnnotationNode> annotations = metaD.annotations();
        for (AnnotationNode annotation : annotations) {
            Node annotReference = annotation.annotReference();
            if (annotReference.kind() == SyntaxKind.QUALIFIED_NAME_REFERENCE) {
                QualifiedNameReferenceNode qualifiedNameRef =
                        (QualifiedNameReferenceNode) annotReference;
                if (qualifiedNameRef.identifier().text().equals(KEYWORD_AUTOINCREMENT) && qualifiedNameRef
                        .modulePrefix().text().equals(BalFileConstants.PERSIST)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void generateRelations(ArrayList<Entity> entityArray) {
        ArrayList<String> entityNames = new ArrayList<>();
        for (Entity entity : entityArray) {
            entityNames.add(entity.getEntityName());
        }
        for (Entity entity : entityArray) {
            for (Relation relation : entity.getRelations()) {
                if (relation.getRelatedType().contains(COLON)) {
                    relation.setRelatedType(relation.getRelatedType().split(COLON, 2)[1]);
                }
                int counter;
                if (!relation.isChild()) {
                    for (Entity childEntity : entityArray) {
                        if (entity.getEntityName().equals(childEntity.getEntityName())) {
                            continue;
                        }
                        if (childEntity.getEntityName().equals(relation.getRelatedType())) {
                            if (relation.getKeyColumns().isEmpty() && relation.getReferences().isEmpty()) {
                                for (String key : childEntity.getKeys()) {
                                    String modifiedKey = key.replace(DOUBLE_QUOTE, EMPTY_STRING).trim();
                                    relation.getKeyColumns().add(
                                            childEntity.getEntityName().toLowerCase() + modifiedKey.substring(0, 1)
                                                    .toUpperCase() + modifiedKey.substring(1));
                                    relation.getReferences().add(modifiedKey);
                                }
                            } else if (relation.getKeyColumns().isEmpty()) {
                                for (String key : childEntity.getKeys()) {
                                    String modifiedKey = key.replace(DOUBLE_QUOTE, EMPTY_STRING).trim();
                                    relation.getKeyColumns().add(
                                            childEntity.getEntityName().toLowerCase() + modifiedKey.substring(0, 1)
                                                    .toUpperCase() + modifiedKey.substring(1));
                                }
                            } else if (relation.getReferences().isEmpty()) {
                                for (String key : childEntity.getKeys()) {
                                    String modifiedKey = key.replace(DOUBLE_QUOTE, EMPTY_STRING).trim();
                                    relation.getReferences().add(modifiedKey);
                                }
                            }
                            int relationIndex = childEntity.getRelations().size();
                            childEntity.getRelations().add(relationIndex, new Relation(entity.getEntityName(), null,
                                    new ArrayList<>(), new ArrayList<>(), true));
                            childEntity.getRelations().get(relationIndex).setRefTable(entity.getTableName());
                            relation.setRefTable(childEntity.getTableName());
                            boolean childIncludesParentColumns = false;
                            ArrayList<Integer> indexesToRemove = new ArrayList<>();
                            for (FieldMetaData fieldMetaData : entity.getFields()) {
                                if (fieldMetaData.getFieldType().contains(COLON)) {
                                    if (entityNames.contains(fieldMetaData.getFieldType().split(COLON, 2)[1])) {
                                        fieldMetaData.setFieldType(fieldMetaData.getFieldType().split(COLON, 2)[1]);
                                        if (fieldMetaData.getRelationType().equals(Relation.RelationType.MANY)) {
                                            relation.relationType = Relation.RelationType.MANY;
                                        } else {
                                            childEntity.getRelations().get(relationIndex).relationType =
                                                    Relation.RelationType.ONE;
                                        }
                                    }
                                } else {
                                    if (entityNames.contains(fieldMetaData.getFieldType())) {
                                        if (fieldMetaData.getRelationType().equals(Relation.RelationType.MANY)) {
                                            relation.relationType = Relation.RelationType.MANY;
                                        } else {
                                            childEntity.getRelations().get(relationIndex).relationType =
                                                    Relation.RelationType.ONE;
                                        }
                                    }
                                }
                                if (!fieldMetaData.getFieldType().equals(childEntity.getEntityName())) {
                                    if (!entityNames.contains(fieldMetaData.getFieldType())) {
                                        childEntity.getRelations().get(relationIndex).getRelatedFields()
                                                .add(fieldMetaData);
                                    }
                                } else {
                                    indexesToRemove.add(entity.getFields().indexOf(fieldMetaData));
                                }
                            }
                            for (Integer index : indexesToRemove) {
                                entity.getFields().remove(index.intValue());
                            }
                            indexesToRemove = new ArrayList<>();
                            counter = 0;
                            for (FieldMetaData fieldMetaData : childEntity.getFields()) {
                                if (fieldMetaData.getFieldType().contains(COLON)) {
                                    if (entityNames.contains(fieldMetaData.getFieldType().split(COLON, 2)[1])) {
                                        fieldMetaData.setFieldType(fieldMetaData.getFieldType().split(COLON,
                                                2)[1]);
                                        if (fieldMetaData.getRelationType().equals(Relation.RelationType.MANY)) {
                                            childEntity.getRelations().get(relationIndex).relationType =
                                                    Relation.RelationType.MANY;
                                        } else {
                                            childEntity.getRelations().get(relationIndex).relationType =
                                                    Relation.RelationType.ONE;
                                        }
                                    }
                                }

                                if (!fieldMetaData.getFieldType().equals(entity.getEntityName())) {
                                    if (!entityNames.contains(fieldMetaData.getFieldType())) {
                                        relation.getRelatedFields().add(counter, fieldMetaData);
                                        counter += 1;
                                    }
                                } else {
                                    childIncludesParentColumns = true;
                                    childEntity.getRelations().get(relationIndex).setParentIncluded(true);
                                    indexesToRemove.add(childEntity.getFields().indexOf(fieldMetaData));
                                    childEntity.getRelations().get(relationIndex).setRelatedInstance(
                                            fieldMetaData.getFieldName());
                                    if (fieldMetaData.getRelationType().equals(Relation.RelationType.MANY)) {
                                        childEntity.getRelations().get(relationIndex).relationType =
                                                Relation.RelationType.MANY;
                                    } else {
                                        childEntity.getRelations().get(relationIndex).relationType =
                                                Relation.RelationType.ONE;
                                    }
                                }
                            }
                            if (!childIncludesParentColumns) {
                                childEntity.getRelations().get(relationIndex).setRelatedFields(new ArrayList<>());
                            }
                            for (Integer index : indexesToRemove) {
                                childEntity.getFields().remove(index.intValue());
                            }
                            if (childEntity.getRelations().get(relationIndex).getKeyColumns().isEmpty() &&
                                    childEntity.getRelations().get(relationIndex).getReferences().isEmpty()) {
                                for (String key : entity.getKeys()) {
                                    String modifiedKey = key.replace(DOUBLE_QUOTE, EMPTY_STRING).trim();
                                    childEntity.getRelations().get(relationIndex).getKeyColumns().add(
                                            entity.getEntityName().toLowerCase() + modifiedKey.substring(0, 1)
                                                    .toUpperCase() + modifiedKey.substring(1));
                                    childEntity.getRelations().get(relationIndex).getReferences().add(modifiedKey);
                                }
                            } else if (childEntity.getRelations().get(relationIndex).getKeyColumns().isEmpty()) {
                                for (String key : entity.getKeys()) {
                                    String modifiedKey = key.replace(DOUBLE_QUOTE, EMPTY_STRING).trim();
                                    childEntity.getRelations().get(relationIndex).getKeyColumns().add(
                                            entity.getEntityName().toLowerCase() + modifiedKey.substring(0, 1)
                                                    .toUpperCase() + modifiedKey.substring(1));
                                }
                            } else if (childEntity.getRelations().get(relationIndex).getReferences().isEmpty()) {
                                for (String key : entity.getKeys()) {
                                    String modifiedKey = key.replace(DOUBLE_QUOTE, EMPTY_STRING).trim();
                                    childEntity.getRelations().get(relationIndex).getReferences().add(modifiedKey);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static ArrayList<ModuleMemberDeclarationNode> formatModuleMembers(
            ArrayList<ModuleMemberDeclarationNode> moduleMembers, ArrayList<Entity> entities) {
        ArrayList<String> entityNames = new ArrayList<>();
        ArrayList<ModuleMemberDeclarationNode> retModuleMembers = new ArrayList<>();
        for (Entity entity : entities) {
            entityNames.add(entity.getEntityName());
        }
        for (ModuleMemberDeclarationNode moduleMember : moduleMembers) {
            RecordTypeDescriptorNode recordDesc = (RecordTypeDescriptorNode) ((TypeDefinitionNode) moduleMember)
                    .typeDescriptor();
            MetadataNode metadataNode = null;
            if (((TypeDefinitionNode) moduleMember).metadata().isPresent()) {
                metadataNode = ((TypeDefinitionNode) moduleMember).metadata().get();
            }
            NodeList<Node> nodeList = NodeFactory.createEmptyNodeList();
            Token visibilityQualifier = null;
            if (((TypeDefinitionNode) moduleMember).visibilityQualifier().isPresent()) {
                visibilityQualifier = ((TypeDefinitionNode) moduleMember).visibilityQualifier().get();
            }
            Token typeKeyword = ((TypeDefinitionNode) moduleMember).typeKeyword();
            Token typeName = ((TypeDefinitionNode) moduleMember).typeName();
            for (Node node : recordDesc.fields()) {
                if (node.kind() == SyntaxKind.RECORD_FIELD_WITH_DEFAULT_VALUE) {
                    RecordFieldWithDefaultValueNode fieldNode = (RecordFieldWithDefaultValueNode) node;
                    String fName = fieldNode.fieldName().text().trim();
                    String fType = fieldNode.typeName().toSourceCode().trim();
                    if (fType.contains(COLON)) {
                        String[] sValues = fType.split(COLON);
                        if (entityNames.contains(sValues[1])) {
                            Optional<MetadataNode> fieldMetaData = ((RecordFieldWithDefaultValueNode) node).metadata();
                            Token readOnlyKeyWord = null;
                            if (((RecordFieldWithDefaultValueNode) node).readonlyKeyword().isPresent()) {
                                readOnlyKeyWord = ((RecordFieldWithDefaultValueNode) node).readonlyKeyword().get();
                            }
                            ExpressionNode expressionNode = ((RecordFieldWithDefaultValueNode) node).expression();
                            if (fieldMetaData.isPresent()) {
                                nodeList = nodeList.add(NodeFactory.createRecordFieldWithDefaultValueNode(
                                        fieldMetaData.get(),
                                        readOnlyKeyWord,
                                        TypeDescriptor.getBuiltinSimpleNameReferenceNode(sValues[1]),
                                        AbstractNodeFactory.createIdentifierToken(fName),
                                        SyntaxTreeConstants.SYNTAX_TREE_EQUAL,
                                        expressionNode,
                                        SyntaxTreeConstants.SYNTAX_TREE_SEMICOLON
                                ));
                            } else {
                                nodeList = nodeList.add(NodeFactory.createRecordFieldWithDefaultValueNode(
                                        null,
                                        readOnlyKeyWord,
                                        TypeDescriptor.getBuiltinSimpleNameReferenceNode(sValues[1]),
                                        AbstractNodeFactory.createIdentifierToken(fName),
                                        SyntaxTreeConstants.SYNTAX_TREE_EQUAL,
                                        expressionNode,
                                        SyntaxTreeConstants.SYNTAX_TREE_SEMICOLON
                                ));
                            }
                        } else {
                            nodeList = nodeList.add(node);
                        }
                    } else {
                        nodeList = nodeList.add(node);
                    }
                } else if (node.kind() == SyntaxKind.RECORD_FIELD) {
                    RecordFieldNode fieldNode = (RecordFieldNode) node;
                    String fName = fieldNode.fieldName().text().trim();
                    String fType = fieldNode.typeName().toSourceCode().trim();
                    if (fType.contains(COLON)) {
                        String[] sValues = fType.split(COLON);
                        if (entityNames.contains(sValues[1])) {
                            Optional<MetadataNode> fieldMetaData = ((RecordFieldNode) node).metadata();
                            Token readOnlyKeyWord = null;
                            Token questionMarkToken = null;
                            if (((RecordFieldNode) node).readonlyKeyword().isPresent()) {
                                readOnlyKeyWord = ((RecordFieldNode) node).readonlyKeyword().get();
                            }
                            if (((RecordFieldNode) node).questionMarkToken().isPresent()) {
                                questionMarkToken = ((RecordFieldNode) node).questionMarkToken().get();
                            }
                            if (fieldMetaData.isPresent()) {
                                nodeList = nodeList.add(NodeFactory.createRecordFieldNode(
                                        fieldMetaData.get(),
                                        readOnlyKeyWord,
                                        TypeDescriptor.getBuiltinSimpleNameReferenceNode(sValues[1]),
                                        AbstractNodeFactory.createIdentifierToken(fName),
                                        questionMarkToken,
                                        SyntaxTreeConstants.SYNTAX_TREE_SEMICOLON
                                ));
                            } else {
                                nodeList = nodeList.add(NodeFactory.createRecordFieldNode(
                                        null,
                                        readOnlyKeyWord,
                                        TypeDescriptor.getBuiltinSimpleNameReferenceNode(sValues[1]),
                                        AbstractNodeFactory.createIdentifierToken(fName),
                                        questionMarkToken,
                                        SyntaxTreeConstants.SYNTAX_TREE_SEMICOLON
                                ));
                            }
                        } else {
                            nodeList = nodeList.add(node);
                        }
                    } else {
                        nodeList = nodeList.add(node);
                    }
                }
            }
            RecordTypeDescriptorNode recordTypeDescriptorNode = NodeFactory.createRecordTypeDescriptorNode(
                    SyntaxTreeConstants.SYNTAX_TREE_KEYWORD_RECORD,
                    AbstractNodeFactory.createIdentifierToken(START_CLOSED_RECORD),
                    nodeList, null,
                    AbstractNodeFactory.createIdentifierToken(END_CLOSED_RECORD)
            );
            TypeDefinitionNode modifiedEntity = NodeFactory.createTypeDefinitionNode(metadataNode, visibilityQualifier,
                    typeKeyword, typeName,
                    recordTypeDescriptorNode, SyntaxTreeConstants.SYNTAX_TREE_SEMICOLON);
            retModuleMembers.add(modifiedEntity);
        }
        return retModuleMembers;

    }

    private static Relation readMetaData(String entityName, String entityType, MetadataNode metaD) {
        NodeList<AnnotationNode> annotations = metaD.annotations();
        for (AnnotationNode annotation : annotations) {
            Node annotReference = annotation.annotReference();
            if (annotReference.kind() == SyntaxKind.QUALIFIED_NAME_REFERENCE) {
                QualifiedNameReferenceNode qualifiedNameRef =
                        (QualifiedNameReferenceNode) annotReference;
                if (qualifiedNameRef.identifier().text().equals(KEYWORD_RELATION) && qualifiedNameRef.modulePrefix()
                        .text().equals(BalFileConstants.PERSIST)) {
                    ArrayList<String> keyColumns = new ArrayList<>();
                    ArrayList<String> reference = new ArrayList<>();
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
                            if (((SpecificFieldNode) fieldNode).fieldName().toString().trim().equals(KEY_COLUMNS)) {
                                Iterator<Node> listIterator = ((ListConstructorExpressionNode) valueNode)
                                        .expressions().iterator();
                                int count = 0;
                                while (listIterator.hasNext()) {
                                    keyColumns.add(count, listIterator.next().toSourceCode().replaceAll(
                                            DOUBLE_QUOTE, BalFileConstants.EMPTY_STRING
                                    ));
                                    count += 1;
                                }
                            } else if (((SpecificFieldNode) fieldNode).fieldName().toSourceCode().trim()
                                    .equals(KEYWORD_REFERENCE)) {
                                Iterator<Node> listIterator = ((ListConstructorExpressionNode) valueNode)
                                        .expressions().iterator();
                                int count = 0;
                                while (listIterator.hasNext()) {
                                    reference.add(count, listIterator.next().toSourceCode().replaceAll(
                                            DOUBLE_QUOTE, BalFileConstants.EMPTY_STRING
                                    ));
                                    count += 1;
                                }
                            }
                        }
                        return new Relation(entityType, entityName, keyColumns, reference, false);
                    } else {
                        return new Relation(entityType, entityName, new ArrayList<>(), new ArrayList<>(), false);
                    }

                }
            }
        }
        return new Relation(entityType, entityName, null, null, false);
    }
}
