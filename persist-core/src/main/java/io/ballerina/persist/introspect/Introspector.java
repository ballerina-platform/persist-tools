/*
 * Copyright (c) 2024 WSO2 LLC. (http://www.wso2.com).
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
package io.ballerina.persist.introspect;

import io.ballerina.persist.BalException;
import io.ballerina.persist.PersistToolsConstants;
import io.ballerina.persist.configuration.PersistConfiguration;
import io.ballerina.persist.inflector.CaseConverter;
import io.ballerina.persist.inflector.Pluralizer;
import io.ballerina.persist.introspectiondto.SqlColumn;
import io.ballerina.persist.introspectiondto.SqlEnum;
import io.ballerina.persist.introspectiondto.SqlForeignKey;
import io.ballerina.persist.introspectiondto.SqlTable;
import io.ballerina.persist.models.Entity;
import io.ballerina.persist.models.EntityField;
import io.ballerina.persist.models.Enum;
import io.ballerina.persist.models.EnumMember;
import io.ballerina.persist.models.Index;
import io.ballerina.persist.models.Module;
import io.ballerina.persist.models.Relation;
import io.ballerina.persist.models.SqlType;
import io.ballerina.persist.nodegenerator.DriverResolver;
import io.ballerina.persist.utils.DatabaseConnector;
import io.ballerina.persist.utils.JdbcDriverLoader;
import io.ballerina.persist.utils.ScriptRunner;
import io.ballerina.projects.Project;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.lang.Integer.parseInt;
import static java.lang.Integer.parseUnsignedInt;

/**
 * Database Introspector class.
 *
 *
 */
public abstract class Introspector {

    protected DatabaseConnector databaseConnector;
    public final PersistConfiguration persistConfiguration;

    protected abstract String getTablesQuery();

    protected abstract String getColumnsQuery(String tableName);

    protected abstract String getIndexesQuery(String tableName);

    protected abstract String getForeignKeysQuery(String tableName);

    protected abstract String getEnumsQuery();

    protected abstract String getBalType(SqlType sqlType);

    protected abstract boolean isEnumType(SqlColumn column);

    protected abstract List<String> extractEnumValues(String enumString);

    private List<SqlTable> tables;
    private List<SqlEnum> sqlEnums;
    private final Module.Builder moduleBuilder;
    private final Map<String, Entity> entityMap;
    private final List<SqlForeignKey> sqlForeignKeys;
    protected PrintStream errStream = System.err;

    protected Introspector(PersistConfiguration persistConfiguration) {
        this.persistConfiguration = persistConfiguration;
        this.tables = new ArrayList<>();
        this.entityMap = new HashMap<>();
        this.sqlForeignKeys = new ArrayList<>();
        this.sqlEnums = new ArrayList<>();
        this.moduleBuilder = Module.newBuilder("db");
    }

    /**
     * Returns the current persist configuration.
     *
     * @return the PersistConfiguration object
     */
    public PersistConfiguration getPersistConfiguration() {
        return persistConfiguration;
    }

    /**
     * Introspects a database and generates a complete Ballerina persist module.
     * This method connects to the database, reads the schema including tables,
     * columns,
     * indexes, foreign keys, and enums, then maps them to Ballerina entities and
     * types.
     *
     * @return a Module object containing all entities, enums, and relationships
     *         mapped from the database schema
     * @throws BalException if there is an error connecting to the database, reading
     *                      the schema,
     *                      or mapping database structures to Ballerina types
     */
    public Module introspectDatabase() throws BalException {
        DriverResolver driverResolver = new DriverResolver(this.persistConfiguration.getProvider());
        try {
            Project driverProject = driverResolver.resolveDriverDependencies();
            try (Connection connection = prepareDatabaseConnection(driverProject)) {
                readDatabaseSchema(connection);
            } catch (SQLException e) {
                throw new BalException("failed to read database schema: " + e.getMessage());
            }
            mapDatabaseSchemaToModule();
            return moduleBuilder.build();
        } finally {
            driverResolver.deleteDriverFile();
        }
    }

