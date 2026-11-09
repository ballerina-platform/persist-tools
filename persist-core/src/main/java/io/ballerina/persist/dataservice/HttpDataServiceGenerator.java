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

import io.ballerina.compiler.syntax.tree.SyntaxInfo;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.persist.BalException;
import io.ballerina.persist.inflector.Pluralizer;
import io.ballerina.persist.nodegenerator.syntax.constants.BalSyntaxConstants;
import io.ballerina.persist.utils.FileUtils;
import io.ballerina.tools.diagnostics.Diagnostic;
import io.ballerina.tools.diagnostics.DiagnosticSeverity;
import io.ballerina.tools.text.TextDocuments;
import org.ballerinalang.formatter.core.Formatter;
import org.ballerinalang.formatter.core.FormatterException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static io.ballerina.persist.dataservice.DataServiceUtils.WORD_BOUNDARY;
import static io.ballerina.persist.dataservice.DataServiceUtils.unescape;
import static io.ballerina.persist.dataservice.Operation.CREATE;
import static io.ballerina.persist.dataservice.Operation.DELETE;
import static io.ballerina.persist.dataservice.Operation.READ;
import static io.ballerina.persist.dataservice.Operation.UPDATE;

/**
 * Generates the sources of an HTTP data service, as the HTTP data service proposal defines them.
 */
public final class HttpDataServiceGenerator {

    public static final String SERVICE_FILE = "dataservice.bal";
    public static final String TYPES_FILE = "dataservice_types.bal";
    public static final String CONFIG_FILE = "dataservice_config.bal";

    private static final String NEWLINE = System.lineSeparator();
    private static final String RELATED_OPERATION = "get related";
    private static final Map<Operation, String> HTTP_METHODS = Map.of(READ, "GET", CREATE, "POST", UPDATE, "PATCH",
            DELETE, "DELETE");
    private static final List<String> LIST_ERRORS = List.of("dsHttp:InvalidInput");
    private static final List<String> GET_ERRORS = List.of("dsHttp:NotFound", "dsHttp:InvalidInput");
    private static final List<String> CREATE_ERRORS = List.of("dsHttp:InvalidInput", "dsHttp:Conflict",
            "dsHttp:ConstraintViolation");
    private static final List<String> UPDATE_ERRORS = List.of("dsHttp:NotFound", "dsHttp:InvalidInput",
            "dsHttp:Conflict", "dsHttp:ConstraintViolation");
    private static final List<String> DELETE_ERRORS = List.of("dsHttp:NotFound", "dsHttp:InvalidInput",
            "dsHttp:ConstraintViolation");
    private static final String NO_CONTENT = "http:NoContent";
    private static final Pattern IDENTIFIER = Pattern.compile("[A-Za-z_][A-Za-z0-9_]*");
    private static final String DATASERVICE_IMPORT = "import ballerina/dataservice;";
    private static final String DATASERVICE_HTTP_IMPORT = "import ballerina/dataservice.http as dsHttp;";
    private static final String HTTP_IMPORT = "import ballerina/http;";

    private final List<ExposedEntity> entities;
    private final Map<String, ExposedEntity> entitiesByName = new HashMap<>();
    private final String basePath;
    private final boolean introspection;
    private final boolean auth;
    private final String packageName;
    private final String modelName;
    private final String datastore;
    private final boolean userOwned;

    /**
     * Creates a generator for a data service.
     *
     * @param entities    the exposed entities, in model order
     * @param options     the data service options
     * @param packageName the name of the package, the title of the OpenAPI definition
     * @param modelName   the name of the persist model, as persist writes it in its file headers
     * @param datastore   the datastore of the persist client, such as `mysql`
     * @param userOwned   whether the files are written for the user to own, by `bal persist generate`
     */
    public HttpDataServiceGenerator(List<ExposedEntity> entities, DataServiceOptions options, String packageName,
                                    String modelName, String datastore, boolean userOwned) {
        this.entities = entities;
        entities.forEach(entity -> entitiesByName.put(entity.name(), entity));
        this.packageName = packageName;
        this.basePath = options.basePath();
        this.introspection = options.introspection();
        this.auth = options.auth();
        this.modelName = modelName;
        this.datastore = datastore;
        this.userOwned = userOwned;
    }

    /**
     * Writes `dataservice.bal`, `dataservice_types.bal` and `dataservice_config.bal` to a directory, and deletes a
     * `dataservice_types.bal` left from an earlier run when no exposed entity needs a type.
     *
     * @param dir the directory of the generated sources
     * @throws BalException if a source cannot be formatted or its directory cannot be created
     * @throws IOException  if a file cannot be written or deleted
     */
    public void write(Path dir) throws BalException, IOException {
        FileUtils.writeToTargetFile(generateService(), dir.resolve(SERVICE_FILE).toAbsolutePath().toString());
        Path typesFile = dir.resolve(TYPES_FILE);
        Optional<String> types = generateTypes();
        if (types.isPresent()) {
            FileUtils.writeToTargetFile(types.get(), typesFile.toAbsolutePath().toString());
        } else {
            Files.deleteIfExists(typesFile);
        }
        FileUtils.writeToTargetFile(generateConfig(), dir.resolve(CONFIG_FILE).toAbsolutePath().toString());
    }

    /**
     * Generates `dataservice.bal`: the listener and the service, which holds the persist client.
     *
     * @return the formatted source
     * @throws BalException if the source cannot be formatted
     */
    public String generateService() throws BalException {
        Set<String> imports = new TreeSet<>(List.of(DATASERVICE_IMPORT, DATASERVICE_HTTP_IMPORT, HTTP_IMPORT,
                "import ballerina/persist;"));
        StringBuilder body = new StringBuilder();
        body.append("# The listener of the data service.").append(NEWLINE);
        body.append("listener http:Listener dataserviceListener =").append(NEWLINE)
                .append("        new (dataserviceConfig.port, dataserviceConfig.listenerConfig);").append(NEWLINE)
                .append(NEWLINE);
        if (auth) {
            appendAuthenticator(body);
        }
        if (introspection) {
            imports.add("import ballerina/openapi;");
            body.append("@openapi:ServiceInfo {embed: true, title: \"").append(packageName).append("\"}")
                    .append(NEWLINE);
        }
        body.append("@http:ServiceConfig {cors: dataserviceConfig.serviceConfig.cors}").append(NEWLINE);
        body.append("service http:InterceptableService ").append(servicePath()).append(" on dataserviceListener {")
                .append(NEWLINE);
        body.append(NEWLINE);
        appendServiceInit(body);
        if (auth) {
            body.append("    # Checks auth before each request is dispatched, and answers the errors that ")
                    .append("`ballerina/http`").append(NEWLINE)
                    .append("    # raises with the `DataserviceError` body.").append(NEWLINE);
            body.append("    #").append(NEWLINE);
            body.append("    # + return - The interceptors").append(NEWLINE);
            body.append("    public function createInterceptors()").append(NEWLINE)
                    .append("            returns [DataserviceAuthInterceptor, DataserviceErrorInterceptor] => ")
                    .append("[new (self.authenticator), new];")
                    .append(NEWLINE);
        } else {
            body.append("    # Answers the errors that `ballerina/http` raises with the `DataserviceError` body.")
                    .append(NEWLINE);
            body.append("    #").append(NEWLINE);
            body.append("    # + return - The error interceptor").append(NEWLINE);
            body.append("    public function createInterceptors() returns DataserviceErrorInterceptor => new;")
                    .append(NEWLINE);
        }
        for (ExposedEntity entity : entities) {
            appendResources(body, entity, imports);
        }
        body.append("}").append(NEWLINE);
        appendInterceptors(body);
        return format(modelHeader() + String.join(NEWLINE, imports) + NEWLINE + NEWLINE + body);
    }

    /**
     * Generates `dataservice_types.bal`: per entity in model order, the wire types, the list envelope, the filter and
     * partial types, the entity description, the status code records and the functions that convert between the
     * wire types and the persist types.
     *
     * @return the formatted source, or empty when no exposed entity needs a type
     * @throws BalException if the source cannot be formatted
     */
    public Optional<String> generateTypes() throws BalException {
        Set<String> imports = new TreeSet<>();
        StringBuilder body = new StringBuilder();
        for (ExposedEntity entity : entities) {
            appendTypes(body, entity, imports);
            appendConverters(body, entity, imports);
        }
        if (body.isEmpty()) {
            return Optional.empty();
        }
        String importLines = imports.isEmpty() ? "" : String.join(NEWLINE, imports) + NEWLINE + NEWLINE;
        return Optional.of(format(modelHeader() + importLines + body.toString().stripLeading()));
    }

    private String modelHeader() {
        return header(String.format(BalSyntaxConstants.AUTO_GENERATED_COMMENT_WITH_REASON, modelName));
    }

    /**
     * Generates `dataservice_config.bal`.
     *
     * @return the formatted source
     * @throws BalException if the source cannot be formatted
     */
    public String generateConfig() throws BalException {
        return format(header(BalSyntaxConstants.AUTO_GENERATED_COMMENT) +
                DATASERVICE_HTTP_IMPORT + NEWLINE +
                NEWLINE +
                "# The runtime settings of the data service, set in the `[dataserviceConfig]` table of Config.toml." +
                NEWLINE +
                (auth ? "configurable dsHttp:AuthConfig dataserviceConfig = ?;" :
                        "configurable dsHttp:Config dataserviceConfig = {};") + NEWLINE);
    }

