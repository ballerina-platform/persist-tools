/*
 * Copyright (c) 2022, WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
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

import io.ballerina.persist.inflector.CaseConverter;
import io.ballerina.persist.inflector.Pluralizer;
import io.ballerina.persist.introspectiondto.SQLForeignKey;
import io.ballerina.persist.introspectiondto.SQLTable;
import io.ballerina.persist.models.Entity;
import io.ballerina.persist.models.EntityField;
import io.ballerina.persist.models.Index;
import io.ballerina.persist.models.Relation;
import io.ballerina.persist.models.SQLType;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.lang.Integer.parseInt;




/**
 * Database Introspector class.
 *
 *
 */
public abstract class Introspector {

    protected final Connection connection;

//    private final PrintStream errStream = System.err;
    protected String databaseName;
    protected abstract String getTablesQuery();
    protected abstract String getColumnsQuery(String tableName);
    protected abstract String getConstraintsQuery();
    protected abstract String getIndexesQuery(String tableName);
    protected abstract String getForeignKeysQuery(String tableName);

    private List<SQLTable> tables;

    private Map<String, Entity> entityMap;

    private List<SQLForeignKey> sqlForeignKeys;

    public Introspector(Connection connection, String databaseName) {
        this.connection = connection;
        this.databaseName = databaseName;
        this.tables = new ArrayList<>();
        this.entityMap = new HashMap<>();
        this.sqlForeignKeys = new ArrayList<>();
    }

