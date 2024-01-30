package io.ballerina.persist.models;

public class SQLType {
    private final String typeName;
    protected final String columnDefaultValue;
    private final String numericPrecision;
    private final String numericScale;
    private final String dateTimePrecision;
    private final int maxLength;

    public SQLType(String typeName, String columnDefaultValue, String numericPrecision, String numericScale,
                   String dateTimePrecision, int maxCharLength) {
        this.typeName = typeName;
        this.columnDefaultValue = columnDefaultValue;
        this.numericPrecision = numericPrecision;
        this.numericScale = numericScale;
        this.dateTimePrecision = dateTimePrecision;
        this.maxLength = maxCharLength;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getColumnDefaultValue() {
        return columnDefaultValue;
    }

    public String getNumericPrecision() {
        return numericPrecision;
    }

    public String getNumericScale() {
        return numericScale;
    }

    public String getDateTimePrecisionLevel() {
        return dateTimePrecision;
    }

    public int getMaxLength() {
        return maxLength;
    }
}
