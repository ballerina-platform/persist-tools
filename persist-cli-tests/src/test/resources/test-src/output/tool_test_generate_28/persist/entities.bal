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

import ballerina/persist as _;
//comment1
//comment2
//comment3
//comment4
public type Company record {|
    //comment5
    //comment6
    readonly int id;
    //comment7
    //comment8
    string name;
    //comment9
    //comment10
    Employee[] employees?;
|};

public type Employee record {|
    //comment13
    //comment14
    readonly int id;
    //comment15
    //comment16
    string name;
    //comment17
    //comment18
    //comment19
    //comment20
    Company company?;
|};
