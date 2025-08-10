#!/bin/bash

echo "🚀 Building and Running Banking Application with Docker"
echo "========================================================"

# Function to build a service
build_service() {
    local service_name=$1
    local service_dir=$2
    
    echo "🔨 Building $service_name..."
    cd "$service_dir"
    
    # Clean and compile
    mvn clean compile -q
    if [ $? -ne 0 ]; then
        echo "❌ Failed to compile $service_name"
        exit 1
    fi
    
    # Package
    mvn package -DskipTests -q
    if [ $? -ne 0 ]; then
        echo "❌ Failed to package $service_name"
        exit 1
    fi
    
    echo "✅ $service_name built successfully"
    cd ..
}

# Build all services
echo "📦 Building all microservices..."
build_service "Common Module" "common"
build_service "Eureka Server" "eureka-server"
build_service "API Gateway" "api-gateway"
build_service "User Service" "user-service"
build_service "Account Service" "account-service"
build_service "Transaction Service" "transaction-service"
build_service "Payment Service" "payment-service"
build_service "Notification Service" "notification-service"

echo ""
echo "🐳 Building Docker images..."
docker compose build

echo ""
echo "🚀 Starting all services with Docker Compose..."
docker compose up -d

echo ""
echo "⏳ Waiting for services to start up..."
sleep 30

echo ""
echo "📊 Checking service status..."
docker compose ps

echo ""
echo "🔍 Checking Eureka Server..."
curl -s http://localhost:8761/actuator/health | jq . 2>/dev/null || echo "Eureka Server health check failed"

echo ""
echo "🌐 Banking Application URLs:"
echo "   Eureka Server: http://localhost:8761"
echo "   API Gateway:  http://localhost:8080"
echo "   User Service: http://localhost:8081"
echo "   Account Service: http://localhost:8082"
echo "   Transaction Service: http://localhost:8083"
echo "   Payment Service: http://localhost:8084"
echo "   Notification Service: http://localhost:8085"
echo "   Prometheus: http://localhost:9090"
echo "   Grafana: http://localhost:3000 (admin/admin)"

echo ""
echo "📝 Useful commands:"
echo "   View logs: docker compose logs -f [service-name]"
echo "   Stop all: docker compose down"
echo "   Restart: docker compose restart [service-name]"
echo "   View status: docker compose ps"
