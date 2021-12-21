FROM openjdk:11
CMD ["./gradlew", "clean", "build"]
ARG JAR_FILE_PATH=build/libs/*.jar
COPY ${JAR_FILE_PATH} /tmp/SpringApp.jar
WORKDIR /tmp
ENTRYPOINT ["java", "-jar","SpringApp.jar"]
