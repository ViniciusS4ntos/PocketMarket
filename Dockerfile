# Build stage
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# cache de dependências
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

# código
COPY src src

# build
RUN ./mvnw clean package -DskipTests


# Runtime stage
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]