/*
 *  Copyright (c) 2023, WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
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
package io.ballerina.persist.cmd;

import io.ballerina.cli.BLauncherCmd;
import io.ballerina.persist.BalException;
import io.ballerina.persist.PersistToolsConstants;
import io.ballerina.persist.models.Entity;
import io.ballerina.persist.models.EntityField;
import io.ballerina.persist.models.ForeignKey;
import io.ballerina.persist.models.Module;
import io.ballerina.persist.models.Relation;
import io.ballerina.persist.nodegenerator.SourceGenerator;
import io.ballerina.persist.nodegenerator.syntax.constants.BalSyntaxConstants;
import io.ballerina.persist.nodegenerator.syntax.utils.SqlScriptUtils;
import io.ballerina.persist.utils.BalProjectUtils;
import picocli.CommandLine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * This Class implements the `persist migrate` command in Ballerina
 * persist-tool.
 *
 * @since 0.4.0
 */
@CommandLine.Command(name = "migrate", description = "Generate DB migration scripts for Ballerina schema changes.")

public class Migrate implements BLauncherCmd {

    private static final PrintStream errStream = System.err;

    private final String sourcePath;

    private static final String COMMAND_IDENTIFIER = "persist-migrate";

    private record NameMapping(String oldName, String newName) { }

    @CommandLine.Parameters
    public List<String> argList;
    @CommandLine.Option(names = {"--datastore"})
    private String datastore = "mysql";

    public Migrate() {
        this("");
    }

