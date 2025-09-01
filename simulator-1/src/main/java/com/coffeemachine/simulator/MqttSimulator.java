package com.coffeemachine.simulator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.coffeemachine.simulator.service.MachineSimulatorService;
import com.fasterxml.jackson.core.JsonProcessingException;

@SpringBootApplication
public class MqttSimulator implements CommandLineRunner {

	@Autowired
    private MachineSimulatorService simulatorService;


    public static void main(String[] args) {
        SpringApplication.run(MqttSimulator.class, args);
    }

    @Override
    public void run(String... args) {
        try {
        	simulatorService.simulateMachines();
        }
        catch(JsonProcessingException e) {
        	e.printStackTrace();
        }
    }
}
