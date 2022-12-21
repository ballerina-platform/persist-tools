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
public type Profile record  {|
    //comment1
    readonly int id;
    //comment2
    string name;
    //comment4
    @persist:Relation {keyColumns: ["userId"], reference: ["id"]}
    User user?;
|};

@persist:Entity {
    key: ["id"],
    tableName: "Users"
}
public type User record  {|
    //comment5
    readonly int id;
    string name;
    //comment6
    Profile profile?;
|};
//comment7
@persist:Entity {
    key: ["id"],
    tableName: "MultipleAssociations"
}
//comment8
public type MultipleAssociations record {|
    //comment9
    readonly int id;
    string name;
    //comment10
    @persist:Relation {keyColumns: ["profileId"], reference: ["id"]}
    //comment11
    Profile profile?;
    //comment8
    @persist:Relation {keyColumns: ["userId"], reference: ["id"]}
    User user?;
|};



