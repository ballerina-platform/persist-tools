package io.ballerina.persist.tools;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * persist tool in `bal build` command tests.
 */
public class BuildCodeGeneratorTest {
    public static final Path TARGET_DIR = Paths.get(System.getProperty("user.dir"), "build");
    public static final Path TEST_DISTRIBUTION_PATH = TARGET_DIR.resolve("ballerina-distribution");

    @Test(enabled = true)
    public void testBuildWithMysql() throws IOException, InterruptedException {
        String log = "Persist client and entity types generated successfully in " +
                "the persist_build_1 directory.";
        Path project = TARGET_DIR.resolve("generated-sources/tool_test_build_1");
        assertLogs(log, project);
    }

    @Test(enabled = true)
    public void testBuildWithInvalidTargetModule() throws IOException, InterruptedException {
        String log = "ERROR: invalid module name : 'persist_add_1' :\n" +
                "module name should follow the template <package_name>.<module_name>";
        Path project = TARGET_DIR.resolve("generated-sources/tool_test_build_2");
        assertLogs(log, project);
    }

    @Test(enabled = true)
    public void testBuildWithInvalidCharachtersInTargetModule() throws IOException, InterruptedException {
        String log = "ERROR: invalid module name : '*****' :\n" +
                "module name can only contain alphanumerics, underscores and periods";
        Path project = TARGET_DIR.resolve("generated-sources/tool_test_build_3");
        assertLogs(log, project);
    }

    @Test(enabled = true)
    public void testBuildWithInvalidLengthOfTargetModule() throws IOException, InterruptedException {
        String log = "ERROR: invalid module name : 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd" +
                "ddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd" +
                "dddddddddddddddddddddd' :\n" +
                "maximum length of module name is 256 characters";
        Path project = TARGET_DIR.resolve("generated-sources/tool_test_build_4");
        assertLogs(log, project);
    }

    @Test(enabled = true)
    public void testBuildWithInvalidDataSource() throws IOException, InterruptedException {
        String log = "ERROR: the persist layer supports one of data stores:";
        Path project = TARGET_DIR.resolve("generated-sources/tool_test_build_5");
        assertContainLogs(log, project);
    }

    @Test(enabled = true)
    public void testBuildWithoutEntities() throws IOException, InterruptedException {
        String log = "ERROR: the model definition file(model.bal) does not contain any entity definition.";
        Path project = TARGET_DIR.resolve("generated-sources/tool_test_build_6");
        assertLogs(log, project);
    }

    private void assertLogs(String log, Path project) throws IOException, InterruptedException {
        List<String> buildArgs = new LinkedList<>();
        Process process = executeRun(TEST_DISTRIBUTION_PATH.toString(), project, buildArgs);
        InputStream outStream = process.getErrorStream();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(outStream, StandardCharsets.UTF_8))) {
            Stream<String> logLines = br.lines();
            String generatedLog = logLines.collect(Collectors.joining(System.lineSeparator()));
            Assert.assertEquals(generatedLog, log);
            logLines.close();
        }
    }

    private void assertContainLogs(String log, Path project) throws IOException, InterruptedException {
        List<String> buildArgs = new LinkedList<>();
        Process process = executeRun(TEST_DISTRIBUTION_PATH.toString(), project, buildArgs);
        InputStream outStream = process.getErrorStream();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(outStream, StandardCharsets.UTF_8))) {
            Stream<String> logLines = br.lines();
            String generatedLog = logLines.collect(Collectors.joining(System.lineSeparator()));
            Assert.assertTrue(generatedLog.contains(log));
            logLines.close();
        }
    }

    /**
     * Ballerina run command.
     */
    public static Process executeRun(String distributionName, Path sourceDirectory,
                                     List<String> args) throws IOException, InterruptedException {
        args.add(0, "build");
        Process process = getProcessBuilderResults(distributionName, sourceDirectory, args);
        process.waitFor();
        return process;
    }

    /**
     *  Get Process from given arguments.
     * @param distributionName The name of the distribution.
     * @param sourceDirectory  The directory where the sources files are location.
     * @param args             The arguments to be passed to the build command.
     * @return process
     * @throws IOException          Error executing build command.
     */
    public static Process getProcessBuilderResults(String distributionName, Path sourceDirectory, List<String> args)
            throws IOException {
        String balFile = "bal";

        if (System.getProperty("os.name").startsWith("Windows")) {
            balFile = "bal.bat";
        }
        args.add(0, TEST_DISTRIBUTION_PATH.resolve(distributionName).resolve("bin").resolve(balFile).toString());
        ProcessBuilder pb = new ProcessBuilder(args);
        pb.directory(sourceDirectory.toFile());
        return pb.start();
    }

//    public static Process getProcessBuilderResults(String distributionName, Path sourceDirectory, List<String> args)
//            throws IOException {

//        if (System.getProperty("os.name").startsWith("Windows")) {
//            balFile = "bal.bat";
//        }
//        args.add(0, TEST_DISTRIBUTION_PATH.resolve(distributionName).resolve("bin").resolve(balFile).toString());
//        ProcessBuilder pb = new ProcessBuilder(args);
//        pb.directory(sourceDirectory.toFile());
//        return pb.start();
//    }
}
