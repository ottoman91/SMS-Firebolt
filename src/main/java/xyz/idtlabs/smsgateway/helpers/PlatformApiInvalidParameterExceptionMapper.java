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
package xyz.idtlabs.smsgateway.helpers;

import xyz.idtlabs.smsgateway.exception.PlatformApiInvalidParameterException;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * The {@link PlatformApiInvalidParameterException} is typically thrown in data
 * validation of the parameters passed in with an api request.
 */
@Component
@Scope("singleton")
public class PlatformApiInvalidParameterExceptionMapper {

    public static ResponseEntity<ApiGlobalErrorResponse> toResponse(final PlatformApiInvalidParameterException exception) {
        final ApiGlobalErrorResponse dataValidationErrorResponse = ApiGlobalErrorResponse.sendMessageInvalidParameterError(
                exception.getGlobalisationMessageCode(), exception.getDefaultUserMessage(), exception.getErrors());
        return new ResponseEntity<>(dataValidationErrorResponse, HttpStatus.BAD_REQUEST) ;
    }   

  


    
}