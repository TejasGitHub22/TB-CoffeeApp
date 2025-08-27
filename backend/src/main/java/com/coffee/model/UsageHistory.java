package com.coffee.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "usage_history")
public class UsageHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "machine_id", nullable = false)
	private CoffeeMachine machine;

	@Column(nullable = false)
	private Instant timestamp;

	@Column(nullable = false)
	private String brewType;

	@Column(nullable = false)
	private String user;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CoffeeMachine getMachine() {
		return machine;
	}

	public void setMachine(CoffeeMachine machine) {
		this.machine = machine;
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
	}

	public String getBrewType() {
		return brewType;
	}

	public void setBrewType(String brewType) {
		this.brewType = brewType;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
}