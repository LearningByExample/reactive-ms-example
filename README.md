## Reactive MicroServices Example

[![Build Status](https://travis-ci.org/LearningByExample/reactive-ms-example.svg?branch=master)](https://travis-ci.org/LearningByExample/reactive-ms-example)

## info
This is a small example of doing reactive MicroServices using spring 5 functional web framework and spring boot 2, includes integration test using WebTestClient.

To run this service:

```shell
$ mvnw spring-boot:run
```

## Sample requests

- GET [http://localhost:8080/api/hello](http://localhost:8080/hello)

- GET [http://localhost:8080/api/hello/reactive](http://localhost:8080/hello/reactive)

- POST [http://localhost:8080/api/hello](http://localhost:8080/hello) 

    ```json
    {
        "name" : "json"
    }
    ```

[![Run in Postman](https://run.pstmn.io/button.svg)](https://app.getpostman.com/run-collection/498aea143dc572212f17)

## References

- https://spring.io/blog/2016/09/22/new-in-spring-5-functional-web-framework
- https://spring.io/blog/2017/02/23/spring-framework-5-0-m5-update