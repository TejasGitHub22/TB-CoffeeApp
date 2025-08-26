package com.coffee.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "coffee_machines")
public class CoffeeMachine {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "facility_id", nullable = false)
	private Facility facility;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private MachineStatus status = MachineStatus.OFF;

	private Double temperature;

	private Integer waterLevel;

	private Integer milkLevel;

	private Integer beansLevel;

	@OneToMany(mappedBy = "machine", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<UsageHistory> usageHistories = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Facility getFacility() {
		return facility;
	}

	public void setFacility(Facility facility) {
		this.facility = facility;
	}

	public MachineStatus getStatus() {
		return status;
	}

	public void setStatus(MachineStatus status) {
		this.status = status;
	}

	public Double getTemperature() {
		return temperature;
	}

	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}

	public Integer getWaterLevel() {
		return waterLevel;
	}

	public void setWaterLevel(Integer waterLevel) {
		this.waterLevel = waterLevel;
	}

	public Integer getMilkLevel() {
		return milkLevel;
	}

	public void setMilkLevel(Integer milkLevel) {
		this.milkLevel = milkLevel;
	}

	public Integer getBeansLevel() {
		return beansLevel;
	}

	public void setBeansLevel(Integer beansLevel) {
		this.beansLevel = beansLevel;
	}

	public List<UsageHistory> getUsageHistories() {
		return usageHistories;
	}

	public void setUsageHistories(List<UsageHistory> usageHistories) {
		this.usageHistories = usageHistories;
	}
}