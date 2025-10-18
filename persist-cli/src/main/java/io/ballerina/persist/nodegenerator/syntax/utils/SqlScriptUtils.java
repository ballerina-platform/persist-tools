/*
 * Copyright (c) 2022, WSO2 LLC. (https://www.wso2.com) All Rights Reserved.
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the
 * License at
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

import io.ballerina.compiler.syntax.tree.AnnotationNode;
import io.ballerina.compiler.syntax.tree.MappingFieldNode;
import io.ballerina.compiler.syntax.tree.SpecificFieldNode;
import io.ballerina.persist.BalException;
import io.ballerina.persist.models.Entity;
import io.ballerina.persist.models.EntityField;
import io.ballerina.persist.models.Enum;
import io.ballerina.persist.models.EnumMember;
import io.ballerina.persist.models.Index;
import io.ballerina.persist.models.Relation;
import io.ballerina.persist.models.SqlType;
import io.ballerina.persist.nodegenerator.syntax.constants.BalSyntaxConstants;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

import static io.ballerina.persist.PersistToolsConstants.BallerinaTypes;
import static io.ballerina.persist.PersistToolsConstants.CUSTOM_SCHEMA_SUPPORTED_DB_PROVIDERS;
import static io.ballerina.persist.PersistToolsConstants.DefaultMaxLength;
import static io.ballerina.persist.PersistToolsConstants.SqlTypes;
import static io.ballerina.persist.PersistToolsConstants.SupportedDataSources.H2_DB;
import static io.ballerina.persist.PersistToolsConstants.SupportedDataSources.MSSQL_DB;
import static io.ballerina.persist.PersistToolsConstants.SupportedDataSources.MYSQL_DB;
import static io.ballerina.persist.PersistToolsConstants.SupportedDataSources.POSTGRESQL_DB;

/**
 * Sql script generator.
 *
 * @since 0.1.0
 */
public class SqlScriptUtils {

    private static final String NEW_LINE = System.lineSeparator();
    private static final String TAB = "\t";
    private static final String COMMA_WITH_SPACE = ", ";
    private static final String PRIMARY_KEY_START_SCRIPT = NEW_LINE + TAB + "PRIMARY KEY(";
    private static final String ENUM_START_SCRIPT = "ENUM(";
    private static final String ENUM_END_SCRIPT = ")";

    private static final String SINGLE_QUOTE = "'";

    private SqlScriptUtils() {}

    public static String[] generateSqlScript(Collection<Entity> entities, String datasource) throws BalException {
        HashMap<String, List<String>> referenceTables = new HashMap<>();
        HashMap<String, List<String>> tableScripts = new HashMap<>();
        //generate create table
        for (Entity entity : entities) {
            if (entity.containsUnsupportedTypes()) {
                continue;
            }
            List<String> tableScript = new ArrayList<>();
            String tableName = getTableNameWithSchema(entity, datasource);
            tableScript.add(generateDropTableQuery(tableName));
            tableScript.add(generateCreateTableQuery(entity, referenceTables, tableName, datasource));
            tableScripts.put(removeSingleQuote(entity.getTableName()), tableScript);
        }
        //generate create index
        List<String> indexScripts = new ArrayList<>();
        for (Entity entity : entities) {
            entity.getIndexes().forEach(index -> {
                indexScripts.add(
                        generateCreateIndexQuery(index, entity, datasource, index.isUnique())
                );
            });
            entity.getUniqueIndexes().forEach(index -> {
                indexScripts.add(
                        generateCreateIndexQuery(index, entity, datasource, index.isUnique())
                );
            });
        }
        List<String> scripts = new ArrayList<>(Arrays.asList(
                rearrangeScriptsWithReference(tableScripts.keySet(), referenceTables, tableScripts)));
        scripts.add(NEW_LINE);
        scripts.addAll(indexScripts);
        return scripts.toArray(new String[0]);
    }

    private static String generateDropTableQuery(String tableName) {
        return MessageFormat.format("DROP TABLE IF EXISTS {0};", tableName);
    }

