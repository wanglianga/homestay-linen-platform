package com.homestay.linen.repository;

import com.homestay.linen.entity.LinenStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LinenStockRepository extends JpaRepository<LinenStock, Long> {
    Optional<LinenStock> findByLinenTypeId(Long linenTypeId);
}
