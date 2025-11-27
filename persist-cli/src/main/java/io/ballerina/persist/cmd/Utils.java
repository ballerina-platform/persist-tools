/*
 *  Copyright (c) 2024, WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 LLC. licenses this file to you under the Apache License,
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

import io.ballerina.persist.PersistToolsConstants;

import java.io.PrintStream;

public class Utils {

    public static boolean validateEagerLoading(String datastore, boolean eagerLoading, PrintStream errStream) {
        if (eagerLoading && !datastore.equals(PersistToolsConstants.SupportedDataSources.MYSQL_DB) &&
                    !datastore.equals(PersistToolsConstants.SupportedDataSources.MSSQL_DB) &&
                    !datastore.equals(PersistToolsConstants.SupportedDataSources.POSTGRESQL_DB) &&
                    !datastore.equals(PersistToolsConstants.SupportedDataSources.H2_DB)) {
            errStream.printf("WARNING: The --eager-loading flag is only supported for SQL datastores " +
                            "(mysql, mssql, postgresql, h2). This flag will be ignored for the '%s' datastore.%n",
                    datastore);
            return false;
        }
        return eagerLoading;
    }
}
