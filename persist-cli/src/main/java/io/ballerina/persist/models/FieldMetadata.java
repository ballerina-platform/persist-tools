package io.ballerina.persist.models;

public class FieldMetadata {
    private String name;
    private String dataType;

    public FieldMetadata(String name, String dataType) {
        this.name = name;
        this.dataType = dataType;
    }

    public FieldMetadata(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getDataType() {
        return dataType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}