    /**
     * Introspects the database and returns an array of available table names.
     * This method can be used by other libraries to discover what tables exist in a
     * database
     * without performing a full introspection and model generation.
     *
     * @return an array of table names found in the database
     * @throws BalException if there is an error connecting to the database or
     *                      reading table information
     */
    public String[] getAvailableTables() throws BalException {
        DriverResolver driverResolver = new DriverResolver(this.persistConfiguration.getProvider());
        try {
            Project driverProject = driverResolver.resolveDriverDependencies();
            try (Connection connection = prepareDatabaseConnection(driverProject)) {
                ScriptRunner sr = new ScriptRunner(connection);
                List<SqlTable> availableTables = sr.getSQLTables(this.getTablesQuery());
                return availableTables.stream()
                        .map(SqlTable::getTableName)
                        .toArray(String[]::new);
            } catch (SQLException e) {
                throw new BalException("failed to read available tables: " + e.getMessage());
            }
        } finally {
            driverResolver.deleteDriverFile();
        }
    }

    private Connection prepareDatabaseConnection(Project driverProject) throws BalException {
        JdbcDriverLoader driverLoader;
        driverLoader = databaseConnector.getJdbcDriverLoader(driverProject);
        Driver driver = databaseConnector.getJdbcDriver(driverLoader);
        try {
            return databaseConnector.getConnection(driver, persistConfiguration, true);
        } catch (SQLException e) {
            throw new BalException("failed to connect to the database: " + e.getMessage());
        }
    }

    /**
     * Reads the database schema by querying tables, columns, indexes, foreign keys,
     * and enums.
     * If specific tables are configured in persistConfigurations, only those tables
     * are processed.
     * Warnings are issued for any specified tables that are not found in the
     * database.
     * Automatically includes referenced tables when foreign key relationships are
     * detected.
     *
     * @param connection the active database connection to use for querying schema
     *                   information
     * @throws SQLException if there is an error executing queries or reading result
     *                      sets
     */
    public void readDatabaseSchema(Connection connection) throws SQLException {
        ScriptRunner sr = new ScriptRunner(connection);
        this.tables = sr.getSQLTables(this.getTablesQuery());

        // Filter tables if specific tables are selected
        if (persistConfiguration.getSelectedTables() != null &&
                !persistConfiguration.getSelectedTables().isEmpty()) {
            List<String> selectedTableNames = new ArrayList<>(persistConfiguration.getSelectedTables());

            // Resolve foreign key dependencies - automatically include referenced tables
            List<String> resolvedTableNames = resolveForeignKeyDependencies(sr, selectedTableNames);

            this.tables = this.tables.stream()
                    .filter(table -> resolvedTableNames.contains(table.getTableName()))
                    .collect(java.util.stream.Collectors.toList());

            // Warn about tables that were specified but not found
            List<String> foundTableNames = this.tables.stream()
                    .map(SqlTable::getTableName)
                    .collect(java.util.stream.Collectors.toList());
            List<String> notFoundTables = selectedTableNames.stream()
                    .filter(name -> !foundTableNames.contains(name))
                    .collect(java.util.stream.Collectors.toList());
            if (!notFoundTables.isEmpty()) {
                errStream.println("WARNING: The following specified tables were not found in the database: " +
                        String.join(", ", notFoundTables));
            }
        }

        this.sqlEnums = sr.getSQLEnums(this.getEnumsQuery());
        for (SqlTable table : tables) {
            sr.readColumnsOfSQLTable(table, this.getColumnsQuery(table.getTableName()));
            this.sqlForeignKeys
                    .addAll(sr.readForeignKeysOfSQLTable(table, this.getForeignKeysQuery(table.getTableName())));
            sr.readIndexesOfSQLTable(table, this.getIndexesQuery(table.getTableName()));
        }
    }

    /**
     * Resolves foreign key dependencies by automatically including referenced
     * tables.
     * When a selected table has foreign keys referencing other tables, those
     * referenced
     * tables are automatically added to ensure valid model generation.
     *
     * @param sr                 the ScriptRunner for executing queries
     * @param selectedTableNames the initially selected table names
     * @return expanded list of table names including all foreign key dependencies
     * @throws SQLException if there is an error reading foreign keys
     */
    private List<String> resolveForeignKeyDependencies(ScriptRunner sr, List<String> selectedTableNames)
            throws SQLException {
        List<String> resolvedTables = new ArrayList<>(selectedTableNames);
        List<String> tablesToProcess = new ArrayList<>(selectedTableNames);
        HashSet<String> processedTables = new HashSet<>();

        // Get all available tables for reference validation
        List<SqlTable> allTables = this.tables;
        HashSet<String> availableTableNames = allTables.stream()
                .map(SqlTable::getTableName)
                .collect(java.util.stream.Collectors.toCollection(HashSet::new));

        // Process tables iteratively to handle chains of dependencies
        while (!tablesToProcess.isEmpty()) {
            String currentTable = tablesToProcess.removeFirst();

            if (processedTables.contains(currentTable) || !availableTableNames.contains(currentTable)) {
                continue;
            }

            processedTables.add(currentTable);

            // Read foreign keys for current table
            SqlTable tempTable = new SqlTable(currentTable);
            List<SqlForeignKey> foreignKeys = sr.readForeignKeysOfSQLTable(tempTable,
                    this.getForeignKeysQuery(currentTable));

            // Check each foreign key and add referenced tables
            for (SqlForeignKey fk : foreignKeys) {
                String referencedTable = fk.getReferencedTableName();

                if (!resolvedTables.contains(referencedTable) && availableTableNames.contains(referencedTable)) {
                    resolvedTables.add(referencedTable);
                    tablesToProcess.add(referencedTable);

                    // Log info about auto-included table
                    errStream.println("INFO: Automatically including table '" + referencedTable +
                            "' due to foreign key relationship from '" + currentTable + "'");
                }
            }
        }

        return resolvedTables;
    }

