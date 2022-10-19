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
import io.ballerina.compiler.syntax.tree.Token;
import io.ballerina.toml.syntax.tree.KeyValueNode;
import io.ballerina.toml.validator.SampleNodeGenerator;

/**
 * Class encapsulating all the Syntax tree related constants.
 *
 * @since 0.1.0
 */
public class SyntaxTreeConstants {

    private SyntaxTreeConstants() {

    }

    public static final Token SYNTAX_TREE_SEMICOLON = AbstractNodeFactory.createIdentifierToken(";");
    public static final Token SYNTAX_TREE_COLON = AbstractNodeFactory.createIdentifierToken(":");
    public static final Token SYNTAX_TREE_OPEN_BRACE = AbstractNodeFactory.createIdentifierToken("{");
    public static final Token SYNTAX_TREE_CLOSE_BRACE = AbstractNodeFactory.createIdentifierToken("}");
    public static final Token SYNTAX_TREE_OPEN_PAREN = AbstractNodeFactory.createIdentifierToken("(");
    public static final Token SYNTAX_TREE_CLOSE_PAREN = AbstractNodeFactory.createIdentifierToken(")");
    public static final Token SYNTAX_TREE_OPEN_BRACKET = AbstractNodeFactory.createIdentifierToken("[");
    public static final Token SYNTAX_TREE_CLOSE_BRACKET = AbstractNodeFactory.createIdentifierToken("]");
    public static final Token SYNTAX_TREE_EQUAL = AbstractNodeFactory.createIdentifierToken("=");
    public static final Token SYNTAX_TREE_PIPE = AbstractNodeFactory.createIdentifierToken("|");
    public static final Token SYNTAX_TREE_SLASH = AbstractNodeFactory.createIdentifierToken("/");
    public static final Token SYNTAX_TREE_COMMA = AbstractNodeFactory.createIdentifierToken(",");

    public static final Token SYNTAX_TREE_BLANK_LINE = AbstractNodeFactory.createIdentifierToken("\n\n");

    public static final Token SYNTAX_TREE_KEYWORD_IMPORT = AbstractNodeFactory.createIdentifierToken("import ");
    public static final Token SYNTAX_TREE_KEYWORD_RETURNS = AbstractNodeFactory.createIdentifierToken("returns ");
    public static final Token SYNTAX_TREE_KEYWORD_STREAM = AbstractNodeFactory.createIdentifierToken("stream ");
    public static final Token SYNTAX_TREE_IT = AbstractNodeFactory.createIdentifierToken("<");
    public static final Token SYNTAX_TREE_GT = AbstractNodeFactory.createIdentifierToken(">");
    public static final Token TYPE_KEYWORD = AbstractNodeFactory.createIdentifierToken("type");
    public static final Token SYNTAX_TREE_QUESTION_MARK = AbstractNodeFactory.createIdentifierToken("?");

    public static final Token SYNTAX_TREE_KEYWORD_RECORD = AbstractNodeFactory.createIdentifierToken("record ");
    public static final Token SYNTAX_TREE_KEYWORD_IF = AbstractNodeFactory.createIdentifierToken("if ");
    public static final Token SYNTAX_TREE_KEYWORD_ELSE = AbstractNodeFactory.createIdentifierToken("else ");

    public static final Token SYNTAX_TREE_KEYWORD_ENUM = AbstractNodeFactory.createIdentifierToken("enum ");

    public static final Token SYNTAX_TREE_KEYWORD_CHECK = AbstractNodeFactory.createIdentifierToken("check ");
    public static final String JAVA_11_DEPENDANCY = "platform.java11.dependency";
    public static final String GROUP_ID_KEYWORD = "groupId";

    public static final KeyValueNode GROUP_ID = SampleNodeGenerator.createStringKV("groupId", "mysql", null);
    public static final KeyValueNode ARTIFACT_ID = SampleNodeGenerator.createStringKV("artifactId",
            "mysql-connector-java", null);
    public static final KeyValueNode VERSION = SampleNodeGenerator.createStringKV("version",
            "8.0.29", null);

}
