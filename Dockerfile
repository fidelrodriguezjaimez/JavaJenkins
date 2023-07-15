FROM maven:3.6.0-jdk-11-slim AS build
COPY src /usr/src/app/src
COPY pom.xml /usr/src//app
RUN mvn -B package --file /usr/src/app/pom.xml

FROM openjdk:11
EXPOSE 8083
COPY --from=build /usr/src/app/target/maven-code-coverage.jar /usr/app/maven-code-coverage.jar
ENTRYPOINT ["java","-jar","/usr/app/maven-code-coverage.jar"]
