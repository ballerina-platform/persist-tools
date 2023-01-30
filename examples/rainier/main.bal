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
import ballerina/persist;
import foo/association.rainier;

public function main() returns error? {
    rainier:RainierClient rainierClient = check new ();

    rainier:Building building1 = {
        buildingCode: "building-1",
        city: "Colombo",
        state: "Western Province",
        country: "Sri Lanka",
        postalCode: "10370",
        'type: "rented"
    };

    rainier:BuildingInsert building2 = {
        buildingCode: "building-2",
        city: "Manhattan",
        state: "New York",
        country: "USA",
        postalCode: "10570",
        'type: "owned"
    };

    rainier:BuildingInsert building3 = {
        buildingCode: "building-3",
        city: "London",
        state: "London",
        country: "United Kingdom",
        postalCode: "39202",
        'type: "rented"
    };

    rainier:Building updatedBuilding1 = {
        buildingCode: "building-1",
        city: "Galle",
        state: "Southern Province",
        country: "Sri Lanka",
        postalCode: "10890",
        'type: "owned"
    };

    rainier:Department department1 = {
        deptNo: "department-1",
        deptName: "Finance"
    };

    rainier:Department department2 = {
        deptNo: "department-2",
        deptName: "Marketing"
    };

    rainier:Department department3 = {
        deptNo: "department-3",
        deptName: "Engineering"
    };

    rainier:Department updatedDepartment1 = {
        deptNo: "department-1",
        deptName: "Finance & Legalities"
    };

    rainier:Workspace workspace1 = {
        workspaceId: "workspace-1",
        workspaceType: "small",
        buildingBuildingCode: "building-2"
    };

    rainier:Workspace workspace2 = {
        workspaceId: "workspace-2",
        workspaceType: "medium",
        buildingBuildingCode: "building-2"
    };

    rainier:Workspace workspace3 = {
        workspaceId: "workspace-3",
        workspaceType: "small",
        buildingBuildingCode: "building-2"
    };

    rainier:Workspace updatedWorkspace1 = {
        workspaceId: "workspace-1",
        workspaceType: "large",
        buildingBuildingCode: "building-2"
    };

    rainier:Employee employee1 = {
        empNo: "employee-1",
        firstName: "Tom",
        lastName: "Scott",
        birthDate: {year: 1992, month: 11, day: 13},
        gender: "M",
        hireDate: {year: 2022, month: 8, day: 1},
        departmentDeptNo: "department-2",
        workspaceWorkspaceId: "workspace-2"
    };

    rainier:Employee invalidEmployee = {
        empNo: "invalid-employee-no-extra-characters-to-force-failure",
        firstName: "Tom",
        lastName: "Scott",
        birthDate: {year: 1992, month: 11, day: 13},
        gender: "M",
        hireDate: {year: 2022, month: 8, day: 1},
        departmentDeptNo: "department-2",
        workspaceWorkspaceId: "workspace-2"
    };

    rainier:Employee employee2 = {
        empNo: "employee-2",
        firstName: "Jane",
        lastName: "Doe",
        birthDate: {year: 1996, month: 9, day: 15},
        gender: "F",
        hireDate: {year: 2022, month: 6, day: 1},
        departmentDeptNo: "department-2",
        workspaceWorkspaceId: "workspace-2"
    };

    rainier:Employee employee3 = {
        empNo: "employee-3",
        firstName: "Hugh",
        lastName: "Smith",
        birthDate: {year: 1986, month: 9, day: 15},
        gender: "F",
        hireDate: {year: 2021, month: 6, day: 1},
        departmentDeptNo: "department-3",
        workspaceWorkspaceId: "workspace-3"
    };

    rainier:Employee updatedEmployee1 = {
        empNo: "employee-1",
        firstName: "Tom",
        lastName: "Jones",
        birthDate: {year: 1994, month: 11, day: 13},
        gender: "M",
        hireDate: {year: 2022, month: 8, day: 1},
        departmentDeptNo: "department-3",
        workspaceWorkspaceId: "workspace-2"
    };

    rainier:OrderItem orderItem1 = {
        orderId: "order-1",
        itemId: "item-1",
        quantity: 5,
        notes: "none"
    };

    rainier:OrderItem orderItem2 = {
        orderId: "order-2",
        itemId: "item-2",
        quantity: 10,
        notes: "more"
    };

    rainier:OrderItem orderItem2Updated = {
        orderId: "order-2",
        itemId: "item-2",
        quantity: 20,
        notes: "more than more"
    };

    string[] buildingCodes = check rainierClient->/building.post([building1]);
    rainier:Building buildingRetrieved = check rainierClient->/building/[building1.buildingCode].get();
    buildingCodes = check rainierClient->/building.post([building2, building3]);
    buildingRetrieved = check rainierClient->/building/[building2.buildingCode].get();
    buildingRetrieved = check rainierClient->/building/[building3.buildingCode].get();
    buildingRetrieved = check rainierClient->/building/[building1.buildingCode].get();
    stream<rainier:Building, error?> buildingStream = rainierClient->/building.get();
    rainier:Building[] buildings = check from rainier:Building building_temp in buildingStream
        select building_temp;
    buildingRetrieved = check rainierClient->/building/[building1.buildingCode].put({
        city: "Galle",
        state: "Southern Province",
        postalCode: "10890",
        'type: "owned"
    });
    buildingRetrieved = check rainierClient->/building/[building1.buildingCode].get();

    rainier:Building|error buildingRetrievedError = rainierClient->/building/["invalid-building-code"].put({
        city: "Galle",
        state: "Southern Province",
        postalCode: "10890"
    });
    if buildingRetrievedError !is persist:Error {
        panic error("Error expected");
    }
    buildingRetrieved = check rainierClient->/building/[building1.buildingCode].delete();
    stream<rainier:Building, error?> buildingStream2 = rainierClient->/building.get();
    rainier:Building[] buildingSet = check from rainier:Building building_temp2 in buildingStream2
        select building_temp2;

    io:println("Building examples successfuly executed!");

    string[] deptNos = check rainierClient->/department.post([department1]);
    rainier:Department departmentRetrieved = check rainierClient->/department/[department1.deptNo].get();
    deptNos = check rainierClient->/department.post([department2, department3]);
    departmentRetrieved = check rainierClient->/department/[department2.deptNo].get();
    departmentRetrieved = check rainierClient->/department/[department3.deptNo].get();
    departmentRetrieved = check rainierClient->/department/[department1.deptNo].get();
    rainier:Department|error departmentRetrievedError = rainierClient->/department/["invalid-department-id"].get();
    if departmentRetrievedError !is persist:Error {
        panic error("Error expected");
    }

    stream<rainier:Department, error?> departmentStream = rainierClient->/department.get();
    rainier:Department[] departments = check from rainier:Department department_temp in departmentStream
        select department_temp;

    departmentRetrieved = check rainierClient->/department/[department1.deptNo].put({
        deptName: "Finance & Legalities"
    });

    departmentRetrieved = check rainierClient->/department/[department1.deptNo].get();

    departmentRetrievedError = rainierClient->/department/["invalid-department-id"].put({
        deptName: "Human Resources"
    });
    if departmentRetrievedError !is persist:Error {
        panic error("Error expected");
    }
    departmentRetrieved = check rainierClient->/department/[department1.deptNo].delete();

    stream<rainier:Department, error?> departmentStream2 = rainierClient->/department.get();
    departments = check from rainier:Department department_Temp2 in departmentStream2
        select department_Temp2;

    departmentRetrievedError = rainierClient->/department/[department1.deptNo].delete();
    if departmentRetrievedError !is persist:Error {
        panic error("Error expected");
    }
    io:println("Department examples successfuly executed!");

    string[] workspaceIds = check rainierClient->/workspace.post([workspace1]);
    rainier:Workspace workspaceRetrieved = check rainierClient->/workspace/[workspace1.workspaceId].get();
    workspaceIds = check rainierClient->/workspace.post([workspace2, workspace3]);
    workspaceRetrieved = check rainierClient->/workspace/[workspace2.workspaceId].get();
    workspaceRetrieved = check rainierClient->/workspace/[workspace3.workspaceId].get();
    workspaceRetrieved = check rainierClient->/workspace/[workspace1.workspaceId].get();
    rainier:Workspace|error workspaceRetrievedError = rainierClient->/workspace/["invalid-workspace-id"].get();
    if workspaceRetrievedError !is persist:Error {
        panic error("Error expected");
    }
    stream<rainier:Workspace, error?> workspaceStream = rainierClient->/workspace.get();
    rainier:Workspace[] workspaces = check from rainier:Workspace workspace_temp in workspaceStream
        select workspace_temp;
    workspaceRetrievedError = check rainierClient->/workspace/[workspace1.workspaceId].put({
        workspaceType: "large"
    });
    workspaceRetrieved = check rainierClient->/workspace/[workspace1.workspaceId].get();

    workspaceRetrievedError = rainierClient->/workspace/["invalid-workspace-id"].put({
        workspaceType: "large"
    });

    workspaceRetrieved = check rainierClient->/workspace/[workspace1.workspaceId].delete();

    stream<rainier:Workspace, error?> workspaceStream2 = rainierClient->/workspace.get();
    workspaces = check from rainier:Workspace workspace_temp2 in workspaceStream2
        select workspace_temp2;

    workspaceRetrievedError = rainierClient->/workspace/[workspace1.workspaceId].delete();
    if workspaceRetrievedError !is persist:Error {
        panic error("Error expected");
    }

    io:println("Workspace examples successfuly executed!");

    string[] empNos = check rainierClient->/employee.post([employee1]);

    rainier:Employee employeeRetrieved = check rainierClient->/employee/[employee1.empNo].get();

    empNos = check rainierClient->/employee.post([employee2, employee3]);
    employeeRetrieved = check rainierClient->/employee/[employee2.empNo].get();

    employeeRetrieved = check rainierClient->/employee/[employee3.empNo].get();
    employeeRetrieved = check rainierClient->/employee/[employee1.empNo].get();
    rainier:Employee|error employeeRetrievedError = rainierClient->/employee/["invalid-employee-id"].get();
    stream<rainier:Employee, error?> employeeStream = rainierClient->/employee.get();
    rainier:Employee[] employees = check from rainier:Employee employee in employeeStream
        select employee;

    employeeRetrieved = check rainierClient->/employee/[employee1.empNo].put({
        lastName: "Jones",
        departmentDeptNo: "department-3",
        birthDate: {year: 1994, month: 11, day: 13}
    });
    employeeRetrieved = check rainierClient->/employee/[employee1.empNo].get();
    employeeRetrievedError = rainierClient->/employee/["invalid-employee-id"].put({
        lastName: "Jones"
    });

    employeeRetrievedError = rainierClient->/employee/[employee1.empNo].put({
        workspaceWorkspaceId: "invalid-workspaceWorkspaceId"
    });

    employeeRetrieved = check rainierClient->/employee/[employee1.empNo].delete();

    stream<rainier:Employee, error?> employeeStream2 = rainierClient->/employee.get();
    employees = check from rainier:Employee employee_temp2 in employeeStream2
        select employee_temp2;
    employeeRetrievedError = rainierClient->/employee/[employee1.empNo].get();
    if employeeRetrievedError !is persist:Error {
        panic error("Error expected");
    }

    io:println("Employee examples successfuly executed!");

    [string, string][] ids = check rainierClient->/orderitem.post([orderItem1, orderItem2]);

    rainier:OrderItem orderItemRetrieved = check rainierClient->/orderitem/[orderItem1.itemId]/[orderItem1.orderId].get();

    orderItemRetrieved = check rainierClient->/orderitem/[orderItem2.itemId]/[orderItem2.orderId].get();

    [string, string][]|error idsError = rainierClient->/orderitem.post([orderItem1]);
    stream<rainier:OrderItem, error?> orderItemStream = rainierClient->/orderitem.get();
    rainier:OrderItem[] orderitems = check from rainier:OrderItem orderItemTemp in orderItemStream
        select orderItemTemp;
    rainier:OrderItem orderItem = check rainierClient->/orderitem/[orderItem1.itemId]/[orderItem1.orderId].get();
    rainier:OrderItem|error orderItemError = rainierClient->/orderitem/["invalid-order-id"]/[orderItem1.itemId].get();
    orderItemError = rainierClient->/orderitem/[orderItem1.orderId]/["invalid-item-id"].get();
    orderItem = check rainierClient->/orderitem/[orderItem2.itemId]/[orderItem2.orderId].put({
        quantity: orderItem2Updated.quantity,
        notes: orderItem2Updated.notes
    });
    orderItem = check rainierClient->/orderitem/[orderItem2.itemId]/[orderItem2.orderId].get();
    orderItemError = rainierClient->/orderitem/[orderItem1.itemId]/[orderItem2.orderId].put({
        quantity: 239,
        notes: "updated notes"
    });
    orderItem = check rainierClient->/orderitem/[orderItem2.itemId]/[orderItem2.orderId].delete();
    orderItemError = rainierClient->/orderitem/[orderItem2.itemId]/[orderItem2.orderId].get();
    if orderItemError !is persist:Error {
        panic error("Error expected");
    }
    orderItemError = rainierClient->/orderitem/["invalid-item-id"]/[orderItem2.orderId].delete();
    if orderItemError !is persist:Error {
        panic error("Error expected");
    }
    io:println("OrderItem examples successfuly executed!");

    io:println("\n========== Building ==========");
    _ = check from rainier:Building building in rainierClient->/building.get()
        do {
            io:println(building);
        };

    io:println("\n========== Workspace ==========");
    _ = check from rainier:Workspace workspace in rainierClient->/workspace.get()
        do {
            io:println(workspace);
        };
    io:println("\n========== Department ==========");
    _ = check from rainier:Department department in rainierClient->/department.get()
        do {
            io:println(department);
        };
    io:println("\n========== Employee ==========");
    _ = check from rainier:Employee employee in rainierClient->/employee.get()
        do {
            io:println(employee);
        };
    io:println("\n========== OrderItem ==========");
    _ = check from rainier:OrderItem orderIt in rainierClient->/orderitem.get()
        do {
            io:println(orderIt);
        };

}
