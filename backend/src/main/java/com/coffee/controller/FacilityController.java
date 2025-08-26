package com.coffee.controller;

import com.coffee.dto.BrewRequestDto;
import com.coffee.dto.CoffeeMachineDto;
import com.coffee.dto.LevelsDto;
import com.coffee.dto.MachineStatusDto;
import com.coffee.dto.UsageHistoryDto;
import com.coffee.model.CoffeeMachine;
import com.coffee.repository.CoffeeMachineRepository;
import com.coffee.repository.UsageHistoryRepository;
import com.coffee.service.MappingService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class FacilityController {
	private final CoffeeMachineRepository coffeeMachineRepository;
	private final UsageHistoryRepository usageHistoryRepository;
	private final MappingService mappingService;

	public FacilityController(CoffeeMachineRepository coffeeMachineRepository,
	                          UsageHistoryRepository usageHistoryRepository,
	                          MappingService mappingService) {
		this.coffeeMachineRepository = coffeeMachineRepository;
		this.usageHistoryRepository = usageHistoryRepository;
		this.mappingService = mappingService;
	}

	@GetMapping("/facility/{id}/machines")
	public List<CoffeeMachineDto> getMachines(@PathVariable Long id) {
		return coffeeMachineRepository.findByFacilityId(id)
				.stream().map(mappingService::toMachineDto).collect(Collectors.toList());
	}

	@GetMapping("/machine/{id}/status")
	public ResponseEntity<MachineStatusDto> getStatus(@PathVariable Long id) {
		return coffeeMachineRepository.findById(id)
				.map(mappingService::toStatusDto)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("/machine/{id}/levels")
	public ResponseEntity<LevelsDto> getLevels(@PathVariable Long id) {
		return coffeeMachineRepository.findById(id)
				.map(mappingService::toLevelsDto)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("/machine/{id}/history")
	public List<UsageHistoryDto> getHistory(@PathVariable Long id) {
		return usageHistoryRepository.findByMachineId(id)
				.stream().map(mappingService::toUsageDto).collect(Collectors.toList());
	}

	@PostMapping("/machine/{id}/brew")
	public ResponseEntity<?> brew(@PathVariable Long id, @RequestBody BrewRequestDto req) {
		// In a real system, we might publish an MQTT command here. For now, accept request.
		return coffeeMachineRepository.findById(id)
				.map(m -> ResponseEntity.accepted().build())
				.orElse(ResponseEntity.status(404).build());
	}
}