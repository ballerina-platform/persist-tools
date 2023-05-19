/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com) All Rights Reserved.
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
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
package io.ballerina.persist.nodegenerator.syntax.clients;

import io.ballerina.compiler.syntax.tree.FunctionDefinitionNode;
import io.ballerina.compiler.syntax.tree.ImportDeclarationNode;
import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;
import io.ballerina.compiler.syntax.tree.NodeList;
import io.ballerina.compiler.syntax.tree.NodeParser;
import io.ballerina.compiler.syntax.tree.SyntaxKind;
import io.ballerina.persist.components.Client;
import io.ballerina.persist.components.Function;
import io.ballerina.persist.components.IfElse;
import io.ballerina.persist.components.TypeDescriptor;
import io.ballerina.persist.models.Entity;
import io.ballerina.persist.models.EntityField;
import io.ballerina.persist.models.Module;
import io.ballerina.persist.models.Relation;
import io.ballerina.persist.nodegenerator.syntax.constants.BalSyntaxConstants;
import io.ballerina.persist.nodegenerator.syntax.utils.BalSyntaxUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * This class is used to generate the DB client syntax tree.
 *
 * @since 0.3.1
 */
public class GSheetClientSyntax implements ClientSyntax {

    public final Map<String, String> queryMethodStatement = new HashMap<>();
    private final Module entityModule;
    public GSheetClientSyntax(Module entityModule) {
        this.entityModule = entityModule;
    }

    @Override
    public NodeList<ImportDeclarationNode> getImports() {
        NodeList<ImportDeclarationNode> imports = BalSyntaxUtils.generateImport(entityModule);
        imports = imports.add(BalSyntaxUtils.getImportDeclarationNode(BalSyntaxConstants.KEYWORD_BALLERINAX,
                BalSyntaxConstants.GOOGLE_API_SHEET, null));
        imports = imports.add(BalSyntaxUtils.getImportDeclarationNode(BalSyntaxConstants.KEYWORD_BALLERINA,
                BalSyntaxConstants.HTTP, null));
        return imports;
    }

    @Override
    public NodeList<ModuleMemberDeclarationNode> getConstantVariables() {
        return BalSyntaxUtils.generateConstantVariables(entityModule);
    }

    @Override
    public Client getClientObject(Module entityModule) {
        Client clientObject = BalSyntaxUtils.generateClientSignature(true);
        clientObject.addMember(NodeParser.parseObjectMember(BalSyntaxConstants.GOOGLE_SHEET_CLIENT), true);
        clientObject.addMember(NodeParser.parseObjectMember(BalSyntaxConstants.HTTP_CLIENT), true);
        clientObject.addMember(NodeParser.parseObjectMember(BalSyntaxConstants.GOOGLE_PERSIST_CLIENT), true);
        return clientObject;
    }

