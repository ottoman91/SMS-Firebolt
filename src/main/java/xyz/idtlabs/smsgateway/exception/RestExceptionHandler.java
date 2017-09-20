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

import xyz.idtlabs.smsgateway.tenants.exception.TenantExists; 
import xyz.idtlabs.smsgateway.tenants.exception.InvalidApiKeyException;
import xyz.idtlabs.smsgateway.tenants.exception.ClientBlockedException;
import xyz.idtlabs.smsgateway.sms.exception.DuplicateDestinationAddressException;
import xyz.idtlabs.smsgateway.sms.exception.MessageBodyIsEmptyException;
import xyz.idtlabs.smsgateway.sms.exception.MessageBodyOverLimit;
import xyz.idtlabs.smsgateway.sms.exception.DestinationIsEmptyException;
import xyz.idtlabs.smsgateway.sms.exception.DestinationNumberFormatError;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
//import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler; 



// import javax.servlet.http.HttpServletRequest;

// import static org.springframework.http.HttpStatus.BAD_REQUEST;
// import static org.springframework.http.HttpStatus.NOT_FOUND;

// @Order(Ordered.HIGHEST_PRECEDENCE)
// @ControllerAdvice
// @Slf4j
// public class RestExceptionHandler extends ResponseEntityExceptionHandler {

//     /**
//      * Handle MissingServletRequestParameterException. Triggered when a 'required' request parameter is missing.
//      *
//      * @param ex      MissingServletRequestParameterException
//      * @param headers HttpHeaders
//      * @param status  HttpStatus
//      * @param request WebRequest
//      * @return the ApiError object
//      */
//     @Override
//     protected ResponseEntity<Object> handleMissingServletRequestParameter(
//             MissingServletRequestParameterException ex, HttpHeaders headers,
//             HttpStatus status, WebRequest request) { 
//         ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
//         String error = ex.getParameterName() + " parameter is missing";
//         apiError.setMessage(error); 
//         apiError.setDebugMessage(ex.getLocalizedMessage());
//         return buildResponseEntity(apiError);
//     }   

    


        
//     // @ExceptionHandler({MissingServletRequestParameterException.class})
//     // protected ResponseEntity<Object> handleMissingServletRequestParameter(
//     //         MissingServletRequestParameterException ex, HttpHeaders headers,
//     //         HttpStatus status, WebRequest request) {  

//     //     ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
//     //     apiError.setMessage("Missing request parameter" + ex.getParameterName());
//     //     return buildResponseEntity(apiError);
//     // }  

   


//     /**
//      * Handle HttpMediaTypeNotSupportedException. This one triggers when JSON is invalid as well.
//      *
//      * @param ex      HttpMediaTypeNotSupportedException
//      * @param headers HttpHeaders
//      * @param status  HttpStatus
//      * @param request WebRequest
//      * @return the ApiError object
//      */
//     @Override
//     protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
//             HttpMediaTypeNotSupportedException ex,
//             HttpHeaders headers,
//             HttpStatus status,
//             WebRequest request) {
//         StringBuilder builder = new StringBuilder();
//         builder.append(ex.getContentType());
//         builder.append(" media type is not supported. Supported media types are ");
//         ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(", "));
//         return buildResponseEntity(new ApiError(HttpStatus.UNSUPPORTED_MEDIA_TYPE, builder.substring(0, builder.length() - 2), ex));
//     }

//     /**
//      * Handle MethodArgumentNotValidException. Triggered when an object fails @Valid validation.
//      *
//      * @param ex      the MethodArgumentNotValidException that is thrown when @Valid validation fails
//      * @param headers HttpHeaders
//      * @param status  HttpStatus
//      * @param request WebRequest
//      * @return the ApiError object
//      */
//     @Override
//     protected ResponseEntity<Object> handleMethodArgumentNotValid(
//             MethodArgumentNotValidException ex,
//             HttpHeaders headers,
//             HttpStatus status,
//             WebRequest request) {
//         ApiError apiError = new ApiError(BAD_REQUEST);
//         apiError.setMessage("Validation error");
//         apiError.addValidationErrors(ex.getBindingResult().getFieldErrors());
//         apiError.addValidationError(ex.getBindingResult().getGlobalErrors());
//         return buildResponseEntity(apiError);
//     }

