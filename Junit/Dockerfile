FROM maven:3.9.16-eclipse-temurin-21
WORKDIR /app
COPY pom.xml .
COPY . /app
RUN mvn package
CMD ["java", "-jar", "target/application.jar"]
