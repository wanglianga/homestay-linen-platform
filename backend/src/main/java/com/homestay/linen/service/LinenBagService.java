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
public class LinenBagService {

    private final LinenBagRepository linenBagRepository;
    private final CheckoutRecordRepository checkoutRecordRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final LinenTypeRepository linenTypeRepository;
    private final LinenStockRepository linenStockRepository;
    private final LinenTrackRepository linenTrackRepository;

    public Result<LinenBag> create(LinenBag linenBag) {
        try {
            CheckoutRecord checkout = checkoutRecordRepository.findById(linenBag.getCheckoutId()).orElse(null);
            if (checkout == null) {
                return Result.error("退房记录不存在");
            }
            Room room = roomRepository.findById(linenBag.getRoomId()).orElse(null);
            if (room == null) {
                return Result.error("房间不存在");
            }
            User cleaner = userRepository.findById(linenBag.getCleanerId()).orElse(null);
            if (cleaner == null) {
                return Result.error("保洁员不存在");
            }
            LinenType linenType = linenTypeRepository.findById(linenBag.getLinenTypeId()).orElse(null);
            if (linenType == null) {
                return Result.error("布草类型不存在");
            }
            linenBag.setBagCode("BAG" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase());
            linenBag.setRoomNo(room.getRoomNo());
            linenBag.setCleanerName(cleaner.getRealName());
            linenBag.setLinenTypeName(linenType.getName());
            linenBag.setStatus("待取件");
            LinenBag saved = linenBagRepository.save(linenBag);

            LinenStock stock = linenStockRepository.findByLinenTypeId(linenType.getId()).orElse(null);
            if (stock != null) {
                stock.setInUseQuantity(stock.getInUseQuantity() + linenBag.getQuantity());
                stock.setInWarehouseQuantity(stock.getInWarehouseQuantity() - linenBag.getQuantity());
                linenStockRepository.save(stock);
            }

            saveTrack(saved, cleaner, "装袋", "布草装袋完成，数量: " + linenBag.getQuantity() + "件", "房间", "待取件", linenBag.getQuantity());

            return Result.success("创建布草袋成功", saved);
        } catch (Exception e) {
            return Result.error("创建布草袋失败: " + e.getMessage());
        }
    }

    public Result<List<LinenBag>> list() {
        try {
            List<LinenBag> bags = linenBagRepository.findAll();
            return Result.success(bags);
        } catch (Exception e) {
            return Result.error("查询布草袋列表失败: " + e.getMessage());
        }
    }

    public Result<LinenBag> getByBagCode(String bagCode) {
        try {
            LinenBag bag = linenBagRepository.findByBagCode(bagCode).orElse(null);
            if (bag == null) {
                return Result.error("布草袋不存在");
            }
            return Result.success(bag);
        } catch (Exception e) {
            return Result.error("查询布草袋失败: " + e.getMessage());
        }
    }

    public Result<LinenBag> updateStatus(Long id, String status) {
        try {
            LinenBag bag = linenBagRepository.findById(id).orElse(null);
            if (bag == null) {
                return Result.error("布草袋不存在");
            }
            bag.setStatus(status);
            LinenBag saved = linenBagRepository.save(bag);
            return Result.success("更新状态成功", saved);
        } catch (Exception e) {
            return Result.error("更新状态失败: " + e.getMessage());
        }
    }

