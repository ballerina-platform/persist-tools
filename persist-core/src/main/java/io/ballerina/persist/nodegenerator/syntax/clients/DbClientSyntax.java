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

import io.ballerina.compiler.syntax.tree.AbstractNodeFactory;
import io.ballerina.compiler.syntax.tree.FunctionDefinitionNode;
import io.ballerina.compiler.syntax.tree.ImportDeclarationNode;
import io.ballerina.compiler.syntax.tree.ImportPrefixNode;
import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;
import io.ballerina.compiler.syntax.tree.Node;
import io.ballerina.compiler.syntax.tree.NodeFactory;
import io.ballerina.compiler.syntax.tree.NodeList;
import io.ballerina.compiler.syntax.tree.NodeParser;
import io.ballerina.compiler.syntax.tree.SyntaxKind;
import io.ballerina.compiler.syntax.tree.Token;
import io.ballerina.persist.BalException;
import io.ballerina.persist.PersistToolsConstants;
import io.ballerina.persist.components.Client;
import io.ballerina.persist.components.Function;
import io.ballerina.persist.components.IfElse;
import io.ballerina.persist.components.TypeDescriptor;
import io.ballerina.persist.models.Entity;
import io.ballerina.persist.models.EntityField;
import io.ballerina.persist.models.Module;
import io.ballerina.persist.models.Relation;
import io.ballerina.persist.nodegenerator.syntax.constants.BalSyntaxConstants;
import io.ballerina.persist.nodegenerator.syntax.constants.SyntaxTokenConstants;
import io.ballerina.persist.nodegenerator.syntax.utils.BalSyntaxUtils;

import java.util.List;
import java.util.Objects;

import static io.ballerina.persist.PersistToolsConstants.CUSTOM_SCHEMA_SUPPORTED_DB_PROVIDERS;
import static io.ballerina.persist.PersistToolsConstants.JDBC_CONNECTOR_MODULE_NAME;
import static io.ballerina.persist.PersistToolsConstants.SUPPORTED_VIA_JDBC_CONNECTOR;
import static io.ballerina.persist.nodegenerator.syntax.utils.BalSyntaxUtils.EntityType.TABLE;

/**
 * This class is used to generate the DB client syntax tree.
 *
 * @since 0.3.1
 */
public class DbClientSyntax implements ClientSyntax {

    private final Module entityModule;
    private final String dbNamePrefix;
    private final String importPackage;
    private final String importDriver;
    private final String dbSpecifics;
    private final String nativeClass;
    private final String initDbClientMethodTemplate;
    private final String dataSource;
    private final boolean eagerLoading;
    private final boolean initParams;

    public DbClientSyntax(Module entityModule, String datasource) throws BalException {
        this(entityModule, datasource, false, false);
    }

    public DbClientSyntax(Module entityModule, String datasource, boolean eagerLoading) throws BalException {
        this(entityModule, datasource, eagerLoading, false);
    }

    public DbClientSyntax(Module entityModule, String datasource, boolean eagerLoading, boolean initParams)
            throws BalException {
        this.entityModule = entityModule;
        this.dataSource = datasource;
        this.initParams = initParams;
        this.dbNamePrefix = SUPPORTED_VIA_JDBC_CONNECTOR.contains(datasource)
                ? PersistToolsConstants.SupportedDataSources.JDBC
                : datasource;
        this.importPackage = SUPPORTED_VIA_JDBC_CONNECTOR.contains(datasource) ? JDBC_CONNECTOR_MODULE_NAME
                : datasource;

        switch (datasource) {
            case PersistToolsConstants.SupportedDataSources.MYSQL_DB -> {
                this.importDriver = BalSyntaxConstants.MYSQL_DRIVER;
                this.dbSpecifics = BalSyntaxConstants.MYSQL_SPECIFICS;
                this.nativeClass = BalSyntaxConstants.MYSQL_PROCESSOR;
                this.initDbClientMethodTemplate = BalSyntaxConstants.INIT_DB_CLIENT_WITH_PARAMS;
            }
            case PersistToolsConstants.SupportedDataSources.MSSQL_DB -> {
                this.importDriver = BalSyntaxConstants.MSSQL_DRIVER;
                this.dbSpecifics = BalSyntaxConstants.MSSQL_SPECIFICS;
                this.nativeClass = BalSyntaxConstants.MSSQL_PROCESSOR;
                this.initDbClientMethodTemplate = BalSyntaxConstants.INIT_DB_CLIENT_WITH_PARAMS;
            }
            case PersistToolsConstants.SupportedDataSources.POSTGRESQL_DB -> {
                this.importDriver = BalSyntaxConstants.POSTGRESQL_DRIVER;
                this.dbSpecifics = BalSyntaxConstants.POSTGRESQL_SPECIFICS;
                this.nativeClass = BalSyntaxConstants.POSTGRESQL_PROCESSOR;
                this.initDbClientMethodTemplate = BalSyntaxConstants.POSTGRESQL_INIT_DB_CLIENT_WITH_PARAMS;
            }
            case PersistToolsConstants.SupportedDataSources.H2_DB -> {
                this.importDriver = BalSyntaxConstants.H2_DRIVER;
                this.dbSpecifics = BalSyntaxConstants.H2_SPECIFICS;
                this.nativeClass = BalSyntaxConstants.H2_PROCESSOR;
                this.initDbClientMethodTemplate = BalSyntaxConstants.JDBC_URL_INIT_DB_CLIENT_WITH_PARAMS;
            }
            default -> throw new BalException("Unsupported datasource: " + datasource);
        }
        this.eagerLoading = eagerLoading;
    }

