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

package io.ballerina.persist.components;

import io.ballerina.compiler.syntax.tree.AbstractNodeFactory;
import io.ballerina.compiler.syntax.tree.AnnotationNode;
import io.ballerina.compiler.syntax.tree.FunctionBodyNode;
import io.ballerina.compiler.syntax.tree.FunctionDefinitionNode;
import io.ballerina.compiler.syntax.tree.FunctionSignatureNode;
import io.ballerina.compiler.syntax.tree.IdentifierToken;
import io.ballerina.compiler.syntax.tree.IfElseStatementNode;
import io.ballerina.compiler.syntax.tree.Node;
import io.ballerina.compiler.syntax.tree.NodeFactory;
import io.ballerina.compiler.syntax.tree.NodeList;
import io.ballerina.compiler.syntax.tree.NodeParser;
import io.ballerina.compiler.syntax.tree.ReturnTypeDescriptorNode;
import io.ballerina.compiler.syntax.tree.StatementNode;
import io.ballerina.compiler.syntax.tree.SyntaxKind;
import io.ballerina.compiler.syntax.tree.Token;
import io.ballerina.compiler.syntax.tree.TypeDescriptorNode;
import io.ballerina.persist.nodegenerator.SyntaxTokenConstants;

import java.util.ArrayList;
import java.util.List;

import static io.ballerina.persist.components.TypeDescriptor.getReturnTypeDescriptorNode;

/**
 * Class representing FunctionDefinitionNode.
 *
 * @since 0.1.0
 */
public class Function {

    /**
     * Represents open and close bracket type.
     */
    public enum Bracket {
        SQUARE,
        CURLY,
        PAREN
    }

    private final SyntaxKind kind;
    private final boolean returnError;
    private NodeList<Token> qualifierList;
    private final Token finalKeyWord = AbstractNodeFactory.createIdentifierToken(ComponentConstants.TAG_FUNCTION);
    private final IdentifierToken functionName;
    private NodeList<Node> relativeResourcePath;
    private final List<Node> parameters;
    private ReturnTypeDescriptorNode returnTypeDescriptorNode;
    private NodeList<StatementNode> statements;

    public Function(String name, SyntaxKind kind, boolean returnError) {
        qualifierList = AbstractNodeFactory.createEmptyNodeList();
        functionName = AbstractNodeFactory.createIdentifierToken(name + " ");
        relativeResourcePath = AbstractNodeFactory.createEmptyNodeList();
        parameters = new ArrayList<>();
        statements = NodeFactory.createEmptyNodeList();
        this.kind = kind;
        this.returnError = returnError;
    }

    public FunctionDefinitionNode getFunctionDefinitionNode() {
        return NodeFactory.createFunctionDefinitionNode(
                kind,
                null,
                qualifierList,
                finalKeyWord,
                functionName,
                relativeResourcePath,
                getFunctionSignature(),
                getFunctionBody()

        );
    }

    public void addRelativeResourcePaths(NodeList<Node> paths) {
        relativeResourcePath = paths;
    }

    public void addQualifiers(String[] qualifiers) {
        for (String qualifier : qualifiers) {
            qualifierList = qualifierList.add(AbstractNodeFactory.createIdentifierToken(qualifier + " "));
        }
    }

    private FunctionSignatureNode getFunctionSignature() {
        return NodeFactory.createFunctionSignatureNode(
                SyntaxTokenConstants.SYNTAX_TREE_OPEN_PAREN,
                AbstractNodeFactory.createSeparatedNodeList(parameters),
                SyntaxTokenConstants.SYNTAX_TREE_CLOSE_PAREN,
                returnTypeDescriptorNode
        );
    }

    public void addRequiredParameter(Node typeName, String name) {
        if (parameters.size() > 0) {
            parameters.add(SyntaxTokenConstants.SYNTAX_TREE_COMMA);
        }
        NodeList<AnnotationNode> annotations = NodeFactory.createEmptyNodeList();
        parameters.add(
                NodeFactory.createRequiredParameterNode(
                        annotations,
                        typeName,
                        AbstractNodeFactory.createIdentifierToken(name)
                )
        );
    }

