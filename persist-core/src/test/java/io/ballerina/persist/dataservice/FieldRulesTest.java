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
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
    private static final String ONE_TO_ONE = "@dataservice:Expose\ntype Profile record {|\n    readonly int id;\n" +
            "    @dataservice:ReadOnly {value: 1}\n    int employeeId;\n" +
            "    @sql:Relation {keys: [\"employeeId\"]}\n    Employee employee;\n|};\n\n" +
            "@dataservice:Expose\ntype Employee record {|\n    readonly int id;\n    Profile? profile;\n|};\n";

    @Test
    public void testRulesAndForeignKeyValues() throws BalException, IOException {
        FieldRules.Result result = resolve(IMPORTS + "@dataservice:Expose\ntype Employee record {|\n" +
                "    readonly int id;\n    @dataservice:ReadOnly {value: \"new\"}\n    string state;\n" +
                "    @dataservice:Internal\n    string? notes;\n    @dataservice:Immutable\n    int deptId;\n" +
                "    @sql:Relation {keys: [\"deptId\"]}\n    @dataservice:Internal\n    Department department;\n|};\n" +
                DEPARTMENT);
        Assert.assertEquals(result.of("Employee"), Map.of(
                "state", new FieldRules.Applied(FieldRule.READ_ONLY, Optional.of("\"new\"")),
                "notes", new FieldRules.Applied(FieldRule.INTERNAL, Optional.empty()),
                "deptId", new FieldRules.Applied(FieldRule.IMMUTABLE, Optional.empty()),
                "department", new FieldRules.Applied(FieldRule.INTERNAL, Optional.empty())));
    }

    @Test
    public void testCompositeForeignKeyValue() throws BalException, IOException {
        FieldRules.Result result = resolve(IMPORTS + "@dataservice:Expose\ntype Line record {|\n" +
                "    readonly int id;\n    @dataservice:ReadOnly {value: \"P1\"}\n    string slotCode;\n" +
                "    @dataservice:ReadOnly {value: 2}\n    int slotSeq;\n" +
                "    @sql:Relation {keys: [\"slotCode\", \"slotSeq\"]}\n    Slot slot;\n|};\n\n" +
                "type Slot record {|\n    readonly string code;\n    readonly int seq;\n    Line[] lines;\n|};\n");
        Assert.assertEquals(result.of("Line").get("slotCode"),
                new FieldRules.Applied(FieldRule.READ_ONLY, Optional.of("\"P1\"")));
        Assert.assertEquals(result.of("Line").get("slotSeq"),
                new FieldRules.Applied(FieldRule.READ_ONLY, Optional.of("2")));
    }


    @Test
    public void testOneToOneForeignKeyWithoutCreate() throws BalException, IOException {
        FieldRules.Result result = resolve(IMPORTS + ONE_TO_ONE.replace("@dataservice:Expose\ntype Profile",
                "@dataservice:Expose {operations: [dataservice:READ, dataservice:UPDATE]}\ntype Profile"));
        Assert.assertEquals(result.of("Profile").get("employeeId"),
                new FieldRules.Applied(FieldRule.READ_ONLY, Optional.of("1")));
    }







    private static FieldRules.Result resolve(String source) throws BalException, IOException {
        Path model = Files.createTempDirectory("rules").resolve("model.bal");
        Files.writeString(model, source);
        Module module = BalProjectUtils.getEntities(model);
        SyntaxTree tree = SyntaxTree.from(TextDocuments.from(source));
        return FieldRules.resolve(tree, module);
    }
}
