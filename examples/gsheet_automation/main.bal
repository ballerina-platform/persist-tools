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
import ballerina/os;
import ballerinax/googleapis.sheets;

string & readonly refreshToken = os:getEnv("REFRESH_TOKEN");
string & readonly clientId = os:getEnv("CLIENT_ID");
string & readonly clientSecret = os:getEnv("CLIENT_SECRET");
string & readonly spreadsheetId = "1GkxVMXJYaoshPOtnLdNV_4syKyMMbInCFfGHlD-d3DU";

public function main() returns error? {
    io:println("Refreshing access token...");
    io:println(os:listEnv().toString());
    io:println("Refreshing access token...");
    sheets:ConnectionConfig spreadsheetConfig = {
        auth: {
            clientId: clientId,
            clientSecret: clientSecret,
            refreshToken: refreshToken,
            refreshUrl: sheets:REFRESH_URL
        }
    };

    sheets:Client spreadsheetClient = check new (spreadsheetConfig);
    string[] sheetNames = ["OrderItem", "Employee", "Workspace", "Building", "Department", "MedicalNeed", "MedicalItem"];
    sheets:Spreadsheet spreadSheet = check spreadsheetClient->openSpreadsheetById(spreadsheetId);
    foreach sheets:Sheet sheet in spreadSheet.sheets {
        if sheetNames.indexOf(sheet.properties.title, 0) !is () {
            check spreadsheetClient->removeSheet(spreadsheetId, sheet.properties.sheetId);
            _ = check spreadsheetClient->addSheet(spreadSheet.spreadsheetId, sheet.properties.title);
        }
    }
    _ = check spreadsheetClient->appendValue(
                                    spreadSheet.spreadsheetId,
                                    ["orderId", "itemId", "quantity", "notes"],
                                    {sheetName: "OrderItem", startIndex: "A1", endIndex: "E1"},
                                    "USER_ENTERED"
                                );
    _ = check spreadsheetClient->appendValue(
                                    spreadSheet.spreadsheetId,
                                    [
                                        "empNo", "firstName", "lastName", "birthDate", "gender", "hireDate",
                                        "departmentDeptNo", "workspaceWorkspaceId"
                                    ],
                                    {sheetName: "Employee", startIndex: "A1", endIndex: "I1"},
                                    "USER_ENTERED"
                                );
    _ = check spreadsheetClient->appendValue(
                                    spreadSheet.spreadsheetId,
                                    ["workspaceId", "workspaceType", "locationBuildingCode"],
                                    {sheetName: "Workspace", startIndex: "A1", endIndex: "D1"},
                                    "USER_ENTERED"
                                );
    _ = check spreadsheetClient->appendValue(
                                    spreadSheet.spreadsheetId,
                                    ["buildingCode", "city", "state", "country", "postalCode", "type"],
                                    {sheetName: "Building", startIndex: "A1", endIndex: "G1"},
                                    "USER_ENTERED"
                                );
    _ = check spreadsheetClient->appendValue(
                                    spreadSheet.spreadsheetId,
                                    ["deptNo", "deptName"],
                                    {sheetName: "Department", startIndex: "A1", endIndex: "C1"},
                                    "USER_ENTERED"
                                );
    _ = check spreadsheetClient->appendValue(
                                    spreadSheet.spreadsheetId,
                                    ["needId", "itemId", "beneficiaryId", "period", "urgency", "quantity"],
                                    {sheetName: "MedicalNeed", startIndex: "A1", endIndex: "G1"},
                                    "USER_ENTERED"
                                );
    _ = check spreadsheetClient->appendValue(
                                    spreadSheet.spreadsheetId,
                                    ["itemId", "name", "itemType", "unit", "quantity", "price"],
                                    {sheetName: "MedicalItem", startIndex: "A1", endIndex: "G1"},
                                    "USER_ENTERED"
                                );
}
