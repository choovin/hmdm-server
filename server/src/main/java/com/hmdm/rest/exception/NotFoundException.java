/*
 *
 * Headwind MDM: Open Source Android MDM Software
 * https://h-mdm.com
 *
 * Copyright (C) 2019 Headwind Solutions LLC (http://h-sms.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.hmdm.rest.exception;

/**
 * <p>Exception thrown when a requested resource is not found.</p>
 * This maps to HTTP 404 Not Found.
 */
public class NotFoundException extends RuntimeException {

    private final String resourceType;
    private final Object resourceIdentifier;

    public NotFoundException(String resourceType, Object resourceIdentifier) {
        super(String.format("%s not found: %s", resourceType, resourceIdentifier));
        this.resourceType = resourceType;
        this.resourceIdentifier = resourceIdentifier;
    }

    public NotFoundException(String message) {
        super(message);
        this.resourceType = null;
        this.resourceIdentifier = null;
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.resourceType = null;
        this.resourceIdentifier = null;
    }

    public String getResourceType() {
        return resourceType;
    }

    public Object getResourceIdentifier() {
        return resourceIdentifier;
    }
}
