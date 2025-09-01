package com.coffeemachine.simulator.model;

import jakarta.persistence.*;
import lombok.Data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Entity
@Table(name = "simulation_data")
@Data
public class MachineData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "machine_id", nullable = false)
    private int machineId;

    @Column(name = "published_message", nullable = false)
    private String message;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getMachineId() {
		return machineId;
	}

	public void setMachineId(int machineId) {
		this.machineId = machineId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public MachineData() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MachineData(int machineId, String message) {
		super();
		this.machineId = machineId;
		this.message = message;
	}

//	public String toJson() {
//        ObjectMapper mapper = new ObjectMapper();
//        try {
//            return mapper.writeValueAsString(this);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//            return "{}";
//        }
//    }
}
