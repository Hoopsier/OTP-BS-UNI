FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests
RUN mvn dependency:copy-dependencies \
  -DincludeGroupIds=org.openjfx \
  -DoutputDirectory=/app/javafx


FROM eclipse-temurin:21-jre

WORKDIR /app

RUN apt-get update && apt-get install -y \
  libx11-6 \
  libxext6 \
  libxrender1 \
  libxtst6 \
  libxi6 \
  libxrandr2 \
  libxfixes3 \
  libxcomposite1 \
  libxcursor1 \
  libxdamage1 \
  libgtk-3-0 \
  libgl1 \
  libfontconfig1 \
  libfreetype6 \
  && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/application.jar /app/application.jar
COPY --from=build /app/javafx /javafx-sdk/lib

CMD ["java", "--module-path", "/javafx-sdk/lib", "--add-modules", "javafx.controls", "-Dprism.order=sw", "-jar", "/app/application.jar"]