    /**
     * Maps the database schema information (tables, columns, relationships) to a
     * Ballerina persist module.
     * This includes mapping database enums to Ballerina enums and database tables
     * to Ballerina entities
     * with proper field types, relationships, and constraints.
     *
     * @throws BalException if there is an error during the mapping process, such as
     *                      unsupported
     *                      data types or invalid relationship configurations
     */
    public void mapDatabaseSchemaToModule() throws BalException {
        mapEnums();
        mapEntities();
        entityMap.forEach(moduleBuilder::addEntity);
    }

    private void mapEnums() {
        this.sqlEnums.forEach(sqlEnum -> {
            String enumName = createEnumName(sqlEnum.getEnumTableName(), sqlEnum.getEnumColumnName());
            Enum.Builder enumBuilder = Enum.newBuilder(enumName);
            List<String> enumValues = extractEnumValues(sqlEnum.getFullEnumText());
            if (!enumValues.isEmpty()) {
                enumValues.forEach(enumValue -> enumBuilder
                        .addMember(new EnumMember(CaseConverter.toUpperSnakeCase(enumValue), enumValue)));
                moduleBuilder.addEnum(enumName, enumBuilder.build());
            }
        });
    }

    private void mapEntities() throws BalException {
        Map<String, Entity.Builder> entityBuilderMap = new HashMap<>();
        tables.forEach(table -> {
            String entityName = CaseConverter.toSingularPascalCase(table.getTableName());

            Entity.Builder entityBuilder = Entity.newBuilder(entityName);
            entityBuilder.setTableName(table.getTableName());
            List<EntityField> keys = new ArrayList<>();
            List<EntityField> fields = new ArrayList<>();
            table.getColumns().forEach(column -> {
                EntityField.Builder fieldBuilder = EntityField.newBuilder(
                        CaseConverter.toCamelCase(column.getColumnName()));

                fieldBuilder.setFieldColumnName(column.getColumnName());
                fieldBuilder.setArrayType(false);
                if (isEnumType(column)) {
                    fieldBuilder.setType(createEnumName(table.getTableName(), column.getColumnName()));
                } else {
                    String maxLen = column.getCharacterMaximumLength();
                    SqlType sqlType = new SqlType(
                            column.getDataType().toUpperCase(Locale.ENGLISH),
                            column.getFullDataType(),
                            column.getColumnDefault(),
                            column.getNumericPrecision() != null ? parseInt(column.getNumericPrecision()) : 0,
                            column.getNumericScale() != null ? parseInt(column.getNumericScale()) : 0,
                            (maxLen != null) ? parseUnsignedInt(maxLen) : 0,
                            persistConfiguration.getProvider());
                    String balType = this.getBalType(sqlType);
                    fieldBuilder.setType(balType);
                    fieldBuilder.setSqlType(sqlType);
                    fieldBuilder.setArrayType(sqlType.isArrayType());
                }

                fieldBuilder.setOptionalType(column.getIsNullable().equals("YES"));
                fieldBuilder.setIsDbGenerated(column.isDbGenerated());

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
                    if (entityField.getFieldColumnName().equals(columnName)) {
                        indexFields.add(entityField);
                    }
                }));
                Index index = new Index(sqlIndex.getIndexName(), indexFields, sqlIndex.getUnique());
                if (index.isUnique()) {
                    entityBuilder.addUniqueIndex(index);
                } else {
                    entityBuilder.addIndex(index);
                }
            });
            entityBuilder.setKeys(keys);