//     /**
//      * Handles javax.validation.ConstraintViolationException. Thrown when @Validated fails.
//      *
//      * @param ex the ConstraintViolationException
//      * @return the ApiError object
//      */
//     @ExceptionHandler(javax.validation.ConstraintViolationException.class)
//     protected ResponseEntity<Object> handleConstraintViolation(
//             javax.validation.ConstraintViolationException ex) {
//         ApiError apiError = new ApiError(BAD_REQUEST);
//         apiError.setMessage("Validation error");
//         apiError.addValidationErrors(ex.getConstraintViolations());
//         return buildResponseEntity(apiError);
//     }

//     /**
//      * Handles EntityNotFoundException. Created to encapsulate errors with more detail than javax.persistence.EntityNotFoundException.
//      *
//      * @param ex the EntityNotFoundException
//      * @return the ApiError object
//      */
//     // @ExceptionHandler(EntityNotFoundException.class)
//     // protected ResponseEntity<Object> handleEntityNotFound(
//     //         EntityNotFoundException ex) {
//     //     ApiError apiError = new ApiError(NOT_FOUND);
//     //     apiError.setMessage(ex.getMessage());
//     //     return buildResponseEntity(apiError);
//     // }

//     /**
//      * Handle HttpMessageNotReadableException. Happens when request JSON is malformed.
//      *
//      * @param ex      HttpMessageNotReadableException
//      * @param headers HttpHeaders
//      * @param status  HttpStatus
//      * @param request WebRequest
//      * @return the ApiError object
//      */
//     @Override
//     protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
//         ServletWebRequest servletWebRequest = (ServletWebRequest) request;
//         log.info("{} to {}", servletWebRequest.getHttpMethod(), servletWebRequest.getRequest().getServletPath());
//         String error = "Malformed JSON request";
//         return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, error, ex));
//     }

//     /**
//      * Handle HttpMessageNotWritableException.
//      *
//      * @param ex      HttpMessageNotWritableException
//      * @param headers HttpHeaders
//      * @param status  HttpStatus
//      * @param request WebRequest
//      * @return the ApiError object
//      */
//     @Override
//     protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
//         String error = "Error writing JSON output";
//         return buildResponseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, error, ex));
//     }

//     /**
//      * Handle javax.persistence.EntityNotFoundException
//      */
//     @ExceptionHandler(javax.persistence.EntityNotFoundException.class)
//     protected ResponseEntity<Object> handleEntityNotFound(javax.persistence.EntityNotFoundException ex) {
//         return buildResponseEntity(new ApiError(HttpStatus.NOT_FOUND, ex));
//     }

//     /**
//      * Handle DataIntegrityViolationException, inspects the cause for different DB causes.
//      *
//      * @param ex the DataIntegrityViolationException
//      * @return the ApiError object
//      */
//     @ExceptionHandler(DataIntegrityViolationException.class)
//     protected ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex,
//                                                                   WebRequest request) {
//         if (ex.getCause() instanceof ConstraintViolationException) {
//             return buildResponseEntity(new ApiError(HttpStatus.CONFLICT, "Database error", ex.getCause()));
//         }
//         return buildResponseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex));
//     }

//     /**
//      * Handle Exception, handle generic Exception.class
//      *
//      * @param ex the Exception
//      * @return the ApiError object
//      */
//     // @ExceptionHandler(MethodArgumentTypeMismatchException.class)
//     // protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex,
//     //                                                                   WebRequest request) {
//     //     ApiError apiError = new ApiError(BAD_REQUEST);
//     //     apiError.setMessage(String.format("The parameter '%s' of value '%s' could not be converted to type '%s'", ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName()));
//     //     apiError.setDebugMessage(ex.getMessage());
//     //     return buildResponseEntity(apiError);
//     // }  

   
//     /**
//      * Handle Exception When ReCreating already existing Client
//      */

