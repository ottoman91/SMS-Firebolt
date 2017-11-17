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

//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.client.RestTemplate;
//import com.github.tomakehurst.wiremock.junit.WireMockRule;
//import static com.github.tomakehurst.wiremock.client.WireMock.*;
//import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
//import org.springframework.http.HttpStatus;
//
//public class WiremockTests { 
//	
//	RestTemplate restTemplate;
//    ResponseEntity <String> response;
//
//    @Rule
//    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(8089).httpsPort(8443));
//
//    @Before
//    public void setup() throws Exception {
//        restTemplate = new RestTemplate();
//        response = null;
//    }
//
//    @Test
//    public void givenWireMockAdminEndpoint_whenGetWithoutParams_thenVerifyRequest() {
//
//        RestTemplate restTemplate = new RestTemplate();
//
//        response = restTemplate.getForEntity("http://localhost:8089/__admin", String.class);
//
//        assertThat("Verify Response Body", response.getBody().contains("mappings"));
//        assertThat("Verify Status Code", response.getStatusCode().equals(HttpStatus.OK));
//    }
//
//    @Test
//    public void givenWireMockEndpoint_whenGetWithoutParams_thenVerifyRequest() {
//        stubFor(get(urlEqualTo("/api/resource/"))
//                .willReturn(aResponse()
//                        .withStatus(HttpStatus.OK.value())
//                        .withHeader("Content-Type", TEXT_PLAIN_VALUE)
//                        .withBody("test")));
//
//        response = restTemplate.getForEntity("http://localhost:8089/api/resource/", String.class);
//
//        assertThat("Verify Response Body", response.getBody().contains("test"));
//        assertThat("Verify Status Code", response.getStatusCode().equals(HttpStatus.OK));
//
//        verify(getRequestedFor(urlMatching("/api/resource/.*")));
//    }
//
//}
