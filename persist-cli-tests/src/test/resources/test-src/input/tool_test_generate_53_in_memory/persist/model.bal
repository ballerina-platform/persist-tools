import ballerina/persist as _;
import ballerina/time;

type User record {|
    readonly int id;
    string name;
    time:Date birthDate;
    string mobileNumber;
    Post[] posts;
	Follow? leader;
	Follow? follower;
|};

type Post record {|
    readonly int id;
    string description;
    string tags;
    string category;
    time:Date created_date;
    User user;
|};

type Follow record {|
    readonly int id;
    User leader;
    User follower;
    time:Date created_date;
|};
