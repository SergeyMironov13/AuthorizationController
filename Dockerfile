FROM eclipse-temurin:17-jdk

EXPOSE 8081

ADD target/AuthorizationController-0.0.1-SNAPSHOT.jar myapp.jar

ENTRYPOINT ["java","-jar","/myapp.jar"]

