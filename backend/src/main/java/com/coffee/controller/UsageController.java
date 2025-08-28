package com.coffee.controller;

import com.coffee.model.UsageHistory;
import com.coffee.repository.UsageHistoryRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usage")
public class UsageController {

    private final UsageHistoryRepository usageHistoryRepository;

    public UsageController(UsageHistoryRepository usageHistoryRepository) {
        this.usageHistoryRepository = usageHistoryRepository;
    }

    public record UsageSummaryDto(
            String period,
            Instant since,
            Instant until,
            Long machineId,
            long totalCups,
            Map<String, Long> byBrewType
    ) {}

    @GetMapping("/summary")
    public ResponseEntity<UsageSummaryDto> getUsageSummary(
            @RequestParam(defaultValue = "daily") String period,
            @RequestParam(required = false) Long machineId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to
    ) {
        Instant now = Instant.now();
        Instant until = to != null ? to : now;
        Instant since = from != null ? from : until.minus(resolveWindow(period));

        // Fetch and filter in-memory for simplicity. For large datasets, replace with a custom query.
        List<UsageHistory> histories = machineId != null
                ? usageHistoryRepository.findByMachineId(machineId)
                : usageHistoryRepository.findAll();

        Map<String, Long> byBrewType = histories.stream()
                .filter(h -> !h.getTimestamp().isBefore(since) && !h.getTimestamp().isAfter(until))
                .collect(Collectors.groupingBy(UsageHistory::getBrewType, Collectors.counting()));

        long total = byBrewType.values().stream().mapToLong(Long::longValue).sum();

        UsageSummaryDto dto = new UsageSummaryDto(
                period,
                since,
                until,
                machineId,
                total,
                new HashMap<>(byBrewType)
        );
        return ResponseEntity.ok(dto);
    }

    private Duration resolveWindow(String period) {
        return switch (period.toLowerCase()) {
            case "daily" -> Duration.ofDays(1);
            case "weekly" -> Duration.ofDays(7);
            case "monthly" -> Duration.ofDays(30);
            case "yearly" -> Duration.ofDays(365);
            default -> Duration.ofDays(1);
        };
    }
}

