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
// package xyz.idtlabs.smsgateway.exception;

// import com.fasterxml.jackson.annotation.JsonFormat;
// import com.fasterxml.jackson.annotation.JsonTypeInfo;
// import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
// import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
// import lombok.*;
// import org.hibernate.validator.internal.engine.path.PathImpl;
// import org.springframework.http.HttpStatus;
// import org.springframework.validation.FieldError;
// import org.springframework.validation.ObjectError;

// import javax.validation.ConstraintViolation;
// import java.time.LocalDateTime;
// import java.util.ArrayList;
// import java.util.Date;
// import java.util.List;
// import java.util.Set;

// @Data
// @JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.CUSTOM, property = "error", visible = true)
// @JsonTypeIdResolver(LowerCaseClassNameResolver.class)
// public class ApiError {

//     private HttpStatus status;
//     @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
//     private LocalDateTime timestamp;
//     private String message;
//     private String debugMessage;
//     private List<ApiSubError> subErrors; 
//     private String errorCode;

//     private ApiError() {
//         timestamp = LocalDateTime.now();
//     }

//     ApiError(HttpStatus status) {
//         this();
//         this.status = status;
//     }

//     ApiError(HttpStatus status, Throwable ex) {
//         this();
//         this.status = status;
//         this.message = "Unexpected error";
//         this.debugMessage = ex.getLocalizedMessage();
//     }

//     ApiError(HttpStatus status, String message, Throwable ex) {
//         this();
//         this.status = status;
//         this.message = message;
//         this.debugMessage = ex.getLocalizedMessage();
//     }

//     private void addSubError(ApiSubError subError) {
//         if (subErrors == null) {
//             subErrors = new ArrayList<>();
//         }
//         subErrors.add(subError);
//     }

//     private void addValidationError(String object, String field, Object rejectedValue, String message) {
//         addSubError(new ApiValidationError(object, field, rejectedValue, message));
//     }

//     private void addValidationError(String object, String message) {
//         addSubError(new ApiValidationError(object, message));
//     }

//     private void addValidationError(FieldError fieldError) {
//         this.addValidationError(
//                 fieldError.getObjectName(),
//                 fieldError.getField(),
//                 fieldError.getRejectedValue(),
//                 fieldError.getDefaultMessage());
//     }

//     void addValidationErrors(List<FieldError> fieldErrors) {
//         fieldErrors.forEach(this::addValidationError);
//     }

//     private void addValidationError(ObjectError objectError) {
//         this.addValidationError(
//                 objectError.getObjectName(),
//                 objectError.getDefaultMessage());
//     }

//     void addValidationError(List<ObjectError> globalErrors) {
//         globalErrors.forEach(this::addValidationError);
//     }

//     /**
//      * Utility method for adding error of ConstraintViolation. Usually when a @Validated validation fails.
//      * @param cv the ConstraintViolation
//      */
//     private void addValidationError(ConstraintViolation<?> cv) {
//         this.addValidationError(
//                 cv.getRootBeanClass().getSimpleName(),
//                 ((PathImpl) cv.getPropertyPath()).getLeafNode().asString(),
//                 cv.getInvalidValue(),
//                 cv.getMessage());
//     }

//     void addValidationErrors(Set<ConstraintViolation<?>> constraintViolations) {
//         constraintViolations.forEach(this::addValidationError);
//     }



//     abstract class ApiSubError {

//     }

//     @Data
//     @EqualsAndHashCode(callSuper = false)
//     @AllArgsConstructor
//     class ApiValidationError extends ApiSubError {
//         private String object;
//         private String field;
//         private Object rejectedValue;
//         private String message;

//         ApiValidationError(String object, String message) {
//             this.object = object;
//             this.message = message;
//         }
//     }
// }

// class LowerCaseClassNameResolver extends TypeIdResolverBase {

//     @Override
//     public String idFromValue(Object value) {
//         return value.getClass().getSimpleName().toLowerCase();
//     }

//     @Override
//     public String idFromValueAndType(Object value, Class<?> suggestedType) {
//         return idFromValue(value);
//     }

//     @Override
//     public JsonTypeInfo.Id getMechanism() {
//         return JsonTypeInfo.Id.CUSTOM;
//     }
// }