    public Migrate(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    @CommandLine.Option(names = { "-h", "--help" }, hidden = true)
    private boolean helpFlag;

    @Override
    public void execute() {
        if (helpFlag) {
            String commandUsageInfo = BLauncherCmd.getCommandUsageInfo(COMMAND_IDENTIFIER);
            errStream.println(commandUsageInfo);
            return;
        }

        errStream.printf(BalSyntaxConstants.EXPERIMENTAL_NOTICE, "The support for migrations is currently an " +
                "experimental feature, and its behavior may be subject to change in future releases.");

        Path projectPath = Paths.get(sourcePath).toAbsolutePath();
        try {
            BalProjectUtils.validateBallerinaProject(projectPath);
        } catch (BalException e) {
            errStream.println(e.getMessage());
            return;
        }

        if (!Objects.equals(datastore, PersistToolsConstants.SupportedDataSources.MYSQL_DB)) {
            errStream.println("Error: invalid datastore: " + datastore + ". currently only MySQL is supported.");
            return;
        }

        // Check if the migration name is given
        if (null == argList || argList.isEmpty()) {
            errStream.println("Error: migration label is not provided. Provide the migration label " +
                    "along with the command like `bal persist migrate <migration-label>`");
            return;
        }
        // Check if one argument is given and not more than one argument.
        if (1 < argList.size()) {
            errStream.println("Error: Too many arguments provided. Only one argument " +
                    "is allowed to pass for migration label");
            return;
        }

        String migrationName = argList.get(0);

        // Returns the path of the bal file in the persist directory
        Path schemaFilePath;
        try {
            schemaFilePath = BalProjectUtils.getSchemaFilePath(this.sourcePath);
        } catch (BalException e) {
            errStream.println(e.getMessage());
            return;
        }

        migrate(migrationName, projectPath, this.sourcePath, schemaFilePath);

    }

    private static void migrate(String migrationName, Path projectDirPath, String sourcePath, Path schemaFilePath) {
        if (schemaFilePath != null) {
            Path persistDirPath = Paths.get(projectDirPath.toString(), "persist");

            // Create a File object for the persist directory
            File persistDir = new File(persistDirPath.toString());

            // Create a File object for the migrations directory
            File migrationsDir = new File(persistDir, "migrations");

            // Check if the migrations directory exists
            if (!migrationsDir.exists()) {
                // Create the migrations directory
                boolean created = migrationsDir.mkdir();
                if (!created) {
                    errStream.println("Error: failed to create migrations directory inside the persist directory");
                    return;
                }

                Module model;
                try {
                    model = BalProjectUtils.getEntities(schemaFilePath);
                } catch (BalException e) {
                    errStream.println("Error getting entities: " + e.getMessage());
                    return;
                }

                String newMigration = createTimestampFolder(migrationName, migrationsDir);

                createTimestampDirectory(newMigration);

                Path newMigrationPath = Paths.get(newMigration);

                if (!model.getEntityMap().isEmpty()) {
                    try {
                        // Generate the SQL script
                        SourceGenerator.addSqlScriptFile("the migrate command",
                                SqlScriptUtils.generateSqlScript(model.getEntityMap().values(),
                                        PersistToolsConstants.SupportedDataSources.MYSQL_DB), newMigrationPath);
                    } catch (BalException e) {
                        errStream.println("ERROR: failed to generate SQL script " + e.getMessage());
                        return;
                    }

                    try {
                        // Copy the source file to the destination folder
                        Files.copy(schemaFilePath, newMigrationPath.resolve(schemaFilePath.getFileName()));
                    } catch (IOException e) {
                        errStream.println("Error: Copying file failed: " + e.getMessage());
                        return;
                    }

                    //Get the relative path of the migration directory from the project root
                    Path relativePath = Paths.get("").toAbsolutePath().relativize(newMigrationPath);

                    errStream.println(System.lineSeparator() + "Detailed list of differences: ");
                    errStream.println("[Entity " + String.join(", ", model.getEntityMap().keySet())
                            + " has been added]" + System.lineSeparator());
                    errStream.println(
                            "Generated migration script to " + relativePath +
                            " directory." + System.lineSeparator());
                    errStream.println("Next steps:" + System.lineSeparator() + 
                            "Execute the \"script.sql\" file located at " +
                            relativePath +
                            " directory in your database to migrate the schema with the latest changes.");
                } else {
                    errStream.println("ERROR: Could not find any entities in the schema file");
                }

            } else {
                // Migrate with the latest bal file in the migrations directory
                migrateWithTimestamp(migrationsDir, migrationName, schemaFilePath,
                        findLatestBalFile(getDirectoryPaths(migrationsDir.toString()), sourcePath));
            }
        }
    }

    // Get a list of all timestamp folders in the migrations directory
    private static List<String> getDirectoryPaths(String folder) {
        List<String> directoryNames = new ArrayList<>();
        Path folderPath = Path.of(folder);

        if (Files.exists(folderPath) && Files.isDirectory(folderPath)) {
            try (Stream<Path> directoryStream = Files.list(folderPath)) {
                List<Path> directories = directoryStream
                        .filter(Files::isDirectory)
                        .toList();

                for (Path directory : directories) {
                    Path fileName = directory.getFileName();
                    if (fileName != null) {
                        directoryNames.add(fileName.toString());
                    } else {
                        errStream.println("Error: Found a directory with a null file name.");
                    }
                }
            } catch (IOException e) {
                errStream.println("Error: An error occurred while retrieving"
                        + " the directory paths: " + e.getMessage());
            }
        }

        return directoryNames;
    }

    // Get the path of the latest .bal file in the migrations directory
    private static Path findLatestBalFile(List<String> folderNames, String sourcePath) {
        if (folderNames.size() == 1) {
            return findBalFileInFolder(folderNames.get(0), sourcePath);
        }

        String latestTimestamp = "";
        ZonedDateTime latestDateTime = ZonedDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(ZoneOffset.UTC);

        for (String folderName : folderNames) {
            String timestamp = folderName.split("_")[0];
            ZonedDateTime dateTime = ZonedDateTime.parse(timestamp, formatter);

            if (dateTime.isAfter(latestDateTime)) {
                latestDateTime = dateTime;
                latestTimestamp = folderName;
            }
        }

        return findBalFileInFolder(latestTimestamp, sourcePath);
    }

    // Find the .bal file in the given folder
    private static Path findBalFileInFolder(String folderName, String sourcePath) {
        Path folderPath = Paths.get(sourcePath).resolve("persist")
                .resolve("migrations").resolve(folderName).toAbsolutePath();
        File folder = folderPath.toFile();
        File[] files = folder.listFiles();

        if (files != null) {
            // Use lambda function to filter the files and find the one ending with ".bal"
            File balFile = Arrays.stream(files)
                    .filter(file -> file.isFile() && file.getName().endsWith(".bal"))
                    .findFirst()
                    .orElse(null);

            if (balFile != null) {
                // Return the Path object of the .bal file
                return balFile.toPath().toAbsolutePath();
            }
        }

        // No .bal file found
        return null;
    }

    private static void migrateWithTimestamp(File migrationsDir, String migrationName, Path currentModelPath,
            Path previousModelPath) {
        List<String> queries;

        String newMigration = createTimestampFolder(migrationName, migrationsDir);

        File newMigrateDirectory = getTimestampDirectory(newMigration);

        Path newMigrationPath = Paths.get(newMigration);
        try {
            Module previousModel = BalProjectUtils.getEntities(previousModelPath);
            Module currentModel = BalProjectUtils.getEntities(currentModelPath);

            queries = findDifferences(previousModel, currentModel);

            // Write queries to file
            if (!queries.isEmpty()) {
                String filePath = Paths.get(newMigrationPath.toString(), "script.sql").toString();
                try (FileOutputStream fStream = new FileOutputStream(filePath);
                        OutputStreamWriter oStream = new OutputStreamWriter(fStream, StandardCharsets.UTF_8);
                        BufferedWriter writer = new BufferedWriter(oStream)) {
                    writer.write("-- AUTO-GENERATED FILE." + System.lineSeparator() +
                            "-- This file is an auto-generated file by Ballerina " +
                            "persistence layer for the migrate command." + System.lineSeparator() +
                            "-- Please verify the generated scripts and " +
                            "execute them against the target DB server." +
                            System.lineSeparator() + System.lineSeparator());
                    for (String query : queries) {
                        writer.write(query);
                        writer.newLine();
                    }
                } catch (IOException e) {
                    errStream.println("Error: An error occurred while writing to file: " + e.getMessage());
                    return;
                }

                //Get the relative path of the migration directory from the project root
                Path relativePath = Paths.get("").toAbsolutePath().relativize(newMigrationPath);

                errStream.println(
                        "Generated migration script to " + relativePath +
                        " directory." + System.lineSeparator());
                errStream.println("Next steps:" + System.lineSeparator() + "Execute the \"script.sql\" file located at "
                        + relativePath +
                        " directory in your database to migrate the schema with the latest changes.");
            }

        } catch (BalException e) {
            errStream.println("Error getting entities: " + e.getMessage());
            return;
        }

        if (!queries.isEmpty()) {
            try {
                // Copy the source file to the destination folder
                Files.copy(currentModelPath, newMigrationPath.resolve(currentModelPath.getFileName()));
            } catch (IOException e) {
                errStream.println("Error: Copying file failed: " + e.getMessage());
            }
        } else {
            // Delete the newMigrateDirectory
            if (newMigrateDirectory != null) {
                boolean deleteNewMigrateFolder = newMigrateDirectory.delete();
                if (!deleteNewMigrateFolder) {
                    errStream.println("Error: Failed to delete timestamp folder.");
                }
            }

            // Delete the migrations directory if it is empty
            if (migrationsDir.exists() && migrationsDir.isDirectory()) {
                File[] files = migrationsDir.listFiles();
                if (files != null && files.length == 0) {
                    try {
                        Files.delete(migrationsDir.toPath());
                    } catch (IOException e) {
                        errStream.println("Error: Failed to delete migration folder: " + e.getMessage());
                    }
                }
            }
        }
    }

    // Create a timestamp folder name
    private static String createTimestampFolder(String migrationName, File migrationsDir) {
        Instant currentTime = Instant.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = formatter.format(ZonedDateTime.ofInstant(currentTime, ZoneOffset.UTC));

        return migrationsDir + File.separator + timestamp + "_" + migrationName;
    }

    // Create the timestamp directory
    private static File getTimestampDirectory(String newMigration) {
        File newMigrateDirectory = new File(newMigration);

        if (!newMigrateDirectory.exists()) {
            boolean isDirectoryCreated = newMigrateDirectory.mkdirs();
            if (!isDirectoryCreated) {
                errStream.println("Error: Failed to create directory: " + newMigrateDirectory.getAbsolutePath());
            } else {
                return newMigrateDirectory;
            }
        }

        return null;
    }

    // Create the timestamp directory without returning the directory
    private static void createTimestampDirectory(String newMigration) {
        File newMigrateDirectory = new File(newMigration);

        if (!newMigrateDirectory.exists()) {
            boolean isDirectoryCreated = newMigrateDirectory.mkdirs();
            if (!isDirectoryCreated) {
                errStream.println("Error: Failed to create directory: " + newMigrateDirectory.getAbsolutePath());
            }
        }
    }

    private static List<String> findDifferences(Module previousModel, Module currentModel) {

        List<String> queries = new ArrayList<>();
        List<String> differences = new ArrayList<>();

        List<String> addedEntities = new ArrayList<>();
        List<NameMapping> renamedEntities = new ArrayList<>();
        List<String> removedEntities = new ArrayList<>();
        HashMap<String, List<EntityField>> addedFields = new HashMap<>();
        HashMap<String, List<NameMapping>> renamedFields = new HashMap<>();
        HashMap<String, List<String>> removedFields = new HashMap<>();
        HashMap<String, List<EntityField>> changedFieldTypes = new HashMap<>();
        Set<String> primaryKeyChangedEntities = new HashSet<>();
        HashMap<String, List<ForeignKey>> addedForeignKeys = new HashMap<>();
        HashMap<String, List<ForeignKey>> removedForeignKeys = new HashMap<>();

        // Compare entities in previousModel and currentModel
        for (Entity previousModelEntity : previousModel.getEntityMap().values()) {
            Entity currentModelEntity = currentModel.getEntityMap().get(previousModelEntity.getEntityName());

            // Check if currentModelEntity exists
            if (currentModelEntity == null) {
                differences.add("Table " + previousModelEntity.getTableName() + " has been removed");
                removedEntities.add(previousModelEntity.getTableName());
                continue;
            }

            // Check if their table names are changed (through annotations)
            if (!Objects.equals(currentModelEntity.getTableName(), previousModelEntity.getTableName())) {
                differences.add("Table " + previousModelEntity.getTableName() + " has been renamed to " +
                        currentModelEntity.getTableName());
                renamedEntities.add(new NameMapping(previousModelEntity.getTableName(),
                        currentModelEntity.getTableName()));
            }

            // Check if the primary key fields has been removed
            if (previousModelEntity.getKeys().size() > currentModelEntity.getKeys().size()) {
                differences.add("Primary key of table " + currentModelEntity.getTableName() + " has changed");
                primaryKeyChangedEntities.add(currentModelEntity.getTableName());
            }

            // Compare fields in previousModelEntity and currentModelEntity
            for (EntityField previousModelField : previousModelEntity.getFields()) {
                EntityField currentModelField = currentModelEntity
                        .getFieldByName(previousModelField.getFieldName());

                // Check if currentModelField exists and if foreign key was removed
                if (currentModelField == null) {
                    if (previousModelField.getRelation() == null) {
                        differences.add("Column " + previousModelField.getFieldColumnName() +
                                " has been removed from table " + previousModelEntity.getTableName());
                        removeField(previousModelEntity, previousModelField, removedFields);
                    } else if (previousModelField.getRelation().isOwner()) {
                        differences.add("Foreign key " + previousModelField.getFieldColumnName() +
                                " has been removed from table " + previousModelEntity.getTableName());
                        addOrRemoveForeignKey(previousModelEntity, previousModelField, removedForeignKeys);
                    }
                    continue;
                }

                // Check if the field names are changed (through annotations)
                if (!Objects.equals(currentModelField.getFieldColumnName(), previousModelField.getFieldColumnName())) {
                    differences.add("Column " + previousModelField.getFieldColumnName() + " in table " +
                            previousModelEntity.getTableName() + " has been renamed to " +
                            currentModelField.getFieldColumnName());
                    if (renamedFields.containsKey(currentModelEntity.getTableName())) {
                        renamedFields.get(currentModelEntity.getTableName()).add(
                                new NameMapping(previousModelField.getFieldColumnName(),
                                        currentModelField.getFieldColumnName()));
                    } else {
                        renamedFields.put(currentModelEntity.getTableName(), new ArrayList<>(
                                List.of(new NameMapping(previousModelField.getFieldColumnName(),
                                        currentModelField.getFieldColumnName()))));
                    }

                }

                // Compare data types
                if (!previousModelField.getFieldType().equals(currentModelField.getFieldType()) ||
                        !Objects.equals(previousModelField.getSqlType(), currentModelField.getSqlType()) ||
                        !Objects.equals(previousModelField.isOptionalType(), currentModelField.isOptionalType())
                ) {
                    differences.add("Data type of column " + previousModelField.getFieldColumnName() +
                            " in table " + previousModelEntity.getTableName() + " has changed to " +
                            currentModelField.getFieldType() + (currentModelField.getSqlType() != null ?
                            " " +  currentModelField.getSqlType().getFullDataType() : "") + " and is now " +
                            (currentModelField.isOptionalType() ? "nullable" : "not nullable"));

                    addOrModifyField(previousModelEntity, currentModelField, changedFieldTypes);
                }

                // Compare readonly fields
                if (!previousModelEntity.getKeys().contains(previousModelField)
                        && currentModelEntity.getKeys().contains(currentModelField)) {
                    differences.add("Primary key of table " + currentModelEntity.getTableName() + " has changed");
                    differences.add("Column " + previousModelField.getFieldColumnName() + " in table " +
                            previousModelEntity.getTableName() + " is now a primary key");
                    primaryKeyChangedEntities.add(previousModelEntity.getTableName());
                }

            }

            // Check for added fields and for added foreign keys
            for (EntityField currentModelField : currentModelEntity.getFields()) {
                EntityField previousModelField = previousModelEntity
                        .getFieldByName(currentModelField.getFieldName());

                if (previousModelField == null) {
                    if (currentModelField.getRelation() == null) {
                        if (currentModelEntity.getKeys().contains(currentModelField)) {
                            differences.add("Column " + currentModelField.getFieldColumnName() + " of type " +
                                    currentModelField.getFieldType() + " has been added to table " +
                                    currentModelEntity.getTableName() + " as a primary key");
                            primaryKeyChangedEntities.add(currentModelEntity.getTableName());
                        } else {
                            differences.add("Column " + currentModelField.getFieldColumnName() + " of type " +
                                    currentModelField.getFieldType() + " has been added to table " +
                                    currentModelEntity.getTableName());
                        }
                        addOrModifyField(currentModelEntity, currentModelField, addedFields);
                    } else if (currentModelField.getRelation().isOwner()) {
                        createForeignKeys(currentModelField, differences, currentModelEntity, addedFields,
                                addedForeignKeys);
                    }
                }
            }
        }

        // Check for added entities
        for (Entity currentModelEntity : currentModel.getEntityMap().values()) {
            Entity previousModelEntity = previousModel.getEntityMap().get(currentModelEntity.getTableName());
            if (previousModelEntity == null && renamedEntities.stream().noneMatch(
                    entry -> entry.newName().equals(currentModelEntity.getTableName()))) {
                differences.add("Table " + currentModelEntity.getTableName() + " has been added");
                addedEntities.add(currentModelEntity.getTableName());
            }
        }

        // Convert differences to queries (ordered)
        addDropTableQueries(removedEntities, queries);
        addDropForeignKeyQueries(removedForeignKeys, queries);
        addDropPrimaryKeyQueries(primaryKeyChangedEntities, addedEntities, queries);
        addDropColumnQueries(removedFields, queries);
        addCreateTableQueries(addedEntities, currentModel, queries);
        addRenameTableQueries(renamedEntities, queries);
        addRenameFieldQueries(renamedFields, queries);
        addCreateFieldQueries(addedFields, queries);
        addCreatePrimaryKeyQueries(primaryKeyChangedEntities, addedEntities, currentModel, queries);
        addCreateForeignKeyQueries(addedForeignKeys, queries);
        addModifyColumnTypeQueries(changedFieldTypes, queries);

        errStream.println(System.lineSeparator() + "Detailed list of differences: ");
        if (!differences.isEmpty()) {
            differences.forEach(difference -> errStream.println("-- " + difference));
            errStream.println();
        } else {
            errStream.println("-- No differences found" + System.lineSeparator());
        }

        return queries;
    }

    private static void addRenameFieldQueries(HashMap<String, List<NameMapping>> renamedFields, List<String> queries) {
        String renameFieldTemplate = "ALTER TABLE %s%nRENAME COLUMN %s TO %s;%n";
        for (Map.Entry<String, List<NameMapping>> entry : renamedFields.entrySet()) {
            for (NameMapping nameMapping : entry.getValue()) {
                queries.add(String.format(renameFieldTemplate, entry.getKey(), nameMapping.oldName(),
                        nameMapping.newName()));
            }
        }
    }

    private static void addRenameTableQueries(List<NameMapping> renamedEntities, List<String> queries) {
        String renameTableTemplate = "RENAME TABLE %s TO %s;%n";
        for (NameMapping nameMapping : renamedEntities) {
            queries.add(String.format(renameTableTemplate, nameMapping.oldName(), nameMapping.newName()));
        }
    }

    private static void createForeignKeys(EntityField currentModelField, List<String> differences,
                                          Entity currentModelEntity, HashMap<String, List<EntityField>> addedFields,
                                          HashMap<String, List<ForeignKey>> addedForeignKeys) {
        for (Relation.Key key : currentModelField.getRelation().getKeyColumns()) {
            differences.add("Column " + key.getColumnName() + " of type " + key.getType() +
                    " has been added to table " + currentModelEntity.getTableName()
                    + " as a foreign key");
        }

        addNewEntityFK(currentModelEntity, currentModelField, addedFields);

        differences.add("Relation " + currentModelField.getFieldName() + " of type " +
                currentModelField.getFieldType() + " has been added to table " +
                currentModelEntity.getTableName());
        addOrRemoveForeignKey(currentModelEntity, currentModelField, addedForeignKeys);
    }

    private static void addCreatePrimaryKeyQueries(Set<String> entities, List<String> addedEntities,
                                                   Module currentModel, List<String> queries) {
        String addPrimaryKeyQuery = "ALTER TABLE %s%nADD PRIMARY KEY (%s);%n";
        for (String tableName : entities) {
            if (addedEntities.contains(tableName)) {
                continue;
            }
            Optional<Entity> entity = currentModel.getEntityByTableName(tableName);
            entity.ifPresent(value ->
                    queries.add(String.format(addPrimaryKeyQuery, tableName, value.getKeys().stream().map(
                    EntityField::getFieldColumnName).reduce((a, b) -> a + ", " + b).orElse(""))));
        }
    }

    private static void addDropPrimaryKeyQueries(Set<String> changedPrimary, List<String> addedEntities,
                                                 List<String> queries) {
        if (changedPrimary.isEmpty()) {
            return;
        }
        String dropPrimaryKeyTemplate = "ALTER TABLE %s%nDROP PRIMARY KEY;%n";
        changedPrimary.forEach(table -> {
            if (!addedEntities.contains(table)) {
                queries.add(String.format(dropPrimaryKeyTemplate, table));
            }
        });
    }

    private static void addOrRemoveForeignKey(Entity entity, EntityField field, Map<String, List<ForeignKey>> map) {
        String removeKeyName = String.format("FK_%s_%s", entity.getTableName(),
                field.getRelation().getAssocEntity().getTableName());
        ForeignKey foreignKey = new ForeignKey(removeKeyName,
                field.getRelation().getKeyColumns().stream().map(Relation.Key::getColumnName).toList(),
                field.getRelation().getAssocEntity().getTableName(),
                field.getRelation().getKeyColumns().stream().map(Relation.Key::getReferenceColumnName).toList());

        if (!map.containsKey(entity.getTableName())) {
            List<ForeignKey> initialData = new ArrayList<>();
            initialData.add(foreignKey);
            map.put(entity.getTableName(), initialData);
        } else {
            List<ForeignKey> existingData = map.get(entity.getTableName());
            existingData.add(foreignKey);
            map.put(entity.getTableName(), existingData);
        }
    }

    private static void removeField(Entity entity, EntityField field, Map<String, List<String>> map) {
        if (!map.containsKey(entity.getTableName())) {
            List<String> initialData = new ArrayList<>();
            initialData.add(field.getFieldColumnName());
            map.put(entity.getTableName(), initialData);
        } else {
            List<String> existingData = map.get(entity.getTableName());
            existingData.add(field.getFieldColumnName());
            map.put(entity.getTableName(), existingData);
        }
    }

    private static void addOrModifyField(Entity entity, EntityField field, Map<String, List<EntityField>> map) {
        if (!map.containsKey(entity.getTableName())) {
            List<EntityField> initialData = new ArrayList<>();
            initialData.add(field);
            map.put(entity.getTableName(), initialData);
        } else {
            List<EntityField> existingData = map.get(entity.getTableName());
            existingData.add(field);
            map.put(entity.getTableName(), existingData);
        }
    }

    private static void addNewEntityFK(Entity entity, EntityField field, Map<String, List<EntityField>> map) {
        for (Relation.Key key : field.getRelation().getKeyColumns()) {
            EntityField primaryKey = field.getRelation().getAssocEntity()
                    .getFieldByColumnName(key.getReferenceColumnName());
            EntityField.Builder customFkBuilder = EntityField.newBuilder(key.getField());
            customFkBuilder.setFieldColumnName(key.getColumnName());
            customFkBuilder.setType(primaryKey.getFieldType());
            customFkBuilder.setArrayType(false);
            customFkBuilder.setSqlType(primaryKey.getSqlType());

            if (!map.containsKey(entity.getTableName())) {
                List<EntityField> initialData = new ArrayList<>();
                initialData.add(customFkBuilder.build());
                map.put(entity.getTableName(), initialData);
            } else {
                List<EntityField> existingData = map.get(entity.getTableName());
                existingData.add(customFkBuilder.build());
                map.put(entity.getTableName(), existingData);
            }
        }
    }

    // Convert Create Table List to Query
    private static void addCreateTableQueries(List<String> addedEntities, Module currentModel,
                                              List<String> queries) {
        for (String tableName:addedEntities) {
            Optional<Entity> entity = currentModel.getEntityByTableName(tableName);
            if (entity.isPresent()) {
                try {
                    queries.add(SqlScriptUtils.generateCreateTableQuery(entity.get(), new HashMap<>(),
                            PersistToolsConstants.SupportedDataSources.MYSQL_DB) + System.lineSeparator());
                } catch (BalException e) {
                    errStream.println("ERROR: failed to generate create table query: " + e.getMessage());
                }
            }
        }
    }

    // Convert list to a MySQL query
    private static void addDropTableQueries(List<String> entities, List<String> queries) {
        for (String entity : entities) {
            String removeTableTemplate = "DROP TABLE %s;%n";
            queries.add(String.format(removeTableTemplate, entity));
        }
    }

    private static void addDropColumnQueries(Map<String, List<String>> map, List<String> queries) {
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            String entity = entry.getKey();
            for (String field : entry.getValue()) {
                String removeFieldTemplate = "ALTER TABLE %s%nDROP COLUMN %s;%n";
                queries.add(String.format(removeFieldTemplate, entity, field));
            }
        }
    }

