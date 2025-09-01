package com.coffeemachine.simulator.service;

import com.coffeemachine.simulator.model.CoffeeMachine;
import com.coffeemachine.simulator.model.MachineData;
import com.coffeemachine.simulator.repository.CoffeeMachineRepository;
import com.coffeemachine.simulator.repository.MachineDataRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PreDestroy;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.UUID;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class MachineSimulatorService {

    private final CoffeeMachineRepository coffeeMachineRepository;
    private final MachineDataRepository machineDataRepository;
    private final ObjectMapper objectMapper;
    
    //declaring the mqtt client...
    MqttClient mqttClient;
    
    //options object...
    MqttConnectOptions options;

    // MQTT Config from application.properties
    @Value("${mqtt.broker.url}")
    private String brokerUrl;
    
    private String clientId = "mqtt-simulator-client" + UUID.randomUUID();
    
    @Value("${mqtt.username}")
    private String username = "your-hivemq-username";
    
    @Value("${mqtt.password}")
    private String password = "your-hivemq-password";

    public MachineSimulatorService(CoffeeMachineRepository coffeeMachineRepository,
                                   MachineDataRepository machineDataRepository,
                                   ObjectMapper objectMapper) {
        this.coffeeMachineRepository = coffeeMachineRepository;
        this.machineDataRepository = machineDataRepository;
        this.objectMapper = objectMapper;
    }

    public void simulateMachines() throws JsonProcessingException{
        Integer maxId = coffeeMachineRepository.findMaxId();
        if (maxId == null) return;
        
     // connect to HiveMQ
        try {
        	mqttClient = new MqttClient(brokerUrl, clientId, new MemoryPersistence());
            options = new MqttConnectOptions();
            options.setUserName(username);
            options.setPassword(password.toCharArray());
            options.setCleanSession(true);
            options.setAutomaticReconnect(true);
            options.setKeepAliveInterval(30000);
            
            mqttClient.connect(options);
        }
        catch(MqttException e) {
        	e.printStackTrace();
        }

        while(true) {
        	for (int id = 1; id <= maxId; id++) {
                Optional<CoffeeMachine> optionalMachine = coffeeMachineRepository.findById(id);

                if (!optionalMachine.isPresent()) continue;

                CoffeeMachine machine = optionalMachine.get();

                // Check if machine is active
                if (!machine.getIsActive()) continue;

                // Try to brew
                String brewType = tryBrew(machine);

                // Update lastUpdate timestamp
//                machine.setLastUpdate(LocalDateTime.now());
//                coffeeMachineRepository.save(machine);

                // Prepare message
                Map<String, Object> messageMap = new LinkedHashMap<>();
                messageMap.put("machineId", machine.getId());
                messageMap.put("facilityId", machine.getFacilityId());
                messageMap.put("status", machine.getStatus());
                messageMap.put("brewType", brewType);
                messageMap.put("temperature", machine.getTemperature());
                messageMap.put("waterLevel", machine.getWaterLevel());
                messageMap.put("milkLevel", machine.getMilkLevel());
                messageMap.put("beansLevel", machine.getBeansLevel());
                messageMap.put("sugarLevel", machine.getSugarLevel());
                messageMap.put("timeStamp", LocalDateTime.now());

                try {
                    if (mqttClient.isConnected()) {
                    	// Convert to JSON
                		String jsonMessage = objectMapper.writeValueAsString(messageMap);
                    	
                    	String topic = "coffeemachine/" + machine.getId() + "/data";
                        MqttMessage mqttMessage = new MqttMessage(jsonMessage.getBytes());
                        mqttMessage.setQos(1);
                        mqttClient.publish(topic, mqttMessage);
                        
                		// Save message to DB as string
                        MachineData msg = new MachineData(machine.getId(), jsonMessage);
                        machineDataRepository.save(msg);
                        System.out.println("✅ Published to topic: " + topic + " → " + jsonMessage);
                    } else {
                        System.err.println("MQTT client is not connected. Skipping publish.");
                    }

                } catch (MqttException e) {
                    e.printStackTrace();
                }
        	}
        	
        	try {
                Thread.sleep(5000); // Pause for 5 seconds
            } catch (InterruptedException e) {
                System.err.println("Thread interrupted. Exiting loop.");
                break; // Optional: exit loop if interrupted
            }
        }
        
    }

    private String tryBrew(CoffeeMachine machine) {
        // Brew recipes
        Map<String, double[]> recipes = new HashMap<>();
        recipes.put("AMERICANO", new double[]{6.0, 0.0, 4.0, 0.5});
        recipes.put("LATTE", new double[]{2.0, 6.0, 4.0, 0.5});
        recipes.put("BLACK_COFFEE", new double[]{8.0, 0.0, 4.0, 0.0});
        recipes.put("CAPPUCCINO", new double[]{3.0, 3.0, 4.0, 0.5});

        List<String> brewTypes = new ArrayList<>(recipes.keySet());
        String selected = brewTypes.get(new Random().nextInt(brewTypes.size()));

        double[] recipe = recipes.get(selected);

        // Check resources
        if (machine.getWaterLevel() >= recipe[0] &&
            machine.getMilkLevel() >= recipe[1] &&
            machine.getBeansLevel() >= recipe[2]&&
            machine.getSugarLevel() >= recipe[3]) {

            // Deduct resources
            machine.setWaterLevel(machine.getWaterLevel() - recipe[0]);
            machine.setMilkLevel(machine.getMilkLevel() - recipe[1]);
            machine.setBeansLevel(machine.getBeansLevel() - recipe[2]);
            machine.setSugarLevel(machine.getSugarLevel() - recipe[3]);
            machine.setTemperature(90.0 + new Random().nextDouble()*10);

            return selected;
        }

        return "None"; // Not enough resources
    }
    
//    @PreDestroy
//    public void shutdown() throws MqttException {
//        if (mqttClient != null && mqttClient.isConnected()) {
//            mqttClient.disconnect();
//            mqttClient.close();
//        }
//    }
}
