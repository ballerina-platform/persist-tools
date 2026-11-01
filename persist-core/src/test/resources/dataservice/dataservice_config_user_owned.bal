// AUTO-GENERATED FILE.

// This file is an auto-generated file by Ballerina persistence layer.

import ballerina/dataservice;
import ballerina/dataservice.http as dsHttp;

# The runtime settings of the data service, set in the `[dataserviceConfig]` table of Config.toml.
configurable dsHttp:Config dataserviceConfig = {};

# The page sizes, checked at startup.
final dataservice:PageConfig dataservicePageConfig =
        check dataservice:pageConfig(dataserviceConfig.pageSize, dataserviceConfig.maxPageSize);
