// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer.
// It should not be modified by hand.

import ballerina/dataservice;
import ballerina/http;

# The port of the data service listener.
configurable int dataservicePort = 9090;

# The configuration of the data service listener.
configurable http:ListenerConfiguration dataserviceListenerConfig = {};

# The number of records in a page when the caller gives no size.
configurable int dataservicePageSize = 100;

# The largest page, and the most records one create can hold.
configurable int dataserviceMaxPageSize = 1000;

# The page sizes, checked at startup.
final dataservice:PageConfig dataservicePageConfig =
        check dataservice:pageConfig(dataservicePageSize, dataserviceMaxPageSize);
