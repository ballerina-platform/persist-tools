/*
 * Copyright (c) 2022, WSO2 LLC. (https://www.wso2.com) All Rights Reserved.
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
package io.ballerina.persist.utils;

import io.ballerina.persist.PersistToolsConstants;
import io.ballerina.persist.objects.BalException;
import io.ballerina.persist.objects.Entity;
import io.ballerina.persist.objects.FieldMetaData;
import io.ballerina.persist.objects.Relation;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Sql script generator.
 *
 * @since 0.1.0
 */
public class SqlScriptGenerationUtils {

    private static final String NEW_LINE = System.lineSeparator();
    private static final String TAB = "\t";
    private static final String EMPTY = "";
    private static final String PRIMARY_KEY_START_SCRIPT = NEW_LINE + TAB + "PRIMARY KEY(";
    private static final String UNIQUE_KEY_START_SCRIPT = NEW_LINE + TAB + "UNIQUE KEY(";
    private static final String UNIQUE = " UNIQUE";
    private static final String ON_DELETE_SYNTAX = " ON DELETE";
    private static final String ON_UPDATE_SYNTAX = " ON UPDATE";
    private static final String RESTRICT = "persist:RESTRICT";
    private static final String CASCADE = "persist:CASCADE";
    private static final String SET_NULL = "persist:SET_NULL";
    private static final String NO_ACTION = "persist:NO_ACTION";
    private static final String RESTRICT_SYNTAX = " RESTRICT";
    private static final String CASCADE_SYNTAX = " CASCADE";
    private static final String NO_ACTION_SYNTAX = " NO ACTION";
    private static final String SET_NULL_SYNTAX = " SET NULL";
    private static final String SET_DEFAULT_SYNTAX = " SET DEFAULT";

    private SqlScriptGenerationUtils(){}

    public static String[] generateSqlScript(ArrayList<Entity> entityArray) throws BalException {
        HashMap<String, List<String>> referenceTables = new HashMap<>();
        HashMap<String, List<String>> tableScripts = new HashMap<>();
        for (Entity entity : entityArray) {
            List<String> tableScript = new ArrayList<>();
            String tableName = entity.getTableName();
            tableScript.add(generateDropTableQuery(tableName));
            tableScript.add(generateCreateTableQuery(tableName, entityArray, entity, referenceTables));
            tableScripts.put(tableName, tableScript);
        }
        return rearrangeScriptsWithReference(tableScripts.keySet(), referenceTables, tableScripts);
    }

    public static void writeScriptFile(String[] sqlScripts, Path filePath) {
        Path path = Paths.get(String.valueOf(filePath), PersistToolsConstants.FILE_NAME);
        StringBuilder sqlScript = new StringBuilder();
        for (String script : sqlScripts) {
            sqlScript.append(script).append(NEW_LINE);
        }
        try {
            Files.deleteIfExists(path);
            Files.createFile(path);
            Files.writeString(path, sqlScript);
        } catch (IOException e) {
            PrintStream errStream = System.err;
            errStream.println("Error while updating the SQL script file (persist_db_push.sql) in the project " +
                    "persist directory: " + e.getMessage());
        }
    }

    private static String generateDropTableQuery(String tableName) {
        return MessageFormat.format("DROP TABLE IF EXISTS {0};", tableName);
    }

    private static String generateCreateTableQuery(String tableName, ArrayList<Entity> entityArray, Entity entity,
                                             HashMap<String, List<String>> referenceTables) {
        String autoIncrementScript = "";
        String startValue = entity.getAutoIncrementStartValue();
        if (!startValue.isEmpty() && Integer.parseInt(startValue) > 1) {
            autoIncrementScript = MessageFormat.format("{1} = {2}", NEW_LINE,
                    PersistToolsConstants.AUTO_INCREMENT_WITH_TAB, startValue);
        }
        return MessageFormat.format("{0}CREATE TABLE {1} ({2}{3}){4};", NEW_LINE, tableName,
                generateFieldsDefinitionSegments(entity.getTableName(), Arrays.asList(entity.getKeys()),
                        entity.getUniqueConstraints(), referenceTables, entity, entityArray),
                NEW_LINE, autoIncrementScript);
    }