    public NodeList<ImportDeclarationNode> getImports() throws BalException {
        NodeList<ImportDeclarationNode> imports = BalSyntaxUtils.generateImport(entityModule);
        imports = imports.add(BalSyntaxUtils.getImportDeclarationNode(BalSyntaxConstants.KEYWORD_BALLERINA,
                BalSyntaxConstants.SQL, null));
        imports = imports.add(BalSyntaxUtils.getImportDeclarationNode(BalSyntaxConstants.KEYWORD_BALLERINAX,
                importPackage, null));
        ImportPrefixNode prefix = NodeFactory.createImportPrefixNode(SyntaxTokenConstants.SYNTAX_TREE_AS,
                AbstractNodeFactory.createToken(SyntaxKind.UNDERSCORE_KEYWORD));
        imports = imports.add(BalSyntaxUtils.getImportDeclarationNode(BalSyntaxConstants.KEYWORD_BALLERINAX,
                importDriver, prefix));

        Token prefixToken = AbstractNodeFactory.createIdentifierToken("psql");
        prefix = NodeFactory.createImportPrefixNode(SyntaxTokenConstants.SYNTAX_TREE_AS, prefixToken);
        imports = imports.add(BalSyntaxUtils.getImportDeclarationNode(BalSyntaxConstants.KEYWORD_BALLERINAX,
                BalSyntaxConstants.PERSIST_MODULE + "." + BalSyntaxConstants.SQL, prefix));
        return imports;
    }

    public NodeList<ModuleMemberDeclarationNode> getConstantVariables() {
        return BalSyntaxUtils.generateConstantVariables(entityModule);
    }

    @Override
    public Client getClientObject(Module entityModule, String clientName) {
        Client clientObject = BalSyntaxUtils.generateClientSignature(clientName, true, this.dataSource);
        clientObject.addMember(NodeParser.parseObjectMember(
                String.format(BalSyntaxConstants.INIT_DB_CLIENT, this.dbNamePrefix)), true);
        clientObject.addMember(NodeParser.parseObjectMember(BalSyntaxConstants.INIT_SQL_CLIENT_MAP), true);
        clientObject.addMember(generateMetadataRecord(entityModule), true);
        return clientObject;
    }

