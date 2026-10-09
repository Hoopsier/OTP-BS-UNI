FROM maven:3.9-eclipse-temurin-21

RUN apt-get update && apt-get install -y --no-install-recommends \
  openjfx \
  libgtk-3-0 \
  libgl1-mesa-dri \
  libgl1 \
  libx11-6 \
  libxtst6 \
  libxrender1 \
  libxi6 \
  && rm -rf /var/lib/apt/lists

WORKDIR /app

COPY pom.xml .
RUN mvn clean compile

COPY src ./src

CMD ["mvn", "javafx:run"]