    public static String generateCreateTableQuery(Entity entity, HashMap<String, List<String>> referenceTables,
                                                  String tableName, String datasource) throws BalException {

        String fieldDefinitions = generateFieldsDefinitionSegments(entity, referenceTables, datasource);

        return MessageFormat.format("{0}CREATE TABLE {1} ({2}{3});", NEW_LINE,
                tableName, fieldDefinitions, NEW_LINE);
    }

    private static String generateCreateIndexQuery(Index index, Entity entity, String datasource, boolean unique) {
        String tableName = getTableNameWithSchema(entity, datasource);
        return MessageFormat.format("CREATE{0} INDEX {1} ON {2} ({3});",
                unique ? " UNIQUE" : "",
                escape(index.getIndexName(), datasource),
                tableName,
                index.getFields().stream()
                        .map(field -> escape(removeSingleQuote(field.getFieldColumnName()), datasource))
                        .reduce((s1, s2) -> s1 + COMMA_WITH_SPACE + s2).orElse(""));
    }

    public static String getTableNameWithSchema(Entity entity, String datasource) {
        String tableName = escape(removeSingleQuote(entity.getTableName()), datasource);
        String schemaName = entity.getSchemaName();
        if (CUSTOM_SCHEMA_SUPPORTED_DB_PROVIDERS.contains(datasource) &&
                schemaName != null && !schemaName.isEmpty()) {
            tableName = schemaName + "." + tableName;
        }
        return tableName;
    }

    private static String generateFieldsDefinitionSegments(Entity entity, HashMap<String, List<String>> referenceTables,
                                                           String datasource) throws BalException {
        StringBuilder sqlScript = new StringBuilder();
        sqlScript.append(getColumnsScript(entity, datasource));

        HashMap<String, List<EntityField>> relationFields = getMapOfRelationFields(entity, true);
        // this is done to retain the original order of the associations
        List<String> associations = entity.getFields().stream().filter(entityField ->
                        entityField.getRelation() != null && entityField.getRelation().isOwner())
                .map(EntityField::getFieldType).toList();
        for (int i = 0; i < associations.size(); i++) {
            int occurrence = findOccurrence(associations, i);
            sqlScript.append(getRelationScripts(entity, relationFields.get(associations.get(i)).get(occurrence),
                    occurrence, referenceTables, datasource));
        }
        sqlScript.append(addPrimaryKey(entity.getKeys(), datasource));
        return sqlScript.substring(0, sqlScript.length() - 1);
    }

    private static int findOccurrence(List<String> associations, int index) {
        int occured = 0;
        for (int i = 0; i < index; i++) {
            if (Objects.equals(associations.get(i), associations.get(index))) {
                occured++;
            }
        }
        return occured;
    }

    private static HashMap<String, List<EntityField>> getMapOfRelationFields(Entity entity, boolean isOwner) {
        HashMap<String, List<EntityField>> relationFields = new HashMap<>();
        for (EntityField entityField : entity.getFields()) {
            if (entityField.getRelation() != null && entityField.getRelation().isOwner() == isOwner) {
                if (relationFields.containsKey(entityField.getFieldType())) {
                    relationFields.get(entityField.getFieldType()).add(entityField);
                } else {
                    List<EntityField> fields = new ArrayList<>();
                    fields.add(entityField);
                    relationFields.put(entityField.getFieldType(), fields);
                }
            }
        }
        return relationFields;
    }

