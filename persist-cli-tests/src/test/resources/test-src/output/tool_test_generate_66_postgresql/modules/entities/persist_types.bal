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

# Represents the `AllTypes` entity.
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

# Represents `AllTypes` with every field optional, for projected reads.
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

# The `AllTypes` type or a projection of it, for reads.
public type AllTypesTargetType typedesc<AllTypesOptionalized>;

# The fields to insert a new `AllTypes`.
public type AllTypesInsert AllTypes;

# The fields to update on an existing `AllTypes`.
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

# Represents the `StringIdRecord` entity.
public type StringIdRecord record {|
    readonly string id;
    string randomField;
|};

# Represents `StringIdRecord` with every field optional, for projected reads.
public type StringIdRecordOptionalized record {|
    string id?;
    string randomField?;
|};

# The `StringIdRecord` type or a projection of it, for reads.
public type StringIdRecordTargetType typedesc<StringIdRecordOptionalized>;

# The fields to insert a new `StringIdRecord`.
public type StringIdRecordInsert StringIdRecord;

# The fields to update on an existing `StringIdRecord`.
public type StringIdRecordUpdate record {|
    string randomField?;
|};

# Represents the `IntIdRecord` entity.
public type IntIdRecord record {|
    readonly int id;
    string randomField;
|};

# Represents `IntIdRecord` with every field optional, for projected reads.
public type IntIdRecordOptionalized record {|
    int id?;
    string randomField?;
|};

# The `IntIdRecord` type or a projection of it, for reads.
public type IntIdRecordTargetType typedesc<IntIdRecordOptionalized>;

# The fields to insert a new `IntIdRecord`.
public type IntIdRecordInsert IntIdRecord;

# The fields to update on an existing `IntIdRecord`.
public type IntIdRecordUpdate record {|
    string randomField?;
|};

# Represents the `FloatIdRecord` entity.
public type FloatIdRecord record {|
    readonly float id;
    string randomField;
|};

# Represents `FloatIdRecord` with every field optional, for projected reads.
public type FloatIdRecordOptionalized record {|
    float id?;
    string randomField?;
|};

# The `FloatIdRecord` type or a projection of it, for reads.
public type FloatIdRecordTargetType typedesc<FloatIdRecordOptionalized>;

# The fields to insert a new `FloatIdRecord`.
public type FloatIdRecordInsert FloatIdRecord;

# The fields to update on an existing `FloatIdRecord`.
public type FloatIdRecordUpdate record {|
    string randomField?;
|};

# Represents the `DecimalIdRecord` entity.
public type DecimalIdRecord record {|
    readonly decimal id;
    string randomField;
|};

# Represents `DecimalIdRecord` with every field optional, for projected reads.
public type DecimalIdRecordOptionalized record {|
    decimal id?;
    string randomField?;
|};

# The `DecimalIdRecord` type or a projection of it, for reads.
public type DecimalIdRecordTargetType typedesc<DecimalIdRecordOptionalized>;

# The fields to insert a new `DecimalIdRecord`.
public type DecimalIdRecordInsert DecimalIdRecord;

# The fields to update on an existing `DecimalIdRecord`.
public type DecimalIdRecordUpdate record {|
    string randomField?;
|};

# Represents the `BooleanIdRecord` entity.
public type BooleanIdRecord record {|
    readonly boolean id;
    string randomField;
|};

# Represents `BooleanIdRecord` with every field optional, for projected reads.
public type BooleanIdRecordOptionalized record {|
    boolean id?;
    string randomField?;
|};

# The `BooleanIdRecord` type or a projection of it, for reads.
public type BooleanIdRecordTargetType typedesc<BooleanIdRecordOptionalized>;

# The fields to insert a new `BooleanIdRecord`.
public type BooleanIdRecordInsert BooleanIdRecord;

# The fields to update on an existing `BooleanIdRecord`.
public type BooleanIdRecordUpdate record {|
    string randomField?;
|};

# Represents the `CompositeAssociationRecord` entity.
public type CompositeAssociationRecord record {|
    readonly string id;
    string randomField;
    boolean alltypesidrecordBooleanType;
    int alltypesidrecordIntType;
    float alltypesidrecordFloatType;
    decimal alltypesidrecordDecimalType;
    string alltypesidrecordStringType;
|};

# Represents `CompositeAssociationRecord` with every field optional, for projected reads.
public type CompositeAssociationRecordOptionalized record {|
    string id?;
    string randomField?;
    boolean alltypesidrecordBooleanType?;
    int alltypesidrecordIntType?;
    float alltypesidrecordFloatType?;
    decimal alltypesidrecordDecimalType?;
    string alltypesidrecordStringType?;
|};

# Represents `CompositeAssociationRecord` with its related entities, for projected reads.
public type CompositeAssociationRecordWithRelations record {|
    *CompositeAssociationRecordOptionalized;
    AllTypesIdRecordOptionalized allTypesIdRecord?;
|};

