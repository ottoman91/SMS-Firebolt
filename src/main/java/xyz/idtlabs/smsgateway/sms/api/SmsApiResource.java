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
import xyz.idtlabs.smsgateway.exception.ApiError;
import xyz.idtlabs.smsgateway.sms.data.DeliveryStatusData;
import xyz.idtlabs.smsgateway.sms.domain.SMSMessage; 
import xyz.idtlabs.smsgateway.sms.domain.SubmittedMessages;
import xyz.idtlabs.smsgateway.sms.service.SMSMessageService; 
import xyz.idtlabs.smsgateway.tenants.service.TenantsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;   
import org.springframework.web.bind.annotation.RestController; 

@RestController
@RequestMapping("/messages")
public class SmsApiResource {

	//This class sends TRANSACTIONAL & PROMOTIONAL SMS
	private SMSMessageService smsMessageService ;
    private TenantsService tenantService;
	
	@Autowired
    public SmsApiResource(final SMSMessageService smsMessageService, final TenantsService tenantService) {
		this.smsMessageService = smsMessageService ; 
        this.tenantService = tenantService;
    } 

    //-------------------Send Message via HTTP Send API--------------------------------------------------------
    
    @RequestMapping(value="/http/send",params = {"apiKey", "to","body"},method = RequestMethod.GET) 
    public ResponseEntity<?> sendMessageViaHttp(
      @RequestParam(value="apiKey",required=true) String apiKey,  @RequestParam(value="to",required=true) String to,
                    @RequestParam(value="body",required=true) String body) {
        tenantService.confirmClientCanSendSms(apiKey);  
        smsMessageService.validateMessageAndDestination(to,body);
        //smsMessageService.checkForDuplicateNumbers(to);
        SubmittedMessages submittedMessages = new SubmittedMessages();
        submittedMessages.setTo(to);
        submittedMessages.setId(apiKey);
 
        return new ResponseEntity<SubmittedMessages>(submittedMessages,HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<Void> sendShortMessages(@RequestHeader(MessageGatewayConstants.TENANT_IDENTIFIER_HEADER) final String tenantId,
    		@RequestHeader(MessageGatewayConstants.TENANT_APPKEY_HEADER) final String appKey, 
    		@RequestBody final List<SMSMessage> payload) {
    	this.smsMessageService.sendShortMessage(tenantId, appKey, payload);
       return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(value = "/report", method = RequestMethod.POST, consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<Collection<DeliveryStatusData>> getDeliveryStatus(@RequestHeader(MessageGatewayConstants.TENANT_IDENTIFIER_HEADER) final String tenantId,
    		@RequestHeader(MessageGatewayConstants.TENANT_APPKEY_HEADER) final String appKey, 
    		@RequestBody final Collection<Long> internalIds) {
    	Collection<DeliveryStatusData> deliveryStatus = this.smsMessageService.getDeliveryStatus(tenantId, appKey, internalIds) ;
    	return new ResponseEntity<>(deliveryStatus, HttpStatus.OK);
    }
}
