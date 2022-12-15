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

import io.ballerina.persist.BalException;
import io.ballerina.persist.utils.DataBaseValidationUtils;
import io.ballerina.toml.syntax.tree.KeyValueNode;
import io.ballerina.toml.syntax.tree.NodeList;

/**
 * Database configuration class for the persist tool.
 * @since 0.1.0
 */
public class DatabaseConfiguration {
    private String host;
    private String username;
    private String password;
    private int port;
    private String database;

    public DatabaseConfiguration(NodeList<KeyValueNode> nodeList) throws BalException {
        for (KeyValueNode member : nodeList) {
            String value = member.value().toSourceCode().replaceAll("\"", "").trim();
            String key = member.identifier().toSourceCode().trim();
            switch (key) {
                case "host" :
                    this.host = value;
                    break;
                case "user" :
                    this.username = value;
                    break;
                case "password" :
                    this.password = value;
                    break;
                case "port" :
                    this.port = Integer.parseInt(value);
                    break;
                case "database":
                    this.database = DataBaseValidationUtils.validateDatabaseInput(value);
                    break;
                default:
                    throw new BalException("invalid database configuration identifier, " + key);
            }
        }

    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }
}
