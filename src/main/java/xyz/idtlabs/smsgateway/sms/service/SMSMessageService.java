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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import xyz.idtlabs.smsgateway.service.SecurityService;
import xyz.idtlabs.smsgateway.exception.PlatformApiDataValidationException; 
import xyz.idtlabs.smsgateway.exception.PlatformApiInvalidParameterException; 
import xyz.idtlabs.smsgateway.helpers.ApiParameterError;
import xyz.idtlabs.smsgateway.sms.data.DeliveryStatusData;
import xyz.idtlabs.smsgateway.sms.domain.SMSMessage;
import xyz.idtlabs.smsgateway.sms.providers.SMSProviderFactory;
import xyz.idtlabs.smsgateway.sms.repository.SmsOutboundMessageRepository;
import xyz.idtlabs.smsgateway.sms.util.SmsMessageStatusType; 
import xyz.idtlabs.smsgateway.sms.exception.SmsMessagesNotFoundException;
import xyz.idtlabs.smsgateway.sms.exception.SmsMessageNotFoundException;
import xyz.idtlabs.smsgateway.sms.exception.DuplicateDestinationAddressException;
import xyz.idtlabs.smsgateway.sms.exception.MessageBodyIsEmptyException;
import xyz.idtlabs.smsgateway.sms.exception.MessageBodyOverLimit;
import xyz.idtlabs.smsgateway.sms.exception.DestinationIsEmptyException; 
import xyz.idtlabs.smsgateway.sms.exception.DestinationNumberFormatError;
import xyz.idtlabs.smsgateway.tenants.domain.Tenant;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource; 
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service; 
import java.util.List; 
import java.util.Date;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections; 
import java.util.regex.Pattern;
import java.util.regex.Matcher; 
import java.io.IOException;
import java.io.StringWriter;







@Service
public class SMSMessageService {

	 private static final Logger logger = LoggerFactory.getLogger(SMSMessageService.class);
	 
	private final SmsOutboundMessageRepository smsOutboundMessageRepository ;
	
	private final SMSProviderFactory smsProviderFactory ;
	
	private final JdbcTemplate jdbcTemplate ;
	
	private ExecutorService executorService ;
	
	private ScheduledExecutorService scheduledExecutorService ;
	
	private final SecurityService securityService ;  

	private String defaultUserMessage;

	private String developerMessage;

	private String errorCode;    

	@Value("classpath:mno.properties")
    private Resource validMobileCodes; 




	
	@Autowired
	public SMSMessageService(final SmsOutboundMessageRepository smsOutboundMessageRepository,
			final SMSProviderFactory smsProviderFactory,
			final DataSource dataSource,
			final SecurityService securityService) {
		this.smsOutboundMessageRepository = smsOutboundMessageRepository ;
		this.smsProviderFactory = smsProviderFactory ;
		this.jdbcTemplate = new JdbcTemplate(dataSource) ;
		this.securityService = securityService ;
	}
	
	@PostConstruct
	public void init() {
		logger.debug("Intializing SMSMessage Service.....");
		executorService = Executors.newSingleThreadExecutor();
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor() ;
		scheduledExecutorService.schedule(new BootupPendingMessagesTask(this.smsOutboundMessageRepository, this.smsProviderFactory) , 1, TimeUnit.MINUTES) ;
		//When do I have to shutdown  scheduledExecutorService ? :-( as it is no use after triggering BootupPendingMessagesTask
		//Shutdown scheduledExecutorService on application close event
	}
	
	public void sendShortMessage(final String tenantId, final String tenantAppKey, final Collection<SMSMessage> messages) {
		logger.debug("Request Received to send messages.....");
		Tenant tenant = this.securityService.authenticate(tenantAppKey) ;
		for(SMSMessage message: messages) {
			message.setTenant(tenant.getId());
		}
		this.smsOutboundMessageRepository.save(messages) ;
		this.executorService.execute(new MessageTask(tenant, this.smsOutboundMessageRepository, this.smsProviderFactory, messages));
	}
	
	public Collection<DeliveryStatusData> getDeliveryStatus(final String tenantId, final String tenantAppKey, final Collection<Long> internalIds) {
		Tenant tenant = this.securityService.authenticate(tenantAppKey) ;
		DeliveryStatusDataRowMapper mapper = new DeliveryStatusDataRowMapper() ;
		String internaIdString = internalIds.toString() ;
		internaIdString = internaIdString.replace("[", "(") ;
		internaIdString = internaIdString.replace("]", ")") ;
		String query = mapper.schema() + " where m.tenant_id=?"+" and m.internal_id in " +internaIdString;
		Collection<DeliveryStatusData> datas = this.jdbcTemplate.query(query, mapper, new Object[] {tenant.getId()}) ;
		return datas ;
	}
	
