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
import xyz.idtlabs.smsgateway.exception.PlatformApiDataValidationException;
import xyz.idtlabs.smsgateway.helpers.ApiParameterError;
import xyz.idtlabs.smsgateway.tenants.exception.TenantNotFoundException; 
import xyz.idtlabs.smsgateway.tenants.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service; 
import java.util.List; 
import org.springframework.data.domain.Page; 
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.ArrayList;



@Service
public class TenantsService {

	private final TenantRepository tenantRepository ;
	
	private final SecurityService securityService ;

    private String defaultUserMessage;

    private String developerMessage;

    private String errorCode;  
	
	@Autowired
	public TenantsService(final TenantRepository tenantRepository,
			final SecurityService securityService) {
		this.tenantRepository = tenantRepository ;
		this.securityService = securityService ;
	}
	

    @PreAuthorize("hasRole('ADMIN')")
	public Tenant createTenant(final Tenant tenant){
		tenant.setApiKey(this.securityService.generateApiKey());
		this.tenantRepository.save(tenant);
		return tenant;
	}
	
//	public Tenant findTenantByNameAndApiKey(final String name, final String apiKey) {
//		Tenant tenant = this.tenantRepository.findByNameAndApiKey(name, apiKey) ;
//		if(tenant == null) {
//			throw new TenantNotFoundException(name, apiKey) ;
//		}
//		return tenant ;
//	}

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

//	public Tenant findTenantByName(final String name) {
//		Tenant tenant = this.tenantRepository.findByName(name) ;
//		if(tenant == null) {
//			throw new TenantNotFoundException(name, "") ;
//		}
//		return tenant ;
//	}

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

    @PreAuthorize("hasRole('ADMIN')")
	public void deleteTenantById(final long id) {
		Tenant tenant = this.tenantRepository.findById(id) ; 
		this.tenantRepository.delete(tenant);

	}  

//    @PreAuthorize("hasRole('ADMIN')")
//	public void deleteTenantByName(final String name) {
//		Tenant tenant = this.tenantRepository.findByName(name) ;
//		this.tenantRepository.delete(tenant);
//
//	}

    @PreAuthorize("hasRole('ADMIN')")
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

    @PreAuthorize("hasRole('ADMIN')")
	public String generateApiKey(){
        String newApiKey = this.securityService.generateApiKey();
        return newApiKey;
    } 

    @PreAuthorize("hasRole('ADMIN')")
    public String blockClient(final long id){
    	Tenant tenant = this.tenantRepository.findById(id);
    	boolean blockedStatus = tenant.getBlocked();
    	boolean newBlockedStatus = true;
    	if (blockedStatus){
    		return "Client is already blocked";
    	}
    	else{
    		tenant.setBlocked(newBlockedStatus);
    		this.tenantRepository.save(tenant);
    		return "Client has been blocked now";
    	}
    }  

    @PreAuthorize("hasRole('ADMIN')")
    public String unblockClient(final long id){
    	Tenant tenant = this.tenantRepository.findById(id);
    	boolean blockedStatus = tenant.getBlocked();
    	boolean newBlockedStatus = false;
    	if (!blockedStatus){
    		return "Client is already unblocked";
    	}
    	else{
    		tenant.setBlocked(newBlockedStatus);
    		this.tenantRepository.save(tenant);
    		return "Client has been unblocked now";
    	}
    }  

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public void confirmClientCanSendSms(final String apiKey){
        List<ApiParameterError> error = new ArrayList<>();
        Tenant tenant = this.tenantRepository.findByApiKey(apiKey);
        if(tenant == null){
            defaultUserMessage = "Invalid or missing API Key";
            developerMessage = "The API key is either incorrect or has not been included in the API call.";
            errorCode = "invalid_key";
            ApiParameterError apiParameterError = ApiParameterError.parameterError(errorCode,
                defaultUserMessage,"apiKey",developerMessage);
            apiParameterError.setValue(apiKey); 
            error.add(apiParameterError);
            throw new PlatformApiDataValidationException(error);
        } 
        else{ 
            boolean blockedStatus = tenant.getBlocked();
            if(blockedStatus == true){
                defaultUserMessage = "Account is not active";
                developerMessage = "The account is not active.";
                errorCode = "account_inactive";
                ApiParameterError apiParameterError = ApiParameterError.parameterError(errorCode,
                defaultUserMessage,"apiKey",developerMessage);
                apiParameterError.setValue(apiKey); 
                error.add(apiParameterError);
                throw new PlatformApiDataValidationException(error);
            }

        }
    }


}