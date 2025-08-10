#!/bin/bash

echo "Starting Internet Banking Distributed System..."
echo "=============================================="

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "Error: Docker is not running. Please start Docker first."
    exit 1
fi

# Start infrastructure services
echo "Starting infrastructure services (PostgreSQL, Prometheus, Grafana)..."
docker-compose up -d

# Wait for PostgreSQL to be ready
echo "Waiting for PostgreSQL to be ready..."
sleep 10

# Build the project
echo "Building the project..."
mvn clean install -DskipTests

# Start Eureka Server
echo "Starting Eureka Server..."
mvn spring-boot:run -pl eureka-server &
EUREKA_PID=$!
sleep 15

# Start API Gateway
echo "Starting API Gateway..."
mvn spring-boot:run -pl api-gateway &
GATEWAY_PID=$!
sleep 10

# Start User Service
echo "Starting User Service..."
mvn spring-boot:run -pl user-service &
USER_PID=$!
sleep 10

# Start Account Service
echo "Starting Account Service..."
mvn spring-boot:run -pl account-service &
ACCOUNT_PID=$!
sleep 10

# Start Transaction Service
echo "Starting Transaction Service..."
mvn spring-boot:run -pl transaction-service &
TRANSACTION_PID=$!
sleep 10

# Start Notification Service
echo "Starting Notification Service..."
mvn spring-boot:run -pl notification-service &
NOTIFICATION_PID=$!
sleep 10

# Start Payment Service
echo "Starting Payment Service..."
mvn spring-boot:run -pl payment-service &
PAYMENT_PID=$!
sleep 10

echo ""
echo "All services are starting up..."
echo "=============================================="
echo "Service URLs:"
echo "Eureka Dashboard: http://localhost:8761"
echo "API Gateway: http://localhost:8080"
echo "User Service: http://localhost:8081"
echo "Account Service: http://localhost:8082"
echo "Transaction Service: http://localhost:8083"
echo "Notification Service: http://localhost:8084"
echo "Payment Service: http://localhost:8085"
echo "Prometheus: http://localhost:9090"
echo "Grafana: http://localhost:3000 (admin/admin)"
echo "=============================================="
echo ""
echo "To stop all services, run: ./stop-services.sh"
echo ""

# Wait for user input to stop services
read -p "Press Enter to stop all services..."

# Stop all services
echo "Stopping all services..."
kill $EUREKA_PID $GATEWAY_PID $USER_PID $ACCOUNT_PID $TRANSACTION_PID $NOTIFICATION_PID $PAYMENT_PID

# Stop infrastructure services
echo "Stopping infrastructure services..."
docker-compose down

echo "All services stopped."
