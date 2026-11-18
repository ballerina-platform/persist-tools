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
import io.ballerina.persist.models.Enum;
import io.ballerina.persist.models.Module;
import io.ballerina.persist.nodegenerator.syntax.constants.BalSyntaxConstants;
import io.ballerina.persist.utils.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static io.ballerina.persist.dataservice.DataServiceUtils.NEWLINE;
import static io.ballerina.persist.dataservice.DataServiceUtils.WORD_BOUNDARY;
import static io.ballerina.persist.dataservice.DataServiceUtils.unescape;
import static io.ballerina.persist.dataservice.Operation.CREATE;
import static io.ballerina.persist.dataservice.Operation.DELETE;
import static io.ballerina.persist.dataservice.Operation.READ;
import static io.ballerina.persist.dataservice.Operation.UPDATE;

/**
 * Generates the sources of a GraphQL data service, as the GraphQL data service proposal defines them.
 */
public final class GraphqlDataServiceGenerator implements DataServiceGenerator {

    /**
     * The schema types of `ballerina/dataservice.graphql`, which every generated service holds.
     */
    public static final Set<String> LIBRARY_TYPES = libraryTypes();

    private static final Set<String> GRAPHQL_RESERVED_KEY_NAMES = Set.of("input", "field");
    private static final Pattern ESCAPE = Pattern.compile("\\\\(.)");
    private static final Pattern GRAPHQL_NAME = Pattern.compile("[_A-Za-z][_0-9A-Za-z]*");
    private static final String DATASERVICE_GRAPHQL_IMPORT = "import ballerina/dataservice.graphql as dsGraphql;";
    private static final String GRAPHQL_IMPORT = "import ballerina/graphql;";
    private static final String DATASERVICE_IMPORT = "import ballerina/dataservice;";
    private static final String PERSIST_IMPORT = "import ballerina/persist;";
    private static final String RELATED_OPERATION = "get related";

    private final List<ExposedEntity> entities;
    private final Map<String, ExposedEntity> entitiesByName = new LinkedHashMap<>();
    private final String basePath;
    private final boolean introspection;
    private final boolean auth;
    private final String modelName;
    private final String datastore;
    private final boolean userOwned;

    /**
     * Creates a generator for a data service.
     *
     * @param entities  the exposed entities, in model order
     * @param options   the data service options
     * @param modelName the name of the persist model, as persist writes it in its file headers
     * @param datastore the datastore of the persist client, such as `mysql`
     * @param userOwned whether the files are written for the user to own, by `bal persist generate`
     */
    public GraphqlDataServiceGenerator(List<ExposedEntity> entities, DataServiceOptions options, String modelName,
                                       String datastore, boolean userOwned) {
        this.entities = entities;
        entities.forEach(entity -> entitiesByName.put(entity.name(), entity));
        this.basePath = options.basePath();
        this.introspection = options.introspection();
        this.auth = options.auth();
        this.modelName = modelName;
        this.datastore = datastore;
        this.userOwned = userOwned;
    }

    /**
     * Checks that the exposed entities can be rendered as a GraphQL schema.
     *
     * @param entities     the exposed entities, in model order
     * @param entityModule the persist model, whose entity and enum names become Ballerina types
     * @throws BalException with every problem found, one per line
     */
    public static void validate(List<ExposedEntity> entities, Module entityModule) throws BalException {
        List<String> errors = new ArrayList<>();
        if (entities.stream().noneMatch(entity -> entity.allows(READ))) {
            errors.add(DataServiceMessages.GRAPHQL_NO_READ);
        }
        Map<String, String> rootFields = new LinkedHashMap<>();
        Set<String> clashes = new LinkedHashSet<>();
        Set<String> generatedTypes = new LinkedHashSet<>(LIBRARY_TYPES);
        for (ExposedEntity entity : entities) {
            Names n = new Names(entity);
            String name = graphqlName(entity.name());
            if (!GRAPHQL_NAME.matcher(name).matches()) {
                errors.add(String.format(DataServiceMessages.GRAPHQL_INVALID_NAME, name, name));
            }
            for (ServiceField key : entity.keys()) {
                // Patch takes the key fields and `input`, which the core reserved names do not hold.
                if (GRAPHQL_RESERVED_KEY_NAMES.contains(unescape(key.name()))) {
                    errors.add(String.format(DataServiceMessages.KEY_NAME_RESERVED, unescape(key.name()), name));
                }
            }
            for (ServiceField field : entity.fields()) {
                if (field.has(FieldRule.INTERNAL)) {
                    continue;
                }
                String fieldName = graphqlName(field.name());
                if (fieldName.startsWith("_")) {
                    errors.add(String.format(DataServiceMessages.GRAPHQL_RESERVED_FIELD, fieldName, name));
                } else if (!GRAPHQL_NAME.matcher(fieldName).matches()) {
                    errors.add(String.format(DataServiceMessages.GRAPHQL_INVALID_NAME, fieldName, name));
                }
            }
            for (String rootField : n.rootFields(entity)) {
                String other = rootFields.putIfAbsent(rootField, name);
                // Two entities that clash on one root field usually clash on several; the first is reported.
                if (other != null && !other.equals(name) && clashes.add(other + "/" + name)) {
                    errors.add(String.format(DataServiceMessages.GRAPHQL_FIELD_CLASH, other, name, rootField));
                }
            }
            generatedTypes.addAll(n.types());
        }
        Set<String> modelTypes = new LinkedHashSet<>(entityModule.getEntityMap().keySet().stream()
                .map(DataServiceUtils::unescape).toList());
        for (Enum enumType : entityModule.getEnumMap().values()) {
            String enumName = unescape(enumType.getEnumName());
            modelTypes.add(enumName);
            generatedTypes.add(enumName + "Filter");
            generatedTypes.add(enumName + "NullableFilter");
        }
        for (String modelType : modelTypes) {
            if (generatedTypes.contains(modelType)) {
                errors.add(String.format(DataServiceMessages.GRAPHQL_TYPE_CLASH, modelType));
            }
        }
        if (!errors.isEmpty()) {
            throw new BalException(String.join(NEWLINE, errors));
        }
    }

    @Override
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
     * Generates `dataservice.bal`: the persist client, the listener and the service.
     *
     * @return the formatted source
     * @throws BalException if the source cannot be formatted
     */
    public String generateService() throws BalException {
        Set<String> imports = new TreeSet<>(List.of(GRAPHQL_IMPORT, PERSIST_IMPORT));
        StringBuilder body = new StringBuilder();
        body.append("# The persist client of the data service.").append(NEWLINE);
        body.append("final Client dataserviceClient = check new ();").append(NEWLINE).append(NEWLINE);
        body.append("# The listener of the data service.").append(NEWLINE);
        body.append("listener graphql:Listener dataserviceListener =").append(NEWLINE)
                .append("        new (dataserviceConfig.port, dataserviceConfig.listenerConfig);").append(NEWLINE)
                .append(NEWLINE);
        if (auth) {
            imports.add(DATASERVICE_GRAPHQL_IMPORT);
            appendRelationScopes(body);
        }
        body.append("@graphql:ServiceConfig {").append(NEWLINE);
        List<String> config = new ArrayList<>();
        // GraphiQL loads the schema through introspection, so it is served exactly when introspection is on.
        config.add(introspection ? "graphiql: {enabled: true}" : "introspection: false");
        config.add("cors: dataserviceConfig.serviceConfig.cors");
        if (auth || !toOneRelations().isEmpty()) {
            config.add("contextInit: dataserviceInitContext");
        }
        body.append("    ").append(String.join("," + NEWLINE + "    ", config)).append(NEWLINE);
        body.append("}").append(NEWLINE);
        body.append("service graphql:Service ").append(DataServiceUtils.servicePath(basePath))
                .append(" on dataserviceListener {").append(NEWLINE);
        for (ExposedEntity entity : entities) {
            if (entity.allows(READ)) {
                appendGetMany(body, entity, imports);
                appendGetOne(body, entity, imports);
            }
            if (entity.allows(CREATE)) {
                appendCreate(body, entity, imports);
            }
            if (entity.allows(UPDATE)) {
                appendUpdate(body, entity, imports);
            }
            if (entity.allows(DELETE)) {
                appendDelete(body, entity, imports);
            }
        }
        body.append("}").append(NEWLINE);
        for (ExposedEntity entity : entities) {
            if (entity.allows(CREATE)) {
                appendCreateFunction(body, entity, imports);
            }
        }
        appendContext(body, imports);
        return DataServiceUtils.format(modelHeader() + String.join(NEWLINE, imports) + NEWLINE + NEWLINE + body);
    }

