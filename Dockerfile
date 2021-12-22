FROM openjdk:11
CMD ["./gradlew", "clean", "build"]
ARG JAR_FILE_PATH=build/libs/*.jar
RUN file="$(ls /usr)" && echo $file
COPY ./build/libs/*.jar /tmp/SpringApp.jar
WORKDIR /tmp
ENTRYPOINT ["java", "-jar","SpringApp.jar"]
