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
import io.ballerina.persist.BalException;
import io.ballerina.tools.text.TextDocuments;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import static io.ballerina.persist.dataservice.Operation.CREATE;
import static io.ballerina.persist.dataservice.Operation.DELETE;
import static io.ballerina.persist.dataservice.Operation.READ;
import static io.ballerina.persist.dataservice.Operation.UPDATE;

/**
 * Tests reading and validating `@dataservice:Expose` annotations from a model's syntax tree.
 */
public class ExposeAnnotationsTest {

    private static final String IMPORTS = "import ballerina/dataservice;\nimport ballerina/persist as _;\n\n";

    @Test
    public void testBareAnnotationAllowsEveryAction() throws BalException {
        Assert.assertEquals(read("@dataservice:Expose\ntype Employee record {| readonly int id; |};"),
                Map.of("Employee", EnumSet.allOf(Operation.class)));
    }

    @Test
    public void testAnnotationWithoutOperations() throws BalException {
        Assert.assertEquals(read("@dataservice:Expose {}\ntype Employee record {| readonly int id; |};"),
                Map.of("Employee", EnumSet.allOf(Operation.class)));
    }

    @Test
    public void testMembersAndStringValues() throws BalException {
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
    public void testImportPrefix() throws BalException {
        SyntaxTree tree = tree("import ballerina/dataservice as ds;\nimport ballerina/persist as _;\n\n" +
                "@ds:Expose {operations: [ds:READ]}\ntype Employee record {| readonly int id; |};");
        Assert.assertEquals(ExposeAnnotations.read(tree), Map.of("Employee", EnumSet.of(READ)));
    }

    @Test
    public void testNoImport() throws BalException {
        SyntaxTree tree = tree("import ballerina/persist as _;\n\ntype Employee record {| readonly int id; |};");
        Assert.assertEquals(ExposeAnnotations.read(tree), Map.of());
    }

    @Test
    public void testEmptyOperations() {
        assertError("@dataservice:Expose {operations: []}\ntype Employee record {| readonly int id; |};",
                "'operations' in the @dataservice:Expose annotation of the entity 'Employee' cannot be empty.");
    }

    @Test
    public void testOperationsNotAList() {
        assertError("@dataservice:Expose {operations: READ_ONLY}\ntype Employee record {| readonly int id; |};",
                "'operations' in the @dataservice:Expose annotation of the entity 'Employee' must be a list of " +
                        "operations.");
    }

    @Test
    public void testUnknownOperation() {
        assertError("@dataservice:Expose {operations: [dataservice:READ, \"write\"]}\n" +
                        "type Employee record {| readonly int id; |};",
                "invalid operation '\"write\"' in the @dataservice:Expose annotation of the entity 'Employee'. " +
                        "Supported values: dataservice:READ, dataservice:CREATE, dataservice:UPDATE, " +
                        "dataservice:DELETE.");
    }

    @Test
    public void testUnknownMember() {
        assertError("@dataservice:Expose {operations: [dataservice:EXECUTE]}\n" +
                        "type Employee record {| readonly int id; |};",
                "invalid operation 'dataservice:EXECUTE' in the @dataservice:Expose annotation of the entity " +
                        "'Employee'. Supported values: dataservice:READ, dataservice:CREATE, dataservice:UPDATE, " +
                        "dataservice:DELETE.");
    }

    @Test
    public void testDuplicateOperation() {
        assertError("@dataservice:Expose {operations: [dataservice:READ, dataservice:DELETE, \"read\"]}\n" +
                        "type Employee record {| readonly int id; |};",
                "duplicate operation '\"read\"' in the @dataservice:Expose annotation of the entity 'Employee'.");
    }

    @Test
    public void testNonRecordType() {
        assertError("@dataservice:Expose\ntype Code string;",
                "the @dataservice:Expose annotation is allowed only on a record type in the persist model.");
    }

    @Test
    public void testEnum() {
        assertError("@dataservice:Expose\nenum Status { ACTIVE, INACTIVE }",
                "the @dataservice:Expose annotation is allowed only on a record type in the persist model.");
    }

    @Test
    public void testEveryErrorIsReported() {
        assertError("@dataservice:Expose {operations: []}\ntype Employee record {| readonly int id; |};\n" +
                        "@dataservice:Expose {operations: [\"read\", \"read\"]}\n" +
                        "type Department record {| readonly int id; |};",
                "'operations' in the @dataservice:Expose annotation of the entity 'Employee' cannot be empty." +
                        System.lineSeparator() + "duplicate operation '\"read\"' in the @dataservice:Expose " +
                        "annotation of the entity 'Department'.");
    }

    private static SyntaxTree tree(String source) {
        return SyntaxTree.from(TextDocuments.from(source));
    }

    private static Map<String, EnumSet<Operation>> read(String members) throws BalException {
        return ExposeAnnotations.read(tree(IMPORTS + members));
    }

    private static void assertError(String members, String message) {
        BalException error = Assert.expectThrows(BalException.class, () -> read(members));
        Assert.assertEquals(error.getMessage(), message);
    }
}
