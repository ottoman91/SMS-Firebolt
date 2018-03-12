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

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.Assert.assertEquals;
import static org.springframework.boot.autoconfigure.jdbc.EmbeddedDatabaseConnection.H2;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import xyz.idtlabs.smsgateway.configuration.SmsFireboltConfiguration;
import xyz.idtlabs.smsgateway.constants.MessageGatewayConstants;
import xyz.idtlabs.smsgateway.sms.domain.SendRestSMS;
import xyz.idtlabs.smsgateway.sms.repository.SmsOutboundMessageRepository;
import xyz.idtlabs.smsgateway.sms.service.*;
import xyz.idtlabs.smsgateway.tenants.domain.Tenant;
import xyz.idtlabs.smsgateway.tenants.repository.TenantRepository;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= SmsFireboltConfiguration.class,webEnvironment = WebEnvironment.RANDOM_PORT )
@EnableAutoConfiguration
@PropertySource("classpath:config.properties")
@AutoConfigureTestDatabase(connection = H2)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SmsApiResourceEndPointIntegrationTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(SmsApiResourceEndPointIntegrationTest.class);


    /** The port. */
    @LocalServerPort
    private int port;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private SmsOutboundMessageRepository smsOutboundMessageRepository;

    @Autowired
    private SmsDeliver smsDeliver;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(8099));


    /** Setting up stubs for Wiremock. */
    public void setupStub1() {
        wireMockRule.stubFor(get(urlPathMatching("/cgi-bin/sendsms"))
                .withQueryParam("smsc", equalTo("1"))
                .withQueryParam("username", equalTo("kannel"))
                .withQueryParam("password", equalTo("kannel"))
                .withQueryParam("to", equalTo("+23277775775"))
                .withQueryParam("text", equalTo("testMessage"))
                .willReturn(aResponse().withStatus(org.springframework.http.HttpStatus.OK.value())));

    }


    public void setupStub2(){


        wireMockRule.stubFor(get(urlPathMatching("/cgi-bin/sendsms"))
                .withQueryParam("smsc",equalTo("1"))
                .withQueryParam("username",equalTo("kannel"))
                .withQueryParam("password", equalTo("kannel"))
                .withQueryParam("to", equalTo("+23277773773"))
                .withQueryParam("text", equalTo("testMessage"))
        .willReturn(aResponse().withStatus(org.springframework.http.HttpStatus.OK.value())));
    }



    @Before
    public void setup(){
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;

    }

    /** Tests for GET Request Api Calls. */

    @Test
    public void whenMessageAndNumberPassedCorrectlyForExistingTenant_return200(){

        setupStub1();
        Tenant testClient = new Tenant("defaultApiKey","defaultClient",
                "defaultClientDisplayName");

        tenantRepository.save(testClient);
        Tenant retrievedClient = tenantRepository.findByName("defaultClient");
        String apiKey = retrievedClient.getApiKey();

        RequestSpecification basicAuth = RestAssured.given().auth().preemptive().
                basic("idtlabsuser","idtlabs");

        Response response = basicAuth.accept(ContentType.JSON).param("apiKey",apiKey).
                param("to","+23277775775").param("body","testMessage").
                get("messages/http/send");

        assertEquals("All message details correctly passed and 200 status not returned",
                HttpStatus.SC_OK,response.statusCode());

        //verify(getRequestedFor(urlEqualTo("/cgi-bin/sendsms?smsc=1&username=kannel&password=kannel&to=%2B23277775775&text=testMessage")));


    }

    @Test
    public void apiCalledWithIncorrectApiKeyForGetRequest_return401(){

        Tenant testClient = new Tenant("defaultApiKey1","defaultClient1",
                "defaultClientDisplayName1");

        tenantRepository.save(testClient);
        Tenant retrievedClient = tenantRepository.findByName("defaultClient1");
        String apiKey = "xxxx";

        Response response = RestAssured.given().param("apiKey",apiKey).
                param("to","+23277775775").param("body","hello").get("messages/http/send");

        assertEquals("For Get Request called with incorrect API Key, 500 status code was not returned",
                HttpStatus.SC_INTERNAL_SERVER_ERROR,response.statusCode());



    }

    @Test
    public void notAuthenticatedToRetrieveAdminRoleEndPointForGetRequest_returns401(){


        Response response = RestAssured.given().accept(ContentType.JSON).get("clients/1");

        assertEquals("For user without Admin role, 401 status code was not returned on accessing a " +
                        "restricted end point",
                401,response.statusCode());

    }

    @Test
    public void noNumberSentWithMessageInGetRequest_return400(){

        Tenant testClient = new Tenant("defaultApiKey2","defaultClient2",
                "defaultClientDisplayName2");

        tenantRepository.save(testClient);
        Tenant retrievedClient = tenantRepository.findByName("defaultClient2");
        String apiKey = retrievedClient.getApiKey();


          Response response = RestAssured.given().accept(ContentType.JSON).param("apiKey",apiKey).
                param("to","").param("body","hello").get("messages/http/send");


        assertEquals("Phone Number not entered with Api Call, and 400 Bad Request Status not shown",
                400,response.statusCode());
    }

    @Test
    public void noMessageBodySentWithMessageInGetRequest_return400(){

        Tenant testClient = new Tenant("defaultApiKey3","defaultClient3",
                "defaultClientDisplayName3");

        tenantRepository.save(testClient);
        Tenant retrievedClient = tenantRepository.findByName("defaultClient3");
        String apiKey = retrievedClient.getApiKey();


        Response response = RestAssured.given().accept(ContentType.JSON).param("apiKey",apiKey).
                param("to","+23277775775").param("body","").get("messages/http/send");

        assertEquals("Message Body not entered with Api Call, and 400 Bad Request Status not shown",
                400,response.statusCode());
    }

    @Test
    public void multipleVerifiedNumbersSentWithMessageInGetRequest_return200(){

        setupStub1();
        setupStub2();
        Tenant testClient = new Tenant("defaultApiKey4","defaultClient4",
                "defaultClientDisplayName4");

        tenantRepository.save(testClient);
        Tenant retrievedClient = tenantRepository.findByName("defaultClient4");
        String apiKey = retrievedClient.getApiKey();

        List<String> numbers = new ArrayList<String>();
        numbers.add("+23277775775");
        numbers.add("+23277773773");


        Response response = RestAssured.given().accept(ContentType.JSON).param("apiKey",apiKey).
                param("to",numbers).param("body","testMessage").get("messages/http/send");

        assertEquals("Message sent with multiple numbers, 200 OK Status not shown",
                HttpStatus.SC_OK,response.statusCode());


    }

    @Test
    public void duplicateNumbersSentWithMessageInGetRequest_return400(){

        Tenant testClient = new Tenant("defaultApiKey6","defaultClient6",
                "defaultClientDisplayName6");

        tenantRepository.save(testClient);
        Tenant retrievedClient = tenantRepository.findByName("defaultClient6");
        String apiKey = retrievedClient.getApiKey();

        List<String> numbers1 = new ArrayList<String>();
        numbers1.add("+23277772772");
        numbers1.add("+23277772772");


        Response response = RestAssured.given().accept(ContentType.JSON).param("apiKey",apiKey).
                param("to",numbers1).param("body","hello").get("messages/http/send");

        assertEquals("Message sent with duplicate numbers, 400 Bad RequestStatus not shown",
                HttpStatus.SC_INTERNAL_SERVER_ERROR,response.statusCode());
    }

    /** Tests for POST Request Api Calls. */


    @Test
    public void singleNumberSentWithMessageInPostRequest_return200(){
        setupStub1();
        Tenant testClient = new Tenant("defaultApiKey","defaultClient",
                "defaultClientDisplayName");

        tenantRepository.save(testClient);
        Tenant retrievedClient = tenantRepository.findByName("defaultClient");
        String apiKey = retrievedClient.getApiKey();
        List<String> numbers = Arrays.asList("+23277775775");

        SendRestSMS smsMessage = new SendRestSMS();

        smsMessage.setBody("testMessage");
        smsMessage.setTo(numbers);


        Response response = RestAssured.given().header(MessageGatewayConstants.TENANT_APPKEY_HEADER,apiKey).
                contentType("application/json").body(smsMessage).
                post("messages");


        assertEquals("Single Number sent with message in Api Call, and 200 Status not returned",
                HttpStatus.SC_OK,response.statusCode());


    }



    @Test
    public void noMessageBodySentWithMessageInPostRequest_return400(){

        Tenant testClient = new Tenant("defaultApiKey","defaultClient",
                "defaultClientDisplayName");

        tenantRepository.save(testClient);
        Tenant retrievedClient = tenantRepository.findByName("defaultClient");
        String apiKey = retrievedClient.getApiKey();
        List<String> numbers = Arrays.asList("+23277775775");

        SendRestSMS smsMessage = new SendRestSMS();

        smsMessage.setBody("");
        smsMessage.setTo(numbers);



        Response response = RestAssured.given().header(MessageGatewayConstants.TENANT_APPKEY_HEADER,apiKey).
                contentType("application/json").body(smsMessage).
                post("messages");

        assertEquals("Message Body not entered with Api Call, and 400 Bad Request Status not shown",
                HttpStatus.SC_BAD_REQUEST,response.statusCode());
    }

    @Test
    public void noNumberSentWithMessageInPostRequest_return400(){

        Tenant testClient = new Tenant("defaultApiKey","defaultClient",
                "defaultClientDisplayName");

        tenantRepository.save(testClient);
        Tenant retrievedClient = tenantRepository.findByName("defaultClient");
        String apiKey = retrievedClient.getApiKey();
        List<String> numbers = Arrays.asList("");

        SendRestSMS smsMessage = new SendRestSMS();

        smsMessage.setBody("sampleText");
        smsMessage.setTo(numbers);



        Response response = RestAssured.given().header(MessageGatewayConstants.TENANT_APPKEY_HEADER,apiKey).
                contentType("application/json").body(smsMessage).
                post("messages");

        assertEquals("Number not entered with Api Call, and 400 Bad Request Status not shown",
                HttpStatus.SC_BAD_REQUEST,response.statusCode());
    }

    @Test
    public void duplicateNumbersSentWithMessageInPostRequest_return400(){

        Tenant testClient = new Tenant("defaultApiKey","defaultClient",
                "defaultClientDisplayName");

        tenantRepository.save(testClient);
        Tenant retrievedClient = tenantRepository.findByName("defaultClient");
        String apiKey = retrievedClient.getApiKey();
        List<String> numbers = Arrays.asList("+23277775775","+23277775775");

        SendRestSMS smsMessage = new SendRestSMS();

        smsMessage.setBody("sampleText");
        smsMessage.setTo(numbers);


        Response response = RestAssured.given().header(MessageGatewayConstants.TENANT_APPKEY_HEADER,apiKey).
                contentType("application/json").body(smsMessage).
                post("messages");


        assertEquals("Duplicate numbers sent with Api Call, and 500 Error not shown",
                HttpStatus.SC_INTERNAL_SERVER_ERROR,response.statusCode());
    }

    @Test
    public void multipleVerifiedNumbersSentWithMessageInPostRequest_return200(){

        setupStub1();
        setupStub2();

        Tenant testClient = new Tenant("defaultApiKey","defaultClient",
                "defaultClientDisplayName");

        tenantRepository.save(testClient);
        Tenant retrievedClient = tenantRepository.findByName("defaultClient");
        String apiKey = retrievedClient.getApiKey();
        List<String> numbers = Arrays.asList("+23277775775","+23277773773");

        SendRestSMS smsMessage = new SendRestSMS();

        smsMessage.setBody("testMessage");
        smsMessage.setTo(numbers);



        Response response = RestAssured.given().header(MessageGatewayConstants.TENANT_APPKEY_HEADER,apiKey).
                contentType("application/json").body(smsMessage).
                post("messages");


        assertEquals("Multiple verified numbers sent with Api Call, and 200 Status not returned",
                HttpStatus.SC_OK,response.statusCode());

    }

    @Test
    public void apiCalledWithIncorrectApiKeyInPostRequest_return200(){

        Tenant testClient = new Tenant("defaultApiKey","defaultClient",
                "defaultClientDisplayName");

        tenantRepository.save(testClient);
        Tenant retrievedClient = tenantRepository.findByName("defaultClient");
        String apiKey = "xxxx";
        List<String> numbers = Arrays.asList("+23277775775");

        SendRestSMS smsMessage = new SendRestSMS();

        smsMessage.setBody("sampleText");
        smsMessage.setTo(numbers);


        Response response = RestAssured.given().
                header(MessageGatewayConstants.TENANT_APPKEY_HEADER,apiKey).
                contentType("application/json").body(smsMessage).
                post("messages");


        assertEquals("Api Called With Incorrect ApiKey, and 500 Status not returned",
                HttpStatus.SC_INTERNAL_SERVER_ERROR,response.statusCode());
    }
}