    // The service holds the client and the settings checked at startup, so a failure stops the service from starting.
    private void appendServiceInit(StringBuilder out) {
        out.append("    private final Client persistClient;").append(NEWLINE);
        out.append("    private final dataservice:PageConfig pageConfig;").append(NEWLINE);
        if (auth) {
            out.append("    private final dsHttp:Authenticator authenticator;").append(NEWLINE);
        }
        out.append(NEWLINE);
        out.append("    # Creates the persist client, and checks the page sizes")
                .append(auth ? " and the auth providers." : ".").append(NEWLINE);
        out.append("    #").append(NEWLINE);
        out.append("    # + return - An error when the client cannot be created or a setting is invalid")
                .append(NEWLINE);
        out.append("    function init() returns error? {").append(NEWLINE);
        out.append("        self.persistClient = check new ();").append(NEWLINE);
        out.append("        self.pageConfig = check dataservice:toPageConfig(dataserviceConfig.pageSize, ")
                .append("dataserviceConfig.maxPageSize);").append(NEWLINE);
        if (auth) {
            out.append("        self.authenticator = check new (dataserviceConfig.serviceConfig.auth, ")
                    .append("dataserviceScopes,").append(NEWLINE)
                    .append("                dataserviceConfig.serviceConfig.cors);").append(NEWLINE);
        }
        out.append("    }").append(NEWLINE).append(NEWLINE);
    }

    // The interceptors are defined in the service's own package, because the OpenAPI definition leaves out an
    // interceptor class from another package (ballerina-library#9633).
    private void appendInterceptors(StringBuilder out) {
        if (auth) {
            out.append(NEWLINE)
                    .append("# Checks the credential and the scopes of each request before it is dispatched.")
                    .append(NEWLINE);
            out.append("isolated service class DataserviceAuthInterceptor {").append(NEWLINE);
            out.append("    *http:RequestInterceptor;").append(NEWLINE).append(NEWLINE);
            out.append("    private final dsHttp:Authenticator authenticator;").append(NEWLINE).append(NEWLINE);
            out.append("    # Creates the interceptor.").append(NEWLINE);
            out.append("    #").append(NEWLINE);
            out.append("    # + authenticator - The authenticator of the service").append(NEWLINE);
            out.append("    isolated function init(dsHttp:Authenticator authenticator) {").append(NEWLINE);
            out.append("        self.authenticator = authenticator;").append(NEWLINE);
            out.append("    }").append(NEWLINE).append(NEWLINE);
            out.append("    isolated resource function 'default [string... path](http:RequestContext ctx, "
                    + "http:Request request)").append(NEWLINE);
            out.append("            returns http:NextService|dsHttp:Unauthorized|dsHttp:Forbidden|error? =>")
                    .append(NEWLINE);
            out.append("        self.authenticator.authorize(ctx, request, path);").append(NEWLINE);
            out.append("}").append(NEWLINE);
        }
        out.append(NEWLINE)
                .append("# Answers the errors that `ballerina/http` raises with the `DataserviceError` body.")
                .append(NEWLINE);
        out.append("isolated service class DataserviceErrorInterceptor {").append(NEWLINE);
        out.append("    *http:ResponseErrorInterceptor;").append(NEWLINE).append(NEWLINE);
        out.append("    isolated remote function interceptResponseError(error err, http:RequestContext ctx, "
                + "http:Response response)").append(NEWLINE);
        out.append("            returns http:NextService? {").append(NEWLINE);
        out.append("        dsHttp:setErrorBody(response, err);").append(NEWLINE);
        out.append("        // The response is changed in place; an error return would add a 500 response to the "
                + "OpenAPI definition.").append(NEWLINE);
        out.append("        http:NextService|error? next = ctx.next();").append(NEWLINE);
        out.append("        return next is http:NextService ? next : ();").append(NEWLINE);
        out.append("    }").append(NEWLINE);
        out.append("}").append(NEWLINE);
    }

    private void appendAuthenticator(StringBuilder out) {
        out.append("# The scopes of each operation, by collection and then by HTTP method.").append(NEWLINE);
        out.append("final readonly & map<map<string[]>> dataserviceScopes = {").append(NEWLINE);
        List<String> collections = new ArrayList<>();
        for (ExposedEntity entity : entities) {
            List<String> methods = new ArrayList<>();
            for (Operation action : List.of(READ, CREATE, UPDATE, DELETE)) {
                List<String> scopes = entity.scopes(action);
                if (entity.allows(action) && !scopes.isEmpty()) {
                    methods.add("\"" + HTTP_METHODS.get(action) + "\": " + stringList(scopes));
                }
            }
            if (!methods.isEmpty()) {
                collections.add("    \"" + unescape(new Names(entity).collection) + "\": {" + String.join(", ", methods)
                        + "}");
            }
        }
        out.append(String.join("," + NEWLINE, collections)).append(NEWLINE).append("};").append(NEWLINE)
                .append(NEWLINE);
    }

    private String header(String reason) {
        if (userOwned) {
            return "// AUTO-GENERATED FILE." + NEWLINE + NEWLINE + reason + NEWLINE + NEWLINE;
        }
        return BalSyntaxConstants.AUTOGENERATED_FILE_COMMENT + NEWLINE + NEWLINE + reason + NEWLINE +
                BalSyntaxConstants.COMMENT_SHOULD_NOT_BE_MODIFIED + NEWLINE + NEWLINE;
    }

    private String servicePath() {
        if ("/".equals(basePath)) {
            return "/";
        }
        String[] segments = basePath.substring(1).split("/");
        for (String segment : segments) {
            if (!IDENTIFIER.matcher(segment).matches() || SyntaxInfo.isKeyword(segment)) {
                return "\"" + basePath + "\"";
            }
        }
        return basePath;
    }

    private void appendResources(StringBuilder out, ExposedEntity entity, Set<String> imports) {
        Names n = new Names(entity);
        String keyParams = entity.keys().stream().map(key -> "[" + key.type() + " " + key.name() + "]")
                .collect(Collectors.joining("/"));
        String keyPath = keyPath(entity);
        String keyList = keyList(entity, "");
        boolean read = entity.allows(READ);
        String changed = read ? n.wire : NO_CONTENT;
        if (read) {
            appendGetMany(out, entity, n, imports);

            List<String> params = new ArrayList<>(keyDocs(entity, n));
            params.add("fields - The fields to return; the key fields are always returned");
            appendDoc(out, entity, "Gets " + n.article + " " + n.words + " by its key.", params,
                    "The " + n.words);
            out.append("    resource function get ").append(n.collection).append("/").append(keyParams)
                    .append("(").append(requestParam(entity)).append("string[]? fields = ())").append(NEWLINE)
                    .append("            returns ").append(n.wire)
                    .append("|").append(n.partial)
                    .append(errors(GET_ERRORS, !checked(entity).isEmpty())).append(" {").append(NEWLINE);
            appendKeyCheck(out, entity, n, keyList, imports);
            appendForbiddenCheck(out, entity, n);
            imports.add(DATASERVICE_IMPORT);
            appendFieldsCheck(out, n);
            out.append("        ").append(n.entity).append("|persist:Error item = self.persistClient->/")
                    .append(n.resource).append(keyPath).append(";").append(NEWLINE);
            appendErrorCheck(out, "item", "toGetError", n, keyList, "        ");
            String getFailure = "dsHttp:toGetError(err, \"" + n.displayName + "\", " + keyList + ")";
            out.append("        if fields is string[] {").append(NEWLINE);
            String source = n.toWire("item");
            if (n.hasWire) {
                out.append("            ").append(n.wire).append(" itemPayload = ").append(source).append(";")
                        .append(NEWLINE);
                source = "itemPayload";
            }
            out.append("            map<anydata> partial = dataservice:selectFields(").append(source).append(", ")
                    .append(n.description).append(", fields);").append(NEWLINE);
            appendIncludes(out, entity, n, source, getFailure);
            out.append("            ").append(n.partial).append("|error converted = partial.cloneWithType();")
                    .append(NEWLINE);
            out.append("            if converted is error {").append(NEWLINE);
            out.append("                return dsHttp:toGetError(converted, \"").append(n.displayName).append("\", ")
                    .append(keyList).append(");").append(NEWLINE);
            out.append("            }").append(NEWLINE);
            out.append("            return converted;").append(NEWLINE);
            out.append("        }").append(NEWLINE);
            out.append("        return ").append(n.toWire("item")).append(";").append(NEWLINE);
            out.append("    }").append(NEWLINE);
            for (ExposedRelation relation : entity.relations()) {
                appendGetRelated(out, entity, n, relation, keyParams, keyPath, keyList, imports);
            }
        }
        if (entity.allows(CREATE)) {
            appendCreate(out, entity, n, imports);
        }
        if (entity.allows(UPDATE)) {
            boolean checkRead = readChecked(entity);
            List<String> params = new ArrayList<>(keyDocs(entity, n));
            params.add("payload - The fields of the " + n.words + " to update");
            appendDoc(out, entity, "Updates " + n.article + " " + n.words + " by its key.", params,
                    read ? "The updated " + n.words + (checkRead ? ", or no content when the caller may not read it or "
                            + "it cannot be read back" : ", or no content when it cannot be read back") : "No content");
            out.append("    resource function patch ").append(n.collection).append("/").append(keyParams).append("(")
                    .append(checkRead ? "http:RequestContext ctx, " : "").append(n.updateWire).append(" payload)")
                    .append(NEWLINE);
            // An update that is saved but cannot be read back is answered with no content.
            out.append("            returns ").append(read ? changed + "|http:NoContent" : changed)
                    .append(errors(UPDATE_ERRORS, false)).append(" {").append(NEWLINE);
            appendKeyCheck(out, entity, n, keyList, imports);
            String value = "payload";
            if (entity.hasWireTypes()) {
                value = "value";
                out.append("        ").append(n.entity).append("Update|dsHttp:InvalidInput value = transformPayloadTo")
                        .append(n.entity).append("Update(payload);").append(NEWLINE);
                out.append("        if value is dsHttp:InvalidInput {").append(NEWLINE);
                out.append("            return value;").append(NEWLINE);
                out.append("        }").append(NEWLINE);
                appendConstraintCheck(out, entity, n, entity.updateFields(), "        ", imports);
            }
            if (read) {
                if (checkRead) {
                    appendHoldsRead(out, entity);
                }
                out.append("        ").append(n.entity).append("|persist:Error item;").append(NEWLINE);
                out.append("        if ").append(value).append(".length() == 0 {").append(NEWLINE);
                if (checkRead) {
                    out.append("            if !readable {").append(NEWLINE);
                    out.append("                return http:NO_CONTENT;").append(NEWLINE);
                    out.append("            }").append(NEWLINE);
                }
                out.append("            item = self.persistClient->/").append(n.resource).append(keyPath).append(";")
                        .append(NEWLINE);
                out.append("        } else {").append(NEWLINE);
                out.append("            item = self.persistClient->/").append(n.resource).append(keyPath)
                        .append(".put(").append(value).append(");").append(NEWLINE);
                out.append("        }").append(NEWLINE);
                appendErrorCheck(out, "item", "toUpdateError", n, keyList, "        ");
                out.append("        return ").append(checkRead ? "readable ? " + n.toWire("item") + " : http:NO_CONTENT"
                        : n.toWire("item")).append(";").append(NEWLINE);
            } else {
                out.append("        if ").append(value).append(".length() == 0 {").append(NEWLINE);
                out.append("            return http:NO_CONTENT;").append(NEWLINE);
                out.append("        }").append(NEWLINE);
                out.append("        ").append(n.entity).append("|persist:Error item = self.persistClient->/")
                        .append(n.resource).append(keyPath).append(".put(").append(value).append(");")
                        .append(NEWLINE);
                appendErrorCheck(out, "item", "toUpdateError", n, keyList, "        ");
                out.append("        return http:NO_CONTENT;").append(NEWLINE);
            }
            out.append("    }").append(NEWLINE);
        }
        if (entity.allows(DELETE)) {
            boolean checkRead = readChecked(entity);
            appendDoc(out, entity, "Deletes " + n.article + " " + n.words + " by its key.", keyDocs(entity, n),
                    read ? "The deleted " + n.words + (checkRead ? ", or no content when the caller may not read it" :
                            "") : "No content");
            out.append("    resource function delete ").append(n.collection).append("/").append(keyParams)
                    .append("(").append(checkRead ? "http:RequestContext ctx" : "").append(")").append(NEWLINE)
                    .append("            returns ")
                    .append(checkRead ? changed + "|http:NoContent" : changed)
                    .append(errors(DELETE_ERRORS, false)).append(" {")
                    .append(NEWLINE);
            appendKeyCheck(out, entity, n, keyList, imports);
            if (checkRead) {
                appendHoldsRead(out, entity);
            }
            out.append("        ").append(n.entity).append("|persist:Error item = self.persistClient->/")
                    .append(n.resource).append(keyPath).append(".delete();").append(NEWLINE);
            appendErrorCheck(out, "item", "toDeleteError", n, keyList, "        ");
            String result = !read ? "http:NO_CONTENT" :
                    checkRead ? "readable ? " + n.toWire("item") + " : http:NO_CONTENT" : n.toWire("item");
            out.append("        return ").append(result).append(";").append(NEWLINE);
            out.append("    }").append(NEWLINE);
        }
    }

