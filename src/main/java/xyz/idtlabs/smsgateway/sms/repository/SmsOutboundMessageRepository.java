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
package xyz.idtlabs.smsgateway.sms.repository;

import java.util.List;

import xyz.idtlabs.smsgateway.sms.domain.SMSMessage;
import xyz.idtlabs.smsgateway.sms.util.SmsMessageStatusType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param; 
import java.util.Date;
import org.springframework.data.jpa.repository.Query;


@Repository
public interface SmsOutboundMessageRepository extends JpaRepository<SMSMessage, Long>, JpaSpecificationExecutor<SMSMessage> {
	
    /** 
     * find {@link SmsMessageStatusType} objects by delivery status
     * 
     * @param deliveryStatus -- {@link SmsMessageStatusType} deliveryStatus
     * @param pageable -- Abstract interface for pagination information.
     * @return List of {@link SmsMessageStatusType} list
     **/
    Page<SMSMessage> findByDeliveryStatus(Integer deliveryStatus, Pageable pageable);
	
	/** 
	 * find {@link SmsMessageStatusType} object by externalId
	 * 
	 * @param externalId -- {@link SmsMessageStatusType} externalId
	 * @return {@link SmsMessageStatusType}
	 **/
    SMSMessage findByExternalId(String externalId);  
    
    SMSMessage findByTenantId(Long tenantId);
	
	/** 
	 * find {@link SmsMessageStatusType} objects with id in "idList" and mifosTenantIdentifier equal to "mifosTenantIdentifier"
	 * 
	 * @param idList -- {@link SmsMessageStatusType} id list
	 * @param mifosTenantIdentifier -- Mifos X tenant identifier e.g. demo
	 * @return List of {@link SmsMessageStatusType} objects
	 **/
	SMSMessage findByTenantIdAndId(@Param("tenantId") final Long tenantId, @Param("id") final Long id); 

    Page<SMSMessage> findAllByTenantId(Long tenantId,Pageable pageable);  

   @Query(value="SELECT * FROM m_outbound_messages s WHERE s.submitted_on_date >= :dateFrom AND s.submitted_on_date <= :dateTo AND s.tenant_id = :id ",nativeQuery=true)
   List<SMSMessage> findByDatesAndId(@Param("id") Long id, @Param("dateFrom") Date dateFrom,@Param("dateTo") Date dateTo);



}
