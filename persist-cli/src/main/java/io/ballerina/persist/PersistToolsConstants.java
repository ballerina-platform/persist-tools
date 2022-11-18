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
package io.ballerina.persist;

/**
 * Persist Tool contants class.
 *
 * @since 0.1.0
 */
public class PersistToolsConstants {

    private PersistToolsConstants() {
    }

    public static final String COMPONENT_IDENTIFIER = "persist";

    public static final String DEFAULT_USER = "root";
    public static final String DEFAULT_PORT = "3306";
    public static final String DEFAULT_PASSWORD = "";
    public static final String DEFAULT_DATABASE = "";
    public static final String DEFAULT_HOST = "localhost";

    public static final String KEY_USER = "user";
    public static final String KEY_PORT = "port";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_DATABASE = "database";
    public static final String KEY_HOST = "host";

    public static final String HOST_PLACEHOLDER = "${%s.clients.host}";
    public static final String PORT_PLACEHOLDER = "${%s.clients.port}";
    public static final String PASSWORD_PLACEHOLDER = "${%s.clients.password}";
    public static final String USER_PLACEHOLDER = "${%s.clients.user}";
    public static final String DATABASE_PLACEHOLDER = "${%s.clients.database}";

    public static final String CONFIG_SCRIPT_FILE = "Config.toml";
    public static final String SQL_SCRIPT_FILE = "persist_db_scripts.sql";
    public static final String TARGET_DIR = "target";

    public static final String PASSWORD = "password";
    public static final String USER = "user";
    public static final String MYSQL_DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    public static final String MYSQL = "mysql";
    public static final String DATABASE = "database";
    public static final String HOST = "host";
    public static final String PORT = "port";

    public static final String CREATE_DATABASE_SQL = "CREATE DATABASE %s";
    public static final String KEYWORD_PROVIDER = "provider";
    public static final String SUBMODULE_PERSIST = "persist";
    public static final String SUBMODULE_FOLDER = "modules";
    public static final String KEYWORD_CLIENTS = "clients";
    public static final String PERSIST_TOML_FILE = "Persist.toml";
    public static final String DATABASE_CONFIGURATION_BAL = "database_configuration.bal";

    public static final String BALLERINA_MYSQL_DRIVER_NAME = "ballerinax/mysql.driver";
    public static final String PLATFORM = "java11";
    public static final String PROPERTY_KEY_PATH = "path";
    public static final String MYSQL_CONNECTOR_NAME_PREFIX = "mysql-connector";
}
