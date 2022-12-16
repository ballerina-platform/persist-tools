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
import io.ballerina.compiler.syntax.tree.NodeFactory;
import io.ballerina.compiler.syntax.tree.SyntaxKind;
import io.ballerina.compiler.syntax.tree.Token;

/**
 * Class encapsulating all the Syntax tree related constants.
 *
 * @since 0.1.0
 */
public class SyntaxTokenConstants {

    private SyntaxTokenConstants() {}
    public static final Token SYNTAX_TREE_SEMICOLON = AbstractNodeFactory.createToken(SyntaxKind.SEMICOLON_TOKEN);
    public static final Token SYNTAX_TREE_COLON = AbstractNodeFactory.createToken(SyntaxKind.COLON_TOKEN);
    public static final Token SYNTAX_TREE_OPEN_BRACE = AbstractNodeFactory.createToken(SyntaxKind.OPEN_BRACE_TOKEN);
    public static final Token SYNTAX_TREE_CLOSE_BRACE = AbstractNodeFactory.createToken(SyntaxKind.CLOSE_BRACE_TOKEN);
    public static final Token SYNTAX_TREE_OPEN_PAREN = AbstractNodeFactory.createToken(SyntaxKind.OPEN_PAREN_TOKEN);
    public static final Token SYNTAX_TREE_CLOSE_PAREN = AbstractNodeFactory.createToken(SyntaxKind.CLOSE_PAREN_TOKEN);
    public static final Token SYNTAX_TREE_OPEN_BRACKET = AbstractNodeFactory.createToken(SyntaxKind.OPEN_BRACKET_TOKEN);
    public static final Token SYNTAX_TREE_CLOSE_BRACKET = AbstractNodeFactory.createToken(
            SyntaxKind.CLOSE_BRACKET_TOKEN);
    public static final Token SYNTAX_TREE_EQUAL = AbstractNodeFactory.createToken(SyntaxKind.EQUAL_TOKEN);
    public static final Token SYNTAX_TREE_PIPE = AbstractNodeFactory.createToken(SyntaxKind.PIPE_TOKEN);
    public static final Token SYNTAX_TREE_SLASH = AbstractNodeFactory.createToken(SyntaxKind.SLASH_TOKEN);
    public static final Token SYNTAX_TREE_COMMA = AbstractNodeFactory.createToken(SyntaxKind.COMMA_TOKEN);

    public static final Token SYNTAX_TREE_BLANK_LINE = AbstractNodeFactory.createIdentifierToken("\n\n");

    public static final Token SYNTAX_TREE_KEYWORD_IMPORT = AbstractNodeFactory.createToken(SyntaxKind.IMPORT_KEYWORD,
            AbstractNodeFactory.createEmptyMinutiaeList(),
            NodeFactory.createMinutiaeList(AbstractNodeFactory.createWhitespaceMinutiae(" ")));
    public static final Token SYNTAX_TREE_KEYWORD_RETURNS = AbstractNodeFactory.createToken(
            SyntaxKind.RETURNS_KEYWORD,
            AbstractNodeFactory.createEmptyMinutiaeList(),
            NodeFactory.createMinutiaeList(AbstractNodeFactory.createWhitespaceMinutiae(" ")));
    public static final Token SYNTAX_TREE_KEYWORD_STREAM = AbstractNodeFactory.createToken(SyntaxKind.STREAM_KEYWORD,
            AbstractNodeFactory.createEmptyMinutiaeList(),
            NodeFactory.createMinutiaeList(AbstractNodeFactory.createWhitespaceMinutiae(" ")));
    public static final Token SYNTAX_TREE_IT = AbstractNodeFactory.createToken(SyntaxKind.LT_TOKEN);
    public static final Token SYNTAX_TREE_GT = AbstractNodeFactory.createToken(SyntaxKind.GT_TOKEN);
    public static final Token SYNTAX_TREE_QUESTION_MARK = AbstractNodeFactory.createToken(
            SyntaxKind.QUESTION_MARK_TOKEN);

    public static final Token SYNTAX_TREE_KEYWORD_RECORD = AbstractNodeFactory.createToken(SyntaxKind.RECORD_KEYWORD,
            AbstractNodeFactory.createEmptyMinutiaeList(),
            NodeFactory.createMinutiaeList(AbstractNodeFactory.createWhitespaceMinutiae(" ")));
    public static final Token SYNTAX_TREE_KEYWORD_IF = AbstractNodeFactory.createToken(SyntaxKind.IF_KEYWORD,
            AbstractNodeFactory.createEmptyMinutiaeList(),
            NodeFactory.createMinutiaeList(AbstractNodeFactory.createWhitespaceMinutiae(" ")));
    public static final Token SYNTAX_TREE_KEYWORD_ELSE = AbstractNodeFactory.createToken(SyntaxKind.ELSE_KEYWORD,
            AbstractNodeFactory.createEmptyMinutiaeList(),
            NodeFactory.createMinutiaeList(AbstractNodeFactory.createWhitespaceMinutiae(" ")));

    public static final Token SYNTAX_TREE_KEYWORD_ENUM = AbstractNodeFactory.createToken(SyntaxKind.ENUM_KEYWORD,
            AbstractNodeFactory.createEmptyMinutiaeList(),
            NodeFactory.createMinutiaeList(AbstractNodeFactory.createWhitespaceMinutiae(" ")));

    public static final Token SYNTAX_TREE_KEYWORD_CHECK = AbstractNodeFactory.createToken(SyntaxKind.CHECK_KEYWORD,
            AbstractNodeFactory.createEmptyMinutiaeList(),
            NodeFactory.createMinutiaeList(AbstractNodeFactory.createWhitespaceMinutiae(" ")));

    public static final Token SYNTAX_TREE_AS = AbstractNodeFactory.createToken(SyntaxKind.AS_KEYWORD,
            NodeFactory.createMinutiaeList(AbstractNodeFactory.createWhitespaceMinutiae(" ")),
            NodeFactory.createMinutiaeList(AbstractNodeFactory.createWhitespaceMinutiae(" ")));
}