    /**
     * Generates `dataservice_types.bal`: per entity in model order, the object type, the list envelope, the filter and
     * sort inputs and the entity description.
     *
     * @return the formatted source, or empty when no exposed entity needs a type
     * @throws BalException if the source cannot be formatted
     */
    public Optional<String> generateTypes() throws BalException {
        Set<String> imports = new TreeSet<>();
        StringBuilder body = new StringBuilder();
        for (ExposedEntity entity : entities) {
            boolean returned = entity.allows(READ) || entity.allows(CREATE);
            if (returned) {
                appendObject(body, entity, imports);
                appendList(body, entity);
            }
            if (entity.allows(READ)) {
                appendRelated(body, entity, imports);
                appendFilter(body, entity);
                appendSort(body, entity);
                imports.add(DATASERVICE_GRAPHQL_IMPORT);
            }
            if (entity.allows(CREATE)) {
                appendInsertInput(body, entity);
            }
            if (entity.allows(UPDATE)) {
                appendUpdateInput(body, entity);
            }
            if (returned) {
                imports.add(DATASERVICE_IMPORT);
                EntityDescription.append(body, entity, entitiesByName, datastore);
            }
            if (entity.allows(CREATE)) {
                appendInsertConverter(body, entity, imports);
            }
            if (entity.allows(UPDATE)) {
                appendUpdateConverter(body, entity, imports);
            }
        }
        appendEnumFilters(body);
        if (body.isEmpty()) {
            return Optional.empty();
        }
        String importLines = imports.isEmpty() ? "" : String.join(NEWLINE, imports) + NEWLINE + NEWLINE;
        return Optional.of(DataServiceUtils.format(modelHeader() + importLines + body.toString().stripLeading()));
    }

    /**
     * Generates `dataservice_config.bal`.
     *
     * @return the formatted source
     * @throws BalException if the source cannot be formatted
     */
    public String generateConfig() throws BalException {
        return DataServiceUtils.format(DataServiceUtils.header(BalSyntaxConstants.AUTO_GENERATED_COMMENT, userOwned) +
                DATASERVICE_IMPORT + NEWLINE + DATASERVICE_GRAPHQL_IMPORT + NEWLINE +
                NEWLINE +
                "# The runtime settings of the data service, set in the `[dataserviceConfig]` table of Config.toml." +
                NEWLINE +
                (auth ? "configurable dsGraphql:AuthConfig dataserviceConfig = ?;" :
                        "configurable dsGraphql:Config dataserviceConfig = {};") + NEWLINE +
                NEWLINE +
                "# The page sizes of the data service, checked at startup." + NEWLINE +
                "final dataservice:PageConfig dataservicePageConfig =" + NEWLINE +
                "        check dataservice:toPageConfig(dataserviceConfig.pageSize, dataserviceConfig.maxPageSize);" +
                NEWLINE + (auth ? NEWLINE +
                "# Authenticates each request with the configured providers; the program does not start without one." +
                NEWLINE + "final dsGraphql:Authenticator dataserviceAuthenticator = check new (" +
                "dataserviceConfig.serviceConfig.auth);" + NEWLINE : ""));
    }

    private String modelHeader() {
        return DataServiceUtils.header(String.format(BalSyntaxConstants.AUTO_GENERATED_COMMENT_WITH_REASON,
                modelName), userOwned);
    }

    private void appendGetMany(StringBuilder out, ExposedEntity entity, Set<String> imports) {
        imports.add(DATASERVICE_IMPORT);
        imports.add(DATASERVICE_GRAPHQL_IMPORT);
        Names n = new Names(entity);
        appendDoc(out, "    ", entity, "Gets a page of " + n.plural + ".", List.of(
                "filter - The conditions that the " + n.plural + " match",
                "sort - The order of the " + n.plural + "; each entry names one field",
                "size - The number of " + n.plural + " in the page",
                "offset - The number of " + n.plural + " to skip"), "A page of " + n.plural + ", or the error");
        out.append("    resource function get ").append(identifier(n.getMany)).append("(")
                .append(authParams(entity, true)).append(n.filter)
                .append("? filter = (), ").append(n.sort).append("[]? sort = (),").append(NEWLINE)
                .append("            int? size = (), int? offset = ()) returns ").append(n.list).append("?|error {")
                .append(NEWLINE);
        appendAuthChecks(out, entity, READ, true);
        out.append("        dataservice:Query query = check dsGraphql:toQuery(").append(n.description)
                .append(", dataservicePageConfig,").append(NEWLINE)
                .append("                filter, sort, size, offset);").append(NEWLINE);
        out.append("        stream<").append(n.entity).append(", persist:Error?> rows = dataserviceClient->/")
                .append(n.resource).append("(").append(n.entity).append(",").append(NEWLINE);
        out.append("            whereClause = query.whereClause, orderByClause = query.orderByClause,").append(NEWLINE);
        out.append("            limitClause = query.limitClause, offsetClause = query.offsetClause").append(NEWLINE);
        out.append("        );").append(NEWLINE);
        out.append("        ").append(n.entity).append("[]|persist:Error items = from ").append(n.entity)
                .append(" item in rows").append(NEWLINE);
        out.append("            select item;").append(NEWLINE);
        out.append("        if items is persist:Error {").append(NEWLINE);
        out.append("            return dsGraphql:toListError(items, \"").append(n.displayName).append("\");")
                .append(NEWLINE);
        out.append("        }").append(NEWLINE);
        out.append("        boolean hasMore = dataservice:trimToPage(items, query);").append(NEWLINE);
        out.append("        return {items: from ").append(n.entity).append(" item in items select new ")
                .append(n.object).append("(item), hasMore};").append(NEWLINE);
        out.append("    }").append(NEWLINE);
    }

    private void appendGetOne(StringBuilder out, ExposedEntity entity, Set<String> imports) {
        imports.add(DATASERVICE_GRAPHQL_IMPORT);
        Names n = new Names(entity);
        appendDoc(out, "    ", entity, "Gets " + n.article + " " + n.words + " by its key.", keyDocs(entity, n),
                "The " + n.words + ", or the error");
        out.append("    resource function get ").append(identifier(n.getOne)).append("(")
                .append(authParams(entity, true)).append(keyParams(entity))
                .append(") returns ").append(n.object).append("?|error {").append(NEWLINE);
        appendAuthChecks(out, entity, READ, true);
        appendKeyCheck(out, entity, n, imports);
        out.append("        ").append(n.entity).append("|persist:Error item = dataserviceClient->/").append(n.resource)
                .append(keyPath(entity)).append(";").append(NEWLINE);
        out.append("        if item is persist:Error {").append(NEWLINE);
        out.append("            return dsGraphql:toGetError(item, \"").append(n.displayName).append("\", ")
                .append(keyList(entity)).append(");").append(NEWLINE);
        out.append("        }").append(NEWLINE);
        out.append("        return new (item);").append(NEWLINE);
        out.append("    }").append(NEWLINE);
    }

    private void appendCreate(StringBuilder out, ExposedEntity entity, Set<String> imports) {
        Names n = new Names(entity);
        appendDoc(out, "    ", entity, "Creates " + n.article + " " + n.words + ".",
                List.of("input - The " + n.words + " to create"), "The created " + n.words + ", or the error");
        if (auth) {
            out.append("    remote function ").append(n.createOne).append("(").append(authParams(entity, true))
                    .append(n.insertInput).append(" input)").append(NEWLINE).append("            returns ")
                    .append(n.object).append("?|error {").append(NEWLINE);
            appendAuthChecks(out, entity, CREATE, true);
            out.append("        return (check ").append(n.createFunction).append("([input]))[0];").append(NEWLINE);
            out.append("    }").append(NEWLINE);
        } else {
            out.append("    remote function ").append(n.createOne).append("(").append(n.insertInput)
                    .append(" input) returns ").append(n.object).append("?|error =>").append(NEWLINE)
                    .append("        (check ").append(n.createFunction).append("([input]))[0];").append(NEWLINE);
        }
        appendDoc(out, "    ", entity, "Creates " + n.plural + ".", List.of("input - The " + n.plural + " to create"),
                "The created " + n.plural + ", or the error");
        if (auth) {
            out.append("    remote function ").append(n.createMany).append("(").append(authParams(entity, true))
                    .append(n.insertInput).append("[] input)").append(NEWLINE).append("            returns ")
                    .append(n.list).append("?|error {").append(NEWLINE);
            appendAuthChecks(out, entity, CREATE, true);
            out.append("        return {items: check ").append(n.createFunction).append("(input), hasMore: false};")
                    .append(NEWLINE);
            out.append("    }").append(NEWLINE);
        } else {
            out.append("    remote function ").append(n.createMany).append("(").append(n.insertInput)
                    .append("[] input) returns ").append(n.list).append("?|error =>").append(NEWLINE)
                    .append("        {items: check ").append(n.createFunction).append("(input), hasMore: false};")
                    .append(NEWLINE);
        }
    }

