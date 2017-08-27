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
package xyz.idtlabs.smsgateway.tenants.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table; 
import javax.validation.constraints.NotNull;

import xyz.idtlabs.smsgateway.sms.domain.AbstractPersistableCustom;

@Entity
@Table(name="m_tenants")
public class Tenant extends AbstractPersistableCustom<Long> {

	@Column(name = "tenant_id", nullable = false)
	private String tenantId;
	
	@Column(name = "api_key", nullable = false)
	private String apiKey ;
	
	@Column(name="organization", nullable = true) 
	@NotNull
	private String organization; 

	@Column(name="display_name", nullable=true)
	@NotNull
	private String displayName;
	
	protected Tenant() { }
	
	//this constructor is only here at the moment for maintaining the legacy code base. It would be depreciated in the final
	//API deployment
	public Tenant(final String tenantId, final String apiKey) {
		this.tenantId = tenantId ;
		this.apiKey = apiKey ;
		this.organization = null;
		this.displayName = null;
	} 

    //the new constructor that would be used in our API
	public Tenant(final String tenantId, final String api_key,
	              final String organization, final String displayName){
		this.tenantId = tenantId ;
		this.apiKey = apiKey ;
		this.organization = organization;
		this.displayName = displayName;
	}
		
	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}



	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}  


	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	} 

}