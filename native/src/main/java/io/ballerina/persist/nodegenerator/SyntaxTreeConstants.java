package io.ballerina.persist.nodegenerator;

import io.ballerina.compiler.syntax.tree.AbstractNodeFactory;
import io.ballerina.compiler.syntax.tree.Token;
import io.ballerina.compiler.syntax.tree.TypeDescriptorNode;
import io.ballerina.persist.components.TypeDescriptor;

/**
 * Class encapsulating all the Syntax tree related constants.
 *
 @since 0.1.0
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
    public static final Token SYNTAX_TREE_KEYWORD_RETURN = AbstractNodeFactory.createIdentifierToken("return ");
    public static final Token SYNTAX_TREE_KEYWORD_STREAM = AbstractNodeFactory.createIdentifierToken("stream ");
    public static final Token SYNTAX_TREE_IT = AbstractNodeFactory.createIdentifierToken("<");
    public static final Token SYNTAX_TREE_GT = AbstractNodeFactory.createIdentifierToken(">");
    public static final TypeDescriptorNode SYNTAX_TREE_VAR_INT =
            TypeDescriptor.getBuiltinSimpleNameReferenceNode("int");
    public static final TypeDescriptorNode SYNTAX_TREE_VAR_ANYDATA =
            TypeDescriptor.getBuiltinSimpleNameReferenceNode("anydata");
    public static final Token SYNTAX_TREE_QUESTION_MARK = AbstractNodeFactory.createIdentifierToken("?");

    public static final Token SYNTAX_TREE_KEYWORD_RECORD = AbstractNodeFactory.createIdentifierToken("record ");
    public static final Token SYNTAX_TREE_KEYWORD_IF = AbstractNodeFactory.createIdentifierToken("if ");
    public static final Token SYNTAX_TREE_KEYWORD_ELSE = AbstractNodeFactory.createIdentifierToken("else ");

    public static final TypeDescriptorNode SYNTAX_TREE_VAR_STRING =
            TypeDescriptor.getBuiltinSimpleNameReferenceNode("string");


}
