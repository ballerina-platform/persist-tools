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

import io.ballerina.compiler.syntax.tree.AbstractNodeFactory;
import io.ballerina.compiler.syntax.tree.AnnotationNode;
import io.ballerina.compiler.syntax.tree.ImportDeclarationNode;
import io.ballerina.compiler.syntax.tree.MetadataNode;
import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;
import io.ballerina.compiler.syntax.tree.ModulePartNode;
import io.ballerina.compiler.syntax.tree.NodeFactory;
import io.ballerina.compiler.syntax.tree.NodeList;
import io.ballerina.compiler.syntax.tree.NodeParser;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.compiler.syntax.tree.TypeDefinitionNode;
import io.ballerina.persist.BalException;
import io.ballerina.persist.models.Entity;
import io.ballerina.persist.models.Module;
import io.ballerina.tools.text.TextDocuments;
import org.ballerinalang.formatter.core.Formatter;
import org.ballerinalang.formatter.core.FormatterException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Adds {@code @dataservice:Expose} annotations to a persist model that {@code bal persist pull} generates.
 */
public final class ExposeAnnotations {

    private static final String IMPORT = "import ballerina/dataservice;";
    private static final String ANNOTATED_TYPE = "@dataservice:Expose type T record {||};";

    private ExposeAnnotations() {
    }

    /**
     * Selects the pulled entities to expose.
     *
     * @param module the pulled entities
     * @param tables the value of {@code --expose-tables}, or {@code null} to expose every entity with supported types
     * @return the entities to expose, in model order
     * @throws BalException if a table is not pulled or its entity contains unsupported data types
     */
    public static List<Entity> select(Module module, String tables) throws BalException {
        Collection<Entity> entities = module.getEntityMap().values();
        if (tables == null) {
            return entities.stream().filter(entity -> !entity.containsUnsupportedTypes()).toList();
        }
        Map<String, Entity> byTable = new LinkedHashMap<>();
        for (Entity entity : entities) {
            byTable.put(tableName(entity), entity);
        }
        Set<String> names = parseTables(tables);
        for (String name : names) {
            Entity entity = byTable.get(name);
            if (entity == null) {
                throw new BalException(String.format("the table '%s' in the '--expose-tables' option is not " +
                        "pulled. Pulled tables: %s.", name, String.join(", ", byTable.keySet())));
            }
            if (entity.containsUnsupportedTypes()) {
                throw new BalException(String.format("the table '%s' in the '--expose-tables' option contains " +
                        "unsupported data types and cannot be exposed.", name));
            }
        }
        return entities.stream().filter(entity -> names.contains(tableName(entity))).toList();
    }

    /**
     * Returns the table name of a pulled entity.
     *
     * @param entity the pulled entity
     * @return the table name, which is the entity name when the entity has no table mapping
     */
    public static String tableName(Entity entity) {
        String tableName = entity.getTableName();
        return tableName == null || tableName.isBlank() ? entity.getEntityName() : tableName;
    }

    /**
     * Adds the {@code ballerina/dataservice} import and an {@code @dataservice:Expose} annotation on each of the given
     * entities to a persist model.
     *
     * @param source   the source of the persist model
     * @param entities the names of the entities to annotate
     * @return the formatted source of the annotated model
     * @throws BalException if the model cannot be formatted
     */
    public static String annotate(String source, Collection<String> entities) throws BalException {
        SyntaxTree tree = SyntaxTree.from(TextDocuments.from(source));
        ModulePartNode root = tree.rootNode();
        AnnotationNode annotation = exposeAnnotation();
        List<ModuleMemberDeclarationNode> members = new ArrayList<>();
        for (ModuleMemberDeclarationNode member : root.members()) {
            if (member instanceof TypeDefinitionNode type && entities.contains(type.typeName().text().trim())) {
                member = type.modify().withMetadata(withAnnotation(type, annotation)).apply();
            }
            members.add(member);
        }
        NodeList<ImportDeclarationNode> imports = root.imports().add(NodeParser.parseImportDeclaration(IMPORT));
        ModulePartNode annotated = root.modify().withImports(imports)
                .withMembers(AbstractNodeFactory.createNodeList(members)).apply();
        try {
            return Formatter.format(tree.modifyWith(annotated).toSourceCode());
        } catch (FormatterException e) {
            throw new BalException("failed to format the persist model: " + e.getMessage());
        }
    }

    private static Set<String> parseTables(String tables) throws BalException {
        List<String> names = Arrays.stream(tables.split(",", -1)).map(String::trim).toList();
        if (names.stream().anyMatch(String::isEmpty)) {
            throw new BalException("the '--expose-tables' option must be a comma-separated list of table names.");
        }
        return names.stream().collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private static AnnotationNode exposeAnnotation() {
        ModulePartNode parsed = SyntaxTree.from(TextDocuments.from(ANNOTATED_TYPE)).rootNode();
        TypeDefinitionNode type = (TypeDefinitionNode) parsed.members().get(0);
        return type.metadata().orElseThrow().annotations().get(0);
    }

    // The annotation goes first, ahead of any persist.sql annotations the pulled entity has.
    private static MetadataNode withAnnotation(TypeDefinitionNode type, AnnotationNode annotation) {
        if (type.metadata().isEmpty()) {
            return NodeFactory.createMetadataNode(null, AbstractNodeFactory.createNodeList(annotation));
        }
        MetadataNode metadata = type.metadata().get();
        List<AnnotationNode> annotations = new ArrayList<>();
        annotations.add(annotation);
        metadata.annotations().forEach(annotations::add);
        return metadata.modify().withAnnotations(AbstractNodeFactory.createNodeList(annotations)).apply();
    }
}
