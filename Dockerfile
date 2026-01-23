# Use Java 17 runtime
FROM eclipse-temurin:17-jre-focal

WORKDIR /app

COPY target/cm-coding-challenge-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
