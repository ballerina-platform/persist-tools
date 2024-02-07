package io.ballerina.persist.nodegenerator;

import io.ballerina.persist.BalException;
import io.ballerina.persist.nodegenerator.syntax.sources.DbModelGenSyntaxTree;
import io.ballerina.persist.utils.BalProjectUtils;
import io.ballerina.projects.Project;
import org.ballerinalang.formatter.core.Formatter;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DriverResolver {

    private final Path driverImportFile;

    public DriverResolver(String sourcePath) {
        driverImportFile = Paths.get(sourcePath, "persist/driver.bal");
    }

    public Project resolveDriverDependencies() throws BalException {
        createDriverImportFile();
        return BalProjectUtils.buildDriverFile(driverImportFile);
    }


    private void createDriverImportFile() throws BalException {
        DbModelGenSyntaxTree dbModelGenSyntaxTree = new DbModelGenSyntaxTree();
        try {
            writeOutputFile
                    (Formatter.format(dbModelGenSyntaxTree.createInitialDriverImportFile().toSourceCode()),
                            driverImportFile);
        } catch (Exception e) {
            throw new BalException("ERROR: failed to create driver import file. " + e.getMessage());
        }
    }
    private void writeOutputFile(String syntaxTree, Path outPath) throws IOException {
        try (PrintWriter writer = new PrintWriter(outPath.toString(), StandardCharsets.UTF_8)) {
            writer.println(syntaxTree);
        }
    }

    public void deleteDriverFile() throws BalException {
        try {
            Files.deleteIfExists(driverImportFile);
        } catch (IOException e) {
            throw new BalException("ERROR: failed to delete driver import file. " + e.getMessage());
        }
    }

}
