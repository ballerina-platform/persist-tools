/*
 *  Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com).
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

import io.ballerina.persist.BalException;
import io.ballerina.persist.configuration.PersistConfiguration;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Unit tests for IntrospectorBuilder.
 */
public class IntrospectorBuilderTest {

    @Test
    public void testBuildMySqlIntrospector() throws BalException {
        Introspector introspector = IntrospectorBuilder.newBuilder()
                .withDatastore("mysql")
                .withHost("localhost")
                .withPort("3306")
                .withUser("root")
                .withPassword("password")
                .withDatabase("testdb")
                .withSourcePath("/path/to/project")
                .build();

        Assert.assertNotNull(introspector);
        Assert.assertTrue(introspector instanceof MySqlIntrospector);

        PersistConfiguration config = introspector.getPersistConfiguration();
        Assert.assertNotNull(config);
        Assert.assertEquals(config.getProvider(), "mysql");
        Assert.assertEquals(config.getSourcePath(), "/path/to/project");
        Assert.assertEquals(config.getDbConfig().getHost(), "localhost");
        Assert.assertEquals(config.getDbConfig().getPort(), 3306);
        Assert.assertEquals(config.getDbConfig().getUsername(), "root");
        Assert.assertEquals(config.getDbConfig().getDatabase(), "testdb");
    }

    @Test
    public void testBuildPostgreSqlIntrospector() throws BalException {
        Introspector introspector = IntrospectorBuilder.newBuilder()
                .withDatastore("postgresql")
                .withHost("localhost")
                .withPort("5432")
                .withUser("postgres")
                .withPassword("password")
                .withDatabase("testdb")
                .withSourcePath("/path/to/project")
                .build();

        Assert.assertNotNull(introspector);
        Assert.assertTrue(introspector instanceof PostgreSqlIntrospector);

        PersistConfiguration config = introspector.getPersistConfiguration();
        Assert.assertNotNull(config);
        Assert.assertEquals(config.getProvider(), "postgresql");
    }

    @Test
    public void testBuildMsSqlIntrospector() throws BalException {
        Introspector introspector = IntrospectorBuilder.newBuilder()
                .withDatastore("mssql")
                .withHost("localhost")
                .withPort("1433")
                .withUser("sa")
                .withPassword("password")
                .withDatabase("testdb")
                .withSourcePath("/path/to/project")
                .build();

        Assert.assertNotNull(introspector);
        Assert.assertTrue(introspector instanceof MsSqlIntrospector);

        PersistConfiguration config = introspector.getPersistConfiguration();
        Assert.assertNotNull(config);
        Assert.assertEquals(config.getProvider(), "sqlserver");
    }

    @Test
    public void testBuildWithDefaultMySqlPort() throws BalException {
        Introspector introspector = IntrospectorBuilder.newBuilder()
                .withDatastore("mysql")
                .withHost("localhost")
                .withUser("root")
                .withPassword("password")
                .withDatabase("testdb")
                .withSourcePath("/path/to/project")
                .build();

        PersistConfiguration config = introspector.getPersistConfiguration();
        Assert.assertEquals(config.getDbConfig().getPort(), 3306);
    }

    @Test
    public void testBuildWithDefaultPostgreSqlPort() throws BalException {
        Introspector introspector = IntrospectorBuilder.newBuilder()
                .withDatastore("postgresql")
                .withHost("localhost")
                .withUser("postgres")
                .withPassword("password")
                .withDatabase("testdb")
                .withSourcePath("/path/to/project")
                .build();

        PersistConfiguration config = introspector.getPersistConfiguration();
        Assert.assertEquals(config.getDbConfig().getPort(), 5432);
    }

    @Test
    public void testBuildWithDefaultMsSqlPort() throws BalException {
        Introspector introspector = IntrospectorBuilder.newBuilder()
                .withDatastore("mssql")
                .withHost("localhost")
                .withUser("sa")
                .withPassword("password")
                .withDatabase("testdb")
                .withSourcePath("/path/to/project")
                .build();

        PersistConfiguration config = introspector.getPersistConfiguration();
        Assert.assertEquals(config.getDbConfig().getPort(), 1433);
    }

