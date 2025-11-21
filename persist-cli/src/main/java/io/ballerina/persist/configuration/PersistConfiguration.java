/*
 * Copyright (c) 2022, WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.ballerina.persist.configuration;

import io.ballerina.persist.PersistToolsConstants;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The configuration class for the persist tool.
 * @since 0.1.0
 */
public class PersistConfiguration {

    private String provider;
    private DatabaseConfiguration dbConfig;
    private String sourcePath;
    private List<String> selectedTables;

    public PersistConfiguration() {
    }

    /**
     * Gets the database configuration containing connection details.
     *
     * @return the DatabaseConfiguration object with host, port, user, password, and database name
     */
    public DatabaseConfiguration getDbConfig() {
        return dbConfig;
    }

    /**
     * Sets the database configuration with connection details.
     *
     * @param dbConfig the DatabaseConfiguration object containing host, port, user, password, and database name
     */
    public void setDbConfig(DatabaseConfiguration dbConfig) {
        this.dbConfig = dbConfig;
    }

    /**
     * Gets the data store provider type.
     *
     * @return the provider type (e.g., "mysql", "postgresql", "mssql")
     */
    public String getProvider() {
        return provider;
    }

    /**
     * Sets the data store provider type. Automatically converts "mssql" to "sqlserver" for compatibility.
     *
     * @param provider the provider type (e.g., "mysql", "postgresql", "mssql")
     */
    public void setProvider(String provider) {
        if (provider.equals(PersistToolsConstants.SupportedDataSources.MSSQL_DB)) {
            this.provider = PersistToolsConstants.SupportedDataSources.MSSQL_DB_ALT;
            return;
        }
        this.provider = provider;
    }

    /**
     * Gets the source path of the Ballerina project.
     *
     * @return the absolute path to the Ballerina project directory
     */
    public String getSourcePath() {
        return sourcePath;
    }

    /**
     * Sets the source path of the Ballerina project.
     *
     * @param sourcePath the absolute path to the Ballerina project directory
     */
    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    /**
     * Gets the list of selected table names to introspect.
     * If null or empty, all tables in the database will be introspected.
     *
     * @return the list of table names to include, or null if all tables should be included
     */
    public List<String> getSelectedTables() {
        return selectedTables == null ? null : new java.util.ArrayList<>(selectedTables);
    }

    /**
     * Sets the list of tables to introspect from a comma-separated string.
     * Table names are trimmed of whitespace and empty entries are filtered out.
     *
     * @param tables a comma-separated string of table names (e.g., "users,orders,products")
     */
    public void setSelectedTables(String tables) {
        this.selectedTables = Arrays.stream(tables.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

}
