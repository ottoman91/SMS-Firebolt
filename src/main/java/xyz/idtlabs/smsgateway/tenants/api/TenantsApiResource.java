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
package xyz.idtlabs.smsgateway.tenants.api;

import xyz.idtlabs.smsgateway.tenants.domain.Tenant;
import xyz.idtlabs.smsgateway.tenants.service.TenantsService;
import xyz.idtlabs.smsgateway.tenants.exception.TenantsNotFoundException; 
import xyz.idtlabs.smsgateway.tenants.exception.TenantAlreadyExists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController; 
import org.springframework.web.bind.annotation.PathVariable;  
import org.springframework.web.bind.annotation.RequestParam;  
import org.springframework.validation.annotation.Validated;
import org.springframework.data.domain.Page;


import java.util.List;


@RestController
@RequestMapping("/clients")
public class TenantsApiResource {

    private final TenantsService tenantService ;
    
    @Autowired
    public TenantsApiResource(final TenantsService tenantService) {
        this.tenantService = tenantService ;
    }
            //-------------------Create a new Client--------------------------------------------------------

    @RequestMapping(method = RequestMethod.POST, consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<Tenant> createClient( @Validated @RequestBody final Tenant tenant) {
        String organizationName = tenant.getOrganization();
        boolean tenantAlreadyExists = tenantService.doesTenantAlreadyExist(organizationName);
        if(tenantAlreadyExists == true){
            throw new TenantAlreadyExists();
        } 
        else{
            Tenant createdTenant = this.tenantService.createTenant(tenant);
            return new ResponseEntity<>(createdTenant,HttpStatus.CREATED);
        }
    } 

        //-------------------Retrieve a Single Client --------------------------------------------------------
    
    @RequestMapping(value = "/{organization}",method = RequestMethod.GET)
    public ResponseEntity<Tenant> getClient(@PathVariable("organization") String organization) {
        System.out.println("Fetching Client with Name " + organization);
        Tenant tenant = tenantService.findTenantByTenantName(organization);
        if (tenant == null) {
            System.out.println("Tenant with name " + organization + " not found");
            return new ResponseEntity<Tenant>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Tenant>(tenant, HttpStatus.OK);
    } 

        //-------------------Retrieve All Clients--------------------------------------------------------
    
    @RequestMapping(params = {"page", "size"},method = RequestMethod.GET) 
    public Page<Tenant> listAllClientsPaginated(
      @RequestParam("page") int page, @RequestParam("size") int size) {
 
        Page<Tenant> resultPage = tenantService.findAllTenantsPaginated(page, size);
        if (page > resultPage.getTotalPages()) {
            throw new TenantsNotFoundException();
        }
 
        return resultPage;
    }
  

        //------------------- Delete a Client --------------------------------------------------------
    
    @RequestMapping(value = "/{organization}",method = RequestMethod.DELETE)
     public ResponseEntity<Tenant> deleteClient(@PathVariable("organization") String organization) {
        System.out.println("Fetching & Deleting Client with name " + organization);

        Tenant tenant = tenantService.findTenantByTenantName(organization);
        if (tenant == null) {
            System.out.println("Unable to delete. Client " + organization + " not found");
            return new ResponseEntity<Tenant>(HttpStatus.NOT_FOUND);
        }

        tenantService.deleteTenantByTenantName(organization);
        return new ResponseEntity<Tenant>(HttpStatus.OK);
    }

//------------------- Update a Client's Details --------------------------------------------------------
    
    @RequestMapping(value = "/change/{organization}",method = RequestMethod.POST,consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<Tenant> updateClientName(@PathVariable("organization") String organization, @Validated @RequestBody final Tenant tenant) {
        System.out.println("Updating Client " + organization);
        
        Tenant currentTenant = tenantService.findTenantByTenantName(organization);
        
        if (currentTenant==null) {
            System.out.println("Client with name " + organization + " not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }  
        String newOrganizationName = tenant.getOrganization(); 
        String newOrganizationDisplayName = tenant.getDisplayName();
        currentTenant.setOrganization(newOrganizationName); 
        currentTenant.setDisplayName(newOrganizationDisplayName);
        
        Tenant updatedTenant = tenantService.updateTenant(currentTenant);
        return new ResponseEntity<>(updatedTenant, HttpStatus.OK);
    } 
}