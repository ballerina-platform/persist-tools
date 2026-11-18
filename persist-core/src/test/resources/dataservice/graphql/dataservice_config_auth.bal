// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer.
// It should not be modified by hand.

import ballerina/dataservice;
import ballerina/dataservice.graphql as dsGraphql;

# The runtime settings of the data service, set in the `[dataserviceConfig]` table of Config.toml.
configurable dsGraphql:AuthConfig dataserviceConfig = ?;

# The page sizes of the data service, checked at startup.
final dataservice:PageConfig dataservicePageConfig =
        check dataservice:toPageConfig(dataserviceConfig.pageSize, dataserviceConfig.maxPageSize);

# Authenticates each request with the configured providers; the program does not start without one.
final dsGraphql:Authenticator dataserviceAuthenticator = check new (dataserviceConfig.serviceConfig.auth);
