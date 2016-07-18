/*
 * Copyright 2016 Netflix, Inc.
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *        http://www.apache.org/licenses/LICENSE-2.0
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.netflix.metacat.common.exception;

import static com.netflix.metacat.common.exception.StandardErrorCode.*;

/**
 * Created by amajumdar on 4/30/15.
 */
public class SchemaAlreadyExistsException extends MetacatServiceException {
    private final String schemaName;
    public SchemaAlreadyExistsException(String schemaName) {
        this(schemaName, null);
    }

    public SchemaAlreadyExistsException(String schemaName, Throwable cause) {
        super(ALREADY_EXISTS, String.format("Schema %s already exists.", schemaName), cause);
        this.schemaName = schemaName;
    }
}
