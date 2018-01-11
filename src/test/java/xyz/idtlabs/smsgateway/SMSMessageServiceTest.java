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

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import xyz.idtlabs.smsgateway.configuration.SmsFireboltConfiguration;
import xyz.idtlabs.smsgateway.exception.PlatformApiDataValidationException;
import xyz.idtlabs.smsgateway.exception.PlatformApiInvalidParameterException;
import xyz.idtlabs.smsgateway.sms.domain.SMSMessage;
import xyz.idtlabs.smsgateway.sms.repository.SmsOutboundMessageRepository;
import xyz.idtlabs.smsgateway.sms.service.BatchMessagesService;
import xyz.idtlabs.smsgateway.sms.service.SMSMessageService;
import xyz.idtlabs.smsgateway.tenants.domain.Tenant;
import xyz.idtlabs.smsgateway.tenants.repository.TenantRepository;
import xyz.idtlabs.smsgateway.tenants.service.TenantsService;

import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.springframework.boot.autoconfigure.jdbc.EmbeddedDatabaseConnection.H2;

@RunWith(SpringRunner.class)
@DataJpaTest
@ContextConfiguration(classes=SmsFireboltConfiguration.class)
@AutoConfigureTestDatabase(connection = H2)

public class SMSMessageServiceTest {


    @Autowired
    private SMSMessageService smsMessageService;
    @Autowired
    private TenantsService tenantService;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private SmsOutboundMessageRepository smsOutboundMessageRepository;

    @Autowired
    private BatchMessagesService batchMessagesService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @After
    public void tearDown() throws Exception {
        tenantRepository.deleteAll();
        smsOutboundMessageRepository.deleteAll();
    }

    @Test
    public void validateMessageAndDestination_FailWhenErrorNotThrownForEmptyMessage(){

        thrown.expect(PlatformApiInvalidParameterException.class);
        thrown.reportMissingExceptionWithMessage("Failed to throw exception when empty message passed for sending");
        smsMessageService.validateMessageAndDestination("123456789","");
    }

    @Test
    public void validateMessageAndDestination_FailWhenErrorNotThrownForMessageWith161Character(){

        char[] chars = new char[161];
        Arrays.fill(chars,'a');
        String message = new String(chars);

        thrown.expect(PlatformApiInvalidParameterException.class);
        thrown.reportMissingExceptionWithMessage("Failed to throw exception when message length of 161 " +
                "characters is passed for sending");
        smsMessageService.validateMessageAndDestination("123456789",message);
    }

    @Test
    public void validateMessageAndDestination_FailWhenErrorNotThrownForMessageWith170Character(){

        char[] chars = new char[170];
        Arrays.fill(chars,'a');
        String message = new String(chars);

        thrown.expect(PlatformApiInvalidParameterException.class);
        thrown.reportMissingExceptionWithMessage("Failed to throw exception when message length of 170 " +
                "characters is passed for sending");
        smsMessageService.validateMessageAndDestination("123456789",message);
    }

    @Test
    public void validateMessageAndDestination_FailWhenErrorNotThrownForEmptyDestinationNumber(){

        char[] chars = new char[160];
        Arrays.fill(chars,'a');
        String message = new String(chars);

        thrown.expect(PlatformApiInvalidParameterException.class);
        thrown.reportMissingExceptionWithMessage("Failed to throw exception when message is sent to empty number " +
                "characters is passed for sending");
        smsMessageService.validateMessageAndDestination("",message);
    }

    @Test
    public void validateMessageAndDestination_FailWhenErrorNotThrownForDuplicateNumbers(){

        char[] chars = new char[160];
        Arrays.fill(chars,'a');
        String message = new String(chars);
        String numbers = "+23277775775,+23277775775";

        thrown.expect(PlatformApiDataValidationException.class);
        thrown.reportMissingExceptionWithMessage("Failed to throw exception when duplicate numbers are entered");
        smsMessageService.validateMessageAndDestination(numbers,message);
    }

    @Test
    public void validateMessageAndDestination_FailWhenExceptionNotThrownForInvalidNumber(){

        char[] chars = new char[160];
        Arrays.fill(chars,'a');
        String message = new String(chars);
        String numbers = "+2327777577578";

        thrown.expect(PlatformApiDataValidationException.class);
        thrown.reportMissingExceptionWithMessage("Failed to throw exception when invalid number is entered");
        smsMessageService.validateMessageAndDestination(numbers,message);
    }

