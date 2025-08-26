package com.coffee.service;

import com.coffee.model.CoffeeMachine;
import com.coffee.model.Facility;
import com.coffee.model.MachineStatus;
import com.coffee.model.UsageHistory;
import com.coffee.repository.CoffeeMachineRepository;
import com.coffee.repository.FacilityRepository;
import com.coffee.repository.UsageHistoryRepository;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.MqttGlobalPublishFilter;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Optional;

@Service
public class MqttService {
	private static final Logger log = LoggerFactory.getLogger(MqttService.class);

	private final CoffeeMachineRepository coffeeMachineRepository;
	private final FacilityRepository facilityRepository;
	private final UsageHistoryRepository usageHistoryRepository;

	@Value("${mqtt.host}")
	private String mqttHost;

	@Value("${mqtt.port}")
	private int mqttPort;

	@Value("${mqtt.username}")
	private String mqttUsername;

	@Value("${mqtt.password}")
	private String mqttPassword;

	public MqttService(CoffeeMachineRepository coffeeMachineRepository,
	                  FacilityRepository facilityRepository,
	                  UsageHistoryRepository usageHistoryRepository) {
		this.coffeeMachineRepository = coffeeMachineRepository;
		this.facilityRepository = facilityRepository;
		this.usageHistoryRepository = usageHistoryRepository;
	}

	@PostConstruct
	public void init() {
		try {
			Mqtt3AsyncClient client = MqttClient.builder()
					.useMqttVersion3()
					.serverHost(mqttHost)
					.serverPort(mqttPort)
					.sslWithDefaultConfig()
					.automaticReconnectWithDefaultConfig()
					.buildAsync();

			client.connectWith()
					.simpleAuth()
					.username(mqttUsername)
					.password(mqttPassword.getBytes(StandardCharsets.UTF_8))
					.applyConnect()
					.send()
					.whenComplete((connAck, throwable) -> {
						if (throwable != null) {
							log.error("MQTT connection failed", throwable);
							return;
						}
						log.info("MQTT connected");
						subscribe(client);
					});

			client.publishes(MqttGlobalPublishFilter.ALL, publish -> {
				String topic = publish.getTopic().toString();
				String payload = new String(publish.getPayloadAsBytes(), StandardCharsets.UTF_8);
				log.info("Received MQTT message. topic={} payload={}", topic, payload);
				try {
					processMessage(topic, payload);
				} catch (Exception e) {
					log.error("Failed processing MQTT message", e);
				}
			});
		} catch (Exception e) {
			log.error("Error initializing MQTT client", e);
		}
	}

	private void subscribe(Mqtt3AsyncClient client) {
		client.subscribeWith().topicFilter("coffeeMachine/+/+").qos(MqttQos.AT_LEAST_ONCE).send();
	}

	private void processMessage(String topic, String payload) {
		String[] parts = topic.split("/");
		if (parts.length != 3) return;
		Long machineId = parseLong(parts[1]);
		String metric = parts[2];
		if (machineId == null) return;

		Optional<CoffeeMachine> optional = coffeeMachineRepository.findById(machineId);
		CoffeeMachine machine = optional.orElseGet(() -> {
			CoffeeMachine m = new CoffeeMachine();
			Facility facility = facilityRepository.findById(1L).orElseGet(() -> {
				Facility f = new Facility();
				f.setName("Default Facility");
				f.setLocation("Unknown");
				return facilityRepository.save(f);
			});
			m.setFacility(facility);
			return coffeeMachineRepository.save(m);
		});

		switch (metric) {
			case "temperature" -> machine.setTemperature(parseDouble(payload));
			case "waterLevel" -> machine.setWaterLevel(parseInt(payload));
			case "milkLevel" -> machine.setMilkLevel(parseInt(payload));
			case "beansLevel" -> machine.setBeansLevel(parseInt(payload));
			case "status" -> machine.setStatus("ON".equalsIgnoreCase(payload) ? MachineStatus.ON : MachineStatus.OFF);
			case "usage" -> {
				UsageHistory h = new UsageHistory();
				h.setMachine(machine);
				h.setTimestamp(Instant.now());
				h.setBrewType(payload);
				h.setUser("simulator");
				usageHistoryRepository.save(h);
			}
			default -> {}
		}
		coffeeMachineRepository.save(machine);
	}

	private Long parseLong(String s) {
		try { return Long.parseLong(s); } catch (Exception e) { return null; }
	}
	private Integer parseInt(String s) {
		try { return Integer.parseInt(s); } catch (Exception e) { return null; }
	}
	private Double parseDouble(String s) {
		try { return Double.parseDouble(s); } catch (Exception e) { return null; }
	}
}