    // Create one and create many share this function: in one transaction it posts the records and reads them back
    // with one query on their keys, in the order of the input, so a batch creates every record or none.
    private void appendCreateFunction(StringBuilder out, ExposedEntity entity, Set<String> imports) {
        imports.add(DATASERVICE_IMPORT);
        imports.add(DATASERVICE_GRAPHQL_IMPORT);
        Names n = new Names(entity);
        String insert = n.entity + "Insert";
        List<ServiceField> keys = entity.keys();
        String keyType = keys.size() == 1 ? keys.get(0).type() :
                "[" + keys.stream().map(ServiceField::type).collect(Collectors.joining(", ")) + "]";
        out.append(NEWLINE).append("isolated function ").append(n.createFunction).append("(").append(n.insertInput)
                .append("[] input) returns ").append(n.object).append("[]|error {").append(NEWLINE);
        out.append("    dataservice:DataserviceError? tooMany = dataservice:checkCreateSize(input.length(), ")
                .append("dataservicePageConfig);").append(NEWLINE);
        out.append("    if tooMany is dataservice:DataserviceError {").append(NEWLINE);
        out.append("        return dsGraphql:toError(tooMany);").append(NEWLINE);
        out.append("    }").append(NEWLINE);
        out.append("    if input.length() == 0 {").append(NEWLINE);
        out.append("        return [];").append(NEWLINE);
        out.append("    }").append(NEWLINE);
        if (insertConverts(entity)) {
            out.append("    ").append(insert).append("[] values = [];").append(NEWLINE);
            out.append("    foreach ").append(n.insertInput).append(" entry in input {").append(NEWLINE);
            out.append("        values.push(check transformInputTo").append(insert).append("(entry));").append(NEWLINE);
            out.append("    }").append(NEWLINE);
        } else {
            out.append("    ").append(insert).append("[] values = from ").append(n.insertInput)
                    .append(" entry in input").append(NEWLINE);
            out.append("        select transformInputTo").append(insert).append("(entry);").append(NEWLINE);
        }
        out.append("    do {").append(NEWLINE);
        out.append("        transaction {").append(NEWLINE);
        out.append("            ").append(keyType).append("[] keys = check dataserviceClient->/").append(n.resource)
                .append(".post(values);").append(NEWLINE);
        out.append("            dataservice:KeyValue[][] keyValues = ")
                .append(keys.size() == 1 ? "from " + keyType + " key in keys select [key]" : "keys").append(";")
                .append(NEWLINE);
        out.append("            dataservice:Query query = dataservice:toKeysQuery(").append(n.description)
                .append(", keyValues);").append(NEWLINE);
        out.append("            stream<").append(n.entity).append(", persist:Error?> stored = dataserviceClient->/")
                .append(n.resource).append("(").append(n.entity).append(",").append(NEWLINE);
        out.append("                whereClause = query.whereClause, orderByClause = query.orderByClause")
                .append(NEWLINE);
        out.append("            );").append(NEWLINE);
        out.append("            ").append(n.entity).append("[] rows = check from ").append(n.entity)
                .append(" item in stored").append(NEWLINE);
        out.append("                select item;").append(NEWLINE);
        out.append("            check dataservice:orderByKeys(rows, ").append(n.description).append(", keyValues);")
                .append(NEWLINE);
        out.append("            check commit;").append(NEWLINE);
        out.append("            return from ").append(n.entity).append(" item in rows select new ").append(n.object)
                .append("(item);").append(NEWLINE);
        out.append("        }").append(NEWLINE);
        out.append("    } on fail error err {").append(NEWLINE);
        out.append("        return err is dsGraphql:Error ? err : dsGraphql:toCreateError(err, \"")
                .append(n.displayName).append("\");").append(NEWLINE);
        out.append("    }").append(NEWLINE);
        out.append("}").append(NEWLINE);
    }

    // Without `read`, patch returns `true`, since GraphQL has no empty result; an empty patch then changes nothing and
    // reads nothing.
    private void appendUpdate(StringBuilder out, ExposedEntity entity, Set<String> imports) {
        imports.add(DATASERVICE_GRAPHQL_IMPORT);
        Names n = new Names(entity);
        boolean read = entity.allows(READ);
        List<String> params = new ArrayList<>(keyDocs(entity, n));
        params.add("input - The fields to change");
        appendDoc(out, "    ", entity, "Updates " + n.article + " " + n.words + " by its key.", params,
                read ? "The updated " + n.words + ", `()` when it cannot be returned, or the error" :
                        "`true` when the " + n.words + " is updated, or the error");
        out.append("    remote function ").append(n.update).append("(").append(authParams(entity, read))
                .append(keyParams(entity)).append(", ")
                .append(n.updateInput).append(" input)").append(NEWLINE).append("            returns ")
                .append(read ? n.object : "boolean").append("?|error {").append(NEWLINE);
        appendAuthChecks(out, entity, UPDATE, read);
        boolean readChecked = auth && read;
        if (readChecked) {
            out.append("        boolean readable = dsGraphql:hasScope(ctx, ").append(stringList(entity.scopes(READ)))
                    .append(");").append(NEWLINE);
        }
        appendKeyCheck(out, entity, n, imports);
        String update = n.entity + "Update";
        out.append("        ").append(update).append(" value = check transformInputTo").append(update)
                .append("(input);").append(NEWLINE);
        if (read) {
            out.append("        ").append(n.entity).append("|persist:Error item;").append(NEWLINE);
            out.append("        if value.length() == 0 {").append(NEWLINE);
            if (readChecked) {
                // An empty patch reads the record, which a caller who may not read it does not get.
                out.append("            if !readable {").append(NEWLINE);
                out.append("                return ();").append(NEWLINE);
                out.append("            }").append(NEWLINE);
            }
            out.append("            item = dataserviceClient->/").append(n.resource).append(keyPath(entity)).append(";")
                    .append(NEWLINE);
            out.append("        } else {").append(NEWLINE);
            out.append("            item = dataserviceClient->/").append(n.resource).append(keyPath(entity))
                    .append(".put(value);").append(NEWLINE);
            out.append("        }").append(NEWLINE);
            out.append("        if item is persist:Error {").append(NEWLINE);
            out.append("            return dsGraphql:toUpdateError(item, \"").append(n.displayName).append("\", ")
                    .append(keyList(entity)).append(");").append(NEWLINE);
            out.append("        }").append(NEWLINE);
            out.append("        return ").append(readChecked ? "readable ? new (item) : ()" : "new (item)").append(";")
                    .append(NEWLINE);
        } else {
            out.append("        if value.length() == 0 {").append(NEWLINE);
            out.append("            return true;").append(NEWLINE);
            out.append("        }").append(NEWLINE);
            out.append("        ").append(n.entity).append("|persist:Error item = dataserviceClient->/")
                    .append(n.resource).append(keyPath(entity)).append(".put(value);").append(NEWLINE);
            out.append("        if item is persist:Error {").append(NEWLINE);
            out.append("            check dsGraphql:toUpdateError(item, \"").append(n.displayName).append("\", ")
                    .append(keyList(entity)).append(");").append(NEWLINE);
            out.append("        }").append(NEWLINE);
            out.append("        return true;").append(NEWLINE);
        }
        out.append("    }").append(NEWLINE);
    }

