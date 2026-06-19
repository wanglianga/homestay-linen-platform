package com.homestay.linen.service;

import com.homestay.linen.common.Result;
import com.homestay.linen.entity.DeliveryReceipt;
import com.homestay.linen.entity.LinenBag;
import com.homestay.linen.entity.User;
import com.homestay.linen.entity.WashBatch;
import com.homestay.linen.repository.DeliveryReceiptRepository;
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
public class DeliveryReceiptService {

    private final DeliveryReceiptRepository deliveryReceiptRepository;
    private final WashBatchRepository washBatchRepository;
    private final UserRepository userRepository;
    private final LinenBagRepository linenBagRepository;

    public Result<DeliveryReceipt> create(DeliveryReceipt deliveryReceipt) {
        try {
            WashBatch batch = washBatchRepository.findById(deliveryReceipt.getWashBatchId()).orElse(null);
            if (batch == null) {
                return Result.error("洗涤批次不存在");
            }
            User driver = userRepository.findById(deliveryReceipt.getDriverId()).orElse(null);
            if (driver == null) {
                return Result.error("司机不存在");
            }
            User warehouse = userRepository.findById(deliveryReceipt.getWarehouseId()).orElse(null);
            if (warehouse == null) {
                return Result.error("库管员不存在");
            }
            deliveryReceipt.setReceiptNo("DR" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase());
            deliveryReceipt.setWashBatchNo(batch.getBatchNo());
            deliveryReceipt.setDriverName(driver.getRealName());
            deliveryReceipt.setWarehouseName(warehouse.getRealName());
            deliveryReceipt.setStatus("配送中");

            List<LinenBag> bags = linenBagRepository.findByWashBatchId(batch.getId());
            int totalBags = bags.size();
            int deliveredQuantity = bags.stream().mapToInt(b -> b.getReturnQuantity() != null ? b.getReturnQuantity() : 0).sum();
            deliveryReceipt.setTotalBags(totalBags);
            deliveryReceipt.setDeliveredQuantity(deliveredQuantity);

            DeliveryReceipt saved = deliveryReceiptRepository.save(deliveryReceipt);
            return Result.success("创建配送单成功", saved);
        } catch (Exception e) {
            return Result.error("创建配送单失败: " + e.getMessage());
        }
    }

    public Result<DeliveryReceipt> deliver(Long id) {
        try {
            DeliveryReceipt receipt = deliveryReceiptRepository.findById(id).orElse(null);
            if (receipt == null) {
                return Result.error("配送单不存在");
            }
            if (!"配送中".equals(receipt.getStatus())) {
                return Result.error("配送单状态不正确，当前状态: " + receipt.getStatus());
            }
            receipt.setDeliveryTime(LocalDateTime.now());
            receipt.setStatus("已送达");
            DeliveryReceipt saved = deliveryReceiptRepository.save(receipt);
            return Result.success("确认送达成功", saved);
        } catch (Exception e) {
            return Result.error("确认送达失败: " + e.getMessage());
        }
    }

    @Transactional
    public Result<DeliveryReceipt> receive(Long id, Integer receivedQuantity, String remark) {
        try {
            DeliveryReceipt receipt = deliveryReceiptRepository.findById(id).orElse(null);
            if (receipt == null) {
                return Result.error("配送单不存在");
            }
            if (!"已送达".equals(receipt.getStatus())) {
                return Result.error("配送单状态不正确，当前状态: " + receipt.getStatus());
            }
            receipt.setReceiveTime(LocalDateTime.now());
            receipt.setReceivedQuantity(receivedQuantity);
            int delivered = receipt.getDeliveredQuantity() != null ? receipt.getDeliveredQuantity() : 0;
            receipt.setDifferenceQuantity(delivered - receivedQuantity);
            receipt.setRemark(remark);
            receipt.setStatus("已签收");
            DeliveryReceipt saved = deliveryReceiptRepository.save(receipt);

            List<LinenBag> bags = linenBagRepository.findByWashBatchId(receipt.getWashBatchId());
            for (LinenBag bag : bags) {
                bag.setStatus("已回库");
                bag.setWarehouseId(receipt.getWarehouseId());
                bag.setWarehouseName(receipt.getWarehouseName());
                bag.setWarehouseCheckTime(LocalDateTime.now());
                bag.setWarehouseQuantity(bag.getReturnQuantity());
                linenBagRepository.save(bag);
            }

            return Result.success("签收成功", saved);
        } catch (Exception e) {
            return Result.error("签收失败: " + e.getMessage());
        }
    }

    public Result<List<DeliveryReceipt>> list() {
        try {
            List<DeliveryReceipt> receipts = deliveryReceiptRepository.findAll();
            return Result.success(receipts);
        } catch (Exception e) {
            return Result.error("查询配送单列表失败: " + e.getMessage());
        }
    }

    public Result<DeliveryReceipt> getById(Long id) {
        try {
            DeliveryReceipt receipt = deliveryReceiptRepository.findById(id).orElse(null);
            if (receipt == null) {
                return Result.error("配送单不存在");
            }
            return Result.success(receipt);
        } catch (Exception e) {
            return Result.error("查询配送单失败: " + e.getMessage());
        }
    }

    public Result<DeliveryReceipt> getByReceiptNo(String receiptNo) {
        try {
            DeliveryReceipt receipt = deliveryReceiptRepository.findByReceiptNo(receiptNo).orElse(null);
            if (receipt == null) {
                return Result.error("配送单不存在");
            }
            return Result.success(receipt);
        } catch (Exception e) {
            return Result.error("查询配送单失败: " + e.getMessage());
        }
    }

    public Result<List<DeliveryReceipt>> listByStatus(String status) {
        try {
            List<DeliveryReceipt> receipts = deliveryReceiptRepository.findByStatus(status);
            return Result.success(receipts);
        } catch (Exception e) {
            return Result.error("按状态查询配送单失败: " + e.getMessage());
        }
    }
}
