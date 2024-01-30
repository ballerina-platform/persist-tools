package io.ballerina.persist.utils;

import io.ballerina.persist.BalException;
import io.ballerina.projects.DependencyGraph;
import io.ballerina.projects.JvmTarget;
import io.ballerina.projects.Package;
import io.ballerina.projects.PackageManifest;
import io.ballerina.projects.Project;
import io.ballerina.projects.ResolvedPackageDependency;

import java.io.IOException;
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

import static io.ballerina.persist.PersistToolsConstants.BALLERINA_MSSQL_DRIVER_NAME;
import static io.ballerina.persist.PersistToolsConstants.BALLERINA_MYSQL_DRIVER_NAME;
import static io.ballerina.persist.PersistToolsConstants.BALLERINA_POSTGRESQL_DRIVER_NAME;
import static io.ballerina.persist.PersistToolsConstants.MSSQL_CONNECTOR_NAME_PREFIX;
import static io.ballerina.persist.PersistToolsConstants.MYSQL_CONNECTOR_NAME_PREFIX;
import static io.ballerina.persist.PersistToolsConstants.MYSQL_DRIVER_CLASS;
import static io.ballerina.persist.PersistToolsConstants.POSTGRESQL_CONNECTOR_NAME_PREFIX;
import static io.ballerina.persist.PersistToolsConstants.PROPERTY_KEY_PATH;

public class DatabaseConnector {


    public Connection getConnection(Driver driver) throws SQLException {
        return driver.connect("jdbc:mysql://root:haritha@172.20.175.103:3306/information_schema?schema=public",
                new Properties());
    }

    public Driver getJdbcDriver(JdbcDriverLoader driverLoader) throws BalException {
        Driver driver;
        try {
            Class<?> drvClass = driverLoader.loadClass(MYSQL_DRIVER_CLASS);
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
}
