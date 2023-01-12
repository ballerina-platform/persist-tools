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

import ballerina/io;

public function main() returns error? {
    UserClient ownerClient = check new ();
    ProfileClient profileClient = check new ();
    MultipleAssociationsClient maClient = check new ();

    _ = check ownerClient->create({
        id: 1,
        name: "TestOwner"
    });

    _ = check ownerClient->create({
        id: 3,
        name: "TestOwner"
    });

    User owner = check ownerClient->readByKey(3);

    _ = check profileClient->create({
        id: 1,
        name: "TestProfile2",
        owner: {
            id: 10,
            name: "TestOwner"
        }
    });
    _ = check profileClient->readByKey(1, ["owner"]);
    _ = check profileClient->create({
        id: 3,
        name: "TestProfile"
    });
    Profile profile2 = check profileClient->readByKey(3);
    _ = check profileClient->create({
        id: 2,
        name: "TestProfile",
        owner: {
            id: 6,
            name: "TestOwner"
        }
    });

    _ = check profileClient->create({
        id: 24,
        name: "TestProfile",
        owner: {
            id: 23,
            name: "TestOwner"
        }
    });
    owner = check ownerClient->readByKey(23, ["profile"]);

    Profile profile = check profileClient->create({
        id: 5,
        name: "TestProfile",
        owner: {
            id: 4,
            name: "TestOwner"
        }
    });

    profile.name = "TestUpdatedProfile";
    profile.owner.name = "TestUpdatedOwner";
    _ = check profileClient->update(profile);
    Profile profile4 = {
        id: 6,
        name: "TestUpdatedProfile",
        owner: {
            id: 4,
            name: "TestUpdatedOwner"
        }
    };
     _ = check profileClient->update(profile4);
    MultipleAssociations ma = {
        id: 1,
        name: "TestMultipleAssociation",
        profile: {
            id: 31,
            name: "Test Profile"
        },
        owner: {
            id: 31,
            name: "TestOwner"
        }
    };

    MultipleAssociations ma2 = check maClient->create(ma);
    ma2.name = "updatedTestMultipleAssociation";
    _ = check maClient->update(ma2);
    MultipleAssociations ma3 = check maClient->readByKey(1, ["profile", "owner"]);
    MultipleAssociations ma4 = check maClient->readByKey(1, ["profile"]);
    MultipleAssociations ma5 = check maClient->readByKey(1, ["owner"]);

    io:println("\n========== Owner ==========");
    _ = check from User ownerItem in ownerClient->read()
        do {
            io:println(ownerItem);
        };

    io:println("\n========== Profile ==========");
    _ = check from Profile profileItem in profileClient->read()
        do {
            io:println(profileItem);
        };
    
    io:println("\n========== MultipleAssociations ==========");
    _ = check from MultipleAssociations maItem in maClient->read()
        do {
            io:println(maItem);
        };
}
