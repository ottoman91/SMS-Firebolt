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
//package xyz.idtlabs.smsgateway.tenants.api; 
//
//import xyz.idtlabs.smsgateway.tenants.domain.Tenant;
//import xyz.idtlabs.smsgateway.tenants.service.TenantsService;
//import xyz.idtlabs.smsgateway.tenants.api.TenantsApiResource;
//import xyz.idtlabs.smsgateway.MessageGateway;
//import java.util.Arrays;
//import org.junit.Test;
//import org.junit.Before;
//import org.junit.runner.RunWith;
//import org.mockito.Mockito;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
////import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.RequestBuilder;
//import org.springframework.mock.web.MockHttpServletResponse;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
//import org.springframework.test.context.web.WebAppConfiguration;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@WebAppConfiguration
//@ContextConfiguration(loader=AnnotationConfigWebContextLoader.class, classes = {MessageGateway.class})
//public class TenantsApiResourceTest { 
//
//    // @InjectMocks
//    // private TenantsApiResource tenantsApiResource = new TenantsApiResource();
//
//    private MockMvc mockMvc;
//
//    @Autowired
//    private WebApplicationContext wac;
//
//    @Mock
//    private TenantsService tenantsService; 
//
//    @Mock
//    Tenant mockedTenant;
//
//    Tenant tenant = new Tenant("123","John Doe", "John Doe MFI"); 
//
//    String exampleClientJson = "{\"name\":\"John Doe\",\"displayNamw\":\"John Doe MFI\"}"; 
//
//    @Before
//    public void setup() {
//        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
//
//    } 
//
//    @Test
//    public void createNewClient() throws Exception {
//         mockMvc.perform(MockMvcRequestBuilders.post("/clients")
//        .contentType(MediaType.APPLICATION_JSON)
//        .content("{\"name\" : \"John Doe\", \"displayName\" : \"John Doe MFI\" }")
//        .accept(MediaType.APPLICATION_JSON))
//        .andExpect(jsonPath("$.name").exists())
//        .andExpect(jsonPath("$.displayName").exists())
//        .andExpect(jsonPath("$.name").value("John Doe"))
//        .andExpect(jsonPath("$.displayName").value("John Doe MFI"))
//        .andDo(print());
//    }


// //     @Test
// //     public void createNewClient() throws Exception {

// //         Mockito.when(
// //                 tenantsService.createTenant(mockedTenant)).thenReturn(tenant);

// //         RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
// //                 "/clients").accept(
// //                 MediaType.APPLICATION_JSON).content(exampleClientJson).contentType(MediaType.APPLICATION_JSON);

// //         MvcResult result = mockMvc.perform(requestBuilder).andReturn(); 

// //         MockHttpServletResponse response = result.getResponse();

// //         assertEquals(HttpStatus.CREATED.value(), response.getStatus());
// // } 


// }
