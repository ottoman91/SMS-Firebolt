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


	@Column(name = "api_key", nullable = false)
	private String apiKey ;
	
	@Column(name="name", nullable = true) 
	@NotNull
	private String name; 

	@Column(name="display_name", nullable=true)
	@NotNull
	private String displayName; 

	@Column(name="blocked",nullable = true)
	private boolean blocked; 
	
	protected Tenant() { }
	
	

    //the new constructor that would be used in our API
	public Tenant(final String apiKey,
	              final String name, final String displayName){
		this.apiKey = apiKey ;
		this.name = name;
		this.displayName = displayName;
		this.blocked = false;
	}
		
	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}



	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}  


	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}  

	public boolean getBlocked(){
		return blocked;
	} 
	public boolean setBlocked(boolean blocked){
		this.blocked = blocked; 
		return blocked;
	} 



}