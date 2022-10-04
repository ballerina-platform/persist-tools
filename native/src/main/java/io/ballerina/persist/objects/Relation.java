package io.ballerina.persist.objects;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Class to store entity relations.
 *
 * @since 0.1.0
 */

public class Relation {

    public ArrayList<String> keyColumns = new ArrayList<>();
    public ArrayList<String> references = new ArrayList<>();

    public ArrayList<FieldMetaData> relatedFields = new ArrayList<>();
    public boolean isChild = false;
    public String relatedType;
    public String refTable;
    public String relatedInstance;
    public boolean parentIncluded = false;

//    public Entity relatedEntity;
    public Optional<String> relatedModule;

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


}
