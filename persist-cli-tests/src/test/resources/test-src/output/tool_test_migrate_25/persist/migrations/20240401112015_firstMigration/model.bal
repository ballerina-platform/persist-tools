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
    readonly string nic;
    @sql:Name {value: "user_name"}
    string name;
    @sql:Name {value: "user_age"}
    int age;
    float salary;
    boolean isEmployed;
|};

@sql:Name {value: "test_2"}
public type Test2 record {|
    readonly string nic;
    @sql:Name {value: "user_name"}
    string name;
    @sql:Name {value: "user_age"}
    int age;
    float salary;
    boolean isEmployed;
|};

@sql:Name {value: "test_3"}
public type Test3 record {|
    readonly string nic;
    @sql:Name {value: "user_name"}
    string name;
    @sql:Name {value: "user_age"}
    int age;
    float salary;
    boolean isEmployed;
|};

@sql:Name {value: "test_4"}
public type Test4 record {|
    readonly string nic;
    @sql:Name {value: "user_name"}
    string name;
    @sql:Name {value: "user_age"}
    int age;
    float salary;
    boolean isEmployed;
|};

@sql:Name {value: "test_5"}
public type Test5 record {|
    readonly string nic;
    @sql:Name {value: "user_name"}
    string name;
    @sql:Name {value: "user_age"}
    int age;
    float salary;
    boolean isEmployed;
|};

@sql:Name {value: "test_7"}
public type Test7 record {|
    readonly string nic;
    @sql:Name {value: "user_name"}
    string name;
    @sql:Name {value: "user_age"}
    int age;
    float salary;
    boolean isEmployed;
|};

@sql:Name {value: "test_8"}
public type Test8 record {|
    readonly string nic;
    @sql:Name {value: "user_name"}
    string name;
    @sql:Name {value: "user_age"}
    int age;
    float salary;
    boolean isEmployed;
|};