    public Map<String, Entity> introspectDatabase() throws SQLException {
        ScriptRunner sr = new ScriptRunner(connection);
        this.tables = sr.getSQLTables(this.getTablesQuery());
        tables.forEach(table -> {
            try {
                sr.readColumnsOfSQLTable(table, this.getColumnsQuery(table.getTableName()));
                this.sqlForeignKeys.addAll(sr.readForeignKeysOfSQLTable
                        (table, this.getForeignKeysQuery(table.getTableName())));
                sr.readIndexesOfSQLTable(table, this.getIndexesQuery(table.getTableName()));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        mapEntities();

        //revise this. relationships are not correctly mapped
//        mapRelations();

        return Collections.unmodifiableMap(entityMap);
    }

    private void mapEntities() {
        Map<String, Entity.Builder> entityBuilderMap = new HashMap<>();
        tables.forEach(table -> {
            String entityName = CaseConverter.toSingularPascalCase(table.getTableName());

            Entity.Builder entityBuilder = Entity.newBuilder(entityName);
            entityBuilder.setResourceName(table.getTableName());
            List<EntityField> keys = new ArrayList<>();
            List<EntityField> fields = new ArrayList<>();
            table.getColumns().forEach(column -> {
                EntityField.Builder fieldBuilder = EntityField.newBuilder(
                        CaseConverter.toCamelCase(column.getColumnName()));
                fieldBuilder.setResourceFieldName(column.getColumnName());
                SQLType sqlType = new SQLType(
                        column.getDataType().toUpperCase(Locale.ENGLISH),
                        column.getColumnDefault(),
                        column.getNumericPrecision(),
                        column.getNumericScale(),
                        column.getDatetimePrecision(),
                        column.getCharacterMaximumLength() != null ? parseInt(column.getCharacterMaximumLength()) : 0
                );

                String balType = sqlType.getBalType();
                fieldBuilder.setType(balType);
                fieldBuilder.setOptionalType(column.getIsNullable().equals("YES"));
                fieldBuilder.setSqlType(sqlType);
                fieldBuilder.setArrayType(false);

                EntityField entityField = fieldBuilder.build();
                entityBuilder.addField(entityField);
                fields.add(entityField);
                if (column.getIsPrimaryKey()) {
                    keys.add(entityField);
                }
            });
            table.getIndexes().forEach(sqlIndex -> {
                List<EntityField> indexFields = new ArrayList<>();
                sqlIndex.getColumnNames().forEach(columnName -> fields.forEach(entityField -> {
                    if (entityField.getFieldResourceName().equals(columnName)) {
                        indexFields.add(entityField);
                    }
                }));
                Index index = new Index(sqlIndex.getIndexName(), indexFields, sqlIndex.getNonUnique().equals("0"));
                if (index.isUnique()) {
                    entityBuilder.addUniqueIndex(index);
                } else {
                    entityBuilder.addIndex(index);
                }
            });
            entityBuilder.setKeys(keys);
            entityBuilderMap.put(entityBuilder.getEntityName(), entityBuilder);
        });

        this.sqlForeignKeys.forEach(sqlForeignKey -> {
            Entity.Builder ownerEntityBuilder = entityBuilderMap
                    .get(CaseConverter.toSingularPascalCase(sqlForeignKey.getTableName()));
            Entity.Builder assocEntityBuilder = entityBuilderMap
                    .get(CaseConverter.toSingularPascalCase(sqlForeignKey.getReferencedTableName()));
            boolean isReferenceMany = inferRelationshipCardinality
                    (ownerEntityBuilder.buildForIntrospection(), sqlForeignKey)
                    == Relation.RelationType.MANY;
            EntityField.Builder assocField = EntityField
                    .newBuilder(
                            isReferenceMany ?
                                    Pluralizer.pluralize(ownerEntityBuilder.getEntityName().toLowerCase(Locale.ENGLISH))
                                    : ownerEntityBuilder.getEntityName().toLowerCase(Locale.ENGLISH)
                    );
            assocField.setType(ownerEntityBuilder.getEntityName());

            EntityField.Builder ownerField = EntityField
                    .newBuilder(assocEntityBuilder.getEntityName().toLowerCase(Locale.ENGLISH));
            ownerField.setType(assocEntityBuilder.getEntityName());

            assocField.setArrayType(isReferenceMany);

            assocEntityBuilder.addField(assocField.build());
            ownerEntityBuilder.addField(ownerField.build());
        });

        entityBuilderMap.forEach((key, value) -> entityMap.put(key, value.buildForIntrospection()));
    }

//    private void mapRelations() {
//
//        this.tables.forEach(table-> table.getSqlForeignKeys().forEach(foreignKey -> {
//            Entity ownerEntity = entityMap.get(table.getTableName());
//            Entity assocEntity = entityMap.get(foreignKey.getReferencedTableName());
//            List<EntityField> ownerColumns = new ArrayList<>();
//            foreignKey.getColumnNames().forEach(columnName ->
//                    ownerColumns.add(ownerEntity.getFieldByName(columnName)));
//            List<EntityField> referencedColumns = new ArrayList<>();
//            foreignKey.getReferencedColumnNames().forEach(columnName ->
//                    referencedColumns.add(assocEntity.getFieldByName(columnName)));
//            Relation.Builder ownerRelBuilder = new Relation.Builder();
//            Relation.Builder referenceRelBuilder = new Relation.Builder();
//
//            ownerRelBuilder.setOwner(true);
//            ownerRelBuilder.setAssocEntity(assocEntity);
//
//            ownerRelBuilder.setRelationType(inferRelationshipCardinality(ownerEntity, foreignKey));
//
//            ownerRelBuilder.setRelationType(Relation.RelationType.ONE);
//            ownerRelBuilder.setReferences(referencedColumns.stream().map(EntityField::getFieldName).toList());
//            ownerRelBuilder.setKeys(ownerColumns.stream().map(entityField ->
//                    new Relation.Key(entityField.getFieldName(), referencedColumns
//                            .get(ownerColumns.indexOf(entityField)).getFieldName(),
//                            entityField.getFieldType())).toList());
//            Relation ownerRelation = ownerRelBuilder.build();
//            ownerColumns.forEach(entityField -> entityField.setRelation(ownerRelation));
//
//            referenceRelBuilder.setOwner(false);
//            referenceRelBuilder.setAssocEntity(ownerEntity);
//            referenceRelBuilder.setRelationType(Relation.RelationType.ONE);
//            referenceRelBuilder.setKeys(referencedColumns.stream().map(entityField ->
//                    new Relation.Key(entityField.getFieldName(), ownerColumns
//                            .get(referencedColumns.indexOf(entityField)).getFieldName(),
//                            entityField.getFieldType())).toList());
//            referenceRelBuilder.setReferences(ownerColumns.stream().map(EntityField::getFieldName).toList());
//            Relation referenceRelation = referenceRelBuilder.build();
//            referencedColumns.forEach(entityField -> entityField.setRelation(referenceRelation));
//        }));
//    }

    private Relation.RelationType inferRelationshipCardinality(Entity ownerEntity, SQLForeignKey foreignKey) {
        List<EntityField> ownerColumns = new ArrayList<>();
        foreignKey.getColumnNames().forEach(columnName ->
                ownerColumns.add(ownerEntity.getFieldByName(columnName)));
        boolean isUniqueIndexPresent = ownerEntity.getUniqueIndexes().stream()
                .anyMatch(index -> areTwoFieldListsEqual(index.getFields(), ownerColumns));
        if (areTwoFieldListsEqual(ownerEntity.getKeys(), ownerColumns)) {
            return Relation.RelationType.ONE;
        } else if (isUniqueIndexPresent) {
            return Relation.RelationType.ONE;
        } else {
            return Relation.RelationType.MANY;
        }
    }

    private boolean areTwoFieldListsEqual(List<EntityField> list1, List<EntityField> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }
        for (EntityField entityField : list1) {
            if (!list2.contains(entityField)) {
                return false;
            }
        }
        return true;
    }
}