    @Transactional
    public Result<LinenBag> pickupByDriver(Long id, Long driverId, Integer pickupWeight) {
        try {
            LinenBag bag = linenBagRepository.findById(id).orElse(null);
            if (bag == null) {
                return Result.error("布草袋不存在");
            }
            if (!"待取件".equals(bag.getStatus())) {
                return Result.error("布草袋状态不正确，当前状态: " + bag.getStatus());
            }
            User driver = userRepository.findById(driverId).orElse(null);
            if (driver == null) {
                return Result.error("司机不存在");
            }
            bag.setDriverId(driverId);
            bag.setDriverName(driver.getRealName());
            bag.setPickupWeight(pickupWeight);
            bag.setPickupTime(LocalDateTime.now());
            bag.setStatus("配送中");
            LinenBag saved = linenBagRepository.save(bag);

            LinenStock stock = linenStockRepository.findByLinenTypeId(bag.getLinenTypeId()).orElse(null);
            if (stock != null) {
                stock.setInUseQuantity(stock.getInUseQuantity() - bag.getQuantity());
                stock.setInDeliveryQuantity(stock.getInDeliveryQuantity() + bag.getQuantity());
                linenStockRepository.save(stock);
            }

            saveTrack(saved, driver, "司机取件", "司机取件完成，重量: " + pickupWeight + "g", "待取件", "配送中", bag.getQuantity());

            return Result.success("司机取件成功", saved);
        } catch (Exception e) {
            return Result.error("司机取件失败: " + e.getMessage());
        }
    }

    @Transactional
    public Result<LinenBag> factoryCheck(Long id, Integer factoryQuantity, String factoryRemark) {
        try {
            LinenBag bag = linenBagRepository.findById(id).orElse(null);
            if (bag == null) {
                return Result.error("布草袋不存在");
            }
            if (!"配送中".equals(bag.getStatus())) {
                return Result.error("布草袋状态不正确，当前状态: " + bag.getStatus());
            }
            bag.setFactoryCheckTime(LocalDateTime.now());
            bag.setFactoryQuantity(factoryQuantity);
            bag.setFactoryRemark(factoryRemark);
            bag.setStatus("洗涤中");
            LinenBag saved = linenBagRepository.save(bag);

            LinenStock stock = linenStockRepository.findByLinenTypeId(bag.getLinenTypeId()).orElse(null);
            if (stock != null) {
                stock.setInDeliveryQuantity(stock.getInDeliveryQuantity() - bag.getQuantity());
                stock.setInWashQuantity(stock.getInWashQuantity() + factoryQuantity);
                linenStockRepository.save(stock);
            }

            User factory = userRepository.findByRole("factory").stream().findFirst().orElse(null);
            if (factory != null) {
                saveTrack(saved, factory, "洗涤厂验收", "洗涤厂验收，实收: " + factoryQuantity + "件", "配送中", "洗涤中", factoryQuantity);
            }

            return Result.success("洗涤厂验收成功", saved);
        } catch (Exception e) {
            return Result.error("洗涤厂验收失败: " + e.getMessage());
        }
    }

    @Transactional
    public Result<LinenBag> returnFromFactory(Long id, Integer returnQuantity) {
        try {
            LinenBag bag = linenBagRepository.findById(id).orElse(null);
            if (bag == null) {
                return Result.error("布草袋不存在");
            }
            if (!"洗涤中".equals(bag.getStatus())) {
                return Result.error("布草袋状态不正确，当前状态: " + bag.getStatus());
            }
            bag.setReturnQuantity(returnQuantity);
            bag.setReturnTime(LocalDateTime.now());
            bag.setStatus("待回库");
            LinenBag saved = linenBagRepository.save(bag);

            LinenStock stock = linenStockRepository.findByLinenTypeId(bag.getLinenTypeId()).orElse(null);
            if (stock != null) {
                stock.setInWashQuantity(stock.getInWashQuantity() - bag.getFactoryQuantity());
                stock.setInDeliveryQuantity(stock.getInDeliveryQuantity() + returnQuantity);
                if (bag.getFactoryQuantity() != null && bag.getFactoryQuantity() > returnQuantity) {
                    stock.setDamageQuantity(stock.getDamageQuantity() + (bag.getFactoryQuantity() - returnQuantity));
                }
                linenStockRepository.save(stock);
            }

            User factory = userRepository.findByRole("factory").stream().findFirst().orElse(null);
            if (factory != null) {
                saveTrack(saved, factory, "洗涤完成", "洗涤完成，返还数量: " + returnQuantity + "件", "洗涤中", "待回库", returnQuantity);
            }

            return Result.success("洗涤厂返还成功", saved);
        } catch (Exception e) {
            return Result.error("洗涤厂返还失败: " + e.getMessage());
        }
    }