    @Override
    public FunctionDefinitionNode getInitFunction(Module entityModule) {
        String httpClient = "httpClient";
        String sheetClient = "googleSheetClient";
        Function init = new Function(BalSyntaxConstants.INIT, SyntaxKind.OBJECT_METHOD_DEFINITION);
        init.addQualifiers(new String[] { BalSyntaxConstants.KEYWORD_PUBLIC, BalSyntaxConstants.KEYWORD_ISOLATED });
        init.addReturns(TypeDescriptor.getOptionalTypeDescriptorNode(BalSyntaxConstants.EMPTY_STRING,
                BalSyntaxConstants.PERSIST_ERROR));
        init.addStatement(NodeParser.parseStatement(generateMetadataRecord(entityModule)));
        init.addStatement(NodeParser.parseStatement(BalSyntaxConstants.SHEET_CLIENT_CONFIG_TEMPLATE));
        init.addStatement(NodeParser.parseStatement(BalSyntaxConstants.HTTP_CLIENT_CONFIG_TEMPLATE));
        init.addStatement(NodeParser.parseStatement(BalSyntaxConstants.HTTP_CLIENT_INIT_TEMPLATE));
        IfElse errorCheck = new IfElse(NodeParser.parseExpression(String.format(
                BalSyntaxConstants.RESULT_IS_BALLERINA_ERROR, httpClient)));
        errorCheck.addIfStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.RETURN_ERROR,
                httpClient)));
        init.addIfElseStatement(errorCheck.getIfElseStatementNode());
        init.addStatement(NodeParser.parseStatement(BalSyntaxConstants.SHEET_CLIENT_INIT_TEMPLATE));
        IfElse isCheck = new IfElse(NodeParser.parseExpression(String.format(
                BalSyntaxConstants.RESULT_IS_BALLERINA_ERROR, sheetClient)));
        isCheck.addIfStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.RETURN_ERROR, sheetClient)));
        init.addIfElseStatement(isCheck.getIfElseStatementNode());
        init.addStatement(NodeParser.parseStatement(BalSyntaxConstants.SELF_HTTP_CLIENT_INIT_TEMPLATE));
        init.addStatement(NodeParser.parseStatement(BalSyntaxConstants.SELF_SHEET_CLIENT_INIT_TEMPLATE));
        init.addStatement(NodeParser.parseStatement(BalSyntaxConstants.SHEET_IDS_TEMPLATE));
        StringBuilder persistClientMap = new StringBuilder();
        for (Entity entity : entityModule.getEntityMap().values()) {
            if (persistClientMap.length() != 0) {
                persistClientMap.append(BalSyntaxConstants.COMMA_WITH_NEWLINE);
            }
            String nameWithUnderScore = BalSyntaxUtils.getStringWithUnderScore(entity.getEntityName());
            persistClientMap.append(String.format(BalSyntaxConstants.GOOGLE_SHEET_CLIENT_MAP,
                    nameWithUnderScore, nameWithUnderScore, nameWithUnderScore));
        }
        init.addStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.PERSIST_CLIENT_TEMPLATE,
                persistClientMap)));
        return init.getFunctionDefinitionNode();
    }

    @Override
    public FunctionDefinitionNode getGetFunction(Entity entity) {
        return BalSyntaxUtils.generateGetFunction(entity, "GoogleSheetsProcessor");
    }

    @Override
    public FunctionDefinitionNode getGetByKeyFunction(Entity entity) {
        return BalSyntaxUtils.generateGetByKeyFunction(entity, "GoogleSheetsProcessor");
    }

    @Override
    public FunctionDefinitionNode getCloseFunction() {
        Function close = BalSyntaxUtils.generateCloseFunction();
        close.addStatement(NodeParser.parseStatement(BalSyntaxConstants.RETURN_NIL));
        return close.getFunctionDefinitionNode();
    }

    @Override
    public FunctionDefinitionNode getPostFunction(Entity entity) {
        String parameterType = String.format(BalSyntaxConstants.INSERT_RECORD, entity.getEntityName());
        List<EntityField> primaryKeys = entity.getKeys();
        Function create = BalSyntaxUtils.generatePostFunction(entity, primaryKeys, parameterType, false);
        addFunctionBodyToPostResource(create, primaryKeys,
                BalSyntaxUtils.getStringWithUnderScore(entity.getEntityName()), parameterType);
        return create.getFunctionDefinitionNode();
    }

    @Override
    public FunctionDefinitionNode getPutFunction(Entity entity) {
        StringBuilder filterKeys = new StringBuilder(BalSyntaxConstants.OPEN_BRACE);
        StringBuilder path = new StringBuilder(BalSyntaxConstants.BACK_SLASH + entity.getResourceName());
        Function update = BalSyntaxUtils.generatePutFunction(entity, filterKeys, path, false);
        if (entity.getKeys().size() > 1) {

            update.addStatement(NodeParser.parseStatement(BalSyntaxConstants.G_SHEET_CLIENT_DECLARATION));

            String getPersistClientStatement = String.format(BalSyntaxConstants.GET_G_SHEET_PERSIST_CLIENT,
                    BalSyntaxUtils.getStringWithUnderScore(entity.getEntityName()));
            update.addStatement(NodeParser.parseStatement(
                    String.format(BalSyntaxConstants.LOCK_TEMPLATE, getPersistClientStatement)));
            update.addStatement(NodeParser.parseStatement(String.format(
                    BalSyntaxConstants.G_SHEET_UPDATE_RUN_UPDATE_QUERY,
                    filterKeys.substring(0, filterKeys.length() - 2).concat(BalSyntaxConstants.CLOSE_BRACE))));
        } else {
            update.addStatement(NodeParser.parseStatement(BalSyntaxConstants.G_SHEET_CLIENT_DECLARATION));

            String getPersistClientStatement = String.format(BalSyntaxConstants.GET_G_SHEET_PERSIST_CLIENT,
                    BalSyntaxUtils.getStringWithUnderScore(entity.getEntityName()));
            update.addStatement(NodeParser.parseStatement(
                    String.format(BalSyntaxConstants.LOCK_TEMPLATE, getPersistClientStatement)));
            update.addStatement(NodeParser.parseStatement(String.format(
                    BalSyntaxConstants.G_SHEET_UPDATE_RUN_UPDATE_QUERY, entity.getKeys().stream().
                            findFirst().get().getFieldName())));
        }
        update.addStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.UPDATE_RETURN_UPDATE_QUERY,
                path)));
        return update.getFunctionDefinitionNode();
    }

    @Override
    public FunctionDefinitionNode getDeleteFunction(Entity entity) {
        StringBuilder filterKeys = new StringBuilder(BalSyntaxConstants.OPEN_BRACE);
        StringBuilder path = new StringBuilder(BalSyntaxConstants.BACK_SLASH + entity.getResourceName());
        Function delete = BalSyntaxUtils.generateDeleteFunction(entity, filterKeys, path, false);
        delete.addStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.GET_OBJECT_QUERY,
                entity.getEntityName(), path)));
        if (entity.getKeys().size() > 1) {
            delete.addStatement(NodeParser.parseStatement(BalSyntaxConstants.G_SHEET_CLIENT_DECLARATION));

            String getPersistClientStatement = String.format(BalSyntaxConstants.GET_G_SHEET_PERSIST_CLIENT,
                    BalSyntaxUtils.getStringWithUnderScore(entity.getEntityName()));
            delete.addStatement(NodeParser.parseStatement(
                    String.format(BalSyntaxConstants.LOCK_TEMPLATE, getPersistClientStatement)));

            delete.addStatement(NodeParser.parseStatement(String.format(
                    BalSyntaxConstants.G_SHEET_DELETE_RUN_DELETE_QUERY,
                    filterKeys.substring(0, filterKeys.length() - 2).concat(BalSyntaxConstants.CLOSE_BRACE))));
        } else {
            delete.addStatement(NodeParser.parseStatement(BalSyntaxConstants.G_SHEET_CLIENT_DECLARATION));

            String getPersistClientStatement = String.format(BalSyntaxConstants.GET_G_SHEET_PERSIST_CLIENT,
                    BalSyntaxUtils.getStringWithUnderScore(entity.getEntityName()));
            delete.addStatement(NodeParser.parseStatement(
                    String.format(BalSyntaxConstants.LOCK_TEMPLATE, getPersistClientStatement)));
            delete.addStatement(NodeParser.parseStatement(String.format(
                    BalSyntaxConstants.G_SHEET_DELETE_RUN_DELETE_QUERY, entity.getKeys().stream().
                            findFirst().get().getFieldName())));
        }
        delete.addStatement(NodeParser.parseStatement(BalSyntaxConstants.RETURN_DELETED_OBJECT));
        return delete.getFunctionDefinitionNode();
    }

    private String generateMetadataRecord(Module entityModule) {
        StringBuilder mapBuilder = new StringBuilder();
        for (Entity entity : entityModule.getEntityMap().values()) {
            int index = 0;
            String endRange = "A";
            boolean hasAssociationMethod = false;
            String entityName = entity.getEntityName();
            String entityResourceName = entity.getResourceName();
            StringBuilder fieldMetaData = new StringBuilder();
            StringBuilder associationsMethods = new StringBuilder();
            StringBuilder fieldType = new StringBuilder();
            String resourceName = BalSyntaxUtils.stripEscapeCharacter(entityResourceName);
            resourceName = resourceName.substring(0, 1).toUpperCase(Locale.ENGLISH) +
                    resourceName.substring(1).toLowerCase(Locale.ENGLISH);
            if (mapBuilder.length() != 0) {
                mapBuilder.append(BalSyntaxConstants.COMMA_WITH_NEWLINE);
            }
            StringBuilder entityMetaData = new StringBuilder();
            entityMetaData.append(String.format(BalSyntaxConstants.METADATA_RECORD_ENTITY_NAME_TEMPLATE,
                    BalSyntaxUtils.stripEscapeCharacter(entityName)));
            entityMetaData.append(String.format(BalSyntaxConstants.TABLE_NAME_TEMPLATE,
                    BalSyntaxUtils.stripEscapeCharacter(entityName)));
            entityMetaData.append(String.format(BalSyntaxConstants.METADATA_KEY_FIELDS_TEMPLATE,
                    BalSyntaxUtils.getPrimaryKeys(entity, true)));
            for (EntityField field : entity.getFields()) {
                if (field.getRelation() == null) {
                    if (fieldMetaData.length() != 0) {
                        fieldMetaData.append(BalSyntaxConstants.COMMA_WITH_NEWLINE);
                        fieldType.append(BalSyntaxConstants.COMMA_WITH_NEWLINE);
                    }
                    endRange = getEndRange(++index);
                    fieldMetaData.append(String.format(BalSyntaxConstants.G_SHEET_FIELD_METADATA_TEMPLATE,
                            field.getFieldName(), BalSyntaxUtils.stripEscapeCharacter(field.getFieldName()), endRange));
                    fieldType.append(String.format(BalSyntaxConstants.FIELD_TYPE, field.getFieldName(),
                            field.getFieldType()));
                } else {
                    Relation relation = field.getRelation();
                    if (relation.isOwner()) {
                        String fieldName;
                        if (fieldMetaData.length() != 0) {
                            fieldMetaData.append(BalSyntaxConstants.COMMA_WITH_NEWLINE);
                            fieldType.append(BalSyntaxConstants.COMMA_WITH_NEWLINE);
                        }
                        for (Relation.Key reference : relation.getKeyColumns()) {
                            fieldName = reference.getField();
                            endRange = getEndRange(++index);
                            fieldMetaData.append(String.format(BalSyntaxConstants.G_SHEET_FIELD_METADATA_TEMPLATE,
                                    fieldName, BalSyntaxUtils.stripEscapeCharacter(fieldName), endRange));
                            fieldType.append(String.format(BalSyntaxConstants.FIELD_TYPE, fieldName,
                                    reference.getType()));
                        }
                    } else if (relation.getRelationType().equals(Relation.RelationType.MANY)) {
                        Entity assEntity = relation.getAssocEntity();
                        for (EntityField entityField : assEntity.getFields()) {
                            if (entityField.getRelation() != null && entityField.getFieldType().
                                    equals(entityName) && entityField.getRelation().
                                    getRelationType().equals(Relation.RelationType.ONE)) {
                                hasAssociationMethod = true;
                                if (associationsMethods.length() != 0) {
                                    associationsMethods.append(BalSyntaxConstants.COMMA_WITH_NEWLINE);
                                }
                                String associateEntityName = BalSyntaxUtils.stripEscapeCharacter(relation.
                                        getAssocEntity().getResourceName());
                                String associateEntityNameCamelCase = associateEntityName.substring(0, 1).
                                        toUpperCase(Locale.ENGLISH) + associateEntityName.substring(1).
                                        toLowerCase(Locale.ENGLISH);

                                String associateFieldName = BalSyntaxUtils.stripEscapeCharacter(field.getFieldName());
                                String associateFieldNameCamelCase = associateFieldName.substring(0, 1).
                                        toUpperCase(Locale.ENGLISH) + associateFieldName.substring(1).
                                        toLowerCase(Locale.ENGLISH);

                                associationsMethods.append(String.format(
                                        BalSyntaxConstants.G_SHEET_METADATA_ASSOCIATIONS_METHODS_TEMPLATE,
                                        "\"" + associateFieldName + "\"", entity.getEntityName().concat(
                                                associateFieldNameCamelCase)));
                                int referenceIndex = 0;
                                StringBuilder conditionStatement = new StringBuilder();
                                for (String reference : relation.getReferences()) {
                                    if (conditionStatement.length() > 1) {
                                        conditionStatement.append(BalSyntaxConstants.AND);
                                    }
                                    conditionStatement.append(String.format(BalSyntaxConstants.CONDITION_STATEMENT,
                                            reference, entity.getKeys().get(referenceIndex).getFieldName()));
                                    referenceIndex++;
                                }
                                queryMethodStatement.put(
                                        "query" + entity.getEntityName().concat(associateFieldNameCamelCase),
                                        String.format(BalSyntaxConstants.STREAM_PARAM_INIT, assEntity.getEntityName(),
                                                associateEntityName, associateEntityNameCamelCase) +
                                        String.format(BalSyntaxConstants.G_SHEET_RETURN_STATEMENT_FOR_RELATIONAL_ENTITY,
                                                BalSyntaxConstants.EMPTY_STRING, associateEntityName + "Stream",
                                                conditionStatement));
                                break;
                            }
                        }
                    }
                }
            }
            entityMetaData.append(String.format(BalSyntaxConstants.RANGE_TEMPLATE, endRange));
            entityMetaData.append(String.format(BalSyntaxConstants.G_SHEET_METADATA_QUERY_TEMPLATE, resourceName));
            entityMetaData.append(String.format(BalSyntaxConstants.G_SHEET_METADATA_QUERY_ONE_TEMPLATE, resourceName));
            entityMetaData.append(String.format(BalSyntaxConstants.DATA_TYPE, fieldType));
            entityMetaData.append(String.format(BalSyntaxConstants.FIELD_METADATA_TEMPLATE, fieldMetaData));
            if (hasAssociationMethod) {
                entityMetaData.append(BalSyntaxConstants.COMMA_WITH_SPACE);
                entityMetaData.append(String.format(BalSyntaxConstants.IN_MEMORY_ASSOC_METHODS_TEMPLATE,
                        associationsMethods));
            } else {
                entityMetaData.append(BalSyntaxConstants.COMMA_WITH_SPACE);
                entityMetaData.append(String.format(BalSyntaxConstants.IN_MEMORY_ASSOC_METHODS_TEMPLATE, ""));
            }
            mapBuilder.append(String.format(BalSyntaxConstants.METADATA_RECORD_ELEMENT_TEMPLATE,
                    BalSyntaxUtils.getStringWithUnderScore(entity.getEntityName()), entityMetaData));
        }
        return String.format(BalSyntaxConstants.SHEET_METADATA_RECORD_TEMPLATE, mapBuilder);
    }

    private static void addFunctionBodyToPostResource(Function create, List<EntityField> primaryKeys,
                                                        String tableName, String parameterType) {
        create.addStatement(NodeParser.parseStatement(BalSyntaxConstants.G_SHEET_CLIENT_DECLARATION));
        String getPersistClientStatement = String.format(BalSyntaxConstants.GET_G_SHEET_PERSIST_CLIENT, tableName);
        create.addStatement(NodeParser.parseStatement(
                String.format(BalSyntaxConstants.LOCK_TEMPLATE, getPersistClientStatement)));

        create.addStatement(NodeParser.parseStatement(BalSyntaxConstants.G_SHEET_CREATE_SQL_RESULTS));

        create.addStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.RETURN_CREATED_KEY,
                parameterType)));
        StringBuilder filterKeys = new StringBuilder();
        for (int i = 0; i < primaryKeys.size(); i++) {
            filterKeys.append("inserted.").append(primaryKeys.get(i).getFieldName());
            if (i < primaryKeys.size() - 1) {
                filterKeys.append(BalSyntaxConstants.COMMA);
            }
        }
        if (primaryKeys.size() == 1) {
            filterKeys = new StringBuilder(BalSyntaxConstants.SELECT_WITH_SPACE + filterKeys +
                    BalSyntaxConstants.SEMICOLON);
        } else {
            filterKeys = new StringBuilder(BalSyntaxConstants.SELECT_WITH_SPACE + BalSyntaxConstants.OPEN_BRACKET +
                    filterKeys + BalSyntaxConstants.CLOSE_BRACKET + BalSyntaxConstants.SEMICOLON);
        }
        create.addStatement(NodeParser.parseStatement(filterKeys.toString()));
    }

    private String getEndRange(int index) {
        switch (index) {
            case 1:
                return "A";
            case 2:
                return "B";
            case 3:
                return "C";
            case 4:
                return "D";
            case 5:
                return "E";
            case 6:
                return "F";
            case 7:
                return "G";
            case 8:
                return "H";
            case 9:
                return "I";
            case 10:
                return "J";
            case 11:
                return "K";
            case 12:
                return "L";
            case 13:
                return "M";
            case 14:
                return "N";
            case 15:
                return "O";
            case 16:
                return "P";
            case 17:
                return "Q";
            case 18:
                return "R";
            case 19:
                return "S";
            case 20:
                return "T";
            case 21:
                return "U";
            case 22:
                return "V";
            case 23:
                return "W";
            case 24:
                return "X";
            case 25:
                return "Y";
            case 26:
                return "Z";
            default:
                throw new IllegalStateException("Unexpected value: " + index);
        }
    }
}
