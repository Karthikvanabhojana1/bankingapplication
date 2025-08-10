#!/bin/bash

# API Gateway Startup Script
# This script starts the API Gateway with proper configuration and health checks

set -e

echo "🚀 Starting Banking Application API Gateway..."

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 17 or higher."
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "❌ Java 17 or higher is required. Current version: $JAVA_VERSION"
    exit 1
fi

echo "✅ Java version: $(java -version 2>&1 | head -n 1)"

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed. Please install Maven 3.6 or higher."
    exit 1
fi

echo "✅ Maven version: $(mvn -version | head -n 1)"

# Set environment variables
export JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC"
export SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-"default"}

echo "🔧 Environment Configuration:"
echo "   - JAVA_OPTS: $JAVA_OPTS"
echo "   - SPRING_PROFILES_ACTIVE: $SPRING_PROFILES_ACTIVE"
echo "   - Server Port: ${SERVER_PORT:-8080}"

# Create logs directory if it doesn't exist
mkdir -p logs

# Clean and build the project
echo "🔨 Building the project..."
mvn clean compile -q

# Check if build was successful
if [ $? -ne 0 ]; then
    echo "❌ Build failed. Please check the errors above."
    exit 1
fi

echo "✅ Build successful!"

# Start the application
echo "🚀 Starting API Gateway..."
echo "📊 Monitoring endpoints will be available at:"
echo "   - Health: http://localhost:${SERVER_PORT:-8080}/actuator/health"
echo "   - Metrics: http://localhost:${SERVER_PORT:-8080}/actuator/metrics"
echo "   - Prometheus: http://localhost:${SERVER_PORT:-8080}/actuator/prometheus"
echo "   - Swagger UI: http://localhost:${SERVER_PORT:-8080}/swagger-ui.html"
echo "   - API Docs: http://localhost:${SERVER_PORT:-8080}/api-docs"
echo ""

# Start the application in the background
mvn spring-boot:run > logs/api-gateway.log 2>&1 &
APP_PID=$!

echo "📝 Application started with PID: $APP_PID"
echo "📋 Logs are being written to: logs/api-gateway.log"
echo ""

# Wait a moment for the application to start
sleep 10

# Check if the application is running
if ps -p $APP_PID > /dev/null; then
    echo "✅ API Gateway is running successfully!"
    echo ""
    echo "🔍 To monitor the application:"
    echo "   - View logs: tail -f logs/api-gateway.log"
    echo "   - Stop application: kill $APP_PID"
    echo "   - Check health: curl http://localhost:${SERVER_PORT:-8080}/actuator/health"
    echo ""
    echo "🎉 API Gateway is ready to handle requests!"
else
    echo "❌ Failed to start API Gateway. Check logs for details."
    exit 1
fi

# Function to handle shutdown
cleanup() {
    echo ""
    echo "🛑 Shutting down API Gateway..."
    if ps -p $APP_PID > /dev/null; then
        kill $APP_PID
        echo "✅ API Gateway stopped."
    fi
    exit 0
}

# Set up signal handlers
trap cleanup SIGINT SIGTERM

# Keep the script running
echo "⏳ Press Ctrl+C to stop the API Gateway..."
wait $APP_PID
