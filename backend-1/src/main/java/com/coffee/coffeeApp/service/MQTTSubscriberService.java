/*package com.coffee.coffeeApp.service;

import java.time.LocalDateTime;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import com.coffee.coffeeApp.repository.AlertLogRepository;
import com.coffee.coffeeApp.repository.CoffeeMachineRepository;
import com.coffee.coffeeApp.repository.UsageHistoryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.coffee.coffeeApp.dto.TelemetryDataDto;
import com.coffee.coffeeApp.entity.AlertLog;
import com.coffee.coffeeApp.entity.CoffeeMachine;
import com.coffee.coffeeApp.entity.UsageHistory;

import jakarta.annotation.PostConstruct;

@Service
public class MQTTSubscriberService {

    @Autowired
    private CoffeeMachineRepository coffeeMachineRepository;

    @Autowired
    private UsageHistoryRepository usageHistoryRepository;

    @Autowired
    private AlertLogRepository alertLogRepository;
    
    @Value("${mqtt.broker.url}")
    String broker;
    
    @Value("${mqtt.username}")
    String username;
    
    @Value("${mqtt.password}")
    String password;

    @PostConstruct
    public void init() throws MqttException {
        String clientId = "coffeeapp-subscriber";
        MqttClient client = new MqttClient(broker, clientId, new MemoryPersistence());

        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setKeepAliveInterval(60);

        client.setCallback(new MqttCallback() {
            public void connectionLost(Throwable cause) {
                System.err.println("MQTT connection lost: " + cause.getMessage());
            }

            public void messageArrived(String topic, MqttMessage message) {
                handleIncomingMessage(topic, new String(message.getPayload()));
            }

            public void deliveryComplete(IMqttDeliveryToken token) {}
        });

        client.connect(options);
        client.subscribe("coffeemachine/+/data", 1);
    }

    private void handleIncomingMessage(String topic, String payload) {
        try {
            TelemetryDataDto data = parsePayload(payload); // Assume JSON → TelemetryData
            Long maxId = coffeeMachineRepository.findMaxId(); // Custom query

            for (int id = 1; id <= maxId; id++) {
                coffeeMachineRepository.findById(id).ifPresent(machine -> {
                    if (machine.getIsActive() == true) {
                        updateCoffeeMachine(machine, data);
                        if (!"None".equalsIgnoreCase(data.getBrewType())) {
                            logUsage(machine, data);
                        }
                        checkAndLogAlerts(machine, data);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private TelemetryDataDto parsePayload(String json) {
    	
        ObjectMapper mapper = new ObjectMapper();
        TelemetryDataDto mappedData = null;
        try {
        	mappedData = mapper.readValue(json, TelemetryDataDto.class);
        }
        catch(JsonProcessingException e) {
        	System.out.println("Error while processing the telemtry data!!!");
        }
        
        return mappedData;
    }

    private void updateCoffeeMachine(CoffeeMachine machine, TelemetryDataDto data) {
        machine.setWaterLevel(data.getWaterLevel());
        machine.setBeansLevel(data.getBeansLevel());
        machine.setMilkLevel(data.getMilkLevel());
        machine.setSugarLevel(data.getSugarLevel());
        machine.setTemperature(data.getTemperature());
        machine.setStatus(data.getStatus());
        machine.setLastUpdate(LocalDateTime.now());
        coffeeMachineRepository.save(machine);
    }

    private void logUsage(CoffeeMachine machine, TelemetryDataDto data) {
        UsageHistory history = new UsageHistory();
        history.setMachineId(machine.getId());
        history.setBrewType(data.getBrewType());
        history.setTimestamp(LocalDateTime.now());
        usageHistoryRepository.save(history);
    }

    private void checkAndLogAlerts(CoffeeMachine machine, TelemetryDataDto data) {
        if (data.getSugarLevel() < 10 || data.getWaterLevel() < 20 ||
            data.getBeansLevel() < 15 || data.getMilkLevel() < 10) {

            AlertLog alert = new AlertLog();
            alert.setMachineId(machine.getId());
            alert.setTimestamp(LocalDateTime.now());
            alert.setMessage("Low resource alert: " +
                (data.getSugarLevel() < 10 ? "Sugar " : "") +
                (data.getWaterLevel() < 20 ? "Water " : "") +
                (data.getBeansLevel() < 15 ? "Beans " : "") +
                (data.getMilkLevel() < 10 ? "Milk " : ""));
            alertLogRepository.save(alert);
        }
    }
}*/

package com.coffee.coffeeApp.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.coffee.coffeeApp.dto.TelemetryDataDto;
import com.coffee.coffeeApp.entity.AlertLog;
import com.coffee.coffeeApp.entity.CoffeeMachine;
import com.coffee.coffeeApp.entity.UsageHistory;
import com.coffee.coffeeApp.repository.AlertLogRepository;
import com.coffee.coffeeApp.repository.CoffeeMachineRepository;
import com.coffee.coffeeApp.repository.UsageHistoryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Service
public class MQTTSubscriberService {

    @Autowired
    private CoffeeMachineRepository coffeeMachineRepository;

    @Autowired
    private UsageHistoryRepository usageHistoryRepository;

    @Autowired
    private AlertLogRepository alertLogRepository;
    
    @Autowired
    private AlertLogService alertLogService;

