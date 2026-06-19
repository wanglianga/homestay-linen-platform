package com.homestay.linen.repository;

import com.homestay.linen.entity.LinenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LinenTypeRepository extends JpaRepository<LinenType, Long> {
}
