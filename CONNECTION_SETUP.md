# 🔗 Backend Simulator & Frontend Connection Setup

## Overview
This setup connects your Java Spring Boot backend simulator with your React frontend to display live simulated coffee machine data.

## 📁 Project Structure
```
/workspace/
├── backend-1 - Copy/          # Java Spring Boot Backend
├── frontend-1/                # React Frontend with Vite
├── start-backend.sh           # Backend startup script
├── start-frontend.sh          # Frontend startup script
└── CONNECTION_SETUP.md        # This file
```

## 🚀 Quick Start

### 1. Start Backend Simulator
```bash
./start-backend.sh
```
- Runs on: `http://localhost:8080`
- API endpoints: `http://localhost:8080/api/*`

### 2. Start Frontend
```bash
./start-frontend.sh
```
- Runs on: `http://localhost:5173`
- Connects to backend automatically

### 3. View Simulated Data
- **Main Dashboard**: `http://localhost:5173/dashboard` (requires login)
- **Backend Test Page**: `http://localhost:5173/backend-test` (no login required)

## 🔧 What Was Changed

### Backend Changes (`backend-1 - Copy/`)
1. **SimulatorController.java** - New controller with simulated coffee machine data
   - `/api/simulator/machines` - Get all simulated machines
   - `/api/simulator/machines/{id}` - Get specific machine
   - `/api/simulator/machines/{id}/supplies` - Update machine supplies
   - `/api/simulator/metrics` - Get system metrics
   - `/api/simulator/facilities` - Get facility information

2. **CorsConfig.java** - CORS configuration for frontend connections
   - Allows requests from `localhost:5173` and `localhost:3000`
   - Enables credentials and all HTTP methods

### Frontend Changes (`frontend-1/`)
1. **.env** - Updated environment configuration
   - `VITE_API_BASE_URL=http://localhost:8080/api`
   - `VITE_STANDALONE_MODE=false` (connects to backend)

2. **lib/api.js** - Enhanced API client
   - Added simulator endpoint calls
   - Fallback to local storage if backend unavailable
   - New methods: `getSystemMetrics()`, `getFacilities()`

3. **pages/BackendTest.jsx** - New test page
   - Real-time connection status
   - Live simulated data display
   - Interactive supply refill testing

4. **pages/CorporateDashboard.jsx** - Updated to use backend data
   - Loads machines from backend simulator first
   - Falls back to local storage if backend unavailable

## 📊 Simulated Data Features

### Coffee Machines
- 5 simulated machines across 3 offices
- Real-time supply level changes
- Status simulation (operational/maintenance/offline)
- Usage statistics (daily/weekly cups)
- Alert generation for low supplies

### System Metrics
- Total/operational/maintenance/offline machine counts
- CPU, memory, and network metrics
- System health indicators

### Facilities
- Multiple office locations
- Machine count per facility
- Location information

## 🧪 Testing the Connection

1. **Start both services** using the startup scripts
2. **Visit the test page**: `http://localhost:5173/backend-test`
3. **Check connection status** - should show "✅ Connected to Backend"
4. **View live data** - machines, metrics, and facilities from backend
5. **Test interactions** - click "Refill" buttons to update supplies

## 🔍 Debugging

### Backend Issues
- Check Java version (requires Java 17+)
- Verify port 8080 is available
- Check console for Spring Boot startup messages

### Frontend Issues
- Verify Node.js is installed
- Check if port 5173 is available
- Look for CORS errors in browser console

### Connection Issues
- Ensure both services are running
- Check browser network tab for API calls
- Verify CORS configuration allows your frontend origin

## 🎯 Next Steps

1. **Login to main dashboard** with demo credentials
2. **View integrated data** in the corporate dashboard
3. **Test real-time updates** by refreshing data
4. **Customize simulation** by modifying SimulatorController.java

## 📝 API Endpoints

### Simulator Endpoints
- `GET /api/simulator/machines` - All machines
- `GET /api/simulator/machines/{id}` - Specific machine
- `PUT /api/simulator/machines/{id}/supplies` - Update supplies
- `GET /api/simulator/metrics` - System metrics
- `GET /api/simulator/facilities` - Facility data

### Health Check
- `GET /api/health` - Backend health status
- `GET /api/health/ping` - Simple ping endpoint

The backend simulator now provides realistic coffee machine data that updates in real-time, and the frontend displays this data with a clean, interactive interface.