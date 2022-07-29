FROM openjdk:19-ea
WORKDIR app
COPY target/parkinglot.jar ~/app/parkinglot.jar
ENTRYPOINT ["java", "-jar", "~/app/parkinglot.jar"]
