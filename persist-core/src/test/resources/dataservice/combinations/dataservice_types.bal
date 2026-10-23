// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/http;

# A list of read items.
#
# + items - The read items
public type ReadItemList record {|
    ReadItem[] items;
|};

# A list of create items.
#
# + items - The create items
public type CreateItemList record {|
    CreateItem[] items;
|};

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
public type ReadCreateItemList record {|
    ReadCreateItem[] items;
|};

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
public type ReadUpdateItemList record {|
    ReadUpdateItem[] items;
|};

# A list of read delete items.
#
# + items - The read delete items
public type ReadDeleteItemList record {|
    ReadDeleteItem[] items;
|};

# A list of create update items.
#
# + items - The create update items
public type CreateUpdateItemList record {|
    CreateUpdateItem[] items;
|};

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
public type CreateDeleteItemList record {|
    CreateDeleteItem[] items;
|};

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
public type ReadCreateUpdateItemList record {|
    ReadCreateUpdateItem[] items;
|};

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
public type ReadCreateDeleteItemList record {|
    ReadCreateDeleteItem[] items;
|};

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
public type ReadUpdateDeleteItemList record {|
    ReadUpdateDeleteItem[] items;
|};

# A list of create update delete items.
#
# + items - The create update delete items
public type CreateUpdateDeleteItemList record {|
    CreateUpdateDeleteItem[] items;
|};

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
public type ReadCreateUpdateDeleteItemList record {|
    ReadCreateUpdateDeleteItem[] items;
|};

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
