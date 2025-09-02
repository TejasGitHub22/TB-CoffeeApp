#!/bin/bash
echo "🚀 Starting Java Spring Boot Backend Simulator..."
echo "📡 Backend will run on http://localhost:8080"
echo "🔗 API endpoints will be available at http://localhost:8080/api"
echo ""

cd "/workspace/backend-1 - Copy"

# Check if Maven wrapper exists
if [ -f "./mvnw" ]; then
    echo "Using Maven wrapper..."
    ./mvnw spring-boot:run
else
    echo "Using system Maven..."
    mvn spring-boot:run
fi