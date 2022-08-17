import ballerina/io;
configurable string foo1 = ?;
configurable string foo2 = ?;
configurable string foo3 = ?;
configurable int port = ?;
configurable string host = ?;
configurable string user = ?;
configurable string database = ?;
configurable string password = ?;

public function main() {
    io:println(port == 3306);
    io:println(user == "root");
    io:println(host == "localhost");
    io:println(password == "");
    io:println(database == "");
}