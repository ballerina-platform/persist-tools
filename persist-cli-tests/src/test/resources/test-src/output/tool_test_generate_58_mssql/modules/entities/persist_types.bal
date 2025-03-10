// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/time;

public enum EnumType {
    TYPE_1,
    TYPE_2,
    TYPE_3,
    TYPE_4
}

public enum Category {
    FOOD,
    TRAVEL,
    FASHION = "fashion",
    SPORTS,
    TECHNOLOGY,
    OTHERS
}

public enum Gender {
    MALE,
    FEMALE
}

public type AllTypes record {|
    readonly int id;
    boolean booleanType;
    int intType;
    float floatType;
    decimal decimalType;
    string stringType;
    byte[] byteArrayType;
    time:Date dateType;
    time:TimeOfDay timeOfDayType;
    time:Utc utcType;
    time:Civil civilType;
    boolean? booleanTypeOptional;
    int? intTypeOptional;
    float? floatTypeOptional;
    decimal? decimalTypeOptional;
    string? stringTypeOptional;
    byte[]? byteArrayTypeOptional;
    time:Date? dateTypeOptional;
    time:TimeOfDay? timeOfDayTypeOptional;
    time:Utc? utcTypeOptional;
    time:Civil? civilTypeOptional;
    EnumType enumType;
    EnumType? enumTypeOptional;
|};

public type AllTypesOptionalized record {|
    int id?;
    boolean booleanType?;
    int intType?;
    float floatType?;
    decimal decimalType?;
    string stringType?;
    byte[] byteArrayType?;
    time:Date dateType?;
    time:TimeOfDay timeOfDayType?;
    time:Utc utcType?;
    time:Civil civilType?;
    boolean? booleanTypeOptional?;
    int? intTypeOptional?;
    float? floatTypeOptional?;
    decimal? decimalTypeOptional?;
    string? stringTypeOptional?;
    byte[]? byteArrayTypeOptional?;
    time:Date? dateTypeOptional?;
    time:TimeOfDay? timeOfDayTypeOptional?;
    time:Utc? utcTypeOptional?;
    time:Civil? civilTypeOptional?;
    EnumType enumType?;
    EnumType? enumTypeOptional?;
|};

public type AllTypesTargetType typedesc<AllTypesOptionalized>;

public type AllTypesInsert AllTypes;

public type AllTypesUpdate record {|
    boolean booleanType?;
    int intType?;
    float floatType?;
    decimal decimalType?;
    string stringType?;
    byte[] byteArrayType?;
    time:Date dateType?;
    time:TimeOfDay timeOfDayType?;
    time:Utc utcType?;
    time:Civil civilType?;
    boolean? booleanTypeOptional?;
    int? intTypeOptional?;
    float? floatTypeOptional?;
    decimal? decimalTypeOptional?;
    string? stringTypeOptional?;
    byte[]? byteArrayTypeOptional?;
    time:Date? dateTypeOptional?;
    time:TimeOfDay? timeOfDayTypeOptional?;
    time:Utc? utcTypeOptional?;
    time:Civil? civilTypeOptional?;
    EnumType enumType?;
    EnumType? enumTypeOptional?;
|};

public type StringIdRecord record {|
    readonly string id;
    string randomField;
|};

public type StringIdRecordOptionalized record {|
    string id?;
    string randomField?;
|};

public type StringIdRecordTargetType typedesc<StringIdRecordOptionalized>;

public type StringIdRecordInsert StringIdRecord;

public type StringIdRecordUpdate record {|
    string randomField?;
|};

public type IntIdRecord record {|
    readonly int id;
    string randomField;
|};

public type IntIdRecordOptionalized record {|
    int id?;
    string randomField?;
|};

public type IntIdRecordTargetType typedesc<IntIdRecordOptionalized>;

public type IntIdRecordInsert IntIdRecord;

public type IntIdRecordUpdate record {|
    string randomField?;
|};

public type FloatIdRecord record {|
    readonly float id;
    string randomField;
|};

public type FloatIdRecordOptionalized record {|
    float id?;
    string randomField?;
|};

public type FloatIdRecordTargetType typedesc<FloatIdRecordOptionalized>;

public type FloatIdRecordInsert FloatIdRecord;

public type FloatIdRecordUpdate record {|
    string randomField?;
|};

public type DecimalIdRecord record {|
    readonly decimal id;
    string randomField;
|};

