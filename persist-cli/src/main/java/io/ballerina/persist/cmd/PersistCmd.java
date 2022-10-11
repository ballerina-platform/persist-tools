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
import picocli.CommandLine;

import java.io.PrintStream;

import static io.ballerina.persist.PersistToolsConstants.COMPONENT_IDENTIFIER;


/**
 * Class to implement "persist" command for ballerina.
 *
 * @since 0.1.0
 */
@CommandLine.Command(
        name = "persist",
        description = "generate database configurations.",
        subcommands = {Init.class, Generate.class, Db.class}
        )

public class PersistCmd implements BLauncherCmd {

    private static final PrintStream errStream = System.err;

    @Override
    public void execute() {
        String commandUsageInfo = BLauncherCmd.getCommandUsageInfo(COMPONENT_IDENTIFIER);
        errStream.println(commandUsageInfo);
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
        out.append("Perform operations on Ballerina Persistent Layer").append(System.lineSeparator());
        out.append(System.lineSeparator());
    }
    
    @Override
    public void printUsage(StringBuilder stringBuilder) {
        stringBuilder.append("  ballerina " + PersistToolsConstants.COMPONENT_IDENTIFIER).
                append(System.lineSeparator());
    }
}
