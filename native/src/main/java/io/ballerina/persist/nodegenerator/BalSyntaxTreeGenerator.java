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
import io.ballerina.persist.components.Function;
import io.ballerina.persist.components.IfElse;
import io.ballerina.persist.components.TypeDescriptor;
import io.ballerina.persist.objects.Entity;
import io.ballerina.persist.objects.FieldMetaData;
import io.ballerina.persist.objects.Relation;
import io.ballerina.tools.text.TextDocument;
import io.ballerina.tools.text.TextDocuments;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;


/**
 * Class containing methods to create and read ballerina files as syntax trees.
 *
 * @since 0.1.0
 */
public class BalSyntaxTreeGenerator {

    /**
     * method to read ballerina files.
     */
    public static ArrayList<Entity> getEntityRecord(Path filePath, Optional<String> module) throws IOException {
        ArrayList<Entity> entityArray = new ArrayList<>();
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
                if (qualifiedNameRef.identifier().text().equals("Entity") && qualifiedNameRef.modulePrefix().text()
                        .equals(BalFileConstants.PERSIST) && annotation.annotValue().isPresent()) {
                    index += 1;
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
                        if (((SpecificFieldNode) fieldNode).fieldName().toString().trim().equals("key")) {
                            keys = new ArrayList<>();
                            Iterator<Node> listIterator = ((ListConstructorExpressionNode) valueNode)
                                    .expressions().iterator();
                            count = 0;
                            while (listIterator.hasNext()) {
                                keys.add(count, listIterator.next().toSourceCode());
                                count += 1;
                            }
                        } else if (((SpecificFieldNode) fieldNode).fieldName().toSourceCode().trim()
                                .equals("tableName")) {
                            tableName = ((BasicLiteralNode) valueNode).literalToken().text()
                                    .replaceAll(BalFileConstants.DOUBLE_QUOTE, BalFileConstants.EMPTY_STRING);
                        }
                    }
                    entityArray.add(index, new Entity(getArray(keys), tableName, module));
                }
            }

            RecordTypeDescriptorNode recordDesc = (RecordTypeDescriptorNode) ((TypeDefinitionNode) moduleNode)
                    .typeDescriptor();
            entityArray.get(index).setEntityName(((TypeDefinitionNode) moduleNode).typeName().text());
            int relationIndex = 0;
            for (Node node : recordDesc.fields()) {
                if (node.kind() == SyntaxKind.RECORD_FIELD_WITH_DEFAULT_VALUE) {
                    RecordFieldWithDefaultValueNode fieldNode = (RecordFieldWithDefaultValueNode) node;
                    String fName = fieldNode.fieldName().text().trim();
                    String fType = fieldNode.typeName().toSourceCode().trim();
                    FieldMetaData field;
                    if (((RecordFieldWithDefaultValueNode) node).metadata().isEmpty()) {
                        field = new FieldMetaData(fName, fType, false);
                    } else {
                        MetadataNode fieldMetaD = ((RecordFieldWithDefaultValueNode) node).metadata().get();
                        Relation relation = readMetaData(fName, fType, fieldMetaD);
                        field = new FieldMetaData(fName, fType, checkAutoIncrement(fieldMetaD));
                        entityArray.get(index).relation = relation;
                        entityArray.get(index).relations.add(relation);
                    }
                    entityArray.get(index).addField(field);
                } else if (node.kind() == SyntaxKind.RECORD_FIELD) {
                    RecordFieldNode fieldNode = (RecordFieldNode) node;
                    String fName = fieldNode.fieldName().text().trim();
                    String fType = fieldNode.typeName().toSourceCode().trim();
                    FieldMetaData field;
                    if (((RecordFieldNode) node).metadata().isEmpty()) {
                        field = new FieldMetaData(fName, fType, false);
                    } else {
                        MetadataNode fieldMetaD = ((RecordFieldNode) node).metadata().get();
                        Relation relation = readMetaData(fName, fType, fieldMetaD);
                        field = new FieldMetaData(fName, fType, checkAutoIncrement(fieldMetaD));
                        entityArray.get(index).relation = relation;
                        entityArray.get(index).relations.add(relation);
                    }
                    entityArray.get(index).addField(field);
                }
            }
        }
        return entityArray;
    }

    public static SyntaxTree generateClientSyntaxTree(Entity entity) {
        boolean keyAutoInc = false;
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
            if (entity.getKeys().length > 1) {
                for (String key : entity.getKeys()) {
                    if (field.getFieldName().equals(key.trim().replaceAll(
                            BalFileConstants.DOUBLE_QUOTE, BalFileConstants.EMPTY_STRING))) {
                        keys.put(field.getFieldName(),
                                field.getFieldType().trim().replaceAll(" ",
                                        BalFileConstants.EMPTY_STRING));
                    }
                }
            } else {
                if (field.getFieldName().equals(entity.getKeys()[0].trim()
                        .replaceAll(BalFileConstants.DOUBLE_QUOTE, BalFileConstants.EMPTY_STRING))) {
                    keyType = field.getFieldType().trim().replaceAll(" ",
                            BalFileConstants.EMPTY_STRING);
                }
            }
            if (!subFields.isEmpty()) {
                subFields.add(NodeFactory.createBasicLiteralNode(SyntaxKind.STRING_LITERAL,
                        AbstractNodeFactory.createLiteralValueToken(SyntaxKind.STRING_LITERAL, ", "
                                        + System.lineSeparator(), NodeFactory.createEmptyMinutiaeList(),
                                NodeFactory.createEmptyMinutiaeList())));
            }
            if (field.isAutoGenerated()) {
                subFields.add(NodeFactory.createSpecificFieldNode(null,
                        AbstractNodeFactory.createIdentifierToken(field.getFieldName()),
                        SyntaxTreeConstants.SYNTAX_TREE_COLON, NodeParser.parseExpression(String.format(
                                BalFileConstants.FIELD_FORMAT_WITH_AUTO_G,
                                field.getFieldName().trim().replaceAll(
                                        BalFileConstants.SINGLE_QUOTE, BalFileConstants.EMPTY_STRING),
                                field.getFieldType().trim().replaceAll(" ",
                                        BalFileConstants.EMPTY_STRING),
                                String.valueOf(field.isAutoGenerated()).trim()))));
                for (String key : entity.getKeys()) {
                    if (field.getFieldName().equals(key.trim().replaceAll(
                            BalFileConstants.DOUBLE_QUOTE, BalFileConstants.EMPTY_STRING))) {
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
                                        BalFileConstants.SINGLE_QUOTE, BalFileConstants.EMPTY_STRING),
                                field.getFieldType().trim().replaceAll(" ",
                                        BalFileConstants.EMPTY_STRING)
                        ))));
            }
        }
        if (entity.relations.size() != 0) {
            for (Relation relation : entity.relations) {
                if (relation.relatedFields.size() == 0) {
                    continue;
                }
                StringBuilder refFieldsString = new StringBuilder();
                StringBuilder joinColumnsString = new StringBuilder();

                for (int i = 0; i < relation.keyColumns.size(); i++) {
                    if (refFieldsString.length() != 0) {
                        refFieldsString.append(", ");
                        joinColumnsString.append(", ");
                    }
                    refFieldsString.append(BalFileConstants.DOUBLE_QUOTE);
                    refFieldsString.append(relation.keyColumns.get(i));
                    refFieldsString.append(BalFileConstants.DOUBLE_QUOTE);
                    joinColumnsString.append(BalFileConstants.DOUBLE_QUOTE);
                    joinColumnsString.append(relation.references.get(i));
                    joinColumnsString.append(BalFileConstants.DOUBLE_QUOTE);
                }
                joinSubFields.add(NodeFactory.createSpecificFieldNode(null,
                        AbstractNodeFactory.createIdentifierToken(relation.relatedInstance),
                        SyntaxTreeConstants.SYNTAX_TREE_COLON,
                        NodeParser.parseExpression(String.format(
                                BalFileConstants.FIELD_FORMAT_JOIN_FIELD, relation.relatedType,
                                relation.relatedInstance, relation.refTable, BalFileConstants.OPEN_SQUARE_BRACKET +
                                        refFieldsString.toString() + BalFileConstants.CLOSE_SQUARE_BRACKET,
                                BalFileConstants.OPEN_SQUARE_BRACKET +
                                        joinColumnsString.toString() + BalFileConstants.CLOSE_SQUARE_BRACKET
                        ))));

                if (!subFields.isEmpty()) {
                    subFields.add(NodeFactory.createBasicLiteralNode(SyntaxKind.STRING_LITERAL,
                            AbstractNodeFactory.createLiteralValueToken(SyntaxKind.STRING_LITERAL, ", "
                                            + System.lineSeparator(), NodeFactory.createEmptyMinutiaeList(),
                                    NodeFactory.createEmptyMinutiaeList())));
                }
                if (relation.isChild) {
                    for (FieldMetaData fieldMetaData : relation.relatedFields) {
                        subFields.add(NodeFactory.createSpecificFieldNode(null,
                                AbstractNodeFactory.createIdentifierToken(BalFileConstants.DOUBLE_QUOTE +
                                        relation.relatedInstance + "." + fieldMetaData.getFieldName() +
                                        BalFileConstants.DOUBLE_QUOTE), SyntaxTreeConstants.SYNTAX_TREE_COLON,
                                NodeParser.parseExpression(String.format(
                                        BalFileConstants.FIELD_FORMAT_RELATED_CHILD_FIELD,
                                        fieldMetaData.getFieldType().trim().replaceAll(
                                                BalFileConstants.SINGLE_QUOTE, BalFileConstants.EMPTY_STRING),
                                        relation.relatedInstance, relation.refTable, fieldMetaData.getFieldName()
                                ))));
                    }
                } else {
                    for (FieldMetaData fieldMetaData : relation.relatedFields) {
                        if (relation.references.contains(fieldMetaData.getFieldName())) {
                            int index = relation.references.indexOf(fieldMetaData.getFieldName());
                            subFields.add(NodeFactory.createSpecificFieldNode(null,
                                    AbstractNodeFactory.createIdentifierToken(BalFileConstants.DOUBLE_QUOTE +
                                            relation.relatedInstance + "." + fieldMetaData.getFieldName()
                                            + BalFileConstants.DOUBLE_QUOTE), SyntaxTreeConstants.SYNTAX_TREE_COLON,
                                    NodeParser.parseExpression(String.format(
                                            BalFileConstants.FIELD_FORMAT_RELATED_PARENT_FIELD,
                                            relation.keyColumns.get(index), fieldMetaData.getFieldType(),
                                            relation.relatedInstance, relation.refTable, fieldMetaData.getFieldName()
                                    ))));
                        } else {
                            subFields.add(NodeFactory.createSpecificFieldNode(null,
                                    AbstractNodeFactory.createIdentifierToken(BalFileConstants.DOUBLE_QUOTE +
                                            relation.relatedInstance + "." + fieldMetaData.getFieldName()
                                            + BalFileConstants.DOUBLE_QUOTE), SyntaxTreeConstants.SYNTAX_TREE_COLON,
                                    NodeParser.parseExpression(String.format(
                                            BalFileConstants.FIELD_FORMAT_RELATED_PARENT_FIELD_WOUT_COLUMN_NAME,
                                            fieldMetaData.getFieldType(),
                                            relation.relatedInstance, relation.refTable, fieldMetaData.getFieldName()
                                    ))));
                        }
                    }

                }
            }
        }
        imports = imports.add(getImportDeclarationNode(BalFileConstants.KEYWORD_BALLERINA,
                BalFileConstants.KEYWORD_SQL));
        imports = imports.add(getImportDeclarationNode(BalFileConstants.KEYWORD_BALLERINAX,
                BalFileConstants.KEYWORD_MYSQL));
        if (hasTime) {
            imports = imports.add(getImportDeclarationNode(BalFileConstants.KEYWORD_BALLERINA,
                    BalFileConstants.KEYWORD_TIME));
        }
        imports = imports.add(getImportDeclarationNode(BalFileConstants.KEYWORD_BALLERINA, BalFileConstants.PERSIST));
        if (entity.getModule().isEmpty()) {
            imports = imports.add(NodeParser.parseImportDeclaration(String.format(
                    BalFileConstants.IMPORT_AS_ENTITIES, entity.getPackageName())));
        } else {
            imports = imports.add(NodeParser.parseImportDeclaration(String.format(
                    BalFileConstants.IMPORT_AS_ENTITIES, entity.getPackageName() + "." + entity.getModule().get())));
        }
        String className = entity.getEntityName();
        if (entity.getModule().isPresent()) {
            className = entity.getModule().get().substring(0, 1).toUpperCase() + entity.getModule().get().substring(1)
                    + entity.getEntityName();
        }
        Class client = createClientClass(entity, className, subFields, joinSubFields, keys, keyType, keyAutoInc);

        moduleMembers = moduleMembers.add(client.getClassDefinitionNode());

        Class clientStream = createClientStreamClass(entity, className);

        moduleMembers = moduleMembers.add(clientStream.getClassDefinitionNode());

        Token eofToken = AbstractNodeFactory.createIdentifierToken(BalFileConstants.EMPTY_STRING);
        ModulePartNode modulePartNode = NodeFactory.createModulePartNode(imports, moduleMembers, eofToken);
        TextDocument textDocument = TextDocuments.from(BalFileConstants.EMPTY_STRING);
        SyntaxTree balTree = SyntaxTree.from(textDocument);
        return balTree.modifyWith(modulePartNode);
    }

    private static Class createClientClass(Entity entity, String className, List<Node> subFields,
                                           List<Node> joinSubFields, HashMap<String, String> keys,
                                           String keyType, boolean keyAutoInc) {
        Class client = new Class(className + "Client", true);
        client.addQualifiers(new String[]{BalFileConstants.KEYWORD_CLIENT});
        client.addMember(NodeFactory.createBasicLiteralNode(SyntaxKind.STRING_LITERAL,
                AbstractNodeFactory.createLiteralValueToken(SyntaxKind.STRING_LITERAL, " ",
                        NodeFactory.createEmptyMinutiaeList(), NodeFactory.createEmptyMinutiaeList())), false);
        client.addMember(TypeDescriptor.getObjectFieldNode(BalFileConstants.KEYWORD_PRIVATE,
                        new String[]{BalFileConstants.KEYWORD_FINAL},
                        TypeDescriptor.getBuiltinSimpleNameReferenceNode(BalFileConstants.KEYWORD_STRING),
                        "entityName", NodeFactory.createBasicLiteralNode(SyntaxKind.STRING_LITERAL,
                                AbstractNodeFactory.createLiteralValueToken(SyntaxKind.STRING_LITERAL,
                                        BalFileConstants.DOUBLE_QUOTE + entity.getEntityName()
                                                + BalFileConstants.DOUBLE_QUOTE,
                                        NodeFactory.createEmptyMinutiaeList(),
                                        NodeFactory.createEmptyMinutiaeList()))),
                true);
        client.addMember(TypeDescriptor.getObjectFieldNode(BalFileConstants.KEYWORD_PRIVATE,
                        new String[]{BalFileConstants.KEYWORD_FINAL},
                        TypeDescriptor.getQualifiedNameReferenceNode("sql", "ParameterizedQuery"),
                        "tableName", NodeFactory.createBasicLiteralNode(SyntaxKind.STRING_LITERAL,
                                AbstractNodeFactory.createLiteralValueToken(SyntaxKind.STRING_LITERAL,
                                        "`" + entity.getTableName() + "`",
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
                keysString.append(", ");
            }
            keysString.append(key);
        }

        client.addMember(TypeDescriptor.getObjectFieldNode(BalFileConstants.KEYWORD_PRIVATE, new String[]{},
                TypeDescriptor.getArrayTypeDescriptorNode(BalFileConstants.KEYWORD_STRING),
                "keyFields", NodeFactory.createListConstructorExpressionNode(
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
                        TypeDescriptor.getQualifiedNameReferenceNode(BalFileConstants.PERSIST, "SQLClient"),
                        BalFileConstants.PERSIST_CLIENT),
                true);

        Function init = new Function(BalFileConstants.INIT);
        init.addQualifiers(new String[]{BalFileConstants.KEYWORD_PUBLIC});
        init.addReturns(TypeDescriptor.getOptionalTypeDescriptorNode(BalFileConstants.EMPTY_STRING,
                BalFileConstants.ERROR));
        init.addStatement(NodeParser.parseStatement(BalFileConstants.INIT_MYSQL_CLIENT));
        if (!joinSubFields.isEmpty()) {
            init.addStatement(NodeParser.parseStatement(BalFileConstants.INIT_PERSIST_CLIENT_RELATED));
        } else {
            init.addStatement(NodeParser.parseStatement(BalFileConstants.INIT_PERSIST_CLIENT));
        }
        client.addMember(init.getFunctionDefinitionNode(), true);

        Function create = new Function(BalFileConstants.CREATE);
        if (joinSubFields.isEmpty()) {
        create.addRequiredParameter(
                TypeDescriptor.getQualifiedNameReferenceNode(BalFileConstants.ENTITIES, entity.getEntityName()),
                "value"
        );
        create.addQualifiers(new String[]{BalFileConstants.KEYWORD_REMOTE});
        create.addReturns(TypeDescriptor.getUnionTypeDescriptorNode(
                TypeDescriptor.getQualifiedNameReferenceNode(BalFileConstants.ENTITIES,
                        entity.getEntityName()), TypeDescriptor.getOptionalTypeDescriptorNode(
                        BalFileConstants.EMPTY_STRING, BalFileConstants.ERROR)));
        StringBuilder retRecord = new StringBuilder();
        create.addStatement(NodeParser.parseStatement(BalFileConstants.CREATE_SQL_RESULTS));
        if (keys.size() > 1) {
            for (FieldMetaData map : entity.getFields()) {
                if (retRecord.length() > 0) {
                    retRecord.append(", ");
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
        }  else {
            for (FieldMetaData map : entity.getFields()) {
                if (retRecord.length() > 0) {
                    retRecord.append(", ");
                }
                if (map.getFieldName().equals(entity.getKeys()[0].trim().replaceAll(
                        BalFileConstants.DOUBLE_QUOTE, BalFileConstants.EMPTY_STRING))) {
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
            boolean isParent = false;
            for (Relation relation : entity.relations) {
                if (relation.isChild) {
                    continue;
                }
                isParent = true;
                IfElse valueCheck = new IfElse(NodeParser.parseExpression(String.format("value.%s is %s",
                        relation.relatedInstance, relation.relatedType)));
                valueCheck.addIfStatement(NodeParser.parseStatement(String.format("%sClient %sClient = check new %s" +
                        "Client();", relation.relatedType, relation.relatedType.toLowerCase(),
                        relation.relatedType)));
                valueCheck.addIfStatement(NodeParser.parseStatement(String.format("boolean exists = check " +
                        "%sClient->exists(<%s> value.%s);", relation.relatedType.toLowerCase(),
                        relation.relatedType, relation.relatedInstance)));
                IfElse checkExistence = new IfElse(NodeParser.parseExpression("!exists"));
                checkExistence.addIfStatement(NodeParser.parseStatement(String.format("value.user = check %sClient" +
                        "->create(<%s> value.%s);", relation.relatedType.toLowerCase(), relation.relatedType,
                        relation.relatedInstance)));
                valueCheck.addIfStatement(checkExistence.getIfElseStatementNode());

                create.addIfElseStatement(valueCheck.getIfElseStatementNode());
            }
            create.addStatement(NodeParser.parseStatement(BalFileConstants.CREATE_SQL_RESULTS));
            create.addStatement(NodeParser.parseStatement("return value;"));
        }

        client.addMember(create.getFunctionDefinitionNode(), true);

        Function readByKey = new Function(BalFileConstants.READ_BY_KEY);
        StringBuilder keyString = new StringBuilder();
        keyString.append(BalFileConstants.KEY);
        if (keys.size() > 1) {
            keyString = new StringBuilder();

            for (String key : keys.keySet()) {
                keyString.append(keys.get(key));
                keyString.append(" ");
                keyString.append(key);
                keyString.append(";");
            }
            readByKey.addRequiredParameter(
                    NodeParser.parseTypeDescriptor(String.format(BalFileConstants.CLOSE_RECORD_VARIABLE, keyString)),
                    BalFileConstants.KEY);
        } else {
            readByKey.addRequiredParameter(
                    TypeDescriptor.getBuiltinSimpleNameReferenceNode(keyType), BalFileConstants.KEY);
        }
        readByKey.addQualifiers(new String[]{BalFileConstants.KEYWORD_REMOTE});
        readByKey.addReturns(TypeDescriptor.getUnionTypeDescriptorNode(
                TypeDescriptor.getQualifiedNameReferenceNode(BalFileConstants.ENTITIES, entity.getEntityName()),
                TypeDescriptor.getSimpleNameReferenceNode (BalFileConstants.ERROR)));

//        if (!entity.relations.isEmpty()) {
//            readByKey.addRequiredParameterWithDefault(NodeParser.parseTypeDescriptor(String.format("%sRelations[]",
//                    entity.getEntityName())), "include");
//            readByKey.addStatement(NodeParser.parseStatement(String.format("return <%s> check " +
//                            "self.persistClient.runReadByKeyQuery(%s, key, include);", entity.getEntityName(),
//                    entity.getEntityName())));
//        } else {
        readByKey.addStatement(NodeParser.parseStatement(String.format(BalFileConstants.READ_BY_KEY_RETURN,
                String.format(BalFileConstants.RECORD_FIELD_VAR,
                        BalFileConstants.ENTITIES, entity.getEntityName()), String.format(
                        BalFileConstants.RECORD_FIELD_VAR,
                        BalFileConstants.ENTITIES, entity.getEntityName()))));
        //}
        client.addMember(readByKey.getFunctionDefinitionNode(), true);

        Function read = new Function(BalFileConstants.READ);
        read.addRequiredParameterWithDefault(
                TypeDescriptor.getOptionalTypeDescriptorNode(BalFileConstants.EMPTY_STRING, TypeDescriptor
                        .getMapTypeDescriptorNode(SyntaxTreeConstants.SYNTAX_TREE_VAR_ANYDATA).toSourceCode()),
                BalFileConstants.FILTER);
        read.addQualifiers(new String[]{BalFileConstants.KEYWORD_REMOTE});
        read.addReturns(TypeDescriptor.getUnionTypeDescriptorNode(TypeDescriptor.getStreamTypeDescriptorNode(
                        TypeDescriptor.getQualifiedNameReferenceNode(BalFileConstants.ENTITIES, entity.getEntityName()),
                        TypeDescriptor.getOptionalTypeDescriptorNode(BalFileConstants.EMPTY_STRING,
                                BalFileConstants.ERROR)),
                TypeDescriptor.getSimpleNameReferenceNode(BalFileConstants.ERROR)));
        read.addStatement(NodeParser.parseStatement(String.format(BalFileConstants.READ_RUN_READ_QUERY,
                String.format(BalFileConstants.RECORD_FIELD_VAR, BalFileConstants.ENTITIES, entity.getEntityName()))));
        read.addStatement(NodeParser.parseStatement(String.format(BalFileConstants.READ_RETURN_STREAM,
                String.format(BalFileConstants.RECORD_FIELD_VAR, BalFileConstants.ENTITIES, entity.getEntityName()),
                className)));
        client.addMember(read.getFunctionDefinitionNode(), true);

        Function update = new Function(BalFileConstants.UPDATE);
        update.addRequiredParameter(
                TypeDescriptor.getRecordTypeDescriptorNode(),
                "'object"
        );
        update.addRequiredParameter(
                TypeDescriptor.getBuiltinSimpleNameReferenceNode(TypeDescriptor
                        .getMapTypeDescriptorNode(SyntaxTreeConstants.SYNTAX_TREE_VAR_ANYDATA).toSourceCode()),
                BalFileConstants.FILTER
        );
        update.addQualifiers(new String[]{BalFileConstants.KEYWORD_REMOTE});
        update.addReturns(TypeDescriptor.getOptionalTypeDescriptorNode(BalFileConstants.EMPTY_STRING,
                BalFileConstants.ERROR));
        update.addStatement(NodeParser.parseStatement(BalFileConstants.UPDATE_RUN_UPDATE_QUERY));
        client.addMember(update.getFunctionDefinitionNode(), true);


        Function delete = new Function(BalFileConstants.DELETE);
        delete.addRequiredParameter(
                TypeDescriptor.getBuiltinSimpleNameReferenceNode(TypeDescriptor
                        .getMapTypeDescriptorNode(SyntaxTreeConstants.SYNTAX_TREE_VAR_ANYDATA).toSourceCode()),
                BalFileConstants.FILTER
        );
        delete.addQualifiers(new String[]{BalFileConstants.KEYWORD_REMOTE});
        delete.addReturns(TypeDescriptor.getOptionalTypeDescriptorNode(BalFileConstants.EMPTY_STRING,
                BalFileConstants.ERROR));
        delete.addStatement(NodeParser.parseStatement(BalFileConstants.DELETE_RUN_DELETE_QUERY));
        client.addMember(delete.getFunctionDefinitionNode(), true);

//        if (!joinSubFields.isEmpty()) {
//            Function exists = new Function(BalFileConstants.EXISTS);
//            exists.addRequiredParameter(TypeDescriptor.getQualifiedNameReferenceNode(BalFileConstants.ENTITIES,
//                            entity.getEntityName()), entity.getEntityName().substring(0,1).toLowerCase() +
//                    entity.getEntityName().substring(1));
//            exists.addReturns(TypeDescriptor.getUnionTypeDescriptorNode(
//                    TypeDescriptor.getBuiltinSimpleNameReferenceNode("boolean"),
//                    TypeDescriptor.getSimpleNameReferenceNode (BalFileConstants.ERROR)));
//        }

        Function close = new Function(BalFileConstants.CLOSE);
        close.addQualifiers(new String[]{});
        close.addReturns(TypeDescriptor.getOptionalTypeDescriptorNode(BalFileConstants.EMPTY_STRING,
                BalFileConstants.ERROR));
        close.addStatement(NodeParser.parseStatement(BalFileConstants.CLOSE_PERSIST_CLIENT));
        client.addMember(close.getFunctionDefinitionNode(), true);
        return client;
    }

    private static Class createClientStreamClass(Entity entity, String className) {
        Class clientStream = new Class(className + "Stream", true);

        clientStream.addMember(NodeFactory.createBasicLiteralNode(SyntaxKind.STRING_LITERAL,
                AbstractNodeFactory.createLiteralValueToken(SyntaxKind.STRING_LITERAL, " ",
                        NodeFactory.createEmptyMinutiaeList(), NodeFactory.createEmptyMinutiaeList())), true);

        clientStream.addMember(TypeDescriptor.getObjectFieldNodeWithoutExpression(BalFileConstants.KEYWORD_PRIVATE,
                new String[]{},
                TypeDescriptor.getStreamTypeDescriptorNode(
                        SyntaxTreeConstants.SYNTAX_TREE_VAR_ANYDATA,
                        TypeDescriptor.getOptionalTypeDescriptorNode(BalFileConstants.EMPTY_STRING,
                                BalFileConstants.ERROR)),
                BalFileConstants.ANYDATA_STREAM), true);

        Function initStream = new Function(BalFileConstants.INIT);
        initStream.addQualifiers(new String[]{BalFileConstants.KEYWORD_PUBLIC, BalFileConstants.KEYWORD_ISOLATED});
        initStream.addStatement(NodeParser.parseStatement(BalFileConstants.INIT_STREAM_STATEMENT));
        initStream.addRequiredParameter(
                TypeDescriptor.getStreamTypeDescriptorNode(
                        SyntaxTreeConstants.SYNTAX_TREE_VAR_ANYDATA,
                        TypeDescriptor.getOptionalTypeDescriptorNode(BalFileConstants.EMPTY_STRING,
                                BalFileConstants.ERROR)),
                BalFileConstants.ANYDATA_STREAM);
        clientStream.addMember(initStream.getFunctionDefinitionNode(), true);

        Function nextStream = new Function(BalFileConstants.NEXT);
        nextStream.addQualifiers(new String[]{BalFileConstants.KEYWORD_PUBLIC, BalFileConstants.KEYWORD_ISOLATED});
        nextStream.addReturns(NodeParser.parseTypeDescriptor(String.format(
                BalFileConstants.NEXT_STREAM_RETURN_TYPE,
                String.format(BalFileConstants.RECORD_FIELD_VAR, BalFileConstants.ENTITIES, entity.getEntityName()))));
        nextStream.addStatement(NodeParser.parseStatement(BalFileConstants.NEXT_STREAM_STREAM_VALUE));

        IfElse streamValueNilCheck = new IfElse(NodeParser.parseExpression(
                BalFileConstants.NEXT_STREAM_IF_STATEMENT));
        streamValueNilCheck.addIfStatement(NodeParser.parseStatement(
                BalFileConstants.NEXT_STREAM_RETURN_STREAM_VALUE));
        IfElse streamValueErrorCheck = new IfElse(NodeParser.parseExpression(
                BalFileConstants.NEXT_STREAM_ELSE_IF_STATEMENT));
        streamValueErrorCheck.addIfStatement(NodeParser.parseStatement(
                BalFileConstants.NEXT_STREAM_RETURN_STREAM_VALUE));
        streamValueErrorCheck.addElseStatement(NodeParser.parseStatement(String.format(
                BalFileConstants.NEXT_STREAM_ELSE_STATEMENT,
                String.format(BalFileConstants.RECORD_FIELD_VAR, BalFileConstants.ENTITIES, entity.getEntityName()),
                String.format(BalFileConstants.RECORD_FIELD_VAR, BalFileConstants.ENTITIES, entity.getEntityName()))));
        streamValueErrorCheck.addElseStatement(NodeParser.parseStatement(BalFileConstants.RETURN_NEXT_RECORD));
        streamValueNilCheck.addElseBody(streamValueErrorCheck);
        nextStream.addIfElseStatement(streamValueNilCheck.getIfElseStatementNode());
        clientStream.addMember(nextStream.getFunctionDefinitionNode(), true);

        Function closeStream = new Function(BalFileConstants.CLOSE);
        closeStream.addQualifiers(new String[]{BalFileConstants.KEYWORD_PUBLIC, BalFileConstants.KEYWORD_ISOLATED});
        closeStream.addReturns(TypeDescriptor.getOptionalTypeDescriptorNode(BalFileConstants.EMPTY_STRING,
                BalFileConstants.ERROR));
        closeStream.addStatement(NodeParser.parseStatement(BalFileConstants.CLOSE_STREAM_STATEMENT));
        clientStream.addMember(closeStream.getFunctionDefinitionNode(), true);
        return clientStream;
    }

    public static SyntaxTree generateConfigSyntaxTree() {
        NodeList<ImportDeclarationNode> imports = AbstractNodeFactory.createEmptyNodeList();
        NodeList<ModuleMemberDeclarationNode> moduleMembers = AbstractNodeFactory.createEmptyNodeList();

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
        return balTree.modifyWith(modulePartNode);
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
                SyntaxTreeConstants.SYNTAX_TREE_SEMICOLON
        );
    }

    private static boolean checkAutoIncrement(MetadataNode metaD) {
        NodeList<AnnotationNode> annotations = metaD.annotations();
        for (AnnotationNode annotation : annotations) {
            Node annotReference = annotation.annotReference();
            if (annotReference.kind() == SyntaxKind.QUALIFIED_NAME_REFERENCE) {
                QualifiedNameReferenceNode qualifiedNameRef =
                        (QualifiedNameReferenceNode) annotReference;
                if (qualifiedNameRef.identifier().text().equals("AutoIncrement") && qualifiedNameRef
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
            for (Relation relation : entity.relations) {
                int counter = 0;
                if (relation != null) {
                    if (!relation.isChild) {

                        String entityType = relation.relatedType;
                        for (Entity childEntity : entityArray) {
                            if (entity.getEntityName().equals(childEntity.getEntityName())) {
                                continue;
                            }
                            if (childEntity.getEntityName().equals(entityType)) {
                                int relationIndex = childEntity.relations.size();
                                childEntity.relations.add(relationIndex, new Relation(entity.getEntityName(), null,
                                        new ArrayList<String>(), new ArrayList<String>(), true));
                                childEntity.relations.get(relationIndex).refTable = entity.getTableName();
                                relation.refTable = childEntity.getTableName();
                                boolean childIncludesParentColumns = false;
                                ArrayList<Integer> indexesToRemove = new ArrayList<Integer>();
                                for (FieldMetaData fieldMetaData : entity.getFields()) {
                                    if (!fieldMetaData.getFieldType().equals(childEntity.getEntityName())) {
                                        if (!entityNames.contains(fieldMetaData.getFieldType())) {
                                            childEntity.relations.get(relationIndex).relatedFields.add(counter,
                                                    fieldMetaData);
                                            counter += 1;
                                        }
                                    } else {
                                        indexesToRemove.add(entity.getFields().indexOf(fieldMetaData));
                                    }
                                }
                                for (Integer index : indexesToRemove) {
                                    entity.getFields().remove(index.intValue());
                                }
                                indexesToRemove = new ArrayList<Integer>();
                                counter = 0;
                                for (FieldMetaData fieldMetaData : childEntity.getFields()) {
                                    if (!fieldMetaData.getFieldType().equals(entity.getEntityName())) {
                                        if (!entityNames.contains(fieldMetaData.getFieldType())) {
                                            relation.relatedFields.add(counter, fieldMetaData);
                                            counter += 1;
                                        }
                                    } else {
                                        childIncludesParentColumns = true;
                                        indexesToRemove.add(childEntity.getFields().indexOf(fieldMetaData));
                                    }
                                    if (fieldMetaData.getFieldType().equals(entity.getEntityName())) {
                                        childEntity.relations.get(relationIndex).relatedInstance =
                                                fieldMetaData.getFieldName();
                                    }
                                }
                                if (!childIncludesParentColumns) {
                                    childEntity.relations.get(relationIndex).relatedFields = new ArrayList<>();
                                }
                                for (Integer index : indexesToRemove) {
                                    childEntity.getFields().remove(index.intValue());
                                }
                            }
                        }
                    } else {

                    }
                }
            }

        }
    }

    private static Relation readMetaData(String entityName, String entityType, MetadataNode metaD) {
        NodeList<AnnotationNode> annotations = metaD.annotations();
        for (AnnotationNode annotation : annotations) {
            Node annotReference = annotation.annotReference();
            if (annotReference.kind() == SyntaxKind.QUALIFIED_NAME_REFERENCE) {
                QualifiedNameReferenceNode qualifiedNameRef =
                        (QualifiedNameReferenceNode) annotReference;
                if (qualifiedNameRef.identifier().text().equals("Relation") && qualifiedNameRef.modulePrefix().text()
                        .equals(BalFileConstants.PERSIST) && annotation.annotValue().isPresent()) {
                    ArrayList<String> keyColumns = new ArrayList<>();
                    ArrayList<String> reference = new ArrayList<>();
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
                        if (((SpecificFieldNode) fieldNode).fieldName().toString().trim().equals("keyColumns")) {
                            Iterator<Node> listIterator = ((ListConstructorExpressionNode) valueNode)
                                    .expressions().iterator();
                            int count = 0;
                            while (listIterator.hasNext()) {
                                keyColumns.add(count, listIterator.next().toSourceCode().replaceAll(
                                        BalFileConstants.DOUBLE_QUOTE, BalFileConstants.EMPTY_STRING
                                ));
                                count += 1;
                            }
                        } else if (((SpecificFieldNode) fieldNode).fieldName().toSourceCode().trim()
                                .equals("reference")) {
                            Iterator<Node> listIterator = ((ListConstructorExpressionNode) valueNode)
                                    .expressions().iterator();
                            int count = 0;
                            while (listIterator.hasNext()) {
                                reference.add(count, listIterator.next().toSourceCode().replaceAll(
                                        BalFileConstants.DOUBLE_QUOTE, BalFileConstants.EMPTY_STRING
                                ));
                                count += 1;
                            }
                        }
                    }
                    return new Relation(entityType, entityName, keyColumns, reference, false);
                }
            }
        }
        return new Relation(entityType, entityName, new ArrayList<>(), new ArrayList<>(), false);
    }
}

