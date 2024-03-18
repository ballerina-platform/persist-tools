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

public enum UserGender {
    MALE,
    FEMALE
}

public type User record {|
    @sql:UniqueIndex {name: "unique_user"}
    readonly int id;
    string name;
    UserGender gender;
    @sql:Index
    @sql:UniqueIndex {name: ["unique_nic", "unique_user"]}
    string nic;
    decimal? salary;
    Car[] cars;
|};

public type Car record {|
    readonly int id;
    string name;
    string model;
    @sql:Index {name: ["ownerId"]}
    int ownerId;
    @sql:Relation {keys: ["ownerId"]}
    User user;
|};
