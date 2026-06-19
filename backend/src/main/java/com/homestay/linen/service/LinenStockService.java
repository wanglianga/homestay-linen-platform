package com.homestay.linen.service;

import com.homestay.linen.common.Result;
import com.homestay.linen.entity.LinenStock;
import com.homestay.linen.entity.LinenType;
import com.homestay.linen.repository.LinenStockRepository;
import com.homestay.linen.repository.LinenTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LinenStockService {

    private final LinenStockRepository linenStockRepository;
    private final LinenTypeRepository linenTypeRepository;

    public Result<List<LinenStock>> list() {
        try {
            List<LinenStock> stocks = linenStockRepository.findAll();
            return Result.success(stocks);
        } catch (Exception e) {
            return Result.error("查询库存列表失败: " + e.getMessage());
        }
    }

    public Result<LinenStock> updateSafetyStock(Long linenTypeId, Integer safetyStock) {
        try {
            LinenStock stock = linenStockRepository.findByLinenTypeId(linenTypeId).orElse(null);
            if (stock == null) {
                return Result.error("库存记录不存在");
            }
            stock.setSafetyStock(safetyStock);
            LinenStock saved = linenStockRepository.save(stock);
            return Result.success("更新安全库存成功", saved);
        } catch (Exception e) {
            return Result.error("更新安全库存失败: " + e.getMessage());
        }
    }

    @Transactional
    public Result<LinenStock> restock(Long linenTypeId, Integer quantity) {
        try {
            LinenStock stock = linenStockRepository.findByLinenTypeId(linenTypeId).orElse(null);
            if (stock == null) {
                LinenType linenType = linenTypeRepository.findById(linenTypeId).orElse(null);
                if (linenType == null) {
                    return Result.error("布草类型不存在");
                }
                stock = new LinenStock();
                stock.setLinenTypeId(linenTypeId);
                stock.setLinenTypeName(linenType.getName());
                stock.setTotalQuantity(quantity);
                stock.setInWarehouseQuantity(quantity);
                stock.setInUseQuantity(0);
                stock.setInWashQuantity(0);
                stock.setInDeliveryQuantity(0);
                stock.setDamageQuantity(0);
                stock.setSafetyStock(50);
                stock.setLastRestockTime(LocalDateTime.now());
            } else {
                stock.setTotalQuantity(stock.getTotalQuantity() + quantity);
                stock.setInWarehouseQuantity(stock.getInWarehouseQuantity() + quantity);
                stock.setLastRestockTime(LocalDateTime.now());
            }
            LinenStock saved = linenStockRepository.save(stock);
            return Result.success("补货成功", saved);
        } catch (Exception e) {
            return Result.error("补货失败: " + e.getMessage());
        }
    }

    @Transactional
    public Result<LinenStock> deductInUse(Long linenTypeId, Integer quantity) {
        try {
            LinenStock stock = linenStockRepository.findByLinenTypeId(linenTypeId).orElse(null);
            if (stock == null) {
                return Result.error("库存记录不存在");
            }
            if (stock.getInUseQuantity() < quantity) {
                return Result.error("在用布草数量不足，当前在用: " + stock.getInUseQuantity());
            }
            stock.setInUseQuantity(stock.getInUseQuantity() - quantity);
            stock.setTotalQuantity(stock.getTotalQuantity() - quantity);
            LinenStock saved = linenStockRepository.save(stock);
            return Result.success("扣减在用布草成功", saved);
        } catch (Exception e) {
            return Result.error("扣减在用布草失败: " + e.getMessage());
        }
    }

    public Result<LinenStock> getByLinenTypeId(Long linenTypeId) {
        try {
            LinenStock stock = linenStockRepository.findByLinenTypeId(linenTypeId).orElse(null);
            if (stock == null) {
                return Result.error("库存记录不存在");
            }
            return Result.success(stock);
        } catch (Exception e) {
            return Result.error("查询库存失败: " + e.getMessage());
        }
    }
}
