package com.homestay.linen.controller;

import com.homestay.linen.common.Result;
import com.homestay.linen.entity.LinenBag;
import com.homestay.linen.service.LinenBagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/linenBag")
@RequiredArgsConstructor
public class LinenBagController {

    private final LinenBagService linenBagService;

    @PostMapping
    public Result<LinenBag> create(@RequestBody LinenBag linenBag) {
        return linenBagService.create(linenBag);
    }

    @GetMapping("/list")
    public Result<List<LinenBag>> list() {
        return linenBagService.list();
    }

    @GetMapping("/bagCode/{bagCode}")
    public Result<LinenBag> getByBagCode(@PathVariable String bagCode) {
        return linenBagService.getByBagCode(bagCode);
    }

    @PutMapping("/{id}/status")
    public Result<LinenBag> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return linenBagService.updateStatus(id, status);
    }

    @PutMapping("/{id}/pickup")
    public Result<LinenBag> pickupByDriver(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        Long driverId = Long.valueOf(params.get("driverId").toString());
        Integer pickupWeight = Integer.valueOf(params.get("pickupWeight").toString());
        return linenBagService.pickupByDriver(id, driverId, pickupWeight);
    }

    @PutMapping("/{id}/factoryCheck")
    public Result<LinenBag> factoryCheck(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        Integer factoryQuantity = Integer.valueOf(params.get("factoryQuantity").toString());
        String factoryRemark = params.get("factoryRemark") != null ? params.get("factoryRemark").toString() : "";
        return linenBagService.factoryCheck(id, factoryQuantity, factoryRemark);
    }

    @PutMapping("/{id}/returnFromFactory")
    public Result<LinenBag> returnFromFactory(@PathVariable Long id, @RequestParam Integer returnQuantity) {
        return linenBagService.returnFromFactory(id, returnQuantity);
    }

    @PutMapping("/{id}/warehouseCheck")
    public Result<LinenBag> warehouseCheck(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        Long warehouseId = Long.valueOf(params.get("warehouseId").toString());
        Integer warehouseQuantity = Integer.valueOf(params.get("warehouseQuantity").toString());
        String warehouseRemark = params.get("warehouseRemark") != null ? params.get("warehouseRemark").toString() : "";
        return linenBagService.warehouseCheck(id, warehouseId, warehouseQuantity, warehouseRemark);
    }

    @GetMapping("/listByStatus")
    public Result<List<LinenBag>> listByStatus(@RequestParam String status) {
        return linenBagService.listByStatus(status);
    }

    @GetMapping("/{id}")
    public Result<LinenBag> getById(@PathVariable Long id) {
        return linenBagService.getById(id);
    }

    @GetMapping("/checkout/{checkoutId}")
    public Result<List<LinenBag>> getByCheckoutId(@PathVariable Long checkoutId) {
        return linenBagService.getByCheckoutId(checkoutId);
    }
}
