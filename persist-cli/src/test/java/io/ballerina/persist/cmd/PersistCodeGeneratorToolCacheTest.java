/*
 *  Copyright (c) 2026, WSO2 LLC. (http://www.wso2.org).
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

import io.ballerina.persist.dataservice.DataServiceOptions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

import static io.ballerina.persist.PersistToolsConstants.CACHE_FILE;

/**
 * Tests the build cache of the persist code generator tool.
 */
public class PersistCodeGeneratorToolCacheTest {

    private static final String MODEL = """
            import ballerina/persist as _;

            type User record {|
                readonly int id;
                string name;
            |};
            """;

    private Path workDir;
    private Path cachePath;
    private Path modelPath;

    @BeforeMethod
    public void setUp() throws IOException {
        workDir = Files.createTempDirectory("persist-cache-test");
        cachePath = workDir.resolve("cache");
        modelPath = workDir.resolve("model.bal");
        Files.writeString(modelPath, MODEL);
    }

    @AfterMethod
    public void tearDown() throws IOException {
        try (Stream<Path> paths = Files.walk(workDir)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(path);
            }
        }
    }

    @Test(description = "The cache key is the hex-encoded SHA-256 digest")
    public void testCacheKeyIsHex() throws Exception {
        String key = PersistCodeGeneratorTool.getHashValue(modelPath, settings(null));
        Assert.assertTrue(key.matches("[0-9a-f]{64}"), key);
    }

    @Test(description = "A shorter key written over a longer one replaces it")
    public void testShorterKeyReplacesLongerKey() throws Exception {
        PersistCodeGeneratorTool.updateCacheFile(cachePath, "f".repeat(100));
        String key = PersistCodeGeneratorTool.getHashValue(modelPath, settings(null));
        PersistCodeGeneratorTool.updateCacheFile(cachePath, key);

        Assert.assertEquals(Files.readString(cachePath.resolve(CACHE_FILE)), key);
        Assert.assertTrue(PersistCodeGeneratorTool.validateCache(cachePath, key));
    }

    @Test(description = "An unchanged model and entry match the cache")
    public void testUnchangedEntryMatchesCache() throws Exception {
        PersistCodeGeneratorTool.updateCacheFile(cachePath,
                PersistCodeGeneratorTool.getHashValue(modelPath, settings(null)));

        Assert.assertTrue(PersistCodeGeneratorTool.validateCache(cachePath,
                PersistCodeGeneratorTool.getHashValue(modelPath, settings(null))));
    }

    @Test(description = "Adding a data service to the entry of an unchanged model misses the cache")
    public void testAddedDataServiceMissesCache() throws Exception {
        PersistCodeGeneratorTool.updateCacheFile(cachePath,
                PersistCodeGeneratorTool.getHashValue(modelPath, settings(null)));
        DataServiceOptions dataService = new DataServiceOptions("http", "/", false, true);

        Assert.assertFalse(PersistCodeGeneratorTool.validateCache(cachePath,
                PersistCodeGeneratorTool.getHashValue(modelPath, settings(dataService))));
    }

    @Test(description = "Changing a data service option of an unchanged model misses the cache")
    public void testChangedDataServiceOptionMissesCache() throws Exception {
        PersistCodeGeneratorTool.updateCacheFile(cachePath, PersistCodeGeneratorTool.getHashValue(modelPath,
                settings(new DataServiceOptions("http", "/", false, true))));

        Assert.assertFalse(PersistCodeGeneratorTool.validateCache(cachePath, PersistCodeGeneratorTool.getHashValue(
                modelPath, settings(new DataServiceOptions("http", "/", true, true)))));
    }

    @Test(description = "A cache directory without a cache file misses the cache")
    public void testMissingCacheFileMissesCache() throws Exception {
        Files.createDirectories(cachePath);
        Assert.assertFalse(PersistCodeGeneratorTool.validateCache(cachePath,
                PersistCodeGeneratorTool.getHashValue(modelPath, settings(null))));
    }

    private static String settings(DataServiceOptions dataService) {
        return PersistCodeGeneratorTool.settings("mysql", null, "app", false, false, dataService);
    }
}
