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

    private static final Pattern ESCAPE = Pattern.compile("\\\\(.)");
    private static final Pattern GRAPHQL_NAME = Pattern.compile("[_A-Za-z][_0-9A-Za-z]*");
    private static final String DATASERVICE_GRAPHQL_IMPORT = "import ballerina/dataservice.graphql as dsGraphql;";
    private static final String GRAPHQL_IMPORT = "import ballerina/graphql;";
    private static final String DATASERVICE_IMPORT = "import ballerina/dataservice;";

    private final List<ExposedEntity> entities;
    private final Map<String, ExposedEntity> entitiesByName = new LinkedHashMap<>();
    private final String basePath;
    private final boolean introspection;
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
        Set<String> imports = new TreeSet<>(List.of(GRAPHQL_IMPORT, "import ballerina/persist;"));
        StringBuilder body = new StringBuilder();
        body.append("# The persist client of the data service.").append(NEWLINE);
        body.append("final Client dataserviceClient = check new ();").append(NEWLINE).append(NEWLINE);
        body.append("# The listener of the data service.").append(NEWLINE);
        body.append("listener graphql:Listener dataserviceListener =").append(NEWLINE)
                .append("        new (dataserviceConfig.port, dataserviceConfig.listenerConfig);").append(NEWLINE)
                .append(NEWLINE);
        body.append("@graphql:ServiceConfig {").append(NEWLINE);
        List<String> config = new ArrayList<>();
        // GraphiQL loads the schema through introspection, so it is served exactly when introspection is on.
        config.add(introspection ? "graphiql: {enabled: true}" : "introspection: false");
        config.add("cors: dataserviceConfig.serviceConfig.cors");
        body.append("    ").append(String.join("," + NEWLINE + "    ", config)).append(NEWLINE);
        body.append("}").append(NEWLINE);
        body.append("service graphql:Service ").append(DataServiceUtils.servicePath(basePath))
                .append(" on dataserviceListener {").append(NEWLINE);
        for (ExposedEntity entity : entities) {
            if (entity.allows(READ)) {
                appendGetMany(body, entity, imports);
                appendGetOne(body, entity, imports);
            }
        }
        body.append("}").append(NEWLINE);
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
            if (entity.allows(READ)) {
                appendObject(body, entity, imports);
                appendList(body, entity);
                appendFilter(body, entity);
                appendSort(body, entity);
                imports.add(DATASERVICE_IMPORT);
                imports.add(DATASERVICE_GRAPHQL_IMPORT);
                EntityDescription.append(body, entity, entitiesByName, datastore);
            }
        }
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
                "configurable dsGraphql:Config dataserviceConfig = {};" + NEWLINE +
                NEWLINE +
                "# The page sizes of the data service, checked at startup." + NEWLINE +
                "final dataservice:PageConfig dataservicePageConfig =" + NEWLINE +
                "        check dataservice:toPageConfig(dataserviceConfig.pageSize, dataserviceConfig.maxPageSize);" +
                NEWLINE);
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
        out.append("    resource function get ").append(identifier(n.getMany)).append("(").append(n.filter)
                .append("? filter = (), ").append(n.sort).append("[]? sort = (),").append(NEWLINE)
                .append("            int? size = (), int? offset = ()) returns ").append(n.list).append("?|error {")
                .append(NEWLINE);
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
        out.append("    resource function get ").append(identifier(n.getOne)).append("(").append(keyParams(entity))
                .append(") returns ").append(n.object).append("?|error {").append(NEWLINE);
        out.append("        ").append(n.entity).append("|persist:Error item = dataserviceClient->/").append(n.resource)
                .append(keyPath(entity)).append(";").append(NEWLINE);
        out.append("        if item is persist:Error {").append(NEWLINE);
        out.append("            return dsGraphql:toGetError(item, \"").append(n.displayName).append("\", ")
                .append(keyList(entity)).append(");").append(NEWLINE);
        out.append("        }").append(NEWLINE);
        out.append("        return new (item);").append(NEWLINE);
        out.append("    }").append(NEWLINE);
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
            Optional<String> type = filterType(column(entity, field));
            if (type.isEmpty()) {
                continue;
            }
            out.append("    # The conditions on `").append(unescape(field.name())).append("`").append(NEWLINE);
            out.append("    dsGraphql:").append(type.get()).append("? ").append(field.name()).append(";")
                    .append(NEWLINE);
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

    // The filter of an enum field is generated per enum, with the enum types.
    private static Optional<String> filterType(ExposedEntity.Column column) {
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
            default -> null;
        };
        return Optional.ofNullable(scalar).map(name -> name + (column.nullable() ? "Nullable" : "") + "Filter");
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
            out.append(NEWLINE).append("    # ").append(fieldDoc(field, entity, n)).append(NEWLINE);
            out.append("    # + return - The value").append(NEWLINE);
            out.append("    isolated resource function get ").append(field.name()).append("() returns ")
                    .append(outputType(field)).append(" => ").append(toOutput(field, imports)).append(";")
                    .append(NEWLINE);
        }
        out.append("}").append(NEWLINE);
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
        return field.wireType();
    }

    private static String toOutput(ServiceField field, Set<String> imports) {
        String access = "self.row." + field.name();
        if (field.conversion().isEmpty()) {
            return access;
        }
        imports.add("import ballerina/dataservice;");
        String convert = field.conversion().get().toStringFunction();
        if (field.nilable()) {
            return access + " is () ? () : " + convert + "(" + access + ")";
        }
        return convert + "(" + access + ")";
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
        if (!entity.doc().isEmpty()) {
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
                fields.add("create" + displayName);
                fields.add("create" + capitalize(getMany));
            }
            if (entity.allows(UPDATE)) {
                fields.add("update" + displayName);
            }
            if (entity.allows(DELETE)) {
                fields.add("delete" + displayName);
            }
            return fields;
        }

        private List<String> types() {
            return List.of(object, relatedObject, list, relatedList, insertInput, updateInput, filter, sort);
        }
    }
}
