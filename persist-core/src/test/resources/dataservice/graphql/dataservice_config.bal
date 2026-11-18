// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer.
// It should not be modified by hand.

import ballerina/dataservice;
import ballerina/dataservice.graphql as dsGraphql;

# The runtime settings of the data service, set in the `[dataserviceConfig]` table of Config.toml.
configurable dsGraphql:Config dataserviceConfig = {};

# The page sizes of the data service, checked at startup.
final dataservice:PageConfig dataservicePageConfig =
        check dataservice:toPageConfig(dataserviceConfig.pageSize, dataserviceConfig.maxPageSize);
