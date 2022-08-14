# urlshortner
Simple URL shortner application using Spring Boot, Astra DB and Zookeeper. 

The Docker-compose is configured to run 3 Zookeeper instances and 4 replicas of Spring boot and nginx as load balancer for the web. 

## To Run
### First build the application and create Jar file using.
mvn package 

### Run below command create docker image which creates a tag urlshortenweb  
docker build -t urlshortenweb .

### To start the application
docker-compose up -d

## Apis:

Shorten URL: http://localhost:8080/shorten?url=www.google.com

Expand URL: http://localhost:8080/L2NVGRY7