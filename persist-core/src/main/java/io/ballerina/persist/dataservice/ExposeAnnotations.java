/*
 * Copyright (c) 2026, WSO2 LLC. (http://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.ballerina.persist.dataservice;

import io.ballerina.compiler.syntax.tree.AnnotationNode;
import io.ballerina.compiler.syntax.tree.BasicLiteralNode;
import io.ballerina.compiler.syntax.tree.EnumDeclarationNode;
import io.ballerina.compiler.syntax.tree.ExpressionNode;
import io.ballerina.compiler.syntax.tree.ImportDeclarationNode;
import io.ballerina.compiler.syntax.tree.ListConstructorExpressionNode;
import io.ballerina.compiler.syntax.tree.MappingFieldNode;
import io.ballerina.compiler.syntax.tree.MetadataNode;
import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;
import io.ballerina.compiler.syntax.tree.ModulePartNode;
import io.ballerina.compiler.syntax.tree.Node;
import io.ballerina.compiler.syntax.tree.QualifiedNameReferenceNode;
import io.ballerina.compiler.syntax.tree.SpecificFieldNode;
import io.ballerina.compiler.syntax.tree.SyntaxKind;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.compiler.syntax.tree.TypeDefinitionNode;
import io.ballerina.persist.BalException;
import io.ballerina.tools.text.TextDocuments;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Reads the `@dataservice:Expose` annotations of a persist model from its syntax tree.
 */
public final class ExposeAnnotations {

    private static final String ORG = "ballerina";
    private static final String MODULE = "dataservice";
    private static final String EXPOSE = "Expose";
    private static final String OPERATIONS = "operations";

    private ExposeAnnotations() {
    }

    /**
     * Reads the annotations of a model file.
     *
     * @param modelPath the model file
     * @return the actions of each annotated entity, in model order
     * @throws BalException if the file cannot be read or an annotation is invalid
     */
    public static Map<String, EnumSet<Operation>> read(Path modelPath) throws BalException {
        return read(parse(modelPath));
    }

    /**
     * Reads the annotations of a model.
     *
     * @param model the syntax tree of the model
     * @return the actions of each annotated entity, in model order
     * @throws BalException if an annotation is invalid, with one line per invalid annotation
     */
    public static Map<String, EnumSet<Operation>> read(SyntaxTree model) throws BalException {
        Map<String, EnumSet<Operation>> exposed = new LinkedHashMap<>();
        List<String> errors = new ArrayList<>();
        ModulePartNode root = model.rootNode();
        Optional<String> prefix = prefix(root);
        if (prefix.isEmpty()) {
            return exposed;
        }
        for (ModuleMemberDeclarationNode member : root.members()) {
            for (AnnotationNode annotation : exposeAnnotations(member, prefix.get())) {
                if (member instanceof TypeDefinitionNode type
                        && type.typeDescriptor().kind() == SyntaxKind.RECORD_TYPE_DESC) {
                    String entity = type.typeName().text().trim();
                    operations(annotation, entity, prefix.get(), errors).ifPresent(ops -> exposed.put(entity, ops));
                } else {
                    errors.add(DataServiceMessages.EXPOSE_PLACEMENT);
                }
            }
        }
        if (!errors.isEmpty()) {
            throw new BalException(String.join(System.lineSeparator(), errors));
        }
        return exposed;
    }

    /**
     * Tells whether a model file has any `@dataservice:Expose` annotation, valid or not.
     *
     * @param modelPath the model file
     * @return whether an annotation is present
     * @throws BalException if the file cannot be read
     */
    public static boolean isPresent(Path modelPath) throws BalException {
        ModulePartNode root = parse(modelPath).rootNode();
        Optional<String> prefix = prefix(root);
        return prefix.isPresent() && root.members().stream()
                .anyMatch(member -> !exposeAnnotations(member, prefix.get()).isEmpty());
    }

