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

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Class containing data related to entities and their respective nodes.
 *
 * @since 0.1.0
 */
public class Module {

    private final Map<String, Entity> entityMap;
    private final Map<String, Enum> enumMap;
    private final String moduleName;
    private final Set<String> importModulePrefixes;

    private Module(String moduleName, Set<String> importModulePrefixes,
                   Map<String, Entity> entityMap, Map<String, Enum> enumMap) {
        this.moduleName = moduleName;
        this.importModulePrefixes = Collections.unmodifiableSet(importModulePrefixes);
        this.entityMap = Collections.unmodifiableMap(entityMap);
        this.enumMap = Collections.unmodifiableMap(enumMap);
    }

    public Map<String, Entity> getEntityMap() {
        return entityMap;
    }

    public Map<String, Enum> getEnumMap() {
        return enumMap;
    }

    public Set<String> getImportModulePrefixes() {
        return importModulePrefixes;
    }

    public String getModuleName() {
        return moduleName;
    }

    public Optional<Entity> getEntityByTableName(String tableName) {
        return entityMap.values().stream().filter(item -> item.getTableName()
                .equals(tableName)).findAny();
    }

    public static Module.Builder newBuilder(String moduleName) {
        return new Module.Builder(moduleName);
    }

    /**
     * Module Definition.Builder.
     */
    public static class Builder {

        String moduleName;
        Map<String, Entity> entityMap = new LinkedHashMap<>();
        Map<String, Enum> enumMap = new LinkedHashMap<>();

        private final Set<String> importModulePrefixes = new HashSet<>();

        private Builder(String moduleName) {
            this.moduleName = moduleName;
        }

        public void addEntity(String key, Entity entity) {
            this.entityMap.put(key, entity);
        }

        public void addEnum(String key, Enum enumValue) {
            this.enumMap.put(key, enumValue);
        }

        public Map<String, Enum> getEnumsMap() {
            return enumMap;
        }

        public void addImportModulePrefix(String modulePrefix) {
            this.importModulePrefixes.add(modulePrefix);
        }

        public Module build() {
            return new Module(moduleName, importModulePrefixes, entityMap, enumMap);
        }
    }
}
