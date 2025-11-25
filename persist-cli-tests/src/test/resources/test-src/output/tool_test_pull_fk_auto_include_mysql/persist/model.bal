import ballerina/persist as _;
import ballerina/time;
import ballerinax/persist.sql;

@sql:Name {value: "album_ratings"}
public type AlbumRating record {|
    @sql:Name {value: "album_id"}
    readonly int albumId;
    @sql:Name {value: "customer_name"}
    @sql:Varchar {length: 100}
    readonly string customerName;
    int? rating;
    @sql:Varchar {length: 255}
    string? review;
    @sql:Name {value: "rated_on"}
    time:Utc? ratedOn;
    @sql:Relation {keys: ["albumId"]}
    Album album;
|};

@sql:Name {value: "albums"}
public type Album record {|
    @sql:Name {value: "album_id"}
    @sql:Generated
    readonly int albumId;
    @sql:Varchar {length: 100}
    string title;
    @sql:Varchar {length: 100}
    string artist;
    @sql:Decimal {precision: [6, 2]}
    decimal price;
    int stock;
    AlbumRating[] albumratings;
|};

