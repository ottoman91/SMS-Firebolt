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
package xyz.idtlabs.smsgateway.sms.service;

import okhttp3.HttpUrl;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import java.lang.Override; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@PropertySource("classpath:config.properties")
public class KannelBackend implements HttpSMSBackend {   

    private static final Logger logger = LoggerFactory.getLogger(KannelBackend.class);  
    private final String url;
    private final String userName;
    private final String password;
    private final int port;



    @Autowired
    public KannelBackend(
        @Value("${kannel.url}")
        String url,
        @Value("#{new Integer('${kannel.port}')}")
        int kannelPort,
        @Value("${kannel.username}")
        String userName,
        @Value("${kannel.password}")
        String password){
        this.url = url;
        this.userName = userName;
        this.password = password;
        this.port = kannelPort;
        
    }

    @Override
    public HttpUrl buildRequest(String message, String number,String smsCentreNumber){
        return createKannelUrl(message,number,smsCentreNumber);
    }  

    //function that creates the kannel URL from the message object passed to it
    private HttpUrl createKannelUrl(String message, String number,String smsCentreNumber){
       
//        

        
         HttpUrl kannelUrl = new HttpUrl.Builder()
             .scheme("http")
             .host(url)
             .port(port)
             .addPathSegment("cgi-bin")
             .addPathSegment("sendsms").addQueryParameter("smsc",smsCentreNumber)
             .addQueryParameter("username", userName)
             .addQueryParameter("password", password)
             .addQueryParameter("to", number)
             .addQueryParameter("text", message)
             .build(); 
         logger.info(url);
            
         return kannelUrl;
    // } 

    	//only for testing purposes
//        HttpUrl kannelUrl = new HttpUrl.Builder()
//            .scheme("http")
//            .host(url)
//            .port(port)
//            .addPathSegment("cgi-bin")
//            .addPathSegment("sendsms")
//            .build(); 
//        logger.info(url);
//            
//        return kannelUrl;
    }
  
}
