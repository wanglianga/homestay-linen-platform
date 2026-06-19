package com.homestay.linen.service;

import com.homestay.linen.common.Result;
import com.homestay.linen.entity.LinenBag;
import com.homestay.linen.entity.LinenTrack;
import com.homestay.linen.entity.User;
import com.homestay.linen.repository.LinenBagRepository;
import com.homestay.linen.repository.LinenTrackRepository;
import com.homestay.linen.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LinenTrackService {

    private final LinenTrackRepository linenTrackRepository;
    private final LinenBagRepository linenBagRepository;
    private final UserRepository userRepository;

    public Result<LinenTrack> create(LinenTrack track) {
        try {
            if (track.getLinenBagId() != null) {
                LinenBag bag = linenBagRepository.findById(track.getLinenBagId()).orElse(null);
                if (bag != null) {
                    if (track.getBagCode() == null) {
                        track.setBagCode(bag.getBagCode());
                    }
                    if (track.getLinenTypeName() == null) {
                        track.setLinenTypeName(bag.getLinenTypeName());
                    }
                    if (track.getRoomNo() == null) {
                        track.setRoomNo(bag.getRoomNo());
                    }
                }
            }
            if (track.getOperatorId() != null) {
                User operator = userRepository.findById(track.getOperatorId()).orElse(null);
                if (operator != null) {
                    if (track.getOperatorName() == null) {
                        track.setOperatorName(operator.getRealName());
                    }
                    if (track.getOperatorRole() == null) {
                        track.setOperatorRole(operator.getRole());
                    }
                }
            }
            if (track.getActionTime() == null) {
                track.setActionTime(LocalDateTime.now());
            }
            LinenTrack saved = linenTrackRepository.save(track);
            return Result.success("创建追踪记录成功", saved);
        } catch (Exception e) {
            return Result.error("创建追踪记录失败: " + e.getMessage());
        }
    }

    public Result<List<LinenTrack>> getByBagId(Long bagId) {
        try {
            List<LinenTrack> tracks = linenTrackRepository.findByLinenBagIdOrderByActionTimeAsc(bagId);
            return Result.success(tracks);
        } catch (Exception e) {
            return Result.error("按布草袋查询追踪记录失败: " + e.getMessage());
        }
    }

    public Result<List<LinenTrack>> getByBagCode(String bagCode) {
        try {
            List<LinenTrack> tracks = linenTrackRepository.findByBagCodeOrderByActionTimeAsc(bagCode);
            return Result.success(tracks);
        } catch (Exception e) {
            return Result.error("按布草袋编号查询追踪记录失败: " + e.getMessage());
        }
    }

    public Result<List<LinenTrack>> getByRoomNo(String roomNo) {
        try {
            List<LinenTrack> tracks = linenTrackRepository.findByRoomNoOrderByActionTimeDesc(roomNo);
            return Result.success(tracks);
        } catch (Exception e) {
            return Result.error("按房间号查询追踪记录失败: " + e.getMessage());
        }
    }

    public Result<List<LinenTrack>> list() {
        try {
            List<LinenTrack> tracks = linenTrackRepository.findAll();
            return Result.success(tracks);
        } catch (Exception e) {
            return Result.error("查询追踪记录列表失败: " + e.getMessage());
        }
    }
}
