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
package xyz.idtlabs.smsgateway.sms.providers;

import java.util.Base64;

import xyz.idtlabs.smsgateway.constants.MessageGatewayConstants;
import xyz.idtlabs.smsgateway.exception.MessageGatewayException;
import xyz.idtlabs.smsgateway.sms.domain.SMSBridge;
import xyz.idtlabs.smsgateway.sms.domain.SMSMessage;

public abstract class SMSProvider {
	
	public abstract void sendMessage(final SMSBridge smsBridgeConfig, final SMSMessage message)
	        throws MessageGatewayException ;
	
	protected String encodeBase64(final SMSBridge smsBridgeConfig) {
		String tenant = smsBridgeConfig.getTenantId().toString() ;
		String username = smsBridgeConfig.getConfigValue(MessageGatewayConstants.PROVIDER_ACCOUNT_ID) ;
    	String password = smsBridgeConfig.getConfigValue(MessageGatewayConstants.PROVIDER_AUTH_TOKEN) ;
        String userPass = username + ":" + password + ":" + tenant;
        return Base64.getEncoder().encodeToString(userPass.getBytes());
    }
}
