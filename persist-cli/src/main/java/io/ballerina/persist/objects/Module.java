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
package io.ballerina.persist.objects;

import java.util.HashMap;
import java.util.Map;

/**
 * Class containing data related to entities and their respective nodes.
 *
 * @since 0.1.0
 */
public class Module {
    private final Map<String, Entity> entityMap;
    private final String moduleName;

    private Module(String moduleName, Map<String, Entity> entityMap) {
        this.moduleName = moduleName;
        this.entityMap = entityMap;
    }

    public Map<String, Entity> getEntityMap() {
        return entityMap;
    }

    public String getModuleName() {
        return moduleName;
    }

    public static Module.Builder newBuilder(String moduleName) {
        return new Module.Builder(moduleName);
    }

    /**
     * Module Definition.Builder.
     */
    public static class Builder {
        String moduleName;
        Map<String, Entity> entityMap = new HashMap<>();

        private Builder(String moduleName) {
            this.moduleName = moduleName;
        }

        public void addEntity(String key, Entity entity) {
            this.entityMap.put(key, entity);
        }

        public Module build() {
            return new Module(moduleName, entityMap);
        }
    }
}