    private static void addDropForeignKeyQueries(Map<String, List<ForeignKey>> map, List<String> queries) {
        for (Map.Entry<String, List<ForeignKey>> entry : map.entrySet()) {
            String entity = entry.getKey();
            for (ForeignKey foreignKey : entry.getValue()) {
                String warningComment = "-- Please verify the foreign key constraint name before executing the query"
                        + System.lineSeparator();
                String removeForeignKeyTemplate = warningComment + "ALTER TABLE %s%nDROP FOREIGN KEY %s;%n";
                queries.add(String.format(removeForeignKeyTemplate, entity, foreignKey.name()));
                HashMap<String, List<String>> fieldMap = new HashMap<>();
                fieldMap.put(entity, foreignKey.columnNames());
                addDropColumnQueries(fieldMap, queries);
            }
        }
    }

    // Convert map of FieldMetadata lists to a MySQL query
    private static void addModifyColumnTypeQueries(Map<String, List<EntityField>> map, List<String> queries) {
        for (Map.Entry<String, List<EntityField>> entry : map.entrySet()) {
            String entity = entry.getKey();
            for (EntityField field : entry.getValue()) {
                String fieldName = field.getFieldColumnName();
                String fieldType;
                try {
                    fieldType = SqlScriptUtils.getSqlType(field,
                            PersistToolsConstants.SupportedDataSources.MYSQL_DB);
                } catch (BalException e) {
                    errStream.println("ERROR: data type conversion failed: " + e.getMessage());
                    return;
                }
                String changeTypeTemplate = "ALTER TABLE %s%nMODIFY COLUMN %s %s%s%s;%n";

                queries.add(String.format(changeTypeTemplate, entity, fieldName, fieldType,
                        field.isOptionalType() ? "" : " NOT NULL",
                        field.isDbGenerated() ? " AUTO_INCREMENT" : ""));
            }
        }
    }

