package com.homestay.linen.repository;

import com.homestay.linen.entity.LinenBag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LinenBagRepository extends JpaRepository<LinenBag, Long> {
    Optional<LinenBag> findByBagCode(String bagCode);
    List<LinenBag> findByCheckoutId(Long checkoutId);
    List<LinenBag> findByRoomId(Long roomId);
    List<LinenBag> findByCleanerId(Long cleanerId);
    List<LinenBag> findByStatus(String status);
    List<LinenBag> findByWashBatchId(Long washBatchId);
    List<LinenBag> findByDriverIdAndStatus(Long driverId, String status);
    List<LinenBag> findByWarehouseIdAndStatus(Long warehouseId, String status);
}
