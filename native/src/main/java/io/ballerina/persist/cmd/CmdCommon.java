/*
 *  Copyright (c) 2022, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package io.ballerina.persist.cmd;

import io.ballerina.cli.BLauncherCmd;
import io.ballerina.persist.PersistToolsConstants;
import io.ballerina.projects.ProjectEnvironmentBuilder;
import picocli.CommandLine;

/**
 * Common class to implement "persist" command for ballerina.
 */

public class CmdCommon implements BLauncherCmd {

    public String sourcePath = "";
    public ProjectEnvironmentBuilder projectEnvironmentBuilder;

    @Override
    public void execute() {
    }
    @Override
    public void setParentCmdParser(CommandLine parentCmdParser) {
    }
    @Override
    public String getName() {
        return PersistToolsConstants.COMPONENT_IDENTIFIER;
    }

    @Override
    public void printLongDesc(StringBuilder out) {
    }

    @Override
    public void printUsage(StringBuilder stringBuilder) {
    }

    public void setSourcePath(String sourceDir) {
        this.sourcePath = sourceDir;
    }

    public void setEnvironmentBuilder(ProjectEnvironmentBuilder projectEnvironmentBuilder) {
        this.projectEnvironmentBuilder = projectEnvironmentBuilder;
    }
}