public type DecimalIdRecordOptionalized record {|
    decimal id?;
    string randomField?;
|};

public type DecimalIdRecordTargetType typedesc<DecimalIdRecordOptionalized>;

public type DecimalIdRecordInsert DecimalIdRecord;

public type DecimalIdRecordUpdate record {|
    string randomField?;
|};

public type BooleanIdRecord record {|
    readonly boolean id;
    string randomField;
|};

public type BooleanIdRecordOptionalized record {|
    boolean id?;
    string randomField?;
|};

public type BooleanIdRecordTargetType typedesc<BooleanIdRecordOptionalized>;

public type BooleanIdRecordInsert BooleanIdRecord;

public type BooleanIdRecordUpdate record {|
    string randomField?;
|};

public type CompositeAssociationRecord record {|
    readonly string id;
    string randomField;
    boolean alltypesidrecordBooleanType;
    int alltypesidrecordIntType;
    float alltypesidrecordFloatType;
    decimal alltypesidrecordDecimalType;
    string alltypesidrecordStringType;
|};

public type CompositeAssociationRecordOptionalized record {|
    string id?;
    string randomField?;
    boolean alltypesidrecordBooleanType?;
    int alltypesidrecordIntType?;
    float alltypesidrecordFloatType?;
    decimal alltypesidrecordDecimalType?;
    string alltypesidrecordStringType?;
|};

public type CompositeAssociationRecordWithRelations record {|
    *CompositeAssociationRecordOptionalized;
    AllTypesIdRecordOptionalized allTypesIdRecord?;
|};

public type CompositeAssociationRecordTargetType typedesc<CompositeAssociationRecordWithRelations>;

public type CompositeAssociationRecordInsert CompositeAssociationRecord;

public type CompositeAssociationRecordUpdate record {|
    string randomField?;
    boolean alltypesidrecordBooleanType?;
    int alltypesidrecordIntType?;
    float alltypesidrecordFloatType?;
    decimal alltypesidrecordDecimalType?;
    string alltypesidrecordStringType?;
|};

public type AllTypesIdRecord record {|
    readonly boolean booleanType;
    readonly int intType;
    readonly float floatType;
    readonly decimal decimalType;
    readonly string stringType;
    string randomField;
|};

public type AllTypesIdRecordOptionalized record {|
    boolean booleanType?;
    int intType?;
    float floatType?;
    decimal decimalType?;
    string stringType?;
    string randomField?;
|};

public type AllTypesIdRecordWithRelations record {|
    *AllTypesIdRecordOptionalized;
    CompositeAssociationRecordOptionalized compositeAssociationRecord?;
|};

public type AllTypesIdRecordTargetType typedesc<AllTypesIdRecordWithRelations>;

public type AllTypesIdRecordInsert AllTypesIdRecord;

public type AllTypesIdRecordUpdate record {|
    string randomField?;
|};

public type User record {|
    readonly int id;
    string name;
    time:Date birthDate;
    string mobileNumber;
|};

public type UserOptionalized record {|
    int id?;
    string name?;
    time:Date birthDate?;
    string mobileNumber?;
|};

public type UserWithRelations record {|
    *UserOptionalized;
    PostOptionalized[] posts?;
    CommentOptionalized[] comments?;
    FollowOptionalized[] followers?;
    FollowOptionalized[] following?;
|};

public type UserTargetType typedesc<UserWithRelations>;

public type UserInsert User;

public type UserUpdate record {|
    string name?;
    time:Date birthDate?;
    string mobileNumber?;
|};

public type Post record {|
    readonly int id;
    string description;
    string tags;
    Category category;
    time:Civil timestamp;
    int userId;
|};

public type PostOptionalized record {|
    int id?;
    string description?;
    string tags?;
    Category category?;
    time:Civil timestamp?;
    int userId?;
|};

public type PostWithRelations record {|
    *PostOptionalized;
    UserOptionalized user?;
    CommentOptionalized[] comments?;
|};

public type PostTargetType typedesc<PostWithRelations>;

public type PostInsert Post;

public type PostUpdate record {|
    string description?;
    string tags?;
    Category category?;
    time:Civil timestamp?;
    int userId?;
|};

public type Follow record {|
    readonly int id;
    int leaderId;
    int followerId;
    time:Civil timestamp;
|};

