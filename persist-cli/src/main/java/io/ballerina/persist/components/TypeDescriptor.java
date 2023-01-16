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
import io.ballerina.compiler.syntax.tree.ArrayDimensionNode;
import io.ballerina.compiler.syntax.tree.ArrayTypeDescriptorNode;
import io.ballerina.compiler.syntax.tree.BuiltinSimpleNameReferenceNode;
import io.ballerina.compiler.syntax.tree.ExpressionNode;
import io.ballerina.compiler.syntax.tree.MapTypeDescriptorNode;
import io.ballerina.compiler.syntax.tree.Node;
import io.ballerina.compiler.syntax.tree.NodeFactory;
import io.ballerina.compiler.syntax.tree.NodeList;
import io.ballerina.compiler.syntax.tree.ObjectFieldNode;
import io.ballerina.compiler.syntax.tree.OptionalTypeDescriptorNode;
import io.ballerina.compiler.syntax.tree.QualifiedNameReferenceNode;
import io.ballerina.compiler.syntax.tree.RecordTypeDescriptorNode;
import io.ballerina.compiler.syntax.tree.ReturnTypeDescriptorNode;
import io.ballerina.compiler.syntax.tree.SimpleNameReferenceNode;
import io.ballerina.compiler.syntax.tree.StreamTypeDescriptorNode;
import io.ballerina.compiler.syntax.tree.SyntaxKind;
import io.ballerina.compiler.syntax.tree.Token;
import io.ballerina.compiler.syntax.tree.TypeDescriptorNode;
import io.ballerina.compiler.syntax.tree.UnionTypeDescriptorNode;
import io.ballerina.persist.nodegenerator.SyntaxTokenConstants;

/**
 * Class representing different types of TypeDescriptorNodes.
 *
 * @since 0.1.0
 */
public class TypeDescriptor {

    private TypeDescriptor() {}

    public static ReturnTypeDescriptorNode getReturnTypeDescriptorNode(Node type) {
        NodeList<AnnotationNode> annotations = NodeFactory.createEmptyNodeList();
        return NodeFactory.createReturnTypeDescriptorNode(
                SyntaxTokenConstants.SYNTAX_TREE_KEYWORD_RETURNS,
                annotations,
                type
        );
    }

    public static ObjectFieldNode getObjectFieldNode(String visibility, String[] qualifiers, Node typeName,
                                                     String fieldName, ExpressionNode expression) {
        NodeList<Token> qualifierList = NodeFactory.createEmptyNodeList();
        for (String qualifier : qualifiers) {
            qualifierList = qualifierList.add(AbstractNodeFactory.createIdentifierToken(qualifier + " "));
        }
        return NodeFactory.createObjectFieldNode(
                null,
                AbstractNodeFactory.createIdentifierToken("\n" + visibility + " "),
                qualifierList,
                typeName,
                AbstractNodeFactory.createIdentifierToken(fieldName),
                SyntaxTokenConstants.SYNTAX_TREE_EQUAL, expression,
                SyntaxTokenConstants.SYNTAX_TREE_SEMICOLON
        );
    }
    public static QualifiedNameReferenceNode getQualifiedNameReferenceNode(String modulePrefix, String identifier) {
        return NodeFactory.createQualifiedNameReferenceNode(
                AbstractNodeFactory.createIdentifierToken(modulePrefix),
                SyntaxTokenConstants.SYNTAX_TREE_COLON,
                AbstractNodeFactory.createIdentifierToken(identifier + " ")
        );
    }

    public static BuiltinSimpleNameReferenceNode getBuiltinSimpleNameReferenceNode(String name) {
        SyntaxKind kind;
        switch (name) {
            case "int":
                kind = SyntaxKind.INT_TYPE_DESC;
                break;
            case "float":
                kind = SyntaxKind.DECIMAL_TYPE_DESC;
                break;
            case "boolean":
                kind = SyntaxKind.BOOLEAN_TYPE_DESC;
                break;
            case "byte":
                kind = SyntaxKind.BYTE_TYPE_DESC;
                break;
            case "var":
                kind = SyntaxKind.VAR_TYPE_DESC;
                break;
            case "anydata":
                kind = SyntaxKind.ANYDATA_TYPE_DESC;
                break;

            default:
                kind = SyntaxKind.STRING_TYPE_DESC;
        }
        return NodeFactory.createBuiltinSimpleNameReferenceNode(
                kind,
                AbstractNodeFactory.createIdentifierToken(name + " ")
        );
    }