    private void appendGetMany(StringBuilder out, ExposedEntity entity, Names n, Set<String> imports) {
        imports.add(DATASERVICE_IMPORT);
        String entityName = "\"" + n.displayName + "\"";
        appendDoc(out, entity, "Gets a page of " + n.plural + ".", List.of(
                "filter - The conditions that the " + n.plural + " match",
                "sort - The fields to sort by, each descending when it starts with `-`",
                "size - The number of " + n.plural + " in the page",
                "offset - The number of " + n.plural + " to skip",
                "fields - The fields to return; the key fields are always returned"), "A page of " + n.plural);
        out.append("    resource function get ").append(n.collection).append("(").append(requestParam(entity))
                .append(n.filter).append("? filter = (),").append(NEWLINE);
        out.append("            string[]? sort = (), int? size = (), int? offset = (), string[]? fields = ())")
                .append(NEWLINE);
        out.append("            returns ").append(n.list).append("|").append(n.partialList)
                .append(errors(LIST_ERRORS, !checked(entity).isEmpty())).append(" {").append(NEWLINE);
        out.append("        dataservice:Query|dataservice:DataserviceError|dataservice:ConversionError query =")
                .append(NEWLINE);
        out.append("            dataservice:toQuery(").append(n.description)
                .append(", self.pageConfig, filter, sort, size, offset);").append(NEWLINE);
        out.append("        if query !is dataservice:Query {").append(NEWLINE);
        out.append("            return dsHttp:toQueryError(query, ").append(entityName).append(");").append(NEWLINE);
        out.append("        }").append(NEWLINE);
        appendForbiddenCheck(out, entity, n);
        appendFieldsCheck(out, n);
        out.append("        stream<").append(n.entity).append(", persist:Error?> rows = self.persistClient->/")
                .append(n.resource).append("(").append(n.entity).append(",").append(NEWLINE);
        out.append("            whereClause = query.whereClause, orderByClause = query.orderByClause,").append(NEWLINE);
        out.append("            limitClause = query.limitClause, offsetClause = query.offsetClause);").append(NEWLINE);
        out.append("        ").append(n.wire).append("[]|persist:Error items = from ").append(n.entity)
                .append(" item in rows select ").append(n.toWire("item")).append(";").append(NEWLINE);
        out.append("        if items is persist:Error {").append(NEWLINE);
        out.append("            return dsHttp:toListError(items, \"").append(n.displayName).append("\");")
                .append(NEWLINE);
        out.append("        }").append(NEWLINE);
        out.append("        boolean hasMore = dataservice:trimToPage(items, query);").append(NEWLINE);
        out.append("        if fields is string[] {").append(NEWLINE);
        out.append("            ").append(n.wire).append("[] sources = items;").append(NEWLINE);
        out.append("            map<anydata>[] partials = from ").append(n.wire).append(" item in sources select ")
                .append("dataservice:selectFields(item, ").append(n.description).append(", fields);").append(NEWLINE);
        appendIncludes(out, entity, n, null, "dsHttp:toListError(err, \"" + n.displayName + "\")");
        out.append("            ").append(n.partial).append("[]|error converted = partials.cloneWithType();")
                .append(NEWLINE);
        out.append("            if converted is error {").append(NEWLINE);
        out.append("                return dsHttp:toListError(converted, \"").append(n.displayName).append("\");")
                .append(NEWLINE);
        out.append("            }").append(NEWLINE);
        out.append("            return <").append(n.partialList).append(">{items: converted, hasMore};")
                .append(NEWLINE);
        out.append("        }").append(NEWLINE);
        out.append("        return <").append(n.list).append(">{items, hasMore};").append(NEWLINE);
        out.append("    }").append(NEWLINE);
    }

    // Adds the relations named in `fields` to the narrowed records: to `partial`, read from `source`, for one record,
    // or else to `partials`, read from the records in `sources`.
    private void appendIncludes(StringBuilder out, ExposedEntity entity, Names n, String source, String failure) {
        if (entity.relations().isEmpty()) {
            return;
        }
        boolean single = source != null;
        out.append("            do {").append(NEWLINE);
        for (ExposedRelation relation : entity.relations()) {
            Names t = new Names(target(relation));
            String name = "\"" + unescape(relation.field()) + "\"";
            boolean loop = relation.many() && !single;
            String indent = loop ? "                        " : "                    ";
            out.append("                if dataservice:isIncluded(fields, ").append(name).append(") {").append(NEWLINE);
            if (loop) {
                out.append("                    foreach int i in 0 ..< sources.length() {").append(NEWLINE);
            }
            String linked = single ? "[" + source + "]" : loop ? "[sources[i]]" : "sources";
            out.append(indent).append("dataservice:Query include = dataservice:toIncludeQuery(").append(NEWLINE)
                    .append(indent).append("        ").append(n.description).append(", ").append(name).append(", ")
                    .append(linked)
                    .append(", self.pageConfig);").append(NEWLINE);
            out.append(indent).append("stream<").append(t.entity)
                    .append(", persist:Error?> includedRows = self.persistClient->/").append(t.resource).append("(")
                    .append(t.entity).append(",").append(NEWLINE);
            out.append(indent).append("    whereClause = include.whereClause, orderByClause = include.orderByClause")
                    .append(relation.many() ? "," + NEWLINE + indent + "    limitClause = include.limitClause" : "")
                    .append(");").append(NEWLINE);
            out.append(indent).append(t.wire).append("[] included = check from ").append(t.entity)
                    .append(" row in includedRows select ").append(t.toWire("row")).append(";").append(NEWLINE);
            if (relation.many()) {
                out.append(indent).append("boolean includedMore = dataservice:trimToPage(included, include);")
                        .append(NEWLINE);
                out.append(indent).append("dataservice:attachMany(").append(NEWLINE).append(indent)
                        .append("        ").append(single ? "partial" : "partials[i]").append(", ")
                        .append(n.description).append(", ").append(name).append(", included, includedMore, fields);")
                        .append(NEWLINE);
                if (loop) {
                    out.append("                    }").append(NEWLINE);
                }
            } else {
                out.append(indent).append("dataservice:attachOne(").append(NEWLINE).append(indent).append("        ")
                        .append(single ? "[partial], " + linked : "partials, sources").append(", ")
                        .append(n.description).append(", ").append(name).append(", included, fields);").append(NEWLINE);
            }
            out.append("                }").append(NEWLINE);
        }
        out.append("            } on fail error err {").append(NEWLINE);
        out.append("                return ").append(failure).append(";").append(NEWLINE);
        out.append("            }").append(NEWLINE);
    }

