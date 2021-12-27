FROM openjdk:11
ARG JAR_FILE_PATH=build/libs/*.jar
COPY ${JAR_FILE_PATH} /home/quant/SpringApp.jar
WORKDIR /home/quant
ENTRYPOINT ["java", "-jar","SpringApp.jar"]
