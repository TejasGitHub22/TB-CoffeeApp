#!/bin/bash
echo "🌐 Starting React Frontend..."
echo "📱 Frontend will run on http://localhost:5173"
echo "🔗 Connecting to backend at http://localhost:8080"
echo ""

cd "/workspace/frontend-1"

# Install dependencies if node_modules doesn't exist
if [ ! -d "node_modules" ]; then
    echo "📦 Installing dependencies..."
    npm install
fi

echo "🚀 Starting development server..."
npm run dev