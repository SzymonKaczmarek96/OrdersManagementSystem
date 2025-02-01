FROM gradle:jdk21 AS builder
WORKDIR /app
COPY . .
RUN ./gradlew clean build

FROM eclipse-temurin:21-jdk
WORKDIR /app
ARG JAR_FILE=build/libs/*.jar
COPY --from=builder /app/${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]