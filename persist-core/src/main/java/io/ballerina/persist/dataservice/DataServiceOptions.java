/*
 * Copyright (c) 2026, WSO2 LLC. (http://www.wso2.com).
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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.ballerina.persist.dataservice;

import io.ballerina.persist.BalException;

import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * The validated `options.dataservice` group of a `[[tool.persist]]` entry.
 *
 * @param protocol   the protocol the service is generated for
 * @param basePath   the base path of the service, without a trailing slash; empty for the root path
 * @param entities   the entities to expose, or an empty set to expose every entity
 * @param operations the actions every exposed entity allows, unless its annotation narrows them
 */
public record DataServiceOptions(Protocol protocol, String basePath, Set<String> entities,
                                 Set<Operation> operations) {

    public static final String OPTION_DATASERVICE = "dataservice";
    public static final String PROTOCOL = "protocol";
    public static final String BASE_PATH = "basePath";
    public static final String ENTITIES = "entities";
    public static final String OPERATIONS = "operations";

    private static final String PREFIX = "options." + OPTION_DATASERVICE + ".";
    private static final Pattern PATH_SEGMENT = Pattern.compile("[A-Za-z0-9_.\\-]+");
    private static final Set<String> FUTURE_PROTOCOLS = Set.of("graphql", "mcp");

    /**
     * The protocols a data service can be generated for.
     */
    public enum Protocol {
        HTTP("http", "/api");

        private final String value;
        private final String defaultBasePath;

        Protocol(String value, String defaultBasePath) {
            this.value = value;
            this.defaultBasePath = defaultBasePath;
        }

        public String value() {
            return value;
        }

        public String defaultBasePath() {
            return defaultBasePath;
        }
    }

    /**
     * The actions a data service allows on an entity.
     */
    public enum Operation {
        READ, CREATE, UPDATE, DELETE;

        public String value() {
            return name().toLowerCase(Locale.ROOT);
        }

        public static Operation fromValue(String value) {
            for (Operation operation : values()) {
                if (operation.value().equals(value)) {
                    return operation;
                }
            }
            return null;
        }
    }

    /**
     * Reads and validates the `options.dataservice` group.
     *
     * @param value the native value of the group, as read from `Ballerina.toml`
     * @return the validated options
     * @throws BalException if a value is missing or invalid
     */
    public static DataServiceOptions from(Object value) throws BalException {
        if (!(value instanceof Map<?, ?> group)) {
            throw new BalException("'options." + OPTION_DATASERVICE + "' must be a table of data service options.");
        }
        Protocol protocol = readProtocol(group.get(PROTOCOL));
        String basePath = readBasePath(group.get(BASE_PATH), protocol);
        Set<String> entities = group.containsKey(ENTITIES) ?
                readNonEmptyStrings(group.get(ENTITIES), ENTITIES) : Collections.emptySet();
        Set<Operation> operations = group.containsKey(OPERATIONS) ?
                readOperations(group.get(OPERATIONS)) : EnumSet.allOf(Operation.class);
        return new DataServiceOptions(protocol, basePath, Collections.unmodifiableSet(entities),
                Collections.unmodifiableSet(operations));
    }

    private static Protocol readProtocol(Object value) throws BalException {
        String supported = supportedProtocols();
        if (value == null) {
            throw new BalException(String.format("'%s%s' is required. Supported values: %s.", PREFIX, PROTOCOL,
                    supported));
        }
        String protocol = value.toString();
        for (Protocol candidate : Protocol.values()) {
            if (candidate.value().equals(protocol)) {
                return candidate;
            }
        }
        if (FUTURE_PROTOCOLS.contains(protocol)) {
            throw new BalException(String.format("the '%s' data service protocol is not supported yet. " +
                    "Supported values: %s.", protocol, supported));
        }
        throw new BalException(String.format("unsupported data service protocol '%s'. Supported values: %s.",
                protocol, supported));
    }

    private static String readBasePath(Object value, Protocol protocol) throws BalException {
        String basePath = value == null ? protocol.defaultBasePath() : value.toString();
        if (!basePath.startsWith("/")) {
            throw new BalException(String.format("invalid '%s%s' '%s': the base path must start with '/'.",
                    PREFIX, BASE_PATH, basePath));
        }
        String trimmed = basePath.replaceAll("/+$", "");
        if (trimmed.isEmpty()) {
            return "";
        }
        for (String segment : trimmed.substring(1).split("/", -1)) {
            if (!PATH_SEGMENT.matcher(segment).matches()) {
                throw new BalException(String.format("invalid '%s%s' '%s': each path segment must be non-empty " +
                        "and contain only letters, digits, '_', '-' and '.'.", PREFIX, BASE_PATH, basePath));
            }
        }
        return trimmed;
    }

    private static Set<Operation> readOperations(Object value) throws BalException {
        Set<Operation> operations = EnumSet.noneOf(Operation.class);
        for (String name : readNonEmptyStrings(value, OPERATIONS)) {
            Operation operation = Operation.fromValue(name);
            if (operation == null) {
                throw new BalException(String.format("invalid operation '%s' in '%s%s'. Supported values: " +
                        "read, create, update, delete.", name, PREFIX, OPERATIONS));
            }
            operations.add(operation);
        }
        return operations;
    }

    private static Set<String> readNonEmptyStrings(Object value, String key) throws BalException {
        if (!(value instanceof List<?> list)) {
            throw new BalException(String.format("'%s%s' must be an array of strings.", PREFIX, key));
        }
        if (list.isEmpty()) {
            throw new BalException(String.format("'%s%s' cannot be empty.", PREFIX, key));
        }
        Set<String> values = new LinkedHashSet<>();
        for (Object item : list) {
            if (!(item instanceof String text)) {
                throw new BalException(String.format("'%s%s' must be an array of strings.", PREFIX, key));
            }
            values.add(text);
        }
        return values;
    }

    private static String supportedProtocols() {
        StringBuilder supported = new StringBuilder();
        for (Protocol protocol : Protocol.values()) {
            if (!supported.isEmpty()) {
                supported.append(", ");
            }
            supported.append(protocol.value());
        }
        return supported.toString();
    }
}
