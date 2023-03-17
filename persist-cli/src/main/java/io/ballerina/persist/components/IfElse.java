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

import io.ballerina.compiler.syntax.tree.ExpressionNode;
import io.ballerina.compiler.syntax.tree.IfElseStatementNode;
import io.ballerina.compiler.syntax.tree.Node;
import io.ballerina.compiler.syntax.tree.NodeFactory;
import io.ballerina.compiler.syntax.tree.NodeList;
import io.ballerina.compiler.syntax.tree.StatementNode;
import io.ballerina.persist.nodegenerator.SyntaxTokenConstants;

/**
 * Class representing IfElseStatementNode.
 *
 * @since 0.1.0
 */
public class IfElse {

    private final ExpressionNode condition;
    private Node elseBody;
    private NodeList<StatementNode> ifStatements;

    private NodeList<StatementNode> elseStatements;

    public IfElse(ExpressionNode condition) {
        this.condition = condition;
        ifStatements = NodeFactory.createEmptyNodeList();
        elseStatements = NodeFactory.createEmptyNodeList();
    }

    public IfElseStatementNode getIfElseStatementNode() {
        if (elseStatements.size() > 0) {
            elseBody = NodeFactory.createElseBlockNode(
                    SyntaxTokenConstants.SYNTAX_TREE_KEYWORD_ELSE,
                    NodeFactory.createBlockStatementNode(
                            SyntaxTokenConstants.SYNTAX_TREE_OPEN_BRACE,
                            elseStatements,
                            SyntaxTokenConstants.SYNTAX_TREE_CLOSE_BRACE
                    )
            );
        }

        return NodeFactory.createIfElseStatementNode(
                SyntaxTokenConstants.SYNTAX_TREE_KEYWORD_IF,
                condition,
                NodeFactory.createBlockStatementNode(
                        SyntaxTokenConstants.SYNTAX_TREE_OPEN_BRACE,
                        ifStatements,
                        SyntaxTokenConstants.SYNTAX_TREE_CLOSE_BRACE
                ),
                elseBody
        );
    }

    public void addIfStatement(StatementNode statement) {
        ifStatements = ifStatements.add(statement);
    }
}
