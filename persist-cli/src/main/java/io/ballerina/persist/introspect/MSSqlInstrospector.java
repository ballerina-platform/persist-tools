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

import io.ballerina.persist.models.SQLType;
import io.ballerina.persist.utils.DatabaseConnector;

import static io.ballerina.persist.PersistToolsConstants.POSTGRESQL_DRIVER_CLASS;
import static io.ballerina.persist.nodegenerator.syntax.constants.BalSyntaxConstants.JDBC_URL_WITH_DATABASE_POSTGRESQL;

public class MSSqlInstrospector extends Introspector {

    public MSSqlInstrospector() {
        databaseConnector = new DatabaseConnector(JDBC_URL_WITH_DATABASE_POSTGRESQL, POSTGRESQL_DRIVER_CLASS);
    }

    @Override
    public String getTablesQuery() {
        String formatQuery = """
            SELECT
                tbl.name AS table_name
            FROM sys.tables tbl
            WHERE tbl.is_ms_shipped = 0 AND tbl.type = 'U'
            ORDER BY tbl.name;
            """;
        formatQuery = formatQuery.replace("\r\n", "%n");
        return formatQuery;
    }

    @Override
    public String getColumnsQuery(String tableName) {
        String formatQuery = """
            SELECT
                c.name AS column_name,
                CASE typ.is_assembly_type
                    WHEN 1 THEN UPPER(TYPE_NAME(c.user_type_id))
                    ELSE UPPER(TYPE_NAME(c.system_type_id))
                    END AS data_type,
                CASE
                    WHEN c.system_type_id IN (35, 99) THEN
                        UPPER(CONCAT(TYPE_NAME(c.user_type_id), '(', c.precision, ', ', c.scale, ')'))
                    WHEN c.system_type_id IN (48, 52, 56, 59, 60, 62, 106, 108, 122, 127) THEN
                        UPPER(CONCAT(TYPE_NAME(c.system_type_id), '(', c.precision, ')'))
                    WHEN c.system_type_id IN (35, 99) THEN
                        UPPER(CONCAT(TYPE_NAME(c.system_type_id), '(', c.precision, ', ', c.scale, ')'))
                    WHEN c.system_type_id IN (167, 175) THEN
                        UPPER(CONCAT(TYPE_NAME(c.system_type_id), '(', c.max_length, ')'))
                    ELSE
                        UPPER(TYPE_NAME(c.system_type_id))
                    END AS full_data_type,
                COLUMNPROPERTY(c.object_id, c.name, 'charmaxlen') AS character_maximum_length,
                OBJECT_DEFINITION(c.default_object_id) AS column_default,
                c.is_nullable AS is_nullable,
                COLUMNPROPERTY(c.object_id, c.name, 'IsIdentity') AS dbGenerated,
                OBJECT_NAME(c.object_id) AS table_name,
                CONVERT(TINYINT, CASE
                                     WHEN c.system_type_id IN (48, 52, 56, 59, 60, 62, 106, 108, 122, 127) 
                                     THEN c.precision
                                 END) AS numeric_precision,
                CONVERT(INT, CASE
                                 WHEN c.system_type_id IN (40, 41, 42, 43, 58, 61) THEN NULL
                                 ELSE ODBCSCALE(c.system_type_id, c.scale) END) AS numeric_scale,
                OBJECT_SCHEMA_NAME(c.object_id) AS namespace,
                cc.definition AS check_constraint,
                CASE
                    WHEN (SELECT DISTINCT
                        count(*)
                    FROM
                        sys.indexes ind
                            INNER JOIN sys.index_columns ic
                                       ON ind.object_id = ic.object_id AND ind.index_id = ic.index_id
                            INNER JOIN sys.columns col
                                       ON ic.object_id = col.object_id AND ic.column_id = col.column_id
                            INNER JOIN
                        sys.tables t ON ind.object_id = t.object_id
                    WHERE  t.name = OBJECT_NAME(c.object_id)
                      AND col.name = c.name
                      AND ind.is_primary_key = 1) = 1
                    THEN 'PRI'
                    ELSE 'NO'
                END AS column_key
            FROM
                sys.columns c
                    INNER JOIN sys.objects obj ON c.object_id = obj.object_id
                    INNER JOIN sys.types typ ON c.user_type_id = typ.user_type_id
                    LEFT JOIN sys.check_constraints cc ON c.object_id = cc.parent_object_id
                    AND c.column_id = cc.parent_column_id
            WHERE
                obj.is_ms_shipped = 0
              AND OBJECT_NAME(c.object_id) = 'Doctor'
            ORDER BY
                table_name, COLUMNPROPERTY(c.object_id, c.name, 'ordinal');
            """;
        formatQuery = formatQuery.replace("\r\n", "%n");
        return String.format(formatQuery, tableName, tableName);
    }

    @Override
    public String getIndexesQuery(String tableName) {
        String formatQuery = """
            SELECT DISTINCT
                ind.name AS index_name,
                ind.is_unique AS is_unique,
                ind.is_unique_constraint AS is_unique_constraint,
                col.name AS column_name,
                ic.key_ordinal AS seq_in_index,
                t.name AS table_name
            FROM
                sys.indexes ind
                    INNER JOIN sys.index_columns ic
                               ON ind.object_id = ic.object_id AND ind.index_id = ic.index_id
                    INNER JOIN sys.columns col
                               ON ic.object_id = col.object_id AND ic.column_id = col.column_id
                    INNER JOIN
                sys.tables t ON ind.object_id = t.object_id
            WHERE t.is_ms_shipped = 0
              AND ic.key_ordinal != 0
              AND ind.filter_definition IS NULL
              AND ind.is_primary_key = 0
              AND ind.name IS NOT NULL
              AND ind.type_desc IN (
                    'CLUSTERED',
                    'NONCLUSTERED',
                    'CLUSTERED COLUMNSTORE',
                    'NONCLUSTERED COLUMNSTORE'
                )
              AND t.name = 'patients'
            ORDER BY table_name, index_name, seq_in_index;
            """;
        formatQuery = formatQuery.replace("\r\n", "%n");
        return String.format(formatQuery, tableName);
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
                FROM (SELECT
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
                WHERE table_name = '%s';
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
                    AND pg_get_constraintdef(con.oid) LIKE '%%ANY ((ARRAY[%::text[]%%'
            ) AS subquery
            GROUP BY subquery.oid, subquery.relname, subquery.attname;
            """;
        formatQuery = formatQuery.replace("\r\n", "%n");
        return formatQuery;
    }

    protected String getBalType(SQLType sqlType) {
        return getBalTypeForCommonDataTypes(sqlType);
    }

}