# The `CompositeAssociationRecord` type or a projection of it, for reads.
public type CompositeAssociationRecordTargetType typedesc<CompositeAssociationRecordWithRelations>;

# The fields to insert a new `CompositeAssociationRecord`.
public type CompositeAssociationRecordInsert CompositeAssociationRecord;

# The fields to update on an existing `CompositeAssociationRecord`.
public type CompositeAssociationRecordUpdate record {|
    string randomField?;
    boolean alltypesidrecordBooleanType?;
    int alltypesidrecordIntType?;
    float alltypesidrecordFloatType?;
    decimal alltypesidrecordDecimalType?;
    string alltypesidrecordStringType?;
|};

# Represents the `AllTypesIdRecord` entity.
public type AllTypesIdRecord record {|
    readonly boolean booleanType;
    readonly int intType;
    readonly float floatType;
    readonly decimal decimalType;
    readonly string stringType;
    string randomField;

|};

# Represents `AllTypesIdRecord` with every field optional, for projected reads.
public type AllTypesIdRecordOptionalized record {|
    boolean booleanType?;
    int intType?;
    float floatType?;
    decimal decimalType?;
    string stringType?;
    string randomField?;
|};

# Represents `AllTypesIdRecord` with its related entities, for projected reads.
public type AllTypesIdRecordWithRelations record {|
    *AllTypesIdRecordOptionalized;
    CompositeAssociationRecordOptionalized compositeAssociationRecord?;
|};

# The `AllTypesIdRecord` type or a projection of it, for reads.
public type AllTypesIdRecordTargetType typedesc<AllTypesIdRecordWithRelations>;

# The fields to insert a new `AllTypesIdRecord`.
public type AllTypesIdRecordInsert AllTypesIdRecord;

# The fields to update on an existing `AllTypesIdRecord`.
public type AllTypesIdRecordUpdate record {|
    string randomField?;
|};

# Represents the `User` entity.
public type User record {|
    readonly int id;
    string name;
    time:Date birthDate;
    string mobileNumber;

|};

# Represents `User` with every field optional, for projected reads.
public type UserOptionalized record {|
    int id?;
    string name?;
    time:Date birthDate?;
    string mobileNumber?;
|};

# Represents `User` with its related entities, for projected reads.
public type UserWithRelations record {|
    *UserOptionalized;
    PostOptionalized[] posts?;
    CommentOptionalized[] comments?;
    FollowOptionalized[] followers?;
    FollowOptionalized[] following?;
|};

# The `User` type or a projection of it, for reads.
public type UserTargetType typedesc<UserWithRelations>;

# The fields to insert a new `User`.
public type UserInsert User;

# The fields to update on an existing `User`.
public type UserUpdate record {|
    string name?;
    time:Date birthDate?;
    string mobileNumber?;
|};

# Represents the `Post` entity.
public type Post record {|
    readonly int id;
    string description;
    string tags;
    Category category;
    time:Civil timestamp;
    int userId;

|};

# Represents `Post` with every field optional, for projected reads.
public type PostOptionalized record {|
    int id?;
    string description?;
    string tags?;
    Category category?;
    time:Civil timestamp?;
    int userId?;
|};

# Represents `Post` with its related entities, for projected reads.
public type PostWithRelations record {|
    *PostOptionalized;
    UserOptionalized user?;
    CommentOptionalized[] comments?;
|};

# The `Post` type or a projection of it, for reads.
public type PostTargetType typedesc<PostWithRelations>;

# The fields to insert a new `Post`.
public type PostInsert Post;

# The fields to update on an existing `Post`.
public type PostUpdate record {|
    string description?;
    string tags?;
    Category category?;
    time:Civil timestamp?;
    int userId?;
|};

# Represents the `Follow` entity.
public type Follow record {|
    readonly int id;
    int leaderId;
    int followerId;
    time:Civil timestamp;
|};

# Represents `Follow` with every field optional, for projected reads.
public type FollowOptionalized record {|
    int id?;
    int leaderId?;
    int followerId?;
    time:Civil timestamp?;
|};

# Represents `Follow` with its related entities, for projected reads.
public type FollowWithRelations record {|
    *FollowOptionalized;
    UserOptionalized leader?;
    UserOptionalized follower?;
|};

# The `Follow` type or a projection of it, for reads.
public type FollowTargetType typedesc<FollowWithRelations>;

# The fields to insert a new `Follow`.
public type FollowInsert Follow;

# The fields to update on an existing `Follow`.
public type FollowUpdate record {|
    int leaderId?;
    int followerId?;
    time:Civil timestamp?;
|};

# Represents the `Comment` entity.
public type Comment record {|
    readonly int id;
    string comment;
    time:Civil timesteamp;
    int userId;
    int postId;
|};

