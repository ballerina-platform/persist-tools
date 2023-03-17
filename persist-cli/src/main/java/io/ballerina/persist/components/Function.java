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

    private final SyntaxKind kind;
    private NodeList<Token> qualifierList;
    private final Token finalKeyWord = AbstractNodeFactory.createIdentifierToken(ComponentConstants.TAG_FUNCTION);
    private final IdentifierToken functionName;
    private NodeList<Node> relativeResourcePath;
    private final List<Node> parameters;
    private ReturnTypeDescriptorNode returnTypeDescriptorNode;
    private NodeList<StatementNode> statements;

    public Function(String name, SyntaxKind kind) {
        qualifierList = AbstractNodeFactory.createEmptyNodeList();
        functionName = AbstractNodeFactory.createIdentifierToken(name + " ");
        relativeResourcePath = AbstractNodeFactory.createEmptyNodeList();
        parameters = new ArrayList<>();
        statements = NodeFactory.createEmptyNodeList();
        this.kind = kind;
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

    public void addReturns(TypeDescriptorNode node) {
        returnTypeDescriptorNode = getReturnTypeDescriptorNode(node);
    }

    private FunctionBodyNode getFunctionBody() {
        return NodeFactory.createFunctionBodyBlockNode(
                SyntaxTokenConstants.SYNTAX_TREE_OPEN_BRACE,
                null,
                statements,
                SyntaxTokenConstants.SYNTAX_TREE_CLOSE_BRACE,
                null
        );
    }

    public void addStatement(StatementNode node) {
        statements = statements.add(node);
    }

    public void addIfElseStatement(IfElseStatementNode node) {
        statements = statements.add(node);
    }

}
