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
import io.ballerina.persist.BalException;
import io.ballerina.persist.components.Client;
import io.ballerina.persist.models.Entity;
import io.ballerina.persist.models.Module;

/**
 * This interface is used to generate the syntax tree for the client.
 *
 * @since 0.3.1
 */
public interface ClientSyntax {

    NodeList<ImportDeclarationNode> getImports() throws BalException;

    NodeList<ModuleMemberDeclarationNode> getConstantVariables();

    Client getClientObject(Module entityModule, String clientName);

    FunctionDefinitionNode getInitFunction(Module entityModule);

    FunctionDefinitionNode getGetFunction(Entity entity);

    FunctionDefinitionNode getGetByKeyFunction(Entity entity);

    FunctionDefinitionNode getCloseFunction();

    FunctionDefinitionNode getPostFunction(Entity entity);

    FunctionDefinitionNode getPutFunction(Entity entity);

    FunctionDefinitionNode getDeleteFunction(Entity entity);

    FunctionDefinitionNode getQueryNativeSQLFunction();

    FunctionDefinitionNode getExecuteNativeSQLFunction();
}
