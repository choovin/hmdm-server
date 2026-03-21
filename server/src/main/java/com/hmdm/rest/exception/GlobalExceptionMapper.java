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

import com.hmdm.rest.json.Response;
import com.hmdm.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * <p>Global exception mapper for JAX-RS resources. Catches all unhandled exceptions
 * and returns proper error responses with appropriate HTTP status codes.</p>
 *
 * <p>This mapper provides:</p>
 * <ul>
 *   <li>Proper HTTP status codes (4xx for client errors, 5xx for server errors)</li>
 *   <li>Comprehensive logging with full context (request path, parameters, stack trace)</li>
 *   <li>User-friendly error messages without exposing internal details</li>
 * </ul>
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionMapper.class);

    @Context
    private HttpServletRequest httpRequest;

    @Override
    public javax.ws.rs.core.Response toResponse(Exception exception) {
        // Determine HTTP status and error message based on exception type
        Status status = determineHttpStatus(exception);
        String errorCode = determineErrorCode(exception);

        // Log full context for debugging
        logException(exception, status);

        // Build response based on exception type
        if (exception instanceof SecurityException) {
            // Authentication/authorization failures - don't expose details
            return buildResponse(Status.UNAUTHORIZED, "error.unauthorized", null);
        } else if (exception instanceof IllegalArgumentException || exception instanceof IllegalStateException) {
            // Client-side errors (4xx)
            return buildResponse(Status.BAD_REQUEST, errorCode, null);
        } else if (exception instanceof javax.ws.rs.NotFoundException) {
            return buildResponse(Status.NOT_FOUND, errorCode, null);
        } else if (exception instanceof javax.ws.rs.BadRequestException) {
            return buildResponse(Status.BAD_REQUEST, errorCode, null);
        } else if (exception instanceof javax.ws.rs.ForbiddenException) {
            return buildResponse(Status.FORBIDDEN, "error.permission.denied", null);
        } else if (exception instanceof javax.ws.rs.NotAllowedException) {
            return buildResponse(Status.METHOD_NOT_ALLOWED, "error.method.not.allowed", null);
        } else {
            // Server-side errors (5xx) - log but don't expose internal details
            return buildResponse(Status.INTERNAL_SERVER_ERROR, "error.internal.server", null);
        }
    }

    /**
     * Determines the appropriate HTTP status code based on exception type.
     */
    private Status determineHttpStatus(Exception exception) {
        if (exception instanceof SecurityException) {
            return Status.UNAUTHORIZED;
        } else if (exception instanceof IllegalArgumentException) {
            return Status.BAD_REQUEST;
        } else if (exception instanceof IllegalStateException) {
            return Status.BAD_REQUEST;
        } else if (exception instanceof javax.ws.rs.NotFoundException) {
            return Status.NOT_FOUND;
        } else if (exception instanceof javax.ws.rs.BadRequestException) {
            return Status.BAD_REQUEST;
        } else if (exception instanceof javax.ws.rs.ForbiddenException) {
            return Status.FORBIDDEN;
        } else if (exception instanceof javax.ws.rs.NotAllowedException) {
            return Status.METHOD_NOT_ALLOWED;
        } else if (exception instanceof javax.ws.rs.NotAcceptableException) {
            return Status.NOT_ACCEPTABLE;
        } else if (exception instanceof javax.ws.rs.NotSupportedException) {
            return Status.UNSUPPORTED_MEDIA_TYPE;
        }
        return Status.INTERNAL_SERVER_ERROR;
    }

    /**
     * Determines the error code for the response message key.
     */
    private String determineErrorCode(Exception exception) {
        if (exception instanceof SecurityException) {
            return "error.unauthorized";
        } else if (exception instanceof IllegalArgumentException) {
            return "error.invalid.argument";
        } else if (exception instanceof IllegalStateException) {
            return "error.invalid.state";
        } else if (exception instanceof javax.ws.rs.NotFoundException) {
            return "error.notfound.resource";
        } else if (exception instanceof javax.ws.rs.BadRequestException) {
            return "error.bad.request";
        } else if (exception instanceof javax.ws.rs.ForbiddenException) {
            return "error.permission.denied";
        } else if (exception instanceof javax.ws.rs.NotAllowedException) {
            return "error.method.not.allowed";
        }
        return "error.internal.server";
    }

    /**
     * Builds the JAX-RS response with proper content type and error body.
     */
    private javax.ws.rs.core.Response buildResponse(Status status, String errorCode, String details) {
        Response errorResponse = Response.ERROR(errorCode, details);

        return javax.ws.rs.core.Response
                .status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(errorResponse)
                .build();
    }

    /**
     * Logs the exception with full context for debugging.
     */
    private void logException(Exception exception, Status status) {
        String requestPath = httpRequest != null ? httpRequest.getRequestURI() : "unknown";
        String queryString = httpRequest != null ? httpRequest.getQueryString() : null;
        String method = httpRequest != null ? httpRequest.getMethod() : "unknown";
        String remoteAddr = httpRequest != null ? httpRequest.getRemoteAddr() : "unknown";

        String fullPath = queryString != null ? requestPath + "?" + queryString : requestPath;

        // Get stack trace as string
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        String stackTrace = sw.toString();

        // Log based on status code category
        if (status.getFamily() == Status.Family.CLIENT_ERROR) {
            logger.warn("Client error on {} {} from {}: {} - {}",
                    method, fullPath, remoteAddr, exception.getClass().getSimpleName(), exception.getMessage());
        } else {
            // Server errors get full stack trace logging
            logger.error("Server error on {} {} from {}: {} - {}\nStack trace:\n{}",
                    method, fullPath, remoteAddr, exception.getClass().getSimpleName(),
                    exception.getMessage() != null ? exception.getMessage() : "null", stackTrace);
        }
    }
}
