FROM eclipse-temurin:17-jdk

WORKDIR /app

EXPOSE 8080

COPY target/todolistapp-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]