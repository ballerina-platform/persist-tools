/*
 * Copyright (c) 2026, WSO2 LLC. (http://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.ballerina.persist.dataservice;

import java.util.EnumSet;

/**
 * A field rule of `ballerina/dataservice`, which says what the API does with a field.
 */
public enum FieldRule {
    READ_ONLY("ReadOnly", EnumSet.of(Operation.CREATE, Operation.UPDATE)),
    IMMUTABLE("Immutable", EnumSet.of(Operation.UPDATE)),
    INTERNAL("Internal", EnumSet.of(Operation.READ, Operation.CREATE, Operation.UPDATE));

    private final String annotation;
    private final EnumSet<Operation> affected;

    FieldRule(String annotation, EnumSet<Operation> affected) {
        this.annotation = annotation;
        this.affected = affected;
    }

    /**
     * Returns the annotation's name.
     *
     * @return the name, such as `ReadOnly`
     */
    public String annotation() {
        return annotation;
    }

    /**
     * Returns the actions whose behaviour the rule changes.
     *
     * @return a copy of the actions
     */
    public EnumSet<Operation> affected() {
        return EnumSet.copyOf(affected);
    }
}