    public void addRequiredParameterWithDefault(Node typeName, String name, Bracket brkt) {
        if (parameters.size() > 0) {
            parameters.add(SyntaxTokenConstants.SYNTAX_TREE_COMMA);
        }
        Token open;
        Token close;
        if (brkt == Bracket.SQUARE) {
            open = SyntaxTokenConstants.SYNTAX_TREE_OPEN_BRACKET;
            close = SyntaxTokenConstants.SYNTAX_TREE_CLOSE_BRACKET;
        } else if (brkt == Bracket.CURLY) {
            open = SyntaxTokenConstants.SYNTAX_TREE_OPEN_BRACE;
            close = SyntaxTokenConstants.SYNTAX_TREE_CLOSE_BRACE;
        } else {
            open = SyntaxTokenConstants.SYNTAX_TREE_OPEN_PAREN;
            close = SyntaxTokenConstants.SYNTAX_TREE_CLOSE_PAREN;
        }
        NodeList<AnnotationNode> annotations = NodeFactory.createEmptyNodeList();
        parameters.add(
                NodeFactory.createDefaultableParameterNode(
                        annotations,
                        typeName,
                        AbstractNodeFactory.createIdentifierToken(name),
                        SyntaxTokenConstants.SYNTAX_TREE_EQUAL,
                        NodeFactory.createListConstructorExpressionNode(
                                open, AbstractNodeFactory
                                        .createSeparatedNodeList(NodeFactory.createBasicLiteralNode(
                                                SyntaxKind.NIL_LITERAL,
                                                AbstractNodeFactory.createLiteralValueToken(SyntaxKind.NIL_LITERAL,
                                                        "", NodeFactory.createEmptyMinutiaeList(),
                                                        NodeFactory.createEmptyMinutiaeList()))), close)));
    }

    public void addReturns(TypeDescriptorNode node) {
        returnTypeDescriptorNode = getReturnTypeDescriptorNode(node);
    }

    private FunctionBodyNode getFunctionBody() {
        NodeList<StatementNode> tempStatements = NodeFactory.createEmptyNodeList();;
        if (this.returnError) {
            tempStatements = tempStatements.add(NodeParser.parseStatement(
                    "return error persist:Error(\"unsupported operation\");"));
        } else {
            tempStatements = tempStatements.add(NodeParser.parseStatement(
                    "return new();"));
        }

        return NodeFactory.createFunctionBodyBlockNode(
                SyntaxTokenConstants.SYNTAX_TREE_OPEN_BRACE,
                null,
                tempStatements,
                SyntaxTokenConstants.SYNTAX_TREE_CLOSE_BRACE,
                null
        );
//        NodeList<AnnotationNode> annotations = NodeFactory.createEmptyNodeList();
//        return NodeFactory.createExternalFunctionBodyNode(
//                NodeFactory.createToken(SyntaxKind.EQUAL_TOKEN,
//                        AbstractNodeFactory.createEmptyMinutiaeList(),
//                        NodeFactory.createMinutiaeList(AbstractNodeFactory.createWhitespaceMinutiae(" "))),
//                annotations,
//                NodeFactory.createToken(SyntaxKind.EXTERNAL_KEYWORD),
//                NodeFactory.createToken(SyntaxKind.SEMICOLON_TOKEN,
//                        AbstractNodeFactory.createEmptyMinutiaeList(),
//                        NodeFactory.createMinutiaeList(AbstractNodeFactory.createEndOfLineMinutiae("\n")))
//        );
    }

    public void addStatement(StatementNode node) {
        statements = statements.add(node);
    }

    public void addIfElseStatement(IfElseStatementNode node) {
        statements = statements.add(node);
    }

}