    private void appendGetRelated(StringBuilder out, ExposedEntity entity, Names n, ExposedRelation relation,
                                  String keyParams, String keyPath, String keyList, Set<String> imports) {
        imports.add(DATASERVICE_IMPORT);
        ExposedEntity targetEntity = target(relation);
        Names t = new Names(targetEntity);
        String name = unescape(relation.field());
        boolean checked = checked(entity).contains(relation);
        String words = String.join(" ", WORD_BOUNDARY.split(name)).toLowerCase(Locale.ENGLISH);
        String targetName = "\"" + t.displayName + "\"";
        List<String> params = new ArrayList<>(keyDocs(entity, n));
        if (relation.many()) {
            params.addAll(List.of("filter - The conditions that the " + words + " match",
                    "sort - The fields to sort by, each descending when it starts with `-`",
                    "size - The number of " + words + " in the page",
                    "offset - The number of " + words + " to skip"));
        }
        params.add("fields - The fields of the " + words + " to return; the key fields are always returned");
        appendDoc(out, entity, "Gets the " + words + " of " + n.article + " " + n.words + ".", params,
                relation.many() ? "A page of the " + words : "The " + words);
        out.append("    resource function get ").append(n.collection).append("/").append(keyParams).append("/")
                .append(relation.field()).append("(").append(checked ? "http:RequestContext ctx, " : "");
        if (relation.many()) {
            out.append(t.filter).append("? filter = (),").append(NEWLINE)
                    .append("            string[]? sort = (), int? size = (), int? offset = (), ");
        }
        out.append("string[]? fields = ())").append(NEWLINE).append("            returns ")
                .append(relation.many() ? t.list + "|" + t.relatedPartialList : t.wire + "|" + t.relatedPartial)
                .append(errors(GET_ERRORS, checked)).append(" {").append(NEWLINE);
        if (checked) {
            appendAuthorize(out, n, relation, "        ");
        }
        appendKeyCheck(out, entity, n, keyList, imports);
        out.append("        dataservice:DataserviceError? invalidFields =").append(NEWLINE)
                .append("            dataservice:checkFields(")
                .append(t.description).append(", fields, false);").append(NEWLINE);
        out.append("        if invalidFields is dataservice:DataserviceError {").append(NEWLINE);
        out.append("            return dsHttp:toQueryError(invalidFields, ").append(targetName).append(");")
                .append(NEWLINE);
        out.append("        }").append(NEWLINE);
        String row;
        if (relation.owner()) {
            row = "item";
            out.append("        ").append(n.entity).append("|persist:Error item = self.persistClient->/")
                    .append(n.resource).append(keyPath).append(";").append(NEWLINE);
            appendErrorCheck(out, "item", "toGetError", n, keyList, "        ", RELATED_OPERATION);
        } else {
            row = "itemKey";
            out.append("        map<anydata> itemKey = {").append(entity.keys().stream()
                    .map(key -> "\"" + unescape(key.name()) + "\": " + key.name()).collect(Collectors.joining(", ")))
                    .append("};").append(NEWLINE);
        }
        Optional<String> relatedKeyPath = relation.owner() && !relation.many() ?
                relatedKeyPath(entity, targetEntity, relation) : Optional.empty();
        if (relatedKeyPath.isPresent()) {
            appendReadRelatedByKey(out, n, t, name, relatedKeyPath.get(), keyList);
            return;
        }
        out.append("        dataservice:Query|dataservice:DataserviceError|dataservice:ConversionError query =")
                .append(NEWLINE);
        out.append("            dataservice:toRelatedQuery(").append(NEWLINE)
                .append("                ").append(n.description).append(", \"").append(name)
                .append("\", ").append(row).append(", self.pageConfig");
        if (relation.many()) {
            out.append(", filter, sort, size, offset");
        }
        out.append(");").append(NEWLINE);
        out.append("        if query !is dataservice:Query {").append(NEWLINE);
        out.append("            return dsHttp:toQueryError(query, ").append(targetName).append(");").append(NEWLINE);
        out.append("        }").append(NEWLINE);
        out.append("        stream<").append(t.entity).append(", persist:Error?> rows = self.persistClient->/")
                .append(t.resource).append("(").append(t.entity).append(",").append(NEWLINE);
        out.append("            whereClause = query.whereClause, orderByClause = query.orderByClause,").append(NEWLINE);
        out.append("            limitClause = query.limitClause, offsetClause = query.offsetClause);").append(NEWLINE);
        out.append("        ").append(t.wire).append("[]|persist:Error items = from ").append(t.entity)
                .append(" related in rows select ").append(t.toWire("related")).append(";").append(NEWLINE);
        out.append("        if items is persist:Error {").append(NEWLINE);
        out.append("            return dsHttp:toListError(items, \"").append(n.displayName).append("\", \"")
                .append(RELATED_OPERATION).append("\");").append(NEWLINE);
        out.append("        }").append(NEWLINE);
        if (relation.many()) {
            out.append("        boolean hasMore = dataservice:trimToPage(items, query);").append(NEWLINE);
        }
        out.append("        if items.length() == 0 {").append(NEWLINE);
        if (!relation.owner()) {
            // The record itself may not exist, which an empty result does not tell.
            out.append("            ").append(n.entity).append("|persist:Error item = self.persistClient->/")
                    .append(n.resource).append(keyPath).append(";").append(NEWLINE);
            appendErrorCheck(out, "item", "toGetError", n, keyList, "            ", RELATED_OPERATION);
        }
        if (relation.many()) {
            out.append("            return <").append(t.list).append(">{items, hasMore};").append(NEWLINE);
        } else {
            out.append("            return <dsHttp:NotFound>{body: dataservice:toRelatedNotFoundError(")
                    .append(n.description).append(", \"").append(name).append("\", ").append(keyList)
                    .append(")};").append(NEWLINE);
        }
        out.append("        }").append(NEWLINE);
        out.append("        if fields is string[] {").append(NEWLINE);
        if (relation.many()) {
            out.append("            ").append(t.relatedPartial).append("[]|error partials = (from ").append(t.wire)
                    .append(" related in items").append(NEWLINE);
            out.append("                select dataservice:selectFields(related, ").append(t.description)
                    .append(", fields)).cloneWithType();").append(NEWLINE);
            out.append("            if partials is error {").append(NEWLINE);
            out.append("                return dsHttp:toListError(partials, \"").append(n.displayName).append("\", \"")
                    .append(RELATED_OPERATION).append("\");").append(NEWLINE);
            out.append("            }").append(NEWLINE);
            out.append("            return <").append(t.relatedPartialList).append(">{items: partials, hasMore};")
                    .append(NEWLINE);
        } else {
            out.append("            ").append(t.relatedPartial)
                    .append("|error partial =").append(NEWLINE)
                    .append("                dataservice:selectFields(items[0], ")
                    .append(t.description).append(", fields).cloneWithType();").append(NEWLINE);
            out.append("            if partial is error {").append(NEWLINE);
            out.append("                return dsHttp:toListError(partial, \"").append(n.displayName).append("\", \"")
                    .append(RELATED_OPERATION).append("\");").append(NEWLINE);
            out.append("            }").append(NEWLINE);
            out.append("            return partial;").append(NEWLINE);
        }
        out.append("        }").append(NEWLINE);
        out.append("        return ").append(relation.many() ? "<" + t.list + ">{items, hasMore}" : "items[0]")
                .append(";").append(NEWLINE);
        out.append("    }").append(NEWLINE);
    }

    // The key path of the record that a to-one relation owned by the entity reaches, from the foreign key fields of
    // `item`; empty when a foreign key field is nilable, as a nil key reaches no record.
    private static Optional<String> relatedKeyPath(ExposedEntity entity, ExposedEntity target,
                                                   ExposedRelation relation) {
        List<String> targetFields = relation.targetFields().stream().map(DataServiceUtils::unescape).toList();
        List<ServiceField> targetKeys = target.keys();
        if (targetKeys.size() != targetFields.size()) {
            return Optional.empty();
        }
        StringBuilder path = new StringBuilder();
        for (ServiceField key : targetKeys) {
            int index = targetFields.indexOf(unescape(key.name()));
            if (index < 0) {
                return Optional.empty();
            }
            String linked = unescape(relation.fields().get(index));
            Optional<ServiceField> field = entity.fields().stream()
                    .filter(candidate -> unescape(candidate.name()).equals(linked)).findFirst();
            if (field.isEmpty() || field.get().nilable()) {
                return Optional.empty();
            }
            path.append("/[item.").append(field.get().name()).append("]");
        }
        return Optional.of(path.toString());
    }

    private static void appendReadRelatedByKey(StringBuilder out, Names n, Names t, String relation, String keyPath,
                                               String keyList) {
        String failure = "dsHttp:toListError(related, \"" + n.displayName + "\", \"" + RELATED_OPERATION + "\")";
        out.append("        ").append(t.entity).append("|persist:Error related = self.persistClient->/")
                .append(t.resource).append(keyPath).append(";").append(NEWLINE);
        out.append("        if related is persist:NotFoundError {").append(NEWLINE);
        out.append("            return <dsHttp:NotFound>{body: dataservice:toRelatedNotFoundError(")
                .append(n.description).append(", \"").append(relation).append("\", ").append(keyList)
                .append(")};").append(NEWLINE);
        out.append("        }").append(NEWLINE);
        out.append("        if related is persist:Error {").append(NEWLINE);
        out.append("            return ").append(failure).append(";").append(NEWLINE);
        out.append("        }").append(NEWLINE);
        out.append("        if fields is string[] {").append(NEWLINE);
        out.append("            ").append(t.relatedPartial).append("|error partial =").append(NEWLINE)
                .append("                dataservice:selectFields(").append(t.toWire("related")).append(", ")
                .append(t.description).append(", fields).cloneWithType();").append(NEWLINE);
        out.append("            if partial is error {").append(NEWLINE);
        out.append("                return dsHttp:toListError(partial, \"").append(n.displayName).append("\", \"")
                .append(RELATED_OPERATION).append("\");").append(NEWLINE);
        out.append("            }").append(NEWLINE);
        out.append("            return partial;").append(NEWLINE);
        out.append("        }").append(NEWLINE);
        out.append("        return ").append(t.toWire("related")).append(";").append(NEWLINE);
        out.append("    }").append(NEWLINE);
    }

