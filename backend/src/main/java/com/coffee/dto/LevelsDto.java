package com.coffee.dto;

public class LevelsDto {
	private Long id;
	private Integer waterLevel;
	private Integer milkLevel;
	private Integer beansLevel;

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public Integer getWaterLevel() { return waterLevel; }
	public void setWaterLevel(Integer waterLevel) { this.waterLevel = waterLevel; }
	public Integer getMilkLevel() { return milkLevel; }
	public void setMilkLevel(Integer milkLevel) { this.milkLevel = milkLevel; }
	public Integer getBeansLevel() { return beansLevel; }
	public void setBeansLevel(Integer beansLevel) { this.beansLevel = beansLevel; }
}