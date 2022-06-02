# Java Spring Boot application to retrieve book and album information

This is a Spring Boot REST architecture based web application which exposes an API to retrieve the books and album information. This application uses Google Book API and iTunes API as upstream services to retrieve books and albums information.
## The functional requirements of this application

Followings are the main functional requirements identified according to the given description.

1. Expose an API to retrieve the books and album information from google Book API and iTunes API.
2. Sort the books and album information by title alphabetically.
3. Create API documentation which will be generated automatically according to existing services.
4. Expose an API to retrieve metrics on response times for upstream services.
5. Expose an API to retrieve application metrics.
6. Limit the number of records retrieved from the upstream services.

## The non-functional requirements of this application

Followings are the non-functional requirements identified according to the given description.
1. The stability of the downstream service may not be affected by the stability of the upstream services.
2. Application properties may be configurable per environment.
3. Resilience: application can handle multiple requests with lesser response time.
4. Higher performance in request handling

## Maven dependencies used in this application

This application has used the following dependencies to implement the below functionalities. When adding dependency information, artifactId is added as the first value and the groupId of the relevant maven dependency is added inside brackets.

| Maven Dependency                                        | Functionality                                          |
|---------------------------------------------------------|--------------------------------------------------------|
| springdoc-openapi-ui (org.springdoc)                    | Implement self API documentation                       |
| httpclient (org.apache.httpcomponents)                  | Send requests and retrieve data from upstream services |
| lombok (org.projectlombok)                              | Generate getters and setters automatically             |
| json (org.json)                                         | Read JSON response data                                |
| junit (junit)                                           | Implement unit test                                    |
| spring-boot-starter-actuator (org.springframework.boot) | Monitor the application and gather metrics             |


## Run the application

### Prerequisites to run this application

This application can build using Maven 3 build automation tool and run on Java 1.8 .
#### 1. Build the application
This application has used the following two files to define the application properties according to the environment.

        ● application-dev.properties file - dev environment properties
        ● application-prod.properties file - production environment properties

And also spring.profiles.active property inside the application.properties file is used to specify which profiles is active. You can set it as follows to build the app with desired environment properties by changing the <environment> value as you need.

    spring.profiles.active=<environment>
 ex - spring.profiles.active=dev
 
You can build the application by executing the below command in the project root directory.

    mvn clean install
This will create digitallibrary-0.0.1-SNAPSHOT.jar artifact file in the project root/target directory.
####  2. Run the application according to the desired environment property.
There are two ways to run this application.
    
##### A. Specify the environment in the artifact execution command
You can run the application with the desired environment properties by adding the relevant environment inside <env> tag in the following command.

    java -jar digitallibrary-0.0.1-SNAPSHOT.jar --spring.profiles.active=<env>
    
Ex :- java -jar digitallibrary-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
      java -jar digitallibrary-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
      
##### B. Run the application with the mentioned environment in the application.properties file
Execute the following command.

    java -jar digitallibrary-0.0.1-SNAPSHOT.jar

## Send request to the application

You have to add the search term into the request query parameter.

    curl --location --request GET 'http://localhost:8083/library/itemList?term=hello'  

## Limit of results on upstream services
You can limit the number of records retrieved from the upstream service by changing the value of response.entityLimit value in the desired application.properties file (application-dev.properties/ application-prod.properties).

## API Documentation

API documentation has been created using Swagger UI and that can be accessed using following URL.

    <applicationHostURL>/swagger-ui/index.html

## Application Monitoring
### Application health information
The application health information such as application status, and application disk management information can be retrieved using the following endpoint.

    http://<applicationHostURL>/actuator/health
ex - http://localhost:8083/actuator/health

### Application metric information
All the available application metrics can be retrieved from the following endpoint as the first step.

    http://<applicationHostURL>/actuator/metrics
ex - http://localhost:8083/actuator/metrics

Then desired metric information can be retrieved using the following endpoint by replacing the <desiredMetric> value by desired metric.

    http://<applicationHostURL>/actuator/metrics/<desiredMetric>
ex - http://localhost:8083/actuator/metrics/application.ready.time

## Technology/mechanism Choice

This web application has been implemented using the Java Spring Boot framework which helps to develop web application and microservices faster and easier. Maven dependency management tool has been used to manage dependencies and also it is used to create the project's final artifact. A jar file will be created as the final artefact and it can be easilydeployed in a microservice environment.

Web application implementation has used Threads which allows the web application to operate more efficiently by handling multiple requests at the same time. Also, Java Future Interface related implementation has been used to manage upstream requests in an asynchronous manner. Those implementations were chosen mainly focusing on the non-functional requirement of this web application.
