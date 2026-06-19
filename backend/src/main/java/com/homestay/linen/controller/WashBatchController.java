package com.homestay.linen.controller;

import com.homestay.linen.common.Result;
import com.homestay.linen.entity.WashBatch;
import com.homestay.linen.service.WashBatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/washBatch")
@RequiredArgsConstructor
public class WashBatchController {

    private final WashBatchService washBatchService;

    @PostMapping
    public Result<WashBatch> create(@RequestBody WashBatch washBatch) {
        return washBatchService.create(washBatch);
    }

    @PostMapping("/{id}/addBags")
    public Result<WashBatch> addBags(@PathVariable Long id, @RequestBody List<Long> bagIds) {
        return washBatchService.addBags(id, bagIds);
    }

    @PutMapping("/{id}/start")
    public Result<WashBatch> startWash(@PathVariable Long id) {
        return washBatchService.startWash(id);
    }

    @PutMapping("/{id}/finish")
    public Result<WashBatch> finishWash(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        Integer returnQuantity = Integer.valueOf(params.get("returnQuantity").toString());
        Integer lossQuantity = params.get("lossQuantity") != null ? Integer.valueOf(params.get("lossQuantity").toString()) : 0;
        String remark = params.get("remark") != null ? params.get("remark").toString() : "";
        return washBatchService.finishWash(id, returnQuantity, lossQuantity, remark);
    }

    @GetMapping("/list")
    public Result<List<WashBatch>> list() {
        return washBatchService.list();
    }

    @GetMapping("/batchNo/{batchNo}")
    public Result<WashBatch> getByBatchNo(@PathVariable String batchNo) {
        return washBatchService.getByBatchNo(batchNo);
    }

    @GetMapping("/listByStatus")
    public Result<List<WashBatch>> listByStatus(@RequestParam String status) {
        return washBatchService.listByStatus(status);
    }

    @GetMapping("/{id}")
    public Result<WashBatch> getById(@PathVariable Long id) {
        return washBatchService.getById(id);
    }
}
