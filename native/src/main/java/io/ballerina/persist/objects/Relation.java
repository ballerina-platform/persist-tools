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

    public Relation(String relatedType, String relatedInstance, ArrayList<String> keyColumns,
                    ArrayList<String> references, boolean isChild) {
        this.keyColumns = keyColumns;
        this.references = references;
        this.relatedType = relatedType;
        this.isChild = isChild;
        this.relatedInstance = relatedInstance;
    }
}
