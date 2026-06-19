package com.homestay.linen.controller;

import com.homestay.linen.common.Result;
import com.homestay.linen.entity.CheckoutRecord;
import com.homestay.linen.service.CheckoutRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/checkoutRecord")
@RequiredArgsConstructor
public class CheckoutRecordController {

    private final CheckoutRecordService checkoutRecordService;

    @PostMapping
    public Result<CheckoutRecord> create(@RequestBody CheckoutRecord checkoutRecord) {
        return checkoutRecordService.create(checkoutRecord);
    }

    @GetMapping("/list")
    public Result<List<CheckoutRecord>> list() {
        return checkoutRecordService.list();
    }

    @GetMapping("/listByRoom")
    public Result<List<CheckoutRecord>> listByRoom(@RequestParam Long roomId) {
        return checkoutRecordService.listByRoom(roomId);
    }

    @GetMapping("/listByCleaner")
    public Result<List<CheckoutRecord>> listByCleaner(@RequestParam Long cleanerId) {
        return checkoutRecordService.listByCleaner(cleanerId);
    }

    @GetMapping("/{id}")
    public Result<CheckoutRecord> getById(@PathVariable Long id) {
        return checkoutRecordService.getById(id);
    }
}
