#!/bin/bash

echo "🐳 Testing Banking Application with Docker"
echo "=========================================="

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker first."
    exit 1
fi

# Check if docker compose is available
if ! docker compose version &> /dev/null; then
    echo "❌ docker compose is not available. Please check Docker installation."
    exit 1
fi

echo "✅ Docker environment check passed"

# Build and start services
echo ""
echo "🔨 Building Docker images..."
docker compose build

echo ""
echo "🚀 Starting all services..."
docker compose up -d

echo ""
echo "⏳ Waiting for services to start up..."
sleep 45

echo ""
echo "📊 Service Status:"
docker compose ps

echo ""
echo "🔍 Health Checks:"
echo "Eureka Server:"
curl -s http://localhost:8761/actuator/health | jq . 2>/dev/null || echo "Failed"

echo ""
echo "🌐 Application URLs:"
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
echo "📝 Commands:"
echo "   View logs: docker compose logs -f [service-name]"
echo "   Stop all: docker compose down"
echo "   View status: docker compose ps"
