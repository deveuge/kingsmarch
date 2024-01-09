FROM maven:3.8.5-openjdk-17 AS builder

# Copy local code to the container image.
COPY . .

# Build a release artifact.
RUN mvn package -DskipTests

# Use AdoptOpenJDK for base image.
FROM openjdk:17.0.1-jdk-slim

# Copy the jar to the production image from the builder stage.
COPY --from=builder /target/kingsmarch-*.war kingsmarch.war

# Run the web service on container startup.
EXPOSE 8080
ENTRYPOINT ["java","-jar","kingsmarch.war"]