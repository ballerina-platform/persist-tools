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
package io.ballerina.persist.components.syntax;

import io.ballerina.compiler.syntax.tree.ImportDeclarationNode;
import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;
import io.ballerina.compiler.syntax.tree.Node;
import io.ballerina.compiler.syntax.tree.NodeList;
import io.ballerina.compiler.syntax.tree.NodeParser;
import io.ballerina.compiler.syntax.tree.SyntaxKind;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.persist.BalException;
import io.ballerina.persist.PersistToolsConstants;
import io.ballerina.persist.components.Client;
import io.ballerina.persist.components.ClientResource;
import io.ballerina.persist.components.Function;
import io.ballerina.persist.components.IfElse;
import io.ballerina.persist.components.TypeDescriptor;
import io.ballerina.persist.models.Entity;
import io.ballerina.persist.models.EntityField;
import io.ballerina.persist.models.Module;
import io.ballerina.persist.models.Relation;
import io.ballerina.persist.nodegenerator.BalSyntaxConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class is used to generate the DB client syntax tree.
 *
 * @since 0.3.1
 */
public class DbClient implements SyntaxGenerator {

    private final Module entityModule;

    public DbClient(Module entityModule) {
        this.entityModule = entityModule;
    }

    @Override
    public SyntaxTree getClientSyntax() throws BalException {
        NodeList<ImportDeclarationNode> imports = CommonSyntax.generateImport(entityModule);
        imports = imports.add(Utils.getImportDeclarationNode(BalSyntaxConstants.KEYWORD_BALLERINAX,
                PersistToolsConstants.SupportDataSources.MYSQL_DB, null));

        NodeList<ModuleMemberDeclarationNode> moduleMembers = CommonSyntax.generateConstantVariables(entityModule);

        Client clientObject = generateClient(entityModule);
        moduleMembers = moduleMembers.add(clientObject.getClassDefinitionNode());
        return CommonSyntax.generateSyntaxTree(imports, moduleMembers);
    }

    @Override
    public Client generateClient(Module entityModule) throws BalException {
        Client clientObject = CommonSyntax.generateClientSignature();
        clientObject.addMember(NodeParser.parseObjectMember(BalSyntaxConstants.INIT_DB_CLIENT), true);
        clientObject.addMember(NodeParser.parseObjectMember(BalSyntaxConstants.INIT_DB_CLIENT_MAP), true);
        clientObject.addMember(generateMetadataRecord(entityModule), true);

        Collection<Entity> entityArray = entityModule.getEntityMap().values();
        if (entityArray.size() == 0) {
            throw new BalException("data definition file() does not contain any entities.");
        }
        Function init = generateInitFunction(entityModule);
        clientObject.addMember(init.getFunctionDefinitionNode(), true);
        List<ClientResource> resourceList = new ArrayList<>();
        for (Entity entity : entityArray) {
            resourceList.add(generateClientResource(entity));
        }
        resourceList.forEach(resource -> {
            resource.getFunctions().forEach(function -> {
                clientObject.addMember(function, false);
            });
        });
        clientObject.addMember(generateCloseFunction().getFunctionDefinitionNode(), true);
        return clientObject;
    }

