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

package io.ballerina.persist.models;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class containing data related to entities and their respective nodes.
 *
 * @since 0.1.0
 */
public class Module {
    private final Map<String, Entity> entityMap;
    private final String moduleName;
    private final String clientName;

    private Set<String> importModulePrefixes;

    private Module(String moduleName, String clientName, Set<String> importModulePrefixes,
                   Map<String, Entity> entityMap) {
        this.moduleName = moduleName;
        this.clientName = clientName;
        this.importModulePrefixes = importModulePrefixes;
        this.entityMap = entityMap;
    }

    public Map<String, Entity> getEntityMap() {
        return entityMap;
    }

    public Set<String> getImportModulePrefixes() {
        return importModulePrefixes;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getClientName() {
        return clientName;
    }

    public static Module.Builder newBuilder(String moduleName) {
        return new Module.Builder(moduleName);
    }

    /**
     * Module Definition.Builder.
     */
    public static class Builder {
        private static final String SUFFIX_CLIENT = "Client";
        String moduleName;
        Map<String, Entity> entityMap = new HashMap<>();

        private Set<String> importModulePrefixes = new HashSet<>();

        private Builder(String moduleName) {
            this.moduleName = moduleName;
        }

        public void addEntity(String key, Entity entity) {
            this.entityMap.put(key, entity);
        }

        public void addImportModulePrefix(String modulePrefix) {
            this.importModulePrefixes.add(modulePrefix);
        }

        public Module build() {
            String clientName = convertTitleCase(moduleName) + SUFFIX_CLIENT;
            return new Module(moduleName, clientName, importModulePrefixes, entityMap);
        }

        private String convertTitleCase(String moduleName) {
            StringBuilder titleBuilder = new StringBuilder();
            String[] moduleParts = moduleName.split(" +");
            Arrays.stream(moduleParts).forEach(modulePart -> {
                titleBuilder.append(modulePart.substring(0, 1).toUpperCase())
                        .append(modulePart.substring(1));
            });
            return titleBuilder.toString();
        }
    }
}
