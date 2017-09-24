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
package xyz.idtlabs.smsgateway.sms.api;

import java.util.Collection;
import java.util.List;

import xyz.idtlabs.smsgateway.constants.MessageGatewayConstants; 
import xyz.idtlabs.smsgateway.exception.PlatformApiDataValidationException;
import xyz.idtlabs.smsgateway.exception.PlatformApiInvalidParameterException; 
import xyz.idtlabs.smsgateway.sms.data.DeliveryStatusData;
import xyz.idtlabs.smsgateway.helpers.PlatformApiDataValidationExceptionMapper;
import xyz.idtlabs.smsgateway.helpers.PlatformApiInvalidParameterExceptionMapper;
import xyz.idtlabs.smsgateway.helpers.PlatformResourceNotFoundExceptionMapper;
import xyz.idtlabs.smsgateway.helpers.ApiGlobalErrorResponse;
import xyz.idtlabs.smsgateway.sms.domain.SMSMessage; 
import xyz.idtlabs.smsgateway.sms.domain.SubmittedMessages;
import xyz.idtlabs.smsgateway.sms.domain.SendRestSMS;
import xyz.idtlabs.smsgateway.sms.service.SMSMessageService; 
import xyz.idtlabs.smsgateway.tenants.service.TenantsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;   
import org.springframework.web.bind.annotation.RestController; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils; 
import com.codahale.metrics.Counter;
import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Gauge;
import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;
import com.ryantenney.metrics.annotation.Counted;




@RestController
@RequestMapping("/messages")
public class SmsApiResource {

	//This class sends TRANSACTIONAL & PROMOTIONAL SMS
	private SMSMessageService smsMessageService ;
    private TenantsService tenantService;
    private static final Logger logger = LoggerFactory.getLogger(SMSMessageService.class);

	
	@Autowired
    public SmsApiResource(final SMSMessageService smsMessageService, final TenantsService tenantService) {
		this.smsMessageService = smsMessageService ; 
        this.tenantService = tenantService;
    } 

   // -------------------Send Message via HTTP GET Request--------------------------------------------------------
    @Metered(name = "HTTP Send Message Meter", absolute=true)
    @Counted(name = "httpSendCount",monotonic=true)
    @ExceptionMetered(name = "HTTP Send Exception Meter", absolute = true)
    @RequestMapping(value="/http/send",params = {"apiKey", "to","body"},method = RequestMethod.GET) 
    public ResponseEntity<?> sendMessageViaHttp(
      @RequestParam(value="apiKey") String apiKey,  @RequestParam(value="to") String to,
                    @RequestParam(value="body",required=true) String body) {
        tenantService.confirmClientCanSendSms(apiKey);  
        smsMessageService.validateMessageAndDestination(to,body); 
        smsMessageService.sendSMS(apiKey,to,body);
        SubmittedMessages submittedMessages = new SubmittedMessages();
        submittedMessages.setTo(to);
        submittedMessages.setId(apiKey); 
        submittedMessages.setAccepted();
 
        return new ResponseEntity<SubmittedMessages>(submittedMessages,HttpStatus.OK);
    }  

  


      //-------------------Send Message via REST POST Request--------------------------------------------------------
    @Metered(name = "REST Send Message Meter", absolute=true)
    @Counted(name = "restSendCount",monotonic=true)
    @ExceptionMetered(name = "REST Send Exception Meter", absolute = true)
    @RequestMapping(method = RequestMethod.POST,consumes = {"application/json"}, produces = {"application/json"}) 
    public ResponseEntity<?> sendMessageViaRest(
        @RequestHeader(MessageGatewayConstants.TENANT_APPKEY_HEADER) 
        final String apiKey, @RequestBody final SendRestSMS smsMessage) { 
        List<String> to = smsMessage.getTo(); 
        String numbers = StringUtils.join(to, ',');
        String body = smsMessage.getBody();
        tenantService.confirmClientCanSendSms(apiKey);
        smsMessageService.validateMessageAndDestination(numbers,body); 
        smsMessageService.sendSMS(apiKey,numbers,body);
        SubmittedMessages submittedMessages = new SubmittedMessages();
        submittedMessages.setTo(numbers);
        submittedMessages.setId(apiKey);
        submittedMessages.setAccepted();
        return new ResponseEntity<SubmittedMessages>(submittedMessages,HttpStatus.OK);
    }

    // @RequestMapping(method = RequestMethod.POST, consumes = {"application/json"}, produces = {"application/json"})
    // public ResponseEntity<Void> sendShortMessages(@RequestHeader(MessageGatewayConstants.TENANT_IDENTIFIER_HEADER) final String tenantId,
    // 		@RequestHeader(MessageGatewayConstants.TENANT_APPKEY_HEADER) final String appKey, 
    // 		@RequestBody final List<SMSMessage> payload) {
    // 	this.smsMessageService.sendShortMessage(tenantId, appKey, payload);
    //    return new ResponseEntity<>(HttpStatus.ACCEPTED);
    // }
    
    @RequestMapping(value = "/report", method = RequestMethod.POST, consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<Collection<DeliveryStatusData>> getDeliveryStatus(@RequestHeader(MessageGatewayConstants.TENANT_IDENTIFIER_HEADER) final String tenantId,
    		@RequestHeader(MessageGatewayConstants.TENANT_APPKEY_HEADER) final String appKey, 
    		@RequestBody final Collection<Long> internalIds) {
    	Collection<DeliveryStatusData> deliveryStatus = this.smsMessageService.getDeliveryStatus(tenantId, appKey, internalIds) ;
    	return new ResponseEntity<>(deliveryStatus, HttpStatus.OK);
    } 

    @ExceptionHandler({PlatformApiDataValidationException.class})
    public ResponseEntity<ApiGlobalErrorResponse> handlePlatformApiDateValidationException(PlatformApiDataValidationException e) {
     return PlatformApiDataValidationExceptionMapper.sendMessageDataValidationException(e);
    } 

    @ExceptionHandler({PlatformApiInvalidParameterException.class})
    public ResponseEntity<ApiGlobalErrorResponse> handlePlatformApiInvalidParameterException(PlatformApiInvalidParameterException e) {
     return PlatformApiInvalidParameterExceptionMapper.toResponse(e);
    }
}
