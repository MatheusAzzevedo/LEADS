# Etapa de build
FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle build

# Etapa de execução
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"] 