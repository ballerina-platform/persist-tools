/*
 * Copyright (c) 2023, WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class to store persist enums.
 *
 * @since 0.3.2
 */
public class Enum {

    private final String enumName;
    private final List<EnumMember> members;

    private Enum(String enumName, List<EnumMember> members) {
        this.enumName = enumName;
        this.members = Collections.unmodifiableList(members);
    }

    public String getEnumName() {
        return this.enumName;
    }

    public List<EnumMember> getMembers() {
        return this.members;
    }

    public static Enum.Builder newBuilder(String entityName) {
        return new Enum.Builder(entityName);
    }

    /**
     * Enum definition builder.
     */
    public static class Builder {
        private String enumName;
        private List<EnumMember> members;

        private Builder(String enumName) {
            this.enumName = enumName;
        }

        public void addMember(EnumMember member) {
            if (members == null) {
                members = new ArrayList<>();
            }
            members.add(member);
        }

        public Enum build() {
            return new Enum(enumName, members);
        }
    }
}
