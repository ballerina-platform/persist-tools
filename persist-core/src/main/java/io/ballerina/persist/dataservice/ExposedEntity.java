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

import io.ballerina.persist.models.Entity;

import java.util.Set;

/**
 * An entity exposed by a data service, with the actions it allows.
 *
 * @param entity     the persist entity
 * @param operations the effective actions of the entity
 */
public record ExposedEntity(Entity entity, Set<DataServiceOptions.Operation> operations) {

    public boolean allows(DataServiceOptions.Operation operation) {
        return operations.contains(operation);
    }
}
