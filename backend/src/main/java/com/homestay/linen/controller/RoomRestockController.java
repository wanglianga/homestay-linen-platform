package com.homestay.linen.controller;

import com.homestay.linen.common.Result;
import com.homestay.linen.entity.RoomRestock;
import com.homestay.linen.service.RoomRestockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roomRestock")
@RequiredArgsConstructor
public class RoomRestockController {

    private final RoomRestockService roomRestockService;

    @PostMapping
    public Result<RoomRestock> restockRoom(@RequestBody RoomRestock roomRestock) {
        return roomRestockService.restockRoom(roomRestock);
    }

    @GetMapping("/listByRoom")
    public Result<List<RoomRestock>> listByRoom(@RequestParam Long roomId) {
        return roomRestockService.listByRoom(roomId);
    }

    @GetMapping("/listByWarehouse")
    public Result<List<RoomRestock>> listByWarehouse(@RequestParam Long warehouseId) {
        return roomRestockService.listByWarehouse(warehouseId);
    }

    @GetMapping("/list")
    public Result<List<RoomRestock>> list() {
        return roomRestockService.list();
    }

    @GetMapping("/{id}")
    public Result<RoomRestock> getById(@PathVariable Long id) {
        return roomRestockService.getById(id);
    }

    @GetMapping("/restockNo/{restockNo}")
    public Result<RoomRestock> getByRestockNo(@PathVariable String restockNo) {
        return roomRestockService.getByRestockNo(restockNo);
    }
}
