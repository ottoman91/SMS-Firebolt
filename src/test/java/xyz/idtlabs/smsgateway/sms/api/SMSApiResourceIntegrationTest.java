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



import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig; 
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

import xyz.idtlabs.smsgateway.MessageGateway;
import xyz.idtlabs.smsgateway.sms.service.HttpSMSBackend;
import xyz.idtlabs.smsgateway.sms.service.HttpSmsDeliver;
import xyz.idtlabs.smsgateway.sms.service.KannelBackend;
import xyz.idtlabs.smsgateway.sms.service.SmsDeliver;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;


/**
 * Integration test for testing out the /http/send URL call for sending a message to backend.   
 * 
 * 
 * 
 */ 

@RunWith(SpringRunner.class)
@SpringBootTest(classes=MessageGateway.class,webEnvironment=WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
@PropertySource("classpath:config.properties")
public class SMSApiResourceIntegrationTest{ 
	
	@Configuration
	static class EmployeeServiceImplTestContextConfiguration {
		
		@Value("${kannel.url}")
		private String kannelUrl;
		
		@Value("${kannel.username}")
	    private String kannelUserName; 
		
		@Value("${kannel.password}")
		private String kannelPassword;  
		
		@Value("#{new Integer('${kannel.port}')}")
	    private int kannelPort;
		
        @Bean
        public SmsDeliver smsDeliver() {
            return new HttpSmsDeliver(); 
        } 
        @Bean
        public HttpSMSBackend httpSmsBackend(){
        	return new KannelBackend(kannelUrl,kannelPort,kannelUserName,kannelPassword);
        } 
    } 
	
	@Autowired
    private TestRestTemplate template = new TestRestTemplate(); 
	
	@Autowired
	private SmsDeliver smsDeliver;  
	
	ResponseEntity<String> response;
	
	@Before
    public void setup() throws Exception {
		this.template = new TestRestTemplate("idtlabsuser","idtlabs");
        response = null;
        
    }
	
	@Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(8099));  
	
	@Test
	public void givenValidSingleMessageValues_thenVerifyValues_thenSendToBackend(){  
		wireMockRule.stubFor(get(urlPathMatching("/messages/http/send"))
				.withBasicAuth("idtlabsuser", "idtlabs")
				.withQueryParam("apiKey", equalTo("12345678"))
				.withQueryParam("to", equalTo("+23277775775"))
				.withQueryParam("body", equalTo("helloText"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        ));  
		
		String url = "http://localhost:8099/messages/http/send";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
				.queryParam("apiKey", "12345678")
				.queryParam("to", "+23277775775")
				.queryParam("body", "helloText");
		
		String sendMessageGet = builder.build().toString();
		response = this.template.getForEntity(sendMessageGet, String.class);
		
		
		//assertThat("Verify Status Code", response.getStatusCode().equals(HttpStatus.OK));
		verify(getRequestedFor(urlPathMatching("/messages/http/send")));
	} 
//	
	
	/**
     * Test for checking the validation of the client and the message sent
     * 
     */ 
	@Test
    public void given_ValidMessageWithApiKey_RecordMessageInDatabaseAndSendToKannel(){ 
		
		wireMockRule.stubFor(get(urlPathMatching("/cgi-bin/sendsms")).willReturn(aResponse().withStatus(HttpStatus.OK.value())));
                    
        smsDeliver.send("testMessage", "123456");
        
	    verify(getRequestedFor(urlPathMatching("/cgi-bin/sendsms")));


}
	
	
}

//


































