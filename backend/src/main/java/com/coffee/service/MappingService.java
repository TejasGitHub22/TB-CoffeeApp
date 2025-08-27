package com.coffee.service;

import com.coffee.dto.CoffeeMachineDto;
import com.coffee.dto.LevelsDto;
import com.coffee.dto.MachineStatusDto;
import com.coffee.dto.UsageHistoryDto;
import com.coffee.model.CoffeeMachine;
import com.coffee.model.UsageHistory;
import org.springframework.stereotype.Service;

@Service
public class MappingService {
	public CoffeeMachineDto toMachineDto(CoffeeMachine m) {
		CoffeeMachineDto dto = new CoffeeMachineDto();
		dto.setId(m.getId());
		dto.setStatus(m.getStatus());
		dto.setTemperature(m.getTemperature());
		dto.setWaterLevel(m.getWaterLevel());
		dto.setMilkLevel(m.getMilkLevel());
		dto.setBeansLevel(m.getBeansLevel());
		return dto;
	}

	public MachineStatusDto toStatusDto(CoffeeMachine m) {
		MachineStatusDto dto = new MachineStatusDto();
		dto.setId(m.getId());
		dto.setStatus(m.getStatus());
		return dto;
	}

	public LevelsDto toLevelsDto(CoffeeMachine m) {
		LevelsDto dto = new LevelsDto();
		dto.setId(m.getId());
		dto.setWaterLevel(m.getWaterLevel());
		dto.setMilkLevel(m.getMilkLevel());
		dto.setBeansLevel(m.getBeansLevel());
		return dto;
	}

	public UsageHistoryDto toUsageDto(UsageHistory h) {
		UsageHistoryDto dto = new UsageHistoryDto();
		dto.setId(h.getId());
		dto.setMachineId(h.getMachine().getId());
		dto.setTimestamp(h.getTimestamp());
		dto.setBrewType(h.getBrewType());
		dto.setUser(h.getUser());
		return dto;
	}
}