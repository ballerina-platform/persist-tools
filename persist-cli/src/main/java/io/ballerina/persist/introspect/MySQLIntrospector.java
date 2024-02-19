/*
 *  Copyright (c) 2022, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package io.ballerina.persist.introspect;


import java.sql.Connection;

public class MySQLIntrospector extends Introspector {

    public MySQLIntrospector(Connection connection, String databaseName, String moduleName) {
        super(connection, databaseName, moduleName);
    }

    @Override
    public String getTablesQuery() {
        String formatQuery = """
            SELECT DISTINCT
                table_info.table_name AS table_name,
                table_info.create_options AS create_options,
                table_info.table_comment AS table_comment
            FROM
                information_schema.tables AS table_info
                JOIN information_schema.columns AS column_info
                ON column_info.table_name = table_info.table_name
            WHERE
                table_info.table_schema= '%s' AND
                column_info.table_schema = '%s' AND
                table_info.table_type = 'BASE TABLE'
            ORDER BY
                table_info.table_name;
            """;
        formatQuery = formatQuery.replace("\r\n", "%n");
        return String.format(formatQuery, this.databaseName, this.databaseName);
    }
    @Override
    public String getColumnsQuery(String tableName) {
        String formatQuery = """
            SELECT
                column_name column_name,
                data_type data_type,
                column_type full_data_type,
                character_maximum_length character_maximum_length,
                numeric_precision numeric_precision,
                numeric_scale numeric_scale,
                datetime_precision datetime_precision,
                column_default column_default,
                is_nullable is_nullable,
                extra extra,
                table_name table_name,
                column_key column_key,
                IF(column_comment = '', NULL, column_comment) AS column_comment,
                IF(extra = 'auto_increment', 1, 0) AS dbgenerated
            FROM
                information_schema.columns
            WHERE
                table_schema = '%s'
                AND table_name = '%s'
            ORDER BY
                ordinal_position ASC;
            """;
        formatQuery = formatQuery.replace("\r\n", "%n");
        return String.format(formatQuery, this.databaseName, tableName);
    }

    @Override
    public String getConstraintsQuery() {
        return """
                SELECT
                    tc.table_schema AS namespace,
                    tc.table_name AS table_name,
                    tc.constraint_name AS constraint_name,
                    LOWER(tc.constraint_type) AS constraint_type,
                    cc.check_clause AS constraint_definition
                FROM
                    INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc
                    LEFT JOIN INFORMATION_SCHEMA.CHECK_CONSTRAINTS cc
                    ON cc.constraint_schema = tc.table_schema
                    AND cc.constraint_name = tc.constraint_name
                WHERE
                    constraint_type = 'CHECK'
                    AND tc.table_schema = ?
                    AND table_name = ?
                ORDER BY
                    namespace,
                    table_name,
                    constraint_type,
                    constraint_name;
                """;
    }

    @Override
    public String getIndexesQuery(String tableName) {
        String formatQuery = """
        SELECT
            table_name AS table_name,
            index_name AS index_name,
            column_name AS column_name,
            sub_part AS partial,
            seq_in_index AS seq_in_index,
            collation AS column_order,
            non_unique AS non_unique,
            index_type AS index_type
        FROM
            information_schema.statistics
        WHERE
            table_schema = '%s'
            AND table_name = '%s'
            AND index_name != 'PRIMARY'
        ORDER BY
            BINARY index_name,
            seq_in_index;
              """;
        formatQuery = formatQuery.replace("\r\n", "%n");
        return String.format(formatQuery, this.databaseName, tableName);
    }

    @Override
    public String getForeignKeysQuery(String tableName) {
        String formatQuery = """
                SELECT
                    kcu.constraint_name constraint_name,
                    kcu.column_name column_name,
                    kcu.referenced_table_name referenced_table_name,
                    kcu.referenced_column_name referenced_column_name,
                    kcu.ordinal_position ordinal_position,
                    kcu.table_name table_name,
                    rc.delete_rule delete_rule,
                    rc.update_rule update_rule
                FROM
                    information_schema.key_column_usage AS kcu
                    INNER JOIN information_schema.referential_constraints AS rc ON
                    BINARY kcu.constraint_name = BINARY rc.constraint_name
                WHERE
                    BINARY kcu.table_schema = '%s'
                    AND rc.constraint_schema = '%s'
                    AND kcu.table_name = '%s'
                    AND kcu.referenced_column_name IS NOT NULL
                ORDER BY
                    BINARY kcu.table_schema,
                    BINARY kcu.table_name,
                    BINARY kcu.constraint_name,
                kcu.ordinal_position;
                """;
        formatQuery = formatQuery.replace("\r\n", "%n");
        return String.format(formatQuery, this.databaseName, this.databaseName, tableName);
    }

    @Override
    protected String getEnumsQuery() {
        String formatQuery = """
            SELECT
                column_name column_name,
                data_type data_type,
                column_type full_enum_type,
                table_name table_name
            FROM
                information_schema.columns
            WHERE
                table_schema = '%s'
                AND data_type = 'enum'
            ORDER BY
                ordinal_position ASC;
            """;
        formatQuery = formatQuery.replace("\r\n", "%n");
        return String.format(formatQuery, this.databaseName);
    }

}
