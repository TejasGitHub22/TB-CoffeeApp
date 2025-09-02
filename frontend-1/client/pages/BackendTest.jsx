import React, { useState, useEffect } from 'react';
import { apiClient } from '@/lib/api';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Separator } from '@/components/ui/separator';

export default function BackendTest() {
  const [connectionStatus, setConnectionStatus] = useState('checking');
  const [machines, setMachines] = useState([]);
  const [metrics, setMetrics] = useState(null);
  const [facilities, setFacilities] = useState([]);
  const [loading, setLoading] = useState(false);
  const [lastUpdate, setLastUpdate] = useState(null);

  const checkBackendConnection = async () => {
    try {
      setLoading(true);
      const response = await fetch('http://localhost:8080/api/health');
      if (response.ok) {
        const data = await response.json();
        setConnectionStatus('connected');
        console.log('✅ Backend health check passed:', data);
        return true;
      } else {
        setConnectionStatus('error');
        return false;
      }
    } catch (error) {
      console.error('❌ Backend connection failed:', error);
      setConnectionStatus('error');
      return false;
    } finally {
      setLoading(false);
    }
  };

  const loadSimulatedData = async () => {
    try {
      setLoading(true);
      
      // Load machines from simulator
      const machinesData = await apiClient.getMachines();
      setMachines(Array.isArray(machinesData) ? machinesData : []);
      console.log('📊 Loaded simulated machines:', machinesData);

      // Load system metrics
      const metricsData = await apiClient.getSystemMetrics();
      setMetrics(metricsData);
      console.log('📈 Loaded system metrics:', metricsData);

      // Load facilities
      const facilitiesData = await apiClient.getFacilities();
      setFacilities(facilitiesData);
      console.log('🏢 Loaded facilities:', facilitiesData);

      setLastUpdate(new Date().toLocaleTimeString());
    } catch (error) {
      console.error('Failed to load simulated data:', error);
    } finally {
      setLoading(false);
    }
  };

  const updateMachineSupplies = async (machineId) => {
    try {
      const newSupplies = {
        water: Math.floor(Math.random() * 100),
        milk: Math.floor(Math.random() * 100),
        coffeeBeans: Math.floor(Math.random() * 100),
        sugar: Math.floor(Math.random() * 100)
      };
      
      await apiClient.updateSupplies(machineId, newSupplies);
      console.log(`✅ Updated supplies for machine ${machineId}`);
      
      // Reload data to see changes
      await loadSimulatedData();
    } catch (error) {
      console.error('Failed to update supplies:', error);
    }
  };

  useEffect(() => {
    checkBackendConnection();
  }, []);

  useEffect(() => {
    if (connectionStatus === 'connected') {
      loadSimulatedData();
    }
  }, [connectionStatus]);

  const getStatusColor = (status) => {
    switch (status) {
      case 'operational': return 'bg-green-500';
      case 'maintenance': return 'bg-yellow-500';
      case 'offline': return 'bg-red-500';
      default: return 'bg-gray-500';
    }
  };

  return (
    <div className="container mx-auto p-6 space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-3xl font-bold">🔗 Backend Connection Test</h1>
        <Button onClick={checkBackendConnection} disabled={loading}>
          {loading ? 'Checking...' : 'Test Connection'}
        </Button>
      </div>

      {/* Connection Status */}
      <Card>
        <CardHeader>
          <CardTitle>Connection Status</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="flex items-center space-x-2">
            <div className={`w-3 h-3 rounded-full ${
              connectionStatus === 'connected' ? 'bg-green-500' : 
              connectionStatus === 'error' ? 'bg-red-500' : 'bg-yellow-500'
            }`} />
            <span className="font-medium">
              {connectionStatus === 'connected' ? '✅ Connected to Backend (Port 8080)' :
               connectionStatus === 'error' ? '❌ Backend Connection Failed' : 
               '🔄 Checking Connection...'}
            </span>
          </div>
          {lastUpdate && (
            <p className="text-sm text-muted-foreground mt-2">
              Last updated: {lastUpdate}
            </p>
          )}
        </CardContent>
      </Card>

      {connectionStatus === 'connected' && (
        <>
          {/* System Metrics */}
          {metrics && (
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center space-x-2">
                  <BarChart3 className="w-5 h-5" />
                  <span>System Metrics</span>
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                  <div className="text-center">
                    <div className="text-2xl font-bold text-blue-600">{metrics.totalMachines}</div>
                    <div className="text-sm text-muted-foreground">Total Machines</div>
                  </div>
                  <div className="text-center">
                    <div className="text-2xl font-bold text-green-600">{metrics.operationalMachines}</div>
                    <div className="text-sm text-muted-foreground">Operational</div>
                  </div>
                  <div className="text-center">
                    <div className="text-2xl font-bold text-yellow-600">{metrics.maintenanceMachines}</div>
                    <div className="text-sm text-muted-foreground">Maintenance</div>
                  </div>
                  <div className="text-center">
                    <div className="text-2xl font-bold text-red-600">{metrics.offlineMachines}</div>
                    <div className="text-sm text-muted-foreground">Offline</div>
                  </div>
                </div>
                {metrics.cpuUsage && (
                  <Separator className="my-4" />
                )}
                {metrics.cpuUsage && (
                  <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                    <div>
                      <div className="text-sm font-medium">CPU Usage</div>
                      <div className="text-lg font-bold">{metrics.cpuUsage}%</div>
                    </div>
                    <div>
                      <div className="text-sm font-medium">Memory Usage</div>
                      <div className="text-lg font-bold">{metrics.memoryUsage}%</div>
                    </div>
                    <div>
                      <div className="text-sm font-medium">Network Latency</div>
                      <div className="text-lg font-bold">{metrics.networkLatency}ms</div>
                    </div>
                  </div>
                )}
              </CardContent>
            </Card>
          )}

          {/* Facilities */}
          {facilities.length > 0 && (
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center space-x-2">
                  <Building className="w-5 h-5" />
                  <span>Facilities</span>
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                  {facilities.map((facility) => (
                    <div key={facility.id} className="p-4 border rounded-lg">
                      <h3 className="font-semibold">{facility.name}</h3>
                      <p className="text-sm text-muted-foreground">{facility.location}</p>
                      <p className="text-sm font-medium mt-2">
                        {facility.machineCount} machines
                      </p>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>
          )}

          {/* Simulated Machines */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center justify-between">
                <div className="flex items-center space-x-2">
                  <Coffee className="w-5 h-5" />
                  <span>Simulated Coffee Machines</span>
                </div>
                <Button onClick={loadSimulatedData} disabled={loading} size="sm">
                  {loading ? 'Loading...' : 'Refresh Data'}
                </Button>
              </CardTitle>
              <CardDescription>
                Live data from backend simulator ({machines.length} machines)
              </CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {machines.map((machine) => (
                  <div key={machine.id} className="border rounded-lg p-4">
                    <div className="flex items-center justify-between mb-3">
                      <div className="flex items-center space-x-3">
                        <div className={`w-3 h-3 rounded-full ${getStatusColor(machine.status)}`} />
                        <div>
                          <h3 className="font-semibold">{machine.id}</h3>
                          <p className="text-sm text-muted-foreground">
                            {machine.office} - {machine.floor}
                          </p>
                        </div>
                      </div>
                      <div className="flex items-center space-x-2">
                        <Badge variant={machine.status === 'operational' ? 'default' : 'secondary'}>
                          {machine.status}
                        </Badge>
                        <Button 
                          size="sm" 
                          variant="outline"
                          onClick={() => updateMachineSupplies(machine.id)}
                        >
                          Refill
                        </Button>
                      </div>
                    </div>
                    
                    {/* Supply Levels */}
                    <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
                      {Object.entries(machine.supplies || {}).map(([supply, level]) => (
                        supply !== 'coffee' && ( // Skip 'coffee' to avoid duplication with 'coffeeBeans'
                          <div key={supply} className="text-center">
                            <div className="text-xs font-medium text-muted-foreground mb-1">
                              {supply.charAt(0).toUpperCase() + supply.slice(1)}
                            </div>
                            <div className="relative">
                              <div className="w-full bg-gray-200 rounded-full h-2">
                                <div 
                                  className={`h-2 rounded-full transition-all duration-300 ${
                                    level < 20 ? 'bg-red-500' : 
                                    level < 40 ? 'bg-yellow-500' : 'bg-green-500'
                                  }`}
                                  style={{ width: `${level}%` }}
                                />
                              </div>
                              <div className="text-xs font-bold mt-1">{level}%</div>
                            </div>
                          </div>
                        )
                      ))}
                    </div>

                    {/* Usage Stats */}
                    {machine.usage && (
                      <div className="mt-3 pt-3 border-t">
                        <div className="flex items-center justify-between text-sm">
                          <span>Daily Cups: <strong>{machine.usage.dailyCups}</strong></span>
                          <span>Weekly Cups: <strong>{machine.usage.weeklyCups}</strong></span>
                        </div>
                      </div>
                    )}

                    {/* Alerts */}
                    {machine.alerts && machine.alerts.length > 0 && (
                      <div className="mt-3 pt-3 border-t">
                        <div className="text-sm font-medium text-red-600 mb-2">
                          ⚠️ {machine.alerts.length} Alert(s)
                        </div>
                        <div className="space-y-1">
                          {machine.alerts.map((alert, index) => (
                            <div key={index} className="text-xs bg-red-50 text-red-700 p-2 rounded">
                              {alert.message}
                            </div>
                          ))}
                        </div>
                      </div>
                    )}
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        </>
      )}

      {connectionStatus === 'error' && (
        <Card>
          <CardHeader>
            <CardTitle className="text-red-600">❌ Backend Connection Failed</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-2">
              <p>Unable to connect to the backend simulator at <code>http://localhost:8080</code></p>
              <p className="text-sm text-muted-foreground">
                Make sure the Java Spring Boot backend is running:
              </p>
              <div className="bg-gray-100 p-3 rounded text-sm font-mono">
                cd "/workspace/backend-1 - Copy"<br/>
                ./mvnw spring-boot:run
              </div>
            </div>
          </CardContent>
        </Card>
      )}
    </div>
  );
}