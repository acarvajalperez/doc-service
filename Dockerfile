# Stage 1: Build logic
FROM maven:3.9.6-eclipse-temurin-17-focal AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B || true
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Minimal runtime image
FROM eclipse-temurin:17-jre-focal
WORKDIR /app

# Font configuration for PDF generation (XWPFConverter)
# install ttf-mscorefonts-installer to prevent bad rendering/deformations of the word texts.
RUN apt-get update && \
    apt-get install -y --no-install-recommends fontconfig cabextract wget && \
    echo "ttf-mscorefonts-installer msttcorefonts/accepted-mscorefonts-eula select true" | debconf-set-selections && \
    apt-get install -y ttf-mscorefonts-installer && \
    fc-cache -f -v && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

COPY --from=builder /app/target/*.jar app.jar

ENV PORT=8080
EXPOSE ${PORT}

ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT} -jar app.jar"]
