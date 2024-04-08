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
import ballerina/test;
import ballerina/http;
import social_media.entities;

http:Client socialMediaEndpoint = check new ("http://localhost:9090/social_media");

@test:Config {}
function testCreateUser() returns error? {
    entities:UserInsert user = {
        firstName: "Dinuka",
        lastName: "Ashan",
        email: "dinuka@gmail.com",
        age: 23,
        dateOfBirth: {year: 2000, month: 8, day: 17},
        gender: entities:MALE,
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
        firstName: "Dinuka",
        lastName: "Ashan",
        email: "dinuka@gmail.com",
        age: 23,
        dateOfBirth: {year: 2000, month: 8, day: 17},
        gender: entities:MALE,
        isMarried: true,
        spouseName: "Amanda"
    };
    http:Response result = check socialMediaEndpoint->/users.post(user);
    test:assertEquals(result.statusCode, 409, "Status code should be 409");
}

@test:Config {
    dependsOn: [testCreateUser]
}
function testGetUsers() returns error? {
    http:Response result = check socialMediaEndpoint->/users.get();
    test:assertEquals(result.statusCode, 200, "Status code should be 200");
    test:assertEquals(result.getJsonPayload(), [
                {
                    "firstName": "Dinuka",
                    "lastName": "Ashan",
                    "email": "dinuka@gmail.com"
                }
            ], "User details should be returned");
}

@test:Config {
    dependsOn: [testCreateUser]
}
function testCreatePost() returns error? {
    entities:PostInsert post = {
        id: 1,
        title: "Post 1",
        content: "Content 1",
        userFirstName: "Dinuka",
        userLastName: "Ashan"
    };
    http:Response result = check socialMediaEndpoint->/posts.post(post);
    test:assertEquals(result.statusCode, 201, "Status code should be 201");
}

@test:Config {
    dependsOn: [testCreateUser, testCreatePost]
}
function testCreatePostAlreadyExists() returns error? {
    entities:PostInsert post = {
        id: 1,
        title: "Post Exists",
        content: "Content Exists",
        userFirstName: "Dinuka",
        userLastName: "Ashan"
    };
    http:Response result = check socialMediaEndpoint->/posts.post(post);
    test:assertEquals(result.statusCode, 409, "Status code should be 409");
}

@test:Config {
    dependsOn: [testCreatePost]
}
function testGetPosts() returns error? {
    http:Response result = check socialMediaEndpoint->/posts.get();
    test:assertEquals(result.statusCode, 200, "Status code should be 200");
    test:assertEquals(result.getJsonPayload(), [
                {
                    "id": 1,
                    "title": "Post 1",
                    "content": "Content 1",
                    "user": {
                        "firstName": "Dinuka",
                        "lastName": "Ashan",
                        "email": "dinuka@gmail.com"
                    }
                }
            ],
            "User details should be returned");
}

@test:Config {
    dependsOn: [testCreatePost]
}
function testCreateComment() returns error? {
    entities:CommentInsert comment = {
        id: 1,
        message: "Comment 1",
        postId: 1
    };
    http:Response result = check socialMediaEndpoint->/comments.post(comment);
    test:assertEquals(result.statusCode, 201, "Status code should be 201");
}

@test:Config {
    dependsOn: [testCreatePost, testCreateComment]
}
function testCreateCommentAlreadyExists() returns error? {
    entities:CommentInsert comment = {
        id: 1,
        message: "Comment Exists",
        postId: 1
    };
    http:Response result = check socialMediaEndpoint->/comments.post(comment);
    test:assertEquals(result.statusCode, 409, "Status code should be 409");
}

@test:Config {
    dependsOn: [testCreateComment]
}
function testGetComments() returns error? {
    http:Response result = check socialMediaEndpoint->/comments.get();
    test:assertEquals(result.statusCode, 200, "Status code should be 200");
    test:assertEquals(result.getJsonPayload(), [
                {
                    "id": 1,
                    "message": "Comment 1",
                    "post": {
                        "id": 1,
                        "content": "Content 1"
                    }
                }
            ],
            "User details should be returned");
}

