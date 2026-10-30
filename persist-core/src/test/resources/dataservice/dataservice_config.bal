// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer.
// It should not be modified by hand.

import ballerina/dataservice;
import ballerina/http;

# The runtime settings of the data service, set in the `[dataserviceConfig]` table of Config.toml.
#
# + port - The port of the listener
# + listenerConfig - The other settings of the listener, such as TLS
# + pageSize - The number of records in a page when the caller gives no size
# + maxPageSize - The largest page, and the most records one create can hold
type DataserviceConfig record {|
    int port = 9090;
    http:ListenerConfiguration listenerConfig = {};
    int pageSize = 100;
    int maxPageSize = 1000;
|};

# The runtime settings of the data service.
configurable DataserviceConfig dataserviceConfig = {};

# The page sizes, checked at startup.
final dataservice:PageConfig dataservicePageConfig =
        check dataservice:pageConfig(dataserviceConfig.pageSize, dataserviceConfig.maxPageSize);
