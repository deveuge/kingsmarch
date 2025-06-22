# Stage 1: Build
FROM maven:3.8.5-openjdk-17 AS builder

# Set working directory inside container
WORKDIR /app

# Copy everything into container
COPY . .

# Build only the 'api' module and skip tests
RUN mvn clean package -pl api -am -DskipTests

# Stage 2: Run
FROM openjdk:17.0.1-jdk-slim

# Set working directory in runtime container
WORKDIR /app

# Copy the generated JAR from the builder stage
COPY --from=builder /app/api/target/api*.jar kingsmarch.jar

# Expose application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "kingsmarch.jar"]