    private static String generateFieldsDefinitionSegments(String tableName,
                                                           List<String> primaryKeys,
                                                           List<List<String>> uniqueConstraints,
                                                           HashMap<String, List<String>> referenceTables,
                                                           Entity entity, ArrayList<Entity> entityList) {
        StringBuilder sqlScript = new StringBuilder();
        createColumnsScript(entity, sqlScript);
        if (entity.getRelations().size() > 0) {
            ArrayList<Relation> relations = entity.getRelations();
            for (Relation relation: relations) {
                if (!relation.isChild()) {
                    ArrayList<String> keyColumns = relation.getKeyColumns();
                    ArrayList<String> references = relation.getReferences();
                    String onDelete = relation.getOnDelete();
                    String onUpdate = relation.getOnUpdate();
                    String onDeleteScript = "";
                    String onUpdateScript = "";
                    if (onDelete != null && !onDelete.isEmpty()) {
                        onDeleteScript = ON_DELETE_SYNTAX + getReferenceAction(onDelete);
                    }
                    if (onUpdate != null && !onUpdate.isEmpty()) {
                        onUpdateScript = ON_UPDATE_SYNTAX + getReferenceAction(onUpdate);
                    }
                    createScriptFromGivenReferenceKeys(references, relation, keyColumns, onDeleteScript,
                            onUpdateScript, tableName, sqlScript, referenceTables, entityList);
                }
            }
        }
        sqlScript.append(addPrimaryKeyUniqueKey(primaryKeys, uniqueConstraints));
        return sqlScript.substring(0, sqlScript.length() - 1);
    }

    private static void createColumnsScript(Entity entity, StringBuilder sqlScript) {
        for (FieldMetaData fieldMetaData :entity.getFields()) {
            String sqlType = getType(fieldMetaData.getFieldType());
            assert sqlType != null;
            if (sqlType.equals(PersistToolsConstants.SqlTypes.VARCHAR)) {
                sqlType += "(" + fieldMetaData.getStringLength() + ")";
            }
            String fieldName = removeSingleQuote(fieldMetaData.getFieldName());
            String autoIncrement = EMPTY;
            if (fieldMetaData.isAutoGenerated()) {
                autoIncrement = PersistToolsConstants.AUTO_INCREMENT_WITH_SPACE;
            }
            sqlScript.append(MessageFormat.format("{0}{1}{2} {3}{4}{5},",
                    NEW_LINE, TAB, fieldName, sqlType, " NOT NULL", autoIncrement));
        }
    }

    private static String removeSingleQuote(String fieldName) {
        if (fieldName.startsWith("'")) {
            return fieldName.substring(1);
        }
        return fieldName;
    }

    private static void createScriptFromGivenReferenceKeys(ArrayList<String> references, Relation relation,
                                                          ArrayList<String> keyColumns, String onDeleteScript,
                                                          String onUpdateScript, String tableName,
                                                          StringBuilder sqlScript,
                                                          HashMap<String, List<String>> referenceTables,
                                                          ArrayList<Entity> entityArray) {
        int count = 0;
        String referenceSqlType = null;
        String referenceFieldName = null;
        String refTableName = relation.getRefTable();
        String unique = "";
        for (String reference : references) {
            ArrayList<FieldMetaData> relatedFields = relation.getRelatedFields();
            for (FieldMetaData fieldMetaData : relatedFields) {
                if (fieldMetaData.getFieldName().equals(reference)) {
                    referenceSqlType = getType(fieldMetaData.getFieldType());
                    assert referenceSqlType != null;
                    if (referenceSqlType.equals(PersistToolsConstants.SqlTypes.VARCHAR)) {
                        referenceSqlType += "(" + fieldMetaData.getStringLength() + ")";
                    }
                    referenceFieldName = removeSingleQuote(reference);
                    break;
                }
            }
            String foreignKey;
            if (!keyColumns.isEmpty()) {
                foreignKey = keyColumns.get(count);
            } else {
                assert referenceFieldName != null;
                foreignKey = refTableName.toLowerCase(Locale.ENGLISH) +
                        referenceFieldName.substring(0, 1).toUpperCase(Locale.ENGLISH) +
                        referenceFieldName.substring(1);
                if (references.size() > 1) {
                    foreignKey += "_" + count;
                }
            }
            if (relation.relationType.equals(Relation.RelationType.ONE)) {
                for (Entity entity : entityArray) {
                    if (entity.getEntityName().equals(relation.getRelatedType())) {
                        String[] keys = entity.getKeys();
                        List<List<String>> uniqueConstraints = entity.getUniqueConstraints();
                        if ((keys.length == 1 && eliminateDoubleQuotes(keys[0]).equals(referenceFieldName)) ||
                                (uniqueConstraints.size() == 1 && uniqueConstraints.get(0).size() == 1 &&
                                        eliminateDoubleQuotes(uniqueConstraints.get(0).get(0)).
                                                equals(referenceFieldName))) {
                            unique = UNIQUE;
                            break;
                        }
                    }
                }
            }
            sqlScript.append(MessageFormat.format("{0}{1}{2} {3}{4},", NEW_LINE, TAB, foreignKey,
                    referenceSqlType, unique));
            sqlScript.append(MessageFormat.format("{0}{1}CONSTRAINT FK_{2}_{3}_{4} FOREIGN KEY({5}) " +
                            "REFERENCES {6}({7}){8}{9},", NEW_LINE, TAB, tableName.toUpperCase(Locale.ENGLISH),
                    refTableName.toUpperCase(Locale.ENGLISH), count, foreignKey, refTableName,
                    referenceFieldName, onDeleteScript, onUpdateScript));
            updateReferenceTable(tableName, refTableName, referenceTables);
            count++;
        }
    }

