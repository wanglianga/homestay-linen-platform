package com.homestay.linen.controller;

import com.homestay.linen.common.Result;
import com.homestay.linen.entity.CostAllocation;
import com.homestay.linen.service.CostAllocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/costAllocation")
@RequiredArgsConstructor
public class CostAllocationController {

    private final CostAllocationService costAllocationService;

    @PostMapping("/generateDaily")
    public Result<List<CostAllocation>> generateDailyReport(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return costAllocationService.generateDailyReport(date);
    }

    @GetMapping("/getByDateRange")
    public Result<List<CostAllocation>> getByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return costAllocationService.getByDateRange(startDate, endDate);
    }

    @GetMapping("/list")
    public Result<List<CostAllocation>> list() {
        return costAllocationService.list();
    }

    @GetMapping("/{id}")
    public Result<CostAllocation> getById(@PathVariable Long id) {
        return costAllocationService.getById(id);
    }
}
