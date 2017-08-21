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
import xyz.idtlabs.smsgateway.tenants.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	
	public String createTenant(final Tenant tenant) {
		tenant.setTenantAppKey(this.securityService.generateApiKey(tenant.getTenantId()));
		this.tenantRepository.save(tenant) ;
		return tenant.getTenantAppKey() ;
	}
	
	public Tenant findTenantByTenantIdAndTenantAppKey(final String tenantId, final String tenantAppKey) {
		Tenant tenant = this.tenantRepository.findByTenantIdAndTenantAppKey(tenantId, tenantAppKey) ;
		if(tenant == null) {
			throw new TenantNotFoundException(tenantId, tenantAppKey) ;
		}
		return tenant ;
	}
	
	public Tenant findTenantByTenantId(final String tenantId) {
		Tenant tenant = this.tenantRepository.findByTenantId(tenantId) ;
		if(tenant == null) {
			throw new TenantNotFoundException(tenantId, "") ;
		}
		return tenant ;
	}
}