//    @ExceptionHandler(TenantExists.class)
//    protected ResponseEntity<Object> tenantExists(
//            TenantExists ex) {
//        ApiError apiError = new ApiError(HttpStatus.CONFLICT);
//        apiError.setMessage("The client already exists");
//        return buildResponseEntity(apiError);
//    } 

//    /**
//      * Handle Exception When ApiKey is invalid
//      */
//    @ExceptionHandler(InvalidApiKeyException.class)
//    protected ResponseEntity<Object> invalidApiKeyException(
//            InvalidApiKeyException ex) {
//        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
//        apiError.setMessage("Invalid or missing API Key");
//        apiError.setDebugMessage("The API key is either incorrect or has not been included in the API call.");
//        apiError.setErrorCode("invalid_key");
//        return buildResponseEntity(apiError);
//    }  

//      /**
//      * Handle Exception When Client is Blocked and Cannot Send a Message 
//      */
//    @ExceptionHandler(ClientBlockedException.class)
//    protected ResponseEntity<Object> clientBlockedException(
//            ClientBlockedException ex) {
//        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
//        apiError.setMessage("Account is not active.");
//        apiError.setDebugMessage("The account is not active.");
//        apiError.setErrorCode("account_inactive");
//        return buildResponseEntity(apiError);
//    }  

//       /**
//      * Handle Exception When Destination Number is Repeated 
//      */
//    @ExceptionHandler(DuplicateDestinationAddressException.class)
//    protected ResponseEntity<Object> duplicateDestinationAddressException(
//            DuplicateDestinationAddressException ex) {
//        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
//        apiError.setMessage("Duplicated destination address found.");
//        apiError.setDebugMessage("The destination number you are attempting to send to is duplicated.");
//        apiError.setErrorCode("duplicate_number");
//        return buildResponseEntity(apiError);
//    }  

//     /**
//     * Handle Exception When Message Body is Empty 
//     */
//    @ExceptionHandler(MessageBodyIsEmptyException.class)
//    protected ResponseEntity<Object> messageBodyIsEmptyException(
//            MessageBodyIsEmptyException ex) {
//        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
//        apiError.setMessage("Empty message content.");
//        apiError.setDebugMessage("Message content is empty.");
//        apiError.setErrorCode("empty_body");
//        return buildResponseEntity(apiError);
//    }  

//      /**
//     * Handle Exception When Message Body is Over 160 characters long 
//      */
//    @ExceptionHandler(MessageBodyOverLimit.class)
//    protected ResponseEntity<Object> messageBodyOverLimit(
//            MessageBodyOverLimit ex) {
//        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
//        apiError.setMessage("Message character over limit.");
//        apiError.setDebugMessage("Message is over 160 chars long.");
//        apiError.setErrorCode("body_over_limit");
//        return buildResponseEntity(apiError);
//    } 
 
//     /**
//     * Handle Exception When Message Body is Over 160 characters long 
//      */
//    @ExceptionHandler(DestinationIsEmptyException.class)
//    protected ResponseEntity<Object> destinationIsEmptyException(
//            DestinationIsEmptyException ex) {
//        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
//        apiError.setMessage("Empty receivers list.");
//        apiError.setDebugMessage("Destination list is empty.");
//        apiError.setErrorCode("empty_number_list");
//        return buildResponseEntity(apiError);
//    } 

// /**
//     * Handle Exception When Destination Number Format is Incorrect 
//      */
//    @ExceptionHandler(DestinationNumberFormatError.class)
//    protected ResponseEntity<Object> destinationNumberFormatError(
//            DestinationNumberFormatError ex) {
//        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
//        apiError.setMessage("Invalid destination address.");
//        apiError.setDebugMessage("The destination number you are attempting to send to is invalid.");
//        apiError.setErrorCode("invalid_number");
//        return buildResponseEntity(apiError);
//    } 



//     private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
//         return new ResponseEntity<>(apiError, apiError.getStatus());
//     }
// }