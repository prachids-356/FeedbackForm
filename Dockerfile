FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/feedback-app-1.0.0.jar app.jar
COPY index.html .
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

FROM tomcat:10.1
COPY target/*.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080