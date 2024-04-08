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
import ballerina/http;
import ballerina/persist;
import redis_cache.entities;

service /user_profile on new http:Listener(9090) {
    private final entities:Client dbClient;

    // Initialize the service
    function init() returns error? {
        self.dbClient = check new ();
    }

    // Define the resource to handle POST requests for users
    resource function post users(entities:User user) returns http:InternalServerError & readonly|http:Created & readonly|http:Conflict & readonly {
        int[]|persist:Error result = self.dbClient->/users.post([user]);
        if result is persist:Error {
            if result is persist:AlreadyExistsError {
                return http:CONFLICT;
            }
            return http:INTERNAL_SERVER_ERROR;
        }
        return http:CREATED;
    }

    resource function get users/[int id]() returns (http:NotFound & readonly)|entities:User|persist:Error {
        entities:User|persist:Error user = self.dbClient->/users/[id];
        if user is persist:Error {
            if user is persist:NotFoundError {
                return http:NOT_FOUND;
            }
            return user1;
        }
        return user;
    }
}

public enum Gender {
    MALE,
    FEMALE
}

entities:User user1 = {
    id: 1,
    name: "John",
    age: 23,
    email: "john@doe.com",
    gender: MALE,
    isMarried: false
};
