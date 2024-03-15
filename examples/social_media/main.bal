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

import social_media.entities;
import ballerina/http;
import ballerina/persist;

public type User record {|
    readonly string firstName;
    readonly string lastName;
    string email;
|};

public type Post record{|
    readonly int id;
    string title;
    string content;
    record {|
        readonly string firstName;
        readonly string lastName;
        string email;
    |} user;
|};

public type Comment record {|
    readonly int id;
    string message;
    record {|
        readonly int id;
        string content;
    |} post;
|};

service /social_media on new http:Listener(9090) {
    private final entities:Client dbClient;

    // Initialize the service
    function init() returns error? {
        self.dbClient = check new ();
    }

    // Define the resource to handle POST requests for users
    resource function post users(entities:User user) returns http:InternalServerError & readonly|http:Created & readonly|http:Conflict & readonly {
        [string, string][]|persist:Error result = self.dbClient->/users.post([user]);
        if result is persist:Error {
            if result is persist:AlreadyExistsError {
                return http:CONFLICT;
            }
            return http:INTERNAL_SERVER_ERROR;
        }
        return http:CREATED;
    }

    // Define the resource to handle POST requests for posts
    resource function post posts(entities:Post post) returns http:InternalServerError & readonly|http:Created & readonly|http:Conflict & readonly {
        int[]|persist:Error result = self.dbClient->/posts.post([post]);
        if result is persist:Error {
            if result is persist:AlreadyExistsError {
                return http:CONFLICT;
            }
            return http:INTERNAL_SERVER_ERROR;
        }
        return http:CREATED;
    }

    // Define the resource to handle POST requests for comments
    resource function post comments(entities:Comment comment) returns http:InternalServerError & readonly|http:Created & readonly|http:Conflict & readonly {
        int[]|persist:Error result = self.dbClient->/comments.post([comment]);
        if result is persist:Error {
            if result is persist:AlreadyExistsError {
                return http:CONFLICT;
            }
            return http:INTERNAL_SERVER_ERROR;
        }
        return http:CREATED;
    }

    // Define the resource to handle GET requests for users
    resource function get users() returns User[]|error {
        stream<User, persist:Error?> users = self.dbClient->/users;
        return from User user in users select user;
    }

    resource function delete users/[string firstName]/[string lastName]() returns http:NoContent | http:InternalServerError {
        entities:User|persist:Error result = self.dbClient->/users/[firstName]/[lastName].delete();
        if result is persist:Error {
            return http:INTERNAL_SERVER_ERROR;
        }
        return http:NO_CONTENT;
    }

    // Define the resource to handle GET request for posts
    resource function get posts() returns Post[]|error {
        stream<Post, persist:Error?> posts = self.dbClient->/posts;
        return from Post post in posts
            select post;
    }

    // Define the resource to handle GET request for comments
    resource function get comments() returns Comment[]|error {
        stream<Comment, persist:Error?> comments = self.dbClient->/comments;
        return from Comment comment in comments
            select comment;
    }

    // Define the resource to handle GET requests for posts by user firstName and lastName
    resource function get users/[string firstName]/[string lastName]/posts() returns Post[]|error {
        stream<Post, persist:Error?> posts = self.dbClient->/posts;
        return from Post post in posts
            where post.user.firstName == firstName && post.user.lastName == lastName
            select post;
    }

    // Define the resource to handle PATCH requests for post by id
    resource function patch posts/[int id](entities:PostUpdate post) returns http:InternalServerError & readonly|http:NotFound & readonly|entities:Post {
        entities:Post|persist:Error result = self.dbClient->/posts/[id].put(post);
        if result is persist:Error {
            if result is persist:NotFoundError {
                return http:NOT_FOUND;
            }
            return http:INTERNAL_SERVER_ERROR;
        }
        return result;
    }

    // Define the resource to handle DELETE request for post by id
    resource function delete posts/[int id]() returns http:NoContent | http:InternalServerError {
        entities:Post|persist:Error result = self.dbClient->/posts/[id].delete();
        if result is persist:Error {
            return http:INTERNAL_SERVER_ERROR;
        }
        return http:NO_CONTENT;
    }

    // Define the resource to handle GET requests for comments by post id
    resource function get posts/[int id]/comments() returns Comment[]|error {
        stream<Comment, persist:Error?> comments = self.dbClient->/comments;
        return from Comment comment in comments
            where comment.post.id == id
            select comment;
    }

    // Define the resource to handle PATCH requests for comment by id
    resource function patch comments/[int id](entities:CommentUpdate comment) returns http:InternalServerError & readonly|http:NotFound & readonly|entities:Comment {
        entities:Comment|persist:Error result = self.dbClient->/comments/[id].put(comment);
        if result is persist:Error {
            if result is persist:NotFoundError {
                return http:NOT_FOUND;
            }
            return http:INTERNAL_SERVER_ERROR;
        }
        return result;
    }

    // Define the resource to handle DELETE request for comment by id
    resource function delete comments/[int id]() returns http:NoContent | http:InternalServerError {
        entities:Comment|persist:Error result = self.dbClient->/comments/[id].delete();
        if result is persist:Error {
            return http:INTERNAL_SERVER_ERROR;
        }
        return http:NO_CONTENT;
    }
}
