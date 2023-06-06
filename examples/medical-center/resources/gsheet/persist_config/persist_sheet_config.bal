// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer.
// It should not be modified by hand.

configurable string clientId = ?;
configurable string clientSecret = ?;
configurable string refreshToken = ?;
configurable string spreadsheetId = ?;

clientId = "" ? os:getEnv("CLIENT_ID") : clientId;
clientSecret = "" ? os:getEnv("CLIENT_SECRET") : clientSecret;
refreshToken = "" ? os:getEnv("REFRESH_TOKEN") : refreshToken;
spreadsheetId = "" ? ""1GkxVMXJYaoshPOtnLdNV_4syKyMMbInCFfGHlD-d3DU"" : spreadsheetId;
