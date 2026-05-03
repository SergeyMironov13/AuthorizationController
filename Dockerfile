FROM eclipse-temurin:17-jdk

EXPOSE 8080

COPY target/AuthorizationController-0.0.1-SNAPSHOT.jar app.jar

CMD ["java", "-jar", "app.jar"]

