#!/bin/bash

echo "🔨 Building JAR files for Docker"
echo "================================"

# Function to build a service
build_service() {
    local service_name=$1
    local service_dir=$2
    
    echo "🔨 Building $service_name..."
    cd "$service_dir"
    
    # Clean and package
    mvn clean package -DskipTests -q
    if [ $? -ne 0 ]; then
        echo "❌ Failed to build $service_name"
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
echo "🎉 All JAR files built successfully!"
echo "Now you can run: ./docker-test.sh"