    public static ArrayTypeDescriptorNode getArrayTypeDescriptorNode(String type) {
        ArrayDimensionNode arrayDimensionNode = NodeFactory.createArrayDimensionNode(
                SyntaxTokenConstants.SYNTAX_TREE_OPEN_BRACKET,
                null,
                SyntaxTokenConstants.SYNTAX_TREE_CLOSE_BRACKET
        );
        NodeList<ArrayDimensionNode> dimensionList = NodeFactory.createNodeList(arrayDimensionNode);
        return NodeFactory.createArrayTypeDescriptorNode(
                getBuiltinSimpleNameReferenceNode(type),
                dimensionList
        );
    }

    public static RecordTypeDescriptorNode getRecordTypeDescriptorNode() {
        return NodeFactory.createRecordTypeDescriptorNode(
                SyntaxTokenConstants.SYNTAX_TREE_KEYWORD_RECORD,
                SyntaxTokenConstants.SYNTAX_TREE_OPEN_BRACE,
                NodeFactory.createEmptyNodeList(),
                null,
                SyntaxTokenConstants.SYNTAX_TREE_CLOSE_BRACE
        );
    }

    public static ObjectFieldNode getObjectFieldNodeWithoutExpression(String visibility, String[] qualifiers,
                                                                      Node typeName, String fieldName) {
        NodeList<Token> qualifierList = NodeFactory.createEmptyNodeList();
        for (String qualifier : qualifiers) {
            qualifierList = qualifierList.add(AbstractNodeFactory.createIdentifierToken(qualifier + " "));
        }
        return NodeFactory.createObjectFieldNode(
                null,
                AbstractNodeFactory.createIdentifierToken("\n" + visibility + " "),
                qualifierList,
                typeName,
                AbstractNodeFactory.createIdentifierToken(fieldName),
                null,
                null,
                SyntaxTokenConstants.SYNTAX_TREE_SEMICOLON
        );
    }

    public static SimpleNameReferenceNode getSimpleNameReferenceNode(String name) {
        return NodeFactory.createSimpleNameReferenceNode(AbstractNodeFactory.createIdentifierToken(name + " "));
    }

    public static UnionTypeDescriptorNode getUnionTypeDescriptorNode(TypeDescriptorNode lhs, TypeDescriptorNode rhs) {
        return NodeFactory.createUnionTypeDescriptorNode(
                lhs,
                SyntaxTokenConstants.SYNTAX_TREE_PIPE,
                rhs
        );
    }

    public static OptionalTypeDescriptorNode getOptionalTypeDescriptorNode(String modulePrefix, String identifier) {
        if (modulePrefix.isEmpty()) {
            return NodeFactory.createOptionalTypeDescriptorNode(
                    getSimpleNameReferenceNode(identifier),
                    SyntaxTokenConstants.SYNTAX_TREE_QUESTION_MARK
            );
        }
        return NodeFactory.createOptionalTypeDescriptorNode(
                NodeFactory.createQualifiedNameReferenceNode(
                        AbstractNodeFactory.createIdentifierToken(modulePrefix),
                        SyntaxTokenConstants.SYNTAX_TREE_COLON,
                        AbstractNodeFactory.createIdentifierToken(identifier)),
                SyntaxTokenConstants.SYNTAX_TREE_QUESTION_MARK
        );
    }

    public static StreamTypeDescriptorNode getStreamTypeDescriptorNode(Node lhs, Node rhs) {
        Token comma;
        if (rhs == null) {
            comma = null;
        } else {
            comma = AbstractNodeFactory.createToken(SyntaxKind.COMMA_TOKEN,
                    AbstractNodeFactory.createEmptyMinutiaeList(),
                    NodeFactory.createMinutiaeList(AbstractNodeFactory.createWhitespaceMinutiae(" ")));
        }
        return NodeFactory.createStreamTypeDescriptorNode(
                AbstractNodeFactory.createToken(SyntaxKind.STREAM_KEYWORD),
                NodeFactory.createStreamTypeParamsNode(
                        AbstractNodeFactory.createToken(SyntaxKind.LT_TOKEN),
                        lhs,
                        comma,
                        rhs,
                        AbstractNodeFactory.createToken(SyntaxKind.GT_TOKEN,
                                AbstractNodeFactory.createEmptyMinutiaeList(),
                                NodeFactory.createMinutiaeList(AbstractNodeFactory.createWhitespaceMinutiae(" ")))
                )
        );
    }

    public static MapTypeDescriptorNode getMapTypeDescriptorNode(TypeDescriptorNode descriptorNode) {
        return NodeFactory.createMapTypeDescriptorNode(
                AbstractNodeFactory.createIdentifierToken("map"),
                NodeFactory.createTypeParameterNode(
                        SyntaxTokenConstants.SYNTAX_TREE_IT,
                        descriptorNode,
                        SyntaxTokenConstants.SYNTAX_TREE_GT
                )
        );
    }
}
