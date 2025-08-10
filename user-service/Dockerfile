FROM openjdk:8-jre-alpine

WORKDIR /app

# Copy the JAR file
COPY target/user-service-1.0.0.jar app.jar

# Expose the port
EXPOSE 8081

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8081/actuator/health || exit 1

# Run the application
CMD ["java", "-jar", "app.jar"]
