/*
 * Copyright (c) 2024, WSO2 LLC. (http://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.ballerina.persist.cmd;

/**
 * This builder class for contains the all the option for use CLI.
 */
public class CmdOptions {
    private final String outputModule;
    private final String id;
    private final String datastore;

    private CmdOptions(CmdOptionsBuilder builder) {
        this.outputModule = builder.outputModule;
        this.id = builder.id;
        this.datastore = builder.datastore;
    }

    public String getOutputModule() {
        return outputModule;
    }

    public String getId() {
        return id;
    }

    public String getDatastore() {
        return datastore;
    }

    /**
     * CMD options builder class.
     */
    public static class CmdOptionsBuilder {
        private String outputModule;
        private String id;
        private String datastore;

        public CmdOptionsBuilder withOutputModule(String outputModule) {
            this.outputModule = outputModule;
            return this;
        }

        public CmdOptionsBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public CmdOptionsBuilder withDatastore(String datastore) {
            this.datastore = datastore;
            return this;
        }

        public CmdOptions build() {
            return new CmdOptions(this);
        }
    }
}

