# Multi-stage Dockerfile para optimización de imagen y seguridad

# Stage 1: Build application
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

# Copy gradle files for caching layers
COPY gradle gradle
COPY gradlew build.gradle.kts settings.gradle.kts ./
RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon || true

# Copy source code and compile jar
COPY src src
RUN ./gradlew bootJar --no-daemon -x test

# Stage 2: Runtime image lightweight & secure
FROM eclipse-temurin:17-jre-alpine AS runner
WORKDIR /app

# Create non-root user and group
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy jar from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar
RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
