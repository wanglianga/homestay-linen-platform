package com.homestay.linen.repository;

import com.homestay.linen.entity.WashBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WashBatchRepository extends JpaRepository<WashBatch, Long> {
    Optional<WashBatch> findByBatchNo(String batchNo);
    List<WashBatch> findByStatus(String status);
    List<WashBatch> findByFactoryId(Long factoryId);
}
