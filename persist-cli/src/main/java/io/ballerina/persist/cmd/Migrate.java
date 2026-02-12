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
import io.ballerina.persist.models.Index;
import io.ballerina.persist.models.MigrationDataHolder;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static io.ballerina.persist.PersistToolsConstants.MIGRATIONS;
import static io.ballerina.persist.PersistToolsConstants.MODEL_FILE;
import static io.ballerina.persist.PersistToolsConstants.PERSIST_DIRECTORY;
import static io.ballerina.persist.nodegenerator.syntax.utils.SqlScriptUtils.getTableNameWithSchema;

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

    @CommandLine.Parameters
    public List<String> argList;
    @CommandLine.Option(names = { "--datastore" })
    private String datastore = "mysql";

    public Migrate() {
        this("");
    }

    public Migrate(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    @CommandLine.Option(names = { "-h", "--help" }, hidden = true)
    private boolean helpFlag;

    @CommandLine.Option(names = { "--model" })
    private String model;

    @Override
    public void execute() {
        if (helpFlag) {
            String commandUsageInfo = BLauncherCmd.getCommandUsageInfo(COMMAND_IDENTIFIER,
                    Migrate.class.getClassLoader());
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

        String migrationName = argList.getFirst();

        // Returns the path of the bal file in the persist directory
        Path schemaFilePath;
        try {
            schemaFilePath = BalProjectUtils.getSchemaFilePath(this.sourcePath, model);
        } catch (BalException e) {
            errStream.println("Error: " + e.getMessage());
            return;
        }

        migrate(migrationName, projectPath, this.sourcePath, schemaFilePath, model);

    }

    private static void migrate(String migrationName, Path projectDirPath, String sourcePath, Path schemaFilePath,
            String model) {
        if (schemaFilePath != null) {
            Path persistDirPath = Paths.get(projectDirPath.toString(), PERSIST_DIRECTORY);

            // Create a File object for the persist directory
            File persistDir = new File(persistDirPath.toString());

            // Determine migrations directory based on model
            File migrationsDir;
            if (model != null && !model.trim().isEmpty()) {
                // Model-specific migrations: persist/{model}/migrations/
                File modelDir = new File(persistDir, model);
                migrationsDir = new File(modelDir, MIGRATIONS);
            } else {
                // Root model migrations: persist/migrations/
                migrationsDir = new File(persistDir, MIGRATIONS);
            }

            // Check if the migrations directory exists
            if (!migrationsDir.exists()) {
                // Create the migrations directory (and parent directories if needed)
                boolean created = migrationsDir.mkdirs();
                if (!created) {
                    errStream.println("Error: failed to create migrations directory inside the persist directory");
                    return;
                }

                Module entityModule;
                try {
                    entityModule = BalProjectUtils.getEntities(schemaFilePath);
                } catch (BalException e) {
                    errStream.println("Error getting entities: " + e.getMessage());
                    return;
                }

                String newMigration = createTimestampFolder(migrationName, migrationsDir);

                createTimestampDirectory(newMigration);

                Path newMigrationPath = Paths.get(newMigration);

                if (!entityModule.getEntityMap().isEmpty()) {
                    try {
                        // Generate the SQL script
                        SourceGenerator.addSqlScriptFile("the migrate command",
                                SqlScriptUtils.generateSqlScript(entityModule.getEntityMap().values(),
                                        PersistToolsConstants.SupportedDataSources.MYSQL_DB),
                                newMigrationPath);
                    } catch (BalException e) {
                        errStream.println("ERROR: failed to generate SQL script " + e.getMessage());
                        return;
                    }

                    try {
                        // Copy the source file to the destination folder with standard name
                        Files.copy(schemaFilePath, newMigrationPath.resolve(MODEL_FILE));
                    } catch (IOException e) {
                        errStream.println("Error: Copying file failed: " + e.getMessage());
                        return;
                    }

                    //Get the relative path of the migration directory from the project root
                    Path relativePath = Paths.get("").toAbsolutePath().relativize(newMigrationPath);

                    List<String> differences = new ArrayList<>();
                    differences.add("Table " + String.join(", ", entityModule.getEntityMap().keySet())
                            + " has been added");
                    printDetailedListOfDifferences(differences);
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
                        findLatestBalFile(getDirectoryPaths(migrationsDir.toString()), sourcePath, model));
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
    private static Path findLatestBalFile(List<String> folderNames, String sourcePath, String model) {
        if (folderNames.size() == 1) {
            return findBalFileInFolder(folderNames.getFirst(), sourcePath, model);
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

        return findBalFileInFolder(latestTimestamp, sourcePath, model);
    }

    // Find the .bal file in the given folder
    private static Path findBalFileInFolder(String folderName, String sourcePath, String model) {
        Path folderPath;
        if (model != null && !model.trim().isEmpty()) {
            // Model-specific path: persist/{model}/migrations/{timestamp}/
            folderPath = Paths.get(sourcePath).resolve(PERSIST_DIRECTORY)
                    .resolve(model).resolve(MIGRATIONS).resolve(folderName).toAbsolutePath();
        } else {
            // Root model path: persist/migrations/{timestamp}/
            folderPath = Paths.get(sourcePath).resolve(PERSIST_DIRECTORY)
                    .resolve(MIGRATIONS).resolve(folderName).toAbsolutePath();
        }

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
        MigrationDataHolder migrationDataHolder = new MigrationDataHolder();
        // Compare entities in previousModel and currentModel
        for (Entity previousModelEntity : previousModel.getEntityMap().values()) {
            Entity currentModelEntity = currentModel.getEntityMap().get(previousModelEntity.getEntityName());

            // Check if currentModelEntity exists
            if (currentModelEntity == null) {
                migrationDataHolder.removeTable(previousModelEntity.getTableName());
                continue;
            }

            // Check if their table names are changed (through annotations)
            if (!Objects.equals(currentModelEntity.getTableName(), previousModelEntity.getTableName())) {
                migrationDataHolder.renameTable(previousModelEntity.getTableName(), currentModelEntity.getTableName());
            }

            // Check if the primary key fields has been removed
            if (previousModelEntity.getKeys().size() > currentModelEntity.getKeys().size()) {
                migrationDataHolder.changePrimaryKey(currentModelEntity.getTableName());
            }

            // Compare fields in previousModelEntity and currentModelEntity
            for (EntityField previousModelField : previousModelEntity.getFields()) {
                EntityField currentModelField = currentModelEntity
                        .getFieldByName(previousModelField.getFieldName());

                // Check if currentModelField exists and if foreign key was removed
                if (currentModelField == null) {
                    if (previousModelField.getRelation() == null) {
                        migrationDataHolder.removeColumn(previousModelEntity.getTableName(),
                                previousModelField.getFieldColumnName());
                    } else if (previousModelField.getRelation().isOwner()) {
                        migrationDataHolder.removeForeignKey(previousModelEntity.getTableName(), previousModelField);
                    }
                    continue;
                }

                // Check if the field names are changed (through annotations)
                if (!Objects.equals(currentModelField.getFieldColumnName(), previousModelField.getFieldColumnName())) {
                    migrationDataHolder.renameColumn(currentModelEntity.getTableName(),
                            previousModelField.getFieldColumnName(), currentModelField.getFieldColumnName());
                }

                if (currentModelField.getRelation() != null &&
                        previousModelField.getRelation() != null &&
                        currentModelField.getRelation().isOwner() &&
                        !Objects.equals(currentModelField.getRelation().getKeyColumns(),
                                previousModelField.getRelation().getKeyColumns())) {

                    if (isOnlyColumnsRenamed(previousModelField.getRelation().getKeyColumns(),
                            currentModelField.getRelation().getKeyColumns())) {
                        for (int i = 0; i < previousModelField.getRelation().getKeyColumns().size(); i++) {
                            migrationDataHolder.renameColumn(currentModelEntity.getTableName(),
                                    previousModelField.getRelation().getKeyColumns().get(i).getColumnName(),
                                    currentModelField.getRelation().getKeyColumns().get(i).getColumnName());
                        }
                    } else {
                        migrationDataHolder.recreateForeignKey(currentModelEntity.getTableName(), previousModelField,
                                currentModelField);
                    }
                }

                // Compare data types
                if (!previousModelField.getFieldType().equals(currentModelField.getFieldType()) ||
                        !Objects.equals(previousModelField.getSqlType(), currentModelField.getSqlType()) ||
                        !Objects.equals(previousModelField.isOptionalType(), currentModelField.isOptionalType()) ||
                        !Objects.equals(previousModelField.isDbGenerated(), currentModelField.isDbGenerated())
                ) {
                    migrationDataHolder.modifyColumn(currentModelEntity.getTableName(), previousModelField,
                            currentModelField);
                }

                // Compare readonly fields
                if (!previousModelEntity.getKeys().contains(previousModelField)
                        && currentModelEntity.getKeys().contains(currentModelField)) {
                    migrationDataHolder.changePrimaryKey(currentModelEntity.getTableName());
                }

            }

            // Check for added fields and for added foreign keys
            for (EntityField currentModelField : currentModelEntity.getFields()) {
                EntityField previousModelField = previousModelEntity.getFieldByName(currentModelField.getFieldName());

                if (previousModelField == null) {
                    if (currentModelField.getRelation() == null) {
                        migrationDataHolder.addColumn(currentModelEntity.getTableName(), currentModelField,
                                currentModelEntity.getKeys().contains(currentModelField));
                    } else if (currentModelField.getRelation().isOwner()) {
                        migrationDataHolder.createForeignKeys(currentModelEntity.getTableName(), currentModelField);
                    }
                }
            }
        }

        // Check for added entities
        for (Entity currentModelEntity : currentModel.getEntityMap().values()) {
            Entity previousModelEntity = previousModel.getEntityMap().get(currentModelEntity.getEntityName());
            if (previousModelEntity == null &&
                    !migrationDataHolder.isEntityRenamed(currentModelEntity.getTableName())) {
                migrationDataHolder.addTable(currentModelEntity.getTableName());
            }
        }

        // Check for index changes
        HashMap<String, List<Index>> previousIndexes = getIndexesFromModule(previousModel);
        HashMap<String, List<Index>> currentIndexes = getIndexesFromModule(currentModel);
        processIndexDifferences(previousIndexes, currentIndexes, migrationDataHolder);

        HashMap<String, List<Index>> previousUniqueIndexes = getUniqueIndexesFromModule(previousModel);
        HashMap<String, List<Index>> currentUniqueIndexes = getUniqueIndexesFromModule(currentModel);
        processIndexDifferences(previousUniqueIndexes, currentUniqueIndexes, migrationDataHolder);

        // Convert differences to queries (ordered)
        addDropTableQueries(migrationDataHolder.getRemovedEntities(), queries);
        addDropForeignKeyQueries(migrationDataHolder.getRemovedForeignKeys(), queries);
        addDropPrimaryKeyQueries(migrationDataHolder.getPrimaryKeyChangedEntities(),
                migrationDataHolder.getAddedEntities(), queries);
        addDropColumnQueries(migrationDataHolder.getRemovedFields(), queries);
        addCreateTableQueries(migrationDataHolder.getAddedEntities(), currentModel, queries);
        addRenameTableQueries(migrationDataHolder.getRenamedEntities(), queries);
        addRenameFieldQueries(migrationDataHolder.getRenamedFields(), queries);
        addCreateFieldQueries(migrationDataHolder.getAddedFields(), queries);
        addCreatePrimaryKeyQueries(migrationDataHolder.getPrimaryKeyChangedEntities(),
                migrationDataHolder.getAddedEntities(), currentModel, queries);
        addCreateForeignKeyQueries(migrationDataHolder.getAddedForeignKeys(), queries);
        addModifyColumnTypeQueries(migrationDataHolder.getChangedFieldTypes(), queries);
        addDropIndexQueries(migrationDataHolder.getRemovedIndexes(), queries);
        addCreateIndexQueries(migrationDataHolder.getAddedIndexes(), queries);
        printDetailedListOfDifferences(migrationDataHolder.getDifferences());

        return queries;
    }

    private static void processIndexDifferences(HashMap<String, List<Index>> previousIndexes,
                                                HashMap<String, List<Index>> currentIndexes,
                                                MigrationDataHolder migrationDataHolder) {
        for (Map.Entry<String, List<Index>> entry : previousIndexes.entrySet()) {
            if (migrationDataHolder.getRemovedEntities().contains(entry.getKey())) {
                continue;
            }
            List<Index> previousIndexList = entry.getValue();
            List<Index> currentIndexList = currentIndexes.get(entry.getKey());
            // if all the indexes are removed from the entity
            if (Objects.isNull(currentIndexList)) {
                for (Index index : previousIndexList) {
                    migrationDataHolder.removeIndex(entry.getKey(), index);
                }
                continue;
            }
            for (Index previousIndex : previousIndexList) {
                boolean isIndexFound = false;
                for (Index currentIndex : currentIndexList) {
                    if (previousIndex.getIndexName().equals(currentIndex.getIndexName())) {
                        isIndexFound = true;
                        // Check if the fields are changed
                        if (!Objects.equals(currentIndex.getFields().size(), previousIndex.getFields().size())) {
                            migrationDataHolder.removeIndex(entry.getKey(), previousIndex);
                            migrationDataHolder.addIndex(entry.getKey(), currentIndex);
                        } else {
                            for (int i = 0; i < currentIndex.getFields().size(); i++) {
                                if (!currentIndex.getFields().get(i).getFieldName()
                                        .equals(previousIndex.getFields().get(i).getFieldName())) {
                                    migrationDataHolder.removeIndex(entry.getKey(), previousIndex);
                                    migrationDataHolder.addIndex(entry.getKey(), currentIndex);
                                    break;
                                }
                            }
                        }
                        break;
                    }
                }
                // Remove indexes that are not found in the current model
                if (!isIndexFound) {
                    migrationDataHolder.removeIndex(entry.getKey(), previousIndex);
                }
            }
        }
        // Add new indexes
        for (Map.Entry<String, List<Index>> entry : currentIndexes.entrySet()) {
            String entity = entry.getKey();
            for (Index index : entry.getValue()) {
                if (previousIndexes.get(entity) == null || previousIndexes.get(entity).stream()
                        .noneMatch(previousIndex -> previousIndex.getIndexName().equals(index.getIndexName()))) {
                    migrationDataHolder.addIndex(entity, index);
                }
            }
        }
    }

    private static boolean isOnlyColumnsRenamed(List<Relation.Key> previousKeys, List<Relation.Key> currentKeys) {
        if (!Objects.equals(previousKeys.size(), currentKeys.size())) {
            return false;
        }
        for (int i = 0; i < previousKeys.size(); i++) {
            if (!previousKeys.get(i).isOnlyColumnRenamed(currentKeys.get(i))) {
                return false;
            }
        }
        return true;
    }

    private static void printDetailedListOfDifferences(List<String> differences) {
        errStream.println(System.lineSeparator() + "Detailed list of differences: ");
        if (!differences.isEmpty()) {
            differences.forEach(difference -> errStream.println("-- " + difference));
            errStream.println();
        } else {
            errStream.println("-- No differences found" + System.lineSeparator());
        }
    }

    private static HashMap<String, List<Index>> getIndexesFromModule(Module module) {
        HashMap<String, List<Index>> indexMap = new HashMap<>();
        for (Entity entity : module.getEntityMap().values()) {
            if (entity.getIndexes().isEmpty()) {
                continue;
            }
            indexMap.put(entity.getTableName(), entity.getIndexes());
        }
        return indexMap;
    }

    private static HashMap<String, List<Index>> getUniqueIndexesFromModule(Module module) {
        HashMap<String, List<Index>> indexMap = new HashMap<>();
        for (Entity entity : module.getEntityMap().values()) {
            if (entity.getUniqueIndexes().isEmpty()) {
                continue;
            }
            indexMap.put(entity.getTableName(), entity.getUniqueIndexes());
        }
        return indexMap;
    }

    private static void addRenameFieldQueries(Map<String, List<MigrationDataHolder.NameMapping>> renamedFields,
                                              List<String> queries) {
        String renameFieldTemplate = "ALTER TABLE %s%nRENAME COLUMN %s TO %s;%n";
        for (Map.Entry<String, List<MigrationDataHolder.NameMapping>> entry : renamedFields.entrySet()) {
            for (MigrationDataHolder.NameMapping nameMapping : entry.getValue()) {
                queries.add(String.format(renameFieldTemplate, entry.getKey(), nameMapping.oldName(),
                        nameMapping.newName()));
            }
        }
    }

    private static void addRenameTableQueries(List<MigrationDataHolder.NameMapping> renamedEntities,
                                              List<String> queries) {
        String renameTableTemplate = "RENAME TABLE %s TO %s;%n";
        for (MigrationDataHolder.NameMapping nameMapping : renamedEntities) {
            queries.add(String.format(renameTableTemplate, nameMapping.oldName(), nameMapping.newName()));
        }
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

    private static void addCreateTableQueries(List<String> addedEntities, Module currentModel,
                                              List<String> queries) {
        for (String tableName : addedEntities) {
            Optional<Entity> entity = currentModel.getEntityByTableName(tableName);
            if (entity.isPresent()) {
                try {
                    queries.add(SqlScriptUtils.generateCreateTableQuery(entity.get(), new HashMap<>(),
                            getTableNameWithSchema(entity.get(), PersistToolsConstants.SupportedDataSources.MYSQL_DB),
                            PersistToolsConstants.SupportedDataSources.MYSQL_DB) + System.lineSeparator());
                } catch (BalException e) {
                    errStream.println("ERROR: failed to generate create table query: " + e.getMessage());
                }
            }
        }
    }

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

    private static void addCreateIndexQueries(Map<String, List<Index>> map, List<String> queries) {
        String addIndexTemplate = "CREATE%s INDEX %s ON %s(%s);%n";
        for (Map.Entry<String, List<Index>> entry : map.entrySet()) {
            String entity = entry.getKey();
            for (Index index : entry.getValue()) {
                queries.add(String.format(addIndexTemplate, index.isUnique() ? " UNIQUE" : "", index.getIndexName(),
                        entity, index.getFields().stream().map(EntityField::getFieldColumnName)
                                .reduce((a, b) -> a + ", " + b).orElse("")));
            }
        }
    }

    private static void addDropIndexQueries(Map<String, List<Index>> map, List<String> queries) {
        String dropIndexTemplate = "DROP INDEX %s ON %s;%n";
        for (Map.Entry<String, List<Index>> entry : map.entrySet()) {
            String entity = entry.getKey();
            for (Index index : entry.getValue()) {
                queries.add(String.format(dropIndexTemplate, index.getIndexName(), entity));
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
