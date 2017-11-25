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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import javax.sql.DataSource;

import xyz.idtlabs.smsgateway.MessageGateway;
import xyz.idtlabs.smsgateway.configuration.MessageGatewayConfiguration;
import xyz.idtlabs.smsgateway.configuration.SmsFireboltConfiguration;
import xyz.idtlabs.smsgateway.configuration.SpringConfiguringClass;
import xyz.idtlabs.smsgateway.service.SecurityService;
import xyz.idtlabs.smsgateway.sms.providers.SMSProviderFactory;
import xyz.idtlabs.smsgateway.sms.repository.SmsOutboundMessageRepository;
import xyz.idtlabs.smsgateway.sms.service.HttpSMSBackend;
import xyz.idtlabs.smsgateway.sms.service.HttpSmsDeliver;
import xyz.idtlabs.smsgateway.sms.service.KannelBackend;
import xyz.idtlabs.smsgateway.sms.service.SMSMessageService;
import xyz.idtlabs.smsgateway.sms.service.SmsDeliver;
import xyz.idtlabs.smsgateway.tenants.domain.Tenant;
import xyz.idtlabs.smsgateway.tenants.repository.TenantRepository;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

import javax.annotation.Resource;

import static org.springframework.boot.autoconfigure.jdbc.EmbeddedDatabaseConnection.H2;

@RunWith(SpringRunner.class)
@DataJpaTest
@ContextConfiguration(classes=MessageGateway.class)
@PropertySource("classpath:config.properties")
@AutoConfigureTestDatabase(connection = H2)
public class SmsMessageServiceIntegrationTest { 
	
	@Configuration
	static class EmployeeServiceImplTestContextConfiguration {
		
		@Value("${kannel.url}")
		String kannelUrl;
		
		@Value("${kannel.username}")
	    String kannelUserName; 
		
		@Value("${kannel.password}")
		String kannelPassword;  
		
		@Value("#{new Integer('${kannel.port}')}")
	    int kannelPort;  
		
//		private SmsOutboundMessageRepository smsOutboundMessageRepository; 
//		
//		private SMSProviderFactory smsProviderFactory; 
//		
//		private DataSource dataSource; 
//		
//		private SecurityService securityService; 
		
		@Value("${country.codes}")
        String countryCodes;

        @Bean
        public SmsDeliver smsDeliver() {
            return new HttpSmsDeliver(); 
        }  
        
//        @Bean
//        public SMSMessageService smsMessageService(){
//        	return new SMSMessageService(smsOutboundMessageRepository, smsProviderFactory, dataSource, null, kannelPassword);
//        }
        @Bean
        public HttpSMSBackend httpSmsBackend(){
        	return new KannelBackend(kannelUrl,kannelPort,kannelUserName,kannelPassword);
        } 
    } 
	
	@Autowired
	private SmsDeliver smsDeliver;  
	
//	@Autowired
//	private SMSMessageService smsMessageService;
	
	@Autowired
	private TenantRepository tenantRepository;  
	
//	@Autowired 
//	private SmsOutboundMessageRepository smsOutboundMessageRepository;

	@After
    public void tearDown() throws Exception {
        tenantRepository.deleteAll(); 
//        smsOutboundMessageRepository.deleteAll();
    } 
	
	
	@Test
    public void shouldSaveAndFetchClient() throws Exception {
        Tenant testClient = new Tenant("defaultApiKey","testClient", "testClientDisplay"); 
        
        tenantRepository.save(testClient); 
        
        Tenant retrievedClient = tenantRepository.findByName("testClient");
        
        assertEquals("client record stored successfully",testClient.getName(),retrievedClient.getName());  
        

        
        
      
    }

}
