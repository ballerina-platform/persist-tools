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
package io.ballerina.persist.cmd;

import io.ballerina.cli.BLauncherCmd;
import io.ballerina.persist.BalException;
import io.ballerina.persist.PersistToolsConstants;
import io.ballerina.persist.models.Entity;
import io.ballerina.persist.models.EntityField;
import io.ballerina.persist.models.FieldMetadata;
import io.ballerina.persist.models.ForeignKey;
import io.ballerina.persist.models.Module;
import io.ballerina.persist.utils.BalProjectUtils;
import picocli.CommandLine;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * This Class implements the `persist migrate` command in Ballerina
 * persist-tool.
 *
 * @since 0.1.0
 */
@CommandLine.Command(name = "migrate", description = "Migrate Ballerina schema to MySQL.")

public class Migrate implements BLauncherCmd {

    private static final PrintStream errStream = System.err;

    private final String sourcePath;

    private static final String COMMAND_IDENTIFIER = "persist-migrate";

    @CommandLine.Parameters
    public List<String> argList;

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

        Path projectPath = Paths.get(sourcePath);
        try {
            BalProjectUtils.validateBallerinaProject(projectPath);
        } catch (BalException e) {
            errStream.println(e.getMessage());
            return;
        }

        // Check if the migration name is given
        if (null == argList) {
            System.out.println("Error: Migration name is not provided (bal migrate <migration-name>)");
            return;
        }
        // Check if one argument is given and not more than one argument.
        if (!(1 == argList.size())) {
            System.out.println("Error: Too many arguments provided (bal migrate <migration-name>)");
            return;
        }

        String migrationName = argList.get(0);

