FROM maven:3.9.9-eclipse-temurin-21
WORKDIR /app

RUN apt-get update && apt-get install -y \
  libx11-6 \
  libxext6 \
  libxrender1 \
  libxtst6 \
  libxi6 \
  libgtk-3-0 \
  libgl1 \
  wget \
  unzip \
  && rm -rf /var/lib/apt/lists/*

COPY pom.xml .

COPY src ./src

RUN mvn clean package -DskipTests

CMD ["mvn", "javafx:run"]
