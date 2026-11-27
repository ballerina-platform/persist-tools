// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/dataservice;
import ballerina/http;

# A list of read items.
public type ReadItemList record {
    # The read items
    ReadItem[] items;
    # Whether records exist after this page
    boolean hasMore;
};

# The conditions that read items match, as the `filter` query parameter holds them.
public type ReadItemFilter record {|
    # The conditions on the `id` field
    record {|
        int eq?;
        int ne?;
        int lt?;
        int lte?;
        int gt?;
        int gte?;
        int[] anyOf?;
    |} id?;
    # The conditions on the `name` field
    record {|
        string eq?;
        string ne?;
        string lt?;
        string lte?;
        string gt?;
        string gte?;
        string[] anyOf?;
        string contains?;
        string startsWith?;
    |} name?;
|};

# A read item with the fields named in the `fields` query parameter.
public type ReadItemPartial record {|
    # The `id` field of `ReadItem`
    int id;
    # The `name` field of `ReadItem`
    string name?;
|};

# A list of read items with the fields named in the `fields` query parameter.
public type ReadItemPartialList record {
    # The read items
    ReadItemPartial[] items;
    # Whether records exist after this page
    boolean hasMore;
};

# The fields of `ReadItem` that callers can see.
final readonly & dataservice:Field[] dataserviceReadItemFields = [
    {name: "id", column: "id", 'type: dataservice:INT, key: true},
    {name: "name", column: "name", 'type: dataservice:STRING}
];

# The description of `ReadItem` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceReadItemEntity = {
    name: "ReadItem",
    collection: "readItems",
    'resource: "readitems",
    datastore: dataservice:H2,
    fields: dataserviceReadItemFields
};

# A list of create items.
public type CreateItemList record {
    # The create items
    CreateItem[] items;
    # Whether records exist after this page
    boolean hasMore;
};

# The fields of `CreateItem` that callers can see.
final readonly & dataservice:Field[] dataserviceCreateItemFields = [
    {name: "id", column: "id", 'type: dataservice:INT, key: true},
    {name: "name", column: "name", 'type: dataservice:STRING}
];

# The description of `CreateItem` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceCreateItemEntity = {
    name: "CreateItem",
    collection: "createItems",
    'resource: "createitems",
    datastore: dataservice:H2,
    fields: dataserviceCreateItemFields
};

# The response to creating one create item.
public type CreateItemCreated record {|
    *http:Created;
    # The created create item
    CreateItem body;
|};

# The response to creating several create items.
public type CreateItemListCreated record {|
    *http:Created;
    # The created create items
    CreateItemList body;
|};

# A list of read create items.
public type ReadCreateItemList record {
    # The read create items
    ReadCreateItem[] items;
    # Whether records exist after this page
    boolean hasMore;
};

# The conditions that read create items match, as the `filter` query parameter holds them.
public type ReadCreateItemFilter record {|
    # The conditions on the `id` field
    record {|
        int eq?;
        int ne?;
        int lt?;
        int lte?;
        int gt?;
        int gte?;
        int[] anyOf?;
    |} id?;
    # The conditions on the `name` field
    record {|
        string eq?;
        string ne?;
        string lt?;
        string lte?;
        string gt?;
        string gte?;
        string[] anyOf?;
        string contains?;
        string startsWith?;
    |} name?;
|};

# A read create item with the fields named in the `fields` query parameter.
public type ReadCreateItemPartial record {|
    # The `id` field of `ReadCreateItem`
    int id;
    # The `name` field of `ReadCreateItem`
    string name?;
|};

# A list of read create items with the fields named in the `fields` query parameter.
public type ReadCreateItemPartialList record {
    # The read create items
    ReadCreateItemPartial[] items;
    # Whether records exist after this page
    boolean hasMore;
};

# The fields of `ReadCreateItem` that callers can see.
final readonly & dataservice:Field[] dataserviceReadCreateItemFields = [
    {name: "id", column: "id", 'type: dataservice:INT, key: true},
    {name: "name", column: "name", 'type: dataservice:STRING}
];

# The description of `ReadCreateItem` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceReadCreateItemEntity = {
    name: "ReadCreateItem",
    collection: "readCreateItems",
    'resource: "readcreateitems",
    datastore: dataservice:H2,
    fields: dataserviceReadCreateItemFields
};

# The response to creating one read create item.
public type ReadCreateItemCreated record {|
    *http:Created;
    # The created read create item
    ReadCreateItem body;
|};

# The response to creating several read create items.
public type ReadCreateItemListCreated record {|
    *http:Created;
    # The created read create items
    ReadCreateItemList body;
|};

# A list of read update items.
public type ReadUpdateItemList record {
    # The read update items
    ReadUpdateItem[] items;
    # Whether records exist after this page
    boolean hasMore;
};

# The conditions that read update items match, as the `filter` query parameter holds them.
public type ReadUpdateItemFilter record {|
    # The conditions on the `id` field
    record {|
        int eq?;
        int ne?;
        int lt?;
        int lte?;
        int gt?;
        int gte?;
        int[] anyOf?;
    |} id?;
    # The conditions on the `name` field
    record {|
        string eq?;
        string ne?;
        string lt?;
        string lte?;
        string gt?;
        string gte?;
        string[] anyOf?;
        string contains?;
        string startsWith?;
    |} name?;
|};

# A read update item with the fields named in the `fields` query parameter.
public type ReadUpdateItemPartial record {|
    # The `id` field of `ReadUpdateItem`
    int id;
    # The `name` field of `ReadUpdateItem`
    string name?;
|};

# A list of read update items with the fields named in the `fields` query parameter.
public type ReadUpdateItemPartialList record {
    # The read update items
    ReadUpdateItemPartial[] items;
    # Whether records exist after this page
    boolean hasMore;
};

# The fields of `ReadUpdateItem` that callers can see.
final readonly & dataservice:Field[] dataserviceReadUpdateItemFields = [
    {name: "id", column: "id", 'type: dataservice:INT, key: true},
    {name: "name", column: "name", 'type: dataservice:STRING}
];

# The description of `ReadUpdateItem` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceReadUpdateItemEntity = {
    name: "ReadUpdateItem",
    collection: "readUpdateItems",
    'resource: "readupdateitems",
    datastore: dataservice:H2,
    fields: dataserviceReadUpdateItemFields
};

# A list of read delete items.
public type ReadDeleteItemList record {
    # The read delete items
    ReadDeleteItem[] items;
    # Whether records exist after this page
    boolean hasMore;
};

# The conditions that read delete items match, as the `filter` query parameter holds them.
public type ReadDeleteItemFilter record {|
    # The conditions on the `id` field
    record {|
        int eq?;
        int ne?;
        int lt?;
        int lte?;
        int gt?;
        int gte?;
        int[] anyOf?;
    |} id?;
    # The conditions on the `name` field
    record {|
        string eq?;
        string ne?;
        string lt?;
        string lte?;
        string gt?;
        string gte?;
        string[] anyOf?;
        string contains?;
        string startsWith?;
    |} name?;
|};

# A read delete item with the fields named in the `fields` query parameter.
public type ReadDeleteItemPartial record {|
    # The `id` field of `ReadDeleteItem`
    int id;
    # The `name` field of `ReadDeleteItem`
    string name?;
|};

# A list of read delete items with the fields named in the `fields` query parameter.
public type ReadDeleteItemPartialList record {
    # The read delete items
    ReadDeleteItemPartial[] items;
    # Whether records exist after this page
    boolean hasMore;
};

# The fields of `ReadDeleteItem` that callers can see.
final readonly & dataservice:Field[] dataserviceReadDeleteItemFields = [
    {name: "id", column: "id", 'type: dataservice:INT, key: true},
    {name: "name", column: "name", 'type: dataservice:STRING}
];

# The description of `ReadDeleteItem` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceReadDeleteItemEntity = {
    name: "ReadDeleteItem",
    collection: "readDeleteItems",
    'resource: "readdeleteitems",
    datastore: dataservice:H2,
    fields: dataserviceReadDeleteItemFields
};

# A list of create update items.
public type CreateUpdateItemList record {
    # The create update items
    CreateUpdateItem[] items;
    # Whether records exist after this page
    boolean hasMore;
};

# The fields of `CreateUpdateItem` that callers can see.
final readonly & dataservice:Field[] dataserviceCreateUpdateItemFields = [
    {name: "id", column: "id", 'type: dataservice:INT, key: true},
    {name: "name", column: "name", 'type: dataservice:STRING}
];

# The description of `CreateUpdateItem` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceCreateUpdateItemEntity = {
    name: "CreateUpdateItem",
    collection: "createUpdateItems",
    'resource: "createupdateitems",
    datastore: dataservice:H2,
    fields: dataserviceCreateUpdateItemFields
};

# The response to creating one create update item.
public type CreateUpdateItemCreated record {|
    *http:Created;
    # The created create update item
    CreateUpdateItem body;
|};

# The response to creating several create update items.
public type CreateUpdateItemListCreated record {|
    *http:Created;
    # The created create update items
    CreateUpdateItemList body;
|};

# A list of create delete items.
public type CreateDeleteItemList record {
    # The create delete items
    CreateDeleteItem[] items;
    # Whether records exist after this page
    boolean hasMore;
};

# The fields of `CreateDeleteItem` that callers can see.
final readonly & dataservice:Field[] dataserviceCreateDeleteItemFields = [
    {name: "id", column: "id", 'type: dataservice:INT, key: true},
    {name: "name", column: "name", 'type: dataservice:STRING}
];

# The description of `CreateDeleteItem` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceCreateDeleteItemEntity = {
    name: "CreateDeleteItem",
    collection: "createDeleteItems",
    'resource: "createdeleteitems",
    datastore: dataservice:H2,
    fields: dataserviceCreateDeleteItemFields
};

# The response to creating one create delete item.
public type CreateDeleteItemCreated record {|
    *http:Created;
    # The created create delete item
    CreateDeleteItem body;
|};

# The response to creating several create delete items.
public type CreateDeleteItemListCreated record {|
    *http:Created;
    # The created create delete items
    CreateDeleteItemList body;
|};

# A list of read create update items.
public type ReadCreateUpdateItemList record {
    # The read create update items
    ReadCreateUpdateItem[] items;
    # Whether records exist after this page
    boolean hasMore;
};

# The conditions that read create update items match, as the `filter` query parameter holds them.
public type ReadCreateUpdateItemFilter record {|
    # The conditions on the `id` field
    record {|
        int eq?;
        int ne?;
        int lt?;
        int lte?;
        int gt?;
        int gte?;
        int[] anyOf?;
    |} id?;
    # The conditions on the `name` field
    record {|
        string eq?;
        string ne?;
        string lt?;
        string lte?;
        string gt?;
        string gte?;
        string[] anyOf?;
        string contains?;
        string startsWith?;
    |} name?;
|};

# A read create update item with the fields named in the `fields` query parameter.
public type ReadCreateUpdateItemPartial record {|
    # The `id` field of `ReadCreateUpdateItem`
    int id;
    # The `name` field of `ReadCreateUpdateItem`
    string name?;
|};

# A list of read create update items with the fields named in the `fields` query parameter.
public type ReadCreateUpdateItemPartialList record {
    # The read create update items
    ReadCreateUpdateItemPartial[] items;
    # Whether records exist after this page
    boolean hasMore;
};

# The fields of `ReadCreateUpdateItem` that callers can see.
final readonly & dataservice:Field[] dataserviceReadCreateUpdateItemFields = [
    {name: "id", column: "id", 'type: dataservice:INT, key: true},
    {name: "name", column: "name", 'type: dataservice:STRING}
];

# The description of `ReadCreateUpdateItem` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceReadCreateUpdateItemEntity = {
    name: "ReadCreateUpdateItem",
    collection: "readCreateUpdateItems",
    'resource: "readcreateupdateitems",
    datastore: dataservice:H2,
    fields: dataserviceReadCreateUpdateItemFields
};

# The response to creating one read create update item.
public type ReadCreateUpdateItemCreated record {|
    *http:Created;
    # The created read create update item
    ReadCreateUpdateItem body;
|};

# The response to creating several read create update items.
public type ReadCreateUpdateItemListCreated record {|
    *http:Created;
    # The created read create update items
    ReadCreateUpdateItemList body;
|};

# A list of read create delete items.
public type ReadCreateDeleteItemList record {
    # The read create delete items
    ReadCreateDeleteItem[] items;
    # Whether records exist after this page
    boolean hasMore;
};

# The conditions that read create delete items match, as the `filter` query parameter holds them.
public type ReadCreateDeleteItemFilter record {|
    # The conditions on the `id` field
    record {|
        int eq?;
        int ne?;
        int lt?;
        int lte?;
        int gt?;
        int gte?;
        int[] anyOf?;
    |} id?;
    # The conditions on the `name` field
    record {|
        string eq?;
        string ne?;
        string lt?;
        string lte?;
        string gt?;
        string gte?;
        string[] anyOf?;
        string contains?;
        string startsWith?;
    |} name?;
|};

# A read create delete item with the fields named in the `fields` query parameter.
public type ReadCreateDeleteItemPartial record {|
    # The `id` field of `ReadCreateDeleteItem`
    int id;
    # The `name` field of `ReadCreateDeleteItem`
    string name?;
|};

# A list of read create delete items with the fields named in the `fields` query parameter.
public type ReadCreateDeleteItemPartialList record {
    # The read create delete items
    ReadCreateDeleteItemPartial[] items;
    # Whether records exist after this page
    boolean hasMore;
};

# The fields of `ReadCreateDeleteItem` that callers can see.
final readonly & dataservice:Field[] dataserviceReadCreateDeleteItemFields = [
    {name: "id", column: "id", 'type: dataservice:INT, key: true},
    {name: "name", column: "name", 'type: dataservice:STRING}
];

# The description of `ReadCreateDeleteItem` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceReadCreateDeleteItemEntity = {
    name: "ReadCreateDeleteItem",
    collection: "readCreateDeleteItems",
    'resource: "readcreatedeleteitems",
    datastore: dataservice:H2,
    fields: dataserviceReadCreateDeleteItemFields
};

# The response to creating one read create delete item.
public type ReadCreateDeleteItemCreated record {|
    *http:Created;
    # The created read create delete item
    ReadCreateDeleteItem body;
|};

# The response to creating several read create delete items.
public type ReadCreateDeleteItemListCreated record {|
    *http:Created;
    # The created read create delete items
    ReadCreateDeleteItemList body;
|};

# A list of read update delete items.
public type ReadUpdateDeleteItemList record {
    # The read update delete items
    ReadUpdateDeleteItem[] items;
    # Whether records exist after this page
    boolean hasMore;
};

# The conditions that read update delete items match, as the `filter` query parameter holds them.
public type ReadUpdateDeleteItemFilter record {|
    # The conditions on the `id` field
    record {|
        int eq?;
        int ne?;
        int lt?;
        int lte?;
        int gt?;
        int gte?;
        int[] anyOf?;
    |} id?;
    # The conditions on the `name` field
    record {|
        string eq?;
        string ne?;
        string lt?;
        string lte?;
        string gt?;
        string gte?;
        string[] anyOf?;
        string contains?;
        string startsWith?;
    |} name?;
|};

# A read update delete item with the fields named in the `fields` query parameter.
public type ReadUpdateDeleteItemPartial record {|
    # The `id` field of `ReadUpdateDeleteItem`
    int id;
    # The `name` field of `ReadUpdateDeleteItem`
    string name?;
|};

# A list of read update delete items with the fields named in the `fields` query parameter.
public type ReadUpdateDeleteItemPartialList record {
    # The read update delete items
    ReadUpdateDeleteItemPartial[] items;
    # Whether records exist after this page
    boolean hasMore;
};

# The fields of `ReadUpdateDeleteItem` that callers can see.
final readonly & dataservice:Field[] dataserviceReadUpdateDeleteItemFields = [
    {name: "id", column: "id", 'type: dataservice:INT, key: true},
    {name: "name", column: "name", 'type: dataservice:STRING}
];

# The description of `ReadUpdateDeleteItem` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceReadUpdateDeleteItemEntity = {
    name: "ReadUpdateDeleteItem",
    collection: "readUpdateDeleteItems",
    'resource: "readupdatedeleteitems",
    datastore: dataservice:H2,
    fields: dataserviceReadUpdateDeleteItemFields
};

# A list of create update delete items.
public type CreateUpdateDeleteItemList record {
    # The create update delete items
    CreateUpdateDeleteItem[] items;
    # Whether records exist after this page
    boolean hasMore;
};

# The fields of `CreateUpdateDeleteItem` that callers can see.
final readonly & dataservice:Field[] dataserviceCreateUpdateDeleteItemFields = [
    {name: "id", column: "id", 'type: dataservice:INT, key: true},
    {name: "name", column: "name", 'type: dataservice:STRING}
];

# The description of `CreateUpdateDeleteItem` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceCreateUpdateDeleteItemEntity = {
    name: "CreateUpdateDeleteItem",
    collection: "createUpdateDeleteItems",
    'resource: "createupdatedeleteitems",
    datastore: dataservice:H2,
    fields: dataserviceCreateUpdateDeleteItemFields
};

# The response to creating one create update delete item.
public type CreateUpdateDeleteItemCreated record {|
    *http:Created;
    # The created create update delete item
    CreateUpdateDeleteItem body;
|};

# The response to creating several create update delete items.
public type CreateUpdateDeleteItemListCreated record {|
    *http:Created;
    # The created create update delete items
    CreateUpdateDeleteItemList body;
|};

# A list of read create update delete items.
public type ReadCreateUpdateDeleteItemList record {
    # The read create update delete items
    ReadCreateUpdateDeleteItem[] items;
    # Whether records exist after this page
    boolean hasMore;
};

# The conditions that read create update delete items match, as the `filter` query parameter holds them.
public type ReadCreateUpdateDeleteItemFilter record {|
    # The conditions on the `id` field
    record {|
        int eq?;
        int ne?;
        int lt?;
        int lte?;
        int gt?;
        int gte?;
        int[] anyOf?;
    |} id?;
    # The conditions on the `name` field
    record {|
        string eq?;
        string ne?;
        string lt?;
        string lte?;
        string gt?;
        string gte?;
        string[] anyOf?;
        string contains?;
        string startsWith?;
    |} name?;
|};

# A read create update delete item with the fields named in the `fields` query parameter.
public type ReadCreateUpdateDeleteItemPartial record {|
    # The `id` field of `ReadCreateUpdateDeleteItem`
    int id;
    # The `name` field of `ReadCreateUpdateDeleteItem`
    string name?;
|};

# A list of read create update delete items with the fields named in the `fields` query parameter.
public type ReadCreateUpdateDeleteItemPartialList record {
    # The read create update delete items
    ReadCreateUpdateDeleteItemPartial[] items;
    # Whether records exist after this page
    boolean hasMore;
};

# The fields of `ReadCreateUpdateDeleteItem` that callers can see.
final readonly & dataservice:Field[] dataserviceReadCreateUpdateDeleteItemFields = [
    {name: "id", column: "id", 'type: dataservice:INT, key: true},
    {name: "name", column: "name", 'type: dataservice:STRING}
];

# The description of `ReadCreateUpdateDeleteItem` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceReadCreateUpdateDeleteItemEntity = {
    name: "ReadCreateUpdateDeleteItem",
    collection: "readCreateUpdateDeleteItems",
    'resource: "readcreateupdatedeleteitems",
    datastore: dataservice:H2,
    fields: dataserviceReadCreateUpdateDeleteItemFields
};

# The response to creating one read create update delete item.
public type ReadCreateUpdateDeleteItemCreated record {|
    *http:Created;
    # The created read create update delete item
    ReadCreateUpdateDeleteItem body;
|};

# The response to creating several read create update delete items.
public type ReadCreateUpdateDeleteItemListCreated record {|
    *http:Created;
    # The created read create update delete items
    ReadCreateUpdateDeleteItemList body;
|};
