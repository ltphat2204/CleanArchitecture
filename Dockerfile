FROM maven:3.9.10-eclipse-temurin-21 AS build

WORKDIR /app

copy pom.xml .
copy src ./src

RUN mvn clean package -DskipTests #build skip test

FROM eclipse-temurin:21-jre-alpine-3.21

COPY --from=build /app/target/*.jar /app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]