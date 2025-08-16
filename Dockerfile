FROM eclipse-temurin:21-jdk-alpine
ARG JAR_FILE=./build/libs/draw-it-api-0.0.1-SNAPSHOT.jar
EXPOSE 80

COPY ${JAR_FILE} app.jar
RUN mkdir /image-temp

# 포트 오픈
EXPOSE 8080

ENTRYPOINT ["java","-Duser.timezone=Asia/Seoul", "-Dspring.profiles.active=prod", "-Djava.net.preferIPv4Stack=true", "-jar","/app.jar"]
