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
package io.ballerina.persist.utils;

import io.ballerina.persist.BalException;
import io.ballerina.persist.configuration.PersistConfiguration;
import io.ballerina.projects.DependencyGraph;
import io.ballerina.projects.JvmTarget;
import io.ballerina.projects.Package;
import io.ballerina.projects.PackageManifest;
import io.ballerina.projects.Project;
import io.ballerina.projects.ResolvedPackageDependency;

import java.io.Console;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Scanner;

import static io.ballerina.persist.PersistToolsConstants.BALLERINA_MSSQL_DRIVER_NAME;
import static io.ballerina.persist.PersistToolsConstants.BALLERINA_MYSQL_DRIVER_NAME;
import static io.ballerina.persist.PersistToolsConstants.BALLERINA_POSTGRESQL_DRIVER_NAME;
import static io.ballerina.persist.PersistToolsConstants.MSSQL_CONNECTOR_NAME_PREFIX;
import static io.ballerina.persist.PersistToolsConstants.MYSQL_CONNECTOR_NAME_PREFIX;
import static io.ballerina.persist.PersistToolsConstants.PASSWORD;
import static io.ballerina.persist.PersistToolsConstants.POSTGRESQL_CONNECTOR_NAME_PREFIX;
import static io.ballerina.persist.PersistToolsConstants.PROPERTY_KEY_PATH;
import static io.ballerina.persist.PersistToolsConstants.USER;
import static io.ballerina.persist.nodegenerator.syntax.constants.BalSyntaxConstants.JDBC_URL_WITHOUT_DATABASE;

public class DatabaseConnector {

    private final String jdbcUrlWithDatabaseFormat;
    private final String driverClass;

    public DatabaseConnector (String jdbcUrlWithDatabaseFormat, String driverClass) {
        this.driverClass = driverClass;
        this.jdbcUrlWithDatabaseFormat = jdbcUrlWithDatabaseFormat;
    }

    public Connection getConnection(Driver driver, PersistConfiguration persistConfigurations,
                                     boolean withDB) throws SQLException {
        String host = persistConfigurations.getDbConfig().getHost();
        int port = persistConfigurations.getDbConfig().getPort();
        String user = persistConfigurations.getDbConfig().getUsername();
        String password = persistConfigurations.getDbConfig().getPassword();
        String database = persistConfigurations.getDbConfig().getDatabase();
        String provider = persistConfigurations.getProvider();
        String url;
        if (withDB) {
            url = String.format(this.jdbcUrlWithDatabaseFormat, provider, host, port, database);
        } else {
            url = String.format(JDBC_URL_WITHOUT_DATABASE, provider, host, port);
        }
        Properties props = new Properties();
        if (user != null) {
            props.put(USER, user);
        }
        if (password != null) {
            props.put(PASSWORD, password);
        }
        return driver.connect(url, props);
    }