    private static String getColumnsScript(Entity entity, String datasource) throws BalException {
        StringBuilder columnScript = new StringBuilder();
        for (EntityField entityField : entity.getFields()) {
            if (entityField.getRelation() != null) {
                continue;
            }

            String fieldName = escape(removeSingleQuote(entityField.getFieldColumnName()), datasource);
            String sqlType;
            Enum enumValue = entityField.getEnum();
            if (enumValue == null) {
                sqlType = getSqlType(entityField, datasource);
            } else {
                sqlType = getEnumType(enumValue, fieldName, datasource);
            }
            assert sqlType != null;
            if (entityField.isOptionalType()) {
                columnScript.append(MessageFormat.format("{0}{1}{2} {3},",
                        NEW_LINE, TAB, fieldName, sqlType));
            } else {
                switch (datasource) {
                    case MSSQL_DB:
                        columnScript.append(MessageFormat.format("{0}{1}{2} {3}{4},",
                                NEW_LINE, TAB, fieldName, sqlType,
                                entityField.isDbGenerated() ? " IDENTITY(1,1)" : " NOT NULL"));
                        break;
                    case POSTGRESQL_DB:
                        columnScript.append(MessageFormat.format("{0}{1}{2} {3}{4},",
                                NEW_LINE, TAB, fieldName, "",
                                entityField.isDbGenerated() ? " SERIAL" : sqlType + " NOT NULL"));
                        break;
                    case MYSQL_DB:
                    case H2_DB:
                        columnScript.append(MessageFormat.format("{0}{1}{2} {3}{4},",
                                NEW_LINE, TAB, fieldName, sqlType,
                                entityField.isDbGenerated() ? " AUTO_INCREMENT" : " NOT NULL"));
                        break;
                    default: { }
                }
            }
        }
        return columnScript.toString();
    }

    private static String getRelationScripts(Entity entity, EntityField entityField, int index, HashMap<String,
            List<String>> referenceTables, String datasource) throws BalException {
        StringBuilder relationScripts = new StringBuilder();
        Relation relation = entityField.getRelation();
        List<Relation.Key> keyColumns = relation.getKeyColumns();
        List<String> references = relation.getKeyColumns().stream().map(Relation.Key::getReferenceColumnName).toList();
        Entity assocEntity = relation.getAssocEntity();
        // if the association entity has unsupported types, skip the relation
        if (assocEntity.containsUnsupportedTypes()) {
            return "";
        }
        EntityField assocEntityField = getMapOfRelationFields(assocEntity, false).get(entity.getEntityName())
                .get(index);
        Relation.RelationType associatedEntityRelationType = assocEntityField.getRelation().getRelationType();
        StringBuilder foreignKey = new StringBuilder();
        StringBuilder referenceFieldName = new StringBuilder();
        int noOfReferencesKey = references.size();
        boolean uniqueIndexExists = entity.getUniqueIndexes().stream().anyMatch(idx -> idx.getFields().stream()
                .map(EntityField::getFieldColumnName).toList()
                .equals(keyColumns.stream().map(Relation.Key::getColumnName).toList()));
        for (int i = 0; i < noOfReferencesKey; i++) {
            String referenceSqlType = null;
            for (EntityField assocField : assocEntity.getFields()) {
                if (assocField.getRelation() != null) {
                    continue;
                }
                if (assocField.getFieldColumnName().equals(references.get(i))) {
                    referenceSqlType = getSqlType(assocField, datasource);
                    break;
                }
            }

            if (relation.getRelationType().equals(Relation.RelationType.ONE) &&
                    associatedEntityRelationType.equals(Relation.RelationType.ONE) &&
                    noOfReferencesKey == 1 && !uniqueIndexExists) {
                referenceSqlType += " UNIQUE";
            }
            foreignKey.append(escape(removeSingleQuote(keyColumns.get(i).getColumnName()), datasource));
            referenceFieldName.append(escape(removeSingleQuote(references.get(i)), datasource));
            if (i < noOfReferencesKey - 1) {
                foreignKey.append(COMMA_WITH_SPACE);
                referenceFieldName.append(COMMA_WITH_SPACE);
            }
            relationScripts.append(MessageFormat.format("{0}{1}{2} {3}{4},", NEW_LINE, TAB,
                    escape(removeSingleQuote(keyColumns.get(i).getColumnName()), datasource), referenceSqlType,
                    " NOT NULL"));
        }
        if (noOfReferencesKey > 1 && relation.getRelationType().equals(Relation.RelationType.ONE) &&
                associatedEntityRelationType.equals(Relation.RelationType.ONE) && !uniqueIndexExists) {
            relationScripts.append(MessageFormat.format("{0}{1}UNIQUE ({2}),", NEW_LINE, TAB, foreignKey));
        }
        relationScripts.append(MessageFormat.format("{0}{1}FOREIGN KEY({2})REFERENCES {3}({4}),",
                NEW_LINE, TAB, foreignKey.toString(),
                getTableNameWithSchema(assocEntity, datasource), referenceFieldName));
        updateReferenceTable(removeSingleQuote(entity.getTableName()), removeSingleQuote(assocEntity.getTableName()),
                referenceTables);
        return relationScripts.toString();
    }

