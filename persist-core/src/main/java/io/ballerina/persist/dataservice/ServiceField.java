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

import java.util.Optional;

/**
 * A field of a persist-generated type, as the data service sees it.
 *
 * @param name       the field name, as written in the model
 * @param type       the field's Ballerina type without `?`, such as `time:Date` or `byte[]`
 * @param nilable    whether the type is nilable, `T?`
 * @param optional   whether the field is optional, `T name?`
 * @param key        whether the field is a key field, which is `readonly`
 */
public record ServiceField(String name, String type, boolean nilable, boolean optional, boolean key) {

    /**
     * A type whose JSON form is a string, converted by `ballerina/dataservice`. Each holds its Ballerina type and the
     * name of the type in the conversion functions, such as `date` in `dateToString`.
     */
    public enum Conversion {
        BYTES("byte[]", "bytes"),
        DATE("time:Date", "date"),
        TIME_OF_DAY("time:TimeOfDay", "timeOfDay"),
        CIVIL("time:Civil", "civil"),
        UTC("time:Utc", "utc");

        private final String balType;
        private final String name;

        Conversion(String balType, String name) {
            this.balType = balType;
            this.name = name;
        }

        public String balType() {
            return balType;
        }

        public String toStringFunction() {
            return "dataservice:" + name + "ToString";
        }

        public String fromStringFunction() {
            return "dataservice:" + name + "FromString";
        }
    }

    /**
     * Returns the conversion of the field's type, if its JSON form is a string.
     *
     * @return the conversion, or empty when the persist type is used as it is
     */
    public Optional<Conversion> conversion() {
        for (Conversion conversion : Conversion.values()) {
            if (conversion.balType.equals(type)) {
                return Optional.of(conversion);
            }
        }
        return Optional.empty();
    }

    /**
     * Returns the type of the field in the wire type.
     *
     * @return `string` for a converted type, the persist type otherwise, with `?` when nilable
     */
    public String wireType() {
        return (conversion().isPresent() ? "string" : type) + (nilable ? "?" : "");
    }

    /**
     * Returns the type of the field in the persist type.
     *
     * @return the type, with `?` when nilable
     */
    public String persistType() {
        return type + (nilable ? "?" : "");
    }
}