    @Transactional
    public Result<LinenBag> warehouseCheck(Long id, Long warehouseId, Integer warehouseQuantity, String warehouseRemark) {
        try {
            LinenBag bag = linenBagRepository.findById(id).orElse(null);
            if (bag == null) {
                return Result.error("布草袋不存在");
            }
            if (!"待回库".equals(bag.getStatus())) {
                return Result.error("布草袋状态不正确，当前状态: " + bag.getStatus());
            }
            User warehouse = userRepository.findById(warehouseId).orElse(null);
            if (warehouse == null) {
                return Result.error("库管员不存在");
            }
            bag.setWarehouseId(warehouseId);
            bag.setWarehouseName(warehouse.getRealName());
            bag.setWarehouseCheckTime(LocalDateTime.now());
            bag.setWarehouseQuantity(warehouseQuantity);
            bag.setWarehouseRemark(warehouseRemark);
            bag.setStatus("已回库");
            LinenBag saved = linenBagRepository.save(bag);

            LinenStock stock = linenStockRepository.findByLinenTypeId(bag.getLinenTypeId()).orElse(null);
            if (stock != null) {
                stock.setInDeliveryQuantity(stock.getInDeliveryQuantity() - bag.getReturnQuantity());
                stock.setInWarehouseQuantity(stock.getInWarehouseQuantity() + warehouseQuantity);
                if (bag.getReturnQuantity() != null && bag.getReturnQuantity() > warehouseQuantity) {
                    stock.setDamageQuantity(stock.getDamageQuantity() + (bag.getReturnQuantity() - warehouseQuantity));
                }
                linenStockRepository.save(stock);
            }

            saveTrack(saved, warehouse, "仓库验收", "仓库验收回库，实收: " + warehouseQuantity + "件", "待回库", "已回库", warehouseQuantity);

            return Result.success("仓库验收成功", saved);
        } catch (Exception e) {
            return Result.error("仓库验收失败: " + e.getMessage());
        }
    }

    public Result<LinenBag> getById(Long id) {
        try {
            LinenBag bag = linenBagRepository.findById(id).orElse(null);
            if (bag == null) {
                return Result.error("布草袋不存在");
            }
            return Result.success(bag);
        } catch (Exception e) {
            return Result.error("查询布草袋失败: " + e.getMessage());
        }
    }

    public Result<List<LinenBag>> listByStatus(String status) {
        try {
            List<LinenBag> bags = linenBagRepository.findByStatus(status);
            return Result.success(bags);
        } catch (Exception e) {
            return Result.error("按状态查询布草袋失败: " + e.getMessage());
        }
    }

    public Result<List<LinenBag>> getByCheckoutId(Long checkoutId) {
        try {
            List<LinenBag> bags = linenBagRepository.findByCheckoutId(checkoutId);
            return Result.success(bags);
        } catch (Exception e) {
            return Result.error("按退房记录查询布草袋失败: " + e.getMessage());
        }
    }

    private void saveTrack(LinenBag bag, User operator, String action, String actionDetail, String locationFrom, String locationTo, Integer quantity) {
        LinenTrack track = new LinenTrack();
        track.setLinenBagId(bag.getId());
        track.setBagCode(bag.getBagCode());
        track.setLinenTypeName(bag.getLinenTypeName());
        track.setRoomNo(bag.getRoomNo());
        track.setAction(action);
        track.setActionDetail(actionDetail);
        track.setOperatorId(operator.getId());
        track.setOperatorName(operator.getRealName());
        track.setOperatorRole(operator.getRole());
        track.setQuantity(quantity);
        track.setLocationFrom(locationFrom);
        track.setLocationTo(locationTo);
        track.setActionTime(LocalDateTime.now());
        linenTrackRepository.save(track);
    }
}
