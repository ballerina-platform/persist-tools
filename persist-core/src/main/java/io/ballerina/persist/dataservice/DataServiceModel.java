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
import io.ballerina.compiler.syntax.tree.ListConstructorExpressionNode;
import io.ballerina.compiler.syntax.tree.MappingFieldNode;
import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;
import io.ballerina.compiler.syntax.tree.ModulePartNode;
import io.ballerina.compiler.syntax.tree.Node;
import io.ballerina.compiler.syntax.tree.QualifiedNameReferenceNode;
import io.ballerina.compiler.syntax.tree.SpecificFieldNode;
import io.ballerina.compiler.syntax.tree.SyntaxKind;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.compiler.syntax.tree.TypeDefinitionNode;
import io.ballerina.persist.BalException;
import io.ballerina.persist.dataservice.DataServiceOptions.Operation;
import io.ballerina.persist.models.Entity;
import io.ballerina.persist.models.Module;
import io.ballerina.tools.text.TextDocuments;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The entities a data service exposes, resolved from the options and the persist model.
 *
 * @param options  the data service options
 * @param entities the exposed entities, in model order
 * @param warnings the warnings raised while resolving the entities
 */
public record DataServiceModel(DataServiceOptions options, List<ExposedEntity> entities, List<String> warnings) {

    private static final String EXPOSE_ANNOTATION = "dataservice:Expose";

    /**
     * Resolves the exposed entities and their effective actions.
     *
     * @param options   the data service options
     * @param module    the entities of the persist model
     * @param modelPath the path of the persist model file
     * @return the resolved data service model
     * @throws BalException if the options and the model do not agree
     */
    public static DataServiceModel resolve(DataServiceOptions options, Module module, Path modelPath)
            throws BalException {
        Map<String, Set<Operation>> annotated = readExposeAnnotations(modelPath);
        Map<String, Entity> entityMap = module.getEntityMap();
        List<String> warnings = new ArrayList<>();

        for (String name : options.entities()) {
            if (!entityMap.containsKey(name)) {
                throw new BalException(String.format("the entity '%s' in 'options.dataservice.entities' is not " +
                        "defined in the model.", name));
            }
            if (entityMap.get(name).containsUnsupportedTypes()) {
                throw new BalException(String.format("the entity '%s' in 'options.dataservice.entities' " +
                        "contains unsupported data types and cannot be exposed.", name));
            }
        }

        List<ExposedEntity> exposed = new ArrayList<>();
        for (Entity entity : entityMap.values()) {
            String name = entity.getEntityName();
            if (!options.entities().isEmpty() && !options.entities().contains(name)) {
                if (annotated.containsKey(name)) {
                    warnings.add(String.format("the entity '%s' has a @dataservice:Expose annotation but is not " +
                            "in 'options.dataservice.entities', so it is not exposed.", name));
                }
                continue;
            }
            if (entity.containsUnsupportedTypes()) {
                warnings.add(String.format("the entity '%s' contains unsupported data types and is not exposed.",
                        name));
                continue;
            }
            Set<Operation> operations = EnumSet.noneOf(Operation.class);
            operations.addAll(options.operations());
            if (annotated.containsKey(name)) {
                operations.retainAll(annotated.get(name));
            }
            if (operations.isEmpty()) {
                throw new BalException(String.format("the entity '%s' allows no operations: " +
                        "'options.dataservice.operations' is %s and its @dataservice:Expose annotation is %s.",
                        name, format(options.operations()), format(annotated.get(name))));
            }
            exposed.add(new ExposedEntity(entity, Collections.unmodifiableSet(operations)));
        }
        if (exposed.isEmpty()) {
            throw new BalException("the data service does not expose any entity.");
        }
        return new DataServiceModel(options, Collections.unmodifiableList(exposed),
                Collections.unmodifiableList(warnings));
    }

    private static Map<String, Set<Operation>> readExposeAnnotations(Path modelPath) throws BalException {
        String source;
        try {
            source = Files.readString(modelPath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new BalException("failed to read the model file: " + e.getMessage());
        }
        ModulePartNode root = SyntaxTree.from(TextDocuments.from(source)).rootNode();
        Map<String, Set<Operation>> annotated = new HashMap<>();
        for (ModuleMemberDeclarationNode member : root.members()) {
            if (!(member instanceof TypeDefinitionNode typeDefinition) || typeDefinition.metadata().isEmpty()) {
                continue;
            }
            String entityName = typeDefinition.typeName().text().trim();
            for (AnnotationNode annotation : typeDefinition.metadata().get().annotations()) {
                if (EXPOSE_ANNOTATION.equals(annotation.annotReference().toSourceCode().trim())) {
                    annotated.put(entityName, readOperations(entityName, annotation));
                }
            }
        }
        return annotated;
    }

    private static Set<Operation> readOperations(String entityName, AnnotationNode annotation)
            throws BalException {
        if (annotation.annotValue().isPresent()) {
            for (MappingFieldNode field : annotation.annotValue().get().fields()) {
                if (field instanceof SpecificFieldNode specificField
                        && DataServiceOptions.OPERATIONS.equals(specificField.fieldName().toSourceCode().trim())
                        && specificField.valueExpr().isPresent()
                        && specificField.valueExpr().get() instanceof ListConstructorExpressionNode list) {
                    Set<Operation> operations = EnumSet.noneOf(Operation.class);
                    for (Node item : list.expressions()) {
                        operations.add(readOperation(entityName, item));
                    }
                    return operations;
                }
            }
        }
        throw new BalException(String.format("the @dataservice:Expose annotation of the entity '%s' must set " +
                "'operations' to a list of operations.", entityName));
    }

    // Enum member names are the upper-case form of their values, so `READ` and `"read"` are the same operation.
    private static Operation readOperation(String entityName, Node item) throws BalException {
        String value = null;
        if (item instanceof QualifiedNameReferenceNode reference) {
            value = reference.identifier().text().trim().toLowerCase(Locale.ROOT);
        } else if (item instanceof BasicLiteralNode literal && literal.kind() == SyntaxKind.STRING_LITERAL) {
            String text = literal.literalToken().text().trim();
            value = text.substring(1, text.length() - 1);
        }
        Operation operation = value == null ? null : Operation.fromValue(value);
        if (operation == null) {
            throw new BalException(String.format("invalid operation '%s' in the @dataservice:Expose annotation of " +
                    "the entity '%s'.", item.toSourceCode().trim(), entityName));
        }
        return operation;
    }

    private static String format(Set<Operation> operations) {
        return operations.stream().map(Operation::value).collect(Collectors.joining(", ", "[", "]"));
    }
}
