/*
 *  Copyright (c) 2024 WSO2 LLC. (http://www.wso2.com).
 *
 *  WSO2 LLC. licenses this file to you under the Apache License,
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

public class PostgreSqlIntrospector extends Introspector {

    public PostgreSqlIntrospector(Connection connection, String databaseName) {
        super(connection, databaseName);
    }

    @Override
    public String getTablesQuery() {
        String formatQuery = """
            SELECT
                tbl.relname AS table_name
            FROM pg_class AS tbl
                INNER JOIN pg_namespace AS namespace ON namespace.oid = tbl.relnamespace
            WHERE
                tbl.relkind = 'r' AND namespace.nspname = 'public'
                ORDER BY table_name;
            """;
        formatQuery = formatQuery.replace("\r\n", "%n");
        return formatQuery;
    }

    @Override
    public String getColumnsQuery(String tableName) {
        String formatQuery = """
            SELECT
                info.table_name AS table_name,
                info.column_name AS column_name,
                format_type(att.atttypid, att.atttypmod) AS formatted_type,
                info.numeric_precision AS numeric_precision,
                info.numeric_scale AS numeric_scale,
                info.numeric_precision_radix,
                info.datetime_precision AS datetime_precision,
                info.data_type AS data_type,
                info.udt_schema AS type_schema_name,
                info.udt_name AS full_data_type,
                pg_get_expr(attdef.adbin, attdef.adrelid) AS column_default,
                info.is_nullable AS is_nullable,
                CASE
                    WHEN info.column_name IN (
                        SELECT columns.column_name
                        FROM
                            information_schema.table_constraints AS constraints
                        JOIN
                            information_schema.constraint_column_usage AS columns
                        ON
                            columns.constraint_name = constraints.constraint_name
                        WHERE
                            constraints.constraint_type = 'PRIMARY KEY' AND
                            columns.table_schema = constraints.table_schema AND
                            columns.table_schema = 'public' AND
                            columns.table_name = constraints.table_name AND
                            columns.table_name = '%s'
                        )
                    THEN 'PRI'
                    ELSE 'NO'
                END AS column_key,
                info.character_maximum_length AS character_maximum_length,
                col_description(att.attrelid, ordinal_position) AS column_comment,
                CASE
                    WHEN pg_get_expr(attdef.adbin, attdef.adrelid) LIKE 'nextval(%'
                    AND pg_get_serial_sequence(info.table_name, info.column_name) IS NOT NULL
                    AND pg_get_serial_sequence(info.table_name, info.column_name) LIKE 'public.%'
                    AND (
                        SELECT increment_by FROM pg_sequences
                        WHERE
                            sequencename =
                                TRIM('"' FROM
                                SPLIT_PART(pg_get_serial_sequence(info.table_name, info.column_name), '.', 2))
                    ) = 1 THEN 1
                    ELSE 0
                END AS dbGenerated
                FROM information_schema.columns info
                JOIN pg_attribute att ON att.attname = info.column_name
                JOIN (
                    SELECT pg_class.oid, relname, pg_namespace.nspname as namespace
                    FROM pg_class
                    JOIN pg_namespace on pg_namespace.oid = pg_class.relnamespace
                    AND pg_namespace.nspname = 'public' WHERE reltype > 0
                ) as oid on oid.oid = att.attrelid
                AND relname = info.table_name
                AND namespace = info.table_schema
                LEFT OUTER JOIN pg_attrdef attdef
                ON attdef.adrelid = att.attrelid AND attdef.adnum = att.attnum AND table_schema = namespace
                WHERE table_schema = 'public' AND table_name = '%s'
                ORDER BY namespace, table_name, ordinal_position;
            """;
        formatQuery = formatQuery.replace("\r\n", "%n");
        return String.format(formatQuery, tableName, tableName);
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