	class DeliveryStatusDataRowMapper implements RowMapper<DeliveryStatusData> {

		private final StringBuilder buff = new StringBuilder() ;
		
		public DeliveryStatusDataRowMapper() {
			buff.append("select internal_id, external_id, delivered_on_date, delivery_status, delivery_error_message from m_outbound_messages m") ;
		}
		
		public String schema() {
			return buff.toString() ;
		}
		
		@Override
		public DeliveryStatusData mapRow(ResultSet rs, int rowNum) throws SQLException { 
			String internalId = rs.getString("internal_id") ;
			String externalId = rs.getString("external_id") ;
			Date deliveredOnDate = rs.getDate("delivered_on_date") ;
			Integer deliveryStatus = rs.getInt("delivery_status") ;
			String errorMessage = rs.getString("delivery_error_message") ;
			DeliveryStatusData data = new DeliveryStatusData(internalId, externalId, deliveredOnDate, deliveryStatus, errorMessage) ;
			return data;
		}
	}
	
	class MessageTask implements Runnable {

		final Collection<SMSMessage> messages ;
		final SmsOutboundMessageRepository smsOutboundMessageRepository ;
		final SMSProviderFactory smsProviderFactory ;
		final Tenant tenant ;
		
		public MessageTask(final Tenant tenant, final SmsOutboundMessageRepository smsOutboundMessageRepository, 
				final SMSProviderFactory smsProviderFactory,
				final Collection<SMSMessage> messages) {
			this.tenant = tenant ;
			this.messages = messages ;
			this.smsOutboundMessageRepository = smsOutboundMessageRepository ;
			this.smsProviderFactory = smsProviderFactory ;
		}
		
		@Override
		public void run() {
			this.smsProviderFactory.sendShortMessage(messages);
			this.smsOutboundMessageRepository.save(messages) ;
		}
	}
	
	class BootupPendingMessagesTask implements Callable<Integer> {

		final SmsOutboundMessageRepository smsOutboundMessageRepository ;
		final SMSProviderFactory smsProviderFactory ;
		public BootupPendingMessagesTask(final SmsOutboundMessageRepository smsOutboundMessageRepository, 
				final SMSProviderFactory smsProviderFactory) {
			this.smsOutboundMessageRepository = smsOutboundMessageRepository ;
			this.smsProviderFactory = smsProviderFactory ;
		}

		@Override
		public Integer call() throws Exception {
			logger.info("Sending Pending Messages on bootup.....");
			Integer page = 0;
			Integer initialSize = 200;
			Integer totalPageSize = 0;
			do {
				PageRequest pageRequest = new PageRequest(page, initialSize);
				Page<SMSMessage> messages = this.smsOutboundMessageRepository.findByDeliveryStatus(SmsMessageStatusType.PENDING.getValue(), pageRequest) ;
				page++;
				totalPageSize = messages.getTotalPages();
				this.smsProviderFactory.sendShortMessage(messages.getContent());
				this.smsOutboundMessageRepository.save(messages) ;
			}while (page < totalPageSize);
			return totalPageSize;
		}
	}  

	public void sendSMS(final String apiKey, final String to, final String body){
		Tenant tenant = this.securityService.authenticate(apiKey) ;  
		long tenantId = tenant.getId(); 
		Date currentDate = new Date();
		SMSMessage smsMessage = new SMSMessage(tenantId,to,currentDate,body);
		this.smsOutboundMessageRepository.save(smsMessage);
	}

	public Page<SMSMessage> findMessagesByTenantId(final Long tenantId, final int page, final int size ){
		Page<SMSMessage> smsMessages = this.smsOutboundMessageRepository.findAllByTenantId(tenantId, new PageRequest(page, size));
		return smsMessages;
	} 

	public SMSMessage findMessageByTenantIdAndId(final Long tenantId, final long messageId){
		SMSMessage smsMessage = this.smsOutboundMessageRepository.findByTenantIdAndId(tenantId,messageId);
		if(smsMessage == null){
			throw new SmsMessageNotFoundException(tenantId,messageId);
		} 
		return smsMessage;
	}   
	public int showTotalMessagesSentBetweenDatesByTenant(final Long tenantId, final Date dateFrom, final Date dateTo ){
		List<SMSMessage> smsMessages = this.smsOutboundMessageRepository.findByDatesAndId(tenantId,dateFrom,dateTo);
		return smsMessages.size();
		
	}  

	private void checkForEmptyMessage(final String message){
		List<ApiParameterError> error = new ArrayList<>();
		if(message.equals(null) || message.equals("")){
            defaultUserMessage = "Empty message content";
			developerMessage = "Message content is empty.";
			errorCode = "empty_body";
			ApiParameterError apiParameterError = ApiParameterError.parameterError(errorCode,
				defaultUserMessage,"body",developerMessage);
			apiParameterError.setValue(message); 
			error.add(apiParameterError);
        	throw new PlatformApiInvalidParameterException(error);		}
	}  