    private static void addCreateFieldQueries(Map<String, List<EntityField>> map, List<String> queries) {
        for (Map.Entry<String, List<EntityField>> entry : map.entrySet()) {
            String entity = entry.getKey();
            for (EntityField field : entry.getValue()) {
                String fieldName = field.getFieldColumnName();
                String fieldType;
                try {
                    fieldType = SqlScriptUtils.getSqlType(field,
                            PersistToolsConstants.SupportedDataSources.MYSQL_DB);
                } catch (BalException e) {
                    errStream.println("ERROR: data type conversion failed: " + e.getMessage());
                            return;
                }
                String addFieldTemplate = "ALTER TABLE %s%nADD COLUMN %s %s%s%s;%n";

                queries.add(String.format(addFieldTemplate, entity, fieldName, fieldType,
                        field.isOptionalType() ? "" : " NOT NULL",
                        field.isDbGenerated() ? " AUTO_INCREMENT" : ""));
            }
        }
    }

    // Convert map of ForeignKey lists to a MySQL query
    private static void addCreateForeignKeyQueries(Map<String, List<ForeignKey>> map, List<String> queries) {
        String addFKTemplate = "ALTER TABLE %s%nADD CONSTRAINT %s FOREIGN KEY (%s) REFERENCES %s(%s);%n";
        for (Map.Entry<String, List<ForeignKey>> entry : map.entrySet()) {
            String entity = entry.getKey();
            for (ForeignKey foreignKey : entry.getValue()) {
                queries.add(String.format(addFKTemplate, entity, foreignKey.name(),
                        foreignKey.columnNames().stream().reduce((a, b) -> a + ", " + b).orElse(""),
                        foreignKey.referenceTable(),
                        foreignKey.referenceColumns().stream().reduce((a, b) -> a + ", " + b).orElse("")));
            }
        }
    }

    @Override
    public void setParentCmdParser(CommandLine parentCmdParser) {

    }

    @Override
    public String getName() {
        return PersistToolsConstants.COMPONENT_IDENTIFIER;
    }

    @Override
    public void printLongDesc(StringBuilder out) {
        out.append("Migrate the Ballerina schema into MySQL").append(System.lineSeparator());
        out.append(System.lineSeparator());
    }

    @Override
    public void printUsage(StringBuilder stringBuilder) {
        stringBuilder.append("  ballerina " + PersistToolsConstants.COMPONENT_IDENTIFIER + " migrate")
                .append(System.lineSeparator());
    }
}
