package com.homestay.linen.controller;

import com.homestay.linen.common.Result;
import com.homestay.linen.entity.LinenStock;
import com.homestay.linen.service.LinenStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/linenStock")
@RequiredArgsConstructor
public class LinenStockController {

    private final LinenStockService linenStockService;

    @GetMapping("/list")
    public Result<List<LinenStock>> list() {
        return linenStockService.list();
    }

    @PutMapping("/updateSafetyStock")
    public Result<LinenStock> updateSafetyStock(@RequestBody Map<String, Object> params) {
        Long linenTypeId = Long.valueOf(params.get("linenTypeId").toString());
        Integer safetyStock = Integer.valueOf(params.get("safetyStock").toString());
        return linenStockService.updateSafetyStock(linenTypeId, safetyStock);
    }

    @PostMapping("/restock")
    public Result<LinenStock> restock(@RequestBody Map<String, Object> params) {
        Long linenTypeId = Long.valueOf(params.get("linenTypeId").toString());
        Integer quantity = Integer.valueOf(params.get("quantity").toString());
        return linenStockService.restock(linenTypeId, quantity);
    }

    @PostMapping("/deductInUse")
    public Result<LinenStock> deductInUse(@RequestBody Map<String, Object> params) {
        Long linenTypeId = Long.valueOf(params.get("linenTypeId").toString());
        Integer quantity = Integer.valueOf(params.get("quantity").toString());
        return linenStockService.deductInUse(linenTypeId, quantity);
    }

    @GetMapping("/linenType/{linenTypeId}")
    public Result<LinenStock> getByLinenTypeId(@PathVariable Long linenTypeId) {
        return linenStockService.getByLinenTypeId(linenTypeId);
    }
}
