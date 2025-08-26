package com.coffee.dto;

public class FacilitySummaryDto {
	private Long id;
	private String name;
	private String location;
	private int totalMachines;
	private int machinesOn;
	private int machinesOff;

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getLocation() { return location; }
	public void setLocation(String location) { this.location = location; }
	public int getTotalMachines() { return totalMachines; }
	public void setTotalMachines(int totalMachines) { this.totalMachines = totalMachines; }
	public int getMachinesOn() { return machinesOn; }
	public void setMachinesOn(int machinesOn) { this.machinesOn = machinesOn; }
	public int getMachinesOff() { return machinesOff; }
	public void setMachinesOff(int machinesOff) { this.machinesOff = machinesOff; }
}