package com.coffee.repository;

import com.coffee.model.UsageHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsageHistoryRepository extends JpaRepository<UsageHistory, Long> {
	List<UsageHistory> findByMachineId(Long machineId);
}