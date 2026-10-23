import ballerina/dataservice;
import ballerina/persist as _;

@dataservice:Expose {operations: [dataservice:READ]}
type ReadItem record {|
    readonly int id;
    string name;
|};

@dataservice:Expose {operations: [dataservice:CREATE]}
type CreateItem record {|
    readonly int id;
    string name;
|};

@dataservice:Expose {operations: [dataservice:UPDATE]}
type UpdateItem record {|
    readonly int id;
    string name;
|};

@dataservice:Expose {operations: [dataservice:DELETE]}
type DeleteItem record {|
    readonly int id;
    string name;
|};

@dataservice:Expose {operations: [dataservice:READ, dataservice:CREATE]}
type ReadCreateItem record {|
    readonly int id;
    string name;
|};

@dataservice:Expose {operations: [dataservice:READ, dataservice:UPDATE]}
type ReadUpdateItem record {|
    readonly int id;
    string name;
|};

@dataservice:Expose {operations: [dataservice:READ, dataservice:DELETE]}
type ReadDeleteItem record {|
    readonly int id;
    string name;
|};

@dataservice:Expose {operations: [dataservice:CREATE, dataservice:UPDATE]}
type CreateUpdateItem record {|
    readonly int id;
    string name;
|};

@dataservice:Expose {operations: [dataservice:CREATE, dataservice:DELETE]}
type CreateDeleteItem record {|
    readonly int id;
    string name;
|};

@dataservice:Expose {operations: [dataservice:UPDATE, dataservice:DELETE]}
type UpdateDeleteItem record {|
    readonly int id;
    string name;
|};

@dataservice:Expose {operations: [dataservice:READ, dataservice:CREATE, dataservice:UPDATE]}
type ReadCreateUpdateItem record {|
    readonly int id;
    string name;
|};

@dataservice:Expose {operations: [dataservice:READ, dataservice:CREATE, dataservice:DELETE]}
type ReadCreateDeleteItem record {|
    readonly int id;
    string name;
|};

@dataservice:Expose {operations: [dataservice:READ, dataservice:UPDATE, dataservice:DELETE]}
type ReadUpdateDeleteItem record {|
    readonly int id;
    string name;
|};

@dataservice:Expose {operations: [dataservice:CREATE, dataservice:UPDATE, dataservice:DELETE]}
type CreateUpdateDeleteItem record {|
    readonly int id;
    string name;
|};

@dataservice:Expose {operations: [dataservice:READ, dataservice:CREATE, dataservice:UPDATE, dataservice:DELETE]}
type ReadCreateUpdateDeleteItem record {|
    readonly int id;
    string name;
|};
