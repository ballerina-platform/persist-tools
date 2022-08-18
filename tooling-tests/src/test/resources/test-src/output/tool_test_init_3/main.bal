import ballerina/io;
configurable int port = ?;
configurable string host = ?;
configurable string user = ?;
configurable string database = ?;
configurable string password = ?;

public function main() {
    io:println(port);
    io:println(user);
    io:println(host);
    io:println(password);
    io:println(database);
}