	private void checkForMessageSize(final String message){ 
		List<ApiParameterError> error = new ArrayList<>();
		if(message.length() > 160){
			defaultUserMessage = "Message character over limit";
			developerMessage = "Message is over 160 chars long.";
			errorCode = "body_over_limit";
			ApiParameterError apiParameterError = ApiParameterError.parameterError(errorCode,
				defaultUserMessage,"body",developerMessage);
			apiParameterError.setValue(message); 
			error.add(apiParameterError);
        	throw new PlatformApiInvalidParameterException(error);
		}
	}  


	private void checkForEmptyDestination(final String number){
		List<ApiParameterError> error = new ArrayList<>();
		if(number.equals(null) || number.equals("")){
			defaultUserMessage = "Empty receivers list";
			developerMessage = "Destination list is empty.";
			errorCode = "empty_number_list";
			ApiParameterError apiParameterError = ApiParameterError.parameterError(errorCode,
				defaultUserMessage,"to",developerMessage);
			apiParameterError.setValue(number); 
			error.add(apiParameterError);
        	throw new PlatformApiInvalidParameterException(error);
		}
	}

	private void checkForDuplicateNumbers(final String numbers){
		List<ApiParameterError> error = new ArrayList<>();
		List<String> individualNumbers = Arrays.asList(numbers.split(","));
		List<String> duplicateNumbers = new ArrayList<String>();
        for (String number : individualNumbers) {
            if(Collections.frequency(individualNumbers, number) > 1) {
            duplicateNumbers.add(number);
            }
        } 
        if (duplicateNumbers.size() != 0){ 
        	defaultUserMessage = "Duplicated destination address found";
			developerMessage = "The destination number you are attempting to send to is duplicated.";
			errorCode = "duplicate_number";
			ApiParameterError apiParameterError = ApiParameterError.parameterError(errorCode,
				defaultUserMessage,"to",developerMessage);
			apiParameterError.setValue(duplicateNumbers); 
			error.add(apiParameterError);
        	throw new PlatformApiDataValidationException(error);
        }
	}  

	private void checkForNumberFormat(final String numbers){
		List<ApiParameterError> error = new ArrayList<>();
		List<String> individualNumbers = Arrays.asList(numbers.split(",")); 
		String regex = "^\\+232.*";
		Pattern pattern = Pattern.compile(regex);
		for ( String number: individualNumbers){ 
			Matcher match = pattern.matcher(number);
			if(!match.find()){
				defaultUserMessage = "Invalid destination address";
				developerMessage = "The destination number you are attempting to send to is invalid";
				errorCode = "invalid_number";
				ApiParameterError apiParameterError = ApiParameterError.parameterError(errorCode,
				    defaultUserMessage,"to",developerMessage);
				apiParameterError.setValue(number); 
				error.add(apiParameterError);
				throw new PlatformApiDataValidationException(error);
			}
		}
	} 

	private void checkForMobileNetworkOperator(final String numbers){
		List<ApiParameterError> error = new ArrayList<>();
		List<String> individualNumbers = Arrays.asList(numbers.split(","));
		String validNetworkOperators = "";  
		StringWriter writer = new StringWriter();

		try{ 
			IOUtils.copy(validMobileCodes.getInputStream(), writer, "UTF-8");
			validNetworkOperators = writer.toString();
            
        }catch(IOException e){
            logger.error("Input error",e.toString());
        }    
        List<String> networkCodes = Arrays.asList(validNetworkOperators.split(","));

        for(String number: individualNumbers){
        	boolean validCode = false; 
        	for(String networkCode: networkCodes){
        		Pattern pattern = Pattern.compile("^\\+232" + networkCode + ".*"); 
        		Matcher match = pattern.matcher(number);
        		if(match.find()){
        			validCode = true;
        		}
        	} 
        	if(validCode == false){ 

        		defaultUserMessage = "MNO not supported";
        		developerMessage = "Mobile company is not supported";
        		errorCode = "mno_invalid"; 
        		ApiParameterError apiParameterError = ApiParameterError.parameterError(errorCode,
        			defaultUserMessage,"to",developerMessage);
        		apiParameterError.setValue(number);
        		error.add(apiParameterError);
        		throw new PlatformApiDataValidationException(error);

        	}
        }       
	}
	

	public void validateMessageAndDestination(final String number, final String message){

		checkForEmptyMessage(message);
		checkForMessageSize(message);
		checkForEmptyDestination(number);
		checkForDuplicateNumbers(number);
		checkForNumberFormat(number);
	    checkForMobileNetworkOperator(number);

	}


}
