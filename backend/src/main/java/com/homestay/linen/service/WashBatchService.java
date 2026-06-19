package com.homestay.linen.service;

import com.homestay.linen.common.Result;
import com.homestay.linen.entity.LinenBag;
import com.homestay.linen.entity.User;
import com.homestay.linen.entity.WashBatch;
import com.homestay.linen.repository.LinenBagRepository;
import com.homestay.linen.repository.UserRepository;
import com.homestay.linen.repository.WashBatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WashBatchService {

    private final WashBatchRepository washBatchRepository;
    private final LinenBagRepository linenBagRepository;
    private final UserRepository userRepository;

    public Result<WashBatch> create(WashBatch washBatch) {
        try {
            User factory = userRepository.findById(washBatch.getFactoryId()).orElse(null);
            if (factory == null) {
                return Result.error("洗涤厂用户不存在");
            }
            washBatch.setBatchNo("WB" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase());
            washBatch.setFactoryName(factory.getRealName());
            washBatch.setStatus("待洗涤");
            WashBatch saved = washBatchRepository.save(washBatch);
            return Result.success("创建洗涤批次成功", saved);
        } catch (Exception e) {
            return Result.error("创建洗涤批次失败: " + e.getMessage());
        }
    }

    @Transactional
    public Result<WashBatch> addBags(Long batchId, List<Long> bagIds) {
        try {
            WashBatch batch = washBatchRepository.findById(batchId).orElse(null);
            if (batch == null) {
                return Result.error("洗涤批次不存在");
            }
            if (!"待洗涤".equals(batch.getStatus())) {
                return Result.error("洗涤批次状态不正确，当前状态: " + batch.getStatus());
            }
            int totalBags = 0;
            int totalQuantity = 0;
            for (Long bagId : bagIds) {
                LinenBag bag = linenBagRepository.findById(bagId).orElse(null);
                if (bag != null && "洗涤中".equals(bag.getStatus())) {
                    bag.setWashBatchId(batchId);
                    bag.setWashBatchNo(batch.getBatchNo());
                    linenBagRepository.save(bag);
                    totalBags++;
                    totalQuantity += bag.getQuantity() != null ? bag.getQuantity() : 0;
                }
            }
            batch.setTotalBags(batch.getTotalBags() + totalBags);
            batch.setTotalQuantity(batch.getTotalQuantity() + totalQuantity);
            WashBatch saved = washBatchRepository.save(batch);
            return Result.success("添加布草袋成功，共添加" + totalBags + "袋", saved);
        } catch (Exception e) {
            return Result.error("添加布草袋失败: " + e.getMessage());
        }
    }

    public Result<WashBatch> startWash(Long id) {
        try {
            WashBatch batch = washBatchRepository.findById(id).orElse(null);
            if (batch == null) {
                return Result.error("洗涤批次不存在");
            }
            if (!"待洗涤".equals(batch.getStatus())) {
                return Result.error("洗涤批次状态不正确，当前状态: " + batch.getStatus());
            }
            batch.setStartTime(LocalDateTime.now());
            batch.setStatus("洗涤中");
            WashBatch saved = washBatchRepository.save(batch);
            return Result.success("开始洗涤成功", saved);
        } catch (Exception e) {
            return Result.error("开始洗涤失败: " + e.getMessage());
        }
    }

    @Transactional
    public Result<WashBatch> finishWash(Long id, Integer returnQuantity, Integer lossQuantity, String remark) {
        try {
            WashBatch batch = washBatchRepository.findById(id).orElse(null);
            if (batch == null) {
                return Result.error("洗涤批次不存在");
            }
            if (!"洗涤中".equals(batch.getStatus())) {
                return Result.error("洗涤批次状态不正确，当前状态: " + batch.getStatus());
            }
            batch.setEndTime(LocalDateTime.now());
            batch.setReturnQuantity(returnQuantity);
            batch.setLossQuantity(lossQuantity);
            batch.setRemark(remark);
            batch.setStatus("已完成");
            WashBatch saved = washBatchRepository.save(batch);

            List<LinenBag> bags = linenBagRepository.findByWashBatchId(id);
            for (LinenBag bag : bags) {
                bag.setStatus("待回库");
                bag.setReturnTime(LocalDateTime.now());
                bag.setReturnQuantity(bag.getFactoryQuantity());
                linenBagRepository.save(bag);
            }

            return Result.success("完成洗涤成功", saved);
        } catch (Exception e) {
            return Result.error("完成洗涤失败: " + e.getMessage());
        }
    }

    public Result<List<WashBatch>> list() {
        try {
            List<WashBatch> batches = washBatchRepository.findAll();
            return Result.success(batches);
        } catch (Exception e) {
            return Result.error("查询洗涤批次列表失败: " + e.getMessage());
        }
    }

    public Result<WashBatch> getByBatchNo(String batchNo) {
        try {
            WashBatch batch = washBatchRepository.findByBatchNo(batchNo).orElse(null);
            if (batch == null) {
                return Result.error("洗涤批次不存在");
            }
            return Result.success(batch);
        } catch (Exception e) {
            return Result.error("查询洗涤批次失败: " + e.getMessage());
        }
    }

    public Result<WashBatch> getById(Long id) {
        try {
            WashBatch batch = washBatchRepository.findById(id).orElse(null);
            if (batch == null) {
                return Result.error("洗涤批次不存在");
            }
            return Result.success(batch);
        } catch (Exception e) {
            return Result.error("查询洗涤批次失败: " + e.getMessage());
        }
    }

    public Result<List<WashBatch>> listByStatus(String status) {
        try {
            List<WashBatch> batches = washBatchRepository.findByStatus(status);
            return Result.success(batches);
        } catch (Exception e) {
            return Result.error("按状态查询洗涤批次失败: " + e.getMessage());
        }
    }
}
