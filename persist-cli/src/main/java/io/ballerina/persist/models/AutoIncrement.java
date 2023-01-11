/*
 * Copyright (c) 2022, WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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
