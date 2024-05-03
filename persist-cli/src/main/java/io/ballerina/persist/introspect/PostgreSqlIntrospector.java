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

import io.ballerina.persist.introspectiondto.SqlColumn;
import io.ballerina.persist.models.SqlType;
import io.ballerina.persist.utils.DatabaseConnector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.ballerina.persist.PersistToolsConstants.POSTGRESQL_DRIVER_CLASS;
import static io.ballerina.persist.nodegenerator.syntax.constants.BalSyntaxConstants.JDBC_URL_WITH_DATABASE_POSTGRESQL;

public class PostgreSqlIntrospector extends Introspector {

    public PostgreSqlIntrospector() {
        databaseConnector = new DatabaseConnector(JDBC_URL_WITH_DATABASE_POSTGRESQL, POSTGRESQL_DRIVER_CLASS);
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
                UPPER(format_type(att.atttypid, att.atttypmod)) AS full_data_type,
                info.numeric_precision AS numeric_precision,
                info.numeric_scale AS numeric_scale,
                info.numeric_precision_radix,
                info.datetime_precision AS datetime_precision,
                info.udt_schema AS type_schema_name,
                UPPER(info.udt_name) AS data_type,
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
                    WHEN pg_get_expr(attdef.adbin, attdef.adrelid) IS NOT NULL
                    AND pg_get_expr(attdef.adbin, attdef.adrelid) LIKE 'nextval(%%'
                    THEN 1
                    ELSE 0
                END AS dbGenerated,
                pg_get_constraintdef(con.oid) AS check_constraint
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
                LEFT OUTER JOIN pg_catalog.pg_constraint con
                ON con.conrelid = att.attrelid AND att.attnum = ANY(con.conkey) AND con.contype = 'c'
                WHERE table_schema = 'public' AND table_name = '%s'
                ORDER BY ordinal_position;
            """;
        formatQuery = formatQuery.replace("\r\n", "%n");
        return String.format(formatQuery, tableName, tableName);
    }

    @Override
    public String getIndexesQuery(String tableName) {
        String formatQuery = """
            WITH rawindex AS (
                SELECT
                    indrelid,
                    indexrelid,
                    indisunique,
                    indisprimary,
                    unnest(indkey) AS indkeyid,
                    generate_subscripts(indkey, 1) AS indkeyidx,
                    unnest(indclass) AS indclass,
                    unnest(indoption) AS indoption
                FROM pg_index
                WHERE
                    indpred IS NULL
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
            WHERE
                schemainfo.nspname = 'public' AND
                rawindex.indisprimary = false AND
                tableinfo.relname = '%s'
            ORDER BY index_name;
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
                rel.relname AS table_name,
                a.attname AS column_name,
                pg_get_constraintdef(con.oid) AS full_enum_type
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
            """;
        formatQuery = formatQuery.replace("\r\n", "%n");
        return formatQuery;
    }

    @Override
    protected boolean isEnumType(SqlColumn column) {
        if (Objects.isNull(column.getCheckConstraint())) {
            return false;
        }
        Pattern pattern = Pattern.compile("^CHECK \\(\\(\\(\"?(\\w+(\\s+\\w+)*)\"?\\)::text = ANY \\(\\(" +
                "ARRAY\\['(\\w+(\\s+\\w+)*)'::character varying(, '(\\w+(\\s+\\w+)*)'::character varying)*\\]\\)::" +
                "text\\[\\]\\)\\)\\)$");
        return pattern.matcher(column.getCheckConstraint()).find();
    }

    @Override
    protected List<String> extractEnumValues(String enumString) {
        List<String> enumValues = new ArrayList<>();
        // input ->
        // CHECK (((gender)::text = ANY ((ARRAY['MALE'::character varying, 'FEMALE'::character varying])::text[])))
        enumString = enumString.substring(5);
        Pattern pattern = Pattern.compile("ARRAY\\[(.*?)]");
        Matcher matcher = pattern.matcher(enumString);

        if (matcher.find()) {
            // Group 1 contains the values inside ARRAY []
            String valuesInArray = matcher.group(1);
            // Split the values by comma
            String[] valuesArray = valuesInArray.split(",");
            // 'MALE'::character varying
            Arrays.stream(valuesArray).map(value -> {
                value = value.split("::")[0];
                value = value.trim();
                value = value.replace("'", "");
                return value.trim();
            }).forEach(enumValues::add);
        }

        return enumValues;
    }

    @Override
    protected String getBalType(SqlType sqlType) {
        return getBalTypeForCommonDataTypes(sqlType);
    }

}