    private static SyntaxTree parse(Path modelPath) throws BalException {
        try {
            return SyntaxTree.from(TextDocuments.from(Files.readString(modelPath)));
        } catch (IOException e) {
            throw new BalException("failed to read the model file: " + e.getMessage());
        }
    }

    private static Optional<String> prefix(ModulePartNode root) {
        for (ImportDeclarationNode importNode : root.imports()) {
            if (importNode.orgName().isPresent() && ORG.equals(importNode.orgName().get().orgName().text())
                    && importNode.moduleName().size() == 1
                    && MODULE.equals(importNode.moduleName().get(0).text())) {
                return Optional.of(importNode.prefix().map(prefix -> prefix.prefix().text()).orElse(MODULE));
            }
        }
        return Optional.empty();
    }

    private static List<AnnotationNode> exposeAnnotations(ModuleMemberDeclarationNode member, String prefix) {
        Optional<MetadataNode> metadata = switch (member.kind()) {
            case TYPE_DEFINITION -> ((TypeDefinitionNode) member).metadata();
            case ENUM_DECLARATION -> ((EnumDeclarationNode) member).metadata();
            default -> Optional.empty();
        };
        List<AnnotationNode> annotations = new ArrayList<>();
        metadata.ifPresent(node -> node.annotations().forEach(annotation -> {
            if (annotation.annotReference() instanceof QualifiedNameReferenceNode reference
                    && prefix.equals(reference.modulePrefix().text())
                    && EXPOSE.equals(reference.identifier().text())) {
                annotations.add(annotation);
            }
        }));
        return annotations;
    }

    private static Optional<EnumSet<Operation>> operations(AnnotationNode annotation, String entity, String prefix,
                                                           List<String> errors) {
        Optional<ExpressionNode> value = Optional.empty();
        if (annotation.annotValue().isPresent()) {
            for (MappingFieldNode field : annotation.annotValue().get().fields()) {
                if (field instanceof SpecificFieldNode specific
                        && OPERATIONS.equals(specific.fieldName().toSourceCode().trim())) {
                    value = specific.valueExpr();
                }
            }
        }
        if (value.isEmpty()) {
            return Optional.of(EnumSet.allOf(Operation.class));
        }
        if (!(value.get() instanceof ListConstructorExpressionNode list)) {
            errors.add(String.format(DataServiceMessages.OPERATIONS_NOT_LIST, entity));
            return Optional.empty();
        }
        if (list.expressions().isEmpty()) {
            errors.add(String.format(DataServiceMessages.OPERATIONS_EMPTY, entity));
            return Optional.empty();
        }
        EnumSet<Operation> operations = EnumSet.noneOf(Operation.class);
        int errorCount = errors.size();
        for (Node member : list.expressions()) {
            String text = member.toSourceCode().trim();
            Optional<Operation> operation = operation(member, prefix);
            if (operation.isEmpty()) {
                errors.add(String.format(DataServiceMessages.OPERATION_UNKNOWN, text, entity));
            } else if (!operations.add(operation.get())) {
                errors.add(String.format(DataServiceMessages.OPERATION_DUPLICATE, text, entity));
            }
        }
        return errors.size() == errorCount ? Optional.of(operations) : Optional.empty();
    }

    private static Optional<Operation> operation(Node member, String prefix) {
        String value = null;
        if (member instanceof QualifiedNameReferenceNode reference && prefix.equals(reference.modulePrefix().text())) {
            value = reference.identifier().text().toLowerCase(Locale.ROOT);
            if (!value.toUpperCase(Locale.ROOT).equals(reference.identifier().text())) {
                return Optional.empty();
            }
        } else if (member instanceof BasicLiteralNode literal && literal.kind() == SyntaxKind.STRING_LITERAL) {
            String text = literal.literalToken().text();
            value = text.substring(1, text.length() - 1);
        }
        for (Operation operation : Operation.values()) {
            if (operation.value().equals(value)) {
                return Optional.of(operation);
            }
        }
        return Optional.empty();
    }
}