            // Mark entities without primary keys as containing unsupported types
            // This prevents IndexOutOfBoundsException during code generation
            if (keys.isEmpty()) {
                entityBuilder.setContainsUnsupportedTypes(true);
                errStream.println("WARNING: Table '" + table.getTableName() +
                        "' does not have a primary key and will be excluded from entity generation.");
            } else {
                entityBuilderMap.put(entityBuilder.getEntityName(), entityBuilder);
            }
        });
        HashMap<String, Integer> ownerFieldNames = new HashMap<>();
        HashMap<String, Integer> assocFieldNames = new HashMap<>();
        for (SqlForeignKey sqlForeignKey : this.sqlForeignKeys) {
            Entity.Builder ownerEntityBuilder = entityBuilderMap
                    .get(CaseConverter.toSingularPascalCase(sqlForeignKey.getTableName()));
            Entity.Builder assocEntityBuilder = entityBuilderMap
                    .get(CaseConverter.toSingularPascalCase(sqlForeignKey.getReferencedTableName()));
            if (!new HashSet<>(sqlForeignKey.getReferencedColumnNames()).containsAll(assocEntityBuilder.getKeys()
                    .stream().map(EntityField::getFieldColumnName).toList())) {
                throw new BalException("bal persist does not support foreign key references to unique " +
                        "keys.");
            }
            boolean isReferenceMany = inferRelationshipCardinality(ownerEntityBuilder.build(),
                    sqlForeignKey) == Relation.RelationType.MANY;
            String assocFieldName = isReferenceMany
                    ? Pluralizer.pluralize(ownerEntityBuilder.getEntityName().toLowerCase(Locale.ENGLISH))
                    : ownerEntityBuilder.getEntityName().toLowerCase(Locale.ENGLISH);
            if (assocFieldNames.containsKey(assocEntityBuilder.getEntityName() + assocFieldName)) {
                assocFieldNames.put(assocEntityBuilder.getEntityName() + assocFieldName,
                        assocFieldNames.get(assocEntityBuilder.getEntityName() + assocFieldName) + 1);
                assocFieldName = assocFieldName +
                        assocFieldNames.get(assocEntityBuilder.getEntityName() + assocFieldName);
            } else {
                assocFieldNames.put(assocEntityBuilder.getEntityName() + assocFieldName, 0);
            }
            EntityField.Builder assocFieldBuilder = EntityField
                    .newBuilder(assocFieldName);
            assocFieldBuilder.setType(ownerEntityBuilder.getEntityName());

            String ownerFieldName = assocEntityBuilder.getEntityName().toLowerCase(Locale.ENGLISH);
            if (ownerFieldNames.containsKey(ownerEntityBuilder.getEntityName() + ownerFieldName)) {
                ownerFieldNames.put(ownerEntityBuilder.getEntityName() + ownerFieldName,
                        ownerFieldNames.get(ownerEntityBuilder.getEntityName() + ownerFieldName) + 1);
                ownerFieldName = ownerFieldName +
                        ownerFieldNames.get(ownerEntityBuilder.getEntityName() + ownerFieldName);
            } else {
                ownerFieldNames.put(ownerEntityBuilder.getEntityName() + ownerFieldName, 0);
            }

            EntityField.Builder ownerFieldBuilder = EntityField
                    .newBuilder(ownerFieldName);
            ownerFieldBuilder.setType(assocEntityBuilder.getEntityName());

            assocFieldBuilder.setArrayType(isReferenceMany);
            assocFieldBuilder.setOptionalType(!isReferenceMany);
            ownerFieldBuilder.setRelationRefs(sqlForeignKey.getColumnNames().stream()
                    .map(columnName -> ownerEntityBuilder.build().getFieldByColumnName(columnName).getFieldName())
                    .toList());

            EntityField ownerField = ownerFieldBuilder.build();

            assocEntityBuilder.addField(assocFieldBuilder.build());
            ownerEntityBuilder.addField(ownerField);
        }

        entityBuilderMap.forEach((key, value) -> entityMap.put(key, value.build()));
    }

    private String createEnumName(String tableName, String columnName) {
        return CaseConverter.toSingularPascalCase(tableName) + CaseConverter.toSingularPascalCase(columnName);
    }

    private Relation.RelationType inferRelationshipCardinality(Entity ownerEntity, SqlForeignKey foreignKey) {
        List<EntityField> ownerColumns = new ArrayList<>();
        foreignKey.getColumnNames()
                .forEach(columnName -> ownerColumns.add(ownerEntity.getFieldByColumnName(columnName)));
        boolean isUniqueIndexPresent = ownerEntity.getUniqueIndexes().stream()
                .anyMatch(index -> index.getFields().equals(ownerColumns));
        if (ownerEntity.getKeys().equals(ownerColumns)) {
            return Relation.RelationType.ONE;
        } else if (isUniqueIndexPresent) {
            return Relation.RelationType.ONE;
        } else {
            return Relation.RelationType.MANY;
        }
    }

    /**
     * Maps common SQL data types to their corresponding Ballerina types.
     * Handles standard types like integers, strings, dates, times, and binary data.
     * If a SQL type is not supported, returns UNSUPPORTED_TYPE and prints a
     * warning.
     *
     * @param sqlType the SQL type information including type name, precision,
     *                scale, etc.
     * @return the corresponding Ballerina type as a string (e.g., "int", "string",
     *         "decimal")
     */
    protected String getBalTypeForCommonDataTypes(SqlType sqlType) {
        return switch (sqlType.getTypeName()) {
            case PersistToolsConstants.SqlTypes.INT,
                    PersistToolsConstants.SqlTypes.INTEGER,
                    PersistToolsConstants.SqlTypes.TINYINT,
                    PersistToolsConstants.SqlTypes.SMALLINT,
                    PersistToolsConstants.SqlTypes.MEDIUMINT,
                    PersistToolsConstants.SqlTypes.BIGINT,
                    PersistToolsConstants.SqlTypes.SERIAL,
                    PersistToolsConstants.SqlTypes.BIGSERIAL,
                    PersistToolsConstants.SqlTypes.INT4,
                    PersistToolsConstants.SqlTypes.INT2,
                    PersistToolsConstants.SqlTypes.INT8 ->
                PersistToolsConstants.BallerinaTypes.INT;
            case PersistToolsConstants.SqlTypes.BOOLEAN,
                    PersistToolsConstants.SqlTypes.BOOL ->
                PersistToolsConstants.BallerinaTypes.BOOLEAN;
            case PersistToolsConstants.SqlTypes.DECIMAL,
                    PersistToolsConstants.SqlTypes.NUMERIC ->
                PersistToolsConstants.BallerinaTypes.DECIMAL;
            case PersistToolsConstants.SqlTypes.DOUBLE,
                    PersistToolsConstants.SqlTypes.FLOAT,
                    PersistToolsConstants.SqlTypes.FLOAT4,
                    PersistToolsConstants.SqlTypes.FLOAT8 ->
                PersistToolsConstants.BallerinaTypes.FLOAT;
            case PersistToolsConstants.SqlTypes.DATE -> PersistToolsConstants.BallerinaTypes.DATE;
            case PersistToolsConstants.SqlTypes.TIME,
                    PersistToolsConstants.SqlTypes.TIMETZ ->
                PersistToolsConstants.BallerinaTypes.TIME_OF_DAY;
            case PersistToolsConstants.SqlTypes.TIME_STAMP,
                    PersistToolsConstants.SqlTypes.TIME_STAMPTZ ->
                PersistToolsConstants.BallerinaTypes.UTC;
            case PersistToolsConstants.SqlTypes.DATE_TIME2,
                    PersistToolsConstants.SqlTypes.DATE_TIME ->
                PersistToolsConstants.BallerinaTypes.CIVIL;
            case PersistToolsConstants.SqlTypes.VARCHAR,
                    PersistToolsConstants.SqlTypes.CHAR,
                    PersistToolsConstants.SqlTypes.CHARACTER,
                    PersistToolsConstants.SqlTypes.BPCHAR,
                    PersistToolsConstants.SqlTypes.TEXT,
                    PersistToolsConstants.SqlTypes.MEDIUMTEXT,
                    PersistToolsConstants.SqlTypes.LONGTEXT,
                    PersistToolsConstants.SqlTypes.TINYTEXT ->
                PersistToolsConstants.BallerinaTypes.STRING;
            case PersistToolsConstants.SqlTypes.LONG_BLOB,
                    PersistToolsConstants.SqlTypes.MEDIUM_BLOB,
                    PersistToolsConstants.SqlTypes.TINY_BLOB,
                    PersistToolsConstants.SqlTypes.BINARY,
                    PersistToolsConstants.SqlTypes.VARBINARY,
                    PersistToolsConstants.SqlTypes.BLOB,
                    PersistToolsConstants.SqlTypes.BYTEA ->
                PersistToolsConstants.BallerinaTypes.BYTE;
            default -> {
                errStream.println("WARNING Unsupported SQL type found: " + sqlType.getFullDataType());
                yield PersistToolsConstants.UNSUPPORTED_TYPE;
            }
        };
    }

}
