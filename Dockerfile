FROM openjdk:11
CMD ["./gradlew", "clean", "build"]
ARG JAR_FILE_PATH=build/libs/*.jar
RUN echo $(ls -l ./home)
COPY legacy/build/libs/*.jar /tmp/SpringApp.jar
WORKDIR /tmp
ENTRYPOINT ["java", "-jar","SpringApp.jar"]
