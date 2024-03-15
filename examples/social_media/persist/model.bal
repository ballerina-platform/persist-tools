import ballerina/persist as _;
import ballerina/time;

public enum Gender {
    MALE,
    FEMALE
}

public type User record{|
    readonly string firstName;
    readonly string lastName;
    string email;
    int age;
    time:Date dateOfBirth;
    Gender gender;
    boolean isMarried;
    string spouseName?;
	Post[] post;
|};

public type Post record{|
    readonly int id;
    string title;
    string content;
    User user;
	Comment[] comment;
|};

public type Comment record {|
    readonly int id;
    string message;
    time:Civil timeStamp;
    Post post;
|};
