package com.homestay.linen.controller;

import com.homestay.linen.common.Result;
import com.homestay.linen.entity.Room;
import com.homestay.linen.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/room")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public Result<Room> create(@RequestBody Room room) {
        return roomService.create(room);
    }

    @PutMapping("/{id}")
    public Result<Room> update(@PathVariable Long id, @RequestBody Room room) {
        return roomService.update(id, room);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        return roomService.delete(id);
    }

    @GetMapping("/{id}")
    public Result<Room> getById(@PathVariable Long id) {
        return roomService.getById(id);
    }

    @GetMapping("/list")
    public Result<List<Room>> list() {
        return roomService.list();
    }

    @GetMapping("/listByStatus")
    public Result<List<Room>> listByStatus(@RequestParam String status) {
        return roomService.listByStatus(status);
    }

    @GetMapping("/listByFloor")
    public Result<List<Room>> listByFloor(@RequestParam Integer floor) {
        return roomService.listByFloor(floor);
    }
}
