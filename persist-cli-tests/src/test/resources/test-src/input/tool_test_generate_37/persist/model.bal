// Copyright (c) 2023 WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
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

public type Profile record {|
    readonly int id;
    string name;
    boolean isAdult;
    float salary;
    decimal age;
    byte[] isRegistered;
    User owner;
|};

public type User record  {|
    readonly int id;
    string name;
    Profile? profile;
    MultipleAssociations multipleAssociations;
|};

public type Dept record  {|
    readonly int id;
    string name;
    MultipleAssociations multipleAssociations;
|};

public type Customer record  {|
    readonly int id;
    string name;
    int age;
    MultipleAssociations multipleAssociations;
|};

public type Student record  {|
    readonly int id;
    readonly string firstName;
    int age;
    string lastName;
    string nicNo;
|};

public type MultipleAssociations record {|
    readonly int id;
    string name;

    User? owner;
    Dept? dept;
    Customer? customer;
|};
