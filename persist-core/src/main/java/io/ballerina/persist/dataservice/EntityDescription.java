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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static io.ballerina.persist.dataservice.DataServiceUtils.NEWLINE;
import static io.ballerina.persist.dataservice.DataServiceUtils.unescape;

/**
 * Writes the description of an entity that `ballerina/dataservice` builds queries from, for every protocol.
 */
final class EntityDescription {

    private EntityDescription() {
    }

    static String fieldsName(ExposedEntity entity) {
        return "dataservice" + unescape(entity.name()) + "Fields";
    }

    static String descriptionName(ExposedEntity entity) {
        return "dataservice" + unescape(entity.name()) + "Entity";
    }

    static void append(StringBuilder out, ExposedEntity entity, Map<String, ExposedEntity> entities,
                       String datastore) {
        out.append(NEWLINE).append("# The fields of `").append(entity.name()).append("` that callers can see.")
                .append(NEWLINE);
        out.append("final readonly & dataservice:Field[] ").append(fieldsName(entity)).append(" = [").append(NEWLINE);
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
        out.append(NEWLINE).append("# The description of `").append(entity.name())
                .append("` that `ballerina/dataservice` builds queries from.").append(NEWLINE);
        out.append("final dataservice:Entity ").append(descriptionName(entity)).append(" = ")
                .append(literal(entity, entities, datastore, "", true)).append(";").append(NEWLINE);
    }

    private static String literal(ExposedEntity entity, Map<String, ExposedEntity> entities, String datastore,
                                  String indent, boolean withRelations) {
        StringBuilder out = new StringBuilder("{").append(NEWLINE);
        out.append(indent).append("    name: \"").append(unescape(entity.name())).append("\",").append(NEWLINE);
        out.append(indent).append("    collection: \"").append(entity.collection()).append("\",").append(NEWLINE);
        out.append(indent).append("    'resource: \"").append(entity.resourceName()).append("\",").append(NEWLINE);
        out.append(indent).append("    datastore: dataservice:").append(datastore.toUpperCase(Locale.ENGLISH))
                .append(",").append(NEWLINE);
        out.append(indent).append("    fields: ").append(fieldsName(entity));
        if (withRelations && !entity.relations().isEmpty()) {
            out.append(",").append(NEWLINE).append(indent).append("    relations: [").append(NEWLINE);
            List<String> relations = new ArrayList<>();
            for (ExposedRelation relation : entity.relations()) {
                relations.add(indent + "        {" + NEWLINE
                        + indent + "            'field: \"" + unescape(relation.field()) + "\"," + NEWLINE
                        + indent + "            many: " + relation.many() + "," + NEWLINE
                        + indent + "            fields: " + stringList(relation.fields()) + "," + NEWLINE
                        + indent + "            targetFields: " + stringList(relation.targetFields()) + "," + NEWLINE
                        + indent + "            target: " + literal(entities.get(relation.target()), entities,
                        datastore, indent + "            ", false) + NEWLINE + indent + "        }");
            }
            out.append(String.join("," + NEWLINE, relations)).append(NEWLINE).append(indent).append("    ]");
        }
        return out.append(NEWLINE).append(indent).append("}").toString();
    }

    // A scope is kept as the body of its string literal in the annotation, so it is quoted as written.
    static String scopeList(List<String> scopes) {
        return "[" + scopes.stream().map(scope -> "\"" + scope + "\"").collect(Collectors.joining(", ")) + "]";
    }

    static String stringList(List<String> names) {
        return "[" + names.stream().map(name -> "\"" + unescape(name) + "\"").collect(Collectors.joining(", "))
                + "]";
    }
}
