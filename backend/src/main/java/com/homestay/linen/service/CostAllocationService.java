package com.homestay.linen.service;

import com.homestay.linen.common.Result;
import com.homestay.linen.entity.CostAllocation;
import com.homestay.linen.entity.LinenType;
import com.homestay.linen.repository.CostAllocationRepository;
import com.homestay.linen.repository.LinenTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CostAllocationService {

    private final CostAllocationRepository costAllocationRepository;
    private final LinenTypeRepository linenTypeRepository;

    @Transactional
    public Result<List<CostAllocation>> generateDailyReport(LocalDate date) {
        try {
            List<LinenType> linenTypes = linenTypeRepository.findAll();
            List<CostAllocation> reports = new ArrayList<>();

            for (LinenType linenType : linenTypes) {
                CostAllocation report = new CostAllocation();
                report.setStatDate(date);
                report.setLinenTypeId(linenType.getId());
                report.setLinenTypeName(linenType.getName());
                report.setWashCount(0);
                report.setWashCost(BigDecimal.ZERO);
                report.setDamageCount(0);
                report.setDamageCost(BigDecimal.ZERO);
                report.setCompensationIncome(BigDecimal.ZERO);
                report.setFactoryCharge(BigDecimal.ZERO);
                report.setNetCost(BigDecimal.ZERO);
                reports.add(costAllocationRepository.save(report));
            }

            return Result.success("生成日报表成功", reports);
        } catch (Exception e) {
            return Result.error("生成日报表失败: " + e.getMessage());
        }
    }

    public Result<List<CostAllocation>> getByDateRange(LocalDate startDate, LocalDate endDate) {
        try {
            List<CostAllocation> reports = costAllocationRepository.findByStatDateBetween(startDate, endDate);
            return Result.success(reports);
        } catch (Exception e) {
            return Result.error("按日期范围查询成本报表失败: " + e.getMessage());
        }
    }

    public Result<List<CostAllocation>> list() {
        try {
            List<CostAllocation> reports = costAllocationRepository.findAll();
            return Result.success(reports);
        } catch (Exception e) {
            return Result.error("查询成本报表列表失败: " + e.getMessage());
        }
    }

    public Result<CostAllocation> getById(Long id) {
        try {
            CostAllocation report = costAllocationRepository.findById(id).orElse(null);
            if (report == null) {
                return Result.error("成本报表不存在");
            }
            return Result.success(report);
        } catch (Exception e) {
            return Result.error("查询成本报表失败: " + e.getMessage());
        }
    }
}
