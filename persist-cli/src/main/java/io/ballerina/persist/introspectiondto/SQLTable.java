package io.ballerina.persist.introspectiondto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SQLTable {
    private String tableName;
    private String createOptions;
    private String tableComment;
    private List<SQLColumn> columns;


//    private List<SQLConstraint> constraints;
    private List<SQLForeignKey> foreignKeys;

    private List<SQLIndex> indexes;

    public SQLTable(String tableName, String createOptions, String tableComment) {
        this.tableName = tableName;
        this.createOptions = createOptions;
        this.tableComment = tableComment;
        this.columns = new ArrayList<>();
        this.foreignKeys = new ArrayList<>();
        this.indexes = new ArrayList<>();
    }

    public String getTableName() {
        return tableName;
    }


    public String getCreateOptions() {
        return createOptions;
    }


    public String getTableComment() {
        return tableComment;
    }


    public List<SQLColumn> getColumns() {
        return Collections.unmodifiableList(columns);
    }




//    public List<Constraint> getConstraints() {
//        return constraints;
//    }
//
//    public void setConstraints(List<Constraint> constraints) {
//        this.constraints = constraints;
//    }

    public List<SQLForeignKey> getForeignKeys() {
        return Collections.unmodifiableList(foreignKeys);
    }

    public List<SQLIndex> getIndexes() {
        return Collections.unmodifiableList(indexes);
    }

    public void addColumn(SQLColumn column) {
        this.columns.add(column);
    }



    public void addForeignKey(SQLForeignKey foreignKey) {
        this.foreignKeys.add(foreignKey);
    }

    public void addIndex(SQLIndex index) {
        this.indexes.add(index);
    }

    public static Builder newBuilder(String tableName) {
        return new Builder(tableName);
    }

    public static class Builder {
        private String tableName;
        private String createOptions;
        private String tableComment;

        private Builder(String tableName) {
            this.tableName = tableName;
        }

        public Builder setCreateOptions(String createOptions) {
            this.createOptions = createOptions;
            return this;
        }

        public Builder setTableComment(String tableComment) {
            this.tableComment = tableComment;
            return this;
        }


        public SQLTable build() {
            return new SQLTable(tableName, createOptions,
                    tableComment);
        }
    }



}
