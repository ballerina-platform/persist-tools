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
import foo/medical_center.entities;

public function main() returns error? {
    entities:EntitiesClient mcClient = check new ();
    entities:MedicalItemInsert item = {
        itemId: 1,
        name: "item name",
        itemType: "type1",
        unit: "ml",
        price: 1.34345133,
        quantity: 2.0040043
    };
    int[] itemIds = check mcClient->/medicalitems.post([item]);
    io:println("Created item id: ", itemIds[0]);
    entities:MedicalItem retrievedItem = check mcClient->/medicalitems/[itemIds[0]].get();
    io:println("Retrieved item: ", retrievedItem);

    entities:MedicalItem|error itemError = mcClient->/medicalitems/[5];
    io:println("Retrieved non-existence item: ", itemError);

    entities:MedicalItem item2 = {
        itemId: 2,
        name: "item2 name",
        itemType: "type1",
        unit: "ml",
        price: 120.2345,
        quantity: 25.125
    };
    entities:MedicalItem item3 = {
        itemId: 3,
        name: "item2 name",
        itemType: "type2",
        unit: "ml",
        price: 570.00004,
        quantity: 124.987
    };
     entities:MedicalItem item4 = {
        itemId: 4,
        name: "item2 name",
        itemType: "type2",
        unit: "kg",
        price: 1200.2345,
        quantity: 13421.456
    };
    _ = check mcClient->/medicalitems.post([item2, item3, item4]);

    io:println("\n========== type1 ==========");
    _ = check from entities:MedicalItem itemx in mcClient->/medicalitems.get()
        where itemx.itemType == "type1"
        do {
            io:println(itemx);
        };

    io:println("\n========== type2 ==========");
    _ = check from entities:MedicalItem itemx in mcClient->/medicalitems.get()
        where itemx.itemType == "type2"
        order by itemx.itemId
        limit 2
        do {
            io:println(itemx);
        };

    io:println("\n========== update type2's unit to kg ==========");
    _ = check from entities:MedicalItem itemx in mcClient->/medicalitems.get()
        where itemx.itemType == "type2"
        do {
            entities:MedicalItemUpdate updatex = {unit: "kg"};
            // TODO: remove comment after issue is resolved (https://github.com/ballerina-platform/ballerina-standard-library/issues/3951)
            //_ = check mcClient->/medicalitems/[itemx.itemId].put(updatex);
        };

    _ = check from entities:MedicalItem itemx in mcClient->/medicalitems.get()
        do {
            io:println(itemx);
        };

    io:println("\n========== delete type2 ==========");
    _ = check from entities:MedicalItem itemx in mcClient->/medicalitems.get()
        where itemx.itemType == "type2"
        do {
            // TODO: remove comment after issue is resolved (https://github.com/ballerina-platform/ballerina-standard-library/issues/3951)
            //_ = check mcClient->/medicalitems/[itemx.itemId].delete();
        };

    _ = check from entities:MedicalItem itemx in mcClient->/medicalitems.get()
        do {
            io:println(itemx);
        };

    io:println("\n========== create medical needs ==========");
    entities:MedicalNeed mnItem = {
        needId: 1,
        itemId: 1,
        beneficiaryId: 1,
        period: {year: 2022, month: 10, day: 10, hour: 1, minute: 2, second: 3},
        urgency: "URGENT",
        quantity: 5
    };
    int[] needIds = check mcClient->/medicalneeds.post([mnItem]);
    io:println("Created need id: ", needIds[0]);

    entities:MedicalNeed mnItem2 = {
        needId: 2,
        itemId: 2,
        beneficiaryId: 2,
        period: {year: 2021, month: 10, day: 10, hour: 1, minute: 2, second: 3},
        urgency: "NOT URGENT",
        quantity: 5
    };
    needIds = check mcClient->/medicalneeds.post([mnItem2]);
    io:println("Created need id: ", needIds[0]);

    check mcClient.close();
}
