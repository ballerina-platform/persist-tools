// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/dataservice;
import ballerina/http;

# A list of read items.
#
# + items - The read items
# + hasMore - Whether records exist after this page
public type ReadItemList record {
    ReadItem[] items;
    boolean hasMore;
};

# The conditions that read items match, as the `filter` query parameter holds them.
#
# + id - The conditions on the `id` field
# + name - The conditions on the `name` field
public type ReadItemFilter record {|
    record {|
        int eq?;
        int ne?;
        int lt?;
        int lte?;
        int gt?;
        int gte?;
        int[] anyOf?;
    |} id?;
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
#
# + id - The `id` field of `ReadItem`
# + name - The `name` field of `ReadItem`
public type ReadItemPartial record {|
    int id;
    string name?;
|};

# A list of read items with the fields named in the `fields` query parameter.
#
# + items - The read items
# + hasMore - Whether records exist after this page
public type ReadItemPartialList record {
    ReadItemPartial[] items;
    boolean hasMore;
};

# The description of `ReadItem` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceReadItemEntity = {
    name: "ReadItem",
    collection: "readItems",
    'resource: "readitems",
    datastore: dataservice:H2,
    fields: [
        {name: "id", column: "id", 'type: dataservice:INT, key: true},
        {name: "name", column: "name", 'type: dataservice:STRING}
    ]
};

# A list of create items.
#
# + items - The create items
# + hasMore - Whether records exist after this page
public type CreateItemList record {
    CreateItem[] items;
    boolean hasMore;
};

# The description of `CreateItem` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceCreateItemEntity = {
    name: "CreateItem",
    collection: "createItems",
    'resource: "createitems",
    datastore: dataservice:H2,
    fields: [
        {name: "id", column: "id", 'type: dataservice:INT, key: true},
        {name: "name", column: "name", 'type: dataservice:STRING}
    ]
};

# The response to creating one create item.
#
# + body - The created create item
public type CreateItemCreated record {|
    *http:Created;
    CreateItem body;
|};

# The response to creating several create items.
#
# + body - The created create items
public type CreateItemListCreated record {|
    *http:Created;
    CreateItemList body;
|};

# A list of read create items.
#
# + items - The read create items
# + hasMore - Whether records exist after this page
public type ReadCreateItemList record {
    ReadCreateItem[] items;
    boolean hasMore;
};

# The conditions that read create items match, as the `filter` query parameter holds them.
#
# + id - The conditions on the `id` field
# + name - The conditions on the `name` field
public type ReadCreateItemFilter record {|
    record {|
        int eq?;
        int ne?;
        int lt?;
        int lte?;
        int gt?;
        int gte?;
        int[] anyOf?;
    |} id?;
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
#
# + id - The `id` field of `ReadCreateItem`
# + name - The `name` field of `ReadCreateItem`
public type ReadCreateItemPartial record {|
    int id;
    string name?;
|};

# A list of read create items with the fields named in the `fields` query parameter.
#
# + items - The read create items
# + hasMore - Whether records exist after this page
public type ReadCreateItemPartialList record {
    ReadCreateItemPartial[] items;
    boolean hasMore;
};

# The description of `ReadCreateItem` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceReadCreateItemEntity = {
    name: "ReadCreateItem",
    collection: "readCreateItems",
    'resource: "readcreateitems",
    datastore: dataservice:H2,
    fields: [
        {name: "id", column: "id", 'type: dataservice:INT, key: true},
        {name: "name", column: "name", 'type: dataservice:STRING}
    ]
};

# The response to creating one read create item.
#
# + body - The created read create item
public type ReadCreateItemCreated record {|
    *http:Created;
    ReadCreateItem body;
|};

# The response to creating several read create items.
#
# + body - The created read create items
public type ReadCreateItemListCreated record {|
    *http:Created;
    ReadCreateItemList body;
|};

# A list of read update items.
#
# + items - The read update items
# + hasMore - Whether records exist after this page
public type ReadUpdateItemList record {
    ReadUpdateItem[] items;
    boolean hasMore;
};

# The conditions that read update items match, as the `filter` query parameter holds them.
#
# + id - The conditions on the `id` field
# + name - The conditions on the `name` field
public type ReadUpdateItemFilter record {|
    record {|
        int eq?;
        int ne?;
        int lt?;
        int lte?;
        int gt?;
        int gte?;
        int[] anyOf?;
    |} id?;
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
#
# + id - The `id` field of `ReadUpdateItem`
# + name - The `name` field of `ReadUpdateItem`
public type ReadUpdateItemPartial record {|
    int id;
    string name?;
|};

# A list of read update items with the fields named in the `fields` query parameter.
#
# + items - The read update items
# + hasMore - Whether records exist after this page
public type ReadUpdateItemPartialList record {
    ReadUpdateItemPartial[] items;
    boolean hasMore;
};

# The description of `ReadUpdateItem` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceReadUpdateItemEntity = {
    name: "ReadUpdateItem",
    collection: "readUpdateItems",
    'resource: "readupdateitems",
    datastore: dataservice:H2,
    fields: [
        {name: "id", column: "id", 'type: dataservice:INT, key: true},
        {name: "name", column: "name", 'type: dataservice:STRING}
    ]
};

# A list of read delete items.
#
# + items - The read delete items
# + hasMore - Whether records exist after this page
public type ReadDeleteItemList record {
    ReadDeleteItem[] items;
    boolean hasMore;
};

# The conditions that read delete items match, as the `filter` query parameter holds them.
#
# + id - The conditions on the `id` field
# + name - The conditions on the `name` field
public type ReadDeleteItemFilter record {|
    record {|
        int eq?;
        int ne?;
        int lt?;
        int lte?;
        int gt?;
        int gte?;
        int[] anyOf?;
    |} id?;
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
#
# + id - The `id` field of `ReadDeleteItem`
# + name - The `name` field of `ReadDeleteItem`
public type ReadDeleteItemPartial record {|
    int id;
    string name?;
|};

# A list of read delete items with the fields named in the `fields` query parameter.
#
# + items - The read delete items
# + hasMore - Whether records exist after this page
public type ReadDeleteItemPartialList record {
    ReadDeleteItemPartial[] items;
    boolean hasMore;
};

# The description of `ReadDeleteItem` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceReadDeleteItemEntity = {
    name: "ReadDeleteItem",
    collection: "readDeleteItems",
    'resource: "readdeleteitems",
    datastore: dataservice:H2,
    fields: [
        {name: "id", column: "id", 'type: dataservice:INT, key: true},
        {name: "name", column: "name", 'type: dataservice:STRING}
    ]
};

# A list of create update items.
#
# + items - The create update items
# + hasMore - Whether records exist after this page
public type CreateUpdateItemList record {
    CreateUpdateItem[] items;
    boolean hasMore;
};

# The description of `CreateUpdateItem` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceCreateUpdateItemEntity = {
    name: "CreateUpdateItem",
    collection: "createUpdateItems",
    'resource: "createupdateitems",
    datastore: dataservice:H2,
    fields: [
        {name: "id", column: "id", 'type: dataservice:INT, key: true},
        {name: "name", column: "name", 'type: dataservice:STRING}
    ]
};

# The response to creating one create update item.
#
# + body - The created create update item
public type CreateUpdateItemCreated record {|
    *http:Created;
    CreateUpdateItem body;
|};

# The response to creating several create update items.
#
# + body - The created create update items
public type CreateUpdateItemListCreated record {|
    *http:Created;
    CreateUpdateItemList body;
|};

# A list of create delete items.
#
# + items - The create delete items
# + hasMore - Whether records exist after this page
public type CreateDeleteItemList record {
    CreateDeleteItem[] items;
    boolean hasMore;
};

# The description of `CreateDeleteItem` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceCreateDeleteItemEntity = {
    name: "CreateDeleteItem",
    collection: "createDeleteItems",
    'resource: "createdeleteitems",
    datastore: dataservice:H2,
    fields: [
        {name: "id", column: "id", 'type: dataservice:INT, key: true},
        {name: "name", column: "name", 'type: dataservice:STRING}
    ]
};

# The response to creating one create delete item.
#
# + body - The created create delete item
public type CreateDeleteItemCreated record {|
    *http:Created;
    CreateDeleteItem body;
|};

# The response to creating several create delete items.
#
# + body - The created create delete items
public type CreateDeleteItemListCreated record {|
    *http:Created;
    CreateDeleteItemList body;
|};

# A list of read create update items.
#
# + items - The read create update items
# + hasMore - Whether records exist after this page
public type ReadCreateUpdateItemList record {
    ReadCreateUpdateItem[] items;
    boolean hasMore;
};

# The conditions that read create update items match, as the `filter` query parameter holds them.
#
# + id - The conditions on the `id` field
# + name - The conditions on the `name` field
public type ReadCreateUpdateItemFilter record {|
    record {|
        int eq?;
        int ne?;
        int lt?;
        int lte?;
        int gt?;
        int gte?;
        int[] anyOf?;
    |} id?;
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
#
# + id - The `id` field of `ReadCreateUpdateItem`
# + name - The `name` field of `ReadCreateUpdateItem`
public type ReadCreateUpdateItemPartial record {|
    int id;
    string name?;
|};

# A list of read create update items with the fields named in the `fields` query parameter.
#
# + items - The read create update items
# + hasMore - Whether records exist after this page
public type ReadCreateUpdateItemPartialList record {
    ReadCreateUpdateItemPartial[] items;
    boolean hasMore;
};

# The description of `ReadCreateUpdateItem` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceReadCreateUpdateItemEntity = {
    name: "ReadCreateUpdateItem",
    collection: "readCreateUpdateItems",
    'resource: "readcreateupdateitems",
    datastore: dataservice:H2,
    fields: [
        {name: "id", column: "id", 'type: dataservice:INT, key: true},
        {name: "name", column: "name", 'type: dataservice:STRING}
    ]
};

# The response to creating one read create update item.
#
# + body - The created read create update item
public type ReadCreateUpdateItemCreated record {|
    *http:Created;
    ReadCreateUpdateItem body;
|};

# The response to creating several read create update items.
#
# + body - The created read create update items
public type ReadCreateUpdateItemListCreated record {|
    *http:Created;
    ReadCreateUpdateItemList body;
|};

# A list of read create delete items.
#
# + items - The read create delete items
# + hasMore - Whether records exist after this page
public type ReadCreateDeleteItemList record {
    ReadCreateDeleteItem[] items;
    boolean hasMore;
};

# The conditions that read create delete items match, as the `filter` query parameter holds them.
#
# + id - The conditions on the `id` field
# + name - The conditions on the `name` field
public type ReadCreateDeleteItemFilter record {|
    record {|
        int eq?;
        int ne?;
        int lt?;
        int lte?;
        int gt?;
        int gte?;
        int[] anyOf?;
    |} id?;
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
#
# + id - The `id` field of `ReadCreateDeleteItem`
# + name - The `name` field of `ReadCreateDeleteItem`
public type ReadCreateDeleteItemPartial record {|
    int id;
    string name?;
|};

# A list of read create delete items with the fields named in the `fields` query parameter.
#
# + items - The read create delete items
# + hasMore - Whether records exist after this page
public type ReadCreateDeleteItemPartialList record {
    ReadCreateDeleteItemPartial[] items;
    boolean hasMore;
};

# The description of `ReadCreateDeleteItem` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceReadCreateDeleteItemEntity = {
    name: "ReadCreateDeleteItem",
    collection: "readCreateDeleteItems",
    'resource: "readcreatedeleteitems",
    datastore: dataservice:H2,
    fields: [
        {name: "id", column: "id", 'type: dataservice:INT, key: true},
        {name: "name", column: "name", 'type: dataservice:STRING}
    ]
};

# The response to creating one read create delete item.
#
# + body - The created read create delete item
public type ReadCreateDeleteItemCreated record {|
    *http:Created;
    ReadCreateDeleteItem body;
|};

# The response to creating several read create delete items.
#
# + body - The created read create delete items
public type ReadCreateDeleteItemListCreated record {|
    *http:Created;
    ReadCreateDeleteItemList body;
|};

# A list of read update delete items.
#
# + items - The read update delete items
# + hasMore - Whether records exist after this page
public type ReadUpdateDeleteItemList record {
    ReadUpdateDeleteItem[] items;
    boolean hasMore;
};

# The conditions that read update delete items match, as the `filter` query parameter holds them.
#
# + id - The conditions on the `id` field
# + name - The conditions on the `name` field
public type ReadUpdateDeleteItemFilter record {|
    record {|
        int eq?;
        int ne?;
        int lt?;
        int lte?;
        int gt?;
        int gte?;
        int[] anyOf?;
    |} id?;
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
#
# + id - The `id` field of `ReadUpdateDeleteItem`
# + name - The `name` field of `ReadUpdateDeleteItem`
public type ReadUpdateDeleteItemPartial record {|
    int id;
    string name?;
|};

# A list of read update delete items with the fields named in the `fields` query parameter.
#
# + items - The read update delete items
# + hasMore - Whether records exist after this page
public type ReadUpdateDeleteItemPartialList record {
    ReadUpdateDeleteItemPartial[] items;
    boolean hasMore;
};

# The description of `ReadUpdateDeleteItem` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceReadUpdateDeleteItemEntity = {
    name: "ReadUpdateDeleteItem",
    collection: "readUpdateDeleteItems",
    'resource: "readupdatedeleteitems",
    datastore: dataservice:H2,
    fields: [
        {name: "id", column: "id", 'type: dataservice:INT, key: true},
        {name: "name", column: "name", 'type: dataservice:STRING}
    ]
};

# A list of create update delete items.
#
# + items - The create update delete items
# + hasMore - Whether records exist after this page
public type CreateUpdateDeleteItemList record {
    CreateUpdateDeleteItem[] items;
    boolean hasMore;
};

# The description of `CreateUpdateDeleteItem` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceCreateUpdateDeleteItemEntity = {
    name: "CreateUpdateDeleteItem",
    collection: "createUpdateDeleteItems",
    'resource: "createupdatedeleteitems",
    datastore: dataservice:H2,
    fields: [
        {name: "id", column: "id", 'type: dataservice:INT, key: true},
        {name: "name", column: "name", 'type: dataservice:STRING}
    ]
};

# The response to creating one create update delete item.
#
# + body - The created create update delete item
public type CreateUpdateDeleteItemCreated record {|
    *http:Created;
    CreateUpdateDeleteItem body;
|};

# The response to creating several create update delete items.
#
# + body - The created create update delete items
public type CreateUpdateDeleteItemListCreated record {|
    *http:Created;
    CreateUpdateDeleteItemList body;
|};

# A list of read create update delete items.
#
# + items - The read create update delete items
# + hasMore - Whether records exist after this page
public type ReadCreateUpdateDeleteItemList record {
    ReadCreateUpdateDeleteItem[] items;
    boolean hasMore;
};

# The conditions that read create update delete items match, as the `filter` query parameter holds them.
#
# + id - The conditions on the `id` field
# + name - The conditions on the `name` field
public type ReadCreateUpdateDeleteItemFilter record {|
    record {|
        int eq?;
        int ne?;
        int lt?;
        int lte?;
        int gt?;
        int gte?;
        int[] anyOf?;
    |} id?;
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
#
# + id - The `id` field of `ReadCreateUpdateDeleteItem`
# + name - The `name` field of `ReadCreateUpdateDeleteItem`
public type ReadCreateUpdateDeleteItemPartial record {|
    int id;
    string name?;
|};

# A list of read create update delete items with the fields named in the `fields` query parameter.
#
# + items - The read create update delete items
# + hasMore - Whether records exist after this page
public type ReadCreateUpdateDeleteItemPartialList record {
    ReadCreateUpdateDeleteItemPartial[] items;
    boolean hasMore;
};

# The description of `ReadCreateUpdateDeleteItem` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceReadCreateUpdateDeleteItemEntity = {
    name: "ReadCreateUpdateDeleteItem",
    collection: "readCreateUpdateDeleteItems",
    'resource: "readcreateupdatedeleteitems",
    datastore: dataservice:H2,
    fields: [
        {name: "id", column: "id", 'type: dataservice:INT, key: true},
        {name: "name", column: "name", 'type: dataservice:STRING}
    ]
};

# The response to creating one read create update delete item.
#
# + body - The created read create update delete item
public type ReadCreateUpdateDeleteItemCreated record {|
    *http:Created;
    ReadCreateUpdateDeleteItem body;
|};

# The response to creating several read create update delete items.
#
# + body - The created read create update delete items
public type ReadCreateUpdateDeleteItemListCreated record {|
    *http:Created;
    ReadCreateUpdateDeleteItemList body;
|};
