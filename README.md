# Spring Boot ezQuiz

- Spring Boot ezQuiz is a web application built on Java 17, Spring Boot 3, JPA/Hibernate and Thymeleaf Template Engine.
- ezQuiz allows users who are Teacher create Quizzes & Questions, then they can assign their Quizzes to Leaner users.
- Also, users who are Learner can take the Quizzes that their teacher assigned, or any available others.

## Documentation

The documentation is divided into several sections:

- [Prerequisites](#prerequisites)
- [Libraries](#libraries)
- [Running the application](#running-the-application)
  - [On Windows](#on-windows)
  - [On MacOS/ Linux](#on-macos-linux)
- [Database connection parameters](#database-connection-parameters)

## Prerequisites

- [Java JDK](https://www.oracle.com/pl/java/technologies/javase-downloads.html) version 17+

## Libraries

| Library name                                                                                                     | Description                                                                                                                                                           |
| ---------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| [Spring Boot 3](https://spring.io/projects/spring-boot)                                                          | Spring Boot makes it easy to create stand-alone, production-grade Spring based Applications that you can "just run".                                                  |
| [Spring Data Repositories](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.repositories) | The JPA module of Spring Data contains a custom namespace that allows defining repository beans.                                                                      |
| [JPA (Hibernate)](https://hibernate.org/)                                                                        | Hibernate ORM enables developers to more easily write applications whose data outlives the application process.                                                       |
| [Lombok](https://projectlombok.org/)                                                                             | Project Lombok is a java library that automatically plugs into your editor and build tools, spicing up your java.                                                     |
| [Thymeleaf Template Engine](https://www.thymeleaf.org/)                                                          | Thymeleaf is a modern server-side Java template engine for both web and standalone environments.                                                                      |
| [Boostrap 5.3](https://getbootstrap.com/docs/5.3/getting-started/introduction/)                                  | Bootstrap is a powerful, feature-packed frontend toolkit. Build anything—from prototype to production—in minutes.                                                     |
| [jQuery](https://jquery.com/)                                                                                    | jQuery is a fast, small, and feature-rich JavaScript library, which makes HTML document traversal and manipulation, event handling, animation, and Ajax much simpler. |

## Running the application

#### On Windows

```bash
## Build application using Maven Wrapper
mvnw.cmd clean install

## Run Spring boot application using Maven Wrapper or simply run Application class
mvnw.cmd spring-boot:run
```

#### On MacOS/ Linux

```bash
## Build application using Maven Wrapper
./mvnw clean install

## Run Spring boot application using Maven Wrapper or simply run Application class
./mvnw spring-boot:run
```

## Database connection parameters

| JDBC URL                                         | Username | Password |
| ------------------------------------------------ | -------- | -------- |
| jdbc:mysql://${MYSQL_HOST:localhost}:3306/ezquiz | admin1   | 123      |
