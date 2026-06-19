package com.homestay.linen.repository;

import com.homestay.linen.entity.CheckoutRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CheckoutRecordRepository extends JpaRepository<CheckoutRecord, Long> {
    List<CheckoutRecord> findByRoomId(Long roomId);
    List<CheckoutRecord> findByCleanerId(Long cleanerId);
    List<CheckoutRecord> findByCheckoutTimeBetween(LocalDateTime start, LocalDateTime end);
}
