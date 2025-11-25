/*
 * Copyright (c) 2023, WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
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

package io.ballerina.persist.components;

import io.ballerina.compiler.syntax.tree.Node;
import io.ballerina.compiler.syntax.tree.NodeFactory;
import io.ballerina.compiler.syntax.tree.NodeList;
import io.ballerina.persist.nodegenerator.syntax.constants.SyntaxTokenConstants;

import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public class ClientResource {

    private final List<Node> metadata = new ArrayList<>();

    private NodeList<Node> functions = NodeFactory.createEmptyNodeList();

    public ClientResource() {
    }

    public void addFunction(Node function, boolean newLine) {
        if (newLine) {
            functions = functions.add(SyntaxTokenConstants.SYNTAX_TREE_BLANK_LINE);
        }
        functions = functions.add(function);
    }

    public NodeList<Node> getFunctions() {
        return functions;
    }

    public List<Node> getMetadata() {
        return metadata;
    }
}
