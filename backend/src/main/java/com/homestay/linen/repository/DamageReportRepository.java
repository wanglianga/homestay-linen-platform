package com.homestay.linen.repository;

import com.homestay.linen.entity.DamageReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DamageReportRepository extends JpaRepository<DamageReport, Long> {
    Optional<DamageReport> findByReportNo(String reportNo);
    List<DamageReport> findByStatus(String status);
    List<DamageReport> findByLinenBagId(Long linenBagId);
    List<DamageReport> findByRoomId(Long roomId);
}
