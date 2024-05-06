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

import io.ballerina.persist.PersistToolsConstants;
import io.ballerina.persist.introspectiondto.SqlColumn;
import io.ballerina.persist.models.SqlType;
import io.ballerina.persist.utils.DatabaseConnector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.ballerina.persist.PersistToolsConstants.MSSQL_DRIVER_CLASS;
import static io.ballerina.persist.nodegenerator.syntax.constants.BalSyntaxConstants.JDBC_URL_WITH_DATABASE_MSSQL;

public class MsSqlInstrospector extends Introspector {

    public MsSqlInstrospector() {
        databaseConnector = new DatabaseConnector(JDBC_URL_WITH_DATABASE_MSSQL, MSSQL_DRIVER_CLASS);
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
                IIF(COLUMNPROPERTY(c.object_id, c.name, 'charmaxlen') = -1, 0,
                    COLUMNPROPERTY(c.object_id, c.name, 'charmaxlen'))  AS character_maximum_length,
                OBJECT_DEFINITION(c.default_object_id) AS column_default,
                IIF(c.is_nullable = 1, 'YES', 'NO') AS is_nullable,
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
              AND OBJECT_NAME(c.object_id) = '%s'
            ORDER BY
                table_name, COLUMNPROPERTY(c.object_id, c.name, 'ordinal');
            """;
        formatQuery = formatQuery.replace("\r\n", "%n");
        return String.format(formatQuery, tableName);
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
              AND t.name = '%s'
            ORDER BY table_name, index_name, seq_in_index;
            """;
        formatQuery = formatQuery.replace("\r\n", "%n");
        return String.format(formatQuery, tableName);
    }

    @Override
    public String getForeignKeysQuery(String tableName) {
        String formatQuery = """
                SELECT OBJECT_NAME(fkc.constraint_object_id) AS constraint_name,
                       parent_table.name                        AS table_name,
                       referenced_table.name                    AS referenced_table_name,
                       parent_column.name                       AS column_name,
                       referenced_column.name                   AS referenced_column_name,
                       fk.delete_referential_action             AS delete_rule,
                       fk.update_referential_action             AS update_rule,
                       fkc.constraint_column_id                 AS ordinal_position
                FROM sys.foreign_key_columns AS fkc
                         INNER JOIN sys.tables AS parent_table
                                    ON fkc.parent_object_id = parent_table.object_id
                         INNER JOIN sys.tables AS referenced_table
                                    ON fkc.referenced_object_id = referenced_table.object_id
                         INNER JOIN sys.columns AS parent_column
                                    ON fkc.parent_object_id = parent_column.object_id
                                        AND fkc.parent_column_id = parent_column.column_id
                         INNER JOIN sys.columns AS referenced_column
                                    ON fkc.referenced_object_id = referenced_column.object_id
                                        AND fkc.referenced_column_id = referenced_column.column_id
                         INNER JOIN sys.foreign_keys AS fk
                                    ON fkc.constraint_object_id = fk.object_id
                                        AND fkc.parent_object_id = fk.parent_object_id
                WHERE parent_table.is_ms_shipped = 0
                  AND referenced_table.is_ms_shipped = 0
                  AND parent_table.name = '%s'
                ORDER BY table_name, ordinal_position;
                """;
        formatQuery = formatQuery.replace("\r\n", "%n");
        return String.format(formatQuery, tableName);
    }

    @Override
    protected String getEnumsQuery() {
        String formatQuery = """
            SELECT
                OBJECT_NAME(parent_object_id) AS table_name,
                COL_NAME(parent_object_id, parent_column_id) AS column_name,
                definition AS full_enum_type
            FROM sys.check_constraints
            WHERE definition LIKE '%OR%'
                AND definition LIKE '%=%'
                AND definition NOT LIKE '%AND%'
                AND definition NOT LIKE '%>%'
                AND definition NOT LIKE '%<%';
            """;
        formatQuery = formatQuery.replace("\r\n", "%n");
        return formatQuery;
    }

    @Override
    protected boolean isEnumType(SqlColumn column) {
        if (Objects.isNull(column.getCheckConstraint())) {
            return false;
        }
        Pattern pattern = Pattern.compile("^\\((\\[(\\w+(\\s+\\w+){0,3})]='(\\w+(\\s+\\w+){0,3})')(\\sOR\\s" +
                "\\[(\\w+(\\s+\\w+){0,3})]='(\\w+(\\s+\\w+){0,3})')*\\)$");
        return pattern.matcher(column.getCheckConstraint()).find();
    }

    protected List<String> extractEnumValues(String enumString) {
        //expected input ->
        // ([status]='ENDED' OR [status]='STARTED' OR [status]='SCHEDULED')
        List<String> enumValues = new ArrayList<>();

        // Using regex to extract values inside parentheses
        Pattern pattern = Pattern.compile("\\((.*?)\\)");
        Matcher matcher = pattern.matcher(enumString);

        if (matcher.find()) {
            // Group 1 contains the values inside parentheses
            String valuesInsideParentheses = matcher.group(1);

            // Split the values by comma
            String[] valuesArray = valuesInsideParentheses.split("OR");
            Arrays.stream(valuesArray).map(value -> {
                //you get -> [status]='ENDED'
                String[] splitValue =  value.split("=");
                return splitValue[1].replace("'", "").trim();
            }).forEach(enumValues::add);
        }
        return enumValues;
    }

    protected String getBalType(SqlType sqlType) {
        if (Objects.equals(sqlType.getTypeName(), PersistToolsConstants.SqlTypes.BIT)) {
            return PersistToolsConstants.BallerinaTypes.BOOLEAN;
        }
        return getBalTypeForCommonDataTypes(sqlType);
    }

}
