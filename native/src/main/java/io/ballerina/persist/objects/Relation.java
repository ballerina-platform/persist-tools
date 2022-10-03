package io.ballerina.persist.objects;

import java.util.ArrayList;

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

    public Entity relatedEntity;
    public boolean parentIncluded = false;

    public Relation(String relatedType, String relatedInstance, ArrayList<String> keyColumns,
                    ArrayList<String> references, boolean isChild) {
        this.keyColumns = keyColumns;
        this.references = references;
        this.relatedType = relatedType;
        this.isChild = isChild;
        this.relatedInstance = relatedInstance;
    }

    public String getClientName() {
        String className = relatedEntity.getEntityName();
        if (relatedEntity.getModule().isPresent()) {
            className = relatedEntity.getModule().get().substring(0, 1).toUpperCase() +
                    relatedEntity.getModule().get().substring(1)
                    + relatedEntity.getEntityName();
        }
        return className;
    }
}
