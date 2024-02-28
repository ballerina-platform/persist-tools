/*
 *  Copyright (c) 2024 WSO2 LLC. (http://www.wso2.com) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package io.ballerina.persist.nodegenerator.syntax.sources;


import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.persist.BalException;
import io.ballerina.persist.models.Module;
import io.ballerina.persist.nodegenerator.syntax.utils.BalSyntaxUtils;




public class DbModelGenSyntaxTree implements IntrospectSyntaxTree {

    @Override
    public SyntaxTree getDataModels(Module entityModule) throws BalException {
        if (!entityModule.getEntityMap().values().isEmpty()) {
            return BalSyntaxUtils.generateModelSyntaxTree(entityModule);
        }
        throw new BalException("No entities found in the database");
    }

    @Override
    public SyntaxTree createInitialDriverImportFile() {
        return BalSyntaxUtils.createDriverImportFile("mysql");
    }


}