    private static void updateReferenceTable(String tableName, String referenceTableName,
                                             HashMap<String, List<String>> referenceTables) {
        List<String> setOfReferenceTables;
        if (referenceTables.containsKey(tableName)) {
            setOfReferenceTables = referenceTables.get(tableName);
        } else {
            setOfReferenceTables = new ArrayList<>();
        }
        setOfReferenceTables.add(referenceTableName);
        referenceTables.put(tableName, setOfReferenceTables);
    }

    private static StringBuilder addPrimaryKeyUniqueKey(List<String> primaryKeys,
                                                        List<List<String>> uniqueConstraints) {
        StringBuilder primaryAndUniqueKeyScript = createKeysScript(primaryKeys, PRIMARY_KEY_START_SCRIPT);
        for (List<String> uniqueConstraint : uniqueConstraints) {
            primaryAndUniqueKeyScript.append(createKeysScript(uniqueConstraint, UNIQUE_KEY_START_SCRIPT));
        }
        return primaryAndUniqueKeyScript;
    }

    private static StringBuilder createKeysScript(List<String> keys, String prefix) {
        int size = keys.size();
        StringBuilder script;
        if (size == 1) {
            script = new StringBuilder(MessageFormat.format("{0}{1}),", prefix,
                    eliminateDoubleQuotes(keys.get(0))));
        } else {
            script = new StringBuilder(MessageFormat.format("{0}{1},", prefix,
                    eliminateDoubleQuotes(keys.get(0))));
            for (int i = 1; i < size - 2; i++) {
                script.append(MessageFormat.format("{0},",
                        eliminateDoubleQuotes(keys.get(i))));
            }
            script.append(MessageFormat.format("{0}),",
                    eliminateDoubleQuotes(keys.get(size - 1))));
        }
        return script;
    }

    private static String getReferenceAction(String value) {
        switch (value) {
            case RESTRICT:
                return RESTRICT_SYNTAX;
            case CASCADE:
                return CASCADE_SYNTAX;
            case NO_ACTION:
                return NO_ACTION_SYNTAX;
            case SET_NULL:
                return SET_NULL_SYNTAX;
            default:
                return SET_DEFAULT_SYNTAX;
        }
    }

    private static String eliminateDoubleQuotes(String text) {
        return text.substring(1, text.length() - 1);
    }

    private static String getType(String type) {
        switch (type) {
            case PersistToolsConstants.BallerinaTypes.INT:
                return PersistToolsConstants.SqlTypes.INT;
            case PersistToolsConstants.BallerinaTypes.BOOLEAN:
                return PersistToolsConstants.SqlTypes.BOOLEAN;
            case PersistToolsConstants.BallerinaTypes.DECIMAL:
                return PersistToolsConstants.SqlTypes.DECIMAL;
            case PersistToolsConstants.BallerinaTypes.FLOAT:
                return PersistToolsConstants.SqlTypes.FLOAT;
            case PersistToolsConstants.BallerinaTypes.DATE:
                return PersistToolsConstants.SqlTypes.DATE;
            case PersistToolsConstants.BallerinaTypes.TIME_OF_DAY:
                return PersistToolsConstants.SqlTypes.TIME;
            case PersistToolsConstants.BallerinaTypes.UTC:
                return PersistToolsConstants.SqlTypes.TIME_STAMP;
            case PersistToolsConstants.BallerinaTypes.CIVIL:
                return PersistToolsConstants.SqlTypes.DATE_TIME;
            case PersistToolsConstants.BallerinaTypes.STRING:
                return PersistToolsConstants.SqlTypes.VARCHAR;
            default:
                return null;
        }
    }

    private static String[] rearrangeScriptsWithReference(Set<String> tables,
                                                          HashMap<String, List<String>> referenceTables,
                                                          HashMap<String, List<String>> tableScripts) {
        List<String> tableOrder = new ArrayList<>();
        for (String table : referenceTables.keySet()) {
            if (tableOrder.isEmpty()) {
                tableOrder.add(table);
            } else {
                int firstIndex = 0;
                List<String> referenceTableNames = referenceTables.get(table);
                for (String referenceTableName: referenceTableNames) {
                    int index = tableOrder.indexOf(referenceTableName);
                    if ((firstIndex == 0 || index > firstIndex) && index > 0) {
                        firstIndex = index;
                    }
                }
                tableOrder.add(firstIndex, table);
            }
        }
        for (String tableName : tables) {
            if (!tableOrder.contains(tableName)) {
                tableOrder.add(0, tableName);
            }
        }
        int length = tables.size() * 2;
        int size = tableOrder.size();
        String[] tableScriptsInOrder = new String[length];
        for (int i = 0; i <= tableOrder.size() - 1; i++) {
            List<String> script =  tableScripts.get(tableOrder.get(size - (i + 1)));
            tableScriptsInOrder[i] = script.get(0);
            tableScriptsInOrder[length - (i + 1)] = script.get(1);
        }
        return tableScriptsInOrder;
    }
}