    public Driver getJdbcDriver(JdbcDriverLoader driverLoader) throws BalException {
        Driver driver;
        try {
            Class<?> drvClass = driverLoader.loadClass(this.driverClass);
            driver = (Driver) drvClass.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            throw new BalException("required database driver class not found. " + e.getMessage());
        } catch (InstantiationException | InvocationTargetException e) {
            throw new BalException("the database driver instantiation is failed. " + e.getMessage());
        } catch (IllegalAccessException e) {
            throw new BalException("access denied while trying to instantiation the database driver. " +
                    e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new BalException("method not found while trying to instantiate jdbc driver. "
                    + e.getMessage());
        }
        return driver;
    }

    public JdbcDriverLoader getJdbcDriverLoader(Project balProject) throws BalException {
        return this.getJdbcDriverLoaderPrivate(balProject);
    }
    private JdbcDriverLoader getJdbcDriverLoaderPrivate(Project balProject) throws BalException {
        JdbcDriverLoader driverLoader = null;
        Path driverDirectoryPath = getDriverPath(balProject).getParent();
        if (Objects.nonNull(driverDirectoryPath)) {
            Path driverPath = driverDirectoryPath.toAbsolutePath();
            URL[] urls = {};
            try {
                driverLoader = new JdbcDriverLoader(urls, driverPath);
            } catch (IOException e) {
                throw new BalException("could not load the driver from the driver path. " + e.getMessage());
            }
        }
        return driverLoader;
    }

    private Path getDriverPath(Project balProject) throws BalException {
        String relativeLibPath;

        DependencyGraph<ResolvedPackageDependency> resolvedPackageDependencyDependencyGraph =
                balProject.currentPackage().getResolution().dependencyGraph();

        ResolvedPackageDependency root = resolvedPackageDependencyDependencyGraph.getRoot();

        Optional<ResolvedPackageDependency> mysqlDriverDependency = resolvedPackageDependencyDependencyGraph
                .getDirectDependencies(root).stream().
                filter(resolvedPackageDependency -> resolvedPackageDependency.packageInstance().
                        descriptor().toString().contains(BALLERINA_MYSQL_DRIVER_NAME)).findFirst();
        if (mysqlDriverDependency.isPresent()) {
            Package mysqlDriverPackage = mysqlDriverDependency.get().packageInstance();
            List<Map<String, Object>> dependencies = getDependencies(mysqlDriverPackage);
            for (Map<String, Object> dependency : dependencies) {
                if (dependency.get(PROPERTY_KEY_PATH).toString().contains(MYSQL_CONNECTOR_NAME_PREFIX)) {
                    relativeLibPath = dependency.get(PROPERTY_KEY_PATH).toString();
                    return mysqlDriverPackage.project().sourceRoot().resolve(relativeLibPath);
                }
            }
        }

        Optional<ResolvedPackageDependency> mssqlDriverDependency = resolvedPackageDependencyDependencyGraph
                .getDirectDependencies(root).stream().
                filter(resolvedPackageDependency -> resolvedPackageDependency.packageInstance().
                        descriptor().toString().contains(BALLERINA_MSSQL_DRIVER_NAME)).findFirst();

        if (mssqlDriverDependency.isPresent()) {
            Package mssqlDriverPackage = mssqlDriverDependency.get().packageInstance();
            List<Map<String, Object>> dependencies = getDependencies(mssqlDriverPackage);
            for (Map<String, Object> dependency : dependencies) {
                if (dependency.get(PROPERTY_KEY_PATH).toString().contains(MSSQL_CONNECTOR_NAME_PREFIX)) {
                    relativeLibPath = dependency.get(PROPERTY_KEY_PATH).toString();
                    return mssqlDriverPackage.project().sourceRoot().resolve(relativeLibPath);
                }
            }
        }

        Optional<ResolvedPackageDependency> postgresqlDriverDependency = resolvedPackageDependencyDependencyGraph
                .getDirectDependencies(root).stream().
                filter(resolvedPackageDependency -> resolvedPackageDependency.packageInstance().
                        descriptor().toString().contains(BALLERINA_POSTGRESQL_DRIVER_NAME)).findFirst();

        if (postgresqlDriverDependency.isPresent()) {
            Package postgresqlDriverPackage = postgresqlDriverDependency.get().packageInstance();
            List<Map<String, Object>> dependencies = getDependencies(postgresqlDriverPackage);
            for (Map<String, Object> dependency : dependencies) {
                if (dependency.get(PROPERTY_KEY_PATH).toString().contains(POSTGRESQL_CONNECTOR_NAME_PREFIX)) {
                    relativeLibPath = dependency.get(PROPERTY_KEY_PATH).toString();
                    return postgresqlDriverPackage.project().sourceRoot().resolve(relativeLibPath);
                }
            }
        }

        throw new BalException("failed to retrieve driver path in the local cache.");
    }
    private static List<Map<String, Object>> getDependencies(Package driverPackage) {
        List<Map<String, Object>> dependencies = new ArrayList<>();
        for (JvmTarget jvmTarget : JvmTarget.values()) {
            PackageManifest.Platform platform = driverPackage.manifest().platform(jvmTarget.code());
            if (platform != null) {
                dependencies.addAll(platform.dependencies());
            }
        }
        return dependencies;
    }
    public static String readDatabasePassword(Scanner scanner, PrintStream errStream) {
        String password;
        Console console = System.console();
        if (console == null) {
            errStream.println("[WARNING] console could not be detected. falling back to standard input.");
            errStream.print("Database Password: ");
            password = scanner.nextLine();
        } else {
            password = new String(console.readPassword("Database Password: "));
        }
        return password;
    }
}
