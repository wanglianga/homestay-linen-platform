package com.homestay.linen.repository;

import com.homestay.linen.entity.CostAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CostAllocationRepository extends JpaRepository<CostAllocation, Long> {
    List<CostAllocation> findByStatDateBetween(LocalDate start, LocalDate end);
    List<CostAllocation> findByLinenTypeIdAndStatDateBetween(Long linenTypeId, LocalDate start, LocalDate end);
}
