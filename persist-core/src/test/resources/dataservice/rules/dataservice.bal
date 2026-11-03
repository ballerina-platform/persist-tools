// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/dataservice;
import ballerina/dataservice.http as dsHttp;
import ballerina/http;
import ballerina/openapi;
import ballerina/persist;

# The persist client that the data service uses.
final Client dataserviceClient = check new ();

# The listener of the data service.
listener http:Listener dataserviceListener =
        new (dataserviceConfig.port, dataserviceConfig.listenerConfig);

@openapi:ServiceInfo {embed: true}
@http:ServiceConfig {cors: dataserviceConfig.serviceConfig.cors}
service http:InterceptableService /api on dataserviceListener {

    # Answers the errors that `ballerina/http` raises with the `DataserviceError` body.
    #
    # + return - The error interceptor
    public function createInterceptors() returns dsHttp:ErrorInterceptor => new;

    # Gets a page of orders.
    #
    # + filter - The conditions that the orders match
    # + sort - The fields to sort by, each descending when it starts with `-`
    # + size - The number of orders in the page
    # + offset - The number of orders to skip
    # + fields - The fields to return; the key fields are always returned
    # + return - A page of orders, or the error response
    resource function get orders(OrderFilter? filter = (), string[]? sort = (), int? size = (), int? offset = (),
            string[]? fields = ()) returns OrderList|OrderPartialList|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:Query|dataservice:DataserviceError|dataservice:ConversionError query =
            dataservice:query(dataserviceOrderEntity, dataservicePageConfig, filter, sort, size, offset);
        if query !is dataservice:Query {
            return dsHttp:toQueryError(query, "Order");
        }
        dataservice:DataserviceError? invalidFields = dataservice:checkFields(dataserviceOrderEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "Order");
        }
        stream<Order, persist:Error?> rows = dataserviceClient->/orders(Order,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
        OrderPayload[]|persist:Error items = from Order item in rows
            select transformOrderToPayload(item);
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        boolean hasMore = dataservice:page(items, query);
        if fields is string[] {
            OrderPayload[] sources = items;
            map<anydata>[] partials = from OrderPayload item in sources
                select dataservice:narrow(item, dataserviceOrderEntity, fields);
            do {
                if dataservice:includes(fields, "customer") {
                    dataservice:Query include = dataservice:includeQuery(dataserviceOrderEntity, "customer", sources, dataservicePageConfig);
                    stream<Customer, persist:Error?> includedRows = dataserviceClient->/customers(Customer,
                        whereClause = include.whereClause, orderByClause = include.orderByClause
                    );
                    Customer[] included = check from Customer row in includedRows
                        select row;
                    dataservice:attachOne(partials, sources, dataserviceOrderEntity, "customer", included, fields);
                }
            } on fail error err {
                return dsHttp:toListError(err);
            }
            OrderPartial[]|error converted = partials.cloneWithType();
            if converted is error {
                return dsHttp:toListError(converted);
            }
            return <OrderPartialList>{items: converted, hasMore};
        }
        return <OrderList>{items, hasMore};
    }

    # Gets an order by its key.
    #
    # + id - The id of the order
    # + fields - The fields to return; the key fields are always returned
    # + return - The order, or the error response
    resource function get orders/[int id](string[]? fields = ()) returns OrderPayload|OrderPartial|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:DataserviceError? invalidFields = dataservice:checkFields(dataserviceOrderEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "Order");
        }
        Order|persist:Error item = dataserviceClient->/orders/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "Order", [id]);
        }
        if fields is string[] {
            OrderPayload[] sources = [transformOrderToPayload(item)];
            map<anydata>[] partials = [dataservice:narrow(sources[0], dataserviceOrderEntity, fields)];
            do {
                if dataservice:includes(fields, "customer") {
                    dataservice:Query include = dataservice:includeQuery(dataserviceOrderEntity, "customer", sources, dataservicePageConfig);
                    stream<Customer, persist:Error?> includedRows = dataserviceClient->/customers(Customer,
                        whereClause = include.whereClause, orderByClause = include.orderByClause
                    );
                    Customer[] included = check from Customer row in includedRows
                        select row;
                    dataservice:attachOne(partials, sources, dataserviceOrderEntity, "customer", included, fields);
                }
            } on fail error err {
                return dsHttp:toGetError(err, "Order", [id]);
            }
            OrderPartial|error partial = partials[0].cloneWithType();
            if partial is error {
                return dsHttp:toGetError(partial, "Order", [id]);
            }
            return partial;
        }
        return transformOrderToPayload(item);
    }

    # Gets the customer of an order.
    #
    # + id - The id of the order
    # + fields - The fields of the customer to return; the key fields are always returned
    # + return - The customer, or the error response
    resource function get orders/[int id]/customer(string[]? fields = ()) returns Customer|CustomerRelatedPartial|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:DataserviceError? invalidFields = dataservice:checkFields(dataserviceCustomerEntity, fields, false);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "Customer");
        }
        Order|persist:Error item = dataserviceClient->/orders/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "Order", [id]);
        }
        dataservice:Query|dataservice:DataserviceError|dataservice:ConversionError query =
            dataservice:relatedQuery(dataserviceOrderEntity, "customer", item, dataservicePageConfig);
        if query !is dataservice:Query {
            return dsHttp:toQueryError(query, "Customer");
        }
        stream<Customer, persist:Error?> rows = dataserviceClient->/customers(Customer,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
        Customer[]|persist:Error items = from Customer related in rows
            select related;
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        if items.length() == 0 {
            return <dsHttp:NotFound>{body: dataservice:noRelated(dataserviceOrderEntity, "customer", [id])};
        }
        if fields is string[] {
            CustomerRelatedPartial|error partial = dataservice:narrow(items[0], dataserviceCustomerEntity, fields).cloneWithType();
            if partial is error {
                return dsHttp:toListError(partial);
            }
            return partial;
        }
        return items[0];
    }

    # Creates one or more orders.
    #
    # + payload - The order, or the orders, to create
    # + return - The created order or orders, or the error response
    resource function post orders(OrderInsertPayload|OrderInsertPayload[] payload)
            returns OrderCreated|OrderListCreated|dsHttp:InvalidInput|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
        OrderInsertPayload[] entries = payload is OrderInsertPayload[] ? payload : [payload];
        dataservice:DataserviceError? tooMany = dataservice:checkCreateSize(entries.length(), dataservicePageConfig);
        if tooMany is dataservice:DataserviceError {
            return <dsHttp:InvalidInput>{body: tooMany};
        }
        OrderInsert[] values = [];
        foreach OrderInsertPayload entry in entries {
            OrderInsert|dsHttp:InvalidInput value = transformPayloadToOrderInsert(entry);
            if value is dsHttp:InvalidInput {
                return value;
            }
            values.push(value);
        }
        OrderPayload[] items = [];
        if values.length() > 0 {
            do {
                transaction {
                    int[] keys = check dataserviceClient->/orders.post(values);
                    dataservice:KeyValue[][] keyValues = from int key in keys
                        select [key];
                    dataservice:Query query = dataservice:keysQuery(dataserviceOrderEntity, keyValues);
                    stream<Order, persist:Error?> stored = dataserviceClient->/orders(Order,
                        whereClause = query.whereClause, orderByClause = query.orderByClause
                    );
                    Order[] rows = check from Order item in stored
                        select item;
                    check dataservice:orderByKeys(rows, dataserviceOrderEntity, keyValues);
                    items = from Order item in rows
                        select transformOrderToPayload(item);
                    check commit;
                }
            } on fail error err {
                return dsHttp:toCreateError(err, "Order");
            }
        }
        if payload is OrderInsertPayload[] {
            return <OrderListCreated>{body: {items, hasMore: false}};
        }
        OrderPayload created = items[0];
        return <OrderCreated>{
            headers: {"Location": dsHttp:buildLocation("/api/orders", [created.id])},
            body: created
        };
    }

    # Updates an order by its key.
    #
    # + id - The id of the order
    # + payload - The fields of the order to update
    # + return - The updated order, or no content when it cannot be read back, or the error response
    resource function patch orders/[int id](OrderUpdatePayload payload)
            returns OrderPayload|http:NoContent|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:Conflict|dsHttp:ConstraintViolation|dsHttp:InternalError {
        OrderUpdate|dsHttp:InvalidInput value = transformPayloadToOrderUpdate(payload);
        if value is dsHttp:InvalidInput {
            return value;
        }
        Order|persist:Error item;
        if value.length() == 0 {
            item = dataserviceClient->/orders/[id];
        } else {
            item = dataserviceClient->/orders/[id].put(value);
        }
        if item is persist:Error {
            return dsHttp:toUpdateError(item, "Order", [id]);
        }
        return transformOrderToPayload(item);
    }

    # Deletes an order by its key.
    #
    # + id - The id of the order
    # + return - The deleted order, or the error response
    resource function delete orders/[int id]() returns OrderPayload|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:ConstraintViolation|dsHttp:InternalError {
        Order|persist:Error item = dataserviceClient->/orders/[id].delete();
        if item is persist:Error {
            return dsHttp:toDeleteError(item, "Order", [id]);
        }
        return transformOrderToPayload(item);
    }

    # Gets a page of customers.
    #
    # + filter - The conditions that the customers match
    # + sort - The fields to sort by, each descending when it starts with `-`
    # + size - The number of customers in the page
    # + offset - The number of customers to skip
    # + fields - The fields to return; the key fields are always returned
    # + return - A page of customers, or the error response
    resource function get customers(CustomerFilter? filter = (), string[]? sort = (), int? size = (), int? offset = (),
            string[]? fields = ()) returns CustomerList|CustomerPartialList|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:Query|dataservice:DataserviceError|dataservice:ConversionError query =
            dataservice:query(dataserviceCustomerEntity, dataservicePageConfig, filter, sort, size, offset);
        if query !is dataservice:Query {
            return dsHttp:toQueryError(query, "Customer");
        }
        dataservice:DataserviceError? invalidFields = dataservice:checkFields(dataserviceCustomerEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "Customer");
        }
        stream<Customer, persist:Error?> rows = dataserviceClient->/customers(Customer,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
        Customer[]|persist:Error items = from Customer item in rows
            select item;
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        boolean hasMore = dataservice:page(items, query);
        if fields is string[] {
            Customer[] sources = items;
            map<anydata>[] partials = from Customer item in sources
                select dataservice:narrow(item, dataserviceCustomerEntity, fields);
            CustomerPartial[]|error converted = partials.cloneWithType();
            if converted is error {
                return dsHttp:toListError(converted);
            }
            return <CustomerPartialList>{items: converted, hasMore};
        }
        return <CustomerList>{items, hasMore};
    }

    # Gets a customer by its key.
    #
    # + id - The id of the customer
    # + fields - The fields to return; the key fields are always returned
    # + return - The customer, or the error response
    resource function get customers/[int id](string[]? fields = ()) returns Customer|CustomerPartial|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:DataserviceError? invalidFields = dataservice:checkFields(dataserviceCustomerEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "Customer");
        }
        Customer|persist:Error item = dataserviceClient->/customers/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "Customer", [id]);
        }
        if fields is string[] {
            Customer[] sources = [item];
            map<anydata>[] partials = [dataservice:narrow(sources[0], dataserviceCustomerEntity, fields)];
            CustomerPartial|error partial = partials[0].cloneWithType();
            if partial is error {
                return dsHttp:toGetError(partial, "Customer", [id]);
            }
            return partial;
        }
        return item;
    }

    # Gets a page of warehouses.
    #
    # + filter - The conditions that the warehouses match
    # + sort - The fields to sort by, each descending when it starts with `-`
    # + size - The number of warehouses in the page
    # + offset - The number of warehouses to skip
    # + fields - The fields to return; the key fields are always returned
    # + return - A page of warehouses, or the error response
    resource function get warehouses(WarehouseFilter? filter = (), string[]? sort = (), int? size = (), int? offset = (),
            string[]? fields = ()) returns WarehouseList|WarehousePartialList|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:Query|dataservice:DataserviceError|dataservice:ConversionError query =
            dataservice:query(dataserviceWarehouseEntity, dataservicePageConfig, filter, sort, size, offset);
        if query !is dataservice:Query {
            return dsHttp:toQueryError(query, "Warehouse");
        }
        dataservice:DataserviceError? invalidFields = dataservice:checkFields(dataserviceWarehouseEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "Warehouse");
        }
        stream<Warehouse, persist:Error?> rows = dataserviceClient->/warehouses(Warehouse,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
        Warehouse[]|persist:Error items = from Warehouse item in rows
            select item;
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        boolean hasMore = dataservice:page(items, query);
        if fields is string[] {
            Warehouse[] sources = items;
            map<anydata>[] partials = from Warehouse item in sources
                select dataservice:narrow(item, dataserviceWarehouseEntity, fields);
            do {
                if dataservice:includes(fields, "orders") {
                    foreach int i in 0 ..< sources.length() {
                        dataservice:Query include = dataservice:includeQuery(dataserviceWarehouseEntity, "orders", [sources[i]], dataservicePageConfig);
                        stream<Order, persist:Error?> includedRows = dataserviceClient->/orders(Order,
                            whereClause = include.whereClause, orderByClause = include.orderByClause, limitClause = include.limitClause
                        );
                        OrderPayload[] included = check from Order row in includedRows
                            select transformOrderToPayload(row);
                        boolean includedMore = dataservice:page(included, include);
                        dataservice:attachMany(partials[i], dataserviceWarehouseEntity, "orders", included, includedMore, fields);
                    }
                }
            } on fail error err {
                return dsHttp:toListError(err);
            }
            WarehousePartial[]|error converted = partials.cloneWithType();
            if converted is error {
                return dsHttp:toListError(converted);
            }
            return <WarehousePartialList>{items: converted, hasMore};
        }
        return <WarehouseList>{items, hasMore};
    }

    # Gets a warehouse by its key.
    #
    # + id - The id of the warehouse
    # + fields - The fields to return; the key fields are always returned
    # + return - The warehouse, or the error response
    resource function get warehouses/[int id](string[]? fields = ()) returns Warehouse|WarehousePartial|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:DataserviceError? invalidFields = dataservice:checkFields(dataserviceWarehouseEntity, fields);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "Warehouse");
        }
        Warehouse|persist:Error item = dataserviceClient->/warehouses/[id];
        if item is persist:Error {
            return dsHttp:toGetError(item, "Warehouse", [id]);
        }
        if fields is string[] {
            Warehouse[] sources = [item];
            map<anydata>[] partials = [dataservice:narrow(sources[0], dataserviceWarehouseEntity, fields)];
            do {
                if dataservice:includes(fields, "orders") {
                    foreach int i in 0 ..< sources.length() {
                        dataservice:Query include = dataservice:includeQuery(dataserviceWarehouseEntity, "orders", [sources[i]], dataservicePageConfig);
                        stream<Order, persist:Error?> includedRows = dataserviceClient->/orders(Order,
                            whereClause = include.whereClause, orderByClause = include.orderByClause, limitClause = include.limitClause
                        );
                        OrderPayload[] included = check from Order row in includedRows
                            select transformOrderToPayload(row);
                        boolean includedMore = dataservice:page(included, include);
                        dataservice:attachMany(partials[i], dataserviceWarehouseEntity, "orders", included, includedMore, fields);
                    }
                }
            } on fail error err {
                return dsHttp:toGetError(err, "Warehouse", [id]);
            }
            WarehousePartial|error partial = partials[0].cloneWithType();
            if partial is error {
                return dsHttp:toGetError(partial, "Warehouse", [id]);
            }
            return partial;
        }
        return item;
    }

    # Gets the orders of a warehouse.
    #
    # + id - The id of the warehouse
    # + filter - The conditions that the orders match
    # + sort - The fields to sort by, each descending when it starts with `-`
    # + size - The number of orders in the page
    # + offset - The number of orders to skip
    # + fields - The fields of the orders to return; the key fields are always returned
    # + return - A page of the orders, or the error response
    resource function get warehouses/[int id]/orders(OrderFilter? filter = (), string[]? sort = (), int? size = (), int? offset = (),
            string[]? fields = ()) returns OrderList|OrderRelatedPartialList|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError {
        dataservice:DataserviceError? invalidFields = dataservice:checkFields(dataserviceOrderEntity, fields, false);
        if invalidFields is dataservice:DataserviceError {
            return dsHttp:toQueryError(invalidFields, "Order");
        }
        dataservice:Query|dataservice:DataserviceError|dataservice:ConversionError query =
            dataservice:relatedQuery(dataserviceWarehouseEntity, "orders", {"id": id}, dataservicePageConfig, filter, sort, size, offset);
        if query !is dataservice:Query {
            return dsHttp:toQueryError(query, "Order");
        }
        stream<Order, persist:Error?> rows = dataserviceClient->/orders(Order,
            whereClause = query.whereClause, orderByClause = query.orderByClause,
            limitClause = query.limitClause, offsetClause = query.offsetClause
        );
        OrderPayload[]|persist:Error items = from Order related in rows
            select transformOrderToPayload(related);
        if items is persist:Error {
            return dsHttp:toListError(items);
        }
        boolean hasMore = dataservice:page(items, query);
        if items.length() == 0 {
            Warehouse|persist:Error item = dataserviceClient->/warehouses/[id];
            if item is persist:Error {
                return dsHttp:toGetError(item, "Warehouse", [id]);
            }
            return <OrderList>{items, hasMore};
        }
        if fields is string[] {
            OrderRelatedPartial[]|error partials = (from OrderPayload related in items
                select dataservice:narrow(related, dataserviceOrderEntity, fields)).cloneWithType();
            if partials is error {
                return dsHttp:toListError(partials);
            }
            return <OrderRelatedPartialList>{items: partials, hasMore};
        }
        return <OrderList>{items, hasMore};
    }
}
