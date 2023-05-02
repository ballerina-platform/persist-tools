package io.ballerina.persist.models;

public class ForeignKey {
    private String name;
    private String columnName;
    private String referenceTable;
    private String referenceColumn;

    public ForeignKey(String name, String columnName, String referenceTable, String referenceColumn) {
        this.name = name;
        this.columnName = columnName;
        this.referenceTable = referenceTable;
        this.referenceColumn = referenceColumn;
    }

    public String getName() {
        return name;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getReferenceTable() {
        return referenceTable;
    }

    public String getReferenceColumn() {
        return referenceColumn;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public void setReferenceTable(String referenceTable) {
        this.referenceTable = referenceTable;
    }

    public void setReferenceColumn(String referenceColumn) {
        this.referenceColumn = referenceColumn;
    }

}
