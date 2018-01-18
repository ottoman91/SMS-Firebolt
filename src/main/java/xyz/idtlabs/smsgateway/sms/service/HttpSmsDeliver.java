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

import org.springframework.stereotype.Service; 
import org.springframework.beans.factory.annotation.Autowired;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Call;
import okhttp3.Response; 
import okhttp3.OkHttpClient;
import okhttp3.Callback;
import java.io.IOException; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.Override;
import xyz.idtlabs.smsgateway.sms.util.SmsMessageStatusType;


@Service
public class HttpSmsDeliver implements SmsDeliver {  

    private static final Logger logger = LoggerFactory.getLogger(HttpSmsDeliver.class);  

    @Autowired 
    HttpSMSBackend httpSMSBackend;

    @Autowired
    SMSMessageService smsMessageService;

    @Override
    public void send(String message, String number,Long batchId,String apiKey,String smsCentreNumber){
        HttpUrl sendMessageUrl = httpSMSBackend.buildRequest(message, number,smsCentreNumber);
        OkHttpClient client = new OkHttpClient(); 
        Request request = new Request.Builder().url(sendMessageUrl).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                smsMessageService.updateMessageDeliveryStatus(message.toString(),number.toString(),
                        batchId.longValue(),apiKey.toString(),SmsMessageStatusType.FAILED.getValue().intValue());
                logger.error("Could not deliver following SMS to Backend: "+ message.toString(),e);
            } 
            @Override public void onResponse(Call call, Response response) throws IOException {
                smsMessageService.updateMessageDeliveryStatus(message.toString(),number.toString(),
                        batchId.longValue(),apiKey.toString(),SmsMessageStatusType.DELIVERED.getValue().intValue());
                logger.info("Message Delivered To Backend" + message.toString());
            }
    });
    }
}
