FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
RUN mkdir -p /app/upload
COPY ./build/libs/*SNAPSHOT.jar project.jar
ENTRYPOINT ["java", "-jar", "project.jar"]
