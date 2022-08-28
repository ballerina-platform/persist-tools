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
import io.ballerina.compiler.syntax.tree.ChildNodeEntry;
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
import io.ballerina.tools.text.TextDocument;
import io.ballerina.tools.text.TextDocuments;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


/**
 * Class to ballerina files as syntax trees.
 */
public class BalSyntaxTreeGenerator {
    private static final PrintStream errStream = System.err;

    /**
     * method to read ballerina files.
     */
    public static ArrayList<Entity> readBalFiles(Path filePath, String module) throws IOException {
        ArrayList<Entity> entityArray = new ArrayList<>();
        int index = -1;
        ArrayList<String> keys = new ArrayList<>();
        String tableName = null;
        int count = 0;
        SyntaxTree balSyntaxTree = SyntaxTree.from(TextDocuments.from(Files.readString(filePath)));
        ModulePartNode rootNote = balSyntaxTree.rootNode();
        NodeList<ModuleMemberDeclarationNode> nodeList = rootNote.members();
        for (ModuleMemberDeclarationNode i : nodeList) {
            if (i.kind().toString().equals("TYPE_DEFINITION")) {
                Collection<ChildNodeEntry> temp = i.childEntries();
                for (ChildNodeEntry j : temp) {
                    if (j.name().equals("metadata")) {
                        MetadataNode metaD = (MetadataNode) j.node().get();
                        NodeList<AnnotationNode> annotations = metaD.annotations();
                        for (AnnotationNode annotation : annotations) {
                            Node annotReference = annotation.annotReference();
                            if (annotReference.kind() == SyntaxKind.QUALIFIED_NAME_REFERENCE) {
                                QualifiedNameReferenceNode qualifiedNameRef =
                                        (QualifiedNameReferenceNode) annotReference;
                                if (qualifiedNameRef.identifier().text().equals("Entity") && qualifiedNameRef
                                        .modulePrefix().text().equals("persist") &&
                                        annotation.annotValue().isPresent()) {
                                    index += 1;
                                    for (MappingFieldNode fieldNode : annotation.annotValue().get().fields()) {
                                        if (fieldNode.kind() == SyntaxKind.SPECIFIC_FIELD) {
                                            SpecificFieldNode specificField = (SpecificFieldNode) fieldNode;
                                            if (specificField.fieldName().kind() == SyntaxKind.IDENTIFIER_TOKEN) {
                                                ExpressionNode valueNode =
                                                        ((SpecificFieldNode) fieldNode).valueExpr().get();

                                                if (valueNode instanceof ListConstructorExpressionNode) {
                                                    keys = new ArrayList<>();
                                                    Iterator listIterator = ((ListConstructorExpressionNode) valueNode)
                                                            .expressions().iterator();
                                                    count = 0;
                                                    while (listIterator.hasNext()) {
                                                        keys.add(count, listIterator.next().toString());
                                                        count += 1;
                                                    }
                                                } else if (valueNode instanceof BasicLiteralNode) {
                                                    tableName = ((BasicLiteralNode) valueNode).literalToken().text()
                                                            .replaceAll("\"", "");
                                                }
                                            }
                                        }
                                    }
                                    entityArray.add(index, new Entity(getArray(keys), tableName, module));

                                }

                            }

                        }
                    }
                }
                RecordTypeDescriptorNode recordDesc = (RecordTypeDescriptorNode) ((TypeDefinitionNode) i)
                        .typeDescriptor();
                entityArray.get(index).entityName = ((TypeDefinitionNode) i).typeName().text();
                for (Node k : recordDesc.fields()) {
                    if (k.kind().toString().equals("RECORD_FIELD_WITH_DEFAULT_VALUE")) {
                        RecordFieldWithDefaultValueNode fieldNode = (RecordFieldWithDefaultValueNode) k;
                        String fName = fieldNode.fieldName().text();
                        String fType = fieldNode.typeName().toString();
                        String defaultValue = fieldNode.expression().toSourceCode();
                        HashMap<String, String> map = new HashMap<String, String>();
                        if (((RecordFieldWithDefaultValueNode) k).metadata().isEmpty()) {
                            map.put("fieldName", fName);
                            map.put("fieldType", fType);
                            map.put("autoGenerated", "false");
                            entityArray.get(index).fields.add(map);
                        } else {
                            MetadataNode fieldMetaD = ((RecordFieldWithDefaultValueNode) k).metadata().get();
                            map.put("fieldName", fName);
                            map.put("fieldType", fType);
                            map.put("autoGenerated", readMetaData(fieldMetaD));
                            entityArray.get(index).fields.add(map);
                        }
                    } else if (k.kind().toString().equals("RECORD_FIELD")) {
                        RecordFieldNode fieldNode = (RecordFieldNode) k;
                        String fName = fieldNode.fieldName().text();
                        String fType = fieldNode.typeName().toString();
                        HashMap<String, String> map = new HashMap<String, String>();
                        if (((RecordFieldNode) k).metadata().isEmpty()) {
                            map.put("fieldName", fName);
                            map.put("fieldType", fType);
                            map.put("autoGenerated", "false");
                            entityArray.get(index).fields.add(map);
                        } else {
                            MetadataNode fieldMetaD = ((RecordFieldNode) k).metadata().get();
                            map.put("fieldName", fName);
                            map.put("fieldType", fType);
                            map.put("autoGenerated", readMetaData(fieldMetaD));
                            entityArray.get(index).fields.add(map);
                        }
                    }
                }
            }
        }
        return entityArray;
    }

