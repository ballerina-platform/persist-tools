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

//import ballerina/io;

public function main() returns error? {
    EmployeeClient employeeClient = check new ();
    CompanyClient companyClient = check new ();

    Company company = {
        id: 1,
        name: "TestCompany1"
    };
    _ = check companyClient->create(company);

    Employee employee = {
        id: 1,
        name: "TestEmployee1",
        company: company
    };
    Employee employee2 = check employeeClient->create(employee);
    Employee employee3 = check employeeClient->readByKey(1, ["company"]);
    company = {
        id: 2,
        name: "TestCompany2"
    };

    _ = check companyClient->create(company);

    Employee employee1 = {
        id: 2,
        name: "TestEmployee2",
        company: company
    };
    _ = check employeeClient->create(employee1);

    Employee employee4 = {
        id: 4,
        name: "TestEmployee3",
        company: {
            id: 4,
            name: "TestCompany"
        }
    };
    _ = check employeeClient->create(employee4);

    Company company2 = check companyClient->readByKey(2, ["employees"]);
    Employee employee9 = {
        id: 9,
        name: "TestEmployee9",
        company: {
            id: 9,
            name: "TestCompany"
        }
    };
    _ = check employeeClient->create(employee9);
    employee9.name = "TestEmployee9Updated9";
    employee9.company.name = "TestCompanyUpdated9";
    _ = check employeeClient->update(employee9);
}
