FROM openjdk:11-jre-slim
ARG JAR_FILE_PATH=build/libs/*.jar
ARG APPLICATION_YAML_PATH
ARG DATABASE_DEPOLY_URL
ARG DATABASE_DEPOLY_ID
ARG DATABASE_DEPOLY_PWD
RUN echo ${APPLICATION_YAML_PATH}
RUN echo ${DATABASE_DEPOLY_URL}
RUN echo ${DATABASE_DEPOLY_ID}
RUN echo ${DATABASE_DEPOLY_PWD}
COPY ${JAR_FILE_PATH} /home/quant/SpringApp.jar
WORKDIR /home/quant
ENTRYPOINT ["java", "-jar","SpringApp.jar","-Dspring.profiles.active=${APPLICATION_YAML_PATH}","-Dspring-boot.run.arguments=--DATABASE_DEPLOY_URL=${DATABASE_DEPOLY_URL},--DATABASE_DEPLOY_ID=${DATABASE_DEPLOY_ID},--DATABASE_DEPLOY_PWD=${DATABASE_DEPOLY_PWD}"]