    private void appendDelete(StringBuilder out, ExposedEntity entity, Set<String> imports) {
        imports.add(DATASERVICE_GRAPHQL_IMPORT);
        Names n = new Names(entity);
        boolean read = entity.allows(READ);
        appendDoc(out, "    ", entity, "Deletes " + n.article + " " + n.words + " by its key.", keyDocs(entity, n),
                read ? "The deleted " + n.words + ", or the error" :
                        "`true` when the " + n.words + " is deleted, or the error");
        out.append("    remote function ").append(n.delete).append("(").append(authParams(entity, read))
                .append(keyParams(entity)).append(") returns ")
                .append(read ? n.object : "boolean").append("?|error {").append(NEWLINE);
        appendAuthChecks(out, entity, DELETE, read);
        appendKeyCheck(out, entity, n, imports);
        out.append("        ").append(n.entity).append("|persist:Error item = dataserviceClient->/").append(n.resource)
                .append(keyPath(entity)).append(".delete();").append(NEWLINE);
        out.append("        if item is persist:Error {").append(NEWLINE);
        out.append("            return dsGraphql:toDeleteError(item, \"").append(n.displayName).append("\", ")
                .append(keyList(entity)).append(");").append(NEWLINE);
        out.append("        }").append(NEWLINE);
        out.append("        return ").append(!read ? "true" : auth ? "dsGraphql:hasScope(ctx, " +
                stringList(entity.scopes(READ)) + ") ? new (item) : ()" : "new (item)").append(";").append(NEWLINE);
        out.append("    }").append(NEWLINE);
    }

    // With auth, a root field takes the request context, which holds the caller, and the field itself when its result
    // has relation fields, whose selection the relation scopes are checked against.
    private String authParams(ExposedEntity entity, boolean returnsRecords) {
        if (!auth) {
            return "";
        }
        return "graphql:Context ctx, " + (returnsRecords && !entity.relations().isEmpty() ? "graphql:Field 'field, " :
                "");
    }

    // Each root field checks the scopes of its action first, and then that the caller may read every relation its
    // selection reads; a mutation's result needs the entity's own `read` scopes for that too.
    private void appendAuthChecks(StringBuilder out, ExposedEntity entity, Operation action, boolean returnsRecords) {
        if (!auth) {
            return;
        }
        out.append("        check dsGraphql:checkScopes(ctx, ").append(stringList(entity.scopes(action))).append(");")
                .append(NEWLINE);
        if (!returnsRecords || entity.relations().isEmpty()) {
            return;
        }
        out.append("        check dsGraphql:checkRelationScopes(ctx, 'field, \"").append(unescape(entity.name()))
                .append("\", ").append(relationsName(entity))
                .append(action == READ ? "" : ", " + stringList(entity.scopes(READ))).append(");").append(NEWLINE);
    }

    private static String relationsName(ExposedEntity entity) {
        return "dataservice" + unescape(entity.name()) + "Relations";
    }

    private static String stringList(List<String> names) {
        return EntityDescription.stringList(names);
    }

    // The related entity and its `read` scopes, by relation field, which a caller needs to read each relation.
    private void appendRelationScopes(StringBuilder out) {
        for (ExposedEntity entity : entities) {
            if (entity.relations().isEmpty()) {
                continue;
            }
            out.append("# The `read` scopes that a caller needs to read each relation of `").append(entity.name())
                    .append("`.").append(NEWLINE);
            out.append("final readonly & map<dsGraphql:RelationScopes> ").append(relationsName(entity)).append(" = {")
                    .append(NEWLINE);
            List<String> members = new ArrayList<>();
            for (ExposedRelation relation : entity.relations()) {
                ExposedEntity target = target(relation);
                members.add("    \"" + unescape(relation.field()) + "\": {target: \"" + unescape(target.name()) +
                        "\", scopes: " + stringList(target.scopes(READ)) + "}");
            }
            out.append(String.join("," + NEWLINE, members)).append(NEWLINE).append("};").append(NEWLINE)
                    .append(NEWLINE);
        }
    }

    // The fields a create accepts: neither a `ReadOnly` field, which the database or the annotation's value sets, nor
    // an `Internal` one. A document that sets one is rejected by ballerina/graphql.
    private static List<ServiceField> insertInputFields(ExposedEntity entity) {
        return entity.insertFields().orElse(entity.fields()).stream()
                .filter(field -> !field.has(FieldRule.READ_ONLY) && !field.has(FieldRule.INTERNAL)).toList();
    }

    // The fields a patch can change: no key, `ReadOnly`, `Immutable` or `Internal` field.
    private static List<ServiceField> updateInputFields(ExposedEntity entity) {
        return entity.updateFields().stream().filter(field -> field.rule().isEmpty()).toList();
    }

    // An insert input fails to convert when a value does not parse or a float is not finite.
    private static boolean insertConverts(ExposedEntity entity) {
        return insertInputFields(entity).stream().anyMatch(field -> field.conversion().isPresent() || isFloat(field));
    }

    // An input field that a client may leave out is nullable, since GraphQL lets a client send `null` for any field it
    // may leave out; `T x?` cannot take that `null` (ballerina-library#9740).
    private static void appendInsertInput(StringBuilder out, ExposedEntity entity) {
        Names n = new Names(entity);
        out.append(NEWLINE).append("# The fields to create ").append(n.article).append(" ").append(n.words)
                .append(" with.").append(NEWLINE);
        out.append("public type ").append(n.insertInput).append(" record {|").append(NEWLINE);
        for (ServiceField field : insertInputFields(entity)) {
            out.append("    # ").append(fieldDoc(field, entity, n)).append(NEWLINE);
            String type = field.conversion().isPresent() ? "string" : field.type();
            out.append("    ").append(type).append(field.nilable() || field.optional() ? "?" : "").append(" ")
                    .append(field.name()).append(";").append(NEWLINE);
        }
        out.append("|};").append(NEWLINE);
    }

    private static void appendUpdateInput(StringBuilder out, ExposedEntity entity) {
        Names n = new Names(entity);
        out.append(NEWLINE).append("# The fields to change in ").append(n.article).append(" ").append(n.words)
                .append("; a field left out is not changed.").append(NEWLINE);
        out.append("public type ").append(n.updateInput).append(" record {|").append(NEWLINE);
        for (ServiceField field : updateInputFields(entity)) {
            out.append("    # ").append(fieldDoc(field, entity, n)).append(NEWLINE);
            String type = field.conversion().isPresent() ? "string" : field.type();
            out.append("    ").append(type).append("? ").append(field.name()).append("?;").append(NEWLINE);
        }
        out.append("|};").append(NEWLINE);
    }

    // A `ReadOnly` or `Internal` field takes the value of its annotation, or `()`, as in the HTTP service.
    private void appendInsertConverter(StringBuilder out, ExposedEntity entity, Set<String> imports) {
        Names n = new Names(entity);
        String insert = n.entity + "Insert";
        List<ServiceField> fields = entity.insertFields().orElse(entity.fields());
        String param = parameterName("value", fields);
        boolean converts = insertConverts(entity);
        out.append(NEWLINE).append("isolated function transformInputTo").append(insert).append("(")
                .append(n.insertInput).append(" ").append(param).append(") returns ").append(insert)
                .append(converts ? "|dsGraphql:Error {" : " {").append(NEWLINE);
        List<String> members = new ArrayList<>();
        for (ServiceField field : fields) {
            String access = param + "." + field.name();
            if (field.has(FieldRule.READ_ONLY) || field.has(FieldRule.INTERNAL)) {
                members.add(field.name() + ": " + field.value().orElse("()"));
                continue;
            }
            if (field.conversion().isEmpty()) {
                if (isFloat(field)) {
                    appendFiniteCheck(out, n, field, field.nilable() ? access + " ?: 0.0" : access, "    ");
                }
                members.add(field.name() + ": " + access);
                continue;
            }
            members.add(field.name());
            appendFromString(out, n, field, access, "    ", imports);
        }
        out.append("    return {").append(String.join(", ", members)).append("};").append(NEWLINE);
        out.append("}").append(NEWLINE);
    }