    @Override
    public FunctionDefinitionNode getInitFunction(Module entityModule) {
        Function init = new Function(BalSyntaxConstants.INIT, SyntaxKind.OBJECT_METHOD_DEFINITION);
        init.addQualifiers(new String[] { BalSyntaxConstants.KEYWORD_PUBLIC, BalSyntaxConstants.KEYWORD_ISOLATED });
        init.addReturns(TypeDescriptor.getOptionalTypeDescriptorNode(BalSyntaxConstants.EMPTY_STRING,
                BalSyntaxConstants.PERSIST_ERROR));

        if (initParams) {
            // Add individual parameters based on datasource type
            switch (dataSource) {
                case PersistToolsConstants.SupportedDataSources.MYSQL_DB -> {
                    init.addRequiredParameter(TypeDescriptor.getBuiltinSimpleNameReferenceNode("string"), "host");
                    init.addRequiredParameter(TypeDescriptor.getBuiltinSimpleNameReferenceNode("int"), "port");
                    init.addRequiredParameter(TypeDescriptor.getBuiltinSimpleNameReferenceNode("string"), "user");
                    init.addRequiredParameter(
                        TypeDescriptor.getBuiltinSimpleNameReferenceNode("string"), "password");
                    init.addRequiredParameter(
                        TypeDescriptor.getBuiltinSimpleNameReferenceNode("string"), "database");
                    init.addDefaultableParameter(
                        TypeDescriptor.getQualifiedNameReferenceNode(this.dbNamePrefix, "Options"),
                        "connectionOptions",
                        NodeParser.parseExpression("{}"));
                }
                case PersistToolsConstants.SupportedDataSources.POSTGRESQL_DB,
                     PersistToolsConstants.SupportedDataSources.MSSQL_DB -> {
                    init.addRequiredParameter(TypeDescriptor.getBuiltinSimpleNameReferenceNode("string"), "host");
                    init.addRequiredParameter(TypeDescriptor.getBuiltinSimpleNameReferenceNode("int"), "port");
                    init.addRequiredParameter(TypeDescriptor.getBuiltinSimpleNameReferenceNode("string"), "user");
                    init.addRequiredParameter(
                        TypeDescriptor.getBuiltinSimpleNameReferenceNode("string"), "password");
                    init.addRequiredParameter(
                        TypeDescriptor.getBuiltinSimpleNameReferenceNode("string"), "database");
                    init.addDefaultableParameter(
                        TypeDescriptor.getQualifiedNameReferenceNode(this.dbNamePrefix, "Options"),
                        "connectionOptions",
                        NodeParser.parseExpression("{}"));
                    init.addDefaultableParameter(
                        TypeDescriptor.getOptionalTypeDescriptorNode("", "string"),
                        "defaultSchema",
                        NodeParser.parseExpression("()"));
                }
                case PersistToolsConstants.SupportedDataSources.H2_DB -> {
                    init.addRequiredParameter(TypeDescriptor.getBuiltinSimpleNameReferenceNode("string"), "url");
                    init.addDefaultableParameter(
                        TypeDescriptor.getOptionalTypeDescriptorNode("", "string"),
                        "user",
                        NodeParser.parseExpression("()"));
                    init.addDefaultableParameter(
                        TypeDescriptor.getOptionalTypeDescriptorNode("", "string"),
                        "password",
                        NodeParser.parseExpression("()"));
                    init.addDefaultableParameter(
                        TypeDescriptor.getQualifiedNameReferenceNode(this.dbNamePrefix, "Options"),
                        "connectionOptions",
                        NodeParser.parseExpression("{}"));
                }
                default -> throw new IllegalStateException("Unsupported datasource: " + dataSource);
            }
            init.addStatement(NodeParser.parseStatement(String.format(this.initDbClientMethodTemplate,
                    this.dbNamePrefix)));
        } else {
            // Original behavior with configurables
            init.addStatement(NodeParser.parseStatement(String.format(this.initDbClientMethodTemplate,
                    this.dbNamePrefix)));
        }
        IfElse errorCheck = new IfElse(NodeParser.parseExpression(String.format(
                BalSyntaxConstants.RESULT_IS_BALLERINA_ERROR, BalSyntaxConstants.DB_CLIENT)));
        errorCheck.addIfStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.RETURN_ERROR,
                BalSyntaxConstants.DB_CLIENT)));
        init.addIfElseStatement(errorCheck.getIfElseStatementNode());
        init.addStatement(NodeParser.parseStatement(BalSyntaxConstants.ADD_CLIENT));

        if (CUSTOM_SCHEMA_SUPPORTED_DB_PROVIDERS.contains(dataSource)) {
            init.addStatement(NodeParser.parseStatement(BalSyntaxConstants.COMMENT_METADATA_UPDATE_SCHEMA_NAME));
            IfElse schemaCheck = new IfElse(NodeParser.parseExpression(String.format(
                    BalSyntaxConstants.NOT_NIL_CHECK, BalSyntaxConstants.DEFAULT_SCHEMA)));
            schemaCheck.addIfStatement(NodeParser.parseStatement(BalSyntaxConstants.LOCK));
            schemaCheck.addIfStatement(NodeParser.parseStatement(BalSyntaxConstants.OPEN_BRACE));
            schemaCheck.addIfStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.FOREACH_METADATA,
                    BalSyntaxConstants.SELF_METADATA)));
            schemaCheck.addIfStatement(NodeParser.parseStatement(BalSyntaxConstants.GET_METADATA_VALUE_FOR_KEY));
            IfElse schemaNameCheck = new IfElse(NodeParser.parseExpression(String.format(
                    BalSyntaxConstants.NIL_CHECK, BalSyntaxConstants.METADATA_SCHEMA)));
            schemaNameCheck.addIfStatement(NodeParser.parseStatement(String.format(
                    BalSyntaxConstants.UPDATE_SCHEMA_NAME, BalSyntaxConstants.METADATA_SCHEMA,
                    BalSyntaxConstants.DEFAULT_SCHEMA)));
            schemaCheck.addIfStatement(schemaNameCheck.getIfElseStatementNode());
            schemaCheck.addIfStatement(NodeParser.parseStatement(BalSyntaxConstants.GET_JOIN_METADATA));
            IfElse joinMetadataCheck = new IfElse(NodeParser.parseExpression(String.format(
                    BalSyntaxConstants.NIL_CHECK, BalSyntaxConstants.JOIN_METADATA)));
            joinMetadataCheck.addIfStatement(NodeParser.parseStatement("continue;"));
            schemaCheck.addIfStatement(joinMetadataCheck.getIfElseStatementNode());
            schemaCheck.addIfStatement(NodeParser.parseStatement(String.format(
                    BalSyntaxConstants.FOREACH_JOIN_METADATA, BalSyntaxConstants.JOIN_METADATA)));
            IfElse joinSchemaNameCheck = new IfElse(NodeParser.parseExpression(String.format(
                    BalSyntaxConstants.NIL_CHECK, BalSyntaxConstants.JOIN_METADATA_REF_SCHEMA)));
            joinSchemaNameCheck.addIfStatement(NodeParser.parseStatement(String.format(
                    BalSyntaxConstants.UPDATE_SCHEMA_NAME, BalSyntaxConstants.JOIN_METADATA_REF_SCHEMA,
                    BalSyntaxConstants.DEFAULT_SCHEMA)));
            schemaCheck.addIfStatement(joinSchemaNameCheck.getIfElseStatementNode());
            schemaCheck.addIfStatement(NodeParser.parseStatement(BalSyntaxConstants.CLOSE_BRACE));
            schemaCheck.addIfStatement(NodeParser.parseStatement(BalSyntaxConstants.CLOSE_BRACE));
            init.addStatement(schemaCheck.getIfElseStatementNode());
            init.addStatement(NodeParser.parseStatement(BalSyntaxConstants.CLOSE_BRACE));
        }
        StringBuilder persistClientMap = new StringBuilder();
        for (Entity entity : entityModule.getEntityMap().values()) {
            if (entity.containsUnsupportedTypes()) {
                continue;
            }
            if (persistClientMap.length() != 0) {
                persistClientMap.append(BalSyntaxConstants.COMMA_WITH_NEWLINE);
            }
            String constantName = BalSyntaxUtils
                    .stripEscapeCharacter(BalSyntaxUtils.getStringWithUnderScore(entity.getEntityName()));
            if (CUSTOM_SCHEMA_SUPPORTED_DB_PROVIDERS.contains(dataSource)) {
                persistClientMap.append(String.format(BalSyntaxConstants.PERSIST_CLIENT_MAP_ELEMENT_WITH_SCHEMA,
                        constantName, constantName, this.dbSpecifics));
            } else {
                persistClientMap.append(String.format(BalSyntaxConstants.PERSIST_CLIENT_MAP_ELEMENT,
                        constantName, constantName, this.dbSpecifics));
            }
        }
        init.addStatement(NodeParser.parseStatement(String.format(
                BalSyntaxConstants.PERSIST_CLIENT_TEMPLATE, persistClientMap)));
        return init.getFunctionDefinitionNode();
    }

    @Override
    public FunctionDefinitionNode getGetFunction(Entity entity) {
        String template = this.eagerLoading ? BalSyntaxConstants.EXTERNAL_SQL_GET_METHOD_LIST_TEMPLATE
                : BalSyntaxConstants.EXTERNAL_SQL_GET_METHOD_TEMPLATE;
        FunctionDefinitionNode functionNode = (FunctionDefinitionNode) NodeParser.parseObjectMember(
                String.format(template,
                        entity.getClientResourceName(),
                        entity.getEntityName(), BalSyntaxConstants.SQL, this.nativeClass));

        String doc = BalSyntaxUtils.createGetResourceDocumentation(entity, TABLE, true);
        return BalSyntaxUtils.addDocumentationToFunction(functionNode, doc);
    }

    @Override
    public FunctionDefinitionNode getGetByKeyFunction(Entity entity) {
        FunctionDefinitionNode functionNode = BalSyntaxUtils.generateGetByKeyFunction(entity, this.nativeClass,
                BalSyntaxConstants.SQL);

        String doc = BalSyntaxUtils.createGetByKeyResourceDocumentation(entity, TABLE);
        return BalSyntaxUtils.addDocumentationToFunction(functionNode, doc);
    }

    @Override
    public FunctionDefinitionNode getCloseFunction() {
        Function close = BalSyntaxUtils.generateCloseFunction();
        String doc = BalSyntaxUtils.createCloseMethodDocumentation();
        close.addDocumentation(BalSyntaxUtils.createMarkdownDocumentationNode(doc));
        close.addStatement(NodeParser.parseStatement(BalSyntaxConstants.PERSIST_CLIENT_CLOSE_STATEMENT));
        IfElse errorCheck = new IfElse(NodeParser.parseExpression(String.format(
                BalSyntaxConstants.RESULT_IS_BALLERINA_ERROR, BalSyntaxConstants.RESULT)));
        errorCheck.addIfStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.RETURN_ERROR,
                BalSyntaxConstants.RESULT)));
        close.addIfElseStatement(errorCheck.getIfElseStatementNode());
        close.addStatement(NodeParser.parseStatement(BalSyntaxConstants.RETURN_RESULT));
        return close.getFunctionDefinitionNode();
    }

    @Override
    public FunctionDefinitionNode getPostFunction(Entity entity) {
        String parameterType = String.format(BalSyntaxConstants.INSERT_RECORD, entity.getEntityName());
        List<EntityField> primaryKeys = entity.getKeys();
        Function create = BalSyntaxUtils.generatePostFunction(entity, primaryKeys, parameterType);
        String doc = BalSyntaxUtils.createPostResourceDocumentation(entity, TABLE);
        create.addDocumentation(BalSyntaxUtils.createMarkdownDocumentationNode(doc));
        addFunctionBodyToPostResource(create, primaryKeys,
                BalSyntaxUtils.getStringWithUnderScore(entity.getEntityName()), parameterType);
        return create.getFunctionDefinitionNode();
    }

    @Override
    public FunctionDefinitionNode getPutFunction(Entity entity) {
        StringBuilder filterKeys = new StringBuilder(BalSyntaxConstants.OPEN_BRACE);
        StringBuilder path = new StringBuilder(BalSyntaxConstants.BACK_SLASH +
                entity.getClientResourceName());
        Function update = BalSyntaxUtils.generatePutFunction(entity, filterKeys, path);
        String doc = BalSyntaxUtils.createPutResourceDocumentation(entity, TABLE);
        update.addDocumentation(BalSyntaxUtils.createMarkdownDocumentationNode(doc));

        update.addStatement(NodeParser.parseStatement(BalSyntaxConstants.SQL_CLIENT_DECLARATION));

        String getPersistClientStatement = String.format(BalSyntaxConstants.GET_PERSIST_CLIENT,
                BalSyntaxUtils.stripEscapeCharacter(BalSyntaxUtils.getStringWithUnderScore(entity.getEntityName())));
        update.addStatement(NodeParser.parseStatement(
                String.format(BalSyntaxConstants.LOCK_TEMPLATE, getPersistClientStatement)));

        String updateStatement;
        if (entity.getKeys().size() > 1) {
            updateStatement = String.format(BalSyntaxConstants.UPDATE_RUN_UPDATE_QUERY,
                    filterKeys.substring(0, filterKeys.length() - 2).concat(BalSyntaxConstants.CLOSE_BRACE));
        } else {
            updateStatement = String.format(BalSyntaxConstants.UPDATE_RUN_UPDATE_QUERY,
                    entity.getKeys().stream().findFirst().get().getFieldName());
        }
        update.addStatement(NodeParser.parseStatement(updateStatement));

        update.addStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.UPDATE_RETURN_UPDATE_QUERY,
                path)));
        return update.getFunctionDefinitionNode();
    }

    @Override
    public FunctionDefinitionNode getDeleteFunction(Entity entity) {
        StringBuilder filterKeys = new StringBuilder(BalSyntaxConstants.OPEN_BRACE);
        StringBuilder path = new StringBuilder(BalSyntaxConstants.BACK_SLASH +
                entity.getClientResourceName());
        Function delete = BalSyntaxUtils.generateDeleteFunction(entity, filterKeys, path);
        String doc = BalSyntaxUtils.createDeleteResourceDocumentation(entity, TABLE);
        delete.addDocumentation(BalSyntaxUtils.createMarkdownDocumentationNode(doc));
        delete.addStatement(NodeParser.parseStatement(String.format(BalSyntaxConstants.GET_OBJECT_QUERY,
                entity.getEntityName(), path)));

        delete.addStatement(NodeParser.parseStatement(BalSyntaxConstants.SQL_CLIENT_DECLARATION));

        String getPersistClientStatement = String.format(BalSyntaxConstants.GET_PERSIST_CLIENT,
                BalSyntaxUtils.stripEscapeCharacter(BalSyntaxUtils.getStringWithUnderScore(entity.getEntityName())));
        delete.addStatement(NodeParser.parseStatement(
                String.format(BalSyntaxConstants.LOCK_TEMPLATE, getPersistClientStatement)));

        String deleteStatement;
        if (entity.getKeys().size() > 1) {
            deleteStatement = String.format(BalSyntaxConstants.DELETE_RUN_DELETE_QUERY,
                    filterKeys.substring(0, filterKeys.length() - 2).concat(BalSyntaxConstants.CLOSE_BRACE));
        } else {
            deleteStatement = String.format(BalSyntaxConstants.DELETE_RUN_DELETE_QUERY,
                    entity.getKeys().stream().findFirst().get().getFieldName());
        }
        delete.addStatement(NodeParser.parseStatement(deleteStatement));

        delete.addStatement(NodeParser.parseStatement(BalSyntaxConstants.RETURN_DELETED_OBJECT));
        return delete.getFunctionDefinitionNode();
    }

    public FunctionDefinitionNode getQueryNativeSQLFunction() {
        FunctionDefinitionNode functionNode = (FunctionDefinitionNode) NodeParser.parseObjectMember(
                String.format(BalSyntaxConstants.QUERY_NATIVE_SQL_METHOD_TEMPLATE, this.nativeClass));

        String doc = BalSyntaxUtils.createQueryNativeSQLDocumentation();
        return BalSyntaxUtils.addDocumentationToFunction(functionNode, doc);
    }

    public FunctionDefinitionNode getExecuteNativeSQLFunction() {
        FunctionDefinitionNode functionNode = (FunctionDefinitionNode) NodeParser.parseObjectMember(
                String.format(BalSyntaxConstants.EXECUTE_NATIVE_SQL_METHOD_TEMPLATE, this.nativeClass));

        String doc = BalSyntaxUtils.createExecuteNativeSQLDocumentation();
        return BalSyntaxUtils.addDocumentationToFunction(functionNode, doc);
    }

    private Node generateMetadataRecord(Module entityModule) {
        StringBuilder mapBuilder = new StringBuilder();
        for (Entity entity : entityModule.getEntityMap().values()) {
            if (entity.containsUnsupportedTypes()) {
                continue;
            }
            if (mapBuilder.length() != 0) {
                mapBuilder.append(BalSyntaxConstants.COMMA_WITH_NEWLINE);
            }
            StringBuilder entityMetaData = new StringBuilder();
            entityMetaData.append(String.format(BalSyntaxConstants.METADATA_RECORD_ENTITY_NAME_TEMPLATE,
                    BalSyntaxUtils.stripEscapeCharacter(entity.getEntityName())));
            entityMetaData.append(String.format(BalSyntaxConstants.METADATA_RECORD_TABLE_NAME_TEMPLATE,
                    BalSyntaxUtils.stripEscapeCharacter(entity.getTableName())));
            if (CUSTOM_SCHEMA_SUPPORTED_DB_PROVIDERS.contains(dataSource) &&
                    entity.getSchemaName() != null && !entity.getSchemaName().isEmpty()) {
                entityMetaData.append(String.format(BalSyntaxConstants.METADATA_RECORD_SCHEMA_NAME_TEMPLATE,
                        BalSyntaxUtils.stripEscapeCharacter(entity.getSchemaName())));
            }
            StringBuilder fieldMetaData = new StringBuilder();
            StringBuilder associateFieldMetaData = new StringBuilder();
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
                                    key.getField(), BalSyntaxUtils.stripEscapeCharacter(key.getColumnName())));
                        }
                    }
                    fieldMetaData.append(foreignKeyFields);
                    Entity associatedEntity = field.getRelation().getAssocEntity();
                    for (EntityField associatedEntityField : associatedEntity.getFields()) {
                        if (associatedEntityField.getRelation() == null) {
                            if (associateFieldMetaData.length() != 0) {
                                associateFieldMetaData.append(BalSyntaxConstants.COMMA_WITH_NEWLINE);
                            }
                            if (Objects.equals(associatedEntityField.getFieldName(),
                                    associatedEntityField.getFieldColumnName())) {
                                associateFieldMetaData.append(String.format((field.isArrayType() ? "\"%s[]" : "\"%s") +
                                        BalSyntaxConstants.ASSOCIATED_FIELD_TEMPLATE,
                                        BalSyntaxUtils.stripEscapeCharacter(field.getFieldName()),
                                        BalSyntaxUtils.stripEscapeCharacter(associatedEntityField.getFieldName()),
                                        BalSyntaxUtils.stripEscapeCharacter(field.getFieldName()),
                                        BalSyntaxUtils.stripEscapeCharacter(associatedEntityField.getFieldName())));
                            } else {
                                associateFieldMetaData.append(String.format((field.isArrayType() ? "\"%s[]" : "\"%s") +
                                        BalSyntaxConstants.ASSOCIATED_FIELD_TEMPLATE_MAPPED,
                                        BalSyntaxUtils.stripEscapeCharacter(field.getFieldName()),
                                        BalSyntaxUtils.stripEscapeCharacter(associatedEntityField.getFieldName()),
                                        BalSyntaxUtils.stripEscapeCharacter(field.getFieldName()),
                                        BalSyntaxUtils.stripEscapeCharacter(associatedEntityField.getFieldName()),
                                        BalSyntaxUtils
                                                .stripEscapeCharacter(associatedEntityField.getFieldColumnName())));
                            }
                        } else {
                            if (associatedEntityField.getRelation().isOwner()) {
                                for (Relation.Key key : associatedEntityField.getRelation().getKeyColumns()) {
                                    if (associateFieldMetaData.length() != 0) {
                                        associateFieldMetaData.append(BalSyntaxConstants.COMMA_WITH_NEWLINE);
                                    }
                                    if (Objects.equals(key.getField(), key.getColumnName())) {
                                        associateFieldMetaData.append(String.format(
                                                (field.isArrayType() ? "\"%s[]" : "\"%s") +
                                                        BalSyntaxConstants.ASSOCIATED_FIELD_TEMPLATE,
                                                field.getFieldName(), key.getField(),
                                                BalSyntaxUtils.stripEscapeCharacter(field.getFieldName()),
                                                BalSyntaxUtils.stripEscapeCharacter(key.getField())));
                                    } else {
                                        associateFieldMetaData.append(String.format(
                                                (field.isArrayType() ? "\"%s[]" : "\"%s") +
                                                        BalSyntaxConstants.ASSOCIATED_FIELD_TEMPLATE_MAPPED,
                                                field.getFieldName(), key.getField(),
                                                BalSyntaxUtils.stripEscapeCharacter(field.getFieldName()),
                                                BalSyntaxUtils.stripEscapeCharacter(key.getField()),
                                                BalSyntaxUtils.stripEscapeCharacter(key.getColumnName())));
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (fieldMetaData.length() != 0) {
                        fieldMetaData.append(BalSyntaxConstants.COMMA_WITH_NEWLINE);
                    }
                    if (field.isDbGenerated()) {
                        fieldMetaData.append(String.format(BalSyntaxConstants.METADATA_RECORD_FIELD_WITH_DBGEN_TEMPLATE,
                                field.getFieldName(), BalSyntaxUtils.stripEscapeCharacter(field.getFieldColumnName()),
                                "true"));
                    } else {
                        fieldMetaData.append(String.format(BalSyntaxConstants.METADATA_RECORD_FIELD_TEMPLATE,
                                field.getFieldName(), BalSyntaxUtils.stripEscapeCharacter(field.getFieldColumnName())));
                    }

                }
            }
            if (associateFieldMetaData.length() > 1) {
                fieldMetaData.append(BalSyntaxConstants.COMMA);
                fieldMetaData.append(associateFieldMetaData);
            }
            entityMetaData.append(String.format(BalSyntaxConstants.FIELD_METADATA_TEMPLATE, fieldMetaData));
            entityMetaData.append(BalSyntaxConstants.COMMA_WITH_SPACE);

            StringBuilder keyFields = new StringBuilder();
            for (EntityField key : entity.getKeys()) {
                if (keyFields.length() != 0) {
                    keyFields.append(BalSyntaxConstants.COMMA_WITH_SPACE);
                }
                keyFields.append("\"").append(BalSyntaxUtils.stripEscapeCharacter(key.getFieldName())).append("\"");
            }
            entityMetaData.append(String.format(BalSyntaxConstants.METADATA_RECORD_KEY_FIELD_TEMPLATE, keyFields));
            if (relationsExists) {
                entityMetaData.append(BalSyntaxConstants.COMMA_WITH_SPACE);
                String joinMetaData = getJoinMetaData(entity);
                entityMetaData.append(String.format(BalSyntaxConstants.JOIN_METADATA_TEMPLATE, joinMetaData));
            }

            mapBuilder.append(String.format(BalSyntaxConstants.METADATA_RECORD_ELEMENT_TEMPLATE,
                    BalSyntaxUtils.stripEscapeCharacter(
                            (BalSyntaxUtils.getStringWithUnderScore(entity.getEntityName()))),
                    entityMetaData));
        }

        Node node;
        if (CUSTOM_SCHEMA_SUPPORTED_DB_PROVIDERS.contains(dataSource)) {
            node = NodeParser.parseObjectMember(String.format(BalSyntaxConstants.METADATA_RECORD_TEMPLATE, mapBuilder));
        } else {
            node = NodeParser.parseObjectMember(String.format(BalSyntaxConstants.METADATA_RECORD_TEMPLATE_WITH_READONLY,
                    mapBuilder));
        }
        return node;
    }

    private String getJoinMetaData(Entity entity) {
        StringBuilder joinMetaData = new StringBuilder();
        for (EntityField entityField : entity.getFields()) {
            StringBuilder refColumns = new StringBuilder();
            StringBuilder joinColumns = new StringBuilder();
            if (entityField.getRelation() != null) {
                String relationType = BalSyntaxConstants.ONE_TO_ONE;
                Entity associatedEntity = entityField.getRelation().getAssocEntity();
                for (EntityField associatedEntityField : associatedEntity.getFields()) {
                    if (associatedEntityField.getFieldType().equals(entity.getEntityName())) {
                        if (associatedEntityField.isArrayType() && !entityField.isArrayType()) {
                            relationType = BalSyntaxConstants.ONE_TO_MANY;
                        } else if (!associatedEntityField.isArrayType() && entityField.isArrayType()) {
                            relationType = BalSyntaxConstants.MANY_TO_ONE;
                        } else if (associatedEntityField.isArrayType() && entityField.isArrayType()) {
                            relationType = BalSyntaxConstants.MANY_TO_MANY;
                        }
                    }
                }
                if (joinMetaData.length() > 0) {
                    joinMetaData.append(BalSyntaxConstants.COMMA_WITH_NEWLINE);
                }
                for (Relation.Key key : entityField.getRelation().getKeyColumns()) {
                    if (joinColumns.length() > 0) {
                        joinColumns.append(BalSyntaxConstants.COMMA);
                    }
                    if (refColumns.length() > 0) {
                        refColumns.append(BalSyntaxConstants.COMMA);
                    }
                    refColumns.append(String.format(BalSyntaxConstants.COLUMN_ARRAY_ENTRY_TEMPLATE,
                            BalSyntaxUtils.stripEscapeCharacter(key.getReferenceColumnName())));
                    joinColumns.append(String.format(BalSyntaxConstants.COLUMN_ARRAY_ENTRY_TEMPLATE,
                            BalSyntaxUtils.stripEscapeCharacter(key.getColumnName())));
                }
                if (CUSTOM_SCHEMA_SUPPORTED_DB_PROVIDERS.contains(dataSource) &&
                        entityField.getRelation().getAssocEntity().getSchemaName() != null &&
                        !entityField.getRelation().getAssocEntity().getSchemaName().isEmpty()) {
                    joinMetaData.append(String.format(BalSyntaxConstants.JOIN_METADATA_FIELD_TEMPLATE_WITH_SCHEMA,
                            entityField.getFieldName(), entityField.getFieldType(), entityField.getFieldName(),
                            entityField.getRelation().getAssocEntity().getSchemaName(),
                            entityField.getRelation().getAssocEntity().getTableName(), refColumns,
                            joinColumns, relationType));
                } else {
                    joinMetaData.append(String.format(BalSyntaxConstants.JOIN_METADATA_FIELD_TEMPLATE,
                            entityField.getFieldName(), entityField.getFieldType(), entityField.getFieldName(),
                            entityField.getRelation().getAssocEntity().getTableName(), refColumns,
                            joinColumns, relationType));
                }
            }
        }
        return joinMetaData.toString();
    }

    private static void addFunctionBodyToPostResource(Function create, List<EntityField> primaryKeys,
            String tableName, String parameterType) {
        create.addStatement(NodeParser.parseStatement(BalSyntaxConstants.SQL_CLIENT_DECLARATION));

        String getPersistClientStatement = String.format(BalSyntaxConstants.GET_PERSIST_CLIENT,
                BalSyntaxUtils.stripEscapeCharacter(tableName));
        create.addStatement(NodeParser.parseStatement(
                String.format(BalSyntaxConstants.LOCK_TEMPLATE, getPersistClientStatement)));

        // there can only be one auto_incremented key and it cannot be a partial key
        if (primaryKeys.get(0).isDbGenerated()) {
            create.addStatement(NodeParser.parseStatement(BalSyntaxConstants.CREATE_SQL_RESULTS_AUTO_INCREMENT));
            create.addStatement(NodeParser.parseStatement(
                    String.format(BalSyntaxConstants.RETURN_CREATED_KEY_AUTO_INCREMENT, "sql:ExecutionResult")));
            create.addStatement(NodeParser.parseStatement(String.format(
                    BalSyntaxConstants.RETURN_FILTERED_AUTO_INCREMENT_KEYS, primaryKeys.get(0).getFieldType())));

            return;
        }

        create.addStatement(NodeParser.parseStatement(BalSyntaxConstants.CREATE_SQL_RESULTS));

        create.addStatement(NodeParser.parseStatement(
                String.format(BalSyntaxConstants.RETURN_CREATED_KEY, parameterType)));
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
}
