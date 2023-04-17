/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com) All Rights Reserved.
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
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
package io.ballerina.persist.components.syntax;

import io.ballerina.compiler.syntax.tree.AbstractNodeFactory;
import io.ballerina.compiler.syntax.tree.IdentifierToken;
import io.ballerina.compiler.syntax.tree.ImportDeclarationNode;
import io.ballerina.compiler.syntax.tree.ImportOrgNameNode;
import io.ballerina.compiler.syntax.tree.ImportPrefixNode;
import io.ballerina.compiler.syntax.tree.NodeFactory;
import io.ballerina.compiler.syntax.tree.SeparatedNodeList;
import io.ballerina.compiler.syntax.tree.Token;
import io.ballerina.persist.nodegenerator.BalSyntaxConstants;
import io.ballerina.persist.nodegenerator.SyntaxTokenConstants;

import java.util.Locale;

import static io.ballerina.persist.nodegenerator.SyntaxTokenConstants.SYNTAX_TREE_SEMICOLON;

/**
 * This class implements the utility methods for syntax generate.
 *
 * @since 0.3.1
 */
public class Utils {

    public static String getEntityNameConstant(String entityName) {
        StringBuilder outputString = new StringBuilder();
        String[] splitedStrings = stripEscapeCharacter(entityName).split(
                BalSyntaxConstants.REGEX_FOR_SPLIT_BY_CAPITOL_LETTER);
        for (String splitedString : splitedStrings) {
            if (outputString.length() != 0) {
                outputString.append(BalSyntaxConstants.UNDERSCORE);
            }
            outputString.append(splitedString.toUpperCase(Locale.ENGLISH));
        }
        if (entityName.startsWith(BalSyntaxConstants.SINGLE_QUOTE)) {
            return BalSyntaxConstants.SINGLE_QUOTE + outputString;
        }
        return outputString.toString();
    }

    public static String stripEscapeCharacter(String fieldName) {
        return fieldName.startsWith(BalSyntaxConstants.SINGLE_QUOTE) ? fieldName.substring(1) : fieldName;
    }

    public static ImportDeclarationNode getImportDeclarationNode(String orgName, String moduleName,
                                                                  ImportPrefixNode prefix) {
        Token orgNameToken = AbstractNodeFactory.createIdentifierToken(orgName);
        ImportOrgNameNode importOrgNameNode = NodeFactory.createImportOrgNameNode(
                orgNameToken,
                SyntaxTokenConstants.SYNTAX_TREE_SLASH);
        Token moduleNameToken = AbstractNodeFactory.createIdentifierToken(moduleName);
        SeparatedNodeList<IdentifierToken> moduleNodeList = AbstractNodeFactory
                .createSeparatedNodeList(moduleNameToken);

        return NodeFactory.createImportDeclarationNode(
                SyntaxTokenConstants.SYNTAX_TREE_KEYWORD_IMPORT,
                importOrgNameNode,
                moduleNodeList,
                prefix,
                SYNTAX_TREE_SEMICOLON);
    }
}
