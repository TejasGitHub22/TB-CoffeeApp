package com.coffee.dto;

import com.coffee.model.MachineStatus;

public class CoffeeMachineDto {
	private Long id;
	private MachineStatus status;
	private Double temperature;
	private Integer waterLevel;
	private Integer milkLevel;
	private Integer beansLevel;

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public MachineStatus getStatus() { return status; }
	public void setStatus(MachineStatus status) { this.status = status; }
	public Double getTemperature() { return temperature; }
	public void setTemperature(Double temperature) { this.temperature = temperature; }
	public Integer getWaterLevel() { return waterLevel; }
	public void setWaterLevel(Integer waterLevel) { this.waterLevel = waterLevel; }
	public Integer getMilkLevel() { return milkLevel; }
	public void setMilkLevel(Integer milkLevel) { this.milkLevel = milkLevel; }
	public Integer getBeansLevel() { return beansLevel; }
	public void setBeansLevel(Integer beansLevel) { this.beansLevel = beansLevel; }
}