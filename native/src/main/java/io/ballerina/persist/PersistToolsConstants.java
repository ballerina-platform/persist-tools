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

    public static final String CONFIG_PATH = "Config.toml";

}
