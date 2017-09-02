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
import org.springframework.validation.annotation.Validated;
import org.springframework.security.provisioning.InMemoryUserDetailsManager; 
import org.springframework.security.core.userdetails.User;
import java.util.ArrayList;
import org.springframework.security.core.GrantedAuthority;



@RestController
@RequestMapping("user")
public class SecurityController {

    private final InMemoryUserDetailsManager inMemoryUserDetailsManager;

    @Autowired
    public SecurityController(InMemoryUserDetailsManager inMemoryUserDetailsManager) {
       this.inMemoryUserDetailsManager = inMemoryUserDetailsManager;
    }

    @RequestMapping("exists/{username}")
    public boolean userExists(@PathVariable("username") String username ) {
        return inMemoryUserDetailsManager.userExists(username);
    }

    @RequestMapping("add/{username}/{password}")
    public String add(@PathVariable("username") String username, @PathVariable("password") String password) {
        inMemoryUserDetailsManager.createUser(new User(username, password, new ArrayList<GrantedAuthority>()));
        return "added";
    }
}
