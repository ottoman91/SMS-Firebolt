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
import xyz.idtlabs.smsgateway.tenants.exception.TenantExists;
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
        String name = tenant.getName();
        boolean tenantAlreadyExists = tenantService.doesTenantAlreadyExist(name);
        if(tenantAlreadyExists == true){
            throw new TenantExists();
        } 
        else{
            Tenant createdTenant = this.tenantService.createTenant(tenant);
            return new ResponseEntity<>(createdTenant,HttpStatus.CREATED);
        }
    } 

        //-------------------Retrieve a Single Client --------------------------------------------------------
    
    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public ResponseEntity<Tenant> getClient(@PathVariable("id") long id) {
        System.out.println("Fetching Client with Id " + id);
        Tenant tenant = tenantService.findTenantById(id);
        if (tenant == null) {
            System.out.println("Tenant with id " + id + " not found");
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
    
    @RequestMapping(value = "/{id}",method = RequestMethod.DELETE)
     public ResponseEntity<Tenant> deleteClient(@PathVariable("id") long id) {
        System.out.println("Fetching & Deleting Client with id " + id);

        Tenant tenant = tenantService.findTenantById(id);
        if (tenant == null) {
            System.out.println("Unable to delete. Client " + id + " not found");
            return new ResponseEntity<Tenant>(HttpStatus.NOT_FOUND);
        }

        tenantService.deleteTenantById(id);
        return new ResponseEntity<Tenant>(HttpStatus.OK);
    }

//------------------- Update a Client's Details --------------------------------------------------------
    
    @RequestMapping(value = "/{id}",method = RequestMethod.PUT,consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<Tenant> updateClientName(@PathVariable("id") long id, @Validated @RequestBody final Tenant tenant) {
        System.out.println("Updating Client " + id);
        
        Tenant currentTenant = tenantService.findTenantById(id);
        
        if (currentTenant==null) {
            System.out.println("Client with id " + id + " not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }   
        String newName = tenant.getName(); 
        String newDisplayName = tenant.getDisplayName();         
        currentTenant.setName(newName); 
        currentTenant.setDisplayName(newDisplayName); 
        Tenant updatedTenant = tenantService.updateTenant(currentTenant);
        return new ResponseEntity<>(updatedTenant, HttpStatus.OK);   
        

          
    }  

    //------------------- Update a Client's Api Key --------------------------------------------------------
    
    @RequestMapping(value = "/{id}/apikey",method = RequestMethod.PUT,consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<Tenant> updateClientApiKey(@PathVariable("id") long id, @Validated @RequestBody final Tenant tenant) {
        System.out.println("Updating Client " + id);
        
        Tenant currentTenant = tenantService.findTenantById(id);
        
        if (currentTenant==null) {
            System.out.println("Client with id " + id + " not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }  
        String newApiKey = tenantService.generateApiKey();
        currentTenant.setApiKey(newApiKey);
        Tenant updatedTenant = tenantService.updateTenant(currentTenant);
        return new ResponseEntity<>(updatedTenant,HttpStatus.OK);
        
             
    }  

    //------------------- Block A Client from Using the API Key --------------------------------------------------------
    
    @RequestMapping(value = "/{id}/block",method = RequestMethod.PUT,consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<String> blockClient(@PathVariable("id") long id, @Validated @RequestBody final Tenant tenant) {
        System.out.println("Blocking Client " + id);
        
        Tenant currentTenant = tenantService.findTenantById(id);
        
        if (currentTenant==null) {
            System.out.println("Client with id " + id + " not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }  
        String clientBlockedStatus = tenantService.blockClient(id);
        return new ResponseEntity<>(clientBlockedStatus,HttpStatus.OK);
        
             
    }  

     //------------------- UnBlock A Client from Using the API Key --------------------------------------------------------
    
    @RequestMapping(value = "/{id}/unblock",method = RequestMethod.PUT,consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<String> unblockClient(@PathVariable("id") long id, @Validated @RequestBody final Tenant tenant) {
        System.out.println("Unblocking Client " + id);
        
        Tenant currentTenant = tenantService.findTenantById(id);
        
        if (currentTenant==null) {
            System.out.println("Client with id " + id + " not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }  
        String clientBlockedStatus = tenantService.unblockClient(id);
        return new ResponseEntity<>(clientBlockedStatus,HttpStatus.OK);
        
             
    } 
}