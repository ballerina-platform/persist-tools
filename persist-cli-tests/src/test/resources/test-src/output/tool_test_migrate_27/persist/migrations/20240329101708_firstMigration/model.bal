// Copyright (c) 2024 WSO2 LLC. (http://www.wso2.com).
//
// WSO2 LLC. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

import ballerina/persist as _;
import ballerinax/persist.sql;

public type TypeExistingFieldTest record {|
    readonly int id;
    string charAnnot;
    string varcharAnnot;
    decimal decimalAnnot;
|};

public type TypeNewFieldTest record {|
    readonly int id;
|};

public type TypeRemoveTest record {|
    readonly int id;
    @sql:Char {length:10}
    string charAnnot;
    @sql:Varchar {length:10}
    string varcharAnnot;
    @sql:Decimal {precision:[10,2]}
    decimal decimalAnnot;
|};

public type TypeChangeTest record {|
    readonly int id;
    @sql:Char {length:10}
    string charAnnot;
    @sql:Varchar {length:10}
    string varcharAnnot;
    @sql:Decimal {precision:[10,2]}
    decimal decimalAnnot;
    @sql:Char {length:10}
    string charToVarchar;
    @sql:Varchar {length:10}
    string varcharToChar;
|};
