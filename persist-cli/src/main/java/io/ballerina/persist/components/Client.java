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
import io.ballerina.compiler.syntax.tree.LiteralValueToken;
import io.ballerina.compiler.syntax.tree.MetadataNode;
import io.ballerina.compiler.syntax.tree.Node;
import io.ballerina.compiler.syntax.tree.NodeFactory;
import io.ballerina.compiler.syntax.tree.NodeList;
import io.ballerina.compiler.syntax.tree.SyntaxKind;
import io.ballerina.compiler.syntax.tree.Token;
import io.ballerina.persist.nodegenerator.syntax.constants.SyntaxTokenConstants;

import static io.ballerina.persist.PersistToolsConstants.SupportedDataSources.GOOGLE_SHEETS;
import static io.ballerina.persist.PersistToolsConstants.SupportedDataSources.H2_DB;
import static io.ballerina.persist.PersistToolsConstants.SupportedDataSources.IN_MEMORY_TABLE;
import static io.ballerina.persist.PersistToolsConstants.SupportedDataSources.MSSQL_DB;
import static io.ballerina.persist.PersistToolsConstants.SupportedDataSources.MYSQL_DB;
import static io.ballerina.persist.PersistToolsConstants.SupportedDataSources.POSTGRESQL_DB;
import static io.ballerina.persist.PersistToolsConstants.SupportedDataSources.REDIS;

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
    private final MetadataNode metadata;

    public Client(String name, String dataStore) {
        visibilityQualifier = AbstractNodeFactory.createIdentifierToken(ComponentConstants.TAG_PUBLIC);
        classTypeQualifiers = AbstractNodeFactory.createEmptyNodeList();
        className = AbstractNodeFactory.createIdentifierToken(name + " ");
        members = NodeFactory.createEmptyNodeList();
        String documentation = String.format("# %s persist client.", getDatasourceTypeName(dataStore));
        LiteralValueToken documentationToken =
                AbstractNodeFactory.createLiteralValueToken(
                        SyntaxKind.DOCUMENTATION_DESCRIPTION,
                        documentation,
                        NodeFactory.createEmptyMinutiaeList(),
                        NodeFactory.createMinutiaeList(
                                AbstractNodeFactory.createEndOfLineMinutiae(System.lineSeparator())));
        metadata = NodeFactory.createMetadataNode(documentationToken, NodeFactory.createEmptyNodeList());
    }

    private static String getDatasourceTypeName(String dataSource) {
        return switch (dataSource) {
            case MYSQL_DB -> "MySQL";
            case MSSQL_DB -> "MSSQL";
            case GOOGLE_SHEETS -> "Google Sheets";
            case IN_MEMORY_TABLE -> "In-Memory";
            case POSTGRESQL_DB -> "PostgreSQL";
            case REDIS -> "Redis";
            case H2_DB -> "H2";
            default -> dataSource;
        };
    }

    public ClassDefinitionNode getClassDefinitionNode() {
        return NodeFactory.createClassDefinitionNode(
                metadata,
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
}