    @Override
    public Function generateInitFunction(Module entityModule) {
        Function init = new Function(BalSyntaxConstants.INIT, SyntaxKind.OBJECT_METHOD_DEFINITION);
        init.addQualifiers(new String[] { BalSyntaxConstants.KEYWORD_PUBLIC });
        init.addReturns(TypeDescriptor.getOptionalTypeDescriptorNode(BalSyntaxConstants.EMPTY_STRING,
                BalSyntaxConstants.PERSIST_ERROR));
        init.addStatement(NodeParser.parseStatement(BalSyntaxConstants.INIT_DB_CLIENT_WITH_PARAMS));
        IfElse errorCheck = new IfElse(NodeParser.parseExpression(String.format(
                BalSyntaxConstants.RESULT_IS_BALLERINA_ERROR, BalSyntaxConstants.DB_CLIENT)));
        errorCheck.addIfStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.RETURN_ERROR,
                BalSyntaxConstants.DB_CLIENT)));
        init.addIfElseStatement(errorCheck.getIfElseStatementNode());
        init.addStatement(NodeParser.parseStatement(BalSyntaxConstants.ADD_CLIENT));
        StringBuilder persistClientMap = new StringBuilder();
        for (Entity entity : entityModule.getEntityMap().values()) {
            if (persistClientMap.length() != 0) {
                persistClientMap.append(BalSyntaxConstants.COMMA_WITH_NEWLINE);
            }
            persistClientMap.append(String.format(BalSyntaxConstants.PERSIST_CLIENT_MAP_ELEMENT,
                    Utils.getEntityNameConstant(entity.getEntityName()),
                    Utils.getEntityNameConstant(entity.getEntityName())));
        }
        init.addStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.PERSIST_CLIENT_TEMPLATE,
                persistClientMap)));
        return init;
    }

    @Override
    public ClientResource generateClientResource(Entity entity) {
        String className = "MySQLProcessor";
        ClientResource resource  = CommonSyntax.generateClientResource(entity, className);

        Function create = generatePostFunction(entity);
        resource.addFunction(create.getFunctionDefinitionNode(), true);

        Function update = generatePutFunction(entity);
        resource.addFunction(update.getFunctionDefinitionNode(), true);

        Function delete = generateDeleteFunction(entity);
        resource.addFunction(delete.getFunctionDefinitionNode(), true);
        return resource;
    }

    @Override
    public Function generateCloseFunction() {
        Function close = CommonSyntax.generateCloseFunction();
        close.addStatement(NodeParser.parseStatement(BalSyntaxConstants.PERSIST_CLIENT_CLOSE_STATEMENT));
        IfElse errorCheck = new IfElse(NodeParser.parseExpression(String.format(
                BalSyntaxConstants.RESULT_IS_BALLERINA_ERROR, BalSyntaxConstants.RESULT)));
        errorCheck.addIfStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.RETURN_ERROR,
                BalSyntaxConstants.RESULT)));
        close.addIfElseStatement(errorCheck.getIfElseStatementNode());
        close.addStatement(NodeParser.parseStatement(BalSyntaxConstants.RETURN_RESULT));
        return close;
    }

    @Override
    public Function generatePostFunction(Entity entity) {
        String parameterType = String.format(BalSyntaxConstants.INSERT_RECORD, entity.getEntityName());
        List<EntityField> primaryKeys = entity.getKeys();
        Function create = CommonSyntax.generatePostFunction(entity, primaryKeys, parameterType);
        addFunctionBodyToPostResource(create, primaryKeys, Utils.getEntityNameConstant(entity.getEntityName()),
                parameterType);
        return create;
    }

    @Override
    public Function generatePutFunction(Entity entity) {
        StringBuilder filterKeys = new StringBuilder(BalSyntaxConstants.OPEN_BRACE);
        StringBuilder path = new StringBuilder(BalSyntaxConstants.BACK_SLASH + entity.getResourceName());
        Function update = CommonSyntax.generatePutFunction(entity, filterKeys, path);
        if (entity.getKeys().size() > 1) {
            update.addStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.UPDATE_RUN_UPDATE_QUERY,
                    Utils.getEntityNameConstant(entity.getEntityName()),
                    filterKeys.substring(0, filterKeys.length() - 2).concat(BalSyntaxConstants.CLOSE_BRACE))));
        } else {
            update.addStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.UPDATE_RUN_UPDATE_QUERY,
                    Utils.getEntityNameConstant(entity.getEntityName()), entity.getKeys().stream().findFirst().get()
                            .getFieldName())));
        }
        update.addStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.UPDATE_RETURN_UPDATE_QUERY,
                path)));
        return update;
    }

    @Override
    public Function generateDeleteFunction(Entity entity) {
        StringBuilder filterKeys = new StringBuilder(BalSyntaxConstants.OPEN_BRACE);
        StringBuilder path = new StringBuilder(BalSyntaxConstants.BACK_SLASH + entity.getResourceName());
        Function delete = CommonSyntax.generateDeleteFunction(entity, filterKeys, path);
        delete.addStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.GET_OBJECT_QUERY,
                entity.getEntityName(), path)));
        if (entity.getKeys().size() > 1) {
            delete.addStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.DELETE_RUN_DELETE_QUERY,
                    Utils.getEntityNameConstant(entity.getEntityName()),
                    filterKeys.substring(0, filterKeys.length() - 2).concat(BalSyntaxConstants.CLOSE_BRACE))));
        } else {
            delete.addStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.DELETE_RUN_DELETE_QUERY,
                    Utils.getEntityNameConstant(entity.getEntityName()), entity.getKeys().stream().findFirst().get()
                            .getFieldName())));
        }
        delete.addStatement(NodeParser.parseStatement(BalSyntaxConstants.RETURN_DELETED_OBJECT));
        return delete;
    }

    private static Node generateMetadataRecord(Module entityModule) {
        StringBuilder mapBuilder = new StringBuilder();
        for (Entity entity : entityModule.getEntityMap().values()) {
            if (mapBuilder.length() != 0) {
                mapBuilder.append(BalSyntaxConstants.COMMA_WITH_NEWLINE);
            }
            StringBuilder entityMetaData = new StringBuilder();
            entityMetaData.append(String.format(BalSyntaxConstants.METADATA_RECORD_ENTITY_NAME_TEMPLATE,
                    Utils.stripEscapeCharacter(entity.getEntityName())));
            entityMetaData.append(String.format(BalSyntaxConstants.METADATA_RECORD_TABLE_NAME_TEMPLATE,
                    Utils.stripEscapeCharacter(entity.getEntityName())));
            StringBuilder fieldMetaData = new StringBuilder();
            StringBuilder associateFieldMEtaData = new StringBuilder();
            boolean relationsExists = false;
            for (EntityField field : entity.getFields()) {
                if (field.getRelation() != null) {
                    relationsExists = true;
                    StringBuilder foreignKeyFields = new StringBuilder();
                    if (field.getRelation().isOwner()) {
                        if (fieldMetaData.length() != 0) {
                            fieldMetaData.append(BalSyntaxConstants.COMMA_WITH_NEWLINE);
                        }
                        for (Relation.Key key : field.getRelation().getKeyColumns()) {
                            if (foreignKeyFields.length() != 0) {
                                foreignKeyFields.append(BalSyntaxConstants.COMMA_WITH_NEWLINE);
                            }
                            foreignKeyFields.append(String.format(BalSyntaxConstants.METADATA_RECORD_FIELD_TEMPLATE,
                                    key.getField(), Utils.stripEscapeCharacter(key.getField())));
                        }
                    }
                    fieldMetaData.append(foreignKeyFields);
                    Entity associatedEntity = field.getRelation().getAssocEntity();
                    for (EntityField associatedEntityField : associatedEntity.getFields()) {
                        if (associatedEntityField.getRelation() == null) {
                            if (associateFieldMEtaData.length() != 0) {
                                associateFieldMEtaData.append(BalSyntaxConstants.COMMA_WITH_NEWLINE);
                            }
                            associateFieldMEtaData.append(String.format((field.isArrayType() ? "\"%s[]" : "\"%s") +
                                            BalSyntaxConstants.ASSOCIATED_FIELD_TEMPLATE,
                                    field.getFieldName(),
                                    associatedEntityField.getFieldName(),
                                    Utils.stripEscapeCharacter(field.getFieldName()),
                                    Utils.stripEscapeCharacter(associatedEntityField.getFieldName())));
                        } else {
                            if (associatedEntityField.getRelation().isOwner()) {
                                for (Relation.Key key : associatedEntityField.getRelation().getKeyColumns()) {
                                    if (associateFieldMEtaData.length() != 0) {
                                        associateFieldMEtaData.append(BalSyntaxConstants.COMMA_WITH_NEWLINE);
                                    }
                                    associateFieldMEtaData.append(String.format((field.isArrayType() ?
                                                    "\"%s[]" : "\"%s") + BalSyntaxConstants.ASSOCIATED_FIELD_TEMPLATE,
                                            field.getFieldName(), key.getField(),
                                            Utils.stripEscapeCharacter(field.getFieldName()),
                                            Utils.stripEscapeCharacter(key.getField())));
                                }
                            }
                        }
                    }
                } else {
                    if (fieldMetaData.length() != 0) {
                        fieldMetaData.append(BalSyntaxConstants.COMMA_WITH_NEWLINE);
                    }
                    fieldMetaData.append(String.format(BalSyntaxConstants.METADATA_RECORD_FIELD_TEMPLATE,
                            field.getFieldName(), Utils.stripEscapeCharacter(field.getFieldName())));
                }
            }
            if (associateFieldMEtaData.length() > 1) {
                fieldMetaData.append(",");
                fieldMetaData.append(associateFieldMEtaData);
            }
            entityMetaData.append(String.format(BalSyntaxConstants.FIELD_METADATA_TEMPLATE, fieldMetaData));
            entityMetaData.append(BalSyntaxConstants.COMMA_SPACE);

            StringBuilder keyFields = new StringBuilder();
            for (EntityField key : entity.getKeys()) {
                if (keyFields.length() != 0) {
                    keyFields.append(BalSyntaxConstants.COMMA_SPACE);
                }
                keyFields.append("\"").append(Utils.stripEscapeCharacter(key.getFieldName())).append("\"");
            }
            entityMetaData.append(String.format(BalSyntaxConstants.METADATA_RECORD_KEY_FIELD_TEMPLATE, keyFields));
            if (relationsExists) {
                entityMetaData.append(BalSyntaxConstants.COMMA_SPACE);
                String joinMetaData = getJoinMetaData(entity);
                entityMetaData.append(String.format(BalSyntaxConstants.JOIN_METADATA_TEMPLATE, joinMetaData));
            }

            mapBuilder.append(String.format(BalSyntaxConstants.METADATA_RECORD_ELEMENT_TEMPLATE,
                    Utils.getEntityNameConstant(entity.getEntityName()), entityMetaData));
        }
        return NodeParser.parseObjectMember(String.format(BalSyntaxConstants.METADATA_RECORD_TEMPLATE, mapBuilder));
    }

    private static String getJoinMetaData(Entity entity) {
        StringBuilder joinMetaData = new StringBuilder();
        for (EntityField entityField : entity.getFields()) {
            StringBuilder refColumns = new StringBuilder();
            StringBuilder joinColumns = new StringBuilder();
            if (entityField.getRelation() != null) {
                String relationType = "persist:ONE_TO_ONE";
                Entity associatedEntity = entityField.getRelation().getAssocEntity();
                for (EntityField associatedEntityField : associatedEntity.getFields()) {
                    if (associatedEntityField.getFieldType().equals(entity.getEntityName())) {
                        if (associatedEntityField.isArrayType() && !entityField.isArrayType()) {
                            relationType = "persist:ONE_TO_MANY";
                        } else if (!associatedEntityField.isArrayType() && entityField.isArrayType()) {
                            relationType = "persist:MANY_TO_ONE";
                        } else if (associatedEntityField.isArrayType() && entityField.isArrayType()) {
                            relationType = "persist:MANY_TO_MANY";
                        }
                    }
                }
                if (joinMetaData.length() > 0) {
                    joinMetaData.append(BalSyntaxConstants.COMMA_WITH_NEWLINE);
                }
                for (Relation.Key key : entityField.getRelation().getKeyColumns()) {
                    if (joinColumns.length() > 0) {
                        joinColumns.append(",");
                    }
                    if (refColumns.length() > 0) {
                        refColumns.append(",");
                    }
                    refColumns.append(String.format(BalSyntaxConstants.COLUMN_ARRAY_ENTRY_TEMPLATE,
                            key.getReference()));
                    joinColumns.append(String.format(BalSyntaxConstants.COLUMN_ARRAY_ENTRY_TEMPLATE, key.getField()));
                }
                joinMetaData.append(String.format(BalSyntaxConstants.JOIN_METADATA_FIELD_TEMPLATE,
                        entityField.getFieldName(), entityField.getFieldType(),
                        entityField.getFieldName(), entityField.getFieldType(), refColumns,
                        joinColumns, relationType));
            }
        }
        return joinMetaData.toString();
    }

    private static void addFunctionBodyToPostResource(Function create, List<EntityField> primaryKeys,
                                                      String tableName, String parameterType) {
        create.addStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.CREATE_SQL_RESULTS,
                tableName)));
        create.addStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.RETURN_CREATED_KEY,
                parameterType)));
        StringBuilder filterKeys = new StringBuilder();
        for (int i = 0; i < primaryKeys.size(); i++) {
            filterKeys.append("inserted.").append(primaryKeys.get(i).getFieldName());
            if (i < primaryKeys.size() - 1) {
                filterKeys.append(",");
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
}