        migrate(migrationName);

    }

    public static void migrate(String migrationName) {
        String projectDirName = System.getProperty("user.dir"); // todo use projectpath instead

        String balFile = getBalFilePath(projectDirName);

        if (balFile != null) { // todo add validation in execute
            Path balFilePath = Paths.get(balFile);

            String persistDirPath = projectDirName + "/persist";

            // Create a File object for the persist directory
            File persistDir = new File(persistDirPath);

            // Create a File object for the migrations directory
            File migrationsDir = new File(persistDir, "migrations");

            // Check if the migrations directory exists
            if (!migrationsDir.exists()) {
                // Create the migrations directory
                boolean created = migrationsDir.mkdir();
                if (!created) {
                    System.out.println("Failed to create migrations directory");
                    return;
                }

                // Create a blankFile.bal file that contains the line "boom"
                Path blankFilePath = Paths.get(persistDirPath + "/blankFile.bal");
                try {
                    Files.createFile(blankFilePath);
                    BufferedWriter writer = new BufferedWriter(new FileWriter(blankFilePath.toString()));
                    writer.write("import ballerina/persist as _;\n");
                    writer.close();
                } catch (IOException e) {
                    System.out.println("Error: Failed to create blankFile.bal: " + e.getMessage());
                    return;
                }

                // Migrate with the blankFile.bal
                migrateWithTimestamp(migrationsDir, migrationName, balFilePath, blankFilePath);

                // Delete the blankFile.bal
                try {
                    Files.delete(blankFilePath);
                } catch (IOException e) {
                    System.out.println("Error: Failed to delete blankFile.bal: " + e.getMessage());
                }

            } else {
                try {
                    // Migrate with the latest bal file in the migrations directory
                    migrateWithTimestamp(migrationsDir, migrationName, balFilePath,
                            getLatestBalFile(migrationsDir.toString()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    // Get the latest bal file in the migrations directory
    public static Path getLatestBalFile(String migrationsDir) throws IOException {

        File migrationsFolder = new File(migrationsDir);
        if (!migrationsFolder.exists()) {
            throw new IllegalArgumentException("Error: Migrations folder does not exist: " + migrationsDir);
        }
        List<File> balFiles = new ArrayList<>();
        try (Stream<Path> walk = Files.walk(migrationsFolder.toPath())) {
            walk.filter(path -> path.toString().endsWith(".bal"))
                    .forEach(path -> balFiles.add(path.toFile()));
        } catch (IOException e) {
            System.err.println("Error occurred while walking the directory: " + e.getMessage());
            throw e;
        }
        if (balFiles.isEmpty()) {
            return null;
        }
        balFiles.sort(Comparator.comparingLong(File::lastModified));
        return balFiles.get(balFiles.size() - 1).toPath().toAbsolutePath();
    }

    public static void migrateWithTimestamp(File migrationsDir, String migrationName, Path newModelPath,
            Path currentModelPath) {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        String timestamp = currentTime.format(formatter);
        List<String> differences = new ArrayList<>();

        // Create the directory with the current timestamp
        String newMigration = migrationsDir + "/" + timestamp + "_" + migrationName;
        File newMigrateDirectory = new File(newMigration);
        if (!newMigrateDirectory.exists()) {
            boolean isDirectoryCreated = newMigrateDirectory.mkdirs();
            if (!isDirectoryCreated) {
                System.out.println("Error: Failed to create directory: " + newMigrateDirectory.getAbsolutePath());
            }
        }

        Path newMigrationPath = Paths.get(newMigration);
        try {
            Module currentModel = BalProjectUtils.getEntities(currentModelPath);
            Module newModel = BalProjectUtils.getEntities(newModelPath);

            differences = findDifferences(currentModel, newModel, newMigrationPath);
            System.out.println("\nDetailed list of differences: "); // todo remove sout
            System.out.println(differences);
            System.out.println("\n");

        } catch (Exception e) {
            e.printStackTrace(); // todo return execption or append to errorstream
        }

        if (!differences.isEmpty()) {
            try {
                // Copy the source file to the destination folder
                Files.copy(newModelPath, newMigrationPath.resolve(newModelPath.getFileName()));
            } catch (IOException e) {
                System.out.println("Error: Copying file failed: " + e.getMessage());
            }
        } else {
            // Delete the newMigrateDirectory
            boolean deleteNewMigrateFolder = newMigrateDirectory.delete();
            if (deleteNewMigrateFolder) {
                System.out.println("Database is up to date. No changes detected.");
            } else {
                System.out.println("Error: Failed to delete timestamp folder.");
            }

            // Delete the migrations directory if it is empty
            if (migrationsDir.exists() && migrationsDir.isDirectory()) {
                if (Objects.requireNonNull(migrationsDir.listFiles()).length == 0) {
                    try {
                        Files.delete(migrationsDir.toPath());
                    } catch (IOException e) {
                        System.out.println("Error: Failed to delete migration folder: " + e.getMessage());
                    }
                }
            }
        }
    }

    // Returns the path of the bal file in the persist directory
    private static String getBalFilePath(String projectDirName) {
        String persistDirName = "persist";
        String balFileExtension = ".bal";

        // Create a file object representing the project directory
        File projectDir = new File(projectDirName);

        // Check if the project directory exists
        if (!projectDir.exists() || !projectDir.isDirectory()) {
            System.out.println("Project directory not found: " + projectDirName);
            return null;
        }

        // Create a file object representing the 'persist' directory
        File persistDir = new File(projectDir, persistDirName);

        // Check if the persist directory exists
        if (!persistDir.exists() || !persistDir.isDirectory()) {
            System.out.println("Persist directory not found in project directory: " + projectDirName);
            return null;
        }

        // Check if a .bal file exists in the persist directory
        File[] balFiles = persistDir.listFiles((dir, name) -> name.toLowerCase().endsWith(balFileExtension));
        if (balFiles == null || balFiles.length == 0) {
            System.out.println("Bal file not found in persist directory: " + projectDirName);
            return null;
        }

        // Get the first .bal file found in the persist directory
        File balFile = balFiles[0];
        Path balFilePath = balFile.toPath();

        // Return the path to the .bal file
        return balFilePath.toString();
    }

    public static List<String> findDifferences(Module currentModel, Module newModel, Path migrationFolderPath) {
        List<String> differences = new ArrayList<>();
        List<String> queries = new ArrayList<>();

        List<String> addedEntities = new ArrayList<>();
        List<String> removedEntities = new ArrayList<>();
        HashMap<String, List<FieldMetadata>> addedFields = new HashMap<>();
        HashMap<String, List<String>> removedFields = new HashMap<>();
        HashMap<String, List<FieldMetadata>> changedFieldTypes = new HashMap<>();
        HashMap<String, List<FieldMetadata>> addedReadOnly = new HashMap<>();
        HashMap<String, List<String>> removedReadOnly = new HashMap<>();
        HashMap<String, List<ForeignKey>> addedForeignKeys = new HashMap<>();
        HashMap<String, List<String>> removedForeignKeys = new HashMap<>();

        // Compare entities in currentModel and newModel
        for (Entity currentModelEntity : currentModel.getEntityMap().values()) {
            Entity newModelEntity = newModel.getEntityMap().get(currentModelEntity.getEntityName());

            // Check if newModelEntity exists
            if (newModelEntity == null) {
                differences.add("Entity " + currentModelEntity.getEntityName() + " has been removed");
                removedEntities.add(currentModelEntity.getEntityName());
                continue;
            }

            // Compare fields in currentModelEntity and newModelEntity
            for (EntityField currentModelField : currentModelEntity.getFields()) {
                EntityField newModelField = newModelEntity.getFieldByName(currentModelField.getFieldName());

                // Check if newModelField exists and if foreign key was removed
                if (newModelField == null) {
                    if (currentModelField.getRelation() == null) {
                        differences.add("Field " + currentModelField.getFieldName() + " has been removed from entity "
                                + currentModelEntity.getEntityName());
                        addToMapNoTypeString(currentModelEntity, currentModelField, removedFields);
                    } else if (currentModelField.getRelation().isOwner()) {
                        differences.add("Foreign key " + currentModelField.getFieldName()
                                + " has been removed from entity " + currentModelEntity.getEntityName());
                        addToMapRemoveForeignKey(currentModelEntity, currentModelField, removedForeignKeys);
                    }
                    continue;
                }

                // Compare data types
                if (!currentModelField.getFieldType().equals(newModelField.getFieldType())) {
                    differences.add("Data type of field " + currentModelField.getFieldName() + " in entity "
                            + currentModelEntity.getEntityName() + " has changed from "
                            + currentModelField.getFieldType() + " to " + newModelField.getFieldType());
                    addToMapWithType(currentModelEntity, newModelField, changedFieldTypes);
                }

                // Compare readonly fields
                if (currentModelEntity.getKeys().contains(currentModelField)
                        && !newModelEntity.getKeys().contains(newModelField)) {
                    differences.add("Field " + currentModelField.getFieldName() + " in entity "
                            + currentModelEntity.getEntityName() + " is no longer a readonly field");
                    addToMapNoTypeString(currentModelEntity, currentModelField, removedReadOnly);

                } else if (!currentModelEntity.getKeys().contains(currentModelField)
                        && newModelEntity.getKeys().contains(newModelField)) {
                    differences.add("Field " + currentModelField.getFieldName() + " in entity "
                            + currentModelEntity.getEntityName() + " is now a readonly field");
                    addToMapNoTypeObject(currentModelEntity, newModelField, addedReadOnly);
                }

            }

            // Check for added fields and for added foreign keys
            for (EntityField newModelField : newModelEntity.getFields()) {
                EntityField currentModelField = currentModelEntity.getFieldByName(newModelField.getFieldName());

                if (currentModelField == null) {
                    if (newModelField.getRelation() == null) {
                        if (newModelEntity.getKeys().contains(newModelField)) {
                            differences.add("Field " + newModelField.getFieldName() + " of type "
                                    + newModelField.getFieldType() + " has been added to entity "
                                    + newModelEntity.getEntityName() + " as a readonly field");
                            addToMapNoTypeObject(newModelEntity, newModelField, addedReadOnly);

                        } else {
                            differences.add(
                                    "Field " + newModelField.getFieldName() + " of type " + newModelField.getFieldType()
                                            + " has been added to entity " + newModelEntity.getEntityName());
                        }
                        addToMapWithType(newModelEntity, newModelField, addedFields);
                    } else if (newModelField.getRelation().isOwner()) {
                        differences.add("Field " + newModelField.getRelation().getKeyColumns().get(0).getField()
                                + " of type " + newModelField.getRelation().getKeyColumns().get(0).getType()
                                + " has been added to entity " + newModelEntity.getEntityName() + " as a foreign key");
                        addToMapNewEntityFK(newModelEntity, newModelField, addedFields);

                        differences.add("Foreign key " + newModelField.getFieldName() + " of type "
                                + newModelField.getFieldType() + " has been added to entity "
                                + newModelEntity.getEntityName());
                        addToMapAddForeignKey(newModelEntity, newModelField, addedForeignKeys);
                    }
                }
            }
        }

        // Check for added entities
        for (Entity newModelEntity : newModel.getEntityMap().values()) {
            Entity currentModelEntity = currentModel.getEntityMap().get(newModelEntity.getEntityName());

            if (currentModelEntity == null) {
                differences.add("Entity " + newModelEntity.getEntityName() + " has been added");
                addedEntities.add(newModelEntity.getEntityName());
                for (EntityField field : newModelEntity.getFields()) {
                    if (field.getRelation() == null) {
                        if (newModelEntity.getKeys().contains(field)) {
                            differences.add("Field " + field.getFieldName() + " of type " + field.getFieldType()
                                    + " has been added to entity " + newModelEntity.getEntityName()
                                    + " as a readonly field");
                            addToMapWithType(newModelEntity, field, addedReadOnly);

                        } else {
                            differences.add("Field " + field.getFieldName() + " of type " + field.getFieldType()
                                    + " has been added to entity " + newModelEntity.getEntityName());
                            addToMapWithType(newModelEntity, field, addedFields);
                        }
                    } else if (field.getRelation().isOwner()) {
                        differences.add("Field " + field.getRelation().getKeyColumns().get(0).getField() + " of type "
                                + field.getRelation().getKeyColumns().get(0).getType() + " has been added to entity "
                                + newModelEntity.getEntityName() + " as a foreign key");
                        addToMapNewEntityFK(newModelEntity, field, addedFields);

                        differences.add("Foreign key " + field.getFieldName() + " of type " + field.getFieldType()
                                + " has been added to entity " + newModelEntity.getEntityName());
                        addToMapAddForeignKey(newModelEntity, field, addedForeignKeys);
                    }
                }
            }
        }

        // Printing for testing purposes
        System.out.println("\n");
        System.out.println("Added entities: " + addedEntities + "\n");
        System.out.println("Removed entities: " + removedEntities + "\n");
        List<String> addedFieldsList = new ArrayList<>();
        for (String key : addedFields.keySet()) {
            for (FieldMetadata field : addedFields.get(key)) {
                addedFieldsList.add(key + "=[" + field.getName() + ", " + field.getDataType() + "]");
            }
        }
        System.out.println("Added fields: " + addedFieldsList + "\n");
        System.out.println("Removed fields: " + removedFields + "\n");
        List<String> changedFieldTypesList = new ArrayList<>();
        for (Map.Entry<String, List<FieldMetadata>> entry : changedFieldTypes.entrySet()) {
            String entityName = entry.getKey();
            for (FieldMetadata field : entry.getValue()) {
                changedFieldTypesList.add(entityName + "=[" + field.getName() + ", " + field.getDataType() + "]");
            }
        }
        System.out.println("Changed field data types: " + changedFieldTypesList + "\n");
        List<String> addedReadOnlyList = new ArrayList<>();
        for (Map.Entry<String, List<FieldMetadata>> entry : addedReadOnly.entrySet()) {
            String entityName = entry.getKey();
            List<FieldMetadata> fieldList = entry.getValue();
            for (FieldMetadata field : fieldList) {
                addedReadOnlyList.add(entityName + "=[" + field.getName() + "]");
            }
        }
        System.out.println("Added readonly fields: " + addedReadOnlyList + "\n");
        System.out.println("Removed readonly fields: " + removedReadOnly + "\n");
        List<String> addedFK = new ArrayList<>();
        for (Map.Entry<String, List<ForeignKey>> entry : addedForeignKeys.entrySet()) {
            String key = entry.getKey();
            List<ForeignKey> value = entry.getValue();
            for (ForeignKey fk : value) {
                addedFK.add(key + "=[" + fk.getName() + ", " + fk.getColumnName() + ", " + fk.getReferenceTable() + ", "
                        + fk.getReferenceColumn() + "]");
            }
        }
        System.out.println("Added foreign keys: " + addedFK + "\n");
        System.out.println("Removed foreign keys: " + removedForeignKeys + "\n");
        System.out.println("\n");

        // Convert differences to queries (ordered)
        convertCreateTableToQuery(queryTypes.ADD_TABLE, addedEntities, queries, addedReadOnly, addedFields);
        convertMapToQuery(queryTypes.ADD_FIELD, addedFields, queries, addedEntities);
        convertMapListToQuery(queryTypes.REMOVE_FOREIGN_KEY, removedForeignKeys, queries);
        convertMapListToQuery(queryTypes.REMOVE_READONLY, removedReadOnly, queries);
        convertMapToQuery(queryTypes.ADD_READONLY, addedReadOnly, queries, addedEntities);
        convertFKMapToQuery(queryTypes.ADD_FOREIGN_KEY, addedForeignKeys, queries);
        convertMapListToQuery(queryTypes.REMOVE_FIELD, removedFields, queries);
        convertListToQuery(queryTypes.REMOVE_TABLE, removedEntities, queries);
        convertMapToQuery(queryTypes.CHANGE_TYPE, changedFieldTypes, queries, addedEntities);

        // Write queries to file
        if (differences.size() != 0) {
            String filePath = migrationFolderPath + "/migration.sql"; // todo return queries instead of differences
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));

                for (String query : queries) {
                    writer.write(query);
                    writer.newLine();
                }
                writer.close();

                System.out.println("Successfully migrated");
            } catch (IOException e) {
                System.out.println("Error: An error occurred while writing to file: " + e.getMessage());
            }
        }

        return differences;
    }

    public static void addToMapAddForeignKey(Entity entity, EntityField field, Map<String, List<ForeignKey>> map) {
        String AddKeyName = String.format("FK_%s_%s", entity.getEntityName(),
                field.getRelation().getAssocEntity().getEntityName());
        ForeignKey foreignKey = new ForeignKey(AddKeyName, field.getRelation().getKeyColumns().get(0).getField(),
                field.getRelation().getAssocEntity().getEntityName(),
                field.getRelation().getKeyColumns().get(0).getReference());

        if (!map.containsKey(entity.getEntityName())) {
            List<ForeignKey> initialData = new ArrayList<>();
            initialData.add(foreignKey);
            map.put(entity.getEntityName(), initialData);
        } else {
            List<ForeignKey> existingData = map.get(entity.getEntityName());
            existingData.add(foreignKey);
            map.put(entity.getEntityName(), existingData);
        }
    }

    public static void addToMapRemoveForeignKey(Entity entity, EntityField field, Map<String, List<String>> map) {
        String RemoveKeyName = String.format("FK_%s_%s", entity.getEntityName(),
                field.getRelation().getAssocEntity().getEntityName());

        if (!map.containsKey(entity.getEntityName())) {
            List<String> initialData = new ArrayList<>();
            initialData.add(RemoveKeyName);
            map.put(entity.getEntityName(), initialData);
        } else {
            List<String> existingData = map.get(entity.getEntityName());
            existingData.add(RemoveKeyName);
            map.put(entity.getEntityName(), existingData);
        }
    }

    public static void addToMapNoTypeString(Entity entity, EntityField field, Map<String, List<String>> map) {
        if (!map.containsKey(entity.getEntityName())) {
            List<String> initialData = new ArrayList<>();
            initialData.add(field.getFieldName());
            map.put(entity.getEntityName(), initialData);
        } else {
            List<String> existingData = map.get(entity.getEntityName());
            existingData.add(field.getFieldName());
            map.put(entity.getEntityName(), existingData);
        }
    }

    public static void addToMapNoTypeObject(Entity entity, EntityField field, Map<String, List<FieldMetadata>> map) {
        FieldMetadata fieldMetadata = new FieldMetadata(field.getFieldName());

        if (!map.containsKey(entity.getEntityName())) {
            List<FieldMetadata> initialData = new ArrayList<>();
            initialData.add(fieldMetadata);
            map.put(entity.getEntityName(), initialData);
        } else {
            List<FieldMetadata> existingData = map.get(entity.getEntityName());
            existingData.add(fieldMetadata);
            map.put(entity.getEntityName(), existingData);
        }
    }

    public static void addToMapWithType(Entity entity, EntityField field, Map<String, List<FieldMetadata>> map) {
        FieldMetadata fieldMetadata = new FieldMetadata(field.getFieldName(), field.getFieldType());

        if (!map.containsKey(entity.getEntityName())) {
            List<FieldMetadata> initialData = new ArrayList<>();
            initialData.add(fieldMetadata);
            map.put(entity.getEntityName(), initialData);
        } else {
            List<FieldMetadata> existingData = map.get(entity.getEntityName());
            existingData.add(fieldMetadata);
            map.put(entity.getEntityName(), existingData);
        }
    }

    public static void addToMapNewEntityFK(Entity entity, EntityField field, Map<String, List<FieldMetadata>> map) {
        FieldMetadata fieldMetadata = new FieldMetadata(field.getRelation().getKeyColumns().get(0).getField(),
                field.getRelation().getKeyColumns().get(0).getType());

        if (!map.containsKey(entity.getEntityName())) {
            List<FieldMetadata> initialData = new ArrayList<>();
            initialData.add(fieldMetadata);
            map.put(entity.getEntityName(), initialData);
        } else {
            List<FieldMetadata> existingData = map.get(entity.getEntityName());
            existingData.add(fieldMetadata);
            map.put(entity.getEntityName(), existingData);
        }
    }

    // Convert Create Table List to Query
    public static void convertCreateTableToQuery(queryTypes type, List<String> addedEntities, List<String> queries,
            HashMap<String, List<FieldMetadata>> addedReadOnly, HashMap<String, List<FieldMetadata>> addedFields) {
        if (Objects.requireNonNull(type) == queryTypes.ADD_TABLE) {
            for (String entity : addedEntities) {
                FieldMetadata primaryKey = addedReadOnly.get(entity).get(0);
                String primaryKeyName = primaryKey.getName();
                String primaryKeyType = primaryKey.getDataType();
                String addTableTemplate = "CREATE TABLE %s (%s %s PRIMARY KEY";

                StringBuilder query = new StringBuilder(
                        String.format(addTableTemplate, entity, primaryKeyName, getDataType(primaryKeyType)));

                String addFieldTemplate = ", %s %s";

                if (addedFields.get(entity) != null) {
                    for (FieldMetadata field : addedFields.get(entity)) {
                        query.append(
                                String.format(addFieldTemplate, field.getName(), getDataType(field.getDataType())));
                    }
                }

                queries.add(query + ");");
            }
        }
    }

    // Convert list to a MySQL query
    public static void convertListToQuery(queryTypes type, List<String> entities, List<String> queries) {
        if (Objects.requireNonNull(type) == queryTypes.REMOVE_TABLE) {
            for (String entity : entities) {
                String removeTableTemplate = "DROP TABLE %s;";
                queries.add(String.format(removeTableTemplate, entity));
            }
        }
    }

    // Convert map of String lists to a MySQL query
    public static void convertMapListToQuery(queryTypes type, Map<String, List<String>> map, List<String> queries) {
        switch (type) {
            case REMOVE_FIELD:
                for (String entity : map.keySet()) {
                    for (String field : map.get(entity)) {
                        String removeFieldTemplate = "ALTER TABLE %s DROP COLUMN %s;";

                        queries.add(String.format(removeFieldTemplate, entity, field));
                    }
                }
                break;

            case REMOVE_READONLY:
                for (String entity : map.keySet()) {
                    String removeReadOnlyTemplate = "ALTER TABLE %s DROP PRIMARY KEY;";

                    queries.add(String.format(removeReadOnlyTemplate, entity));
                }
                break;

            case REMOVE_FOREIGN_KEY:
                for (String entity : map.keySet()) {
                    for (String field : map.get(entity)) {
                        String[] fieldData = field.split(",");

                        String foreign_key_name = fieldData[0];
                        String removeForeignKeyTemplate = "ALTER TABLE %s DROP FOREIGN KEY %s;";

                        queries.add(String.format(removeForeignKeyTemplate, entity, foreign_key_name));
                    }
                }
                break;
            default:
                break;
        }
    }

    // Convert map of FieldMetadata lists to a MySQL query
    public static void convertMapToQuery(queryTypes type, Map<String, List<FieldMetadata>> map, List<String> queries,
            List<String> addedEntities) {
        switch (type) {
            case ADD_FIELD:
                for (String entity : map.keySet()) {
                    if (!addedEntities.contains(entity)) {
                        for (FieldMetadata field : map.get(entity)) {
                            String fieldName = field.getName();
                            String fieldType = getDataType(field.getDataType());
                            String addFieldTemplate = "ALTER TABLE %s ADD COLUMN %s %s;";

                            queries.add(String.format(addFieldTemplate, entity, fieldName, fieldType));
                        }
                    }
                }
                break;

            case CHANGE_TYPE:
                for (String entity : map.keySet()) {
                    for (FieldMetadata field : map.get(entity)) {
                        String fieldName = field.getName();
                        String fieldType = getDataType(field.getDataType());
                        String changeTypeTemplate = "ALTER TABLE %s MODIFY COLUMN %s %s;";

                        queries.add(String.format(changeTypeTemplate, entity, fieldName, fieldType));
                    }
                }
                break;

            case ADD_READONLY:
                for (String entity : map.keySet()) {
                    if (!addedEntities.contains(entity)) {
                        for (FieldMetadata field : map.get(entity)) {
                            String primaryKey = field.getName();
                            String addReadOnlyTemplate = "ALTER TABLE %s ADD PRIMARY KEY (%s);";

                            queries.add(String.format(addReadOnlyTemplate, entity, primaryKey));
                        }
                    }
                }
                break;

            default:
                break;
        }
    }

    // Convert map of ForeignKey lists to a MySQL query
    public static void convertFKMapToQuery(queryTypes type, Map<String, List<ForeignKey>> map, List<String> queries) {
        if (Objects.requireNonNull(type) == queryTypes.ADD_FOREIGN_KEY) {
            for (String entity : map.keySet()) {
                for (ForeignKey foreignKey : map.get(entity)) {

                    String foreign_key_name = foreignKey.getName();
                    String child_column_name = foreignKey.getColumnName();
                    String reference_table_name = foreignKey.getReferenceTable();
                    String reference_column_name = foreignKey.getReferenceColumn();
                    String addForeignKeyTemplate = "ALTER TABLE %s ADD CONSTRAINT %s FOREIGN KEY (%s) REFERENCES %s(%s);";

                    queries.add(String.format(addForeignKeyTemplate, entity, foreign_key_name, child_column_name,
                            reference_table_name, reference_column_name));
                }
            }
        }
    }

    // Convert Java data type to MySQL data type
    public static String getDataType(String dataType) {
        String resultType;

        switch (dataType) {
            case "string":
                resultType = "VARCHAR(191)";
                break;

            case "int":
                resultType = "INT";
                break;

            case "long":
                resultType = "BIGINT";
                break;

            case "float":
                resultType = "FLOAT";
                break;

            case "double":
                resultType = "DOUBLE";
                break;

            case "boolean":
                resultType = "TINYINT(1)";
                break;

            case "Date":
            case "LocalDate":
                resultType = "DATE";
                break;

            case "LocalTime":
                resultType = "TIME";
                break;

            case "LocalDateTime":
            case "time:Civil":
                resultType = "DATETIME";
                break;

            case "Blob":
            case "byte[]":
                resultType = "BLOB";
                break;

            case "Clob":
                resultType = "CLOB";
                break;

            default:
                resultType = "ERROR: VARIABLE NOT FOUND";
                break;
        }

        return resultType;

    }

    // Types of MySQL queries
    public enum queryTypes {
        ADD_TABLE,
        REMOVE_TABLE,
        ADD_FIELD,
        REMOVE_FIELD,
        CHANGE_TYPE,
        ADD_READONLY,
        REMOVE_READONLY,
        ADD_FOREIGN_KEY,
        REMOVE_FOREIGN_KEY
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