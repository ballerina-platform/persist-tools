/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com) All Rights Reserved.
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.ballerina.persist.nodegenerator.syntax.utils;

import io.ballerina.persist.BalException;
import io.ballerina.persist.models.Entity;
import io.ballerina.persist.models.EntityField;
import io.ballerina.persist.models.Relation;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;

import static io.ballerina.persist.nodegenerator.syntax.constants.ScriptConstants.APPEND_ROW_TO_SHEET;
import static io.ballerina.persist.nodegenerator.syntax.constants.ScriptConstants.CHECK_IF_SHEET_EXIST;
import static io.ballerina.persist.nodegenerator.syntax.constants.ScriptConstants.COMMA_SPACE;
import static io.ballerina.persist.nodegenerator.syntax.constants.ScriptConstants.DELETE_SHEET;
import static io.ballerina.persist.nodegenerator.syntax.constants.ScriptConstants.DOUBLE_QUOTE;
import static io.ballerina.persist.nodegenerator.syntax.constants.ScriptConstants.FUNCTION_CLOSE;
import static io.ballerina.persist.nodegenerator.syntax.constants.ScriptConstants.FUNCTION_HEADER;
import static io.ballerina.persist.nodegenerator.syntax.constants.ScriptConstants.GET_ACTIVE_SPREADSHEET;
import static io.ballerina.persist.nodegenerator.syntax.constants.ScriptConstants.GET_BY_SHEET_NAME;
import static io.ballerina.persist.nodegenerator.syntax.constants.ScriptConstants.INSERT_SHEET;
import static io.ballerina.persist.nodegenerator.syntax.constants.ScriptConstants.SET_SHEET_NAME;
import static io.ballerina.persist.nodegenerator.syntax.constants.ScriptConstants.VAR;

/**
 * google AppScript script generator.
 *
 * @since 1.0.0
 */

public class AppScriptUtils {
    private static final String NEW_LINE = System.lineSeparator();
    private static final String TAB = "\t";

    private AppScriptUtils() {}

    public static String generateJavaScriptFile(Collection<Entity> entities) throws BalException {
        boolean initialized = false;
        StringBuilder tableScript = new StringBuilder();
        tableScript.append(FUNCTION_HEADER).append(NEW_LINE);
        for (Entity entity : entities) {
            String tableName = removeSingleQuote(entity.getEntityName());
            if (!initialized) {
                initialized = true;
                tableScript.append(TAB).append(generateGetActiveScript()).append(NEW_LINE);
                tableScript.append(TAB).append(VAR).append(generateGetSheetByName(tableName)).append(NEW_LINE);
            } else {
                tableScript.append(TAB).append(generateGetSheetByName(tableName)).append(NEW_LINE);
            }
            tableScript.append(generateIfExistDelete()).append(NEW_LINE);
            tableScript.append(TAB).append(generateInsertSheet()).append(NEW_LINE);
            tableScript.append(TAB).append(generateSetName(tableName)).append(NEW_LINE);
            tableScript.append(TAB).append(generateAppendRow(entity.getFields())).append(NEW_LINE);
            tableScript.append(NEW_LINE);
        }
        tableScript.append(TAB).append(generateGetSheetByName("Sheet1")).append(NEW_LINE);
        tableScript.append(generateIfExistDelete()).append(NEW_LINE);
        tableScript.append(FUNCTION_CLOSE).append(NEW_LINE);
        return tableScript.toString();
    }

    private static String removeSingleQuote(String fieldName) {
        if (fieldName.startsWith("'")) {
            return fieldName.substring(1);
        }
        return fieldName;
    }

    private static String generateGetActiveScript() {
        return GET_ACTIVE_SPREADSHEET;
    }

    private static String generateGetSheetByName(String tableName) {
        return MessageFormat.format(GET_BY_SHEET_NAME, tableName);
    }

    private static String generateIfExistDelete() {
        StringBuilder script = new StringBuilder();
        script.append(CHECK_IF_SHEET_EXIST).append(NEW_LINE);
        script.append(TAB).append(TAB).append(DELETE_SHEET).append(NEW_LINE);
        script.append(TAB).append(FUNCTION_CLOSE);
        return script.toString();
    }

    private static String generateInsertSheet() {
        return INSERT_SHEET;
    }

    private static String generateSetName(String tableName) {
        return MessageFormat.format(SET_SHEET_NAME, tableName);
    }

    private static String generateAppendRow(List<EntityField> fields) {
        StringBuilder columns = new StringBuilder();
        for (EntityField field : fields) {
            if (field.getRelation() == null) {
                if (columns.length() != 0) {
                    columns.append(COMMA_SPACE);
                }
                columns.append(DOUBLE_QUOTE);
                columns.append(field.getFieldName());
                columns.append(DOUBLE_QUOTE);
            } else {
                if (field.getRelation().isOwner()) {
                    if (columns.length() != 0) {
                        columns.append(COMMA_SPACE);
                    }
                    columns.append(DOUBLE_QUOTE);
                    for (Relation.Key keyColumn : field.getRelation().getKeyColumns()) {
                        columns.append(keyColumn.getField());
                    }
                    columns.append(DOUBLE_QUOTE);
                }
            }
        }
        return MessageFormat.format(APPEND_ROW_TO_SHEET, columns);
    }
}
