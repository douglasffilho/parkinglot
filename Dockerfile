FROM openjdk:11-jdk-alpine
WORKDIR app
COPY target/parkinglot.jar ~/app/parkinglot.jar
ENTRYPOINT ["java", "-jar", "~/app/parkinglot.jar"]
