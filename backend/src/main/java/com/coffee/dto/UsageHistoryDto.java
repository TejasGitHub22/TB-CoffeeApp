package com.coffee.dto;

import java.time.Instant;

public class UsageHistoryDto {
	private Long id;
	private Long machineId;
	private Instant timestamp;
	private String brewType;
	private String user;

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public Long getMachineId() { return machineId; }
	public void setMachineId(Long machineId) { this.machineId = machineId; }
	public Instant getTimestamp() { return timestamp; }
	public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
	public String getBrewType() { return brewType; }
	public void setBrewType(String brewType) { this.brewType = brewType; }
	public String getUser() { return user; }
	public void setUser(String user) { this.user = user; }
}