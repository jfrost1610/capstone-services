FROM maven:3.6-jdk-8-alpine as build
ADD ./ app/
WORKDIR /app
RUN mvn install -DskipTests

FROM openjdk:8-jdk-alpine
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8421
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=dev","app.jar"]
