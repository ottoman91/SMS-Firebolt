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
package xyz.idtlabs.smsgateway.tenants.service;

import xyz.idtlabs.smsgateway.service.SecurityService;
import xyz.idtlabs.smsgateway.tenants.domain.Tenant;
import xyz.idtlabs.smsgateway.tenants.exception.TenantNotFoundException; 
import xyz.idtlabs.smsgateway.tenants.exception.TenantsNotFoundException;
import xyz.idtlabs.smsgateway.tenants.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service; 
import java.util.List; 
import org.springframework.data.domain.Page; 
import org.springframework.data.domain.PageRequest;



@Service
public class TenantsService {

	private final TenantRepository tenantRepository ;
	
	private final SecurityService securityService ;
	
	@Autowired
	public TenantsService(final TenantRepository tenantRepository,
			final SecurityService securityService) {
		this.tenantRepository = tenantRepository ;
		this.securityService = securityService ;
	}
	


	public Tenant createTenant(final Tenant tenant){
		tenant.setApiKey(this.securityService.generateApiKey());
		this.tenantRepository.save(tenant);
		return tenant;
	}
	
	public Tenant findTenantByNameAndApiKey(final String name, final String apiKey) {
		Tenant tenant = this.tenantRepository.findByNameAndApiKey(name, apiKey) ;
		if(tenant == null) {
			throw new TenantNotFoundException(name, apiKey) ;
		}
		return tenant ;
	} 

	public Tenant findTenantByApiKey(final String apiKey) {
		Tenant tenant = this.tenantRepository.findByApiKey(apiKey) ;
		if(tenant == null) {
			throw new TenantNotFoundException(apiKey, "") ;
		}
		return tenant ;
	}
	
	public Tenant findTenantById(final long id) {
		Tenant tenant = this.tenantRepository.findById(id) ;
		if(tenant == null) {
			throw new TenantNotFoundException(id, "") ;
		}
		return tenant ;
	}  

	public Tenant findTenantByName(final String name) {
		Tenant tenant = this.tenantRepository.findByName(name) ;
		if(tenant == null) {
			throw new TenantNotFoundException(name, "") ;
		}
		return tenant ;
	}  

	public Page<Tenant> findAllTenantsPaginated(int page, int size) {
        return tenantRepository.findAll(new PageRequest(page, size));
    }
	// public List<Tenant> findAllTenants(){
	// 	List<Tenant> tenants = this.tenantRepository.findAll();
	// 	if(tenants.isEmpty()){
	// 		throw new TenantsNotFoundException();
	// 	}
	// 	return tenants;
	// } 

	public void deleteTenantById(final long id) {
		Tenant tenant = this.tenantRepository.findById(id) ; 
		this.tenantRepository.delete(tenant);

	}  

	public void deleteTenantByName(final String name) {
		Tenant tenant = this.tenantRepository.findByName(name) ; 
		this.tenantRepository.delete(tenant);

	} 
	public Tenant updateTenant(final Tenant tenant) {
		this.tenantRepository.save(tenant);
		return tenant ;
	}  

	public boolean doesTenantAlreadyExist(final String name){
		Tenant tenant = this.tenantRepository.findByName(name);
		if(tenant == null){
			return false;
		}
		else{
			return true;
		}
	} 
	public String generateApiKey(){
        String newApiKey = this.securityService.generateApiKey();
        return newApiKey;
    } 

    public String blockClient(final long id){
    	Tenant tenant = this.tenantRepository.findById(id);
    	boolean blockedStatus = tenant.getBlocked();
    	if (blockedStatus == true){
    		return "Client is already blocked";
    	}
    	else{
    		tenant.setBlocked();
    		this.tenantRepository.save(tenant);
    		return "Client has been blocked now";
    	}
    }
}