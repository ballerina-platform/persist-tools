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

import io.ballerina.persist.introspectiondto.SQLTable;
import io.ballerina.persist.models.Entity;
import io.ballerina.persist.models.EntityField;
import io.ballerina.persist.models.Index;
import io.ballerina.persist.models.Relation;
import io.ballerina.persist.models.SQLType;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;




/**
 * Database Introspector class.
 *
 *
 */
public abstract class Introspector {

    protected final Connection connection;

    private final PrintStream errStream = System.err;
    protected String databaseName;
    protected abstract String getTablesQuery();
    protected abstract String getColumnsQuery(String tableName);
    protected abstract String getConstraintsQuery();
    protected abstract String getIndexesQuery(String tableName);
    protected abstract String getForeignKeysQuery(String tableName);

    public Introspector(Connection connection, String databaseName) {
        this.connection = connection;
        this.databaseName = databaseName;
    }

    public List<Entity> introspectDatabase() throws SQLException {
        ScriptRunner sr = new ScriptRunner(connection);
        List<SQLTable> tables = sr.getSQLTables(this.getTablesQuery());
        tables.forEach(table -> {
            try {
                sr.readColumnsOfSQLTable(table, this.getColumnsQuery(table.getTableName()));
                sr.readForeignKeysOfSQLTable(table, this.getForeignKeysQuery(table.getTableName()));
                sr.readIndexesOfSQLTable(table, this.getIndexesQuery(table.getTableName()));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        tables.forEach(table -> {
            errStream.println("Indexes found: " + table.getIndexes().size());
            table.getIndexes().forEach(index -> {
                errStream.println();
                errStream.println("Table Name: " + index.getTableName());
                errStream.println("Index Name: " + index.getIndexName());
                errStream.println();
                index.getColumnNames().forEach(column -> errStream.print(column + ", "));
                errStream.println();
                errStream.println("Index Non Unique: " + index.getNonUnique());
                errStream.println("Index Partial: " + index.getPartial());
                errStream.println("Index Type: " + index.getIndexType());
                errStream.println("Index Column Order: " + index.getColumnOrder());
            });
        });
        Map<String, Entity> entityMap = new HashMap<>();
        tables.forEach(table -> {
            Entity.Builder entityBuilder = Entity.newBuilder(table.getTableName());
            List<EntityField> keys = new ArrayList<>();
            List<EntityField> fields = new ArrayList<>();
            table.getColumns().forEach(column -> {
                EntityField.Builder fieldBuilder = EntityField.newBuilder(column.getColumnName());
                SQLType sqlType = new SQLType(
                        column.getDataType(),
                        column.getColumnDefault(),
                        column.getNumericPrecision(),
                        column.getNumericScale(),
                        column.getDatetimePrecision(),
                        column.getCharacterMaximumLength() != null ? parseInt(column.getCharacterMaximumLength()) : 0
                );

                fieldBuilder.setType(column.getDataType());
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
                Index index = new Index(sqlIndex.getIndexName(), fields, sqlIndex.getNonUnique().equals("0"));
                if (index.isUnique()) {
                    entityBuilder.addUniqueIndex(index);
                } else {
                    entityBuilder.addIndex(index);
                }
            });
            entityBuilder.setKeys(keys);
            entityMap.put(table.getTableName(), entityBuilder.build());
        });

        tables.forEach(table-> {
            table.getForeignKeys().forEach(foreignKey -> {
                Entity entity = entityMap.get(table.getTableName());
                Entity assocEntity = entityMap.get(foreignKey.getReferencedTableName());
                List<EntityField> ownerColumns = new ArrayList<>();
                foreignKey.getColumnNames().forEach(columnName -> {
                    ownerColumns.add(entity.getFieldByName(columnName));
                });
                List<EntityField> referencedColumns = new ArrayList<>();
                foreignKey.getReferencedColumnNames().forEach(columnName -> {
                    referencedColumns.add(assocEntity.getFieldByName(columnName));
                });
                Relation.Builder ownerRelBuilder = new Relation.Builder();
                Relation.Builder referenceRelBuilder = new Relation.Builder();

                ownerRelBuilder.setOwner(true);
                ownerRelBuilder.setAssocEntity(assocEntity);
                if (entity.getKeys().contains(ownerColumns.get(0))) {
                    ownerRelBuilder.setRelationType(Relation.RelationType.MANY);
                    // add indexes unique as well
                } else {
                    ownerRelBuilder.setRelationType(Relation.RelationType.ONE);
                }
                ownerRelBuilder.setRelationType(Relation.RelationType.ONE);
                ownerRelBuilder.setReferences(referencedColumns.stream().map(EntityField::getFieldName).toList());
                ownerRelBuilder.setKeys(ownerColumns.stream().map(entityField ->
                        new Relation.Key(entityField.getFieldName(), referencedColumns
                        .get(ownerColumns.indexOf(entityField)).getFieldName(),
                                entityField.getSqlType().getTypeName())).toList());
                Relation ownerRelation = ownerRelBuilder.build();
                ownerColumns.forEach(entityField -> entityField.setRelation(ownerRelation));

                referenceRelBuilder.setOwner(false);
                referenceRelBuilder.setAssocEntity(entity);
                referenceRelBuilder.setRelationType(Relation.RelationType.ONE);
                referenceRelBuilder.setKeys(referencedColumns.stream().map(entityField ->
                        new Relation.Key(entityField.getFieldName(), ownerColumns
                        .get(referencedColumns.indexOf(entityField)).getFieldName(),
                                entityField.getSqlType().getTypeName())).toList());
                referenceRelBuilder.setReferences(ownerColumns.stream().map(EntityField::getFieldName).toList());
                Relation referenceRelation = referenceRelBuilder.build();
                referencedColumns.forEach(entityField -> entityField.setRelation(referenceRelation));
            });
        });

        errStream.println("Entities found: " + entityMap.size());
        entityMap.forEach((entityName, entity) -> {
            errStream.println();
            errStream.println(entity.getEntityName());
            errStream.println();
            errStream.println("Fields:");
            entity.getFields().forEach(field -> errStream.println(field.getFieldName() + "\t\t" +
                    field.getFieldType() + "\t\tOptional: " + field.isOptionalType() +
                    "\t\tArray: " + field.isArrayType()));
            errStream.println();
            errStream.println("Keys:");
            entity.getKeys().forEach(key -> errStream.println(key.getFieldName()));
        });

        return entityMap.values().stream().toList();
    }


}
