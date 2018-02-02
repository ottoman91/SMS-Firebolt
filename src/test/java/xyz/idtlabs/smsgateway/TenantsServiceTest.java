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
import static org.junit.Assert.assertNotNull;
import static org.springframework.boot.autoconfigure.jdbc.EmbeddedDatabaseConnection.H2;
import org.junit.After;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import xyz.idtlabs.smsgateway.configuration.SmsFireboltConfiguration;
import xyz.idtlabs.smsgateway.exception.PlatformApiDataValidationException;
import xyz.idtlabs.smsgateway.tenants.domain.Tenant;
import xyz.idtlabs.smsgateway.tenants.exception.TenantNotFoundException;
import xyz.idtlabs.smsgateway.tenants.repository.TenantRepository;
import xyz.idtlabs.smsgateway.tenants.service.TenantsService;
import org.springframework.security.test.context.support.WithMockUser;  


@RunWith(SpringRunner.class)
@DataJpaTest
@ContextConfiguration(classes=SmsFireboltConfiguration.class)
@AutoConfigureTestDatabase(connection = H2)
public class TenantsServiceTest {  
	
	@Autowired
	private TenantsService tenantService; 
	
	@Autowired
	private TenantRepository tenantRepository;

    @Rule
    public ExpectedException thrown = ExpectedException.none();



	@After
	public void tearDown() throws Exception {
		tenantRepository.deleteAll();
	}

