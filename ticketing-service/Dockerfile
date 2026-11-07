# Stage 1: build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B package -DskipTests

# Stage 2: runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S app && adduser -S app -G app \
    && mkdir /data && chown app:app /data
COPY --from=build /app/target/*.jar app.jar
USER app
ENV TICKETING_DB_PATH=/data/ticketing
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]
