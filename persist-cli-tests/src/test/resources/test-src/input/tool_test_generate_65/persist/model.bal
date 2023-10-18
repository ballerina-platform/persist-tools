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

import ballerina/persist as _;

type Book record {|
    readonly string bookId;
    string title;
    string author;
    decimal price;
    int stock;
    OrderItem? orderitem;
|};

type Order record {|
    readonly string orderId;
    string customerId;
    string createdAt;
    decimal totalPrice;
    OrderItem[] orderItems;
    Payment? payment;
|};

type OrderItem record {|
    readonly string orderItemId;
    int quantity;
    decimal price;
    Book book;
    Order 'order;
|};

type Payment record {|
    readonly string paymentId;
    decimal paymentAmount;
    string paymentDate;
    Order 'order;
|};
