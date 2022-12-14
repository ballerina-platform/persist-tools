package io.ballerina.persist.models;

/**
 * Model class for auto increment parameters.
 * @since 0.1.0
 */
public class AutoIncrement {
    int startValue;
    int interval;

    public int getStartValue() {
        return startValue;
    }

    public int getInterval() {
        return interval;
    }

    public static AutoIncrement.Builder newBuilder() {
        return new AutoIncrement.Builder();
    }

    private AutoIncrement(int startValue, int interval) {
        this.startValue = startValue;
        this.interval = interval;
    }

    /**
     * Entity Field AutoIncrement Definition.Builder.
     */
    public static class Builder {
        // The default value are set to 1 as default.
        // If annotation field default values are changed, change here are well.
        int startValue = 1;
        int interval = 1;

        public void setStartValue(int startValue) {
            this.startValue = startValue;
        }

        public void setInterval(int interval) {
            this.interval = interval;
        }

        public AutoIncrement build() {
            return new AutoIncrement(startValue, interval);
        }
    }
}
