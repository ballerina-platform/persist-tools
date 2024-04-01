/*
 *  Copyright (c) 2023, WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
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
package io.ballerina.persist.models;

import java.util.List;

/**
 * Foreign Key class.
 * @param name name of the foreign key
 * @param columnNames list of columns taking part in the foreign key constraint
 * @param referenceTable reference table
 * @param referenceColumns list of key columns in the reference table
 * @since 0.4.0
 */
public record ForeignKey(String name, List<String> columnNames, String referenceTable, List<String> referenceColumns) {

}
