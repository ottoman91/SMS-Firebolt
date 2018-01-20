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

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import xyz.idtlabs.smsgateway.configuration.SmsFireboltConfiguration;
import xyz.idtlabs.smsgateway.sms.service.*;
import org.springframework.http.HttpStatus;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= SmsFireboltConfiguration.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@PropertySource("classpath:config.properties")
public class SmsDeliverServiceIntegrationTest {

    @Configuration
    static class SmsDeliverServiceIntegrationTestContextConfiguration{

        @Value("${kannel.url}")
        String kannelUrl;

        @Value("${kannel.username}")
        String kannelUserName;

        @Value("${kannel.password}")
        String kannelPassword;

        @Value("#{new Integer('${kannel.port}')}")
        int kannelPort;

        @Autowired
        private SMSMessageService smsMessageService;

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
    private SmsDeliver smsDeliver;


    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(8099));

    @Test
    public void sendMessageToKannelBackend(){


        wireMockRule.stubFor(get(urlPathMatching("/cgi-bin/sendsms"))
                .withQueryParam("smsc",equalTo("1"))
                .withQueryParam("username",equalTo("kannel"))
                .withQueryParam("password", equalTo("kannel"))
                .withQueryParam("to", equalTo("+23277775775"))
                .withQueryParam("text", equalTo("testMessage"))
                .willReturn(aResponse().withStatus(HttpStatus.OK.value())));

        smsDeliver.send("testMessage","+23277775775",1L,"testKey","1");

        verify(getRequestedFor(urlEqualTo("/cgi-bin/sendsms?smsc=1&username=kannel&password=kannel&to=%2B23277775775&text=testMessage")));
    }

}
