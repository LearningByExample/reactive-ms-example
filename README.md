## Reactive MicroServices Example

[![Build Status](https://travis-ci.org/LearningByExample/reactive-ms-example.svg?branch=master)](https://travis-ci.org/LearningByExample/reactive-ms-example)
[![Coverage Status](https://coveralls.io/repos/github/LearningByExample/reactive-ms-example/badge.svg?branch=master)](https://coveralls.io/github/LearningByExample/reactive-ms-example?branch=master)
## info
This is an example of doing reactive MicroServices using spring 5 functional web framework and spring boot 2.

This service provide and API that will get the geo location and the sunrise and sunset times from an address.

```Gherkin
Scenario: Get Location
  Given I've an address
  When I call the location service
  Then I should get a geo location
  And I should get the sunrise and sunset times
```
To implemented this example we consume a couple of REST APIs.

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

Get from address
```shell
$ curl -X GET "http://localhost:8080/api/location/Trafalgar%20Square%2C%20London%2C%20England" -H  "accept: application/json"
```

Post from JSON
```shell
$ curl -X POST "http://localhost:8080/api/location" -H  "accept: application/json" -H  "content-type: application/json" -d "{  \"address\": \"Trafalgar Square, London, England\"}"
```

Both will produce something like:
```json
{
  "geographicCoordinates": {
    "latitude": 51.508039,
    "longitude": -0.128069
  },
  "sunriseSunset": {
    "sunrise": "2017-05-21T03:59:08+00:00",
    "sunset": "2017-05-21T19:55:11+00:00"
  }
}
```

## API
[![View in the embedded Swagger UI](https://avatars0.githubusercontent.com/u/7658037?v=3&s=20) View in the embedded Swagger UI](http://localhost:8080/index.html)

[![Run in Postman](https://lh4.googleusercontent.com/Dfqo9J42K7-xRvHW3GVpTU7YCa_zpy3kEDSIlKjpd2RAvVlNfZe5pn8Swaa4TgCWNTuOJOAfwWY=s20) Run in Postman](https://app.getpostman.com/run-collection/498aea143dc572212f17)

## Project Structure

- [main/java](/src/main/java/org/learning/by/example/reactive/microservices)
    - [/application](/src/main/java/org/learning/by/example/reactive/microservices/application) : Main Spring boot application and context configuration.  
    - [/routers](/src/main/java/org/learning/by/example/reactive/microservices/routers) : Reactive routing functions.
    - [/handlers](/src/main/java/org/learning/by/example/reactive/microservices/handlers) : Handlers used by the routers.
    - [/services](/src/main/java/org/learning/by/example/reactive/microservices/services) : Services for the business logic needed by handlers.
    - [/exceptions](/src/main/java/org/learning/by/example/reactive/microservices/exceptions) : Businesses exceptions.
    - [/model](/src/main/java/org/learning/by/example/reactive/microservices/model) : POJOs.
- [test/java](/src/test/java/org/learning/by/example/reactive/microservices)
    - [/application](/src/test/java/org/learning/by/example/reactive/microservices/application) : Application system and unit tests.
    - [/routers](/src/test/java/org/learning/by/example/reactive/microservices/routers) : Integration tests for routes.
    - [/handlers](/src/test/java/org/learning/by/example/reactive/microservices/handlers) : Unit tests for handlers.
    - [/services](/src/test/java/org/learning/by/example/reactive/microservices/services) : Unit tests for services.
    - [/model](/src/test/java/org/learning/by/example/reactive/microservices/model) : POJOs used by the test.
    - [/test](/src/test/java/org/learning/by/example/reactive/microservices/test) : Helpers and base classes for testing.

## References

- https://spring.io/blog/2016/09/22/new-in-spring-5-functional-web-framework
- https://spring.io/blog/2017/02/23/spring-framework-5-0-m5-update
- http://junit.org/junit5/docs/current/user-guide/#running-tests-build-maven
- https://github.com/junit-team/junit5-samples
- http://quotesondesign.com/
