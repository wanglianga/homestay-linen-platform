package com.homestay.linen.controller;

import com.homestay.linen.common.Result;
import com.homestay.linen.entity.DeliveryReceipt;
import com.homestay.linen.service.DeliveryReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/deliveryReceipt")
@RequiredArgsConstructor
public class DeliveryReceiptController {

    private final DeliveryReceiptService deliveryReceiptService;

    @PostMapping
    public Result<DeliveryReceipt> create(@RequestBody DeliveryReceipt deliveryReceipt) {
        return deliveryReceiptService.create(deliveryReceipt);
    }

    @PutMapping("/{id}/deliver")
    public Result<DeliveryReceipt> deliver(@PathVariable Long id) {
        return deliveryReceiptService.deliver(id);
    }

    @PutMapping("/{id}/receive")
    public Result<DeliveryReceipt> receive(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        Integer receivedQuantity = Integer.valueOf(params.get("receivedQuantity").toString());
        String remark = params.get("remark") != null ? params.get("remark").toString() : "";
        return deliveryReceiptService.receive(id, receivedQuantity, remark);
    }

    @GetMapping("/list")
    public Result<List<DeliveryReceipt>> list() {
        return deliveryReceiptService.list();
    }

    @GetMapping("/{id}")
    public Result<DeliveryReceipt> getById(@PathVariable Long id) {
        return deliveryReceiptService.getById(id);
    }

    @GetMapping("/listByStatus")
    public Result<List<DeliveryReceipt>> listByStatus(@RequestParam String status) {
        return deliveryReceiptService.listByStatus(status);
    }

    @GetMapping("/receiptNo/{receiptNo}")
    public Result<DeliveryReceipt> getByReceiptNo(@PathVariable String receiptNo) {
        return deliveryReceiptService.getByReceiptNo(receiptNo);
    }
}
