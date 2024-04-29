FROM openjdk:17-oracle as build
EXPOSE 8081
VOLUME /tmp
WORKDIR /workspace/app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src
RUN ./mvnw package -DskipTests
COPY target/TodoService-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/workspace/app/app.jar"]