    private void appendForbiddenCheck(StringBuilder out, ExposedEntity entity, Names n) {
        for (ExposedRelation relation : checked(entity)) {
            String name = "\"" + unescape(relation.field()) + "\"";
            out.append("        if dataservice:isIncluded(fields, ").append(name).append(") {").append(NEWLINE);
            appendAuthorize(out, n, relation, "            ");
            out.append("        }").append(NEWLINE);
        }
    }

    private void appendAuthorize(StringBuilder out, Names n, ExposedRelation relation, String indent) {
        ExposedEntity target = target(relation);
        out.append(indent).append("dsHttp:Forbidden? forbidden =").append(NEWLINE).append(indent)
                .append("    dsHttp:checkRelationScopes(ctx, \"")
                .append(n.displayName).append("\", \"").append(unescape(relation.field())).append("\", \"")
                .append(new Names(target).displayName).append("\", ").append(stringList(target.scopes(READ)))
                .append(");").append(NEWLINE);
        out.append(indent).append("if forbidden is dsHttp:Forbidden {").append(NEWLINE);
        out.append(indent).append("    return forbidden;").append(NEWLINE);
        out.append(indent).append("}").append(NEWLINE);
    }

    // With auth, every relation checks that the caller may read the related entity before it is read.
    private List<ExposedRelation> checked(ExposedEntity entity) {
        return auth ? entity.relations() : List.of();
    }

    // With auth, patch and delete return the record only to a caller who may read it.
    private boolean readChecked(ExposedEntity entity) {
        return auth && entity.allows(READ);
    }

    private void appendHoldsRead(StringBuilder out, ExposedEntity entity) {
        out.append("        boolean readable = dsHttp:hasScope(ctx, ").append(stringList(entity.scopes(READ)))
                .append(");").append(NEWLINE);
    }

    private String requestParam(ExposedEntity entity) {
        return checked(entity).isEmpty() ? "" : "http:RequestContext ctx, ";
    }

    // The 401 and 403 of the operation's scopes come from DataserviceAuthInterceptor, whose return type the OpenAPI
    // definition lists; a resource declares 403 only for a relation check of its own.
    private static String errors(List<String> errors, boolean checksRelation) {
        List<String> members = new ArrayList<>(errors);
        if (checksRelation) {
            members.add("dsHttp:Forbidden");
        }
        members.add("dsHttp:InternalError");
        return "|" + String.join("|", members);
    }

    private ExposedEntity target(ExposedRelation relation) {
        return entitiesByName.get(relation.target());
    }

    private static void appendFieldsCheck(StringBuilder out, Names n) {
        out.append("        dataservice:DataserviceError? invalidFields =").append(NEWLINE)
                .append("            dataservice:checkFields(")
                .append(n.description).append(", fields);").append(NEWLINE);
        out.append("        if invalidFields is dataservice:DataserviceError {").append(NEWLINE);
        out.append("            return dsHttp:toQueryError(invalidFields, \"").append(n.displayName).append("\");")
                .append(NEWLINE);
        out.append("        }").append(NEWLINE);
    }

    private void appendCreate(StringBuilder out, ExposedEntity entity, Names n, Set<String> imports) {
        imports.add(DATASERVICE_IMPORT);
        String insert = n.entity + "Insert";
        appendDoc(out, entity, "Creates one or more " + n.plural + ".",
                List.of("payload - The " + n.words + ", or the " + n.plural + ", to create"),
                "The created " + n.words + " or " + n.plural);
        out.append("    resource function post ").append(n.collection).append("(").append(n.insertWire).append("|")
                .append(n.insertWire).append("[] payload)").append(NEWLINE);
        out.append("            returns ").append(n.created).append("|").append(n.listCreated)
                .append(errors(CREATE_ERRORS, false))
                .append(" {").append(NEWLINE);
        String entries = entity.hasWireTypes() ? "entries" : "values";
        String entriesType = entity.hasWireTypes() ? n.insertWire : insert;
        out.append("        ").append(entriesType).append("[] ").append(entries).append(" = payload is ")
                .append(entriesType).append("[] ? payload : [payload];").append(NEWLINE);
        out.append("        dataservice:DataserviceError? tooMany = dataservice:checkCreateSize(").append(entries)
                .append(".length(), self.pageConfig);").append(NEWLINE);
        out.append("        if tooMany is dataservice:DataserviceError {").append(NEWLINE);
        out.append("            return <dsHttp:InvalidInput>{body: tooMany};").append(NEWLINE);
        out.append("        }").append(NEWLINE);
        if (entity.hasWireTypes()) {
            out.append("        ").append(insert).append("[] values = [];").append(NEWLINE);
            out.append("        foreach ").append(n.insertWire).append(" entry in entries {").append(NEWLINE);
            out.append("            ").append(insert).append("|dsHttp:InvalidInput value = transformPayloadTo")
                    .append(insert).append("(entry);").append(NEWLINE);
            out.append("            if value is dsHttp:InvalidInput {").append(NEWLINE);
            out.append("                return value;").append(NEWLINE);
            out.append("            }").append(NEWLINE);
            appendConstraintCheck(out, entity, n, insertSource(entity), "            ", imports);
            out.append("            values.push(value);").append(NEWLINE);
            out.append("        }").append(NEWLINE);
        }
        out.append("        ").append(n.wire).append("[] items = [];").append(NEWLINE);
        out.append("        if values.length() > 0 {").append(NEWLINE);
        List<ServiceField> keys = entity.keys();
        String keyType = keys.size() == 1 ? keys.get(0).type() :
                "[" + keys.stream().map(ServiceField::type).collect(Collectors.joining(", ")) + "]";
        String entityName = "\"" + n.displayName + "\"";
        // A failed check inside the transaction rolls it back, so a batch creates every record or none.
        out.append("            do {").append(NEWLINE);
        out.append("                transaction {").append(NEWLINE);
        out.append("                    ").append(keyType).append("[] keys = check self.persistClient->/")
                .append(n.resource).append(".post(values);").append(NEWLINE);
        out.append("                    dataservice:KeyValue[][] keyValues = ")
                .append(keys.size() == 1 ? "from " + keyType + " key in keys select [key]" : "keys").append(";")
                .append(NEWLINE);
        out.append("                    dataservice:Query query = dataservice:toKeysQuery(").append(n.description)
                .append(", keyValues);").append(NEWLINE);
        out.append("                    stream<").append(n.entity)
                .append(", persist:Error?> stored = self.persistClient->/").append(n.resource).append("(")
                .append(n.entity).append(",").append(NEWLINE);
        out.append("                        whereClause = query.whereClause, orderByClause = query.orderByClause);")
                .append(NEWLINE);
        out.append("                    ").append(n.entity).append("[] rows = check from ").append(n.entity)
                .append(" item in stored select item;").append(NEWLINE);
        out.append("                    check dataservice:orderByKeys(rows, ").append(n.description)
                .append(", keyValues);").append(NEWLINE);
        if (entity.hasWireTypes()) {
            out.append("                    items = from ").append(n.entity).append(" item in rows select ")
                    .append(n.toWire("item")).append(";").append(NEWLINE);
        } else {
            out.append("                    items = rows;").append(NEWLINE);
        }
        out.append("                    check commit;").append(NEWLINE);
        out.append("                }").append(NEWLINE);
        out.append("            } on fail error err {").append(NEWLINE);
        out.append("                return dsHttp:toCreateError(err, ").append(entityName).append(");")
                .append(NEWLINE);
        out.append("            }").append(NEWLINE);
        out.append("        }").append(NEWLINE);
        out.append("        if payload is ").append(n.insertWire).append("[] {").append(NEWLINE);
        out.append("            return <").append(n.listCreated).append(">{body: {items, hasMore: false}};")
                .append(NEWLINE);
        out.append("        }").append(NEWLINE);
        out.append("        ").append(n.wire).append(" created = items[0];").append(NEWLINE);
        out.append("        return <").append(n.created).append(">{").append(NEWLINE);
        out.append("            headers: {\"Location\": dsHttp:toLocation(\"").append(collectionPath(n.collection))
                .append("\", ").append(keyList(entity, "created.")).append(")},").append(NEWLINE);
        out.append("            body: created").append(NEWLINE);
        out.append("        };").append(NEWLINE);
        out.append("    }").append(NEWLINE);
    }

    private void appendDoc(StringBuilder out, ExposedEntity entity, String summary,
                           List<String> params, String result) {
        out.append(NEWLINE).append("    # ").append(summary).append(NEWLINE);
        if (!entity.doc().isEmpty()) {
            out.append("    #").append(NEWLINE);
            for (String line : entity.doc()) {
                out.append("    #").append(line.isEmpty() ? "" : " " + line).append(NEWLINE);
            }
        }
        out.append("    #").append(NEWLINE);
        for (String param : params) {
            out.append("    # + ").append(param).append(NEWLINE);
        }
        out.append("    # + return - ").append(result).append(", or the error response").append(NEWLINE);
    }

    private static List<String> keyDocs(ExposedEntity entity, Names n) {
        return entity.keys().stream().map(key -> key.name() + " - The " + unescape(key.name()) + " of the " +
                n.words).toList();
    }

    // The wire type cannot carry the constraints of a converted field, so the converted record is checked.
    private static void appendConstraintCheck(StringBuilder out, ExposedEntity entity, Names n,
                                              List<ServiceField> fields, String indent, Set<String> imports) {
        if (fields.stream().noneMatch(field -> field.conversion().isPresent()
                && !entity.constraints(field.name()).isEmpty())) {
            return;
        }
        imports.add(DATASERVICE_IMPORT);
        out.append(indent).append("dataservice:DataserviceError? broken = dataservice:checkConstraints(value, \"")
                .append(n.displayName).append("\");").append(NEWLINE);
        out.append(indent).append("if broken is dataservice:DataserviceError {").append(NEWLINE);
        out.append(indent).append("    return <dsHttp:InvalidInput>{body: broken};").append(NEWLINE);
        out.append(indent).append("}").append(NEWLINE);
    }

