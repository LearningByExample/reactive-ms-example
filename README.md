## Reactive MicroServices Example

[![Build Status](https://travis-ci.org/LearningByExample/reactive-ms-example.svg?branch=master)](https://travis-ci.org/LearningByExample/reactive-ms-example)

## info
This is an example of doing reactive MicroServices using spring 5 functional web framework and spring boot 2.

This service provide and API that will greet the user with a quote.

This example cover several topics: 

- Functional programing.
- Reactive types.
- Router Functions.
- Static Web-Content.
- Creation on Reactive Java Services/Components.
- Error handling in routes and services.
- Reactive Web Client to consume external REST Services.
- Organizing your project in manageable packaging.

Includes and in depth look to testing:
- Unit, Integration and System tests.
- Mocking, including reactive functions.
- BDD style assertions.
- Test categories and profiles.

## usage

To run this service:

```shell
$ mvnw spring-boot:run
```

## Sample requests

- GET [http://localhost:8080/api/greetings](http://localhost:8080/greetings)

- GET [http://localhost:8080/api/greetings/reactive](http://localhost:8080/greetings/reactive)

- POST [http://localhost:8080/api/greetings](http://localhost:8080/greetings) 

    ```json
    {
        "name" : "json"        
    }
    ```
## API
[Run the API via the embedded Swagger UI](http://localhost:8080/index.html)

## Project Structure

- main/java
    - ReactiveMsApplication.java : Main Spring boot application.
    - ApplicationConfig.java : Spring Application Configuration and Beams.
    - /routers : Reactive routing functions.
    - /handlers : Handlers used by the routers.
    - /services : Services for the business logic needed by handlers.
    - /exceptions : Businesses exceptions.
    - /model : POJOs.
- test/java:
    - ReactiveMsApplicationTest : System tests.
    - /routers : Integration tests for routes.
    - /handlers : Unit tests for handlers.
    - /services : Unit tests for services.
    - /categories : Tests categories.
    - /model : POJOs used by the test.
    - BasicIntegrationTest.java : Base class for integration testing
    - HandlerHelper.java : Helper function for handlers

## References

- https://spring.io/blog/2016/09/22/new-in-spring-5-functional-web-framework
- https://spring.io/blog/2017/02/23/spring-framework-5-0-m5-update
- http://quotesondesign.com/