    public static SyntaxTree generateBalFile(Entity entity) {

        boolean keyAutoInc = false;
        NodeList<ImportDeclarationNode> imports = AbstractNodeFactory.createEmptyNodeList();
        NodeList<ModuleMemberDeclarationNode> moduleMembers = AbstractNodeFactory.createEmptyNodeList();

        imports = imports.add(getImportDeclarationNode("ballerina", "sql"));
        imports = imports.add(getImportDeclarationNode("ballerinax", "mysql"));
        for (HashMap i : entity.fields) {
            if (i.get("fieldType").toString().contains("time")) {
                imports = imports.add(getImportDeclarationNode("ballerina", "time"));
                break;
            }
        }
        imports = imports.add(getImportDeclarationNode("ballerina", "persist"));
        String className = entity.entityName;
        if (!entity.module.equals("")) {
            className = entity.module.substring(0, 1).toUpperCase() + entity.module.substring(1)
                    + entity.entityName;
        }
        Class client = new Class(className + "Client", false);
        client.addQualifiers(new String[]{"client"});

        client.addMember(NodeFactory.createBasicLiteralNode(SyntaxKind.STRING_LITERAL,
                AbstractNodeFactory.createLiteralValueToken(SyntaxKind.STRING_LITERAL, " ",
                        NodeFactory.createEmptyMinutiaeList(), NodeFactory.createEmptyMinutiaeList())), false);

        client.addMember(TypeDescriptor.getObjectFieldNode(
                        "private",
                        new String[]{"final"},
                TypeDescriptor.getBuiltinSimpleNameReferenceNode("string"),
                        "entityName", NodeFactory.createBasicLiteralNode(SyntaxKind.STRING_LITERAL,
                        AbstractNodeFactory.createLiteralValueToken(SyntaxKind.STRING_LITERAL,
                                "\"" + entity.entityName + "\"",
                                NodeFactory.createEmptyMinutiaeList(), NodeFactory.createEmptyMinutiaeList()))),
                true);
        client.addMember(TypeDescriptor.getObjectFieldNode(
                        "private",
                        new String[]{"final"},
                TypeDescriptor.getQualifiedNameReferenceNode("sql", "ParameterizedQuery"),
                        "tableName", NodeFactory.createBasicLiteralNode(SyntaxKind.STRING_LITERAL,
                        AbstractNodeFactory.createLiteralValueToken(SyntaxKind.STRING_LITERAL,
                                "`" + entity.tableName + "`",
                                NodeFactory.createEmptyMinutiaeList(), NodeFactory.createEmptyMinutiaeList()))),
                false);
        List<Node> subFields = new ArrayList<>();
        for (HashMap i : entity.fields) {
            if (!subFields.isEmpty()) {
                subFields.add(NodeFactory.createBasicLiteralNode(SyntaxKind.STRING_LITERAL,
                        AbstractNodeFactory.createLiteralValueToken(SyntaxKind.STRING_LITERAL, ", \n",
                                NodeFactory.createEmptyMinutiaeList(), NodeFactory.createEmptyMinutiaeList())));
            }
            if (i.get("autoGenerated").equals("true")) {
                subFields.add(NodeFactory.createSpecificFieldNode(null,
                        AbstractNodeFactory.createIdentifierToken(i.get("fieldName").toString()),
                        SyntaxTreeConstants.SYNTAX_TREE_COLON,
                        NodeParser.parseExpression(String.format(
                                "{columnName: \"%s\", 'type: %s, autoGenerated: %s}",
                                i.get("fieldName").toString().trim().replaceAll("\'", ""),
                                i.get("fieldType").toString().trim().replaceAll(" ", ""),
                                i.get("autoGenerated").toString().trim()))));
                if (i.get("fieldName").toString().equals(entity.keys[0].trim().replaceAll("\"",
                        ""))) {
                    keyAutoInc = true;
                }

            } else {
                subFields.add(NodeFactory.createSpecificFieldNode(null,
                        AbstractNodeFactory.createIdentifierToken(i.get("fieldName").toString()),
                        SyntaxTreeConstants.SYNTAX_TREE_COLON,
                        NodeParser.parseExpression(String.format("{columnName: \"%s\", 'type: %s}",
                                i.get("fieldName").toString().trim().replaceAll("\'", ""),
                                i.get("fieldType").toString().trim().replaceAll(" ", "")
                        ))));
            }
        }

        client.addMember(TypeDescriptor.getObjectFieldNode(
                        "private",
                        new String[]{"final"},
                        TypeDescriptor.getSimpleNameReferenceNode("map<persist:FieldMetadata>"),
                        "fieldMetadata", NodeFactory.createMappingConstructorExpressionNode(
                                SyntaxTreeConstants.SYNTAX_TREE_OPEN_BRACE, AbstractNodeFactory
                                .createSeparatedNodeList(subFields),
                                SyntaxTreeConstants.SYNTAX_TREE_CLOSE_BRACE)
                )
        , true);
        String keysString = "";
        for (String i : entity.keys) {
            if (!keysString.equals("")) {
                keysString += ", ";
            }
            keysString += i;
        }
        client.addMember(TypeDescriptor.getObjectFieldNode(
                "private",
                new String[]{},
                TypeDescriptor.getArrayTypeDescriptorNode("string"),
                "keyFields", NodeFactory.createListConstructorExpressionNode(
                        SyntaxTreeConstants.SYNTAX_TREE_OPEN_BRACKET, AbstractNodeFactory
                                .createSeparatedNodeList(NodeFactory.createBasicLiteralNode(SyntaxKind.STRING_LITERAL,
                                        AbstractNodeFactory.createLiteralValueToken(SyntaxKind.STRING_LITERAL,
                                                keysString, NodeFactory.createEmptyMinutiaeList(),
                                                NodeFactory.createEmptyMinutiaeList())))
                        , SyntaxTreeConstants.SYNTAX_TREE_CLOSE_BRACKET)
        ), false);

        client.addMember(TypeDescriptor.getObjectFieldNodeWithoutExpression(
                        "private",
                        new String[]{},
                        TypeDescriptor.getQualifiedNameReferenceNode("persist",
                                "SQLClient"),
                        "persistClient"), true);

        Function init = new Function("init");
        init.addQualifiers(new String[]{"public"});
        init.addReturns(TypeDescriptor.getOptionalTypeDescriptorNode("persist", "Error"));
        init.addStatement(NodeParser.parseStatement(
                "mysql:Client dbClient = check new (host = HOST, user = USER, password = PASSWORD, database " +
                        "= DATABASE, port = PORT);"));
        init.addStatement(NodeParser.parseStatement("self.persistClient " +
                "= check new (self.entityName, self.tableName, self.fieldMetadata, self.keyFields, dbClient);"));

        Function create = new Function("create");
        create.addRequiredParameter(
                TypeDescriptor.getSimpleNameReferenceNode(entity.entityName),
                "value"
        );
        create.addQualifiers(new String[]{"remote"});
        create.addReturns(TypeDescriptor.getUnionTypeDescriptorNode(SyntaxTreeConstants.SYNTAX_TREE_VAR_INT,
                TypeDescriptor.getOptionalTypeDescriptorNode("persist", "Error")));
        create.addStatement(NodeParser.parseStatement("sql:ExecutionResult result = " +
                "check self.persistClient.runInsertQuery(value);"));
        if (!keyAutoInc) {
            IfElse valueNilCheck = new IfElse(NodeParser.parseExpression("result.lastInsertId is ()"));
            valueNilCheck.addIfStatement(NodeParser.parseStatement(String.format("return value.%s;",
                    entity.keys[0].trim().replaceAll("\"", ""))));
            create.addIfElseStatement(valueNilCheck.getIfElseStatementNode());
        }


        create.addStatement(NodeParser.parseStatement("return <int>result.lastInsertId;"));

        Function readByKey = new Function("readByKey");
        readByKey.addRequiredParameter(
                TypeDescriptor.getBuiltinSimpleNameReferenceNode("int"),
                "key"
        );
        readByKey.addQualifiers(new String[]{"remote"});
        readByKey.addReturns(TypeDescriptor.getUnionTypeDescriptorNode(
                TypeDescriptor.getSimpleNameReferenceNode(entity.entityName),
                TypeDescriptor.getQualifiedNameReferenceNode("persist", "Error")));
        readByKey.addStatement(NodeParser.parseStatement(String.format("return (check self.persistClient." +
                "runReadByKeyQuery(%s, key)).cloneWithType(%s);", entity.entityName, entity.entityName)));

        Function read = new Function("read");
        read.addRequiredParameterWithDefault(
                TypeDescriptor.getOptionalTypeDescriptorNode("", TypeDescriptor
                        .getMapTypeDescriptorNode(SyntaxTreeConstants.SYNTAX_TREE_VAR_ANYDATA).toSourceCode()),
                "filter");
        read.addQualifiers(new String[]{"remote"});
        read.addReturns(TypeDescriptor.getUnionTypeDescriptorNode(TypeDescriptor.getStreamTypeDescriptorNode(
                TypeDescriptor.getSimpleNameReferenceNode(entity.entityName),
                        TypeDescriptor.getOptionalTypeDescriptorNode("persist", "Error")),
                TypeDescriptor.getQualifiedNameReferenceNode("persist", "Error")));
        read.addStatement(NodeParser.parseStatement("stream<anydata, error?> result = " +
                "check self.persistClient.runReadQuery(filter);"));
        read.addStatement(NodeParser.parseStatement(String.format("return new stream<%s, " +
                "error?>(new %sStream(result));", entity.entityName, entity.entityName)));

        Function update = new Function("update");
        update.addRequiredParameter(
                TypeDescriptor.getRecordTypeDescriptorNode(),
                "'object"
        );
        update.addRequiredParameter(
                TypeDescriptor.getBuiltinSimpleNameReferenceNode(TypeDescriptor
                        .getMapTypeDescriptorNode(SyntaxTreeConstants.SYNTAX_TREE_VAR_ANYDATA).toSourceCode()),
                "filter"
        );
        update.addQualifiers(new String[]{"remote"});
        update.addReturns(TypeDescriptor.getOptionalTypeDescriptorNode("persist", "Error"));
        update.addStatement(NodeParser.parseStatement("_ = " +
                "check self.persistClient.runUpdateQuery('object, filter);"));


        Function delete = new Function("delete");
        delete.addRequiredParameter(
                TypeDescriptor.getBuiltinSimpleNameReferenceNode(TypeDescriptor
                        .getMapTypeDescriptorNode(SyntaxTreeConstants.SYNTAX_TREE_VAR_ANYDATA).toSourceCode()),
                "filter"
        );
        delete.addQualifiers(new String[]{"remote"});
        delete.addReturns(TypeDescriptor.getOptionalTypeDescriptorNode("persist", "Error"));
        delete.addStatement(NodeParser.parseStatement("_ = check self.persistClient.runDeleteQuery(filter);"));

        Function close = new Function("close");
        close.addQualifiers(new String[]{});
        close.addReturns(TypeDescriptor.getOptionalTypeDescriptorNode("persist", "Error"));
        close.addStatement(NodeParser.parseStatement("return self.persistClient.close();"));



        client.addMember(init.getFunctionDefinitionNode(), true);
        client.addMember(create.getFunctionDefinitionNode(), true);
        client.addMember(readByKey.getFunctionDefinitionNode(), true);
        client.addMember(read.getFunctionDefinitionNode(), true);
        client.addMember(update.getFunctionDefinitionNode(), true);
        client.addMember(delete.getFunctionDefinitionNode(), true);
        client.addMember(close.getFunctionDefinitionNode(), true);

        moduleMembers = moduleMembers.add(client.getClassDefinitionNode());

        Class clientStream = new Class(className + "Stream", true);

        clientStream.addMember(NodeFactory.createBasicLiteralNode(SyntaxKind.STRING_LITERAL,
                AbstractNodeFactory.createLiteralValueToken(SyntaxKind.STRING_LITERAL, " ",
                        NodeFactory.createEmptyMinutiaeList(), NodeFactory.createEmptyMinutiaeList())), true);

        clientStream.addMember(TypeDescriptor.getObjectFieldNodeWithoutExpression(
                        "private",
                        new String[]{},
                        TypeDescriptor.getStreamTypeDescriptorNode(
                            SyntaxTreeConstants.SYNTAX_TREE_VAR_ANYDATA,
                            TypeDescriptor.getOptionalTypeDescriptorNode("persist", "Error")),
                        "anydataStream"), true);

        Function initStream = new Function("init");
        initStream.addQualifiers(new String[]{"public", "isolated"});
        initStream.addStatement(NodeParser.parseStatement(
                "self.anydataStream = anydataStream;"));
        initStream.addRequiredParameter(
                TypeDescriptor.getStreamTypeDescriptorNode(
                        SyntaxTreeConstants.SYNTAX_TREE_VAR_ANYDATA,
                        TypeDescriptor.getOptionalTypeDescriptorNode("persist", "Error")),
                "anydataStream");

        Function nextStream = new Function("next");
        nextStream.addQualifiers(new String[]{"public", "isolated"});
        nextStream.addReturns(NodeParser.parseTypeDescriptor(String.format("record {|%s value;|}|persist:Error?",
                entity.entityName)));
        nextStream.addStatement(NodeParser.parseStatement("var streamValue = self.anydataStream.next();"));

        IfElse streamValueNilCheck = new IfElse(NodeParser.parseExpression("streamValue is ()"));
        streamValueNilCheck.addIfStatement(NodeParser.parseStatement("return streamValue;"));
        IfElse streamValueErrorCheck = new IfElse(NodeParser.parseExpression("(streamValue is error)"));
        streamValueErrorCheck.addIfStatement(NodeParser.parseStatement("return streamValue;"));
        streamValueErrorCheck.addElseStatement(NodeParser.parseStatement(String.format(
                "record {|%s value;|} nextRecord = {value: check streamValue.value.cloneWithType(%s)};",
                entity.entityName, entity.entityName)));
        streamValueErrorCheck.addElseStatement(NodeParser.parseStatement("return nextRecord;"));
        streamValueNilCheck.addElseBody(streamValueErrorCheck);

        nextStream.addIfElseStatement(streamValueNilCheck.getIfElseStatementNode());

        Function closeStream = new Function("close");
        closeStream.addQualifiers(new String[]{"public", "isolated"});
        closeStream.addReturns(TypeDescriptor.getOptionalTypeDescriptorNode("persist", "Error"));
        closeStream.addStatement(NodeParser.parseStatement("return self.anydataStream.close();"));

        clientStream.addMember(initStream.getFunctionDefinitionNode(), true);
        clientStream.addMember(nextStream.getFunctionDefinitionNode(), true);
        clientStream.addMember(closeStream.getFunctionDefinitionNode(), true);

        moduleMembers = moduleMembers.add(clientStream.getClassDefinitionNode());

        Token eofToken = AbstractNodeFactory.createIdentifierToken("");
        ModulePartNode modulePartNode = NodeFactory.createModulePartNode(imports, moduleMembers, eofToken);
        TextDocument textDocument = TextDocuments.from("");
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

    private static String readMetaData(MetadataNode metaD) {
        NodeList<AnnotationNode> annotations = metaD.annotations();
        for (AnnotationNode annotation : annotations) {
            Node annotReference = annotation.annotReference();
            if (annotReference.kind() == SyntaxKind.QUALIFIED_NAME_REFERENCE) {
                QualifiedNameReferenceNode qualifiedNameRef =
                        (QualifiedNameReferenceNode) annotReference;
                if (qualifiedNameRef.identifier().text().equals("AutoIncrement") && qualifiedNameRef
                        .modulePrefix().text().equals("persist")) {
                    return "true";
                }
            }
        }
        return "false";
    }
}
