package com.homestay.linen.config;

import com.homestay.linen.entity.*;
import com.homestay.linen.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final LinenTypeRepository linenTypeRepository;
    private final LinenStockRepository linenStockRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        initUsers();
        initRooms();
        initLinenTypes();
    }

    private void initUsers() {
        if (userRepository.count() > 0) {
            return;
        }

        User reception = new User();
        reception.setUsername("reception");
        reception.setPassword("123456");
        reception.setRealName("前台小王");
        reception.setRole("reception");
        reception.setPhone("13800138001");
        reception.setEnabled(true);

        User cleaner = new User();
        cleaner.setUsername("cleaner");
        cleaner.setPassword("123456");
        cleaner.setRealName("保洁阿姨");
        cleaner.setRole("cleaner");
        cleaner.setPhone("13800138002");
        cleaner.setEnabled(true);

        User driver = new User();
        driver.setUsername("driver");
        driver.setPassword("123456");
        driver.setRealName("司机老张");
        driver.setRole("driver");
        driver.setPhone("13800138003");
        driver.setEnabled(true);

        User factory = new User();
        factory.setUsername("factory");
        factory.setPassword("123456");
        factory.setRealName("洗涤厂李工");
        factory.setRole("factory");
        factory.setPhone("13800138004");
        factory.setEnabled(true);

        User warehouse = new User();
        warehouse.setUsername("warehouse");
        warehouse.setPassword("123456");
        warehouse.setRealName("库管赵姐");
        warehouse.setRole("warehouse");
        warehouse.setPhone("13800138005");
        warehouse.setEnabled(true);

        User manager = new User();
        manager.setUsername("manager");
        manager.setPassword("123456");
        manager.setRealName("店长钱总");
        manager.setRole("manager");
        manager.setPhone("13800138006");
        manager.setEnabled(true);

        userRepository.saveAll(List.of(reception, cleaner, driver, factory, warehouse, manager));
    }

    private void initRooms() {
        if (roomRepository.count() > 0) {
            return;
        }

        String[] roomTypes = {"大床房", "双床房", "大床房", "双床房", "豪华大床房",
                "豪华双床房", "大床房", "双床房", "套房", "总统套房"};

        for (int i = 1; i <= 10; i++) {
            Room room = new Room();
            room.setRoomNo("10" + i);
            room.setRoomType(roomTypes[i - 1]);
            room.setFloor(1);
            room.setStatus("空闲");
            room.setRemark("初始化房间");
            roomRepository.save(room);
        }
    }

    private void initLinenTypes() {
        if (linenTypeRepository.count() > 0) {
            return;
        }

        LinenType sheet = new LinenType();
        sheet.setName("床单");
        sheet.setDescription("标准尺寸床单");
        sheet.setUnitPrice(new BigDecimal("80"));
        sheet.setWashCost(new BigDecimal("5"));
        sheet.setStandardWeight(200);
        sheet.setPerRoomQuantity(1);

        LinenType duvet = new LinenType();
        duvet.setName("被套");
        duvet.setDescription("标准尺寸被套");
        duvet.setUnitPrice(new BigDecimal("150"));
        duvet.setWashCost(new BigDecimal("8"));
        duvet.setStandardWeight(350);
        duvet.setPerRoomQuantity(1);

        LinenType bathTowel = new LinenType();
        bathTowel.setName("浴巾");
        bathTowel.setDescription("标准浴巾");
        bathTowel.setUnitPrice(new BigDecimal("50"));
        bathTowel.setWashCost(new BigDecimal("3"));
        bathTowel.setStandardWeight(400);
        bathTowel.setPerRoomQuantity(2);

        LinenType bathrobe = new LinenType();
        bathrobe.setName("浴袍");
        bathrobe.setDescription("标准浴袍");
        bathrobe.setUnitPrice(new BigDecimal("200"));
        bathrobe.setWashCost(new BigDecimal("10"));
        bathrobe.setStandardWeight(600);
        bathrobe.setPerRoomQuantity(2);

        LinenType floorTowel = new LinenType();
        floorTowel.setName("地巾");
        floorTowel.setDescription("浴室地巾");
        floorTowel.setUnitPrice(new BigDecimal("30"));
        floorTowel.setWashCost(new BigDecimal("2"));
        floorTowel.setStandardWeight(250);
        floorTowel.setPerRoomQuantity(1);

        List<LinenType> linenTypes = linenTypeRepository.saveAll(List.of(sheet, duvet, bathTowel, bathrobe, floorTowel));

        for (LinenType lt : linenTypes) {
            LinenStock stock = new LinenStock();
            stock.setLinenTypeId(lt.getId());
            stock.setLinenTypeName(lt.getName());
            int perRoom = lt.getPerRoomQuantity();
            int total = perRoom * 10 * 3;
            stock.setTotalQuantity(total);
            stock.setInWarehouseQuantity(total);
            stock.setInUseQuantity(0);
            stock.setInWashQuantity(0);
            stock.setInDeliveryQuantity(0);
            stock.setDamageQuantity(0);
            stock.setSafetyStock(perRoom * 10);
            linenStockRepository.save(stock);
        }
    }
}
