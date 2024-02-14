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
package io.ballerina.persist.nodegenerator.syntax.utils;

import io.ballerina.compiler.syntax.tree.AnnotationNode;
import io.ballerina.compiler.syntax.tree.ExpressionNode;
import io.ballerina.compiler.syntax.tree.MappingConstructorExpressionNode;
import io.ballerina.compiler.syntax.tree.MappingFieldNode;
import io.ballerina.compiler.syntax.tree.SpecificFieldNode;
import io.ballerina.persist.BalException;
import io.ballerina.persist.PersistToolsConstants;
import io.ballerina.persist.models.Entity;
import io.ballerina.persist.models.EntityField;
import io.ballerina.persist.models.Enum;
import io.ballerina.persist.models.EnumMember;
import io.ballerina.persist.models.Index;
import io.ballerina.persist.models.Relation;
import io.ballerina.persist.models.SQLType;
import io.ballerina.persist.nodegenerator.syntax.constants.BalSyntaxConstants;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
            List<String> tableScript = new ArrayList<>();
            String tableName = removeSingleQuote(entity.getTableName());
            tableScript.add(generateDropTableQuery(escape(tableName, datasource)));
            tableScript.add(generateCreateTableQuery(entity, referenceTables, datasource));
            tableScripts.put(tableName, tableScript);
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

    private static String generateCreateTableQuery(Entity entity, HashMap<String, List<String>> referenceTables,
                                                   String datasource) throws BalException {

        String fieldDefinitions = generateFieldsDefinitionSegments(entity, referenceTables, datasource);

        return MessageFormat.format("{0}CREATE TABLE {1} ({2}{3});", NEW_LINE,
                escape(removeSingleQuote(entity.getTableName()), datasource), fieldDefinitions, NEW_LINE);
    }

    private static String generateCreateIndexQuery(Index index, Entity entity, String datasource, boolean unique) {
        return MessageFormat.format("CREATE{0} INDEX {1} ON {2} ({3});",
                    unique ? " UNIQUE" : "",
                    escape(index.getIndexName(), datasource),
                    escape(removeSingleQuote(entity.getTableName()), datasource),
                    index.getFields().stream()
                            .map(field -> escape(removeSingleQuote(field.getFieldColumnName()), datasource))
                            .reduce((s1, s2) -> s1 + COMMA_WITH_SPACE + s2).orElse(""));
    }

    private static String generateFieldsDefinitionSegments(Entity entity, HashMap<String, List<String>> referenceTables,
                                                           String datasource) throws BalException {
        StringBuilder sqlScript = new StringBuilder();
        sqlScript.append(getColumnsScript(entity, datasource));
        List<EntityField> relationFields = entity.getFields().stream()
                .filter(entityField -> entityField.getRelation() != null && entityField.getRelation().isOwner())
                .toList();
        for (EntityField entityField : relationFields) {
            sqlScript.append(getRelationScripts(removeSingleQuote(entity.getTableName()),
                    entityField, referenceTables, datasource));
        }
        sqlScript.append(addPrimaryKey(entity.getKeys(), datasource));
        return sqlScript.substring(0, sqlScript.length() - 1);
    }

    private static String getColumnsScript(Entity entity, String datasource) throws BalException {
        StringBuilder columnScript = new StringBuilder();
        for (EntityField entityField :entity.getFields()) {
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
                columnScript.append(MessageFormat.format("{0}{1}{2} {3}{4},",
                        NEW_LINE, TAB, fieldName, sqlType,
                        entityField.isDbGenerated() ? " AUTO_INCREMENT" : " NOT NULL"));
            }
        }
        return columnScript.toString();
    }

    private static String getRelationScripts(String tableName, EntityField entityField,
                                             HashMap<String, List<String>> referenceTables,
                                             String datasource) throws BalException {
        StringBuilder relationScripts = new StringBuilder();
        Relation relation = entityField.getRelation();
        List<Relation.Key> keyColumns = relation.getKeyColumns();
        List<String> references = relation.getReferences();
        Entity assocEntity = relation.getAssocEntity();
        StringBuilder foreignKey = new StringBuilder();
        StringBuilder referenceFieldName = new StringBuilder();
        Relation.RelationType associatedEntityRelationType = Relation.RelationType.NONE;
        int noOfReferencesKey = references.size();
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
            for (EntityField field: assocEntity.getFields()) {
                if (escape(removeSingleQuote(field.getFieldType()), datasource).equals(escape(tableName, datasource))) {
                    associatedEntityRelationType = field.getRelation().getRelationType();
                    break;
                }
            }
            if (relation.getRelationType().equals(Relation.RelationType.ONE) &&
                    associatedEntityRelationType.equals(Relation.RelationType.ONE) && noOfReferencesKey == 1) {
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
                associatedEntityRelationType.equals(Relation.RelationType.ONE)) {
            relationScripts.append(MessageFormat.format("{0}{1}UNIQUE ({2}),", NEW_LINE, TAB, foreignKey));
        }
        relationScripts.append(MessageFormat.format("{0}{1}FOREIGN KEY({2}) REFERENCES {3}({4}),",
                NEW_LINE, TAB, foreignKey.toString(),
                escape(removeSingleQuote(assocEntity.getTableName()), datasource), referenceFieldName));
        updateReferenceTable(tableName, assocEntity.getTableName(), referenceTables);
        return relationScripts.toString();
    }

    private static String removeSingleQuote(String fieldName) {
        if (fieldName.startsWith("'")) {
            return fieldName.substring(1);
        }
        return fieldName;
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

    private static String getSqlType(EntityField entityField, String datasource) throws BalException {
        String sqlType;
        if (!entityField.isArrayType()) {
            sqlType = getTypeNonArray(entityField, datasource);
        } else {
            sqlType = getTypeArray(entityField.getFieldType(), datasource);
        }
        if (!sqlType.equals(PersistToolsConstants.SqlTypes.VARCHAR)) {
            return sqlType;
        }
        String length = BalSyntaxConstants.VARCHAR_LENGTH;
        if (entityField.getAnnotation() != null) {
            for (AnnotationNode annotationNode : entityField.getAnnotation()) {
                String annotationName = annotationNode.annotReference().toSourceCode().trim();
                if (annotationName.equals(BalSyntaxConstants.CONSTRAINT_STRING)) {
                    Optional<MappingConstructorExpressionNode> annotationFieldNode = annotationNode.annotValue();
                    if (annotationFieldNode.isPresent()) {
                        for (MappingFieldNode mappingFieldNode : annotationFieldNode.get().fields()) {
                            SpecificFieldNode specificFieldNode = (SpecificFieldNode) mappingFieldNode;
                            String fieldName = specificFieldNode.fieldName().toSourceCode().trim();
                            if (fieldName.equals(BalSyntaxConstants.MAX_LENGTH)) {
                                Optional<ExpressionNode> valueExpr = specificFieldNode.valueExpr();
                                if (valueExpr.isPresent()) {
                                    length = valueExpr.get().toSourceCode().trim();
                                }
                            } else if (fieldName.equals(BalSyntaxConstants.LENGTH)) {
                                Optional<ExpressionNode> valueExpr = specificFieldNode.valueExpr();
                                if (valueExpr.isPresent()) {
                                    length = valueExpr.get().toSourceCode().trim();
                                }
                            }
                        }
                    }
                }
            }
        }
        return sqlType + (String.format("(%s)", length));
    }

    public static String getTypeNonArray(String field, String datasource) throws BalException {
        switch (removeSingleQuote(field)) {

            // Ballerina --> int
            // MySQL --> INT
            // MSSQL --> INT
            // PostgreSQL --> INT
            case PersistToolsConstants.BallerinaTypes.INT:
                return PersistToolsConstants.SqlTypes.INT;

            // Ballerina --> boolean
            // MySQL --> BOOLEAN
            // MSSQL --> BIT
            // PostgreSQL --> BOOLEAN
            case PersistToolsConstants.BallerinaTypes.BOOLEAN:
                if (datasource.equals(PersistToolsConstants.SupportedDataSources.MSSQL_DB)) {
                    return PersistToolsConstants.SqlTypes.BIT;
                }
                return PersistToolsConstants.SqlTypes.BOOLEAN;

            // Ballerina --> decimal
            // MySQL --> DECIMAL(65,30)
            // MSSQL --> DECIMAL(38,30)
            // PostgreSQL --> DECIMAL(65,30)
            case PersistToolsConstants.BallerinaTypes.DECIMAL:
                if (datasource.equals(PersistToolsConstants.SupportedDataSources.MSSQL_DB)) {
                    return PersistToolsConstants.SqlTypes.DECIMAL + String.format("(%s,%s)",
                            PersistToolsConstants.DefaultMaxLength.DECIMAL_PRECISION_MSSQL,
                            PersistToolsConstants.DefaultMaxLength.DECIMAL_SCALE);
                }
                if (datasource.equals(PersistToolsConstants.SupportedDataSources.POSTGRESQL_DB)) {
                    return PersistToolsConstants.SqlTypes.DECIMAL + String.format("(%s,%s)",
                            PersistToolsConstants.DefaultMaxLength.DECIMAL_PRECISION_POSTGRESQL,
                            PersistToolsConstants.DefaultMaxLength.DECIMAL_SCALE);
                }
                return PersistToolsConstants.SqlTypes.DECIMAL + String.format("(%s,%s)",
                        PersistToolsConstants.DefaultMaxLength.DECIMAL_PRECISION_MYSQL,
                        PersistToolsConstants.DefaultMaxLength.DECIMAL_SCALE);

            // Ballerina --> float
            // MySQL --> DOUBLE
            // MSSQL --> FLOAT
            // PostgreSQL --> FLOAT
            case PersistToolsConstants.BallerinaTypes.FLOAT:
                if (datasource.equals(PersistToolsConstants.SupportedDataSources.MYSQL_DB)) {
                    return PersistToolsConstants.SqlTypes.DOUBLE;
                }
                return PersistToolsConstants.SqlTypes.FLOAT;

            // Ballerina --> time:Date
            // MySQL --> DATE
            // MSSQL --> DATE
            // PostgreSQL --> DATE
            case PersistToolsConstants.BallerinaTypes.DATE:
                return PersistToolsConstants.SqlTypes.DATE;

            // Ballerina --> time:TimeOfDay
            // MySQL --> TIME
            // MSSQL --> TIME
            // PostgreSQL --> TIME
            case PersistToolsConstants.BallerinaTypes.TIME_OF_DAY:
                return PersistToolsConstants.SqlTypes.TIME;

            // Ballerina --> time:Utc
            // MySQL --> TIMESTAMP
            // MSSQL --> DATETIME2
            // PostgreSQL --> TIMESTAMP
            case PersistToolsConstants.BallerinaTypes.UTC:
                if (datasource.equals(PersistToolsConstants.SupportedDataSources.MSSQL_DB)) {
                    return PersistToolsConstants.SqlTypes.DATE_TIME2;
                }
                return PersistToolsConstants.SqlTypes.TIME_STAMP;

            // Ballerina --> time:Civil
            // MySQL --> DATETIME
            // MSSQL --> DATETIME2
            // PostgreSQL --> TIMESTAMP
            case PersistToolsConstants.BallerinaTypes.CIVIL:
                if (datasource.equals(PersistToolsConstants.SupportedDataSources.MSSQL_DB)) {
                    return PersistToolsConstants.SqlTypes.DATE_TIME2;
                }
                if (datasource.equals(PersistToolsConstants.SupportedDataSources.POSTGRESQL_DB)) {
                    return PersistToolsConstants.SqlTypes.TIME_STAMP;
                }
                return PersistToolsConstants.SqlTypes.DATE_TIME;

            // Ballerina --> string
            // MySQL --> VARCHAR
            // MSSQL --> VARCHAR
            // PostgreSQL --> VARCHAR
            case PersistToolsConstants.BallerinaTypes.STRING:
                return PersistToolsConstants.SqlTypes.VARCHAR;

            default:
                throw new BalException("couldn't find equivalent SQL type for the field type: " + field);
        }
    }

    public static String getTypeNonArray(EntityField field, String datasource) throws BalException {
        SQLType sqlType = field.getSqlType();
        if (sqlType != null) {
            switch (sqlType.getTypeName()) {
                case PersistToolsConstants.SqlTypes.DECIMAL:
                    return PersistToolsConstants.SqlTypes.DECIMAL + String.format("(%s,%s)",
                                sqlType.getNumericPrecision(),
                                sqlType.getNumericScale());
                case PersistToolsConstants.SqlTypes.VARCHAR:
                    return PersistToolsConstants.SqlTypes.VARCHAR + String.format("(%s)", sqlType.getMaxLength());
                case PersistToolsConstants.SqlTypes.CHAR:
                    return PersistToolsConstants.SqlTypes.CHAR + String.format("(%s)", sqlType.getMaxLength());
                default: { }
            }
        }

        switch (removeSingleQuote(field.getFieldType())) {

            // Ballerina --> int
            // MySQL --> INT
            // MSSQL --> INT
            // PostgreSQL --> INT
            case PersistToolsConstants.BallerinaTypes.INT:
                return PersistToolsConstants.SqlTypes.INT;

            // Ballerina --> boolean
            // MySQL --> BOOLEAN
            // MSSQL --> BIT
            // PostgreSQL --> BOOLEAN
            case PersistToolsConstants.BallerinaTypes.BOOLEAN:
                if (datasource.equals(PersistToolsConstants.SupportedDataSources.MSSQL_DB)) {
                    return PersistToolsConstants.SqlTypes.BIT;
                }
                return PersistToolsConstants.SqlTypes.BOOLEAN;

            // Ballerina --> decimal
            // MySQL --> DECIMAL(65,30)
            // MSSQL --> DECIMAL(38,30)
            // PostgreSQL --> DECIMAL(65,30)
            case PersistToolsConstants.BallerinaTypes.DECIMAL:
                if (datasource.equals(PersistToolsConstants.SupportedDataSources.MSSQL_DB)) {
                    return PersistToolsConstants.SqlTypes.DECIMAL + String.format("(%s,%s)",
                            PersistToolsConstants.DefaultMaxLength.DECIMAL_PRECISION_MSSQL,
                            PersistToolsConstants.DefaultMaxLength.DECIMAL_SCALE);
                }
                if (datasource.equals(PersistToolsConstants.SupportedDataSources.POSTGRESQL_DB)) {
                    return PersistToolsConstants.SqlTypes.DECIMAL + String.format("(%s,%s)",
                            PersistToolsConstants.DefaultMaxLength.DECIMAL_PRECISION_POSTGRESQL,
                            PersistToolsConstants.DefaultMaxLength.DECIMAL_SCALE);
                }
                return PersistToolsConstants.SqlTypes.DECIMAL + String.format("(%s,%s)",
                        PersistToolsConstants.DefaultMaxLength.DECIMAL_PRECISION_MYSQL,
                        PersistToolsConstants.DefaultMaxLength.DECIMAL_SCALE);

            // Ballerina --> float
            // MySQL --> DOUBLE
            // MSSQL --> FLOAT
            // PostgreSQL --> FLOAT
            case PersistToolsConstants.BallerinaTypes.FLOAT:
                if (datasource.equals(PersistToolsConstants.SupportedDataSources.MYSQL_DB)) {
                    return PersistToolsConstants.SqlTypes.DOUBLE;
                }
                return PersistToolsConstants.SqlTypes.FLOAT;

            // Ballerina --> time:Date
            // MySQL --> DATE
            // MSSQL --> DATE
            // PostgreSQL --> DATE
            case PersistToolsConstants.BallerinaTypes.DATE:
                return PersistToolsConstants.SqlTypes.DATE;

            // Ballerina --> time:TimeOfDay
            // MySQL --> TIME
            // MSSQL --> TIME
            // PostgreSQL --> TIME
            case PersistToolsConstants.BallerinaTypes.TIME_OF_DAY:
                return PersistToolsConstants.SqlTypes.TIME;

            // Ballerina --> time:Utc
            // MySQL --> TIMESTAMP
            // MSSQL --> DATETIME2
            // PostgreSQL --> TIMESTAMP
            case PersistToolsConstants.BallerinaTypes.UTC:
                if (datasource.equals(PersistToolsConstants.SupportedDataSources.MSSQL_DB)) {
                    return PersistToolsConstants.SqlTypes.DATE_TIME2;
                }
                return PersistToolsConstants.SqlTypes.TIME_STAMP;

            // Ballerina --> time:Civil
            // MySQL --> DATETIME
            // MSSQL --> DATETIME2
            // PostgreSQL --> TIMESTAMP
            case PersistToolsConstants.BallerinaTypes.CIVIL:
                if (datasource.equals(PersistToolsConstants.SupportedDataSources.MSSQL_DB)) {
                    return PersistToolsConstants.SqlTypes.DATE_TIME2;
                }
                if (datasource.equals(PersistToolsConstants.SupportedDataSources.POSTGRESQL_DB)) {
                    return PersistToolsConstants.SqlTypes.TIME_STAMP;
                }
                return PersistToolsConstants.SqlTypes.DATE_TIME;

            // Ballerina --> string
            // MySQL --> VARCHAR
            // MSSQL --> VARCHAR
            // PostgreSQL --> VARCHAR
            case PersistToolsConstants.BallerinaTypes.STRING:
                return PersistToolsConstants.SqlTypes.VARCHAR;

            default:
                throw new BalException("couldn't find equivalent SQL type for the field type: " + field);
        }
    }

    public static String getTypeArray(String field, String datasource) throws BalException {

        // Ballerina --> byte[]
        // MySQL --> LONGBLOB
        // MSSQL --> VARBINARY
        // PostgreSQL --> BYTEA
        if (PersistToolsConstants.BallerinaTypes.BYTE.equals(field)) {
            if (datasource.equals(PersistToolsConstants.SupportedDataSources.MSSQL_DB)) {
                return PersistToolsConstants.SqlTypes.VARBINARY;
            }
            if (datasource.equals(PersistToolsConstants.SupportedDataSources.POSTGRESQL_DB)) {
                return PersistToolsConstants.SqlTypes.BYTEA;
            }
            return PersistToolsConstants.SqlTypes.LONG_BLOB;
        }
        throw new BalException("couldn't find equivalent SQL type for the field type: " + field);
    }

    private static String getEnumType(Enum enumValue, String fieldName, String datasource) {
        if (datasource.equals(PersistToolsConstants.SupportedDataSources.MSSQL_DB) ||
            datasource.equals(PersistToolsConstants.SupportedDataSources.POSTGRESQL_DB)) {
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
        List<String> tableOrder = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : referenceTables.entrySet()) {
            if (tableOrder.isEmpty()) {
                tableOrder.add(removeSingleQuote(entry.getKey()));
            } else {
                int firstIndex = 0;
                List<String> referenceTableNames = referenceTables.get(entry.getKey());
                for (String referenceTableName: referenceTableNames) {
                    int index = tableOrder.indexOf(referenceTableName);
                    if ((firstIndex == 0 || index > firstIndex) && index >= 0) {
                        firstIndex = index + 1;
                    }
                }
                tableOrder.add(firstIndex, removeSingleQuote(entry.getKey()));
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
            List<String> script =  tableScripts.get(removeSingleQuote(tableOrder.get(size - (i + 1))));
            tableScriptsInOrder[i] = script.get(0);
            tableScriptsInOrder[length - (i + 1)] = script.get(1);
        }
        return tableScriptsInOrder;
    }

    private static String escape(String name, String datasource) {
        if (datasource.equals(PersistToolsConstants.SupportedDataSources.MSSQL_DB)) {
            return "[" + name + "]";
        }
        if (datasource.equals(PersistToolsConstants.SupportedDataSources.POSTGRESQL_DB)) {
            return "\"" + name + "\"";
        }
        return "`" + name + "`";
    }
}
