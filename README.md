## Reactive MicroServices Example

[![Build Status](https://travis-ci.org/LearningByExample/reactive-ms-example.svg?branch=master)](https://travis-ci.org/LearningByExample/reactive-ms-example)

## info
This is a small example of doing reactive MicroServices using spring 5 functional web framework and spring boot 2, includes integration test using WebTestClient.

This service provide and API that will greet the user with a quote.

This example cover several topics: 

- Router Functions
- Static Web-Content
- Creation on Reactive Java Services/Components
- Error handling in routes and services
- Reactive Web Client to consume external REST Services
- Unit testing of all components and layers
- Mocking
- Organizing your project in manageable packaging

It use extensively functional programming and reactive types

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
        "greetings" : "json",
        "quote" : "Every night I pray that clients with taste will get money and clients with money will get taste."
    }
    ```
## API
[Run API via Swagger UI](http://localhost:8080/index.html)

## References

- https://spring.io/blog/2016/09/22/new-in-spring-5-functional-web-framework
- https://spring.io/blog/2017/02/23/spring-framework-5-0-m5-update