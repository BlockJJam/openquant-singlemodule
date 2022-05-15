FROM openjdk:11-jre-slim
ARG JAR_FILE_PATH=build/libs/*.jar
ARG APPLICATION_YAML_PATH
ARG DATABASE_DEPLOY_URL
ARG DATABASE_DEPLOY_ID
ARG DATABASE_DEPLOY_PWD
ENV SPRING_PROFILES_ACTIVE=$APPLICATION_YAML_PATH
ENV SPRING_DATABASE_DEPLOY_URL=$DATABASE_DEPLOY_URL
ENV SPRING_DATABASE_DEPLOY_ID=$DATABASE_DEPLOY_ID
ENV SPRING_DATABASE_DEPLOY_PWD=$DATABASE_DEPLOY_PWD
COPY ${JAR_FILE_PATH} /home/quant/SpringApp.jar
WORKDIR /home/quant
ENTRYPOINT ["java", "-jar","SpringApp.jar","-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}","Dspring-boot.run.arguments=--spring.datasource.url=${SPRING_DATABASE_DEPLOY_URL}, --spring.datasource.username=${SPRING_DATABASE_DEPLOY_ID}, --spring.datasource.password=${SPRING_DATABASE_DEPLOY_PWD}"]
#ENTRYPOINT ["java", "-jar","SpringApp.jar","--spring.profiles.active=${SPRING_PROFILES_ACTIVE}","--spring.datasource.url=${SPRING_DATABASE_DEPLOY_URL}","--spring.datasource.username=${SPRING_DATABASE_DEPLOY_ID}","--spring.datasource.password=${SPRING_DATABASE_DEPLOY_PWD}"]