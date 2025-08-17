FROM openjdk:8u151-jdk-alpine3.7 AS base

# Install Bash
RUN apk add --no-cache bash libc6-compat

FROM base AS builder

# Install Maven
RUN apk add --no-cache maven

# Copy resources
WORKDIR /app
COPY . .
RUN mvn clean package

FROM base

# Copy the jar
WORKDIR /app
COPY --from=builder /app/target/flink-ecommerce-java-1.0-SNAPSHOT-jar-with-dependencies.jar flink-ecommerce-java.jar

# Run the jar
CMD java -Xmx512m -jar flink-ecommerce-java.jar