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

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * The validated `options.dataservice` group of a `[[tool.persist]]` entry.
 *
 * @param protocol the protocol the service is generated for
 * @param basePath the base path of the service, without a trailing `/`; `/` for the root
 */
public record DataServiceOptions(String protocol, String basePath) {

    public static final String OPTION_DATASERVICE = "dataservice";
    public static final String PROTOCOL = "protocol";
    public static final String BASE_PATH = "basePath";
    public static final String HTTP = "http";
    public static final String DEFAULT_HTTP_BASE_PATH = "/api";

    private static final Set<String> RESERVED_PROTOCOLS = Set.of("graphql", "mcp", "odata", "soap");
    private static final Pattern SEGMENT = Pattern.compile("[A-Za-z0-9_.\\-]+");

    /**
     * Reads and validates the `options.dataservice` group.
     *
     * @param group the group, as read from `Ballerina.toml`
     * @return the validated options
     * @throws BalException if a value is missing or invalid, with the message to print
     */
    public static DataServiceOptions from(Map<?, ?> group) throws BalException {
        Object protocol = group.get(PROTOCOL);
        if (protocol == null) {
            throw new BalException(DataServiceMessages.PROTOCOL_REQUIRED);
        }
        String validProtocol = validateProtocol(protocol.toString());
        Object basePath = group.get(BASE_PATH);
        return new DataServiceOptions(validProtocol,
                validateBasePath(basePath == null ? DEFAULT_HTTP_BASE_PATH : basePath.toString()));
    }

    /**
     * Validates a protocol value, as given in `Ballerina.toml` or with `--dataservice`.
     *
     * @param protocol the value
     * @return the value, if it is supported
     * @throws BalException if the protocol is reserved or unknown
     */
    public static String validateProtocol(String protocol) throws BalException {
        if (HTTP.equals(protocol)) {
            return protocol;
        }
        if (RESERVED_PROTOCOLS.contains(protocol)) {
            throw new BalException(String.format(DataServiceMessages.PROTOCOL_NOT_SUPPORTED_YET, protocol));
        }
        throw new BalException(String.format(DataServiceMessages.PROTOCOL_INVALID, protocol));
    }

    /**
     * Validates a base path and removes its trailing `/`.
     *
     * @param basePath the value
     * @return the base path without a trailing `/`, or `/` for the root
     * @throws BalException if the base path is invalid
     */
    public static String validateBasePath(String basePath) throws BalException {
        if (!basePath.startsWith("/")) {
            throw new BalException(String.format(DataServiceMessages.BASE_PATH_NO_LEADING_SLASH, basePath));
        }
        String trimmed = basePath.endsWith("/") ? basePath.substring(0, basePath.length() - 1) : basePath;
        if (trimmed.isEmpty()) {
            return "/";
        }
        for (String segment : trimmed.substring(1).split("/", -1)) {
            if (!SEGMENT.matcher(segment).matches()) {
                throw new BalException(String.format(DataServiceMessages.BASE_PATH_INVALID_SEGMENT, basePath));
            }
        }
        return trimmed;
    }

    /**
     * Returns the default options for a protocol, as `bal persist generate --dataservice` uses them.
     *
     * @param protocol a supported protocol
     * @return the options with the protocol's default base path
     */
    public static DataServiceOptions defaults(String protocol) {
        return new DataServiceOptions(protocol, DEFAULT_HTTP_BASE_PATH);
    }
}