@test:Config {
    dependsOn: [testCreatePost]
}
function testGetPostByUser() returns error? {
    http:Response result = check socialMediaEndpoint->/users/["Dinuka"]/["Ashan"]/posts;
    test:assertEquals(result.statusCode, 200, "Status code should be 200");
    test:assertEquals(result.getJsonPayload(), [
                {
                    "id": 1,
                    "title": "Post 1",
                    "content": "Content 1",
                    "user": {
                        "firstName": "Dinuka",
                        "lastName": "Ashan",
                        "email": "dinuka@gmail.com"
                    }
                }
            ], "Post details should be returned");
}

@test:Config {
    dependsOn: [testCreateComment]
}
function testGetCommentByPost() returns error? {
    http:Response result = check socialMediaEndpoint->/posts/[1]/comments;
    test:assertEquals(result.statusCode, 200, "Status code should be 200");
    test:assertEquals(result.getJsonPayload(), [
                {
                    "id": 1,
                    "message": "Comment 1",
                    "post": {
                        "id": 1,
                        "content": "Content 1"
                    }
                }
            ], "Comment details should be returned");
}

@test:Config {
    dependsOn: [testCreatePostAlreadyExists, testGetPosts, testGetCommentByPost, testGetPostByUser]
}
function testUpdatePost() returns error? {
    entities:PostUpdate postUpdated = {
        title: "Post Updated",
        content: "Content Updated"
    };
    http:Response result = check socialMediaEndpoint->/posts/[1].patch(postUpdated);
    test:assertEquals(result.getJsonPayload(),
            {
                "id": 1,
                "title": "Post Updated",
                "content": "Content Updated",
                "userFirstName": "Dinuka",
                "userLastName": "Ashan"
            }, "Post details should be returned");
}

@test:Config {
    dependsOn: [testCreatePostAlreadyExists, testGetComments, testGetCommentByPost]
}
function testUpdateComment() returns error? {
    entities:CommentUpdate commentUpdated = {
        message: "Comment Updated"
    };
    http:Response result = check socialMediaEndpoint->/comments/[1].patch(commentUpdated);
    test:assertEquals(result.getJsonPayload(),
            {
                "id": 1,
                "message": "Comment Updated",
                "postId": 1
            }
            , "Post details should be returned");
}

@test:Config {
    dependsOn: [testDeletePost]
}
function testDeleteUser() returns error? {
    http:Response result = check socialMediaEndpoint->/users/["Dinuka"]/["Ashan"].delete();
    test:assertEquals(result.statusCode, 204, "Status code should be 204");
    http:Response result2 = check socialMediaEndpoint->/users;
    test:assertEquals(result2.statusCode, 200, "Status code should be 200");
    test:assertEquals(result2.getJsonPayload(), [], "User details should be empty");
}

@test:Config {
    dependsOn: [testDeleteComment]
}
function testDeletePost() returns error? {
    http:Response result = check socialMediaEndpoint->/posts/[1].delete();
    test:assertEquals(result.statusCode, 204, "Status code should be 204");
    http:Response result2 = check socialMediaEndpoint->/posts;
    test:assertEquals(result2.statusCode, 200, "Status code should be 200");
    test:assertEquals(result2.getJsonPayload(), [], "Posts details should be empty");
}

@test:Config {
    dependsOn: [testUpdateComment, testGetPostByUser]
}
function testDeleteComment() returns error? {
    http:Response result = check socialMediaEndpoint->/comments/[1].delete();
    test:assertEquals(result.statusCode, 204, "Status code should be 204");
    http:Response result2 = check socialMediaEndpoint->/comments;
    test:assertEquals(result2.statusCode, 200, "Status code should be 200");
    test:assertEquals(result2.getJsonPayload(), [], "Comment details should be empty");
}
