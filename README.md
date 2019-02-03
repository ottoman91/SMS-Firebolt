# Firebolt
Firebolt is an SMS gateway written in the Spring Boot framework. It was built with the intent of enabling small and medium enterprises(SMEs) and fintech startups in low resource environments to deliver SMS based services for their customers. The gateway was initially created to facilitate SMS communication for [miKashBoks](https://mikashboks.com/), a mobile money based conditional cash transfer scheme for small-holder farmers in Sierra Leone that was piloted with support from the UNDP in Sierra Leone. The framework is open-source and can be redeployed in  any similar low resource environment.  

## Requirements
1. Java JDK or JRE version 7 or higher
2. MySQL 
3. The Gradle Build Tool 

## Usage
1. Clone this repo
2. In terminal or another CLI, go to the home director of the repo
3. Run the following command to build the jar
  `./gradlew clean build` 
4. Run the jar with the following command 
`java -jar build/libs/firebolt.jar`
##### To run 
  cd build/lib
  
