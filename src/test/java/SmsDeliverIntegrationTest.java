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

//import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
//import static com.github.tomakehurst.wiremock.client.WireMock.get;
//import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
//import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
//import static com.github.tomakehurst.wiremock.client.WireMock.verify;
//import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
//
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpStatus;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import com.github.tomakehurst.wiremock.junit.WireMockRule;
//
//import okhttp3.HttpUrl;
//import xyz.idtlabs.smsgateway.MessageGateway;
//import xyz.idtlabs.smsgateway.sms.service.HttpSMSBackend;
//import xyz.idtlabs.smsgateway.sms.service.HttpSmsDeliver;
//import xyz.idtlabs.smsgateway.sms.service.KannelBackend;
//import xyz.idtlabs.smsgateway.sms.service.SmsDeliver;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes=MessageGateway.class,webEnvironment=WebEnvironment.DEFINED_PORT)
//@EnableAutoConfiguration
//public class SmsDeliverIntegrationTest { 
//	
//	@Configuration
//	static class EmployeeServiceImplTestContextConfiguration {
//		  
//        @Bean
//        public SmsDeliver smsDeliver() {
//            return new HttpSmsDeliver(); 
//   
//        } 
//        
////        @Bean
////        public HttpSMSBackend httpSmsBackend(){
////        	return new KannelBackend("httpbin.org","kannel","kannel");
////        }
//    } 
//	
//	@MockBean
//	private HttpSMSBackend httpSmsBackend;
//	
//	@Autowired
//	private SmsDeliver smsDeliver;    
//	
//	private HttpUrl kannelUrl = new HttpUrl.Builder()
//            .scheme("http")
//            .host("httpbin.org")
//            .build(); 
//	
//	@Before
//	public void setUp() {
//	    
//	    Mockito.when(httpSmsBackend.buildRequest("testMessage","123456")).thenReturn(kannelUrl);
//	}
//	
//	
//	@Rule
//    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(8099));  
//	
//	@Test
//    public void given_ValidMessageWithApiKey_RecordMessageInDatabaseAndSendToKannel(){ 
//    wireMockRule.stubFor(get(urlPathMatching("/httpbin.org"))
//                    .willReturn(aResponse()
//                            .withStatus(HttpStatus.OK.value())
//                            ));  
//    smsDeliver.send("testMessage", "123456");
//	verify(getRequestedFor(urlPathMatching("httpbin.org")));
//
//
//}
//}