public type FollowOptionalized record {|
    int id?;
    int leaderId?;
    int followerId?;
    time:Civil timestamp?;
|};

public type FollowWithRelations record {|
    *FollowOptionalized;
    UserOptionalized leader?;
    UserOptionalized follower?;
|};

public type FollowTargetType typedesc<FollowWithRelations>;

public type FollowInsert Follow;

public type FollowUpdate record {|
    int leaderId?;
    int followerId?;
    time:Civil timestamp?;
|};

public type Comment record {|
    readonly int id;
    string comment;
    time:Civil timesteamp;
    int userId;
    int postId;
|};

public type CommentOptionalized record {|
    int id?;
    string comment?;
    time:Civil timesteamp?;
    int userId?;
    int postId?;
|};

public type CommentWithRelations record {|
    *CommentOptionalized;
    UserOptionalized user?;
    PostOptionalized post?;
|};

public type CommentTargetType typedesc<CommentWithRelations>;

public type CommentInsert Comment;

public type CommentUpdate record {|
    string comment?;
    time:Civil timesteamp?;
    int userId?;
    int postId?;
|};

public type Employee record {|
    readonly string empNo;
    string firstName;
    string lastName;
    time:Date birthDate;
    Gender gender;
    time:Date hireDate;
    string departmentDeptNo;
    string workspaceWorkspaceId;
|};

public type EmployeeOptionalized record {|
    string empNo?;
    string firstName?;
    string lastName?;
    time:Date birthDate?;
    Gender gender?;
    time:Date hireDate?;
    string departmentDeptNo?;
    string workspaceWorkspaceId?;
|};

public type EmployeeWithRelations record {|
    *EmployeeOptionalized;
    DepartmentOptionalized department?;
    WorkspaceOptionalized workspace?;
|};

public type EmployeeTargetType typedesc<EmployeeWithRelations>;

public type EmployeeInsert Employee;

public type EmployeeUpdate record {|
    string firstName?;
    string lastName?;
    time:Date birthDate?;
    Gender gender?;
    time:Date hireDate?;
    string departmentDeptNo?;
    string workspaceWorkspaceId?;
|};

public type Workspace record {|
    readonly string workspaceId;
    string workspaceType;
    string locationBuildingCode;

|};

public type WorkspaceOptionalized record {|
    string workspaceId?;
    string workspaceType?;
    string locationBuildingCode?;
|};

public type WorkspaceWithRelations record {|
    *WorkspaceOptionalized;
    BuildingOptionalized location?;
    EmployeeOptionalized[] employees?;
|};

public type WorkspaceTargetType typedesc<WorkspaceWithRelations>;

public type WorkspaceInsert Workspace;

public type WorkspaceUpdate record {|
    string workspaceType?;
    string locationBuildingCode?;
|};

public type Building record {|
    readonly string buildingCode;
    string city;
    string state;
    string country;
    string postalCode;
    string 'type;

|};

public type BuildingOptionalized record {|
    string buildingCode?;
    string city?;
    string state?;
    string country?;
    string postalCode?;
    string 'type?;
|};

public type BuildingWithRelations record {|
    *BuildingOptionalized;
    WorkspaceOptionalized[] workspaces?;
|};

public type BuildingTargetType typedesc<BuildingWithRelations>;

public type BuildingInsert Building;

public type BuildingUpdate record {|
    string city?;
    string state?;
    string country?;
    string postalCode?;
    string 'type?;
|};

public type Department record {|
    readonly string deptNo;
    string deptName;

|};

public type DepartmentOptionalized record {|
    string deptNo?;
    string deptName?;
|};

public type DepartmentWithRelations record {|
    *DepartmentOptionalized;
    EmployeeOptionalized[] employees?;
|};

public type DepartmentTargetType typedesc<DepartmentWithRelations>;

public type DepartmentInsert Department;

public type DepartmentUpdate record {|
    string deptName?;
|};

public type OrderItem record {|
    readonly string orderId;
    readonly string itemId;
    int quantity;
    string notes;
|};

public type OrderItemOptionalized record {|
    string orderId?;
    string itemId?;
    int quantity?;
    string notes?;
|};

public type OrderItemTargetType typedesc<OrderItemOptionalized>;

public type OrderItemInsert OrderItem;

public type OrderItemUpdate record {|
    int quantity?;
    string notes?;
|};

