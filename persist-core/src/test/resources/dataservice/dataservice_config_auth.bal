// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer.
// It should not be modified by hand.

import ballerina/dataservice;
import ballerina/dataservice.http as dsHttp;
import ballerina/http;

# The runtime settings of the data service, set in the `[dataserviceConfig]` table of Config.toml.
configurable dsHttp:AuthConfig dataserviceConfig = ?;

# The page sizes, checked at startup.
final dataservice:PageConfig dataservicePageConfig =
        check dataservice:toPageConfig(dataserviceConfig.pageSize, dataserviceConfig.maxPageSize);

# The authentication providers, checked at startup.
final http:ListenerAuthConfig[] dataserviceAuthConfig =
        check dsHttp:initAuthProviders(dataserviceConfig.serviceConfig.auth);
