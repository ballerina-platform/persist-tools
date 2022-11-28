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
import ballerina/time;
import ballerina/io;
import perist_generate_7.clients;
import perist_generate_7.foo;

public function main() returns error? {
    clients:MedicalItemClient miClient = check new ();
    clients:MedicalNeedClient mnClient = check new ();
    clients:MedicalNeed1Client mn1Client = check new ();
    MedicalItem item = {
        itemId: 1,
        'type: "type1",
        unit: "ml"
    };
    MedicalItem createdItem = check miClient->create(item);


    MedicalNeed1 item2 = {
        needId: 1,
        period: check time:civilFromString("2021-04-12T23:20:50.520+05:30[Asia/Colombo]"),
        urgency: "urgent",
        quantity: 1
    };
    MedicalNeed1 createdNeed1 = check mn1Client->create(item2);

    foo:MedicalNeed item3 = {
        needId: 1,
        itemId: 1,
        period: check time:civilFromString("2021-04-12T23:20:50.520+05:30[Asia/Colombo]"),
        quantity: 1
    };
    foo:MedicalNeed createdNeed = check mnClient->create(item3);
    io:println(createdItem);
    io:println(createdNeed1);
    io:println(createdNeed);
}
