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
// package xyz.idtlabs.smsgateway.sms.api; 


// import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
// import static com.github.tomakehurst.wiremock.client.WireMock.get;
// import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
// import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
// import static org.assertj.core.api.Assertions.assertThat;

// import org.junit.ClassRule;
// import org.junit.Test;
// import org.junit.runner.RunWith;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.cloud.contract.wiremock.AutoConfigureHttpClient;
// import org.springframework.cloud.contract.wiremock.WireMockSpring;
// import org.springframework.test.annotation.DirtiesContext;
// import org.springframework.test.context.junit4.SpringRunner;

// import com.github.tomakehurst.wiremock.junit.WireMockClassRule; 

// import xyz.idtlabs.smsgateway.sms.domain.Message;
// import xyz.idtlabs.smsgateway.sms.service.SmsDeliver; 




// @RunWith(SpringRunner.class)
// @SpringBootTest("app.baseUrl=https://localhost:6443")
// @DirtiesContext
// @AutoConfigureHttpClient
// public class WiremockHttpsServerApplicationTests {

//     @ClassRule
//     public static WireMockClassRule wiremock = new WireMockClassRule(
//             WireMockSpring.options().httpsPort(6443));

//     @Autowired
//     private SmsDeliver smsDeliver;

//     private Message message = new Message();
    
//     // @Test
//     // public void sendingMessageToKannelBackend() throws Exception {
//     //     stubFor(post(urlEqualTo("/messages"))
//     //             .willReturn(aResponse().withHeader("Content-Type", "text/plain").withBody("Hello World!")));
//     //     assertThat(this.service.go()).isEqualTo("Hello World!");
//     // } 

//     @Test
//     public void sendingMessageToKannelBackend() throws Exception {
//         stubFor(post(urlEqualTo("/messages"))
//                 .withHeader("Content-Type", equalTo("application/json"))
//                 .withRequestBody(containing("\"name\": \"John Doe\""))
//                 .withRequestBody(containing("\"displayName\": \"John Doe MFI\""))
//                 .willReturn(aResponse().withStatus(200)));

//         InputStream jsonInputStream = this.getClass().getClassLoader().getResourceAsStream("wiremock_intro.json");
//         String jsonString = convertInputStreamToString(jsonInputStream);
//         StringEntity entity = new StringEntity(jsonString);

//         CloseableHttpClient httpClient = HttpClients.createDefault();
//         HttpPost request = new HttpPost("http://localhost:8080/clients");
//         request.addHeader("Content-Type", APPLICATION_JSON);
//         request.setEntity(entity);
//         HttpResponse response = httpClient.execute(request);

//         verify(postRequestedFor(urlEqualTo(BAELDUNG_WIREMOCK_PATH))
//                 .withHeader("Content-Type", equalTo(APPLICATION_JSON)));
//         assertEquals(200, response.getStatusLine().getStatusCode());
//     }

// }







// // //code snippet from the official wiredoc stuff.
// // // public class SMSApiResourceIntegrationTest {

// // //     private static final String SMS_MESSAGE_API = "/clients";
// // //     private static final String APPLICATION_JSON = "application/json";

// // //     @Rule
// // //     public WireMockRule wireMockRule = new WireMockRule();

  
// // //     @Test
// // //     public void givenJUnitManagedServer_whenMatchingBody_thenCorrect() throws IOException {
// // //         stubFor(post(urlEqualTo(SMS_MESSAGE_API))
// // //                 .withHeader("Content-Type", equalTo(APPLICATION_JSON))
// // //                 .withRequestBody(containing("\"name\": \"John Doe\""))
// // //                 .withRequestBody(containing("\"displayName\": \"John Doe MFI\""))
// // //                 .willReturn(aResponse().withStatus(200)));

// // //         InputStream jsonInputStream = this.getClass().getClassLoader().getResourceAsStream("wiremock_intro.json");
// // //         String jsonString = convertInputStreamToString(jsonInputStream);
// // //         StringEntity entity = new StringEntity(jsonString);

// // //         CloseableHttpClient httpClient = HttpClients.createDefault();
// // //         HttpPost request = new HttpPost("http://localhost:8080/clients");
// // //         request.addHeader("Content-Type", APPLICATION_JSON);
// // //         request.setEntity(entity);
// // //         HttpResponse response = httpClient.execute(request);

// // //         verify(postRequestedFor(urlEqualTo(BAELDUNG_WIREMOCK_PATH))
// // //                 .withHeader("Content-Type", equalTo(APPLICATION_JSON)));
// // //         assertEquals(200, response.getStatusLine().getStatusCode());
// // //     }


// // //     private static String convertHttpResponseToString(HttpResponse httpResponse) throws IOException {
// // //         InputStream inputStream = httpResponse.getEntity().getContent();
// // //         return convertInputStreamToString(inputStream);
// // //     }

// // //     private static String convertInputStreamToString(InputStream inputStream) {
// // //         Scanner scanner = new Scanner(inputStream, "UTF-8");
// // //         String string = scanner.useDelimiter("\\Z").next();
// // //         scanner.close();
// // //         return string;
// // //     }

// // //     private HttpResponse generateClientAndReceiveResponseForPriorityTests() throws IOException {
// // //         CloseableHttpClient httpClient = HttpClients.createDefault();
// // //         HttpGet request = new HttpGet("http://localhost:8080/baeldung/wiremock");
// // //         request.addHeader("Accept", "text/xml");
// // //         return httpClient.execute(request);
// // //     }
// // // }


























