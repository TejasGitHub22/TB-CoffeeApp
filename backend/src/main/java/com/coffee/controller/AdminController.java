package com.coffee.controller;

import com.coffee.dto.FacilitySummaryDto;
import com.coffee.dto.UsageHistoryDto;
import com.coffee.model.CoffeeMachine;
import com.coffee.model.Facility;
import com.coffee.repository.CoffeeMachineRepository;
import com.coffee.repository.FacilityRepository;
import com.coffee.repository.UsageHistoryRepository;
import com.coffee.service.MappingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
	private final FacilityRepository facilityRepository;
	private final CoffeeMachineRepository coffeeMachineRepository;
	private final UsageHistoryRepository usageHistoryRepository;
	private final MappingService mappingService;

	public AdminController(FacilityRepository facilityRepository,
	                      CoffeeMachineRepository coffeeMachineRepository,
	                      UsageHistoryRepository usageHistoryRepository,
	                      MappingService mappingService) {
		this.facilityRepository = facilityRepository;
		this.coffeeMachineRepository = coffeeMachineRepository;
		this.usageHistoryRepository = usageHistoryRepository;
		this.mappingService = mappingService;
	}

	@GetMapping("/facilities")
	public List<FacilitySummaryDto> facilities() {
		return facilityRepository.findAll().stream().map(f -> {
			FacilitySummaryDto dto = new FacilitySummaryDto();
			dto.setId(f.getId());
			dto.setName(f.getName());
			dto.setLocation(f.getLocation());
			List<CoffeeMachine> machines = coffeeMachineRepository.findByFacility(f);
			dto.setTotalMachines(machines.size());
			int on = (int) machines.stream().filter(m -> m.getStatus() != null && m.getStatus().name().equals("ON")).count();
			dto.setMachinesOn(on);
			dto.setMachinesOff(machines.size() - on);
			return dto;
		}).collect(Collectors.toList());
	}

	@GetMapping("/usage")
	public List<UsageHistoryDto> usage() {
		return usageHistoryRepository.findAll().stream().map(mappingService::toUsageDto).collect(Collectors.toList());
	}
}