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

/**
 * The configuration class for the persist tool.
 * @since 0.1.0
 */
public class PersistConfiguration {

    private String provider;
    private DatabaseConfiguration dbConfig;
    private DatabaseConfiguration shadowDbConfig;

    public PersistConfiguration() {
    }

    public DatabaseConfiguration getDbConfig() {
        return dbConfig;
    }

    public void setDbConfig(DatabaseConfiguration dbConfig) {
        this.dbConfig = dbConfig;
    }

    public DatabaseConfiguration getShadowDbConfig() {
        return shadowDbConfig;
    }

    public void setShadowDbConfig(DatabaseConfiguration shadowDbConfig) {
        this.shadowDbConfig = shadowDbConfig;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}