    @Test
    public void testBuildWithTableSelection() throws BalException {
        Introspector introspector = IntrospectorBuilder.newBuilder()
                .withDatastore("mysql")
                .withHost("localhost")
                .withUser("root")
                .withPassword("password")
                .withDatabase("testdb")
                .withSourcePath("/path/to/project")
                .withTables("users,orders,products")
                .build();

        PersistConfiguration config = introspector.getPersistConfiguration();
        Assert.assertNotNull(config.getSelectedTables());
        Assert.assertEquals(config.getSelectedTables().size(), 3);
        Assert.assertTrue(config.getSelectedTables().contains("users"));
        Assert.assertTrue(config.getSelectedTables().contains("orders"));
        Assert.assertTrue(config.getSelectedTables().contains("products"));
    }

    @Test
    public void testBuildWithoutPassword() throws BalException {
        Introspector introspector = IntrospectorBuilder.newBuilder()
                .withDatastore("mysql")
                .withHost("localhost")
                .withUser("root")
                .withDatabase("testdb")
                .withSourcePath("/path/to/project")
                .build();

        Assert.assertNotNull(introspector);
        PersistConfiguration config = introspector.getPersistConfiguration();
        Assert.assertEquals(config.getDbConfig().getPassword(), "");
    }

    @Test(expectedExceptions = BalException.class, expectedExceptionsMessageRegExp = ".*datastore is required.*")
    public void testBuildWithoutDatastore() throws BalException {
        IntrospectorBuilder.newBuilder()
                .withHost("localhost")
                .withUser("root")
                .withPassword("password")
                .withDatabase("testdb")
                .withSourcePath("/path/to/project")
                .build();
    }

    @Test(expectedExceptions = BalException.class, expectedExceptionsMessageRegExp = ".*host is required.*")
    public void testBuildWithoutHost() throws BalException {
        IntrospectorBuilder.newBuilder()
                .withDatastore("mysql")
                .withUser("root")
                .withPassword("password")
                .withDatabase("testdb")
                .withSourcePath("/path/to/project")
                .build();
    }

    @Test(expectedExceptions = BalException.class, expectedExceptionsMessageRegExp = ".*user is required.*")
    public void testBuildWithoutUser() throws BalException {
        IntrospectorBuilder.newBuilder()
                .withDatastore("mysql")
                .withHost("localhost")
                .withPassword("password")
                .withDatabase("testdb")
                .withSourcePath("/path/to/project")
                .build();
    }

    @Test(expectedExceptions = BalException.class, expectedExceptionsMessageRegExp = ".*database is required.*")
    public void testBuildWithoutDatabase() throws BalException {
        IntrospectorBuilder.newBuilder()
                .withDatastore("mysql")
                .withHost("localhost")
                .withUser("root")
                .withPassword("password")
                .withSourcePath("/path/to/project")
                .build();
    }

    @Test(expectedExceptions = BalException.class, expectedExceptionsMessageRegExp = ".*unsupported data store.*")
    public void testBuildWithUnsupportedDatastore() throws BalException {
        IntrospectorBuilder.newBuilder()
                .withDatastore("oracle")
                .withHost("localhost")
                .withUser("root")
                .withPassword("password")
                .withDatabase("testdb")
                .withSourcePath("/path/to/project")
                .build();
    }

    @Test
    public void testBuilderMethodChaining() {
        // Verify that builder methods return the same instance for chaining
        IntrospectorBuilder builder = IntrospectorBuilder.newBuilder();
        Assert.assertSame(builder.withDatastore("mysql"), builder);
        Assert.assertSame(builder.withHost("localhost"), builder);
        Assert.assertSame(builder.withPort("3306"), builder);
        Assert.assertSame(builder.withUser("root"), builder);
        Assert.assertSame(builder.withPassword("password"), builder);
        Assert.assertSame(builder.withDatabase("testdb"), builder);
        Assert.assertSame(builder.withSourcePath("/path/to/project"), builder);
        Assert.assertSame(builder.withTables("users"), builder);
    }

    @Test
    public void testBuildWithEmptyStringValues() {
        // Empty strings should be treated as missing values
        try {
            IntrospectorBuilder.newBuilder()
                    .withDatastore("")
                    .withHost("localhost")
                    .withUser("root")
                    .withDatabase("testdb")
                    .withSourcePath("/path/to/project")
                    .build();
            Assert.fail("Expected BalException for empty datastore");
        } catch (BalException e) {
            Assert.assertTrue(e.getMessage().contains("datastore is required"));
        }
    }
}

