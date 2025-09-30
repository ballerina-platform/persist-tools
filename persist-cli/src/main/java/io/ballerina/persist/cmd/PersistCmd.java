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
 * Class to implement "persist" commands for ballerina.
 *
 * @since 0.1.0
 */
@CommandLine.Command(
        name = "persist",
        description = "generate database configurations.",
        subcommands = {Init.class, Generate.class, Push.class, Migrate.class, Add.class, Pull.class}
)

public class PersistCmd implements BLauncherCmd {

    private static final PrintStream errStream = System.err;
    private static final int EXIT_CODE_0 = 0;
    private static final int EXIT_CODE_2 = 2;
    private static final ExitHandler DEFAULT_EXIT_HANDLER = code -> Runtime.getRuntime().exit(code);

    private final ExitHandler exitHandler;

    @CommandLine.Option(names = {"-h", "--help"}, hidden = true)
    private boolean helpFlag;

    /**
     * Functional interface for handling exit behavior.
     * Public to allow test access from other packages.
     */
    @FunctionalInterface
    public interface ExitHandler {
        void exit(int code);
    }

    /**
     * Default constructor for production use.
     */
    public PersistCmd() {
        this(DEFAULT_EXIT_HANDLER);
    }

    /**
     * Constructor for testing with custom exit handler.
     * This is public to allow tests in other packages to use it.
     *
     * @param exitHandler custom exit handler (for testing)
     */
    public PersistCmd(ExitHandler exitHandler) {
        this.exitHandler = exitHandler;
    }

    private void exit(int code) {
        exitHandler.exit(code);
    }

    @Override
    public void execute() {
        if (helpFlag) {
            String commandUsageInfo = BLauncherCmd.getCommandUsageInfo(getName(), PersistCmd.class.getClassLoader());
            errStream.println(commandUsageInfo);
            exit(EXIT_CODE_0);
            return;
        }
        String commandUsageInfo = BLauncherCmd.getCommandUsageInfo(COMPONENT_IDENTIFIER,
                PersistCmd.class.getClassLoader());
        errStream.println(commandUsageInfo);
        exit(EXIT_CODE_2);
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