    @Test
    public void validateMessageAndDestination_FailWhenExceptionNotThrownForInvalidMNO(){

        char[] chars = new char[160];
        Arrays.fill(chars,'a');
        String message = new String(chars);
        String numbers = "+16179829491";

        thrown.expect(PlatformApiDataValidationException.class);
        thrown.reportMissingExceptionWithMessage("Failed to throw exception when number with unsupported MNO is added");
        smsMessageService.validateMessageAndDestination(numbers,message);
    }

    @Test
    public void validateMessageAndDestination_FailWhenExceptionThrownWhenAllValuesAreProperlyPassed(){

        char[] chars = new char[160];
        Arrays.fill(chars,'a');
        String message = new String(chars);
        String numbers = "+23277775775,+23277776776";
        smsMessageService.validateMessageAndDestination(numbers,message);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void findMessagesByTenantId_FailWhenMessagesFromOneClientNotFoundFromDatabase(){

        Tenant testClient = new Tenant("defaultApiKey","testClient","testClientDisplay");
        Tenant savedTestClient = tenantService.createTenant(testClient);
        char[] chars = new char[160];
        Arrays.fill(chars,'a');
        String message1 = new String(chars);

        char[] chars2 = new char[160];
        Arrays.fill(chars2,'b');
        String message2 = new String(chars2);
        String number = "+23277775775";
        String apiKey = tenantRepository.findByName("testClient").getApiKey();
        java.util.Date dt = new java.util.Date();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(dt);
        Long batchId = batchMessagesService.returnBatchId(currentTime);
        smsMessageService.saveSMS(savedTestClient.getApiKey(),number,message1,batchId);
        smsMessageService.saveSMS(savedTestClient.getApiKey(),number,message2,batchId);
        Page<SMSMessage> smsMessage = smsMessageService.findMessagesByTenantId(savedTestClient.getId(),0,2);
        assertEquals("Messages sent by client not retrieved",2,smsMessage.getTotalElements());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void findMessagesByTenantIdAndId_FailWhenSingleMessageFromClientNotFoundFromDatabase(){

        Tenant testClient = new Tenant("defaultApiKey","testClient","testClientDisplay");
        Tenant savedTestClient = tenantService.createTenant(testClient);
        char[] chars = new char[160];
        Arrays.fill(chars,'a');
        String message1 = new String(chars);
        String number = "+23277775775";
        java.util.Date dt = new java.util.Date();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(dt);
        Long batchId = batchMessagesService.returnBatchId(currentTime);
        smsMessageService.saveSMS(savedTestClient.getApiKey(),number,message1,batchId);
        long messageId = smsOutboundMessageRepository.findByTenantId(savedTestClient.getId()).getId();
        SMSMessage smsMessage = smsMessageService.findMessageByTenantIdAndId(savedTestClient.getId(),messageId);
        assertEquals("Unable to retrieve single message sent by client ",message1,smsMessage.getMessage());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void showTotalMessagesSentBetweenDatesByTenant_FailWhenMessagesSentBetweenDatesNotRetrievedFromDatabase(){

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new Date();
        Date startingDate = new Date();
        String date = "2011-01-01";
        try {

            startingDate = format.parse(date);
        } catch (java.text.ParseException e) {
        }


        String currentStringDate = currentDate.toString();
        try{
            currentDate = format.parse(currentStringDate);
        }catch(java.text.ParseException e){

        }

        Tenant testClient = new Tenant("defaultApiKey","testClient","testClientDisplay");
        Tenant savedTestClient = tenantService.createTenant(testClient);
        char[] chars = new char[160];
        Arrays.fill(chars,'a');
        String message = new String(chars);

        char[] chars2 = new char[160];
        Arrays.fill(chars2,'b');
        String message1 = new String(chars2);

        String number = "+23277775775";
        java.util.Date dt = new java.util.Date();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(dt);
        Long batchId = batchMessagesService.returnBatchId(currentTime);
        smsMessageService.saveSMS(savedTestClient.getApiKey(),number,message,batchId);
        smsMessageService.saveSMS(savedTestClient.getApiKey(),number,message1,batchId);


        int numberOfMessagesSent = smsMessageService.showTotalMessagesSentBetweenDatesByTenant(savedTestClient.getId(),
                startingDate,currentDate);

        assertEquals("Messages between specific dates were not recovered",2,numberOfMessagesSent);
    }


}