    // A float key value can be NaN or infinite, which no record has.
    private static void appendKeyCheck(StringBuilder out, ExposedEntity entity, Names n, String keyList,
                                       Set<String> imports) {
        if (entity.keys().stream().noneMatch(key -> "float".equals(key.type()))) {
            return;
        }
        imports.add(DATASERVICE_IMPORT);
        out.append("        dataservice:DataserviceError? invalidKey = dataservice:checkFiniteKey(\"")
                .append(n.displayName).append("\", ").append(keyList).append(");").append(NEWLINE);
        out.append("        if invalidKey is dataservice:DataserviceError {").append(NEWLINE);
        out.append("            return <dsHttp:InvalidInput>{body: invalidKey};").append(NEWLINE);
        out.append("        }").append(NEWLINE);
    }

    private static void appendErrorCheck(StringBuilder out, String variable, String function, Names n,
                                         String keyList, String indent) {
        appendErrorCheck(out, variable, function, n, keyList, indent, null);
    }

    private static void appendErrorCheck(StringBuilder out, String variable, String function, Names n,
                                         String keyList, String indent, String operation) {
        out.append(indent).append("if ").append(variable).append(" is persist:Error {").append(NEWLINE);
        out.append(indent).append("    return dsHttp:").append(function).append("(").append(variable).append(", \"")
                .append(n.displayName).append("\", ").append(keyList)
                .append(operation == null ? "" : ", \"" + operation + "\"").append(");").append(NEWLINE);
        out.append(indent).append("}").append(NEWLINE);
    }

    private void appendTypes(StringBuilder out, ExposedEntity entity, Set<String> imports) {
        Names n = new Names(entity);
        if (entity.hasWireTypes()) {
            appendRecord(out, entity, "The JSON form of `" + n.entity + "`.", n.wire, n.entity,
                    returned(entity.fields()), imports);
            if (entity.allows(CREATE)) {
                String doc = "The JSON form of `" + n.entity + "Insert`.";
                List<ServiceField> insertFields = insertSource(entity);
                if (entity.insertFields().isPresent() || insertFields.stream().anyMatch(f -> f.rule().isPresent())) {
                    appendRecord(out, entity, doc, n.insertWire, n.entity + "Insert", insertWire(insertFields),
                            imports);
                } else {
                    out.append(NEWLINE).append("# ").append(doc).append(NEWLINE);
                    out.append("public type ").append(n.insertWire).append(" ").append(n.wire).append(";")
                            .append(NEWLINE);
                }
            }
            if (entity.allows(UPDATE)) {
                appendRecord(out, entity, "The JSON form of `" + n.entity + "Update`.", n.updateWire,
                        n.entity + "Update", updateWire(entity.updateFields()), imports);
            }
        }
        if (entity.allows(READ) || entity.allows(CREATE)) {
            out.append(NEWLINE).append("# A list of ").append(n.plural).append(".").append(NEWLINE);
            out.append("#").append(NEWLINE);
            out.append("# + items - The ").append(n.plural).append(NEWLINE);
            out.append("# + hasMore - Whether records exist after this page").append(NEWLINE);
            out.append("public type ").append(n.list).append(" record {").append(NEWLINE);
            out.append("    ").append(n.wire).append("[] items;").append(NEWLINE);
            out.append("    boolean hasMore;").append(NEWLINE);
            out.append("};").append(NEWLINE);
        }
        if (entity.allows(READ)) {
            appendFilter(out, entity, n);
            appendPartial(out, entity, n);
            appendRelatedPartial(out, entity, n);
        }
        if (entity.allows(READ) || entity.allows(CREATE)) {
            imports.add(DATASERVICE_IMPORT);
            appendDescription(out, entity, n);
        }
        if (entity.allows(CREATE)) {
            imports.add(HTTP_IMPORT);
            appendStatusRecord(out, "The response to creating one " + n.words + ".", n.created, n.wire,
                    "The created " + n.words);
            appendStatusRecord(out, "The response to creating several " + n.plural + ".", n.listCreated, n.list,
                    "The created " + n.plural);
        }
    }

    private static void appendFilter(StringBuilder out, ExposedEntity entity, Names n) {
        List<ServiceField> fields = filterable(entity);
        out.append(NEWLINE).append("# The conditions that ").append(n.plural)
                .append(" match, as the `filter` query parameter holds them.").append(NEWLINE);
        out.append("#").append(NEWLINE);
        for (ServiceField field : fields) {
            out.append("# + ").append(field.name()).append(" - The conditions on the `").append(unescape(field.name()))
                    .append("` field").append(NEWLINE);
        }
        out.append("public type ").append(n.filter).append(" record {|").append(NEWLINE);
        for (ServiceField field : fields) {
            ExposedEntity.Column column = column(entity, field);
            String type = field.conversion().isPresent() ? "string" : field.type();
            List<String> operators = new ArrayList<>(List.of(type + " eq?;", type + " ne?;"));
            if (!"BOOLEAN".equals(column.type()) && !"ENUM".equals(column.type())) {
                for (String operator : List.of("lt", "lte", "gt", "gte")) {
                    operators.add(type + " " + operator + "?;");
                }
            }
            operators.add(type + "[] anyOf?;");
            if ("STRING".equals(column.type())) {
                operators.addAll(List.of("string contains?;", "string startsWith?;"));
            }
            if (column.nullable()) {
                operators.add("boolean isNull?;");
            }
            out.append("    record {|").append(NEWLINE);
            for (String operator : operators) {
                out.append("        ").append(operator).append(NEWLINE);
            }
            out.append("    |} ").append(field.name()).append("?;").append(NEWLINE);
        }
        out.append("|};").append(NEWLINE);
    }

    private void appendPartial(StringBuilder out, ExposedEntity entity, Names n) {
        List<ServiceField> fields = returned(entity.fields());
        out.append(NEWLINE).append("# ").append(capitalize(n.article)).append(" ").append(n.words)
                .append(" with the fields named in the `fields` query parameter.").append(NEWLINE);
        out.append("#").append(NEWLINE);
        for (ServiceField field : fields) {
            out.append("# + ").append(field.name()).append(" - ").append(fieldDoc(field, n.entity, entity))
                    .append(NEWLINE);
        }
        for (ExposedRelation relation : entity.relations()) {
            out.append("# + ").append(relation.field()).append(" - The related ")
                    .append(relation.many() ? new Names(target(relation)).plural + ", when `fields` names them"
                            : new Names(target(relation)).words + ", when `fields` names it").append(NEWLINE);
        }
        out.append("public type ").append(n.partial).append(" record {|").append(NEWLINE);
        for (ServiceField field : fields) {
            out.append("    ").append(field.wireType()).append(" ").append(field.name())
                    .append(field.key() ? "" : "?").append(";").append(NEWLINE);
        }
        for (ExposedRelation relation : entity.relations()) {
            Names t = new Names(target(relation));
            out.append("    ").append(relation.many() ? t.relatedPartialList : t.relatedPartial + "?").append(" ")
                    .append(relation.field()).append("?;").append(NEWLINE);
        }
        out.append("|};").append(NEWLINE);
        out.append(NEWLINE).append("# A list of ").append(n.plural)
                .append(" with the fields named in the `fields` query parameter.").append(NEWLINE);
        out.append("#").append(NEWLINE);
        out.append("# + items - The ").append(n.plural).append(NEWLINE);
        out.append("# + hasMore - Whether records exist after this page").append(NEWLINE);
        out.append("public type ").append(n.partialList).append(" record {").append(NEWLINE);
        out.append("    ").append(n.partial).append("[] items;").append(NEWLINE);
        out.append("    boolean hasMore;").append(NEWLINE);
        out.append("};").append(NEWLINE);
    }

    // A related entity that callers can read is described in each relation that reaches it as well, with its own
    // fields only, so that every read is one hop.
    private void appendRelatedPartial(StringBuilder out, ExposedEntity entity, Names n) {
        List<ExposedRelation> reaching = entities.stream().flatMap(other -> other.relations().stream())
                .filter(relation -> relation.target().equals(entity.name())).toList();
        if (reaching.isEmpty()) {
            return;
        }
        out.append(NEWLINE).append("# ").append(capitalize(n.article)).append(" ").append(n.words)
                .append(" read through a relation, with the fields named in the `fields` query parameter.")
                .append(NEWLINE);
        out.append("#").append(NEWLINE);
        List<ServiceField> fields = returned(entity.fields());
        for (ServiceField field : fields) {
            out.append("# + ").append(field.name()).append(" - ").append(fieldDoc(field, n.entity, entity))
                    .append(NEWLINE);
        }
        out.append("public type ").append(n.relatedPartial).append(" record {|").append(NEWLINE);
        for (ServiceField field : fields) {
            out.append("    ").append(field.wireType()).append(" ").append(field.name())
                    .append(field.key() ? "" : "?").append(";").append(NEWLINE);
        }
        out.append("|};").append(NEWLINE);
        if (reaching.stream().anyMatch(ExposedRelation::many)) {
            out.append(NEWLINE).append("# The ").append(n.plural)
                    .append(" read through a relation, with the fields named in the `fields` query parameter.")
                    .append(NEWLINE);
            out.append("#").append(NEWLINE);
            out.append("# + items - The ").append(n.plural).append(", at most `dataserviceConfig.pageSize`")
                    .append(NEWLINE);
            out.append("# + hasMore - Whether more ").append(n.plural).append(" exist").append(NEWLINE);
            out.append("public type ").append(n.relatedPartialList).append(" record {").append(NEWLINE);
            out.append("    ").append(n.relatedPartial).append("[] items;").append(NEWLINE);
            out.append("    boolean hasMore;").append(NEWLINE);
            out.append("};").append(NEWLINE);
        }
    }

