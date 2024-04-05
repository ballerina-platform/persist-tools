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

public type Test1 record {|
    readonly int id;
    @sql:Name {value: "field_1"}
    string field1;
    string field2;
    @sql:Name {value: "field_3"}
    string fieldT3;
    @sql:Name {value: "fieldT_4"}
    string field4;
    @sql:Name {value: "field_6"}
    string field6;
|};

public type Test2 record {|
    readonly string id;
    @sql:Name {value: "field_1"}
    string field1;
|};

public type Test3 record {|
    readonly int id;
    @sql:Name {value: "test_nic"}
    readonly string nic;
|};

public type Test4 record {|
    @sql:Name {value: "id4"}
    readonly int id;
|};

public type Test5 record {|
    readonly int id;
    readonly string nic;
|};

public type Test6 record {|
    @sql:Name {value: "id6"}
    readonly int idd;
    readonly string nic;
|};

public type Test7 record {|
    @sql:Name {value: "idd"}
    readonly int id;
    readonly string nic;
|};

public type Test8 record {|
    readonly string nic;
|};
