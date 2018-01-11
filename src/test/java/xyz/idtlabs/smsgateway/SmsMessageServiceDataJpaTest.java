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
package xyz.idtlabs.smsgateway;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import xyz.idtlabs.smsgateway.configuration.SmsFireboltConfiguration;
import xyz.idtlabs.smsgateway.sms.domain.SMSMessage;
import xyz.idtlabs.smsgateway.sms.repository.SmsOutboundMessageRepository;
import xyz.idtlabs.smsgateway.sms.service.SMSMessageService;
import xyz.idtlabs.smsgateway.tenants.domain.Tenant;
import xyz.idtlabs.smsgateway.tenants.repository.TenantRepository;
import static org.springframework.boot.autoconfigure.jdbc.EmbeddedDatabaseConnection.H2;

@RunWith(SpringRunner.class)
@DataJpaTest
@ContextConfiguration(classes=SmsFireboltConfiguration.class)
@PropertySource("classpath:config.properties")
@AutoConfigureTestDatabase(connection = H2)
public class SmsMessageServiceDataJpaTest { 
	
	@Autowired
	private SMSMessageService smsMessageService;
	
	@Autowired
	private TenantRepository tenantRepository;  
	
	@Autowired 
	private SmsOutboundMessageRepository smsOutboundMessageRepository;

	@After
    public void tearDown() throws Exception {
        tenantRepository.deleteAll(); 
        smsOutboundMessageRepository.deleteAll();
    } 
	
	@Test
    public void saveMessageToDatabaseSuccessfully() throws Exception {
        Tenant testClient = new Tenant("defaultApiKey","testClient", "testClientDisplay"); 
        tenantRepository.save(testClient);         
        Tenant retrievedClient = tenantRepository.findByName("testClient");
        assertEquals("client record stored successfully",testClient.getName(),retrievedClient.getName());   
        String apiKey = retrievedClient.getApiKey();
        Long tenantId = retrievedClient.getId();
        smsMessageService.saveSMS(apiKey, "+23277775775", "helloText");
        SMSMessage message = smsOutboundMessageRepository.findByTenantId(tenantId);  
        assertEquals("message is retrieved properly","helloText",message.getMessage());
    }
}
