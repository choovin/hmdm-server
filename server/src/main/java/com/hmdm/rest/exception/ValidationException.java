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

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Exception thrown when input validation fails.</p>
 * This maps to HTTP 400 Bad Request.
 */
public class ValidationException extends RuntimeException {

    private final Map<String, String> fieldErrors = new HashMap<>();

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidationException addFieldError(String field, String message) {
        this.fieldErrors.put(field, message);
        return this;
    }

    public Map<String, String> getFieldErrors() {
        return new HashMap<>(fieldErrors);
    }

    public boolean hasFieldErrors() {
        return !fieldErrors.isEmpty();
    }
}
