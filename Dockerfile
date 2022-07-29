FROM openjdk:11-jdk-slim
WORKDIR app
COPY target/parkinglot.jar ~/app/parkinglot.jar
ENTRYPOINT ["java", "-jar", "~/app/parkinglot.jar"]