	@Test
    @WithMockUser(roles = {"ADMIN"})
    public void createTenant_FailWhenNewClientNameOrDisplayNameNotCreated() {

		Tenant testClient = new Tenant("defaultApiKey","testClient", "testClientDisplay");
		Tenant savedTestClient = tenantService.createTenant(testClient);
		assertEquals("New Client Name Not created",testClient.getName(),savedTestClient.getName());
		assertEquals("New Client Display Name Not created",testClient.getDisplayName(),savedTestClient.getDisplayName());

    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void findAllTenantsPaginated_FailWhenAllTenantsNotRetrieved(){
	    Tenant testClient1 = new Tenant("defaultApiKey","testClient1","testClient1Display");
	    Tenant testClient2 = new Tenant("defaultApiKey","testClient2","testClient2Display");
	    Tenant savedTestClient1 = tenantService.createTenant(testClient1);
	    Tenant savedTestClient2 = tenantService.createTenant(testClient2);
        Page <Tenant> clientPage = tenantService.findAllTenantsPaginated(0,2);
        assertEquals("All tenants not retrieved via pagination",2,clientPage.getTotalElements());
    }

    @Test
    @WithMockUser(roles={"ADMIN"})
    public void createTenant_FailWhenNewClientNotRetrievedViaNameSearchFromDatabase() {

        Tenant testClient = new Tenant("defaultApiKey","testClient", "testClientDisplay");
        Tenant savedTestClient = tenantService.createTenant(testClient);
        Tenant retrievedTestClientFromDatabase = tenantRepository.findByName(savedTestClient.getName());
        assertEquals("Failed to retrieve new client from database",savedTestClient,
                retrievedTestClientFromDatabase);

    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void doesTenantAlreadyExist_FailWhenExistingClientNotDetected(){
	    Tenant testClient = new Tenant("defaultApiKey","testClient","testClientDisplay");
	    Tenant savedTestClient = tenantService.createTenant(testClient);
	    boolean tenantExists = tenantService.doesTenantAlreadyExist("testClient");
        assertEquals("Failed to check that client already exists",true,tenantExists);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void doesTenantAlreadyExist_FailWhenNonExistingClientDetected(){
        Tenant testClient = new Tenant("defaultApiKey","testClient","testClientDisplay");
        Tenant savedTestClient = tenantService.createTenant(testClient);
        boolean tenantDoesNotExist = tenantService.doesTenantAlreadyExist("notTestClient");
        assertEquals("Failed to check that client does not exist",false,tenantDoesNotExist);
    }


    @Test
	@WithMockUser(roles = {"ADMIN"})
	public void findTenantById_FailWhenExistingClientNotRetrievedViaClientIdSearch(){
		Tenant testClient = new Tenant("defaultApiKey","testClient","testClientDisplay");
		Tenant savedTestClient = tenantService.createTenant(testClient);
		long clientId = savedTestClient.getId();
		Tenant retrievedClient = tenantService.findTenantById(clientId);
		assertEquals("Failed to retrieve client via client id",testClient,retrievedClient);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void findTenantById_FailWhenNonExistingClientRetrievedViaId(){
        Tenant testClient = new Tenant("defaultApiKey","testClient","testClientDisplay");
        Tenant savedTestClient = tenantService.createTenant(testClient);
        thrown.expect(TenantNotFoundException.class);
        thrown.reportMissingExceptionWithMessage("Failed to throw exception at not finding client with client id ");
        Tenant notRetrievedClient = tenantService.findTenantById(990);
    }


    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void deleteTenantById_FailWhenClientNotDeleted(){
        Tenant testClient = new Tenant("defaultApiKey","testClient","testClientDisplay");
        Tenant savedTestClient = tenantService.createTenant(testClient);
        long clientId = savedTestClient.getId();
        tenantService.deleteTenantById(clientId);
        thrown.expect(TenantNotFoundException.class);
        thrown.reportMissingExceptionWithMessage("Failed to throw exception at finding deleted client record");
        Tenant notRetrievedClient = tenantService.findTenantById(clientId);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void updateTenant_FailWhenClientNameNotUpdated(){
        Tenant testClient = new Tenant("defaultApiKey","testClient","testClientDisplay");
        Tenant savedTestClient = tenantService.createTenant(testClient);
        savedTestClient.setName("newTestClient");
        long clientId = savedTestClient.getId();
        tenantService.updateTenant(savedTestClient);
        Tenant retrievedClient = tenantService.findTenantById(clientId);
        assertEquals("Client Name not Updated","newTestClient",retrievedClient.getName());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void updateTenant_FailWhenClientDisplayNameNotUpdated(){
        Tenant testClient = new Tenant("defaultApiKey","testClient","testClientDisplay");
        Tenant savedTestClient = tenantService.createTenant(testClient);
        savedTestClient.setDisplayName("newTestClientDisplay");
        long clientId = savedTestClient.getId();
        tenantService.updateTenant(savedTestClient);
        Tenant retrievedClient = tenantService.findTenantById(clientId);
        assertEquals("Client Display Name not Updated","newTestClientDisplay",
                retrievedClient.getDisplayName());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void updateTenant_FailWhenBothClientDisplayNameAndClientNameNotUpdated(){
        Tenant testClient = new Tenant("defaultApiKey","testClient","testClientDisplay");
        Tenant savedTestClient = tenantService.createTenant(testClient);
        savedTestClient.setName("newTestClient");
        savedTestClient.setDisplayName("newTestClientDisplay");
        long clientId = savedTestClient.getId();
        tenantService.updateTenant(savedTestClient);
        Tenant retrievedClient = tenantService.findTenantById(clientId);
        assertEquals("Client Name not Updated","newTestClient",retrievedClient.getName());
        assertEquals("Client Display Name not Updated","newTestClientDisplay",
                retrievedClient.getDisplayName());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void generateApiKey_FailWhenNewClientApiKeyNotGenerated(){
        Tenant testClient = new Tenant("defaultApiKey","testClient","testClientDisplay");
        Tenant savedTestClient = tenantService.createTenant(testClient);
        String newApiKey = tenantService.generateApiKey();
        assertNotNull(newApiKey);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void generateApiKey_FailWhenNewClientApiKeyNotUpdated(){
        Tenant testClient = new Tenant("defaultApiKey","testClient","testClientDisplay");
        Tenant savedTestClient = tenantService.createTenant(testClient);
        String newApiKey = tenantService.generateApiKey();
        savedTestClient.setApiKey(newApiKey);
        assertEquals("New Client Api Key Not updated",newApiKey,savedTestClient.getApiKey());

    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void blockClient_FailWhenUnblockedClientNotBlocked(){
        Tenant testClient = new Tenant("defaultApiKey","testClient","testClientDisplay");
        Tenant savedTestClient = tenantService.createTenant(testClient);
        long clientId = savedTestClient.getId();
        String clientBlockStatusMessage = tenantService.blockClient(clientId);
        assertEquals("Client Blocked Status Not Set to True",true,savedTestClient.getBlocked());
        assertEquals("Incorrect Message for Blocking non-blocked Client",
                "Client has been blocked now",clientBlockStatusMessage);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void blockClient_FailWhenBlockedClientBlockedAgain(){
        Tenant testClient = new Tenant("defaultApiKey","testClient","testClientDisplay");
        testClient.setBlocked(true);
        Tenant savedTestClient = tenantService.createTenant(testClient);
        long clientId = savedTestClient.getId();
        String clientBlockStatusMessage = tenantService.blockClient(clientId);
        assertEquals("Client Blocked Status Not Set to True",true,savedTestClient.getBlocked());
        assertEquals("Incorrect Message for Blocking blocked Client",
                "Client is already blocked",clientBlockStatusMessage);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void unblockClient_FailWhenBlockedClientNotUnblocked(){
        Tenant testClient = new Tenant("defaultApiKey","testClient","testClientDisplay");
        testClient.setBlocked(true);
        Tenant savedTestClient = tenantService.createTenant(testClient);
        long clientId = savedTestClient.getId();
        String clientBlockStatusMessage = tenantService.unblockClient(clientId);
        assertEquals("Client Unblocked Status Not Set to False",false,savedTestClient.getBlocked());
        assertEquals("Incorrect Message for Unblocking blocked Client",
                "Client has been unblocked now",clientBlockStatusMessage);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void unblockClient_FailWhenUnblockedClientUnblockedAgain(){
        Tenant testClient = new Tenant("defaultApiKey","testClient","testClientDisplay");
        Tenant savedTestClient = tenantService.createTenant(testClient);
        long clientId = savedTestClient.getId();
        String clientBlockStatusMessage = tenantService.unblockClient(clientId);
        assertEquals("Client Unblocked Status Not Set to False",false,savedTestClient.getBlocked());
        assertEquals("Incorrect Message for Unblocking already unblocked Client",
                "Client is already unblocked",clientBlockStatusMessage);
    }

    @Test
    @WithMockUser(roles = {"ADMIN","USER"})
    public void confirmClientCanSendMessage_FailWhenAbleToConfirmClientWithWrongApiKey(){
        Tenant testClient = new Tenant("defaultApiKey","testClient","testClientDisplay");
        Tenant savedTestClient = tenantService.createTenant(testClient);
        thrown.expect(PlatformApiDataValidationException.class);
        thrown.reportMissingExceptionWithMessage("Failed to throw Data Validation Exception when checking for client " +
                "with incorrect Api Key");
        tenantService.confirmClientCanSendSms("123456");
    }

    @Test
    @WithMockUser(roles = {"ADMIN","USER"})
    public void confirmClientCanSendMessage_FailWhenAbleToConfirmBlockedClient(){
        Tenant testClient = new Tenant("defaultApiKey","testClient","testClientDisplay");
        testClient.setBlocked(true);
        Tenant savedTestClient = tenantService.createTenant(testClient);
        String clientApiKey = savedTestClient.getApiKey();
        thrown.expect(PlatformApiDataValidationException.class);
        thrown.reportMissingExceptionWithMessage("Failed to throw Data Validation Exception when checking for blocked" +
                        "client");
        tenantService.confirmClientCanSendSms(clientApiKey);
    }
}
