package com.homestay.linen.service;

import com.homestay.linen.common.Result;
import com.homestay.linen.entity.*;
import com.homestay.linen.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoomRestockService {

    private final RoomRestockRepository roomRestockRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final LinenTypeRepository linenTypeRepository;
    private final LinenStockRepository linenStockRepository;
    private final LinenTrackRepository linenTrackRepository;

    @Transactional
    public Result<RoomRestock> restockRoom(RoomRestock restock) {
        try {
            Room room = roomRepository.findById(restock.getRoomId()).orElse(null);
            if (room == null) {
                return Result.error("房间不存在");
            }
            User warehouse = userRepository.findById(restock.getWarehouseId()).orElse(null);
            if (warehouse == null) {
                return Result.error("库管员不存在");
            }
            LinenType linenType = linenTypeRepository.findById(restock.getLinenTypeId()).orElse(null);
            if (linenType == null) {
                return Result.error("布草类型不存在");
            }
            LinenStock stock = linenStockRepository.findByLinenTypeId(linenType.getId()).orElse(null);
            if (stock == null || stock.getInWarehouseQuantity() < restock.getQuantity()) {
                return Result.error("仓库库存不足，当前可用: " + (stock != null ? stock.getInWarehouseQuantity() : 0));
            }

            restock.setRestockNo("RR" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase());
            restock.setRoomNo(room.getRoomNo());
            restock.setWarehouseName(warehouse.getRealName());
            restock.setLinenTypeName(linenType.getName());
            if (restock.getRestockTime() == null) {
                restock.setRestockTime(LocalDateTime.now());
            }
            RoomRestock saved = roomRestockRepository.save(restock);

            stock.setInWarehouseQuantity(stock.getInWarehouseQuantity() - restock.getQuantity());
            stock.setInUseQuantity(stock.getInUseQuantity() + restock.getQuantity());
            linenStockRepository.save(stock);

            LinenTrack track = new LinenTrack();
            track.setBagCode("RESTOCK-" + saved.getRestockNo());
            track.setLinenTypeName(linenType.getName());
            track.setRoomNo(room.getRoomNo());
            track.setAction("房间补货");
            track.setActionDetail("为房间" + room.getRoomNo() + "补货" + linenType.getName() + " " + restock.getQuantity() + "件");
            track.setOperatorId(warehouse.getId());
            track.setOperatorName(warehouse.getRealName());
            track.setOperatorRole(warehouse.getRole());
            track.setQuantity(restock.getQuantity());
            track.setLocationFrom("仓库");
            track.setLocationTo("房间" + room.getRoomNo());
            track.setActionTime(LocalDateTime.now());
            linenTrackRepository.save(track);

            return Result.success("房间补货成功", saved);
        } catch (Exception e) {
            return Result.error("房间补货失败: " + e.getMessage());
        }
    }

    public Result<List<RoomRestock>> listByRoom(Long roomId) {
        try {
            List<RoomRestock> restocks = roomRestockRepository.findByRoomId(roomId);
            return Result.success(restocks);
        } catch (Exception e) {
            return Result.error("按房间查询补货记录失败: " + e.getMessage());
        }
    }

    public Result<List<RoomRestock>> listByWarehouse(Long warehouseId) {
        try {
            List<RoomRestock> restocks = roomRestockRepository.findByWarehouseId(warehouseId);
            return Result.success(restocks);
        } catch (Exception e) {
            return Result.error("按库管员查询补货记录失败: " + e.getMessage());
        }
    }

    public Result<List<RoomRestock>> list() {
        try {
            List<RoomRestock> restocks = roomRestockRepository.findAll();
            return Result.success(restocks);
        } catch (Exception e) {
            return Result.error("查询补货记录列表失败: " + e.getMessage());
        }
    }

    public Result<RoomRestock> getById(Long id) {
        try {
            RoomRestock restock = roomRestockRepository.findById(id).orElse(null);
            if (restock == null) {
                return Result.error("补货记录不存在");
            }
            return Result.success(restock);
        } catch (Exception e) {
            return Result.error("查询补货记录失败: " + e.getMessage());
        }
    }

    public Result<RoomRestock> getByRestockNo(String restockNo) {
        try {
            RoomRestock restock = roomRestockRepository.findByRestockNo(restockNo).orElse(null);
            if (restock == null) {
                return Result.error("补货记录不存在");
            }
            return Result.success(restock);
        } catch (Exception e) {
            return Result.error("查询补货记录失败: " + e.getMessage());
        }
    }
}