    private static String removeSingleQuote(String fieldName) {
        if (fieldName.startsWith("'")) {
            return fieldName.substring(1);
        }
        return fieldName;
    }

    public static void updateReferenceTable(String tableName, String referenceTableName,
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

    private static String addPrimaryKey(List<EntityField> primaryKeys, String datasource) {
        return createKeysScript(primaryKeys, datasource);
    }

    private static String createKeysScript(List<EntityField> keys, String datasource) {
        StringBuilder keyScripts = new StringBuilder();
        if (keys.size() > 0) {
            keyScripts.append(MessageFormat.format("{0}", PRIMARY_KEY_START_SCRIPT));
            for (EntityField key : keys) {
                keyScripts.append(MessageFormat.format("{0},",
                        escape(removeSingleQuote(key.getFieldColumnName()), datasource)));
            }
            keyScripts.deleteCharAt(keyScripts.length() - 1).append("),");
        }
        return keyScripts.toString();
    }

    public static String getSqlType(EntityField entityField, String datasource) throws BalException {
        String sqlType;
        if (!entityField.isArrayType()) {
            sqlType = getTypeNonArray(entityField.getFieldType(), entityField.getSqlType(), datasource);
        } else {
            sqlType = getTypeArray(entityField.getFieldType(), datasource);
        }
        if (!sqlType.equals(SqlTypes.VARCHAR)) {
            return sqlType;
        }
        String length = BalSyntaxConstants.VARCHAR_LENGTH;
        for (AnnotationNode annotationNode : entityField.getAnnotation()) {
            String annotationName = annotationNode.annotReference().toSourceCode().trim();
            if (annotationName.equals(BalSyntaxConstants.SQL_TEXT_MAPPING_ANNOTATION_NAME)) {
                return SqlTypes.TEXT;
            }
            if (annotationName.equals(BalSyntaxConstants.CONSTRAINT_STRING)) {
                if (annotationNode.annotValue().isEmpty()) {
                    continue;
                }
                for (MappingFieldNode mappingFieldNode : annotationNode.annotValue().get().fields()) {
                    SpecificFieldNode specificFieldNode = (SpecificFieldNode) mappingFieldNode;
                    String fieldName = specificFieldNode.fieldName().toSourceCode().trim();
                    if (fieldName.equals(BalSyntaxConstants.MAX_LENGTH)) {
                        if (specificFieldNode.valueExpr().isEmpty()) {
                            continue;
                        }
                        length = specificFieldNode.valueExpr().get().toSourceCode().trim();
                    } else if (fieldName.equals(BalSyntaxConstants.LENGTH)) {
                        if (specificFieldNode.valueExpr().isEmpty()) {
                            continue;
                        }
                        length = specificFieldNode.valueExpr().get().toSourceCode().trim();
                    }
                }
            }
        }
        return sqlType + (String.format("(%s)", length));
    }


    public static String getTypeNonArray(String field, SqlType sqlType, String datasource) throws BalException {
        if (sqlType != null) {
            switch (sqlType.getTypeName()) {
                case SqlTypes.DECIMAL:
                    return SqlTypes.DECIMAL + String.format("(%s,%s)",
                            sqlType.getNumericPrecision(),
                            sqlType.getNumericScale());
                case SqlTypes.VARCHAR:
                    return SqlTypes.VARCHAR + String.format("(%s)", sqlType.getMaxLength());
                case SqlTypes.CHAR:
                    return SqlTypes.CHAR + String.format("(%s)", sqlType.getMaxLength());
                default: {
                }
            }
        }
        switch (removeSingleQuote(field)) {

            // Ballerina --> int
            // MySQL --> INT
            // MSSQL --> INT
            // PostgreSQL --> INT
            // H2 --> INT
            case BallerinaTypes.INT:
                return SqlTypes.INT;

            // Ballerina --> boolean
            // MySQL --> BOOLEAN
            // MSSQL --> BIT
            // PostgreSQL --> BOOLEAN
            // H2 --> BOOLEAN
            case BallerinaTypes.BOOLEAN:
                if (datasource.equals(MSSQL_DB)) {
                    return SqlTypes.BIT;
                }
                return SqlTypes.BOOLEAN;

            // Ballerina --> decimal
            // MySQL --> DECIMAL(65,30)
            // MSSQL --> DECIMAL(38,30)
            // PostgreSQL --> DECIMAL(65,30)
            // H2 --> DECIMAL(65,30)
            case BallerinaTypes.DECIMAL:
                if (datasource.equals(MSSQL_DB)) {
                    return SqlTypes.DECIMAL + String.format("(%s,%s)",
                            DefaultMaxLength.DECIMAL_PRECISION_MSSQL,
                            DefaultMaxLength.DECIMAL_SCALE);
                }
                if (datasource.equals(POSTGRESQL_DB)) {
                    return SqlTypes.DECIMAL + String.format("(%s,%s)",
                            DefaultMaxLength.DECIMAL_PRECISION_POSTGRESQL,
                            DefaultMaxLength.DECIMAL_SCALE);
                }
                return SqlTypes.DECIMAL + String.format("(%s,%s)",
                        DefaultMaxLength.DECIMAL_PRECISION,
                        DefaultMaxLength.DECIMAL_SCALE);

            // Ballerina --> float
            // MySQL --> DOUBLE
            // MSSQL --> FLOAT
            // PostgreSQL --> FLOAT
            // H2 --> FLOAT
            case BallerinaTypes.FLOAT:
                if (datasource.equals(MYSQL_DB)) {
                    return SqlTypes.DOUBLE;
                }
                return SqlTypes.FLOAT;

            // Ballerina --> time:Date
            // MySQL --> DATE
            // MSSQL --> DATE
            // PostgreSQL --> DATE
            // H2 --> DATE
            case BallerinaTypes.DATE:
                return SqlTypes.DATE;

            // Ballerina --> time:TimeOfDay
            // MySQL --> TIME
            // MSSQL --> TIME
            // PostgreSQL --> TIME
            // H2 --> TIME
            case BallerinaTypes.TIME_OF_DAY:
                return SqlTypes.TIME;

            // Ballerina --> time:Utc
            // MySQL --> TIMESTAMP
            // MSSQL --> DATETIME2
            // PostgreSQL --> TIMESTAMP
            // H2 --> TIMESTAMP
            case BallerinaTypes.UTC:
                if (datasource.equals(MSSQL_DB)) {
                    return SqlTypes.DATE_TIME2;
                }
                return SqlTypes.TIME_STAMP;

            // Ballerina --> time:Civil
            // MySQL --> DATETIME
            // MSSQL --> DATETIME2
            // PostgreSQL --> TIMESTAMP
            // H2 --> DATETIME
            case BallerinaTypes.CIVIL:
                if (datasource.equals(MSSQL_DB)) {
                    return SqlTypes.DATE_TIME2;
                }
                if (datasource.equals(POSTGRESQL_DB)) {
                    return SqlTypes.TIME_STAMP;
                }
                return SqlTypes.DATE_TIME;

            // Ballerina --> string
            // MySQL --> VARCHAR
            // MSSQL --> VARCHAR
            // PostgreSQL --> VARCHAR
            // H2 --> VARCHAR
            case BallerinaTypes.STRING:
                return SqlTypes.VARCHAR;

            default:
                throw new BalException("couldn't find equivalent SQL type for the field type: " + field);
        }
    }

    public static String getTypeArray(String field, String datasource) throws BalException {

        // Ballerina --> byte[]
        // MySQL --> LONGBLOB
        // MSSQL --> VARBINARY
        // PostgreSQL --> BYTEA
        // H2 --> LONGBLOB
        if (BallerinaTypes.BYTE.equals(field)) {
            if (datasource.equals(MSSQL_DB)) {
                return SqlTypes.VARBINARY_WITH_MAX;
            }
            if (datasource.equals(POSTGRESQL_DB)) {
                return SqlTypes.BYTEA;
            }
            return SqlTypes.LONG_BLOB;
        }
        throw new BalException("couldn't find equivalent SQL type for the field type: " + field);
    }

    private static String getEnumType(Enum enumValue, String fieldName, String datasource) {
        if (datasource.equals(MSSQL_DB) ||
                datasource.equals(POSTGRESQL_DB) ||
                datasource.equals(H2_DB)) {
            int maxLength = 0;
            List<EnumMember> members = enumValue.getMembers();
            StringBuilder checkStringBuilder = new StringBuilder();
            for (int i = 0; i < members.size(); i++) {
                EnumMember member = members.get(i);
                String value;
                if (member.getValue() != null) {
                    value = member.getValue();
                } else {
                    value = member.getIdentifier();
                }

                checkStringBuilder.append(SINGLE_QUOTE);
                checkStringBuilder.append(value);
                checkStringBuilder.append(SINGLE_QUOTE);

                if (i < members.size() - 1) {
                    checkStringBuilder.append(COMMA_WITH_SPACE);
                }

                if (value.length() > maxLength) {
                    maxLength = value.length();
                }
            }

            return String.format("VARCHAR(%s) CHECK (%s IN (%s))", maxLength, fieldName, checkStringBuilder);
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(ENUM_START_SCRIPT);

        List<EnumMember> members = enumValue.getMembers();
        for (int i = 0; i < members.size(); i++) {
            stringBuilder.append(SINGLE_QUOTE);

            EnumMember member = members.get(i);
            if (member.getValue() != null) {
                stringBuilder.append(member.getValue());
            } else {
                stringBuilder.append(member.getIdentifier());
            }

            stringBuilder.append(SINGLE_QUOTE);

            if (i < members.size() - 1) {
                stringBuilder.append(COMMA_WITH_SPACE);
            }
        }

        stringBuilder.append(ENUM_END_SCRIPT);
        return stringBuilder.toString();
    }

    private static String[] rearrangeScriptsWithReference(Set<String> tables,
                                                          HashMap<String, List<String>> referenceTables,
                                                          HashMap<String, List<String>> tableScripts) {
        // Step 1: Build the dependency graph
        Map<String, List<String>> graph = new HashMap<>();
        Map<String, Integer> inDegree = new HashMap<>();

        // Initialize graph and in-degree map
        for (String table : tables) {
            graph.put(table, new ArrayList<>());
            inDegree.put(table, 0);
        }

        // Populate the graph and in-degree map
        for (Map.Entry<String, List<String>> entry : referenceTables.entrySet()) {
            String table = entry.getKey();
            for (String referenceTable : entry.getValue()) {
                graph.get(referenceTable).add(table);
                inDegree.put(table, inDegree.get(table) + 1);
            }
        }

        // Step 2: Perform topological sorting using Kahn's Algorithm
        Queue<String> queue = new LinkedList<>();
        List<String> sortedOrder = new ArrayList<>();

        // Add nodes with in-degree 0 to the queue
        for (Map.Entry<String, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
            }
        }

        // Process the graph
        while (!queue.isEmpty()) {
            String current = queue.poll();
            sortedOrder.add(current);

            for (String neighbor : graph.get(current)) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.add(neighbor);
                }
            }
        }

        // Reverse the sorted order for reverse dependency processing
        Collections.reverse(sortedOrder);

        // Step 3: Rearrange table scripts based on the sorted order
        int length = tables.size() * 2;
        String[] tableScriptsInOrder = new String[length];
        for (int i = 0; i < sortedOrder.size(); i++) {
            String tableName = sortedOrder.get(i);
            List<String> script = tableScripts.get(removeSingleQuote(tableName));
            tableScriptsInOrder[i] = script.get(0); // DROP TABLE script
            tableScriptsInOrder[length - (i + 1)] = script.get(1); // CREATE TABLE script
        }

        return tableScriptsInOrder;
    }

    private static String escape(String name, String datasource) {
        if (datasource.equals(MSSQL_DB)) {
            return "[" + name + "]";
        }
        if (datasource.equals(POSTGRESQL_DB) ||
                datasource.equals(H2_DB)) {
            return '"' + name + '"';
        }
        return '`' + name + '`';
    }
}