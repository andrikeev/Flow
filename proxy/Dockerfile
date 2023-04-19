FROM --platform=linux/amd64 openjdk:17.0.1-jdk-slim
EXPOSE 8080
RUN mkdir /app
COPY ./build/libs/app.jar /app/app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
