## Reactive MicroServices Example

[![Build Status](https://travis-ci.org/LearningByExample/reactive-ms-example.svg?branch=master)](https://travis-ci.org/LearningByExample/reactive-ms-example)
[![Coverage Status](https://coveralls.io/repos/github/LearningByExample/reactive-ms-example/badge.svg?branch=master)](https://coveralls.io/github/LearningByExample/reactive-ms-example?branch=master)
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

Includes and in depth look to testing using JUnit5:
- Unit, Integration and System tests.
- Mocking, including reactive functions.
- BDD style assertions.
- Test tags with maven profiles.

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
    - /application : Main Spring boot application and context configuration.  
    - /routers : Reactive routing functions.
    - /handlers : Handlers used by the routers.
    - /services : Services for the business logic needed by handlers.
    - /exceptions : Businesses exceptions.
    - /model : POJOs.
- test/java:
    - /application : Application system tests.
    - /routers : Integration tests for routes.
    - /handlers : Unit tests for handlers.
    - /services : Unit tests for services.
    - /categories : Tests categories.
    - /model : POJOs used by the test.
    - /test : Helpers and base classes for testing.

## References

- https://spring.io/blog/2016/09/22/new-in-spring-5-functional-web-framework
- https://spring.io/blog/2017/02/23/spring-framework-5-0-m5-update
- http://junit.org/junit5/docs/current/user-guide/#running-tests-build-maven
- https://github.com/junit-team/junit5-samples
- http://quotesondesign.com/
