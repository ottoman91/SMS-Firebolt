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
import org.springframework.stereotype.Service; 
import org.springframework.beans.factory.annotation.Autowired;
import xyz.idtlabs.smsgateway.sms.domain.Message;
import xyz.idtlabs.smsgateway.sms.service.SMSMessageService; 
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Request.Builder; 
import okhttp3.Call;
import okhttp3.Response; 
import okhttp3.OkHttpClient;
import okhttp3.Callback;
import java.io.IOException; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.Override;

@Service
public class HttpSmsDeliver implements SmsDeliver {  

    private static final Logger logger = LoggerFactory.getLogger(HttpSmsDeliver.class);  

    @Autowired 
    HttpSMSBackend httpSMSBackend;  

    @Override
    public void send(String message, String number){  
        HttpUrl sendMessageUrl = httpSMSBackend.buildRequest(message, number);
        OkHttpClient client = new OkHttpClient(); 
        Request request = new Request.Builder().url(sendMessageUrl).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
            logger.error("Could not Deliver SMS to Backend",e); 
            } 
            @Override public void onResponse(Call call, Response response) throws IOException { 
                logger.info("Message Delivered To Backend");
            }
    });   
    }
}