    private void appendDescription(StringBuilder out, ExposedEntity entity, Names n) {
        out.append(NEWLINE).append("# The fields of `").append(n.entity).append("` that callers can see.")
                .append(NEWLINE);
        out.append("final readonly & dataservice:Field[] ").append(n.fields).append(" = [").append(NEWLINE);
        List<String> members = new ArrayList<>();
        for (ExposedEntity.Column column : entity.columns()) {
            StringBuilder member = new StringBuilder("    {name: \"").append(column.name())
                    .append("\", column: \"").append(column.column()).append("\", 'type: dataservice:")
                    .append(column.type());
            if (column.nullable()) {
                member.append(", nullable: true");
            }
            if (column.key()) {
                member.append(", key: true");
            }
            if (column.generated()) {
                member.append(", generated: true");
            }
            members.add(member.append("}").toString());
        }
        out.append(String.join("," + NEWLINE, members)).append(NEWLINE).append("];").append(NEWLINE);
        out.append(NEWLINE).append("# The description of `").append(n.entity)
                .append("` that `ballerina/dataservice` builds queries from.").append(NEWLINE);
        out.append("final dataservice:Entity ").append(n.description).append(" = ")
                .append(entityLiteral(entity, "", true)).append(";").append(NEWLINE);
    }

    private String entityLiteral(ExposedEntity entity, String indent, boolean withRelations) {
        Names n = new Names(entity);
        StringBuilder out = new StringBuilder("{").append(NEWLINE);
        out.append(indent).append("    name: \"").append(n.displayName).append("\",").append(NEWLINE);
        out.append(indent).append("    collection: \"").append(n.collection).append("\",").append(NEWLINE);
        out.append(indent).append("    'resource: \"").append(n.resource).append("\",").append(NEWLINE);
        out.append(indent).append("    datastore: dataservice:").append(datastore.toUpperCase(Locale.ENGLISH))
                .append(",").append(NEWLINE);
        out.append(indent).append("    fields: ").append(n.fields);
        if (withRelations && !entity.relations().isEmpty()) {
            out.append(",").append(NEWLINE).append(indent).append("    relations: [").append(NEWLINE);
            List<String> relations = new ArrayList<>();
            for (ExposedRelation relation : entity.relations()) {
                relations.add(indent + "        {" + NEWLINE
                        + indent + "            'field: \"" + unescape(relation.field()) + "\"," + NEWLINE
                        + indent + "            many: " + relation.many() + "," + NEWLINE
                        + indent + "            fields: " + stringList(relation.fields()) + "," + NEWLINE
                        + indent + "            targetFields: " + stringList(relation.targetFields()) + "," + NEWLINE
                        + indent + "            target: " + entityLiteral(target(relation), indent + "            ",
                        false) + NEWLINE + indent + "        }");
            }
            out.append(String.join("," + NEWLINE, relations)).append(NEWLINE).append(indent).append("    ]");
        }
        return out.append(NEWLINE).append(indent).append("}").toString();
    }

    private static String stringList(List<String> names) {
        return "[" + names.stream().map(name -> "\"" + unescape(name) + "\"").collect(Collectors.joining(", "))
                + "]";
    }

    // Filter keys starting with `$` are reserved for joining conditions.
    private static List<ServiceField> filterable(ExposedEntity entity) {
        return returned(entity.fields()).stream().filter(field -> column(entity, field).queryable()
                && !unescape(field.name()).startsWith("$")).toList();
    }

    private static ExposedEntity.Column column(ExposedEntity entity, ServiceField field) {
        String name = unescape(field.name());
        return entity.columns().stream().filter(column -> column.name().equals(name)).findFirst().orElseThrow();
    }

    private static String capitalize(String text) {
        return text.substring(0, 1).toUpperCase(Locale.ENGLISH) + text.substring(1);
    }

    private void appendRecord(StringBuilder out, ExposedEntity entity, String doc, String name,
                              String persistType, List<ServiceField> fields, Set<String> imports) {
        out.append(NEWLINE).append("# ").append(doc).append(NEWLINE);
        out.append("#").append(NEWLINE);
        for (ServiceField field : fields) {
            out.append("# + ").append(field.name()).append(" - ").append(fieldDoc(field, persistType, entity))
                    .append(NEWLINE);
        }
        out.append("public type ").append(name).append(" record {|").append(NEWLINE);
        for (ServiceField field : fields) {
            // A date, time or byte[] field is a string here, so its constraints are checked after conversion.
            if (field.conversion().isEmpty()) {
                for (String annotation : entity.constraints(field.name())) {
                    imports.add("import ballerina/constraint;");
                    out.append("    ").append(annotation).append(NEWLINE);
                }
            }
            out.append("    ").append(field.key() ? "readonly " : "").append(field.wireType()).append(" ")
                    .append(field.name()).append(field.optional() ? "?" : "").append(";").append(NEWLINE);
        }
        out.append("|};").append(NEWLINE);
    }

    private static void appendStatusRecord(StringBuilder out, String doc, String name, String bodyType,
                                           String bodyDoc) {
        out.append(NEWLINE).append("# ").append(doc).append(NEWLINE);
        out.append("#").append(NEWLINE);
        out.append("# + body - ").append(bodyDoc).append(NEWLINE);
        out.append("public type ").append(name).append(" record {|").append(NEWLINE);
        out.append("    *http:Created;").append(NEWLINE);
        out.append("    ").append(bodyType).append(" body;").append(NEWLINE);
        out.append("|};").append(NEWLINE);
    }

    private static String fieldDoc(ServiceField field, String persistType, ExposedEntity entity) {
        if (field.doc().isPresent()) {
            return field.doc().get();
        }
        Optional<String> target = entity.foreignKeyTarget(field.name());
        if (target.isPresent()) {
            return "The key of the related '" + target.get() + "'.";
        }
        String doc = "The `" + unescape(field.name()) + "` field of `" + persistType + "`";
        if (field.conversion().isEmpty()) {
            return doc;
        }
        return doc + switch (field.conversion().get()) {
            case BYTES -> ", as a base64 string";
            case DATE -> ", as an ISO 8601 date";
            case TIME_OF_DAY -> ", as an ISO 8601 time";
            case CIVIL -> ", as an ISO 8601 date-time without an offset";
            case UTC -> ", as an ISO 8601 date-time in UTC";
        };
    }

    private void appendConverters(StringBuilder out, ExposedEntity entity, Set<String> imports) {
        if (!entity.hasWireTypes()) {
            return;
        }
        Names n = new Names(entity);
        appendToWire(out, entity, n, imports);
        if (entity.allows(CREATE)) {
            appendFromWire(out, n, n.insertWire, n.entity + "Insert", insertSource(entity), imports);
        }
        if (entity.allows(UPDATE)) {
            appendFromUpdateWire(out, n, entity.updateFields(), imports);
        }
    }

    private void appendToWire(StringBuilder out, ExposedEntity entity, Names n, Set<String> imports) {
        String param = parameterName("value", entity.fields());
        out.append(NEWLINE).append("# Converts ").append(n.article).append(" `").append(n.entity)
                .append("` to its JSON form.").append(NEWLINE);
        out.append("#").append(NEWLINE);
        out.append("# + ").append(param).append(" - The ").append(n.entity).append(" to convert").append(NEWLINE);
        out.append("# + return - The JSON form").append(NEWLINE);
        out.append("isolated function transform").append(n.entity).append("ToPayload(").append(n.entity).append(" ")
                .append(param).append(") returns ").append(n.wire);
        StringBuilder statements = new StringBuilder();
        List<String> members = new ArrayList<>();
        for (ServiceField field : returned(entity.fields())) {
            String access = param + "." + field.name();
            String value;
            if (field.conversion().isEmpty()) {
                value = access;
            } else if (field.nilable()) {
                String text = unescape(field.name()) + "Text";
                addConversionImports(field, imports);
                statements.append("    ").append(field.persistType()).append(" ").append(field.name()).append(" = ")
                        .append(access).append(";").append(NEWLINE);
                statements.append("    string? ").append(text).append(" = ();").append(NEWLINE);
                statements.append("    if ").append(field.name()).append(" !is () {").append(NEWLINE);
                statements.append("        ").append(text).append(" = ")
                        .append(field.conversion().get().toStringFunction()).append("(").append(field.name())
                        .append(");").append(NEWLINE);
                statements.append("    }").append(NEWLINE);
                value = text;
            } else {
                imports.add(DATASERVICE_IMPORT);
                value = field.conversion().get().toStringFunction() + "(" + access + ")";
            }
            members.add("    " + field.name() + ": " + value);
        }
        String constructor = "{" + NEWLINE + String.join("," + NEWLINE, members) + NEWLINE + "}";
        if (statements.isEmpty()) {
            out.append(" => ").append(constructor).append(";").append(NEWLINE);
        } else {
            out.append(" {").append(NEWLINE).append(statements).append("    return ").append(constructor).append(";")
                    .append(NEWLINE).append("}").append(NEWLINE);
        }
    }

    private void appendFromWire(StringBuilder out, Names n, String wireType, String persistType,
                                List<ServiceField> fields, Set<String> imports) {
        imports.add(DATASERVICE_HTTP_IMPORT);
        String param = parameterName("payload", fields);
        out.append(NEWLINE).append("# Converts the JSON form of ").append(articleFor(persistType)).append(" `")
                .append(persistType).append("` to the persist type.").append(NEWLINE);
        out.append("#").append(NEWLINE);
        out.append("# + ").append(param).append(" - The JSON form to convert").append(NEWLINE);
        out.append("# + return - The persist type, or `InvalidInput` when a value cannot be converted")
                .append(NEWLINE);
        out.append("isolated function transformPayloadTo").append(persistType).append("(").append(wireType).append(" ")
                .append(param).append(")").append(NEWLINE).append("        returns ").append(persistType)
                .append("|dsHttp:InvalidInput {")
                .append(NEWLINE);
        StringBuilder members = new StringBuilder();
        for (ServiceField field : fields) {
            if (!members.isEmpty()) {
                members.append(", ");
            }
            String access = param + "." + field.name();
            if (field.has(FieldRule.READ_ONLY) || field.has(FieldRule.INTERNAL)) {
                members.append(field.name()).append(": ").append(field.value().orElse("()"));
                continue;
            }
            if (field.conversion().isEmpty()) {
                members.append(field.name()).append(": ").append(access);
                continue;
            }
            members.append(field.name());
            ServiceField.Conversion conversion = field.conversion().get();
            addConversionImports(field, imports);
            out.append("    ").append(field.persistType()).append("|dataservice:ConversionError ")
                    .append(field.name()).append(" = ");
            if (field.nilable()) {
                String text = unescape(field.name()) + "Text";
                out.append("();").append(NEWLINE);
                out.append("    string? ").append(text).append(" = ").append(access).append(";").append(NEWLINE);
                out.append("    if ").append(text).append(" !is () {").append(NEWLINE);
                out.append("        ").append(field.name()).append(" = ").append(conversion.fromStringFunction())
                        .append("(").append(text).append(");").append(NEWLINE);
                out.append("    }").append(NEWLINE);
            } else {
                out.append(conversion.fromStringFunction()).append("(").append(access).append(");").append(NEWLINE);
            }
            appendInvalidInputCheck(out, n, field, "    ");
        }
        out.append("    return {").append(members).append("};").append(NEWLINE);
        out.append("}").append(NEWLINE);
    }

