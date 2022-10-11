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
import io.ballerina.compiler.syntax.tree.EnumDeclarationNode;
import io.ballerina.compiler.syntax.tree.MetadataNode;
import io.ballerina.compiler.syntax.tree.Node;
import io.ballerina.compiler.syntax.tree.NodeFactory;
import io.ballerina.compiler.syntax.tree.Token;
import io.ballerina.persist.nodegenerator.SyntaxTreeConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing EnumDeclarationNode.
 *
 * @since 0.1.0
 */
public class Enum {

    private final MetadataNode metadata;
    private final Token qualifier;
    private final String name;
    private final List<Node> enumMemberList;

    public Enum(String name) {
        this.name = name;
        qualifier = AbstractNodeFactory.createIdentifierToken("public ");
        metadata = null;
        enumMemberList = new ArrayList<>();
    }

    public EnumDeclarationNode getEnumDeclarationNode() {
        return NodeFactory.createEnumDeclarationNode(
                metadata,
                qualifier,
                SyntaxTreeConstants.SYNTAX_TREE_KEYWORD_ENUM,
                AbstractNodeFactory.createIdentifierToken(name),
                SyntaxTreeConstants.SYNTAX_TREE_OPEN_BRACE,
                NodeFactory.createSeparatedNodeList(enumMemberList),
                SyntaxTreeConstants.SYNTAX_TREE_CLOSE_BRACE
        );
    }

    public void addMember(Node member) {
        if (enumMemberList.size() > 0) {
            enumMemberList.add(SyntaxTreeConstants.SYNTAX_TREE_COMMA);
        }
        enumMemberList.add(member);
    }
}
