package com.homestay.linen.repository;

import com.homestay.linen.entity.RoomRestock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRestockRepository extends JpaRepository<RoomRestock, Long> {
    Optional<RoomRestock> findByRestockNo(String restockNo);
    List<RoomRestock> findByRoomId(Long roomId);
    List<RoomRestock> findByWarehouseId(Long warehouseId);
}
