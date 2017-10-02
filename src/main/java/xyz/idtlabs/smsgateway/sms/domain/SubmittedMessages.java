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
package xyz.idtlabs.smsgateway.sms.domain;

import java.util.Date;
import java.text.DateFormat; 





public class SubmittedMessages {

    private String id;  

    private boolean accepted;

    private String to;  


  

    

    public void setId(final String id){
        this.id = id;
    } 

    public String getId(){
        return id;
    } 

    public void setAccepted(){
        this.accepted = true;
    }
    
    public boolean getAccepted(){
        return accepted;
    }

    
    public void setTo(final String to) {
        this.to = to ;
    }
    
    public String getTo() {
        return to ;
    } 

  


   
}
