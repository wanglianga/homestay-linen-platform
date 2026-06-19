package com.homestay.linen.repository;

import com.homestay.linen.entity.DeliveryReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryReceiptRepository extends JpaRepository<DeliveryReceipt, Long> {
    Optional<DeliveryReceipt> findByReceiptNo(String receiptNo);
    List<DeliveryReceipt> findByStatus(String status);
    List<DeliveryReceipt> findByWashBatchId(Long washBatchId);
    List<DeliveryReceipt> findByWarehouseId(Long warehouseId);
    List<DeliveryReceipt> findByDriverId(Long driverId);
}
