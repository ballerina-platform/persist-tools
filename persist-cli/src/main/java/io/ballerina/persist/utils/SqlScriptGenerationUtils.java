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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

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

    public static void generateSqlScript(ArrayList<Entity> entityArray, Path absoluteSourcePath) throws BalException {
        HashMap<String, List<String>> referenceTables = new HashMap<>();
        List<String> tableNamesInScript = new ArrayList<>();
        generateSqlScript(entityArray, referenceTables, absoluteSourcePath, tableNamesInScript);
    }

    public static void generateSqlScript(ArrayList<Entity> entityArray,
                                         HashMap<String, List<String>> referenceTables, Path filePath,
                                         List<String> tableNamesInScript) throws BalException {
        try {
            Files.deleteIfExists(Paths.get(String.valueOf(filePath), PersistToolsConstants.FILE_NAME));
        } catch (IOException e) {
            throw new BalException("Error while reading the SQL script file (persist_db_push.sql) " +
                    "generated in the project target directory. ");
        }
        for (Entity entity : entityArray) {
            String tableName = entity.getTableName();
            String sqlScript = generateDropTableQuery(tableName) + generateTableQuery(tableName, entityArray, entity,
                    referenceTables);
            createSqlFile(sqlScript, entity.getTableName(), referenceTables, tableNamesInScript, filePath);
        }
    }

    private static String generateDropTableQuery(String tableName) {
        return MessageFormat.format("DROP TABLE IF EXISTS {0};", tableName);
    }

    private static String generateTableQuery(String tableName, ArrayList<Entity> entityArray, Entity entity,
                                             HashMap<String, List<String>> referenceTables) {
        return MessageFormat.format("{1}CREATE TABLE {0} (", tableName, NEW_LINE) +
                generateFieldsQuery(entity.getTableName(), Arrays.asList(entity.getKeys()),
                        entity.getUniqueConstraints(), referenceTables, entity, entityArray);
    }

    private static String generateFieldsQuery(String tableName,
                                              List<String> primaryKeys, List<List<String>> uniqueConstraints,
                                              HashMap<String, List<String>> referenceTables,
                                              Entity entity, ArrayList<Entity> entityArray) {
        StringBuilder sqlScript = new StringBuilder();
        String end = NEW_LINE + ");";
        end = createColumnsScript(entity, end, sqlScript);
        int count = 0;
        if (entity.getRelations().size() > 0) {
            ArrayList<Relation> relations = entity.getRelations();
            for (Relation relation: relations) {
                String uniqueKeyword = "";
                if (!relation.isChild()) {
                    ArrayList<String> keyColumns = relation.getKeyColumns();
                    ArrayList<String> references = relation.getReferences();
                    String onDelete = relation.getOnDelete();
                    String onUpdate = relation.getOnUpdate();
                    String onDeleteScript = "";
                    String onUpdateScript = "";
                    String referenceFieldName = "";
                    if (onDelete != null && !onDelete.isEmpty()) {
                        onDeleteScript = ON_DELETE_SYNTAX + getReferenceAction(onDelete);
                    }
                    if (onUpdate != null && !onUpdate.isEmpty()) {
                        onUpdateScript = ON_UPDATE_SYNTAX + getReferenceAction(onUpdate);
                    }
                    String referenceSqlType = "";
                    String foreignKey = null;
                    if (!references.isEmpty()) {
                        createScriptFromGivenReferenceKeys(references, relation, keyColumns, onDeleteScript,
                                onUpdateScript, tableName, sqlScript, referenceTables, entityArray);
                    } else {
                        String refTableName = null;
                        if (keyColumns.size() == 1) {
                            refTableName = relation.getRefTable();
                            for (Entity entityRecord : entityArray) {
                                if (entityRecord.getEntityName().equals(refTableName)) {
                                    String[] refPrimaryKeys = entityRecord.getKeys();
                                    if (refPrimaryKeys.length == 1) {
                                        referenceFieldName = removeSingleQuote(refPrimaryKeys[0]);
                                        break;
                                    } else {
                                        uniqueConstraints = entityRecord.getUniqueConstraints();
                                        if (uniqueConstraints.size() == 1 && uniqueConstraints.get(0).size() == 1) {
                                            referenceFieldName = removeSingleQuote(uniqueConstraints.get(0).get(0));
                                            break;
                                        }
                                    }
                                }
                            }
                        } else if (keyColumns.isEmpty()) {
                            refTableName = relation.getRefTable();
                            for (Entity entityRecord : entityArray) {
                                if (entityRecord.getEntityName().equals(refTableName)) {
                                    String[] refPrimaryKeys = entityRecord.getKeys();
                                    if (refPrimaryKeys.length == 1) {
                                        referenceFieldName = removeSingleQuote(refPrimaryKeys[0]);
                                        break;
                                    } else {
                                        uniqueConstraints = entityRecord.getUniqueConstraints();
                                        if (uniqueConstraints.size() == 1 && uniqueConstraints.get(0).size() == 1) {
                                            referenceFieldName = removeSingleQuote(uniqueConstraints.get(0).get(0));
                                            break;
                                        }
                                    }
                                }
                            }
                            foreignKey = refTableName.toLowerCase(Locale.ENGLISH) +
                                    referenceFieldName.substring(0, 1).toUpperCase(Locale.ENGLISH) +
                                    referenceFieldName.substring(1);
                        }
                        for (FieldMetaData fieldMetaData: relation.getRelatedFields()) {
                            if (fieldMetaData.getFieldName().equals(referenceFieldName)) {
                                referenceSqlType = fieldMetaData.getFieldType();
                            }

                        }
                        if (relation.relationType.equals(Relation.RelationType.ONE)) {
                            uniqueKeyword = UNIQUE;
                        }
                        assert refTableName != null;
                        sqlScript.append(MessageFormat.format("{10}{11}{0} {1}{12},{10}{11}CONSTRAINT " +
                                        "FK_{2}_{3}_{4} FOREIGN KEY({5}) REFERENCES {6}({7}){8}{9},",
                                foreignKey, referenceSqlType, tableName.toUpperCase(Locale.ENGLISH),
                                refTableName.toUpperCase(Locale.ENGLISH), count, foreignKey,
                                refTableName, referenceFieldName, onDeleteScript, onUpdateScript, NEW_LINE, TAB,
                                uniqueKeyword));
                        updateReferenceTable(tableName, refTableName, referenceTables);
                    }
                }
            }
        }
        sqlScript.append(addPrimaryKeyUniqueKey(primaryKeys, uniqueConstraints));
        return sqlScript.substring(0, sqlScript.length() - 1) + end;
    }

    private static String createColumnsScript(Entity entity, String end, StringBuilder sqlScript) {
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
                String startValue = fieldMetaData.getStartValueOfAutoIncrement();
                if (!startValue.isEmpty() && Integer.parseInt(startValue) > 1) {
                    end = MessageFormat.format("{0}){1} = {2};", NEW_LINE,
                            PersistToolsConstants.AUTO_INCREMENT_WITH_TAB, startValue);
                }
            }
            sqlScript.append(MessageFormat.format("{0}{1}{2} {3}{4}{5},",
                    NEW_LINE, TAB, fieldName, sqlType, " NOT NULL", autoIncrement));
        }
        return end;
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
            sqlScript.append(MessageFormat.format("{10}{11}{0} {1}{12},{10}{11}CONSTRAINT " +
                            "FK_{2}_{3}_{4} FOREIGN KEY({5}) REFERENCES {6}({7}){8}{9},",
                    foreignKey, referenceSqlType, tableName.toUpperCase(Locale.ENGLISH),
                    refTableName.toUpperCase(Locale.ENGLISH), count, foreignKey,
                    refTableName, referenceFieldName, onDeleteScript, onUpdateScript, NEW_LINE, TAB, unique));
            updateReferenceTable(tableName, refTableName, referenceTables);
            count++;
        }
    }

    private static void updateReferenceTable(String tableName, String referenceTableName,
                                             HashMap<String, List<String>> referenceTables) {
        List<String> setOfReferenceTables;
        if (referenceTables.containsKey(referenceTableName)) {
            setOfReferenceTables = referenceTables.get(referenceTableName);
        } else {
            setOfReferenceTables = new ArrayList<>();
        }
        setOfReferenceTables.add(tableName);
        referenceTables.put(referenceTableName, setOfReferenceTables);
    }

    private static String addPrimaryKeyUniqueKey(List<String> primaryKeys, List<List<String>> uniqueConstraints) {
        String primaryKeyScript = PRIMARY_KEY_START_SCRIPT;
        String uniqueKeyScript = UNIQUE_KEY_START_SCRIPT;
        String stringFormat = "{0}{1}, ";
        String script = EMPTY;
        for (String primaryKey : primaryKeys) {
            primaryKeyScript = MessageFormat.format(stringFormat, primaryKeyScript, eliminateDoubleQuotes(primaryKey));
        }
        if (!primaryKeyScript.equals(PRIMARY_KEY_START_SCRIPT)) {
            script = primaryKeyScript.substring(0, primaryKeyScript.length() - 2).concat("),");
        }
        for (List<String> uniqueConstraint : uniqueConstraints) {
            for (String unique : uniqueConstraint) {
                uniqueKeyScript = MessageFormat.format(stringFormat, uniqueKeyScript, eliminateDoubleQuotes(unique));
            }
            if (!uniqueKeyScript.equals(UNIQUE_KEY_START_SCRIPT)) {
                script = script.concat(uniqueKeyScript.substring(0, uniqueKeyScript.length() - 2).concat("),"));
            }
            uniqueKeyScript = UNIQUE_KEY_START_SCRIPT;
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

    private static void createSqlFile(String script,
                                     String tableName, HashMap<String, List<String>> referenceTables,
                                     List<String> tableNamesInScript, Path directoryPath) throws BalException {
        try {
            String content = EMPTY;
            Path filePath = Paths.get(String.valueOf(directoryPath), PersistToolsConstants.FILE_NAME);
            if (Files.exists(filePath)) {
                byte[] bytes = Files.readAllBytes(filePath);
                content = new String(bytes, StandardCharsets.UTF_8);
                String tableNames = "";
                int firstIndex = 0;
                if (referenceTables.containsKey(tableName)) {
                    List<String> tables = referenceTables.get(tableName);
                    for (String table : tables) {
                        String name = table + ";";
                        int index = content.indexOf(name);
                        if ((firstIndex == 0 || index < firstIndex) && index > 1) {
                            tableNames = name;
                            firstIndex = index;
                        }
                    }
                    int index = firstIndex + tableNames.length();
                    content = content.substring(0, index) + NEW_LINE + NEW_LINE + script + NEW_LINE +
                            content.substring(index);
                } else {
                    int firstIndexOfScript = 0;
                    for (String table :tableNamesInScript) {
                        if (referenceTables.containsKey(table)) {
                            int index = script.indexOf(tableName);
                            if ((firstIndexOfScript == 0 || index < firstIndexOfScript) && index > 1) {
                                firstIndexOfScript = index;
                            }
                        }
                    }
                    if (firstIndexOfScript > 0) {
                        int index = firstIndexOfScript + tableName.length() + 1;
                        content = script.substring(0, index) + NEW_LINE + NEW_LINE + content + NEW_LINE +
                                script.substring(index);
                    } else {
                        script = script.concat(NEW_LINE + NEW_LINE);
                        content = script.concat(content);
                    }
                }
            } else {
                if (Files.notExists(directoryPath)) {
                    Files.createDirectories(directoryPath);
                }
                content = content.concat(script);
            }
            Files.writeString(filePath, content);
            tableNamesInScript.add(tableName);
        } catch (IOException e) {
            throw new BalException("Error in read or write a script file: " + e.getMessage());
        }
    }
}
