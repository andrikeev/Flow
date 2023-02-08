FROM openjdk:11-jre-slim
EXPOSE 8080
RUN mkdir /app
COPY ./build/libs/*.jar /app/app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
