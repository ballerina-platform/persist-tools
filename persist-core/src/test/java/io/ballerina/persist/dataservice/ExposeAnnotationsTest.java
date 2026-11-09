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

import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.tools.text.TextDocuments;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.ballerina.persist.dataservice.Operation.CREATE;
import static io.ballerina.persist.dataservice.Operation.DELETE;
import static io.ballerina.persist.dataservice.Operation.READ;
import static io.ballerina.persist.dataservice.Operation.UPDATE;

/**
 * Tests reading `@dataservice:Expose` annotations from a model's syntax tree.
 */
public class ExposeAnnotationsTest {

    private static final String IMPORTS = "import ballerina/dataservice;\nimport ballerina/persist as _;\n\n";

    @Test
    public void testBareAnnotationAllowsEveryAction() {
        Assert.assertEquals(read("@dataservice:Expose\ntype Employee record {| readonly int id; |};"),
                Map.of("Employee", EnumSet.allOf(Operation.class)));
    }

    @Test
    public void testAnnotationWithoutOperations() {
        Assert.assertEquals(read("@dataservice:Expose {}\ntype Employee record {| readonly int id; |};"),
                Map.of("Employee", EnumSet.allOf(Operation.class)));
    }

    @Test
    public void testMembersAndStringValues() {
        Map<String, EnumSet<Operation>> exposed = read(
                "@dataservice:Expose {operations: [dataservice:UPDATE, \"read\"]}\n" +
                "type Employee record {| readonly int id; |};\n" +
                "type AuditLog record {| readonly int id; |};\n" +
                "@dataservice:Expose {operations: [\"delete\", dataservice:CREATE]}\n" +
                "type Department record {| readonly int id; |};");
        Assert.assertEquals(exposed, Map.of("Employee", EnumSet.of(READ, UPDATE),
                "Department", EnumSet.of(CREATE, DELETE)));
        Assert.assertEquals(exposed.keySet().stream().toList(), List.of("Employee", "Department"));
    }

    @Test
    public void testImportPrefix() {
        SyntaxTree tree = tree("import ballerina/dataservice as ds;\nimport ballerina/persist as _;\n\n" +
                "@ds:Expose {operations: [ds:READ]}\ntype Employee record {| readonly int id; |};");
        Assert.assertEquals(ExposeAnnotations.read(tree), Map.of("Employee", EnumSet.of(READ)));
    }

    @Test
    public void testNoImport() {
        SyntaxTree tree = tree("import ballerina/persist as _;\n\ntype Employee record {| readonly int id; |};");
        Assert.assertEquals(ExposeAnnotations.read(tree), Map.of());
    }









    @Test
    public void testScopes() {
        String model = IMPORTS + "@dataservice:Expose {operations: [dataservice:READ, dataservice:DELETE], " +
                "scopes: {read: [], delete: [\"hr:admin\", \"hr:owner\"]}}\n" +
                "type Employee record {| readonly int id; |};\n" +
                "@dataservice:Expose\ntype Department record {| readonly int id; |};";
        Assert.assertEquals(read(model.substring(IMPORTS.length())).keySet(), Set.of("Employee", "Department"));
        Assert.assertEquals(ExposeAnnotations.scopes(tree(model)), Map.of(
                "Employee", Map.of(READ, List.of(), DELETE, List.of("hr:admin", "hr:owner")),
                "Department", Map.of()));
    }

    @Test
    public void testQuotedScopeKeys() {
        String model = IMPORTS + "@dataservice:Expose {\"scopes\": {\"read\": [\"hr:read\"], " +
                "'delete: [\"hr:admin\"]}}\ntype Employee record {| readonly int id; |};";
        Assert.assertEquals(read(model.substring(IMPORTS.length())).keySet(), Set.of("Employee"));
        Assert.assertEquals(ExposeAnnotations.scopes(tree(model)), Map.of(
                "Employee", Map.of(READ, List.of("hr:read"), DELETE, List.of("hr:admin"))));
    }


    private static SyntaxTree tree(String source) {
        return SyntaxTree.from(TextDocuments.from(source));
    }

    private static Map<String, EnumSet<Operation>> read(String members) {
        return ExposeAnnotations.read(tree(IMPORTS + members));
    }

    @Test
    public void testDocs() {
        SyntaxTree model = SyntaxTree.from(TextDocuments.from(IMPORTS +
                "# An employee.\n#\n# Paid monthly.\n#\n# + id - The key\n@dataservice:Expose\n" +
                "type Employee record {| readonly int id; |};\n" +
                "type AuditLog record {| readonly int id; |};\n"));
        Assert.assertEquals(ExposeAnnotations.docs(model),
                Map.of("Employee", List.of("An employee.", "", "Paid monthly.")));
    }
}
