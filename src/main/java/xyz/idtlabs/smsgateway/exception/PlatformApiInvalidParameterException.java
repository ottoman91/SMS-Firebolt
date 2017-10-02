/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
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
package xyz.idtlabs.smsgateway.exception;

import java.util.List;

import xyz.idtlabs.smsgateway.helpers.ApiParameterError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a HTTP 400 Error is thrown from an API request 
 */
@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="PlatformApiInvalidParameterException")
public class PlatformApiInvalidParameterException extends RuntimeException {

    private final String globalisationMessageCode;
    private final String defaultUserMessage;
    private final List<ApiParameterError> errors;

    public PlatformApiInvalidParameterException(final List<ApiParameterError> errors) {
        this.globalisationMessageCode = "validation.msg.validation.errors.exist";
        this.defaultUserMessage = "Validation errors exist.";
        this.errors = errors;
    }

    public PlatformApiInvalidParameterException(final String globalisationMessageCode, final String defaultUserMessage,
            final List<ApiParameterError> errors) {
        this.globalisationMessageCode = globalisationMessageCode;
        this.defaultUserMessage = defaultUserMessage;
        this.errors = errors;
    } 

   

    public String getGlobalisationMessageCode() {
        return this.globalisationMessageCode;
    }

    public String getDefaultUserMessage() {
        return this.defaultUserMessage;
    }

    public List<ApiParameterError> getErrors() {
        return this.errors;
    }
}