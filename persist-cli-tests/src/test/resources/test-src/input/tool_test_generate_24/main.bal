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
import ballerina/uuid;
import ballerina/io;
import foo/persist_generate_24.rainier;

public function main() returns error? {
    rainier:RainierClient rc = new ();
    string employeeId = "40083df0-5a27-48a9-8e0d-6e70e0d6acbf";

    // Select just the employee
    rainier:Employee? employee = check rc->/employee/[employeeId]();
    io:println(employee);

    // Select just the employee's firstName, lastName, and birthDate
    rainier:Employee empDetails = check rc->/employee/[employeeId]();
    io:println(empDetails);

    // Select all employees in a department
    stream<rainier:Employee, error?> empStream = rc->/employee();
    record {string firstName; string lastName;}[] empInDept = check from var emp in empStream
        where emp.departmentDeptNo == "d009"
        select {firstName: emp.firstName, lastName: emp.firstName};
    io:println(empInDept);
}

public function inserts() returns error? {
    rainier:RainierClient rc = new ();

    string[] dept = check rc->/department.post([{
        deptNo: "d010",
        deptName: "Customer Service"
    }]);

    rainier:EmployeeInsert newEmp = {
        empNo: uuid:createType4AsString(),
        firstName: "Jack",
        lastName: "Ryan",
        birthDate: {year: 1976, month: 4, day: 23},
        gender: "M",
        hireDate: {year: 2019, month: 12, day: 23},
        departmentDeptNo: dept[0],
        workspaceWorkspaceId: "WS-1234"
    };
    string[] emp = check rc->/employee.post([newEmp]);
    io:println(emp);
}

public function updates() returns error? {
    rainier:RainierClient rc = new ();

    string employeeId = "40083df0-5a27-48a9-8e0d-6e70e0d6acbf";
    _ = check rc->/employee/[employeeId].put({departmentDeptNo: "d010"});
}
