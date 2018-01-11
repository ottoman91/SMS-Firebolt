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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "m_batch_messages")
public class Batch extends AbstractPersistableCustom<Long> {

    @com.fasterxml.jackson.annotation.JsonIgnore
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "submitted_on_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date submittedOnDate;

    protected Batch() {

    }

    public Batch (final Long tenantId, final Date submittedOnDate){
        this.tenantId = tenantId;
        this.submittedOnDate = submittedOnDate;
    }

    public Long getTenantId(){
        return tenantId;
    }

    public Date getSubmittedOnDate(){
        return submittedOnDate;
    }

    public void setTenantId(Long tenantId){
        this.tenantId = tenantId;
    }

    public void setSubmittedOnDate(Date submittedOnDate){
        this.submittedOnDate = submittedOnDate;
    }
}
