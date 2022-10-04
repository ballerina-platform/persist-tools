package io.ballerina.persist.objects;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Class to store entity relations.
 *
 * @since 0.1.0
 */

public class Relation {

    private ArrayList<String> keyColumns = new ArrayList<>();
    private ArrayList<String> references = new ArrayList<>();

    private ArrayList<FieldMetaData> relatedFields = new ArrayList<>();
    private boolean isChild = false;
    private String relatedType;
    private String refTable;
    private String relatedInstance;
    private boolean parentIncluded = false;
    private Optional<String> relatedModule;

    public Relation(String relatedType, String relatedInstance, ArrayList<String> keyColumns,
                    ArrayList<String> references, boolean isChild) {
        this.keyColumns = keyColumns;
        this.references = references;
        this.relatedType = relatedType;
        this.isChild = isChild;
        this.relatedInstance = relatedInstance;
    }

    public String getClientName() {
        String className = relatedType;
        if (relatedModule.isPresent()) {
            className = relatedModule.get().substring(0, 1).toUpperCase() +
                    relatedModule.get().substring(1)
                    + relatedType;
        }
        return className;
    }

    public String getRefTable() {
        return this.refTable;
    }

    public void setRefTable(String refTable) {
        this.refTable = refTable;
    }

    public String getRelatedType() {
        return this.relatedType;
    }

    public String getRelatedInstance() {
        return relatedInstance;
    }

    public void setRelatedInstance(String relatedInstance) {
        this.relatedInstance = relatedInstance;
    }

    public ArrayList<String> getKeyColumns() {
        return keyColumns;
    }

    public ArrayList<String> getReferences() {
        return references;
    }

    public ArrayList<FieldMetaData> getRelatedFields() {
        return relatedFields;
    }

    public void setRelatedFields(ArrayList<FieldMetaData> relatedFields) {
        this.relatedFields = relatedFields;
    }

    public boolean isChild() {
        return isChild;
    }

    public void setRelatedModule(Optional<String> relatedModule) {
        this.relatedModule = relatedModule;
    }

    public boolean isParentIncluded() {
        return this.parentIncluded;
    }
    public void setParentIncluded(boolean parentIncluded) {
        this.parentIncluded = parentIncluded;
    }


}
