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
import io.ballerina.persist.BalException;
import io.ballerina.persist.inflector.Pluralizer;
import io.ballerina.persist.nodegenerator.syntax.constants.BalSyntaxConstants;
import io.ballerina.persist.nodegenerator.syntax.utils.BalSyntaxUtils;
import org.ballerinalang.formatter.core.Formatter;
import org.ballerinalang.formatter.core.FormatterException;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
    private static final String GET_ERRORS = "|dsHttp:NotFound|dsHttp:InternalError";
    private static final String CREATE_ERRORS =
            "|dsHttp:NotFound|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError";
    private static final String WRITE_ERRORS = "|dsHttp:NotFound|dsHttp:ConstraintViolation|dsHttp:InternalError";
    private static final String INVALID_INPUT = "|dsHttp:InvalidInput";
    private static final Pattern IDENTIFIER = Pattern.compile("[A-Za-z_][A-Za-z0-9_]*");
    private static final Pattern WORD_BOUNDARY = Pattern.compile("(?<=[a-z0-9])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])");

    private final List<ExposedEntity> entities;
    private final String basePath;
    private final String modelName;
    private final boolean userOwned;
    private final Set<String> imports = new TreeSet<>();

    /**
     * Creates a generator for a data service.
     *
     * @param entities  the exposed entities, in model order
     * @param options   the data service options
     * @param modelName the name of the persist model, as persist writes it in its file headers
     * @param userOwned whether the files are written for the user to own, by `bal persist generate`
     */
    public HttpDataServiceGenerator(List<ExposedEntity> entities, DataServiceOptions options, String modelName,
                                    boolean userOwned) {
        this.entities = entities;
        this.basePath = options.basePath();
        this.modelName = modelName;
        this.userOwned = userOwned;
    }

    /**
     * Generates `dataservice.bal`: the client, the listener and the service.
     *
     * @return the formatted source
     * @throws BalException if the source cannot be formatted
     */
    public String generateService() throws BalException {
        imports.clear();
        imports.add("import ballerina/dataservice.http as dsHttp;");
        imports.add("import ballerina/http;");
        imports.add("import ballerina/persist;");
        StringBuilder body = new StringBuilder();
        body.append("# The persist client that the data service uses.").append(NEWLINE);
        body.append("final Client dataserviceClient = check new ();").append(NEWLINE).append(NEWLINE);
        body.append("# The listener of the data service.").append(NEWLINE);
        body.append("listener http:Listener dataserviceListener = new (dataservicePort, dataserviceListenerConfig);")
                .append(NEWLINE).append(NEWLINE);
        body.append("service ").append(servicePath()).append(" on dataserviceListener {").append(NEWLINE);
        for (ExposedEntity entity : entities) {
            appendResources(body, entity);
        }
        body.append("}").append(NEWLINE);
        return format(modelHeader() + String.join(NEWLINE, imports) + NEWLINE + NEWLINE + body);
    }

    /**
     * Generates `dataservice_types.bal`: per entity in model order, the wire types, the list envelope, the status
     * code records and the functions that convert between the wire types and the persist types.
     *
     * @return the formatted source, or empty when no exposed entity needs a type
     * @throws BalException if the source cannot be formatted
     */
    public Optional<String> generateTypes() throws BalException {
        imports.clear();
        StringBuilder body = new StringBuilder();
        for (ExposedEntity entity : entities) {
            appendTypes(body, entity);
            appendConverters(body, entity);
        }
        if (body.isEmpty()) {
            return Optional.empty();
        }
        if (entities.stream().anyMatch(entity -> entity.allows(CREATE))) {
            imports.add("import ballerina/http;");
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
                "import ballerina/http;" + NEWLINE + NEWLINE +
                "# The port of the data service listener." + NEWLINE +
                "configurable int dataservicePort = 9090;" + NEWLINE + NEWLINE +
                "# The configuration of the data service listener." + NEWLINE +
                "configurable http:ListenerConfiguration dataserviceListenerConfig = {};" + NEWLINE);
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

    private void appendResources(StringBuilder out, ExposedEntity entity) {
        Names n = new Names(entity);
        String keyParams = entity.keys().stream().map(key -> "[" + key.type() + " " + key.name() + "]")
                .collect(Collectors.joining("/"));
        String keyPath = keyPath(entity);
        String keyList = keyList(entity, "");
        if (entity.allows(READ)) {
            out.append(NEWLINE).append("    # Gets the ").append(n.plural).append(".").append(NEWLINE);
            out.append("    #").append(NEWLINE);
            out.append("    # + return - The ").append(n.plural).append(", or an internal error").append(NEWLINE);
            out.append("    resource function get ").append(n.resource).append("() returns ").append(n.list)
                    .append("|dsHttp:InternalError {").append(NEWLINE);
            out.append("        ").append(n.wire).append("[]|persist:Error items = from ").append(n.entity)
                    .append(" item in dataserviceClient->/").append(n.resource).append("(").append(n.entity)
                    .append(")").append(NEWLINE);
            out.append("            select ").append(n.toWire("item")).append(";").append(NEWLINE);
            out.append("        if items is persist:Error {").append(NEWLINE);
            out.append("            return dsHttp:toListError(items);").append(NEWLINE);
            out.append("        }").append(NEWLINE);
            out.append("        return {items};").append(NEWLINE);
            out.append("    }").append(NEWLINE);

            out.append(NEWLINE).append("    # Gets ").append(n.article).append(" ").append(n.words)
                    .append(" by its key.").append(NEWLINE);
            appendReturnDoc(out, "The " + n.words);
            out.append("    resource function get ").append(n.resource).append("/").append(keyParams)
                    .append("() returns ").append(n.wire).append(GET_ERRORS).append(" {").append(NEWLINE);
            out.append("        ").append(n.entity).append("|persist:Error item = dataserviceClient->/")
                    .append(n.resource).append(keyPath).append(";").append(NEWLINE);
            appendErrorCheck(out, "item", "toGetError", n, keyList, "        ");
            out.append("        return ").append(n.toWire("item")).append(";").append(NEWLINE);
            out.append("    }").append(NEWLINE);
        }
        if (entity.allows(CREATE)) {
            appendCreate(out, entity, n, keyPath, keyList);
        }
        if (entity.allows(UPDATE)) {
            out.append(NEWLINE).append("    # Updates the given fields of ").append(n.article).append(" ")
                    .append(n.words).append(".").append(NEWLINE);
            appendReturnDoc(out, "The updated " + n.words);
            out.append("    resource function patch ").append(n.resource).append("/").append(keyParams).append("(")
                    .append(n.updateWire).append(" payload)").append(NEWLINE);
            out.append("            returns ").append(n.wire).append(entity.hasWireTypes() ? INVALID_INPUT : "")
                    .append(WRITE_ERRORS).append(" {").append(NEWLINE);
            String value = "payload";
            if (entity.hasWireTypes()) {
                value = "value";
                out.append("        ").append(n.entity).append("Update|dsHttp:InvalidInput value = transformPayloadTo")
                        .append(n.entity).append("Update(payload);").append(NEWLINE);
                out.append("        if value is dsHttp:InvalidInput {").append(NEWLINE);
                out.append("            return value;").append(NEWLINE);
                out.append("        }").append(NEWLINE);
            }
            out.append("        ").append(n.entity).append("|persist:Error item;").append(NEWLINE);
            out.append("        if ").append(value).append(".length() == 0 {").append(NEWLINE);
            out.append("            item = dataserviceClient->/").append(n.resource).append(keyPath).append(";")
                    .append(NEWLINE);
            out.append("        } else {").append(NEWLINE);
            out.append("            item = dataserviceClient->/").append(n.resource).append(keyPath)
                    .append(".put(").append(value).append(");").append(NEWLINE);
            out.append("        }").append(NEWLINE);
            appendErrorCheck(out, "item", "toUpdateError", n, keyList, "        ");
            out.append("        return ").append(n.toWire("item")).append(";").append(NEWLINE);
            out.append("    }").append(NEWLINE);
        }
        if (entity.allows(DELETE)) {
            out.append(NEWLINE).append("    # Deletes ").append(n.article).append(" ").append(n.words)
                    .append(" and returns it.").append(NEWLINE);
            appendReturnDoc(out, "The deleted " + n.words);
            out.append("    resource function delete ").append(n.resource).append("/").append(keyParams)
                    .append("() returns ").append(n.wire).append(WRITE_ERRORS).append(" {").append(NEWLINE);
            out.append("        ").append(n.entity).append("|persist:Error item = dataserviceClient->/")
                    .append(n.resource).append(keyPath).append(".delete();").append(NEWLINE);
            appendErrorCheck(out, "item", "toDeleteError", n, keyList, "        ");
            out.append("        return ").append(n.toWire("item")).append(";").append(NEWLINE);
            out.append("    }").append(NEWLINE);
        }
    }

    private void appendCreate(StringBuilder out, ExposedEntity entity, Names n, String keyPath, String keyList) {
        String insert = n.entity + "Insert";
        out.append(NEWLINE).append("    # Creates one ").append(n.words).append(", or several from an array.")
                .append(NEWLINE);
        appendReturnDoc(out, "The created " + n.words + " or " + n.plural);
        out.append("    resource function post ").append(n.resource).append("(").append(n.insertWire).append("|")
                .append(n.insertWire).append("[] payload)").append(NEWLINE);
        out.append("            returns ").append(n.created).append("|").append(n.listCreated)
                .append(entity.hasWireTypes() ? INVALID_INPUT : "").append(CREATE_ERRORS).append(" {").append(NEWLINE);
        if (entity.hasWireTypes()) {
            out.append("        ").append(n.insertWire).append("[] entries = payload is ").append(n.insertWire)
                    .append("[] ? payload : [payload];").append(NEWLINE);
            out.append("        ").append(insert).append("[] values = [];").append(NEWLINE);
            out.append("        foreach ").append(n.insertWire).append(" entry in entries {").append(NEWLINE);
            out.append("            ").append(insert).append("|dsHttp:InvalidInput value = transformPayloadTo")
                    .append(insert).append("(entry);").append(NEWLINE);
            out.append("            if value is dsHttp:InvalidInput {").append(NEWLINE);
            out.append("                return value;").append(NEWLINE);
            out.append("            }").append(NEWLINE);
            out.append("            values.push(value);").append(NEWLINE);
            out.append("        }").append(NEWLINE);
        } else {
            out.append("        ").append(insert).append("[] values = payload is ").append(insert)
                    .append("[] ? payload : [payload];").append(NEWLINE);
        }
        out.append("        ").append(n.wire).append("[] items = [];").append(NEWLINE);
        out.append("        if values.length() > 0 {").append(NEWLINE);
        List<ServiceField> keys = entity.keys();
        String keyType = keys.size() == 1 ? keys.get(0).type() :
                "[" + keys.stream().map(ServiceField::type).collect(Collectors.joining(", ")) + "]";
        String keyBinding = keys.size() == 1 ? keys.get(0).name() :
                "[" + keys.stream().map(ServiceField::name).collect(Collectors.joining(", ")) + "]";
        out.append("            ").append(keyType).append("[]|persist:Error keys = dataserviceClient->/")
                .append(n.resource).append(".post(values);").append(NEWLINE);
        out.append("            if keys is persist:Error {").append(NEWLINE);
        out.append("                return dsHttp:toCreateError(keys, \"").append(n.displayName).append("\");")
                .append(NEWLINE);
        out.append("            }").append(NEWLINE);
        out.append("            foreach ").append(keyType).append(" ").append(keyBinding).append(" in keys {")
                .append(NEWLINE);
        out.append("                ").append(n.entity).append("|persist:Error item = dataserviceClient->/")
                .append(n.resource).append(keyPath).append(";").append(NEWLINE);
        appendErrorCheck(out, "item", "toCreateError", n, keyList, "                ");
        out.append("                items.push(").append(n.toWire("item")).append(");").append(NEWLINE);
        out.append("            }").append(NEWLINE);
        out.append("        }").append(NEWLINE);
        out.append("        if payload is ").append(n.insertWire).append("[] {").append(NEWLINE);
        out.append("            return <").append(n.listCreated).append(">{body: {items}};").append(NEWLINE);
        out.append("        }").append(NEWLINE);
        out.append("        ").append(n.wire).append(" created = items[0];").append(NEWLINE);
        out.append("        return <").append(n.created).append(">{").append(NEWLINE);
        out.append("            headers: {\"Location\": dsHttp:buildLocation(\"").append(collectionPath(n.resource))
                .append("\", ").append(keyList(entity, "created.")).append(")},").append(NEWLINE);
        out.append("            body: created").append(NEWLINE);
        out.append("        };").append(NEWLINE);
        out.append("    }").append(NEWLINE);
    }

    private static void appendReturnDoc(StringBuilder out, String result) {
        out.append("    #").append(NEWLINE);
        out.append("    # + return - ").append(result).append(", or the error response").append(NEWLINE);
    }

    private static void appendErrorCheck(StringBuilder out, String variable, String function, Names n,
                                         String keyList, String indent) {
        out.append(indent).append("if ").append(variable).append(" is persist:Error {").append(NEWLINE);
        out.append(indent).append("    return dsHttp:").append(function).append("(").append(variable).append(", \"")
                .append(n.displayName).append("\", ").append(keyList).append(");").append(NEWLINE);
        out.append(indent).append("}").append(NEWLINE);
    }

    private void appendTypes(StringBuilder out, ExposedEntity entity) {
        Names n = new Names(entity);
        if (entity.hasWireTypes()) {
            appendRecord(out, "The JSON form of `" + n.entity + "`.", n.wire, n.entity, entity.fields());
            if (entity.allows(CREATE)) {
                String doc = "The JSON form of `" + n.entity + "Insert`.";
                if (entity.insertFields().isPresent()) {
                    appendRecord(out, doc, n.insertWire, n.entity + "Insert", entity.insertFields().get());
                } else {
                    out.append(NEWLINE).append("# ").append(doc).append(NEWLINE);
                    out.append("public type ").append(n.insertWire).append(" ").append(n.wire).append(";")
                            .append(NEWLINE);
                }
            }
            if (entity.allows(UPDATE)) {
                appendRecord(out, "The JSON form of `" + n.entity + "Update`.", n.updateWire,
                        n.entity + "Update", entity.updateFields());
            }
        }
        if (entity.allows(READ) || entity.allows(CREATE)) {
            out.append(NEWLINE).append("# A list of ").append(n.plural).append(".").append(NEWLINE);
            out.append("#").append(NEWLINE);
            out.append("# + items - The ").append(n.plural).append(NEWLINE);
            out.append("public type ").append(n.list).append(" record {|").append(NEWLINE);
            out.append("    ").append(n.wire).append("[] items;").append(NEWLINE);
            out.append("|};").append(NEWLINE);
        }
        if (entity.allows(CREATE)) {
            appendStatusRecord(out, "The response to creating one " + n.words + ".", n.created, n.wire,
                    "The created " + n.words);
            appendStatusRecord(out, "The response to creating several " + n.plural + ".", n.listCreated, n.list,
                    "The created " + n.plural);
        }
    }

    private static void appendRecord(StringBuilder out, String doc, String name, String persistType,
                                     List<ServiceField> fields) {
        out.append(NEWLINE).append("# ").append(doc).append(NEWLINE);
        out.append("#").append(NEWLINE);
        for (ServiceField field : fields) {
            out.append("# + ").append(field.name()).append(" - ").append(fieldDoc(field, persistType))
                    .append(NEWLINE);
        }
        out.append("public type ").append(name).append(" record {|").append(NEWLINE);
        for (ServiceField field : fields) {
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

    private static String fieldDoc(ServiceField field, String persistType) {
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

    private void appendConverters(StringBuilder out, ExposedEntity entity) {
        if (!entity.hasWireTypes()) {
            return;
        }
        imports.add("import ballerina/dataservice;");
        if (entity.allows(CREATE) || entity.allows(UPDATE)) {
            imports.add("import ballerina/dataservice.http as dsHttp;");
        }
        Names n = new Names(entity);
        appendToWire(out, entity, n);
        if (entity.allows(CREATE) && entity.insertFields().isPresent()) {
            appendFromWire(out, n, n.insertWire, n.entity + "Insert", entity.insertFields().get());
        } else if (entity.allows(CREATE)) {
            appendFromWire(out, n, n.insertWire, n.entity + "Insert", entity.fields());
        }
        if (entity.allows(UPDATE)) {
            appendFromUpdateWire(out, n, entity.updateFields());
        }
    }

    private void appendToWire(StringBuilder out, ExposedEntity entity, Names n) {
        String param = parameterName("value", entity.fields());
        out.append(NEWLINE).append("# Converts ").append(n.article).append(" `").append(n.entity)
                .append("` to its JSON form.").append(NEWLINE);
        out.append("#").append(NEWLINE);
        out.append("# + ").append(param).append(" - The `").append(n.entity).append("` record").append(NEWLINE);
        out.append("# + return - The JSON form").append(NEWLINE);
        out.append("isolated function transform").append(n.entity).append("ToPayload(").append(n.entity).append(" ")
                .append(param).append(") returns ").append(n.wire).append(" => {").append(NEWLINE);
        StringBuilder members = new StringBuilder();
        for (ServiceField field : entity.fields()) {
            if (!members.isEmpty()) {
                members.append(",").append(NEWLINE);
            }
            String access = param + "." + field.name();
            members.append("    ").append(field.name()).append(": ");
            if (field.conversion().isEmpty()) {
                members.append(access);
            } else if (field.nilable()) {
                String local = local(field.name());
                useTypeImport(field);
                members.append("let ").append(field.persistType()).append(" ").append(local).append(" = ")
                        .append(access).append(" in ").append(local).append(" is () ? () : ")
                        .append(field.conversion().get().toStringFunction()).append("(").append(local).append(")");
            } else {
                members.append(field.conversion().get().toStringFunction()).append("(").append(access).append(")");
            }
        }
        out.append(members).append(NEWLINE).append("};").append(NEWLINE);
    }

    private void appendFromWire(StringBuilder out, Names n, String wireType, String persistType,
                                List<ServiceField> fields) {
        String param = parameterName("payload", fields);
        out.append(NEWLINE).append("# Converts the JSON form of ").append(articleFor(persistType)).append(" `")
                .append(persistType).append("` to the persist type.").append(NEWLINE);
        appendFromWireDoc(out, param);
        out.append("isolated function transformPayloadTo").append(persistType).append("(").append(wireType).append(" ")
                .append(param).append(") returns ").append(persistType).append("|dsHttp:InvalidInput {")
                .append(NEWLINE);
        StringBuilder members = new StringBuilder();
        for (ServiceField field : fields) {
            if (!members.isEmpty()) {
                members.append(", ");
            }
            String access = param + "." + field.name();
            if (field.conversion().isEmpty()) {
                members.append(field.name()).append(": ").append(access);
                continue;
            }
            members.append(field.name());
            ServiceField.Conversion conversion = field.conversion().get();
            useTypeImport(field);
            out.append("    ").append(field.persistType()).append("|dataservice:ConversionError ")
                    .append(field.name()).append(" = ");
            if (field.nilable()) {
                String text = unescape(field.name()) + "Text";
                out.append("let string? ").append(text).append(" = ").append(access).append(" in ").append(text)
                        .append(" is () ? () : ").append(conversion.fromStringFunction()).append("(").append(text)
                        .append(");").append(NEWLINE);
            } else {
                out.append(conversion.fromStringFunction()).append("(").append(access).append(");").append(NEWLINE);
            }
            appendInvalidInputCheck(out, n, field, "    ");
        }
        out.append("    return {").append(members).append("};").append(NEWLINE);
        out.append("}").append(NEWLINE);
    }

    private void appendFromUpdateWire(StringBuilder out, Names n, List<ServiceField> fields) {
        String persistType = n.entity + "Update";
        String param = parameterName("payload", fields);
        String result = parameterName("value", fields);
        out.append(NEWLINE).append("# Converts the JSON form of ").append(articleFor(persistType)).append(" `")
                .append(persistType).append("` to the persist type.").append(NEWLINE);
        appendFromWireDoc(out, param);
        out.append("isolated function transformPayloadTo").append(persistType).append("(").append(n.updateWire)
                .append(" ").append(param).append(") returns ").append(persistType).append("|dsHttp:InvalidInput {")
                .append(NEWLINE);
        out.append("    ").append(persistType).append(" ").append(result).append(" = {};").append(NEWLINE);
        for (ServiceField field : fields) {
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
                useTypeImport(field);
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

    private static void appendFromWireDoc(StringBuilder out, String param) {
        out.append("#").append(NEWLINE);
        out.append("# + ").append(param).append(" - The JSON form").append(NEWLINE);
        out.append("# + return - The persist value, or the response for a value that is not in its format")
                .append(NEWLINE);
    }

    private static void appendInvalidInputCheck(StringBuilder out, Names n, ServiceField field, String indent) {
        out.append(indent).append("if ").append(field.name()).append(" is dataservice:ConversionError {")
                .append(NEWLINE);
        out.append(indent).append("    return dsHttp:toInvalidInput(").append(field.name()).append(", \"")
                .append(n.displayName).append("\", \"").append(unescape(field.name())).append("\");")
                .append(NEWLINE);
        out.append(indent).append("}").append(NEWLINE);
    }

    private void useTypeImport(ServiceField field) {
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

    // An escaped field name, such as 'type, stays escaped as a variable name.
    private static String local(String fieldName) {
        return fieldName;
    }

    private static String unescape(String name) {
        return BalSyntaxUtils.stripEscapeCharacter(name.startsWith("'") ? name.substring(1) : name);
    }

    private static String articleFor(String name) {
        return "aeiouAEIOU".indexOf(unescape(name).charAt(0)) >= 0 ? "an" : "a";
    }

    private static String format(String source) throws BalException {
        try {
            return Formatter.format(source);
        } catch (FormatterException e) {
            throw new BalException("failed to format the data service source. " + e.getMessage());
        }
    }


    private static final class Names {
        private final String entity;
        private final String displayName;
        private final String resource;
        private final String wire;
        private final String insertWire;
        private final String updateWire;
        private final String list;
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
            this.hasWire = entity.hasWireTypes();
            this.wire = hasWire ? this.entity + "Payload" : this.entity;
            this.insertWire = this.entity + (hasWire ? "InsertPayload" : "Insert");
            this.updateWire = this.entity + (hasWire ? "UpdatePayload" : "Update");
            this.list = this.entity + "List";
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
