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

import xyz.idtlabs.smsgateway.sms.domain.Message;
import okhttp3.HttpUrl.Builder;
import okhttp3.HttpUrl;
import org.springframework.stereotype.Service; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource; 
import org.springframework.context.annotation.PropertySource;
import java.lang.Override; 
import java.util.Properties; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ClassLoader;
import java.lang.Thread;
import java.lang.Integer;




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
    public HttpUrl buildRequest(String message, String number){ 
        return createKannelUrl(message,number);
    }  

    //function that creates the kannel URL from the message object passed to it
    private HttpUrl createKannelUrl(String message, String number){    
       
        //String numbers = message.getTo();
        //String messageBody = message.getBody();

        
    //     HttpUrl kannelUrl = new HttpUrl.Builder()
    //         .scheme("https")
    //         .host(url)
    //         .port(port)
    //         .addPathSegment("cgi-bin")
    //         .addPathSegment("sendsms")
    //         .addQueryParameter("username", userName)
    //         .addQueryParameter("password", password)
    //         .addQueryParameter("to", number)
    //         .addQueryParameter("text", message)
    //         .build(); 
    //     logger.info(url);
            
    //     return kannelUrl;
    // } 

        HttpUrl kannelUrl = new HttpUrl.Builder()
            .scheme("http")
            .host(url)
            .port(port)
            .addPathSegment("cgi-bin")
            .addPathSegment("sendsms")
            .build(); 
        logger.info(url);
            
        return kannelUrl;
    }
  
}
