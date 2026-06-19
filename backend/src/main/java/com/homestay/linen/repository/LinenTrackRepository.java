package com.homestay.linen.repository;

import com.homestay.linen.entity.LinenTrack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LinenTrackRepository extends JpaRepository<LinenTrack, Long> {
    List<LinenTrack> findByLinenBagIdOrderByActionTimeAsc(Long linenBagId);
    List<LinenTrack> findByBagCodeOrderByActionTimeAsc(String bagCode);
    List<LinenTrack> findByRoomNoOrderByActionTimeDesc(String roomNo);
    List<LinenTrack> findByOperatorIdOrderByActionTimeDesc(Long operatorId);
}