# Represents `Comment` with every field optional, for projected reads.
public type CommentOptionalized record {|
    int id?;
    string comment?;
    time:Civil timesteamp?;
    int userId?;
    int postId?;
|};

# Represents `Comment` with its related entities, for projected reads.
public type CommentWithRelations record {|
    *CommentOptionalized;
    UserOptionalized user?;
    PostOptionalized post?;
|};

# The `Comment` type or a projection of it, for reads.
public type CommentTargetType typedesc<CommentWithRelations>;

# The fields to insert a new `Comment`.
public type CommentInsert Comment;

# The fields to update on an existing `Comment`.
public type CommentUpdate record {|
    string comment?;
    time:Civil timesteamp?;
    int userId?;
    int postId?;
|};

# Represents the `Employee` entity.
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

# Represents `Employee` with every field optional, for projected reads.
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

# Represents `Employee` with its related entities, for projected reads.
public type EmployeeWithRelations record {|
    *EmployeeOptionalized;
    DepartmentOptionalized department?;
    WorkspaceOptionalized workspace?;
|};

# The `Employee` type or a projection of it, for reads.
public type EmployeeTargetType typedesc<EmployeeWithRelations>;

# The fields to insert a new `Employee`.
public type EmployeeInsert Employee;

# The fields to update on an existing `Employee`.
public type EmployeeUpdate record {|
    string firstName?;
    string lastName?;
    time:Date birthDate?;
    Gender gender?;
    time:Date hireDate?;
    string departmentDeptNo?;
    string workspaceWorkspaceId?;
|};

# Represents the `Workspace` entity.
public type Workspace record {|
    readonly string workspaceId;
    string workspaceType;
    string locationBuildingCode;

|};

# Represents `Workspace` with every field optional, for projected reads.
public type WorkspaceOptionalized record {|
    string workspaceId?;
    string workspaceType?;
    string locationBuildingCode?;
|};

# Represents `Workspace` with its related entities, for projected reads.
public type WorkspaceWithRelations record {|
    *WorkspaceOptionalized;
    BuildingOptionalized location?;
    EmployeeOptionalized[] employees?;
|};

# The `Workspace` type or a projection of it, for reads.
public type WorkspaceTargetType typedesc<WorkspaceWithRelations>;

# The fields to insert a new `Workspace`.
public type WorkspaceInsert Workspace;

# The fields to update on an existing `Workspace`.
public type WorkspaceUpdate record {|
    string workspaceType?;
    string locationBuildingCode?;
|};

# Represents the `Building` entity.
public type Building record {|
    readonly string buildingCode;
    string city;
    string state;
    string country;
    string postalCode;
    string 'type;
|};

# Represents `Building` with every field optional, for projected reads.
public type BuildingOptionalized record {|
    string buildingCode?;
    string city?;
    string state?;
    string country?;
    string postalCode?;
    string 'type?;
|};

# Represents `Building` with its related entities, for projected reads.
public type BuildingWithRelations record {|
    *BuildingOptionalized;
    WorkspaceOptionalized[] workspaces?;
|};

# The `Building` type or a projection of it, for reads.
public type BuildingTargetType typedesc<BuildingWithRelations>;

# The fields to insert a new `Building`.
public type BuildingInsert Building;

# The fields to update on an existing `Building`.
public type BuildingUpdate record {|
    string city?;
    string state?;
    string country?;
    string postalCode?;
    string 'type?;
|};

# Represents the `Department` entity.
public type Department record {|
    readonly string deptNo;
    string deptName;

|};

# Represents `Department` with every field optional, for projected reads.
public type DepartmentOptionalized record {|
    string deptNo?;
    string deptName?;
|};

# Represents `Department` with its related entities, for projected reads.
public type DepartmentWithRelations record {|
    *DepartmentOptionalized;
    EmployeeOptionalized[] employees?;
|};

# The `Department` type or a projection of it, for reads.
public type DepartmentTargetType typedesc<DepartmentWithRelations>;

# The fields to insert a new `Department`.
public type DepartmentInsert Department;

# The fields to update on an existing `Department`.
public type DepartmentUpdate record {|
    string deptName?;
|};

# Represents the `OrderItem` entity.
public type OrderItem record {|
    readonly string orderId;
    readonly string itemId;
    int quantity;
    string notes;
|};

# Represents `OrderItem` with every field optional, for projected reads.
public type OrderItemOptionalized record {|
    string orderId?;
    string itemId?;
    int quantity?;
    string notes?;
|};

# The `OrderItem` type or a projection of it, for reads.
public type OrderItemTargetType typedesc<OrderItemOptionalized>;

# The fields to insert a new `OrderItem`.
public type OrderItemInsert OrderItem;

# The fields to update on an existing `OrderItem`.
public type OrderItemUpdate record {|
    int quantity?;
    string notes?;
|};

