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
            WITH rawindex AS (
                SELECT
                    indrelid,\s
                    indexrelid,
                    indisunique,
                    indisprimary,
                    unnest(indkey) AS indkeyid,
                    generate_subscripts(indkey, 1) AS indkeyidx,
                    unnest(indclass) AS indclass,
                    unnest(indoption) AS indoption
                FROM pg_index\s
                WHERE
                    indpred IS NULL\s
                    AND NOT indisexclusion
            )
            SELECT
                indexinfo.relname AS index_name,
                tableinfo.relname AS table_name,
                columninfo.attname AS column_name,
                rawindex.indisunique AS is_unique,
                rawindex.indkeyidx AS seq_in_index,
                CASE rawindex.indoption & 1
                    WHEN 1 THEN 'DESC'
                    ELSE 'ASC' END
                    AS column_order
            FROM
                rawindex
                INNER JOIN pg_class AS tableinfo ON tableinfo.oid = rawindex.indrelid
                INNER JOIN pg_class AS indexinfo ON indexinfo.oid = rawindex.indexrelid
                INNER JOIN pg_namespace AS schemainfo ON schemainfo.oid = tableinfo.relnamespace
                LEFT JOIN pg_attribute AS columninfo
                    ON columninfo.attrelid = tableinfo.oid AND columninfo.attnum = rawindex.indkeyid
                INNER JOIN pg_am AS indexaccess ON indexaccess.oid = indexinfo.relam
                LEFT JOIN pg_opclass AS opclass
                    ON opclass.oid = rawindex.indclass
                LEFT JOIN pg_constraint pc ON rawindex.indexrelid = pc.conindid AND pc.contype <> 'f'
            WHERE\s
                schemainfo.nspname = 'public' AND
                rawindex.indisprimary = false
            ORDER BY table_name, index_name;
            """;
        formatQuery = formatQuery.replace("\r\n", "%n");
        return String.format(formatQuery, this.databaseName, tableName);
    }

    @Override
    public String getForeignKeysQuery(String tableName) {
        String formatQuery = """
                SELECT
                    table_name,
                    att2.attname    AS column_name,
                    cl.relname      AS referenced_table_name,
                    att.attname     AS referenced_column_name,
                    conname         AS constraint_name,
                    NULL            AS update_rule,
                    NULL            AS delete_rule
                FROM (SELECT\s
                            ns.nspname AS "namespace",
                            unnest(con1.conkey)                AS parent,
                            unnest(con1.confkey)                AS child,
                            cl.relname                          AS table_name,
                            generate_subscripts(con1.conkey, 1) AS colidx,
                            con1.confrelid,
                            con1.conrelid,
                            con1.conname
                    FROM pg_class cl
                            join pg_constraint con1 on con1.conrelid = cl.oid
                            join pg_namespace ns on cl.relnamespace = ns.oid
                    WHERE
                        ns.nspname = 'public'
                        and con1.contype = 'f'
                    ORDER BY colidx
                    ) con
                        JOIN pg_attribute att on att.attrelid = con.confrelid and att.attnum = con.child
                        JOIN pg_class cl on cl.oid = con.confrelid
                        JOIN pg_attribute att2 on att2.attrelid = con.conrelid and att2.attnum = con.parent
                WHERE table_name = '%s'
                ORDER BY table_name, constraint_name, con.colidx;
                """;
        formatQuery = formatQuery.replace("\r\n", "%n");
        return String.format(formatQuery, tableName);
    }

    @Override
    protected String getEnumsQuery() {
        String formatQuery = """
            SELECT
                subquery.relname AS table_name,
                subquery.attname AS column_name,
                'enum(' || string_agg(quote_literal(match[1]), ',') || ')' AS full_enum_type
            FROM (
                SELECT
                    con.oid,
                    rel.relname,
                    a.attname,
                    regexp_matches(pg_get_constraintdef(con.oid),'''([A-Z]+)''','g') AS match
                FROM
                    pg_catalog.pg_constraint con
                INNER JOIN
                    pg_catalog.pg_class rel ON rel.oid = con.conrelid
                INNER JOIN
                    pg_catalog.pg_namespace nsp ON nsp.oid = connamespace
                INNER JOIN
                    pg_catalog.pg_attribute a ON a.attrelid = rel.oid AND a.attnum = ANY(con.conkey)
                WHERE
                    nsp.nspname = 'public'
                    AND con.contype = 'c'
                    AND pg_get_constraintdef(con.oid) LIKE '%ANY ((ARRAY[%::text[]%'
            ) AS subquery
            GROUP BY subquery.oid, subquery.relname, subquery.attname;
            """;
        formatQuery = formatQuery.replace("\r\n", "%n");
        return formatQuery;
    }

}
