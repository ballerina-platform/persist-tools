package io.ballerina.persist.models;

import java.util.Collections;
import java.util.List;

public class Index {
    private final String indexName;
    private final List<EntityField> fields;
    private boolean unique;

    public Index(String indexName, List<EntityField> fields, boolean unique) {
        this.indexName = indexName;
        this.fields = Collections.unmodifiableList(fields);
        this.unique = unique;
    }

    public String getIndexName() {
        return indexName;
    }

    public List<EntityField> getFields() {
        return fields;
    }

    public boolean isUnique() {
        return unique;
    }

}
