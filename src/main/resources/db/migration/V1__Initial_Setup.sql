--
-- Licensed to the Apache Software Foundation (ASF) under one
-- or more contributor license agreements. See the NOTICE file
-- distributed with this work for additional information
-- regarding copyright ownership. The ASF licenses this file
-- to you under the Apache License, Version 2.0 (the
-- "License"); you may not use this file except in compliance
-- with the License. You may obtain a copy of the License at
--
-- http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing,
-- software distributed under the License is distributed on an
-- "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
-- KIND, either express or implied. See the License for the
-- specific language governing permissions and limitations
-- under the License.
--

CREATE TABLE m_tenants (
  id                      BIGINT(20) PRIMARY KEY AUTO_INCREMENT NOT NULL,
  api_key                 VARCHAR(100)                    UNIQUE NOT NULL,
  name                    VARCHAR(500)                   UNIQUE NOT NULL, 
  display_name      VARCHAR(500)                    UNIQUE NOT NULL,
  blocked           TINYINT(1)                      NULL DEFAULT 0
  ); 

CREATE INDEX by_api_key on m_tenants (`api_key`);

CREATE TABLE m_batch_messages (
  id                      BIGINT(20) PRIMARY KEY AUTO_INCREMENT NOT NULL,
  submitted_on_date       TIMESTAMP                                    NULL DEFAULT NULL
);

CREATE TABLE m_sms_bridge (
  id                      BIGINT(20) PRIMARY KEY AUTO_INCREMENT NOT NULL,
  tenant_id               BIGINT(20)                                      NOT NULL,
  tenant_phone_no         VARCHAR(255)                                    NOT NULL,
  provider_name           VARCHAR(100)                                    NOT NULL,
  country_code        VARCHAR(5)                    NOT NULL,
  provider_key          VARCHAR(100)                    NOT NULL,     
  description             VARCHAR(500)                                    NOT NULL,
  created_on              TIMESTAMP                                       NULL DEFAULT NULL,
  last_modified_on        TIMESTAMP                                       NULL DEFAULT NULL,
  CONSTRAINT `m_sms_bridge_1` FOREIGN KEY (`tenant_id`) REFERENCES `m_tenants` (`id`)
);

CREATE TABLE m_outbound_messages (
  id                      BIGINT(20) PRIMARY KEY AUTO_INCREMENT NOT NULL,
  tenant_id               BIGINT(20)                                     NOT NULL,
  batch_id                BIGINT(20)                                     NOT NULL,
  sms_centre_number       VARCHAR(100)                                   NULL DEFAULT NULL,
  external_id             VARCHAR(100)                                    NULL DEFAULT NULL,
  internal_id             VARCHAR(100)                                    NULL DEFAULT NULL,
  delivery_error_message  VARCHAR(500)                                    NULL DEFAULT NULL,
  source_address        VARCHAR(100)                                    NULL DEFAULT NULL,
  sms_bridge_id           BIGINT(20)                                      NULL DEFAULT NULL,
  mobile_number           VARCHAR(255)                                    NOT NULL,
  submitted_on_date       TIMESTAMP                                       NOT NULL,
  delivered_on_date       TIMESTAMP                                       NULL DEFAULT NULL,
  delivery_status       TINYINT(3) unsigned                                        NULL DEFAULT 100,
  message             VARCHAR(4096)                                   NOT NULL,
  CONSTRAINT `m_outbound_messages_1` FOREIGN KEY (`sms_bridge_id`) REFERENCES `m_sms_bridge` (`id`),
  CONSTRAINT `m_outbound_messages_2` FOREIGN KEY (`tenant_id`) REFERENCES `m_tenants` (`id`),
  CONSTRAINT `m_outbound_messages_3` FOREIGN KEY (`batch_id`) REFERENCES `m_batch_messages` (`id`)
);

CREATE TABLE m_sms_bridge_configuration (
  id                      BIGINT(20) PRIMARY KEY AUTO_INCREMENT NOT NULL,
  sms_bridge_id           BIGINT(20)                                     NOT NULL,
  config_name             VARCHAR(100)                                    NULL DEFAULT NULL,
  config_value             VARCHAR(100)                                    NOT NULL,
  CONSTRAINT `m_provider_configuration_1` FOREIGN KEY (`sms_bridge_id`) REFERENCES `m_sms_bridge` (`id`)
);