    // Declares `field` as the persist value of the string at `access`, returning the INVALID_INPUT error when it does
    // not parse; a nil string stays nil.
    private static void appendFromString(StringBuilder out, Names n, ServiceField field, String access, String indent,
                                         Set<String> imports) {
        imports.add(DATASERVICE_IMPORT);
        imports.add(DATASERVICE_GRAPHQL_IMPORT);
        if (field.type().startsWith("time:")) {
            imports.add("import ballerina/time;");
        }
        ServiceField.Conversion conversion = field.conversion().orElseThrow();
        out.append(indent).append(field.persistType()).append("|dataservice:ConversionError ").append(field.name())
                .append(" = ");
        if (field.nilable()) {
            String text = unescape(field.name()) + "Text";
            out.append("();").append(NEWLINE);
            out.append(indent).append("string? ").append(text).append(" = ").append(access).append(";").append(NEWLINE);
            out.append(indent).append("if ").append(text).append(" !is () {").append(NEWLINE);
            out.append(indent).append("    ").append(field.name()).append(" = ").append(conversion.fromStringFunction())
                    .append("(").append(text).append(");").append(NEWLINE);
            out.append(indent).append("}").append(NEWLINE);
        } else {
            out.append(conversion.fromStringFunction()).append("(").append(access).append(");").append(NEWLINE);
        }
        out.append(indent).append("if ").append(field.name()).append(" is dataservice:ConversionError {")
                .append(NEWLINE);
        out.append(indent).append("    return dsGraphql:toInvalidInput(").append(field.name()).append(", \"")
                .append(n.displayName).append("\", \"").append(unescape(field.name())).append("\");").append(NEWLINE);
        out.append(indent).append("}").append(NEWLINE);
    }

    // A field left out is absent from the persist update, so it is not changed; `null` clears a nullable field and is
    // rejected on one that cannot hold it.
    private void appendUpdateConverter(StringBuilder out, ExposedEntity entity, Set<String> imports) {
        imports.add(DATASERVICE_GRAPHQL_IMPORT);
        Names n = new Names(entity);
        String update = n.entity + "Update";
        List<ServiceField> fields = updateInputFields(entity);
        String param = parameterName("value", fields);
        String result = parameterName("update", fields);
        out.append(NEWLINE).append("isolated function transformInputTo").append(update).append("(")
                .append(n.updateInput).append(" ").append(param).append(") returns ").append(update)
                .append("|dsGraphql:Error {").append(NEWLINE);
        out.append("    ").append(update).append(" ").append(result).append(" = {};").append(NEWLINE);
        for (ServiceField field : fields) {
            String name = field.name();
            String key = "\"" + unescape(name) + "\"";
            out.append("    if ").append(param).append(".hasKey(").append(key).append(") {").append(NEWLINE);
            if (field.conversion().isEmpty() && field.nilable() && !isFloat(field)) {
                out.append("        ").append(result).append(".").append(name).append(" = ").append(param).append("?.")
                        .append(name).append(";").append(NEWLINE);
            } else if (field.conversion().isEmpty() && field.nilable()) {
                out.append("        float? ").append(name).append(" = ").append(param).append("?.").append(name)
                        .append(";").append(NEWLINE);
                appendFiniteCheck(out, n, field, name + " ?: 0.0", "        ");
                out.append("        ").append(result).append(".").append(name).append(" = ").append(name).append(";")
                        .append(NEWLINE);
            } else if (field.conversion().isEmpty()) {
                out.append("        ").append(field.type()).append("? ").append(name).append(" = ").append(param)
                        .append("?.").append(name).append(";").append(NEWLINE);
                appendNullCheck(out, n, field, name, "        ");
                if (isFloat(field)) {
                    appendFiniteCheck(out, n, field, name, "        ");
                }
                out.append("        ").append(result).append(".").append(name).append(" = ").append(name).append(";")
                        .append(NEWLINE);
            } else {
                String text = unescape(name) + "Text";
                out.append("        string? ").append(text).append(" = ").append(param).append("?.").append(name)
                        .append(";").append(NEWLINE);
                if (field.nilable()) {
                    out.append("        if ").append(text).append(" is () {").append(NEWLINE);
                    out.append("            ").append(result).append(".").append(name).append(" = ();").append(NEWLINE);
                    out.append("        } else {").append(NEWLINE);
                    appendParse(out, n, field, text, "            ", imports);
                    out.append("            ").append(result).append(".").append(name).append(" = ").append(name)
                            .append(";").append(NEWLINE);
                    out.append("        }").append(NEWLINE);
                } else {
                    appendNullCheck(out, n, field, text, "        ");
                    appendParse(out, n, field, text, "        ", imports);
                    out.append("        ").append(result).append(".").append(name).append(" = ").append(name)
                            .append(";").append(NEWLINE);
                }
            }
            out.append("    }").append(NEWLINE);
        }
        out.append("    return ").append(result).append(";").append(NEWLINE);
        out.append("}").append(NEWLINE);
    }

    private static void appendFiniteCheck(StringBuilder out, Names n, ServiceField field, String value,
                                          String indent) {
        out.append(indent).append("check dsGraphql:checkFinite(").append(value).append(", \"").append(n.displayName)
                .append("\", \"").append(unescape(field.name())).append("\");").append(NEWLINE);
    }

    // ballerina/graphql takes a float literal beyond the largest float, such as `1e400`, as `Infinity`, which no
    // record has as a key.
    private static void appendKeyCheck(StringBuilder out, ExposedEntity entity, Names n, Set<String> imports) {
        if (entity.keys().stream().noneMatch(GraphqlDataServiceGenerator::isFloat)) {
            return;
        }
        imports.add(DATASERVICE_IMPORT);
        out.append("        dataservice:DataserviceError? invalidKey = dataservice:checkFiniteKey(\"")
                .append(n.displayName).append("\", ").append(keyList(entity)).append(");").append(NEWLINE);
        out.append("        if invalidKey is dataservice:DataserviceError {").append(NEWLINE);
        out.append("            return dsGraphql:toError(invalidKey);").append(NEWLINE);
        out.append("        }").append(NEWLINE);
    }

    private static void appendNullCheck(StringBuilder out, Names n, ServiceField field, String variable,
                                        String indent) {
        out.append(indent).append("if ").append(variable).append(" is () {").append(NEWLINE);
        out.append(indent).append("    return dsGraphql:toNullError(\"").append(n.displayName).append("\", \"")
                .append(unescape(field.name())).append("\");").append(NEWLINE);
        out.append(indent).append("}").append(NEWLINE);
    }

