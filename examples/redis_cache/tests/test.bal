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
import redis_cache.entities;
import ballerina/http;
import ballerina/test;
import ballerina/lang.runtime;

http:Client socialMediaEndpoint = check new ("http://localhost:9090/user_profile");

@test:Config {}
function testCreateUser() returns error? {
    entities:UserInsert user = {
        id: 1,
        name: "John",
        age: 23,
        email: "john@doe.com",
        gender: MALE,
        isMarried: false
    };
    http:Response result = check socialMediaEndpoint->/users.post(user);
    test:assertEquals(result.statusCode, 201, "Status code should be 201");
}

@test:Config {
    dependsOn: [testCreateUser]
}
function testCreateUserAlreadyExists() returns error? {
    entities:UserInsert user = {
        id: 1,
        name: "John",
        age: 23,
        email: "john@doe.com",
        gender: MALE,
        isMarried: false,
        spouseName: "Amanda"
    };
    http:Response result = check socialMediaEndpoint->/users.post(user);
    test:assertEquals(result.statusCode, 409, "Status code should be 409");
}

@test:Config {
    dependsOn: [testCreateUser]
}
function testGetUser() returns error? {
    http:Response result = check socialMediaEndpoint->/users/[user1.id];
    test:assertEquals(result.statusCode, 200, "Status code should be 200");
    test:assertEquals(result.getJsonPayload(),
            {
                "id": 1,
                "name": "John",
                "age": 23,
                "email": "john@doe.com",
                "gender": "MALE",
                "isMarried": false
            }, "User details should be returned");
}

@test:Config {
    dependsOn: [testGetUser]
}
function testGetUserWithDelay() returns error? {
    runtime:sleep(4);
    http:Response result = check socialMediaEndpoint->/users/[user1.id];
    test:assertEquals(result.statusCode, 404, "Status code should be 404");

    entities:UserInsert user = {
        id: 1,
        name: "John",
        age: 23,
        email: "john@doe.com",
        gender: MALE,
        isMarried: false
    };
    result = check socialMediaEndpoint->/users.post(user);
    test:assertEquals(result.statusCode, 201, "Status code should be 201");

    result = check socialMediaEndpoint->/users/[user1.id];
    test:assertEquals(result.statusCode, 200, "Status code should be 200");
    test:assertEquals(result.getJsonPayload(),
            {
                "id": 1,
                "name": "John",
                "age": 23,
                "email": "john@doe.com",
                "gender": "MALE",
                "isMarried": false
            }, "User details should be returned");
}