    @Value("${mqtt.broker.url}")
    private String broker;

    @Value("${mqtt.username}")
    private String username;

    @Value("${mqtt.password}")
    private String password;

    // Keep references so they don’t get GC’ed and so we can close them
    private MqttClient client;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        try {
            String clientId = "coffeeapp-subscriber";
            client = new MqttClient(broker, clientId, new MemoryPersistence());

            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(username);
            options.setPassword(password.toCharArray());
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setKeepAliveInterval(60);

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.err.println("MQTT connection lost: " + cause.getMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    handleIncomingMessage(topic, new String(message.getPayload()));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) { }
            });

            client.connect(options);
            // Topic pattern: coffeemachine/{machineId}/data
            client.subscribe("coffeemachine/+/data", 1);

            System.out.println("MQTT subscriber connected and subscribed to coffeemachine/+/data");
        } catch (MqttException e) {
            // If this throws at startup, Spring may fail to boot—log clearly.
            System.err.println("Failed to initialize MQTT subscriber: " + e.getMessage());
            // You may rethrow as unchecked if you want to fail-fast:
            // throw new IllegalStateException("MQTT init failed", e);
        }
    }

    @PreDestroy
    public void shutdown() {
        try {
            if (client != null && client.isConnected()) {
                client.disconnect();
            }
            if (client != null) {
                client.close();
            }
        } catch (MqttException e) {
            System.err.println("Error during MQTT shutdown: " + e.getMessage());
        }
    }

    private void handleIncomingMessage(String topic, String payload) {
        // Extract machineId from topic: coffeemachine/{id}/data
        Integer machineId = extractMachineId(topic);
        if (machineId == null) {
            System.err.println("Invalid topic format, expected coffeemachine/{id}/data but got: " + topic);
            return;
        }

        TelemetryDataDto data = parsePayload(payload);
        if (data == null) {
            // Malformed JSON—skip
            return;
        }

        try {
            Optional<CoffeeMachine> opt = coffeeMachineRepository.findById(machineId);
            if (opt.isEmpty()) {
                System.err.println("No CoffeeMachine found with id=" + machineId + " for topic=" + topic);
                return;
            }

            CoffeeMachine machine = opt.get();
            if (!Boolean.TRUE.equals(machine.getIsActive())) {
                // Ignore updates for inactive machines
                return;
            }

            updateCoffeeMachine(machine, data);

            if (data.getBrewType() != null && !"None".equalsIgnoreCase(data.getBrewType())) {
                logUsage(machine, data);
            }

            checkAndLogAlerts(machine, data);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Integer extractMachineId(String topic) {
        // Expected: coffeemachine/{id}/data
        String[] parts = topic.split("/");
        if (parts.length >= 3 && "coffeemachine".equals(parts[0]) && "data".equals(parts[2])) {
            try {
                return Integer.valueOf(parts[1]);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        // Also handle leading slash case if broker delivers with leading '/'
        if (parts.length >= 4 && "".equals(parts[0]) && "coffeemachine".equals(parts[1]) && "data".equals(parts[3])) {
            try {
                return Integer.valueOf(parts[2]);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private TelemetryDataDto parsePayload(String json) {
        try {
            return objectMapper.readValue(json, TelemetryDataDto.class);
        } catch (JsonProcessingException e) {
            System.err.println("Error while processing telemetry data JSON: " + e.getMessage());
            return null;
        }
    }

    private void updateCoffeeMachine(CoffeeMachine machine, TelemetryDataDto data) {
        machine.setWaterLevel(data.getWaterLevel());
        machine.setBeansLevel(data.getBeansLevel());
        machine.setMilkLevel(data.getMilkLevel());
        machine.setSugarLevel(data.getSugarLevel());
        machine.setTemperature(data.getTemperature());
        machine.setStatus(data.getStatus());
        machine.setLastUpdate(LocalDateTime.now());
        coffeeMachineRepository.save(machine);
    }

    private void logUsage(CoffeeMachine machine, TelemetryDataDto data) {
        UsageHistory history = new UsageHistory();
        history.setMachineId(machine.getId());
        history.setBrewType(data.getBrewType());
        history.setTimestamp(LocalDateTime.now());
        usageHistoryRepository.save(history);
    }

    private void checkAndLogAlerts(CoffeeMachine machine, TelemetryDataDto data) {
        boolean lowSugar = data.getSugarLevel() < 10;
        boolean lowWater = data.getWaterLevel() < 20;
        boolean lowBeans = data.getBeansLevel() < 15;
        boolean lowMilk  = data.getMilkLevel()  < 10;

        if (lowSugar || lowWater || lowBeans || lowMilk) {
            
            if (lowSugar) alertLogService.createLowSugarAlert(machine.getId(), machine.getSugarLevel());
            if (lowWater) alertLogService.createLowWaterAlert(machine.getId(), machine.getWaterLevel());
            if (lowBeans) alertLogService.createLowBeansAlert(machine.getId(), machine.getBeansLevel());
            if (lowMilk)  alertLogService.createLowMilkAlert(machine.getId(), machine.getMilkLevel());

//            AlertLog alert = new AlertLog();
//            alert.setMachineId(machine.getId());
//            alert.setTimestamp(LocalDateTime.now());
//            alert.setMessage(sb.toString().trim());
//            alertLogRepository.save(alert);
        }
    }
}
