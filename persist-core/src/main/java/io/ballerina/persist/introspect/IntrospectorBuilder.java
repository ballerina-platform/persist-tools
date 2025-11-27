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
import io.ballerina.persist.PersistToolsConstants;
import io.ballerina.persist.configuration.DatabaseConfiguration;
import io.ballerina.persist.configuration.PersistConfiguration;

import java.util.Objects;

/**
 * Builder class for constructing Introspector instances with database configuration.
 * Provides a fluent API for setting connection parameters and building the appropriate
 * introspector implementation based on the datastore type.
 *
 * <p>Example usage:
 * <pre>{@code
 * IntrospectorBuilder.Result result = IntrospectorBuilder.newBuilder()
 *     .withDatastore("mysql")
 *     .withHost("localhost")
 *     .withPort("3306")
 *     .withUser("root")
 *     .withPassword("password")
 *     .withDatabase("mydb")
 *     .withSourcePath("/path/to/project")
 *     .withTables("users,orders")
 *     .build();
 *
 * Module module = result.introspectDatabase();
 * }</pre>
 *
 * @since 1.8.0
 */
public class IntrospectorBuilder {

    private String datastore;
    private String host;
    private String port;
    private String user;
    private String password;
    private String database;
    private String sourcePath;
    private String tables;
    private PersistConfiguration persistConfiguration;

    private IntrospectorBuilder() {
        // Private constructor to enforce builder pattern
    }

    /**
     * Creates a new IntrospectorBuilder instance.
     *
     * @return a new IntrospectorBuilder
     */
    public static IntrospectorBuilder newBuilder() {
        return new IntrospectorBuilder();
    }

    /**
     * Sets the datastore type (e.g., "mysql", "postgresql", "mssql").
     *
     * @param datastore the datastore type
     * @return this builder instance for method chaining
     */
    public IntrospectorBuilder withDatastore(String datastore) {
        this.datastore = datastore;
        return this;
    }

    /**
     * Sets the database host address.
     *
     * @param host the host address
     * @return this builder instance for method chaining
     */
    public IntrospectorBuilder withHost(String host) {
        this.host = host;
        return this;
    }

    /**
     * Sets the database port number.
     *
     * @param port the port number as a string
     * @return this builder instance for method chaining
     */
    public IntrospectorBuilder withPort(String port) {
        this.port = port;
        return this;
    }

    /**
     * Sets the database user name.
     *
     * @param user the user name
     * @return this builder instance for method chaining
     */
    public IntrospectorBuilder withUser(String user) {
        this.user = user;
        return this;
    }

    /**
     * Sets the database password.
     *
     * @param password the password
     * @return this builder instance for method chaining
     */
    public IntrospectorBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    /**
     * Sets the database name.
     *
     * @param database the database name
     * @return this builder instance for method chaining
     */
    public IntrospectorBuilder withDatabase(String database) {
        this.database = database;
        return this;
    }

    /**
     * Sets the source path of the Ballerina project.
     *
     * @param sourcePath the absolute path to the Ballerina project directory
     * @return this builder instance for method chaining
     */
    public IntrospectorBuilder withSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
        return this;
    }

    /**
     * Sets the comma-separated list of table names to introspect.
     * If not set or empty, all tables will be introspected.
     *
     * @param tables comma-separated table names (e.g., "users,orders,products")
     * @return this builder instance for method chaining
     */
    public IntrospectorBuilder withTables(String tables) {
        this.tables = tables;
        return this;
    }

    /**
     * Builds and returns an Introspector instance.
     * Validates all required parameters and creates the appropriate introspector
     * implementation based on the datastore type. Default ports are assigned if
     * not explicitly set.
     *
     * @return the configured Introspector instance
     * @throws BalException if required parameters are missing or invalid, or if
     *                      the datastore type is unsupported
     */
    public Introspector build() throws BalException {
        // Validate required parameters
        validateRequiredParameters();

        // Apply default ports if not specified
        applyDefaultPort();

        // Build PersistConfiguration
        PersistConfiguration persistConfiguration = buildPersistConfiguration();

        // Create the appropriate introspector based on datastore type
        return createIntrospector(persistConfiguration);
    }

    private void validateRequiredParameters() throws BalException {
        if (Objects.isNull(datastore) || datastore.trim().isEmpty()) {
            throw new BalException("datastore is required");
        }
        switch (this.datastore) {
            case PersistToolsConstants.SupportedDataSources.MYSQL_DB:
            case PersistToolsConstants.SupportedDataSources.POSTGRESQL_DB:
            case PersistToolsConstants.SupportedDataSources.MSSQL_DB:
                break;
            default:
                throw new BalException("unsupported data store: '" + datastore + "'");
        }
        if (Objects.isNull(host) || host.trim().isEmpty()) {
            throw new BalException("host is required");
        }
        if (Objects.isNull(user) || user.trim().isEmpty()) {
            throw new BalException("user is required");
        }
        if (Objects.isNull(database) || database.trim().isEmpty()) {
            throw new BalException("database is required");
        }
        if (Objects.isNull(sourcePath) || sourcePath.trim().isEmpty()) {
            throw new BalException("sourcePath is required");
        }
        // Password can be empty/null for some databases, so we don't validate it
    }

    private Introspector createIntrospector(PersistConfiguration persistConfiguration) throws BalException {
        switch (this.datastore) {
            case PersistToolsConstants.SupportedDataSources.MYSQL_DB:
                return new MySqlIntrospector(persistConfiguration);
            case PersistToolsConstants.SupportedDataSources.POSTGRESQL_DB:
                return new PostgreSqlIntrospector(persistConfiguration);
            case PersistToolsConstants.SupportedDataSources.MSSQL_DB:
                return new MsSqlIntrospector(persistConfiguration);
            default:
                throw new BalException("unsupported data store: '" + datastore + "'");
        }
    }

    private void applyDefaultPort() {
        if (Objects.isNull(port)) {
            switch (this.datastore) {
                case PersistToolsConstants.SupportedDataSources.MYSQL_DB:
                    port = "3306";
                    break;
                case PersistToolsConstants.SupportedDataSources.POSTGRESQL_DB:
                    port = "5432";
                    break;
                case PersistToolsConstants.SupportedDataSources.MSSQL_DB:
                    port = "1433";
                    break;
                default:
                    // No default port for unsupported datastores
                    break;
            }
        }
    }

    private PersistConfiguration buildPersistConfiguration() throws BalException {
        PersistConfiguration persistConfiguration = new PersistConfiguration();
        persistConfiguration.setProvider(datastore);
        persistConfiguration.setSourcePath(sourcePath);

        // Set database configuration
        DatabaseConfiguration dbConfig = new DatabaseConfiguration(
                this.host,
                this.user,
                this.password != null ? this.password : "",
                this.port,
                this.database
        );
        persistConfiguration.setDbConfig(dbConfig);

        // Set selected tables if specified
        if (tables != null && !tables.trim().isEmpty()) {
            persistConfiguration.setSelectedTables(tables);
        }

        return persistConfiguration;
    }
}

