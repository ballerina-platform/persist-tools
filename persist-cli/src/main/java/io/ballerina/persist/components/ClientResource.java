/*
 * Copyright (c) 2023, WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
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

package io.ballerina.persist.components;

import io.ballerina.compiler.syntax.tree.AbstractNodeFactory;
import io.ballerina.compiler.syntax.tree.Node;
import io.ballerina.compiler.syntax.tree.NodeFactory;
import io.ballerina.compiler.syntax.tree.NodeList;
import io.ballerina.compiler.syntax.tree.SyntaxKind;
import io.ballerina.compiler.syntax.tree.Token;
import io.ballerina.persist.nodegenerator.SyntaxTokenConstants;

import java.util.List;

import static io.ballerina.persist.nodegenerator.BalSyntaxConstants.COMMA_SPACE;

/**
 *
 */
public class ClientResource {

    private final Token resourceName;

    private List<Node> metadata;

    private NodeList<Node> functions = NodeFactory.createEmptyNodeList();

    public ClientResource(String resourceName) {
        this.resourceName = AbstractNodeFactory.createIdentifierToken(resourceName);
    }

    public void addMetadata(Node member) {
        if (!metadata.isEmpty()) {
            metadata.add(NodeFactory.createBasicLiteralNode(SyntaxKind.STRING_LITERAL,
                    AbstractNodeFactory.createLiteralValueToken(SyntaxKind.STRING_LITERAL, COMMA_SPACE
                                    + System.lineSeparator(), NodeFactory.createEmptyMinutiaeList(),
                            NodeFactory.createEmptyMinutiaeList())));
        }
        metadata.add(member);
    }

    public void addFunction(Node function, boolean newLine) {
        if (functions.isEmpty()) {
            if (newLine) {
                functions = functions.add(SyntaxTokenConstants.SYNTAX_TREE_BLANK_LINE);
            }
        }

        functions = functions.add(function);
    }

    public NodeList<Node> getFunctions() {
        return functions;
    }

    public List<Node> getMetadata() {
        return metadata;
    }

    public Token getResourceName() {
        return resourceName;
    }
}
