package com.coffee.repository;

import com.coffee.model.CoffeeMachine;
import com.coffee.model.Facility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoffeeMachineRepository extends JpaRepository<CoffeeMachine, Long> {
	List<CoffeeMachine> findByFacility(Facility facility);
	List<CoffeeMachine> findByFacilityId(Long facilityId);
}