    private static void appendParse(StringBuilder out, Names n, ServiceField field, String text, String indent,
                                    Set<String> imports) {
        imports.add(DATASERVICE_IMPORT);
        if (field.type().startsWith("time:")) {
            imports.add("import ballerina/time;");
        }
        out.append(indent).append(field.type()).append("|dataservice:ConversionError ").append(field.name())
                .append(" = ").append(field.conversion().orElseThrow().fromStringFunction()).append("(").append(text)
                .append(");").append(NEWLINE);
        out.append(indent).append("if ").append(field.name()).append(" is dataservice:ConversionError {")
                .append(NEWLINE);
        out.append(indent).append("    return dsGraphql:toInvalidInput(").append(field.name()).append(", \"")
                .append(n.displayName).append("\", \"").append(unescape(field.name())).append("\");").append(NEWLINE);
        out.append(indent).append("}").append(NEWLINE);
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

    private static List<String> keyDocs(ExposedEntity entity, Names n) {
        return entity.keys().stream().map(key -> key.name() + " - The " + unescape(key.name()) + " of the " +
                n.words).toList();
    }

    private static String keyParams(ExposedEntity entity) {
        return entity.keys().stream().map(key -> key.type() + " " + key.name()).collect(Collectors.joining(", "));
    }

    private static String keyPath(ExposedEntity entity) {
        return entity.keys().stream().map(key -> "/[" + key.name() + "]").collect(Collectors.joining());
    }

    private static String keyList(ExposedEntity entity) {
        return "[" + entity.keys().stream().map(ServiceField::name).collect(Collectors.joining(", ")) + "]";
    }

    // Input fields are documented in place: ballerina/graphql reads an input field's description from its own doc
    // comment, not from the record's `+ field` lines.
    private static void appendFilter(StringBuilder out, ExposedEntity entity) {
        Names n = new Names(entity);
        out.append(NEWLINE).append("# The conditions that ").append(n.plural)
                .append(" match; every condition set must hold.").append(NEWLINE);
        out.append("public type ").append(n.filter).append(" record {|").append(NEWLINE);
        for (ServiceField field : filterable(entity)) {
            ExposedEntity.Column column = column(entity, field);
            String type = "ENUM".equals(column.type()) ? enumFilter(field.type(), column.nullable()) :
                    "dsGraphql:" + filterType(column);
            out.append("    # The conditions on `").append(unescape(field.name())).append("`").append(NEWLINE);
            out.append("    ").append(type).append("? ").append(field.name()).append(";").append(NEWLINE);
        }
        out.append("|};").append(NEWLINE);
    }

    private static void appendSort(StringBuilder out, ExposedEntity entity) {
        Names n = new Names(entity);
        out.append(NEWLINE).append("# One entry of the order of ").append(n.plural).append("; set exactly one field.")
                .append(NEWLINE);
        out.append("public type ").append(n.sort).append(" record {|").append(NEWLINE);
        for (ServiceField field : filterable(entity)) {
            out.append("    # Sorts by `").append(unescape(field.name())).append("`").append(NEWLINE);
            out.append("    dsGraphql:SortDirection? ").append(field.name()).append(";").append(NEWLINE);
        }
        out.append("|};").append(NEWLINE);
    }

    private static String enumFilter(String enumType, boolean nullable) {
        return unescape(enumType) + (nullable ? "Nullable" : "") + "Filter";
    }

    // The filter of an enum is generated in the service's module, once per enum and nullability a filter uses.
    private void appendEnumFilters(StringBuilder out) {
        Set<String> written = new LinkedHashSet<>();
        for (ExposedEntity entity : entities) {
            if (!entity.allows(READ)) {
                continue;
            }
            for (ServiceField field : filterable(entity)) {
                ExposedEntity.Column column = column(entity, field);
                if (!"ENUM".equals(column.type()) || !written.add(enumFilter(field.type(), column.nullable()))) {
                    continue;
                }
                String enumType = field.type();
                out.append(NEWLINE).append("# The conditions on ").append(column.nullable() ? "a nullable `" : "a `")
                        .append(unescape(enumType)).append("` field")
                        .append(column.nullable() ? ". A null field matches only `isNull`." :
                                "; every condition set must hold.").append(NEWLINE);
                out.append("public type ").append(enumFilter(enumType, column.nullable())).append(" record {|")
                        .append(NEWLINE);
                out.append("    # Equal to the value").append(NEWLINE);
                out.append("    ").append(enumType).append("? eq;").append(NEWLINE);
                out.append("    # Not equal to the value")
                        .append(column.nullable() ? "; a null field does not match" : "").append(NEWLINE);
                out.append("    ").append(enumType).append("? ne;").append(NEWLINE);
                out.append("    # Equal to any of the values").append(NEWLINE);
                out.append("    ").append(enumType).append("[]? anyOf;").append(NEWLINE);
                if (column.nullable()) {
                    out.append("    # `true` for null, `false` for not null").append(NEWLINE);
                    out.append("    boolean? isNull;").append(NEWLINE);
                }
                out.append("|};").append(NEWLINE);
            }
        }
    }

    private static String filterType(ExposedEntity.Column column) {
        String scalar = switch (column.type()) {
            case "INT" -> "Int";
            case "FLOAT" -> "Float";
            case "DECIMAL" -> "Decimal";
            case "STRING" -> "String";
            case "BOOLEAN" -> "Boolean";
            case "DATE" -> "Date";
            case "TIME_OF_DAY" -> "TimeOfDay";
            case "CIVIL" -> "Civil";
            case "UTC" -> "Utc";
            default -> throw new IllegalArgumentException("no filter for " + column.type());
        };
        return scalar + (column.nullable() ? "Nullable" : "") + "Filter";
    }

    // A field that cannot be compared, such as a `byte[]` field, is neither filtered nor sorted.
    private static List<ServiceField> filterable(ExposedEntity entity) {
        return returned(entity).stream().filter(field -> column(entity, field).queryable()).toList();
    }

    private static ExposedEntity.Column column(ExposedEntity entity, ServiceField field) {
        String name = unescape(field.name());
        return entity.columns().stream().filter(column -> column.name().equals(name)).findFirst().orElseThrow();
    }

    private void appendObject(StringBuilder out, ExposedEntity entity, Set<String> imports) {
        Names n = new Names(entity);
        out.append(NEWLINE);
        appendEntityDoc(out, entity, n);
        out.append("public isolated service class ").append(n.object).append(" {").append(NEWLINE);
        out.append("    private final readonly & ").append(n.entity).append(" row;").append(NEWLINE).append(NEWLINE);
        out.append("    isolated function init(").append(n.entity).append(" row) {").append(NEWLINE);
        out.append("        self.row = row.cloneReadOnly();").append(NEWLINE);
        out.append("    }").append(NEWLINE);
        for (ServiceField field : returned(entity)) {
            appendFieldResolver(out, entity, n, field, imports);
        }
        for (ExposedRelation relation : entity.relations()) {
            if (relation.many()) {
                appendToMany(out, entity, relation, imports);
            } else {
                appendToOne(out, entity, relation, imports);
            }
        }
        out.append("}").append(NEWLINE);
    }

    private void appendToMany(StringBuilder out, ExposedEntity entity, ExposedRelation relation,
                              Set<String> imports) {
        imports.add(DATASERVICE_IMPORT);
        imports.add(DATASERVICE_GRAPHQL_IMPORT);
        imports.add(PERSIST_IMPORT);
        Names n = new Names(entity);
        Names t = new Names(target(relation));
        String words = words(relation.field());
        appendDoc(out, "    ", null, "Gets the " + words + " of " + n.article + " " + n.words + ".", List.of(
                "filter - The conditions that the " + words + " match",
                "sort - The order of the " + words + "; each entry names one field",
                "size - The number of " + words + " in the page",
                "offset - The number of " + words + " to skip"), "A page of the " + words + ", or the error");
        out.append("    isolated resource function get ").append(relation.field()).append("(").append(t.filter)
                .append("? filter = (), ").append(t.sort).append("[]? sort = (),").append(NEWLINE)
                .append("            int? size = (), int? offset = ()) returns ").append(t.relatedList)
                .append("?|error {").append(NEWLINE);
        out.append("        dataservice:Query query = check dsGraphql:toRelatedQuery(").append(n.description)
                .append(", \"").append(unescape(relation.field())).append("\", self.row,").append(NEWLINE)
                .append("                dataservicePageConfig, filter, sort, size, offset);").append(NEWLINE);
        out.append("        stream<").append(t.entity).append(", persist:Error?> rows = dataserviceClient->/")
                .append(t.resource).append("(").append(t.entity).append(",").append(NEWLINE);
        out.append("            whereClause = query.whereClause, orderByClause = query.orderByClause,").append(NEWLINE);
        out.append("            limitClause = query.limitClause, offsetClause = query.offsetClause").append(NEWLINE);
        out.append("        );").append(NEWLINE);
        out.append("        ").append(t.entity).append("[]|persist:Error items = from ").append(t.entity)
                .append(" item in rows").append(NEWLINE);
        out.append("            select item;").append(NEWLINE);
        out.append("        if items is persist:Error {").append(NEWLINE);
        out.append("            return dsGraphql:toListError(items, \"").append(n.displayName).append("\", \"")
                .append(RELATED_OPERATION).append("\");").append(NEWLINE);
        out.append("        }").append(NEWLINE);
        out.append("        boolean hasMore = dataservice:trimToPage(items, query);").append(NEWLINE);
        out.append("        return {items: from ").append(t.entity).append(" item in items select new ")
                .append(t.relatedObject).append("(item), hasMore};").append(NEWLINE);
        out.append("    }").append(NEWLINE);
    }

    // A to-one relation is read through the request's dataloader of the relation, keyed by the record's link: the
    // fields that link the relation, as a record.
    private void appendToOne(StringBuilder out, ExposedEntity entity, ExposedRelation relation,
                             Set<String> imports) {
        imports.add(GRAPHQL_IMPORT);
        Names n = new Names(entity);
        Names t = new Names(target(relation));
        String prefetch = "prefetch" + capitalize(unescape(relation.field()));
        String link = link(relation);
        out.append(NEWLINE).append("    isolated function ").append(prefetch).append("(graphql:Context ctx) {")
                .append(NEWLINE);
        out.append("        ctx.getDataLoader(\"").append(loaderId(entity, relation)).append("\").add(").append(link)
                .append(");").append(NEWLINE);
        out.append("    }").append(NEWLINE);
        appendDoc(out, "    ", null, "Gets the " + words(relation.field()) + " of " + n.article + " " + n.words + ".",
                List.of("ctx - The request context, which holds the dataloader of the relation"),
                "The " + words(relation.field()) + ", `()` when there is none, or the error");
        out.append("    @graphql:ResourceConfig {prefetchMethodName: \"").append(prefetch).append("\"}")
                .append(NEWLINE);
        out.append("    isolated resource function get ").append(relation.field())
                .append("(graphql:Context ctx) returns ").append(t.relatedObject).append("?|error {").append(NEWLINE);
        out.append("        ").append(t.entity).append("? row = check ctx.getDataLoader(\"")
                .append(loaderId(entity, relation)).append("\").get(").append(link).append(");").append(NEWLINE);
        out.append("        return row is ").append(t.entity).append(" ? new (row) : ();").append(NEWLINE);
        out.append("    }").append(NEWLINE);
    }

    private static String link(ExposedRelation relation) {
        return "{" + relation.fields().stream().map(field -> "\"" + unescape(field) + "\": self.row." + field)
                .collect(Collectors.joining(", ")) + "}";
    }

    private static String loaderId(ExposedEntity entity, ExposedRelation relation) {
        return unescape(entity.name()) + "." + unescape(relation.field());
    }

    private static String loaderFunction(ExposedEntity entity, ExposedRelation relation) {
        return "dataserviceLoad" + unescape(entity.name()) + capitalize(unescape(relation.field()));
    }

    private List<Map.Entry<ExposedEntity, ExposedRelation>> toOneRelations() {
        List<Map.Entry<ExposedEntity, ExposedRelation>> relations = new ArrayList<>();
        for (ExposedEntity entity : entities) {
            for (ExposedRelation relation : entity.relations()) {
                if (!relation.many()) {
                    relations.add(Map.entry(entity, relation));
                }
            }
        }
        return relations;
    }

    // Each request gets one dataloader per to-one relation, whose batch function reads the related records of every
    // collected link with one query.
    private void appendContext(StringBuilder out, Set<String> imports) {
        List<Map.Entry<ExposedEntity, ExposedRelation>> relations = toOneRelations();
        if (relations.isEmpty() && !auth) {
            return;
        }
        if (!relations.isEmpty()) {
            imports.add("import ballerina/graphql.dataloader;");
            imports.add(DATASERVICE_IMPORT);
        }
        imports.add("import ballerina/http;");
        out.append(NEWLINE).append("# Creates the context of a request")
                .append(auth ? ", after authenticating it" : "")
                .append(relations.isEmpty() ? "." : ", with a dataloader per to-one relation.").append(NEWLINE);
        out.append("#").append(NEWLINE);
        out.append("# + requestContext - The HTTP request context").append(NEWLINE);
        out.append("# + request - The HTTP request").append(NEWLINE);
        out.append("# + return - The context, or the error").append(NEWLINE);
        out.append("isolated function dataserviceInitContext(http:RequestContext requestContext, http:Request request)")
                .append(NEWLINE).append("        returns graphql:Context|error {").append(NEWLINE);
        out.append("    graphql:Context ctx = new;").append(NEWLINE);
        if (auth) {
            // contextInit runs before ballerina/graphql parses the document, so every request is authenticated.
            imports.add(DATASERVICE_GRAPHQL_IMPORT);
            out.append("    check dataserviceAuthenticator.authenticate(ctx, request);").append(NEWLINE);
        }
        for (Map.Entry<ExposedEntity, ExposedRelation> entry : relations) {
            out.append("    ctx.registerDataLoader(\"").append(loaderId(entry.getKey(), entry.getValue()))
                    .append("\",").append(NEWLINE).append("            new dataloader:DefaultDataLoader(")
                    .append(loaderFunction(entry.getKey(), entry.getValue())).append("));").append(NEWLINE);
        }
        out.append("    return ctx;").append(NEWLINE);
        out.append("}").append(NEWLINE);
        for (Map.Entry<ExposedEntity, ExposedRelation> entry : relations) {
            appendLoader(out, entry.getKey(), entry.getValue());
        }
    }

    private void appendLoader(StringBuilder out, ExposedEntity entity, ExposedRelation relation) {
        Names n = new Names(entity);
        Names t = new Names(target(relation));
        String name = "\"" + unescape(relation.field()) + "\"";
        out.append(NEWLINE).append("isolated function ").append(loaderFunction(entity, relation))
                .append("(readonly & anydata[] keys) returns anydata[]|error {").append(NEWLINE);
        out.append("    map<anydata>[] links = from anydata key in keys").append(NEWLINE);
        out.append("        select check key.ensureType();").append(NEWLINE);
        out.append("    dataservice:Query query = dataservice:toIncludeQuery(").append(n.description).append(", ")
                .append(name).append(", links,").append(NEWLINE).append("            dataservicePageConfig);")
                .append(NEWLINE);
        out.append("    stream<").append(t.entity).append(", persist:Error?> rows = dataserviceClient->/")
                .append(t.resource).append("(").append(t.entity).append(",").append(NEWLINE);
        out.append("        whereClause = query.whereClause, orderByClause = query.orderByClause").append(NEWLINE);
        out.append("    );").append(NEWLINE);
        out.append("    ").append(t.entity).append("[]|persist:Error found = from ").append(t.entity)
                .append(" row in rows").append(NEWLINE);
        out.append("        select row;").append(NEWLINE);
        out.append("    if found is persist:Error {").append(NEWLINE);
        out.append("        return dsGraphql:toListError(found, \"").append(n.displayName).append("\", \"")
                .append(RELATED_OPERATION).append("\");").append(NEWLINE);
        out.append("    }").append(NEWLINE);
        out.append("    return dsGraphql:toKeyOrder(found, ").append(n.description).append(", ").append(name)
                .append(", links);").append(NEWLINE);
        out.append("}").append(NEWLINE);
    }

    // A related record has the entity's own fields and no relation fields, so a document reads relations one hop
    // deep.
    private void appendRelated(StringBuilder out, ExposedEntity entity, Set<String> imports) {
        List<ExposedRelation> reaching = entities.stream().flatMap(other -> other.relations().stream())
                .filter(relation -> relation.target().equals(entity.name())).toList();
        if (reaching.isEmpty()) {
            return;
        }
        Names n = new Names(entity);
        out.append(NEWLINE).append("# ").append(capitalize(n.article)).append(" ").append(n.words)
                .append(", as a related record: its own fields and no relation fields.").append(NEWLINE);
        out.append("public isolated service class ").append(n.relatedObject).append(" {").append(NEWLINE);
        out.append("    private final readonly & ").append(n.entity).append(" row;").append(NEWLINE).append(NEWLINE);
        out.append("    isolated function init(").append(n.entity).append(" row) {").append(NEWLINE);
        out.append("        self.row = row.cloneReadOnly();").append(NEWLINE);
        out.append("    }").append(NEWLINE);
        for (ServiceField field : returned(entity)) {
            appendFieldResolver(out, entity, n, field, imports);
        }
        out.append("}").append(NEWLINE);
        if (reaching.stream().anyMatch(ExposedRelation::many)) {
            out.append(NEWLINE).append("# A page of related ").append(n.plural).append(".").append(NEWLINE);
            out.append("#").append(NEWLINE);
            out.append("# + items - The ").append(n.plural).append(NEWLINE);
            out.append("# + hasMore - Whether records exist after this page; read them with `offset`").append(NEWLINE);
            out.append("public type ").append(n.relatedList).append(" record {").append(NEWLINE);
            out.append("    ").append(n.relatedObject).append("[] items;").append(NEWLINE);
            out.append("    boolean hasMore;").append(NEWLINE);
            out.append("};").append(NEWLINE);
        }
    }

    private ExposedEntity target(ExposedRelation relation) {
        return entitiesByName.get(relation.target());
    }

    private static String words(String name) {
        return String.join(" ", WORD_BOUNDARY.split(unescape(name))).toLowerCase(Locale.ENGLISH);
    }

    private static void appendList(StringBuilder out, ExposedEntity entity) {
        Names n = new Names(entity);
        // ballerina/graphql describes the fields of an output record from its `+ field` lines.
        out.append(NEWLINE).append("# A page of ").append(n.plural).append(".").append(NEWLINE);
        out.append("#").append(NEWLINE);
        out.append("# + items - The ").append(n.plural).append(NEWLINE);
        out.append("# + hasMore - Whether records exist after this page").append(NEWLINE);
        out.append("public type ").append(n.list).append(" record {").append(NEWLINE);
        out.append("    ").append(n.object).append("[] items;").append(NEWLINE);
        out.append("    boolean hasMore;").append(NEWLINE);
        out.append("};").append(NEWLINE);
    }

    // A date, time or byte[] field is a string in the schema, since ballerina/graphql has no scalar for it.
    private static String outputType(ServiceField field) {
        return isFloat(field) ? field.wireType() + "|error" : field.wireType();
    }

    private static boolean isFloat(ServiceField field) {
        return "float".equals(field.type());
    }

    // A nullable date, time or byte[] field is read into a variable, since a field access cannot be narrowed.
    private static void appendFieldResolver(StringBuilder out, ExposedEntity entity, Names n, ServiceField field,
                                            Set<String> imports) {
        out.append(NEWLINE).append("    # ").append(fieldDoc(field, entity, n)).append(NEWLINE);
        out.append("    # + return - The value").append(NEWLINE);
        out.append("    isolated resource function get ").append(field.name()).append("() returns ")
                .append(outputType(field));
        String access = "self.row." + field.name();
        if (isFloat(field)) {
            // JSON cannot hold a stored NaN or Infinity, which is answered as an error of the field.
            String finite = "dsGraphql:toFinite(%s, \"" + n.displayName + "\", \"" + unescape(field.name()) + "\")";
            imports.add(DATASERVICE_GRAPHQL_IMPORT);
            if (!field.nilable()) {
                out.append(" => ").append(String.format(finite, access)).append(";").append(NEWLINE);
                return;
            }
            out.append(" {").append(NEWLINE);
            out.append("        float? value = ").append(access).append(";").append(NEWLINE);
            out.append("        return value is () ? () : check ").append(String.format(finite, "value")).append(";")
                    .append(NEWLINE);
            out.append("    }").append(NEWLINE);
            return;
        }
        if (field.conversion().isEmpty()) {
            out.append(" => ").append(access).append(";").append(NEWLINE);
            return;
        }
        imports.add(DATASERVICE_IMPORT);
        String convert = field.conversion().get().toStringFunction();
        if (!field.nilable()) {
            out.append(" => ").append(convert).append("(").append(access).append(");").append(NEWLINE);
            return;
        }
        if (field.type().startsWith("time:")) {
            imports.add("import ballerina/time;");
        }
        out.append(" {").append(NEWLINE);
        out.append("        ").append(field.persistType()).append(" value = ").append(access).append(";")
                .append(NEWLINE);
        out.append("        return value is () ? () : ").append(convert).append("(value);").append(NEWLINE);
        out.append("    }").append(NEWLINE);
    }

    private static void appendEntityDoc(StringBuilder out, ExposedEntity entity, Names n) {
        if (entity.doc().isEmpty()) {
            out.append("# ").append(capitalize(n.article)).append(" ").append(n.words).append(".").append(NEWLINE);
            return;
        }
        for (String line : entity.doc()) {
            out.append("#").append(line.isEmpty() ? "" : " " + line).append(NEWLINE);
        }
    }

    private static void appendDoc(StringBuilder out, String indent, ExposedEntity entity, String summary,
                                  List<String> params, String result) {
        out.append(NEWLINE).append(indent).append("# ").append(summary).append(NEWLINE);
        if (entity != null && !entity.doc().isEmpty()) {
            out.append(indent).append("#").append(NEWLINE);
            for (String line : entity.doc()) {
                out.append(indent).append("#").append(line.isEmpty() ? "" : " " + line).append(NEWLINE);
            }
        }
        out.append(indent).append("#").append(NEWLINE);
        for (String param : params) {
            out.append(indent).append("# + ").append(param).append(NEWLINE);
        }
        out.append(indent).append("# + return - ").append(result).append(NEWLINE);
    }

    private static String fieldDoc(ServiceField field, ExposedEntity entity, Names n) {
        if (field.doc().isPresent()) {
            return field.doc().get();
        }
        Optional<String> target = entity.foreignKeyTarget(field.name());
        if (target.isPresent()) {
            return "The key of the related '" + target.get() + "'.";
        }
        String doc = "The `" + unescape(field.name()) + "` field of `" + n.displayName + "`";
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

    private static List<ServiceField> returned(ExposedEntity entity) {
        return entity.fields().stream().filter(field -> !field.has(FieldRule.INTERNAL)).toList();
    }

    // The name a quoted identifier such as `'n\-ame` stands for, `n-ame`.
    private static String graphqlName(String name) {
        return ESCAPE.matcher(unescape(name)).replaceAll("$1");
    }

    private static String identifier(String name) {
        return SyntaxInfo.isKeyword(name) ? "'" + name : name;
    }

    private static String capitalize(String text) {
        return text.substring(0, 1).toUpperCase(Locale.ENGLISH) + text.substring(1);
    }

    private static String articleFor(String name) {
        return "aeiouAEIOU".indexOf(name.charAt(0)) >= 0 ? "an" : "a";
    }

    private static Set<String> libraryTypes() {
        Set<String> types = new LinkedHashSet<>();
        for (String scalar : List.of("Int", "Float", "Decimal", "String", "Boolean", "Date", "TimeOfDay", "Civil",
                "Utc")) {
            types.add(scalar + "Filter");
            types.add(scalar + "NullableFilter");
        }
        types.add("SortDirection");
        return Set.copyOf(types);
    }

    private static final class Names {
        private final String entity;
        private final String displayName;
        private final String resource;
        private final String getMany;
        private final String getOne;
        private final String object;
        private final String relatedObject;
        private final String list;
        private final String relatedList;
        private final String insertInput;
        private final String updateInput;
        private final String filter;
        private final String sort;
        private final String description;
        private final String createOne;
        private final String createMany;
        private final String createFunction;
        private final String update;
        private final String delete;
        private final String words;
        private final String plural;
        private final String article;

        private Names(ExposedEntity entity) {
            this.entity = entity.name();
            this.displayName = unescape(entity.name());
            this.resource = entity.resourceName();
            String[] parts = WORD_BOUNDARY.split(displayName);
            parts[0] = parts[0].toLowerCase(Locale.ENGLISH);
            this.getOne = String.join("", parts);
            // An entity whose plural is its singular name, such as `Equipment`, gets `List` on its list fields.
            this.getMany = entity.collection().equals(getOne) ? getOne + "List" : entity.collection();
            this.object = displayName + "Object";
            this.relatedObject = displayName + "RelatedObject";
            this.list = displayName + "List";
            this.relatedList = displayName + "RelatedList";
            this.insertInput = displayName + "InsertInput";
            this.updateInput = displayName + "UpdateInput";
            this.filter = displayName + "Filter";
            this.sort = displayName + "Sort";
            this.description = EntityDescription.descriptionName(entity);
            this.createOne = "create" + displayName;
            this.createMany = "create" + capitalize(getMany);
            this.createFunction = "dataserviceCreate" + displayName;
            this.update = "update" + displayName;
            this.delete = "delete" + displayName;
            this.words = String.join(" ", WORD_BOUNDARY.split(displayName)).toLowerCase(Locale.ENGLISH);
            this.plural = Pluralizer.pluralize(words);
            this.article = articleFor(words);
        }

        private List<String> rootFields(ExposedEntity entity) {
            List<String> fields = new ArrayList<>();
            if (entity.allows(READ)) {
                fields.add(getMany);
                fields.add(getOne);
            }
            if (entity.allows(CREATE)) {
                fields.add(createOne);
                fields.add(createMany);
            }
            if (entity.allows(UPDATE)) {
                fields.add(update);
            }
            if (entity.allows(DELETE)) {
                fields.add(delete);
            }
            return fields;
        }

        private List<String> types() {
            return List.of(object, relatedObject, list, relatedList, insertInput, updateInput, filter, sort);
        }
    }
}
