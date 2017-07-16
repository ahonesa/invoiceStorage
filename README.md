Invoices Storage
=========================

This is a recruitment assignment project

Goal of the project is to build a high performance and -availability storage solution for customers, invoices and invoicePayments

Uses Cassandra as storage, which is able to store large amouts of invoices and payments clustered.

## Features:

* Akka-http
* Phantom
* Cassandra
* Swagger

## Requirements

* sbt
* JDK 8
* Cassandra (local comp, container or cluster)

## Configuration

* Set Cassandra configuration in application.conf

## Run application

```
sbt reStart
```

## Swagger

Project does not have Swagger-UI libraries included, but e.g. http://petstore.swagger.io can be used with local swagger.json URL: http://localhost:12345/api-docs/swagger.json

## Run tests

```
sbt test
```

## Schema

![Cassandra schema](https://github.com/ahonesa/invoiceStorage/blob/master/schema.jpeg)