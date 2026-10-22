import ballerina/graphql;
import ballerina/mcp;

// User-written services on the generated listener, served on the same port.
service /custom on dataserviceListener {

    resource function get ping() returns string => "pong";
}

listener graphql:Listener dataserviceGraphqlListener = new (dataserviceListener);

service /graphql on dataserviceGraphqlListener {

    resource function get greeting() returns string => "hello";
}

listener mcp:StreamableHttpListener dataserviceMcpListener = check new (dataserviceListener);

@mcp:ServiceConfig {info: {name: "custom", version: "1.0.0"}}
service mcp:Service /mcp on dataserviceMcpListener {

    # Answers a ping.
    # + return - pong
    remote function ping() returns string => "pong";
}

public function main() {
}
