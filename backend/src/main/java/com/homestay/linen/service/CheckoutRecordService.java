package com.homestay.linen.service;

import com.homestay.linen.common.Result;
import com.homestay.linen.entity.CheckoutRecord;
import com.homestay.linen.entity.Room;
import com.homestay.linen.entity.User;
import com.homestay.linen.repository.CheckoutRecordRepository;
import com.homestay.linen.repository.RoomRepository;
import com.homestay.linen.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckoutRecordService {

    private final CheckoutRecordRepository checkoutRecordRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public Result<CheckoutRecord> create(CheckoutRecord checkoutRecord) {
        try {
            Room room = roomRepository.findById(checkoutRecord.getRoomId()).orElse(null);
            if (room == null) {
                return Result.error("房间不存在");
            }
            User cleaner = userRepository.findById(checkoutRecord.getCleanerId()).orElse(null);
            if (cleaner == null) {
                return Result.error("保洁员不存在");
            }
            checkoutRecord.setRoomNo(room.getRoomNo());
            checkoutRecord.setCleanerName(cleaner.getRealName());
            CheckoutRecord saved = checkoutRecordRepository.save(checkoutRecord);
            return Result.success("创建退房记录成功", saved);
        } catch (Exception e) {
            return Result.error("创建退房记录失败: " + e.getMessage());
        }
    }

    public Result<List<CheckoutRecord>> list() {
        try {
            List<CheckoutRecord> records = checkoutRecordRepository.findAll();
            return Result.success(records);
        } catch (Exception e) {
            return Result.error("查询退房记录列表失败: " + e.getMessage());
        }
    }

    public Result<List<CheckoutRecord>> listByRoom(Long roomId) {
        try {
            List<CheckoutRecord> records = checkoutRecordRepository.findByRoomId(roomId);
            return Result.success(records);
        } catch (Exception e) {
            return Result.error("按房间查询退房记录失败: " + e.getMessage());
        }
    }

    public Result<List<CheckoutRecord>> listByCleaner(Long cleanerId) {
        try {
            List<CheckoutRecord> records = checkoutRecordRepository.findByCleanerId(cleanerId);
            return Result.success(records);
        } catch (Exception e) {
            return Result.error("按保洁员查询退房记录失败: " + e.getMessage());
        }
    }

    public Result<CheckoutRecord> getById(Long id) {
        try {
            CheckoutRecord record = checkoutRecordRepository.findById(id).orElse(null);
            if (record == null) {
                return Result.error("退房记录不存在");
            }
            return Result.success(record);
        } catch (Exception e) {
            return Result.error("查询退房记录失败: " + e.getMessage());
        }
    }
}
