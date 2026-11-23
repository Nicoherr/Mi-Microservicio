# Multi-stage Dockerfile para optimización de imagen y seguridad

# Stage 1: Build application usando imagen base con Gradle instalado
FROM gradle:8.14-jdk17-alpine AS builder
WORKDIR /app

# Copy gradle configuration files first for caching
COPY build.gradle.kts settings.gradle.kts ./
RUN gradle dependencies --no-daemon || true

# Copy source code and build bootJar
COPY src src
RUN gradle bootJar --no-daemon -x test

# Stage 2: Runtime image lightweight & secure
FROM eclipse-temurin:25-jre-alpine AS runner
WORKDIR /app

# Create non-root user and group
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy compiled jar from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar
RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
