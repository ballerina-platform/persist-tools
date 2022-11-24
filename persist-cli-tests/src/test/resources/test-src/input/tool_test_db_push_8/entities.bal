// Copyright (c) 2022 WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
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

import ballerina/persist;

@persist:Entity {
    key: ["id"],
    tableName: "Profiles"
}
public type Profile record {|
    @persist:AutoIncrement{startValue: 10}
    readonly int id;
    string name;
    boolean isAdult;
    float salary;
    decimal age;
|};

@persist:Entity {
    key: ["id"],
    tableName: "Users"
}
public type User record  {|
    readonly int id;
    string name;
    @persist:Relation
    Profile profile?;
|};

@persist:Entity {
    key: ["id"]
}
public type Dept record  {|
    readonly int id;
    string name;
|};

@persist:Entity {
    key: ["id"],
    uniqueConstraints: [["age", "name"]]
}
public type Customer record  {|
    readonly int id;
    string name;
    int age;
|};

@persist:Entity {
    key: ["id", "firstName"],
    uniqueConstraints: [["age", "lastName"], ["nicNo"]]
}
public type Student record  {|
    readonly int id;
    readonly string firstName;
    int age;
    string lastName;
    string nicNo;
|};

@persist:Entity {
    key: ["id"],
    tableName: "MultipleAssociations"
}
public type MultipleAssociations record {|
    readonly int id;
    string name;

    @persist:Relation{}
    User user?;

    @persist:Relation{keyColumns: ["deptId"], onDelete: persist:SET_DEFAULT}
    Dept dept?;

    @persist:Relation{reference: ["id"], onUpdate: "SET_DEFAULT"}
    Customer customer?;
|};
