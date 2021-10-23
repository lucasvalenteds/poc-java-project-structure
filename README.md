# POC: Java Project Structure

Demonstrating how to organize Java projects based on Gradle to minimize maintenance effort.

The goal is to create a template for back-end services for projects based on Service-Oriented Architecture (SOA) with a standard tech stack that makes it easy to deploy new services and reduces maintenance cost of shared code without creating shared libraries. Even though having multiple services allows us to choose different technologies for each project, the approach we want for this template is to increase consistency among multiple services regarding API contracts, error handling, code structure, testing and configuration to provide great developer experience.

The demo project is a REST API implemented in Java with endpoints to manager users. It allows clients to create, delete, find one and find many users persisted on a SQL database. The project contains two main Java packages: one for the service domain specific code and another one for shared code. The shared code is designed to increase reuse and reduce effort to implement the code in the domain package.

The REST API is implemented using Spring Framework MVC and deployed in an embedded Jetty instance configured via Spring Boot. Its interface is based on HTTP protocol and the payload is based on JSON standard. The configuration connects to a MySQL instance and run database migrations automatically via Flyway when running locally for convenience. Also, all the configuration is provided via environment variables with fallback to default values present in a properties file. The code is mostly tested using unit tests using JUnit, Mockito and AssertJ, but there are integration tests using Testcontainers and Wiremock. No manual configuration is required to run automated tests.

## Project structure

The project structure is mostly managed by Gradle. It has four modules: `contract`, `shared`, `server` and `client`.

The `contract` module contains every class and interface that defines the service, such as POJOs, API responses and interfaces with the operations this service exposes to clients. It also contains participants that are shared among services such as classes for API responses and custom exceptions for validation errors, resource not found and client error. This module does not depend on any other module, actually every module depends on it.

The `shared` module contains utilities and common code that we need to implement most web services. It contains error handlers for Spring controllers, SQL utility methods and an abstraction to provide make it easy to implement HTTP clients with JSON serialization and deserialization with minimal effort. It also provides Spring Beans to configure the database according to the project standard mentioned before.

The `server` module is intended to be mostly domain specific code. It contains implementations of the contract to provide the service functionalities via HTTP protocol, persistence in relational database and custom errors.

The `client` module provides an implementation of the service contract that communicates via HTTP to an instance of the service running somewhere. It may be used by other services to communicate with this given REST API without having to implement the contract themselves. Some people may name it SDK or service driver too.

To minimize the cost of maintenance of shared code, it must be defined in a specific package, apart from the domain specific code. The shared code for any module should be in `com.example.shared` Java package and the domain specific code should be named after the entity it represents, such as `com.example.users` or `com.example.invoices`.

Beyond Java code, we also have Gradle build scripts written using Kotlin DSL. We also want to reuse them in other services that needs configurations such as JUnit Jupiter engine, test coverage with JaCoCo and Java version. That configuration resides in `buildSrc` and is Gradle exposes that via plugins that can be applied to subprojects that needs them.

## How to run

### Software

| Description | Command |
| :--- | :--- |
| Run tests | `./gradlew test` |
| Run application | `./gradlew bootRun` |

> The application requires the database to be provisioned beforehand

### Infrastructure

| Description | Command |
| :--- | :--- |
| Provision database instance | `make provision` |
| Destroy database instance | `make destroy` |

### Manual testing

| Description | Command |
| :--- | :--- |
| Create a user | `make create` |
| Delete a user | `make delete ID=<user_id>` |
| Find one user | `make find-one ID=<user_id>` |
| Find all users | `make find-all` |

## Preview

```
$ make create 
*   Trying 127.0.0.1:8080...
* Connected to localhost (127.0.0.1) port 8080 (#0)
> POST /users HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.79.1
> Accept: */*
> Content-Type: application/json
> Content-Length: 33
> 
} [33 bytes data]
* Mark bundle as not supporting multiuse
< HTTP/1.1 201 Created
< Content-Type: application/json
< Transfer-Encoding: chunked
< 
{ [78 bytes data]
* Connection #0 to host localhost left intact
{
  "id": "845ac029-28fa-43aa-bc8c-e354acd57c3c",
  "name": "John Smith",
  "age": 45
}
```

```
$ make create 
*   Trying 127.0.0.1:8080...
* Connected to localhost (127.0.0.1) port 8080 (#0)
> POST /users HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.79.1
> Accept: */*
> Content-Type: application/json
> Content-Length: 33
> 
} [33 bytes data]
* Mark bundle as not supporting multiuse
< HTTP/1.1 400 Bad Request
< Content-Type: application/json
< Transfer-Encoding: chunked
< 
{ [72 bytes data]
* Connection #0 to host localhost left intact
{
  "code": 1001,
  "message": "Another user with same name already exists"
}
```

```
$ make find-all 
*   Trying 127.0.0.1:8080...
* Connected to localhost (127.0.0.1) port 8080 (#0)
> GET /users HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.79.1
> Accept: */*
> Content-Type: application/json
> 
* Mark bundle as not supporting multiuse
< HTTP/1.1 200 OK
< Content-Type: application/json
< Transfer-Encoding: chunked
< 
{ [100 bytes data]
* Connection #0 to host localhost left intact
{
  "items": [
    {
      "id": "845ac029-28fa-43aa-bc8c-e354acd57c3c",
      "name": "John Smith",
      "age": 45
    }
  ],
  "total": 1
}
```

```
$ make find-one ID=845ac029-28fa-43aa-bc8c-e354acd57c3c
*   Trying 127.0.0.1:8080...
* Connected to localhost (127.0.0.1) port 8080 (#0)
> GET /users/845ac029-28fa-43aa-bc8c-e354acd57c3c HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.79.1
> Accept: */*
> Content-Type: application/json
> 
* Mark bundle as not supporting multiuse
< HTTP/1.1 200 OK
< Content-Type: application/json
< Transfer-Encoding: chunked
< 
{ [78 bytes data]
* Connection #0 to host localhost left intact
{
  "id": "845ac029-28fa-43aa-bc8c-e354acd57c3c",
  "name": "John Smith",
  "age": 45
}
```

```
$ make delete ID=845ac029-28fa-43aa-bc8c-e354acd57c3c
*   Trying 127.0.0.1:8080...
* Connected to localhost (127.0.0.1) port 8080 (#0)
> DELETE /users/845ac029-28fa-43aa-bc8c-e354acd57c3c HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.79.1
> Accept: */*
> Content-Type: application/json
> 
* Mark bundle as not supporting multiuse
< HTTP/1.1 202 Accepted
< Content-Type: application/json
< Transfer-Encoding: chunked
< 
{ [78 bytes data]
* Connection #0 to host localhost left intact
{
  "id": "845ac029-28fa-43aa-bc8c-e354acd57c3c",
  "name": "John Smith",
  "age": 45
}
```
