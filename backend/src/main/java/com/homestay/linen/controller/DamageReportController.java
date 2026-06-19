package com.homestay.linen.controller;

import com.homestay.linen.common.Result;
import com.homestay.linen.entity.DamageReport;
import com.homestay.linen.service.DamageReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/damageReport")
@RequiredArgsConstructor
public class DamageReportController {

    private final DamageReportService damageReportService;

    @PostMapping
    public Result<DamageReport> create(@RequestBody DamageReport damageReport) {
        return damageReportService.create(damageReport);
    }

    @PutMapping("/{id}/approve")
    public Result<DamageReport> approve(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        Long managerId = Long.valueOf(params.get("managerId").toString());
        BigDecimal compensationAmount = params.get("compensationAmount") != null ? new BigDecimal(params.get("compensationAmount").toString()) : BigDecimal.ZERO;
        BigDecimal factoryChargeAmount = params.get("factoryChargeAmount") != null ? new BigDecimal(params.get("factoryChargeAmount").toString()) : BigDecimal.ZERO;
        String remark = params.get("remark") != null ? params.get("remark").toString() : "";
        return damageReportService.approve(id, managerId, compensationAmount, factoryChargeAmount, remark);
    }

    @GetMapping("/list")
    public Result<List<DamageReport>> list() {
        return damageReportService.list();
    }

    @GetMapping("/listByStatus")
    public Result<List<DamageReport>> listByStatus(@RequestParam String status) {
        return damageReportService.listByStatus(status);
    }

    @GetMapping("/{id}")
    public Result<DamageReport> getById(@PathVariable Long id) {
        return damageReportService.getById(id);
    }

    @GetMapping("/reportNo/{reportNo}")
    public Result<DamageReport> getByReportNo(@PathVariable String reportNo) {
        return damageReportService.getByReportNo(reportNo);
    }
}
