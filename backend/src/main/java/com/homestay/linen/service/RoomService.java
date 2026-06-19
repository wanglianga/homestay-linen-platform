package com.homestay.linen.service;

import com.homestay.linen.common.Result;
import com.homestay.linen.entity.Room;
import com.homestay.linen.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    public Result<Room> create(Room room) {
        try {
            if (roomRepository.findByRoomNo(room.getRoomNo()).isPresent()) {
                return Result.error("房间号已存在");
            }
            Room saved = roomRepository.save(room);
            return Result.success("创建房间成功", saved);
        } catch (Exception e) {
            return Result.error("创建房间失败: " + e.getMessage());
        }
    }

    public Result<Room> update(Long id, Room room) {
        try {
            Room existing = roomRepository.findById(id).orElse(null);
            if (existing == null) {
                return Result.error("房间不存在");
            }
            if (!existing.getRoomNo().equals(room.getRoomNo())
                    && roomRepository.findByRoomNo(room.getRoomNo()).isPresent()) {
                return Result.error("房间号已存在");
            }
            room.setId(id);
            Room saved = roomRepository.save(room);
            return Result.success("更新房间成功", saved);
        } catch (Exception e) {
            return Result.error("更新房间失败: " + e.getMessage());
        }
    }

    public Result<Void> delete(Long id) {
        try {
            if (!roomRepository.existsById(id)) {
                return Result.error("房间不存在");
            }
            roomRepository.deleteById(id);
            return Result.<Void>success();
        } catch (Exception e) {
            return Result.error("删除房间失败: " + e.getMessage());
        }
    }

    public Result<Room> getById(Long id) {
        try {
            Room room = roomRepository.findById(id).orElse(null);
            if (room == null) {
                return Result.error("房间不存在");
            }
            return Result.success(room);
        } catch (Exception e) {
            return Result.error("查询房间失败: " + e.getMessage());
        }
    }

    public Result<List<Room>> list() {
        try {
            List<Room> rooms = roomRepository.findAll();
            return Result.success(rooms);
        } catch (Exception e) {
            return Result.error("查询房间列表失败: " + e.getMessage());
        }
    }

    public Result<List<Room>> listByStatus(String status) {
        try {
            List<Room> rooms = roomRepository.findByStatus(status);
            return Result.success(rooms);
        } catch (Exception e) {
            return Result.error("按状态查询房间失败: " + e.getMessage());
        }
    }

    public Result<List<Room>> listByFloor(Integer floor) {
        try {
            List<Room> rooms = roomRepository.findByFloor(floor);
            return Result.success(rooms);
        } catch (Exception e) {
            return Result.error("按楼层查询房间失败: " + e.getMessage());
        }
    }
}
