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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.idtlabs.smsgateway.service.SecurityService;
import xyz.idtlabs.smsgateway.sms.domain.BatchMessages;
import xyz.idtlabs.smsgateway.sms.repository.BatchMessagesRepository;
import xyz.idtlabs.smsgateway.tenants.domain.Tenant;

import java.util.Date;

@Service
public class BatchMessagesService {

    private static final Logger logger = LoggerFactory.getLogger(BatchMessagesService.class);
    private final BatchMessagesRepository batchMessagesRepository;

    @Autowired
    public BatchMessagesService(final BatchMessagesRepository batchMessagesRepository){
        this.batchMessagesRepository = batchMessagesRepository;
    }

    public Long returnBatchId(final String currentDate){
        BatchMessages batchMessage = new BatchMessages(currentDate);
        this.batchMessagesRepository.save(batchMessage);
        return this.batchMessagesRepository.findBySubmittedOnDate(currentDate).getId();

    }


}
