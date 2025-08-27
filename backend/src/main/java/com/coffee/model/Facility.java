package com.coffee.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "facilities")
public class Facility {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String location;

	@OneToMany(mappedBy = "facility", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CoffeeMachine> machines = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public List<CoffeeMachine> getMachines() {
		return machines;
	}

	public void setMachines(List<CoffeeMachine> machines) {
		this.machines = machines;
	}
}