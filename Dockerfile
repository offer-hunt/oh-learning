FROM gradle:9.1.0-jdk21 AS build
WORKDIR /app

COPY . .
RUN gradle build --no-daemon -x test

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build app/build/libs/* app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
