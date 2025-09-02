package com.coffee.coffeeApp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/api/simulator")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class SimulatorController {

    // Simulated coffee machine data
    private List<Map<String, Object>> simulatedMachines = new ArrayList<>();
    
    public SimulatorController() {
        initializeSimulatedData();
    }
    
    private void initializeSimulatedData() {
        // Create simulated coffee machines with realistic data
        simulatedMachines.add(createSimulatedMachine("CM001", "Main Office", "Floor 1", "operational"));
        simulatedMachines.add(createSimulatedMachine("CM002", "Main Office", "Floor 2", "maintenance"));
        simulatedMachines.add(createSimulatedMachine("CM003", "Branch Office", "Floor 1", "operational"));
        simulatedMachines.add(createSimulatedMachine("CM004", "Remote Office", "Floor 1", "offline"));
        simulatedMachines.add(createSimulatedMachine("CM005", "Main Office", "Floor 3", "operational"));
    }
    
    private Map<String, Object> createSimulatedMachine(String machineId, String office, String floor, String status) {
        Map<String, Object> machine = new HashMap<>();
        machine.put("id", machineId);
        machine.put("machineId", machineId);
        machine.put("office", office);
        machine.put("floor", floor);
        machine.put("status", status);
        machine.put("powerStatus", status.equals("offline") ? "offline" : "online");
        machine.put("electricityStatus", "available");
        
        // Simulate supply levels
        Map<String, Integer> supplies = new HashMap<>();
        supplies.put("water", getRandomLevel());
        supplies.put("milk", getRandomLevel());
        supplies.put("coffeeBeans", getRandomLevel());
        supplies.put("coffee", supplies.get("coffeeBeans")); // Same as coffeeBeans
        supplies.put("sugar", getRandomLevel());
        machine.put("supplies", supplies);
        
        // Simulate usage data
        Map<String, Integer> usage = new HashMap<>();
        usage.put("dailyCups", ThreadLocalRandom.current().nextInt(0, 50));
        usage.put("weeklyCups", ThreadLocalRandom.current().nextInt(50, 300));
        machine.put("usage", usage);
        
        // Simulate maintenance info
        Map<String, String> maintenance = new HashMap<>();
        maintenance.put("filterStatus", getRandomFilterStatus());
        maintenance.put("cleaningStatus", getRandomCleaningStatus());
        machine.put("maintenance", maintenance);
        
        // Simulate alerts
        List<Map<String, Object>> alerts = new ArrayList<>();
        if (supplies.get("water") < 20) {
            alerts.add(createAlert("low_supply", "Water level is low", "high"));
        }
        if (supplies.get("coffeeBeans") < 15) {
            alerts.add(createAlert("low_supply", "Coffee beans level is critical", "critical"));
        }
        machine.put("alerts", alerts);
        
        machine.put("lastUpdated", LocalDateTime.now().toString());
        machine.put("updatedBy", "simulator");
        
        return machine;
    }
    
    private Map<String, Object> createAlert(String type, String message, String priority) {
        Map<String, Object> alert = new HashMap<>();
        alert.put("type", type);
        alert.put("message", message);
        alert.put("priority", priority);
        alert.put("timestamp", LocalDateTime.now().toString());
        return alert;
    }
    
    private int getRandomLevel() {
        return ThreadLocalRandom.current().nextInt(5, 100);
    }
    
    private String getRandomFilterStatus() {
        String[] statuses = {"good", "needs_replacement", "critical"};
        return statuses[ThreadLocalRandom.current().nextInt(statuses.length)];
    }
    
    private String getRandomCleaningStatus() {
        String[] statuses = {"clean", "needs_cleaning", "overdue"};
        return statuses[ThreadLocalRandom.current().nextInt(statuses.length)];
    }

    @GetMapping("/machines")
    public ResponseEntity<List<Map<String, Object>>> getAllSimulatedMachines() {
        // Update supply levels randomly to simulate real-time changes
        updateSupplyLevels();
        return ResponseEntity.ok(simulatedMachines);
    }

    @GetMapping("/machines/{id}")
    public ResponseEntity<Map<String, Object>> getSimulatedMachine(@PathVariable String id) {
        Optional<Map<String, Object>> machine = simulatedMachines.stream()
                .filter(m -> m.get("id").equals(id) || m.get("machineId").equals(id))
                .findFirst();
        
        if (machine.isPresent()) {
            // Update this machine's data to simulate real-time changes
            updateSingleMachine(machine.get());
            return ResponseEntity.ok(machine.get());
        }
        
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/machines/{id}")
    public ResponseEntity<Map<String, Object>> updateSimulatedMachine(
            @PathVariable String id, 
            @RequestBody Map<String, Object> updateData) {
        
        Optional<Map<String, Object>> machineOpt = simulatedMachines.stream()
                .filter(m -> m.get("id").equals(id) || m.get("machineId").equals(id))
                .findFirst();
        
        if (machineOpt.isPresent()) {
            Map<String, Object> machine = machineOpt.get();
            
            // Update machine data
            if (updateData.containsKey("status")) {
                machine.put("status", updateData.get("status"));
                machine.put("powerStatus", updateData.get("status").equals("offline") ? "offline" : "online");
            }
            
            if (updateData.containsKey("supplies")) {
                machine.put("supplies", updateData.get("supplies"));
            }
            
            machine.put("lastUpdated", LocalDateTime.now().toString());
            machine.put("updatedBy", "user");
            
            return ResponseEntity.ok(machine);
        }
        
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/machines/{id}/supplies")
    public ResponseEntity<Map<String, String>> updateMachineSupplies(
            @PathVariable String id,
            @RequestBody Map<String, Object> requestBody) {
        
        Optional<Map<String, Object>> machineOpt = simulatedMachines.stream()
                .filter(m -> m.get("id").equals(id) || m.get("machineId").equals(id))
                .findFirst();
        
        if (machineOpt.isPresent()) {
            Map<String, Object> machine = machineOpt.get();
            
            if (requestBody.containsKey("supplies")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> newSupplies = (Map<String, Object>) requestBody.get("supplies");
                @SuppressWarnings("unchecked")
                Map<String, Integer> currentSupplies = (Map<String, Integer>) machine.get("supplies");
                
                // Update supplies
                newSupplies.forEach((key, value) -> {
                    if (value instanceof Number) {
                        currentSupplies.put(key, ((Number) value).intValue());
                    }
                });
                
                // Ensure coffee and coffeeBeans are in sync
                if (currentSupplies.containsKey("coffeeBeans")) {
                    currentSupplies.put("coffee", currentSupplies.get("coffeeBeans"));
                } else if (currentSupplies.containsKey("coffee")) {
                    currentSupplies.put("coffeeBeans", currentSupplies.get("coffee"));
                }
                
                machine.put("lastUpdated", LocalDateTime.now().toString());
                machine.put("updatedBy", "technician");
            }
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Supplies updated successfully");
            response.put("machineId", id);
            return ResponseEntity.ok(response);
        }
        
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getSystemMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        // Calculate aggregate metrics from all machines
        long operationalCount = simulatedMachines.stream()
                .filter(m -> "operational".equals(m.get("status")))
                .count();
        
        long maintenanceCount = simulatedMachines.stream()
                .filter(m -> "maintenance".equals(m.get("status")))
                .count();
        
        long offlineCount = simulatedMachines.stream()
                .filter(m -> "offline".equals(m.get("status")))
                .count();
        
        metrics.put("totalMachines", simulatedMachines.size());
        metrics.put("operationalMachines", operationalCount);
        metrics.put("maintenanceMachines", maintenanceCount);
        metrics.put("offlineMachines", offlineCount);
        metrics.put("systemHealth", operationalCount > maintenanceCount + offlineCount ? "good" : "warning");
        metrics.put("timestamp", LocalDateTime.now().toString());
        
        // Add some random system metrics
        metrics.put("cpuUsage", ThreadLocalRandom.current().nextInt(10, 90));
        metrics.put("memoryUsage", ThreadLocalRandom.current().nextInt(20, 80));
        metrics.put("networkLatency", ThreadLocalRandom.current().nextInt(10, 100));
        
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/facilities")
    public ResponseEntity<List<Map<String, Object>>> getSimulatedFacilities() {
        List<Map<String, Object>> facilities = new ArrayList<>();
        
        Map<String, Object> mainOffice = new HashMap<>();
        mainOffice.put("id", 1);
        mainOffice.put("name", "Main Office");
        mainOffice.put("location", "Downtown");
        mainOffice.put("machineCount", simulatedMachines.stream()
                .filter(m -> "Main Office".equals(m.get("office")))
                .count());
        facilities.add(mainOffice);
        
        Map<String, Object> branchOffice = new HashMap<>();
        branchOffice.put("id", 2);
        branchOffice.put("name", "Branch Office");
        branchOffice.put("location", "Uptown");
        branchOffice.put("machineCount", simulatedMachines.stream()
                .filter(m -> "Branch Office".equals(m.get("office")))
                .count());
        facilities.add(branchOffice);
        
        Map<String, Object> remoteOffice = new HashMap<>();
        remoteOffice.put("id", 3);
        remoteOffice.put("name", "Remote Office");
        remoteOffice.put("location", "Suburbs");
        remoteOffice.put("machineCount", simulatedMachines.stream()
                .filter(m -> "Remote Office".equals(m.get("office")))
                .count());
        facilities.add(remoteOffice);
        
        return ResponseEntity.ok(facilities);
    }

    private void updateSupplyLevels() {
        // Randomly decrease supply levels to simulate usage
        for (Map<String, Object> machine : simulatedMachines) {
            if ("operational".equals(machine.get("status"))) {
                @SuppressWarnings("unchecked")
                Map<String, Integer> supplies = (Map<String, Integer>) machine.get("supplies");
                
                // Randomly decrease supplies
                supplies.forEach((key, value) -> {
                    if (ThreadLocalRandom.current().nextBoolean() && value > 0) {
                        int decrease = ThreadLocalRandom.current().nextInt(1, 3);
                        supplies.put(key, Math.max(0, value - decrease));
                    }
                });
                
                // Keep coffee and coffeeBeans in sync
                if (supplies.containsKey("coffeeBeans")) {
                    supplies.put("coffee", supplies.get("coffeeBeans"));
                }
            }
        }
    }
    
    private void updateSingleMachine(Map<String, Object> machine) {
        // Update timestamp and add some random variation
        machine.put("lastUpdated", LocalDateTime.now().toString());
        
        if ("operational".equals(machine.get("status"))) {
            @SuppressWarnings("unchecked")
            Map<String, Integer> usage = (Map<String, Integer>) machine.get("usage");
            
            // Occasionally increment daily cups
            if (ThreadLocalRandom.current().nextInt(100) < 10) { // 10% chance
                usage.put("dailyCups", usage.get("dailyCups") + 1);
                usage.put("weeklyCups", usage.get("weeklyCups") + 1);
            }
        }
    }
}