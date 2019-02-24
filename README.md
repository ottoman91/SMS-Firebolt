# Firebolt
Firebolt is an SMS aggregator written in the Spring Boot framework. It was built with the intent of enabling small and medium enterprises(SMEs) and fintech startups in low resource environments to deliver SMS based services for their customers. The aggregator was initially created to facilitate SMS communication for [miKashBoks](https://mikashboks.com/), a mobile money based conditional cash transfer scheme for small-holder farmers in Sierra Leone that was piloted with support from the United Nations Capital Development Fund(UNCDF) in Sierra Leone. The framework is open-source and can be redeployed in  any similar low resource environment. The aggregator was based off the code-base of the Message Gateway of the [Fineract Project](https://github.com/openMF/message-gateway). 
You can read more about miKashBoks and my role in the project [here](http://usmankhaliq.com/fin/icommit2/)

## Requirements
1. Java JDK or JRE version 7 or higher
2. MySQL 
3. The Gradle Build Tool 
4. A running or configured Kannel server ( Note: A Kannel server would be required during deployment, but the APIs can be tested out without a running Kannel instance on a local machine)

## High Level Architecture of Firebolt 
The following is an illustration of the high level architecture of Firebolt. 
![High Level Architecture](https://github.com/ottoman91/SMS-Firebolt/blob/develop/Firebolt%20Architecture.png) 

### Tenant Service
The Tenant Service provides a number of APIs that are used by the administrators of Firebolt to create, edit and delete records of clients using Firebolt. The service can also be used for blocking and unblocking clients from using the Firebolt APIs. Finally, it provides APIs that can be used by the administrators for billing purposes, such as reporting the number of messages sent by a client between specific dates etc. 

### Message Service
The message service provides APIs that are used by the clients for sending messages to specific numbers. The Message service provides a layer of abstraction that allows for clients to easily send their message requests via the API calls that are then forwarded to the Kannel gateway, which in turn sends the messages to the recipients. 
## Usage and Configuration
1. Clone this repo
2. In terminal or another CLI, go to the home director of the repo 
3. Open the src/main/resources/config.properties file. Change the following properties according to your configuration (Note: For testing the aggregator on a local instance, you do not need to change these settings):
* kannel.url - URL for the Kannel instance deployed
* kannel.port - Port of the deployed Kannel Instance
* kannel.username - Username of the deployed Kannel instance
* kannel.password - Password of the deployed Kannel instance
* country.codes - Write down the country codes according to the countries where the aggregator would be deployed
* Write down the names of the mobile network carriers, along with an ID that identifies them. For example, the config.properties file by default contains the names of two mobile network operators in Sierra Leone (Africell and Airtel), and also contains codes attached with each of these. Change these settings according to your requirements
* admin.username - Username of the admin of the aggregator
* admin.password - Password for admin rights of the aggregator
* server.port - Port of the server that is running the Firebolt instance 
4. Open the src/main/resources/sentry.properties file. Enter the dsn of your sentry instance to log crash reports in real-time 
5. Open the src/main/resources/users.properties file. Enter the names and priviledge levels of the users of Firebolt. 
3. Run the following command to build the jar
  `./gradlew clean build` 
4. Run the jar with the following command 
`java -jar build/libs/firebolt.jar` 

5. After running the jar, use an API development platform such as [Postman](https://www.getpostman.com/) to test out the API endpoints on your server.  

## The API Structure 
Firebolt communicates with a deployed instance of the [Kannel](https://www.kannel.org/) SMS gateway for sending messages. It provides a number of RESTful APIs that provide the following features:
1. Managing the configuration of the companies using Firebolt to send messages to their customers
2. Assessing statistics about the messages sent by differen companies, which can then be used to bill these companies
3. Sending messages to users 
4. Sanity checks to ensure that the details of the customer(mobile phone number, message length etc.) are correct. 

### The Clients APIs
These APIs are used to manage the companies that sign up for using Firebolt. 

#### Create new Client with http://host:9191/clients/ 
Request Body:
```
method: POST 
Basic Auth with username and password corresponding to the admin.username and admin.password values in the src/main/resources/config.propertiles file
{
	"name" : "Jack",
	"displayName" : "Jack"
}
``` 
Response Body:
```
{
    "id": 2,
    "apiKey": "f4da4c9a-a734-4309-b0e2-d4e8b907fdec",
    "name": "Jack",
    "displayName": "Jack",
    "blocked": false
}
```
#### Retrive a single client with http://host:9191/clients/{id} 
Request Body:
```
method: GET 
Basic Auth with username and password corresponding to the admin.username and admin.password values in the src/main/resources/config.propertiles file
``` 
Response Body:
```
{
    "id": 2,
    "apiKey": "f4da4c9a-a734-4309-b0e2-d4e8b907fdec",
    "name": "Jack",
    "displayName": "Jack",
    "blocked": false
}
``` 
#### Retrieve all clients with http://host:9191/clients?page={}&size={} 
Request Body:
```
method: GET 
Basic Auth with username and password corresponding to the admin.username and admin.password values in the src/main/resources/config.propertiles file
```  
#### Delete a client with http://host:9191/clients/{id}
Request Body:
```
method: DELETE 
Basic Auth with username and password corresponding to the admin.username and admin.password values in the src/main/resources/config.propertiles file
```  
#### Update a Client's Details with http://host:9191/clients/{id}
Request Body:
```
method: PUT 
Basic Auth with username and password corresponding to the admin.username and admin.password values in the src/main/resources/config.propertiles file 
{
	"name" : "Jack",
	"displayName" : "Jack Snow"
}
```  
Response Body: 
```
{
    "id": 2,
    "apiKey": "f4da4c9a-a734-4309-b0e2-d4e8b907fdec",
    "name": "Jack",
    "displayName": "Jack Snow",
    "blocked": false
}
``` 
#### Update a Client's API Key with http://host:9191/clients/{id}/apikey
Request Body:
```
method: PUT 
Basic Auth with username and password corresponding to the admin.username and admin.password values in the src/main/resources/config.propertiles file 
{
	"apiKey" : "12345678",
}
```   
#### Block a Client from Using the ApiKey with http://host:9191/clients/{id}/block
Request Body:
```
method: GET 
Basic Auth with username and password corresponding to the admin.username and admin.password values in the src/main/resources/config.propertiles file
```   
#### Unlock a client from using the ApiKey with http://host:9191/clients/{id}/unblock 
Request Body:
```
method: GET 
Basic Auth with username and password corresponding to the admin.username and admin.password values in the src/main/resources/config.propertiles file
```    
#### Retrieve all messages sent by a client with http://host:9191/clients/{id}/messages?page={}&size={} 
Request Body:
```
method: GET 
Basic Auth with username and password corresponding to the admin.username and admin.password values in the src/main/resources/config.propertiles file
```   
#### Retrieve a single message sent by a client with http://host:9191/clients/{id}/messages/{messageId} 
Request Body:
```
method: GET 
Basic Auth with username and password corresponding to the admin.username and admin.password values in the src/main/resources/config.propertiles file
```   
#### Retrieve stats of messages sent by a client within specified dates with http://host:9191/clients/{id}/messages/stats?dataFrom={}&dateTo={} 
The dates should be in the yyyy-MM-dd format  
Request Body:
```
method: GET 
Basic Auth with username and password corresponding to the admin.username and admin.password values in the src/main/resources/config.propertiles file
```   
### The Messages APIs
These APIs are used by the clients to send messages to their customers. Each client needs to use their APIKey in order to send the message. 

#### Send Message Via HTTP Get Request with http://host:9191/messages/http/send?apiKey={}&to={}&body={} 
```
method: GET 
```    
### Send Message Via a JSOn Object in a Post Request with http://host:9191/messages 
Request Body:
```
method: POST 
Basic Auth with username and password corresponding to the admin.username and admin.password values in the src/main/resources/config.propertiles file
Add a Header to the APICall with the key 'SMS-Firebolt-Api-Key' and the value equal to the value of the customer's APIKey
{
	"to":"+23277774775,+23277776774",
	"body":"whats up"
}
```   
