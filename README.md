## Reactive MicroServices Example

[![Build Status](https://travis-ci.org/cecile/reactive-ms-example.svg?branch=master)](https://travis-ci.org/cecile/reactive-ms-example)

## info
This is a small example of doing reactive MicroServices using spring 5 functional web framework and spring boot 2

To run this service:

```shell
$ mvnw spring-boot:run
```

## Sample requests

- GET [http://localhost:8080/hello](http://localhost:8080/hello)

- GET [http://localhost:8080/hello/reactive](http://localhost:8080/hello/reactive)

- POST [http://localhost:8080/hello](http://localhost:8080/hello) 

    ```json
    {
        "name" : "json"
    }
    ```

- [![Run in Postman](https://run.pstmn.io/button.svg)](https://app.getpostman.com/run-collection/498aea143dc572212f17)