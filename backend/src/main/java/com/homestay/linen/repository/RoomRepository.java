package com.homestay.linen.repository;

import com.homestay.linen.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByRoomNo(String roomNo);
    List<Room> findByStatus(String status);
    List<Room> findByFloor(Integer floor);
}
