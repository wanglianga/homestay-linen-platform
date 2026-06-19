package com.homestay.linen.service;

import com.homestay.linen.common.Result;
import com.homestay.linen.entity.DamageReport;
import com.homestay.linen.entity.LinenBag;
import com.homestay.linen.entity.LinenStock;
import com.homestay.linen.entity.User;
import com.homestay.linen.repository.DamageReportRepository;
import com.homestay.linen.repository.LinenBagRepository;
import com.homestay.linen.repository.LinenStockRepository;
import com.homestay.linen.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DamageReportService {

    private final DamageReportRepository damageReportRepository;
    private final LinenBagRepository linenBagRepository;
    private final UserRepository userRepository;
    private final LinenStockRepository linenStockRepository;

    public Result<DamageReport> create(DamageReport report) {
        try {
            User reporter = userRepository.findById(report.getReporterId()).orElse(null);
            if (reporter == null) {
                return Result.error("上报人不存在");
            }
            if (report.getLinenBagId() != null) {
                LinenBag bag = linenBagRepository.findById(report.getLinenBagId()).orElse(null);
                if (bag != null) {
                    report.setBagCode(bag.getBagCode());
                    report.setLinenTypeId(bag.getLinenTypeId());
                    report.setLinenTypeName(bag.getLinenTypeName());
                    report.setRoomId(bag.getRoomId());
                    report.setRoomNo(bag.getRoomNo());
                }
            }
            report.setReportNo("DP" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase());
            report.setReporterName(reporter.getRealName());
            report.setStatus("待审批");
            if (report.getUnitPrice() != null && report.getQuantity() != null) {
                report.setTotalAmount(report.getUnitPrice().multiply(BigDecimal.valueOf(report.getQuantity())));
            }
            DamageReport saved = damageReportRepository.save(report);
            return Result.success("创建报损单成功", saved);
        } catch (Exception e) {
            return Result.error("创建报损单失败: " + e.getMessage());
        }
    }

    @Transactional
    public Result<DamageReport> approve(Long id, Long managerId, BigDecimal compensationAmount, BigDecimal factoryChargeAmount, String remark) {
        try {
            DamageReport report = damageReportRepository.findById(id).orElse(null);
            if (report == null) {
                return Result.error("报损单不存在");
            }
            if (!"待审批".equals(report.getStatus())) {
                return Result.error("报损单状态不正确，当前状态: " + report.getStatus());
            }
            User manager = userRepository.findById(managerId).orElse(null);
            if (manager == null) {
                return Result.error("审批人不存在");
            }
            report.setManagerId(managerId);
            report.setManagerName(manager.getRealName());
            report.setApproveTime(LocalDateTime.now());
            report.setCompensationAmount(compensationAmount);
            report.setFactoryChargeAmount(factoryChargeAmount);
            report.setRemark(remark);
            report.setStatus("已审批");
            DamageReport saved = damageReportRepository.save(report);

            LinenStock stock = linenStockRepository.findByLinenTypeId(report.getLinenTypeId()).orElse(null);
            if (stock != null) {
                stock.setDamageQuantity(stock.getDamageQuantity() + report.getQuantity());
                stock.setTotalQuantity(stock.getTotalQuantity() - report.getQuantity());
                linenStockRepository.save(stock);
            }

            return Result.success("审批成功", saved);
        } catch (Exception e) {
            return Result.error("审批失败: " + e.getMessage());
        }
    }

    public Result<List<DamageReport>> list() {
        try {
            List<DamageReport> reports = damageReportRepository.findAll();
            return Result.success(reports);
        } catch (Exception e) {
            return Result.error("查询报损单列表失败: " + e.getMessage());
        }
    }

    public Result<List<DamageReport>> listByStatus(String status) {
        try {
            List<DamageReport> reports = damageReportRepository.findByStatus(status);
            return Result.success(reports);
        } catch (Exception e) {
            return Result.error("按状态查询报损单失败: " + e.getMessage());
        }
    }

    public Result<DamageReport> getById(Long id) {
        try {
            DamageReport report = damageReportRepository.findById(id).orElse(null);
            if (report == null) {
                return Result.error("报损单不存在");
            }
            return Result.success(report);
        } catch (Exception e) {
            return Result.error("查询报损单失败: " + e.getMessage());
        }
    }

    public Result<DamageReport> getByReportNo(String reportNo) {
        try {
            DamageReport report = damageReportRepository.findByReportNo(reportNo).orElse(null);
            if (report == null) {
                return Result.error("报损单不存在");
            }
            return Result.success(report);
        } catch (Exception e) {
            return Result.error("查询报损单失败: " + e.getMessage());
        }
    }
}
