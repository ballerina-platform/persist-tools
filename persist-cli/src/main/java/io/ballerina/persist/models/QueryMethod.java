package io.ballerina.persist.models;

/**
 * Query method class.
 *
 * @since 1.0.0
 *
 */
public class QueryMethod {

    private final String methodName;
    private final String associatedEntityName;
    private final String methodBody;

    public QueryMethod(String methodName, String associatedEntityName, String methodBody) {
        this.methodName = methodName;
        this.associatedEntityName = associatedEntityName;
        this.methodBody = methodBody;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public String getMethodBody() {
        return this.methodBody;
    }

    public String getAssociatedEntityName() {
        return this.associatedEntityName;
    }
}
