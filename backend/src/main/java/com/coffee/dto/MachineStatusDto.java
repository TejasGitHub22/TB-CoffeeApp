package com.coffee.dto;

import com.coffee.model.MachineStatus;

public class MachineStatusDto {
	private Long id;
	private MachineStatus status;

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public MachineStatus getStatus() { return status; }
	public void setStatus(MachineStatus status) { this.status = status; }
}