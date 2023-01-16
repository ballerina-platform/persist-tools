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
import io.ballerina.compiler.syntax.tree.ClassDefinitionNode;
import io.ballerina.compiler.syntax.tree.Node;
import io.ballerina.compiler.syntax.tree.NodeFactory;
import io.ballerina.compiler.syntax.tree.NodeList;
import io.ballerina.compiler.syntax.tree.Token;
import io.ballerina.persist.nodegenerator.SyntaxTokenConstants;

/**
 * Class encapsulating methods to create Ballerina Classes.
 *
 * @since 0.1.0
 */
public class Client {

    private Token visibilityQualifier;
    private NodeList<Token> classTypeQualifiers;
    private final Token classKeyWord = AbstractNodeFactory.createIdentifierToken(ComponentConstants.TAG_CLASS);
    private final Token className;
    private NodeList<Node> members;

    private boolean timeImport = false;

    public Client(String name, boolean isPublic) {
        if (isPublic) {
            visibilityQualifier = AbstractNodeFactory.createIdentifierToken(ComponentConstants.TAG_PUBLIC);
        }
        classTypeQualifiers = AbstractNodeFactory.createEmptyNodeList();
        className = AbstractNodeFactory.createIdentifierToken(name + " ");
        members = NodeFactory.createEmptyNodeList();
    }

    public ClassDefinitionNode getClassDefinitionNode() {
        return NodeFactory.createClassDefinitionNode(
                null,
                visibilityQualifier,
                classTypeQualifiers,
                classKeyWord,
                className,
                SyntaxTokenConstants.SYNTAX_TREE_OPEN_BRACE,
                members,
                SyntaxTokenConstants.SYNTAX_TREE_CLOSE_BRACE,
                null);
    }

    public void addMember(Node member, boolean newLine) {
        if (newLine) {
            members = members.add(SyntaxTokenConstants.SYNTAX_TREE_BLANK_LINE);
        }
        members = members.add(member);
    }

    public void addQualifiers(String[] qualifiers) {
        for (String qualifier : qualifiers) {
            classTypeQualifiers = classTypeQualifiers.add(AbstractNodeFactory.createIdentifierToken(qualifier + " "));
        }
    }

    public boolean isTimeImport() {
        return timeImport;
    }

    public void setTimeImport(boolean timeImport) {
        this.timeImport = timeImport;
    }
}
