FROM openjdk:11-jdk-slim
WORKDIR app
COPY target/parkinglot.jar ~/app/parkinglot.jar
EXPOSE 8080:8080
ENTRYPOINT ["java", "-jar", "~/app/parkinglot.jar"]
