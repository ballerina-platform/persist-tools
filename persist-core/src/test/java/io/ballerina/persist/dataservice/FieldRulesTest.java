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
import io.ballerina.persist.models.Module;
import io.ballerina.persist.utils.BalProjectUtils;
import io.ballerina.tools.text.TextDocuments;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Tests reading and checking the field rules of a persist model.
 */
public class FieldRulesTest {

    private static final String IMPORTS = "import ballerina/dataservice;\nimport ballerina/persist as _;\n" +
            "import ballerinax/persist.sql;\n\n";
    private static final String DEPARTMENT = "\n@dataservice:Expose\ntype Department record {|\n" +
            "    readonly int id;\n    string name;\n    Employee[] employees;\n|};\n";

    @Test
    public void testRulesAndForeignKeyValues() throws BalException, IOException {
        FieldRules.Result result = resolve(IMPORTS + "@dataservice:Expose\ntype Employee record {|\n" +
                "    readonly int id;\n    @dataservice:ReadOnly {value: \"new\"}\n    string state;\n" +
                "    @dataservice:Internal\n    string? notes;\n    @dataservice:Immutable\n" +
                "    Department department;\n|};\n" + DEPARTMENT);
        Assert.assertEquals(result.of("Employee"), Map.of(
                "state", new FieldRules.Applied(FieldRule.READ_ONLY, Optional.of("\"new\"")),
                "notes", new FieldRules.Applied(FieldRule.INTERNAL, Optional.empty()),
                "department", new FieldRules.Applied(FieldRule.IMMUTABLE, Optional.empty()),
                "departmentId", new FieldRules.Applied(FieldRule.IMMUTABLE, Optional.empty())));
        Assert.assertTrue(result.warnings().isEmpty());
    }

    @Test
    public void testCompositeForeignKeyValue() throws BalException, IOException {
        FieldRules.Result result = resolve(IMPORTS + "@dataservice:Expose\ntype Line record {|\n" +
                "    readonly int id;\n    @dataservice:ReadOnly {value: [\"P1\", 2]}\n    Slot slot;\n|};\n\n" +
                "type Slot record {|\n    readonly string code;\n    readonly int seq;\n    Line[] lines;\n|};\n");
        Assert.assertEquals(result.of("Line").get("slotCode"),
                new FieldRules.Applied(FieldRule.READ_ONLY, Optional.of("\"P1\"")));
        Assert.assertEquals(result.of("Line").get("slotSeq"),
                new FieldRules.Applied(FieldRule.READ_ONLY, Optional.of("2")));
    }

    @Test
    public void testNoEffectWarning() throws BalException, IOException {
        FieldRules.Result result = resolve(IMPORTS + "@dataservice:Expose {operations: [dataservice:READ]}\n" +
                "type Item record {|\n    readonly int id;\n    @dataservice:Immutable\n    string code;\n" +
                "    @dataservice:ReadOnly\n    string? label;\n|};\n");
        Assert.assertEquals(result.warnings(), List.of(
                "@dataservice:Immutable on the field 'code' of the entity 'Item' has no effect, because the " +
                        "entity does not allow 'update'.",
                "@dataservice:ReadOnly on the field 'label' of the entity 'Item' has no effect, because the " +
                        "entity does not allow 'create' or 'update'."));
    }

    @Test
    public void testRuleOnRelationWithoutForeignKey() throws IOException {
        String department = DEPARTMENT.replace("    Employee[] employees;",
                "    @dataservice:ReadOnly\n    Employee[] employees;");
        BalException error = Assert.expectThrows(BalException.class, () -> resolve(IMPORTS +
                "@dataservice:Expose\ntype Employee record {|\n    readonly int id;\n    Department department;\n" +
                "|};\n" + department));
        Assert.assertEquals(error.getMessage(), "@dataservice:ReadOnly cannot be used on the relation field " +
                "'employees' of the entity 'Department', which has no foreign key.");
    }

    @Test(dataProvider = "errors")
    public void testError(String fields, String message) throws IOException {
        BalException error = Assert.expectThrows(BalException.class, () -> resolve(IMPORTS +
                "@dataservice:Expose\ntype Employee record {|\n" + fields + "\n|};\n" + DEPARTMENT));
        Assert.assertEquals(error.getMessage(), message);
    }

    @DataProvider
    public Object[][] errors() {
        String relation = "    Department department;";
        return new Object[][]{
                {"    readonly int id;\n    @dataservice:ReadOnly {value: \"x\"}\n    @dataservice:Internal\n" +
                        "    string name;\n" + relation,
                        "the field 'name' of the entity 'Employee' has more than one of @dataservice:ReadOnly, " +
                                "@dataservice:Immutable and @dataservice:Internal."},
                {"    @dataservice:Immutable\n    readonly int id;\n" + relation,
                        "the key field 'id' of the entity 'Employee' cannot have @dataservice:Immutable."},
                {"    @dataservice:ReadOnly\n    readonly int id;\n" + relation,
                        "the key field 'id' of the entity 'Employee' cannot have @dataservice:ReadOnly."},
                {"    readonly int id;\n    @dataservice:Internal\n    string name;\n" + relation,
                        "the field 'name' of the entity 'Employee' needs a 'value' in @dataservice:Internal, " +
                                "because it is neither nullable nor generated."},
                {"    readonly int id;\n    @dataservice:ReadOnly {value: 1}\n    string name;\n" + relation,
                        "the 'value' in @dataservice:ReadOnly of the field 'name' of the entity 'Employee' is " +
                                "not a 'string'."},
                {"    readonly int id;\n    @dataservice:ReadOnly {value: \"x\"}\n" + relation,
                        "the 'value' in @dataservice:ReadOnly of the field 'department' of the entity " +
                                "'Employee' is not a 'int'."},
                {"    readonly int id;\n    @dataservice:ReadOnly {value: LIMIT}\n    int quota;\n" + relation,
                        "the 'value' in @dataservice:ReadOnly of the field 'quota' of the entity 'Employee' is " +
                                "not a 'int'."},
                {"    readonly int id;\n    @dataservice:Immutable\n    int deptId;\n" +
                        "    @sql:Relation {keys: [\"deptId\"]}\n" + relation,
                        "@dataservice:Immutable cannot be used on the foreign key field 'deptId' of the entity " +
                                "'Employee'. Use it on the relation field 'department'."},
        };
    }

    private static FieldRules.Result resolve(String source) throws BalException, IOException {
        Path model = Files.createTempDirectory("rules").resolve("model.bal");
        Files.writeString(model, source);
        Module module = BalProjectUtils.getEntities(model);
        SyntaxTree tree = SyntaxTree.from(TextDocuments.from(source));
        return FieldRules.resolve(tree, module, ExposeAnnotations.read(tree));
    }
}
