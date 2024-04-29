# TelustTest

This project uses Jakarta EE with Jakarta imports, Spring Data JPA, Spring MVC.

## Prerequisites


Make sure you have installed all of the following prerequisites on your development machine:

- Java - [Download & Install JDK](https://www.oracle.com/ca-en/java/technologies/downloads/#java17)(Java 17 required)
- Docker - [Download & Install Docker](https://docs.docker.com/get-docker/)
- Maven - [Download & Install Maven](https://maven.apache.org/download.cgi)

## Building with Maven

To build the project, navigate into the project directory and run the Maven command:
```bash
mvn clean install
mvn clean package
mvn spring-boot:run

```
To Run Tests with Maven, run the following command:
```bash
mvn test
```

To run the project in Docker, run the following commands:

```bash

docker build -t TelustTest .
docker run -p 8081:8081 TelustTest
```