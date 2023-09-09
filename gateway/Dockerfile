FROM amazoncorretto:11-alpine-jdk
COPY target/*.jar shareit-gateway.jar
RUN apk update && apk add tzdata
ENV TZ="Europe/Moscow"
ENTRYPOINT ["java","-jar","/shareit-gateway.jar"]