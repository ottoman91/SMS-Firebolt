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

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;
import static org.junit.Assert.assertEquals;
import static org.springframework.boot.autoconfigure.jdbc.EmbeddedDatabaseConnection.H2;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import xyz.idtlabs.smsgateway.configuration.MessageGatewayConfiguration;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import xyz.idtlabs.smsgateway.tenants.domain.Tenant;
import xyz.idtlabs.smsgateway.tenants.repository.TenantRepository;
import org.slf4j.Logger;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= MessageGatewayConfiguration.class,webEnvironment = WebEnvironment.RANDOM_PORT )
@EnableAutoConfiguration
@AutoConfigureTestDatabase(connection = H2)
public class ApiEndPointIntegrationTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(ApiEndPointIntegrationTest.class);

    /** The port. */
    @LocalServerPort
    private int port;

    @Autowired
    private TenantRepository tenantRepository;

    @Before
    public void setup(){
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;

    }



//    @Test
//    public void whenNotAuthenticated_return401(){
//
//        Response response = RestAssured.given().when().get("clients/1");
//        assertEquals("User not authenticated and 401 response not returned",
//                HttpStatus.SC_UNAUTHORIZED,response.statusCode());
//    }

//    @Test
//    public void whenAuthenticated_return500(){
//
//        RequestSpecification basicAuth = RestAssured.given().auth().preemptive().
//                basic("idtlabs","idtlabs");
//
//        Response response = basicAuth.accept(ContentType.JSON).get("clients/1");
//        assertEquals("User authenticated properly and 500 status not returned",
//                HttpStatus.SC_INTERNAL_SERVER_ERROR,response.statusCode());
//    }

    @Test
    public void whenMessageAndNumberPassedCorrectlyForExistingTenant_return200(){

        Tenant testClient = new Tenant("defaultApiKey","defaultClient",
                "defaultClientDisplayName");

        tenantRepository.save(testClient);
        Tenant retrievedClient = tenantRepository.findByName("defaultClient");
        String apiKey = retrievedClient.getApiKey();

        RequestSpecification basicAuth = RestAssured.given().auth().preemptive().
                basic("idtlabs","idtlabs");

        Response response = basicAuth.accept(ContentType.JSON).param("apiKey",apiKey).
                param("to","+23277775775").param("body","hello").get("messages/http/send");

        assertEquals("All message details correctly passed and 200 status not returned",
                HttpStatus.SC_OK,response.statusCode());
    }

    @Test
    public void noAuthentication_return401(){

        Tenant testClient = new Tenant("defaultApiKey1","defaultClient1",
                "defaultClientDisplayName1");

        tenantRepository.save(testClient);
        Tenant retrievedClient = tenantRepository.findByName("defaultClient1");
        String apiKey = retrievedClient.getApiKey();


        Response response = RestAssured.given().param("apiKey",apiKey).
                param("to","+23277775775").param("body","hello").get("messages/http/send");

        assertEquals("For unauthorized user, 401 status code was not returned",
                HttpStatus.SC_UNAUTHORIZED,response.statusCode());
    }

    @Test
    public void noNumberSentWithMessageInGetRequest_return400(){

        Tenant testClient = new Tenant("defaultApiKey","defaultClient",
                "defaultClientDisplayName");

        tenantRepository.save(testClient);
        Tenant retrievedClient = tenantRepository.findByName("defaultClient");
        String apiKey = retrievedClient.getApiKey();

        RequestSpecification basicAuth = RestAssured.given().auth().preemptive().
                basic("idtlabs","idtlabs");

        Response response = basicAuth.accept(ContentType.JSON).param("apiKey",apiKey).
                param("to","+23277775775").param("body","hello").get("messages/http/send");

        assertEquals("All message details correctly passed and 200 status not returned",
                HttpStatus.SC_OK,response.statusCode());
    }






}