    private void appendFromUpdateWire(StringBuilder out, Names n, List<ServiceField> fields, Set<String> imports) {
        imports.add(DATASERVICE_HTTP_IMPORT);
        String persistType = n.entity + "Update";
        String param = parameterName("payload", fields);
        String result = parameterName("value", fields);
        out.append(NEWLINE).append("# Converts the JSON form of ").append(articleFor(persistType)).append(" `")
                .append(persistType).append("` to the persist type.").append(NEWLINE);
        out.append("#").append(NEWLINE);
        out.append("# + ").append(param).append(" - The JSON form to convert").append(NEWLINE);
        out.append("# + return - The persist type, or `InvalidInput` when a value cannot be converted")
                .append(NEWLINE);
        out.append("isolated function transformPayloadTo").append(persistType).append("(").append(n.updateWire)
                .append(" ").append(param).append(")").append(NEWLINE).append("        returns ").append(persistType)
                .append("|dsHttp:InvalidInput {")
                .append(NEWLINE);
        out.append("    ").append(persistType).append(" ").append(result).append(" = {};").append(NEWLINE);
        for (ServiceField field : fields) {
            if (field.rule().isPresent()) {
                continue;
            }
            String name = field.name();
            String access = param + "?." + name;
            String text = unescape(name) + "Text";
            String key = "\"" + unescape(name) + "\"";
            if (field.conversion().isEmpty() && field.nilable()) {
                out.append("    if ").append(param).append(".hasKey(").append(key).append(") {").append(NEWLINE);
                out.append("        ").append(result).append(".").append(name).append(" = ").append(access)
                        .append(";").append(NEWLINE);
                out.append("    }").append(NEWLINE);
            } else if (field.conversion().isEmpty()) {
                out.append("    ").append(field.type()).append("? ").append(name).append(" = ").append(access)
                        .append(";").append(NEWLINE);
                out.append("    if ").append(name).append(" !is () {").append(NEWLINE);
                out.append("        ").append(result).append(".").append(name).append(" = ").append(name)
                        .append(";").append(NEWLINE);
                out.append("    }").append(NEWLINE);
            } else {
                ServiceField.Conversion conversion = field.conversion().get();
                addConversionImports(field, imports);
                String indent = "    ";
                if (field.nilable()) {
                    out.append("    if ").append(param).append(".hasKey(").append(key).append(") {")
                            .append(NEWLINE);
                    out.append("        string? ").append(text).append(" = ").append(access).append(";")
                            .append(NEWLINE);
                    out.append("        if ").append(text).append(" is () {").append(NEWLINE);
                    out.append("            ").append(result).append(".").append(name).append(" = ();")
                            .append(NEWLINE);
                    out.append("        } else {").append(NEWLINE);
                    indent = "            ";
                } else {
                    out.append("    string? ").append(text).append(" = ").append(access).append(";").append(NEWLINE);
                    out.append("    if ").append(text).append(" is string {").append(NEWLINE);
                    indent = "        ";
                }
                out.append(indent).append(field.type()).append("|dataservice:ConversionError ").append(name)
                        .append(" = ").append(conversion.fromStringFunction()).append("(").append(text).append(");")
                        .append(NEWLINE);
                appendInvalidInputCheck(out, n, field, indent);
                out.append(indent).append(result).append(".").append(name).append(" = ").append(name).append(";")
                        .append(NEWLINE);
                if (field.nilable()) {
                    out.append("        }").append(NEWLINE);
                }
                out.append("    }").append(NEWLINE);
            }
        }
        out.append("    return ").append(result).append(";").append(NEWLINE);
        out.append("}").append(NEWLINE);
    }

    private static void appendInvalidInputCheck(StringBuilder out, Names n, ServiceField field, String indent) {
        out.append(indent).append("if ").append(field.name()).append(" is dataservice:ConversionError {")
                .append(NEWLINE);
        out.append(indent).append("    return dsHttp:toInvalidInput(").append(field.name()).append(", \"")
                .append(n.displayName).append("\", \"").append(unescape(field.name())).append("\");")
                .append(NEWLINE);
        out.append(indent).append("}").append(NEWLINE);
    }

    private static List<ServiceField> insertSource(ExposedEntity entity) {
        return entity.insertFields().orElse(entity.fields());
    }

    private static List<ServiceField> returned(List<ServiceField> fields) {
        return fields.stream().filter(field -> !field.has(FieldRule.INTERNAL)).toList();
    }

    // A read-only field may be sent back, as read, and is ignored.
    private static List<ServiceField> insertWire(List<ServiceField> fields) {
        return returned(fields).stream().map(field -> field.has(FieldRule.READ_ONLY) ?
                new ServiceField(field.name(), field.type(), field.nilable(), true, field.key(), field.rule(),
                        field.value(), field.doc()) : field).toList();
    }

    private static List<ServiceField> updateWire(List<ServiceField> fields) {
        return fields.stream().filter(field -> !field.has(FieldRule.IMMUTABLE) && !field.has(FieldRule.INTERNAL))
                .toList();
    }

    private static void addConversionImports(ServiceField field, Set<String> imports) {
        imports.add(DATASERVICE_IMPORT);
        if (field.type().startsWith("time:")) {
            imports.add("import ballerina/time;");
        }
    }

    private String collectionPath(String resource) {
        return ("/".equals(basePath) ? "" : basePath) + "/" + resource;
    }

    private static String keyPath(ExposedEntity entity) {
        return entity.keys().stream().map(key -> "/[" + key.name() + "]").collect(Collectors.joining());
    }

    private static String keyList(ExposedEntity entity, String prefix) {
        return "[" + entity.keys().stream().map(key -> prefix + key.name()).collect(Collectors.joining(", ")) + "]";
    }

    // A field with the same name as the parameter would shadow it in the generated record constructor.
    private static String parameterName(String preferred, List<ServiceField> fields) {
        String name = preferred;
        while (true) {
            String candidate = name;
            if (fields.stream().noneMatch(field -> unescape(field.name()).equals(candidate)
                    || (unescape(field.name()) + "Text").equals(candidate))) {
                return name;
            }
            name = "_" + name;
        }
    }

    private static String articleFor(String name) {
        return "aeiouAEIOU".indexOf(unescape(name).charAt(0)) >= 0 ? "an" : "a";
    }

    // A syntax error is reported with its line, so that a broken template is found from the message.
    static String format(String source) throws BalException {
        SyntaxTree tree = SyntaxTree.from(TextDocuments.from(source));
        for (Diagnostic diagnostic : tree.diagnostics()) {
            if (diagnostic.diagnosticInfo().severity() == DiagnosticSeverity.ERROR) {
                int line = diagnostic.location().lineRange().startLine().line();
                throw new BalException(String.format("failed to format the data service source: %s at line %d: %s",
                        diagnostic.message(), line + 1, tree.textDocument().line(line).text().strip()));
            }
        }
        try {
            return Formatter.format(tree).toSourceCode();
        } catch (FormatterException e) {
            throw new BalException("failed to format the data service source. " + e.getMessage());
        }
    }


    private static final class Names {
        private final String entity;
        private final String displayName;
        private final String resource;
        private final String collection;
        private final String wire;
        private final String insertWire;
        private final String updateWire;
        private final String list;
        private final String filter;
        private final String partial;
        private final String partialList;
        private final String description;
        private final String fields;
        private final String relatedPartial;
        private final String relatedPartialList;
        private final String created;
        private final String listCreated;
        private final String words;
        private final String plural;
        private final String article;
        private final boolean hasWire;

        private Names(ExposedEntity entity) {
            this.entity = entity.name();
            this.displayName = unescape(entity.name());
            this.resource = entity.resourceName();
            this.collection = entity.collection();
            this.hasWire = entity.hasWireTypes();
            this.wire = hasWire ? this.entity + "Payload" : this.entity;
            this.insertWire = this.entity + (hasWire ? "InsertPayload" : "Insert");
            this.updateWire = this.entity + (hasWire ? "UpdatePayload" : "Update");
            this.list = this.entity + "List";
            this.filter = this.entity + "Filter";
            this.partial = this.entity + "Partial";
            this.partialList = this.entity + "PartialList";
            this.description = "dataservice" + displayName + "Entity";
            this.fields = "dataservice" + displayName + "Fields";
            this.relatedPartial = this.entity + "RelatedPartial";
            this.relatedPartialList = this.entity + "RelatedPartialList";
            this.created = this.entity + "Created";
            this.listCreated = this.entity + "ListCreated";
            this.words = String.join(" ", WORD_BOUNDARY.split(displayName)).toLowerCase(Locale.ENGLISH);
            this.plural = Pluralizer.pluralize(words);
            this.article = articleFor(words);
        }

        private String toWire(String expression) {
            return hasWire ? "transform" + entity + "ToPayload(" + expression + ")" : expression;
        }
    }
}
