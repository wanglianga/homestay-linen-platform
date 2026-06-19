package com.homestay.linen.controller;

import com.homestay.linen.common.Result;
import com.homestay.linen.entity.LinenType;
import com.homestay.linen.service.LinenTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/linenType")
@RequiredArgsConstructor
public class LinenTypeController {

    private final LinenTypeService linenTypeService;

    @PostMapping
    public Result<LinenType> create(@RequestBody LinenType linenType) {
        return linenTypeService.create(linenType);
    }

    @PutMapping("/{id}")
    public Result<LinenType> update(@PathVariable Long id, @RequestBody LinenType linenType) {
        return linenTypeService.update(id, linenType);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        return linenTypeService.delete(id);
    }

    @GetMapping("/{id}")
    public Result<LinenType> getById(@PathVariable Long id) {
        return linenTypeService.getById(id);
    }

    @GetMapping("/list")
    public Result<List<LinenType>> list() {
        return linenTypeService.list();
    }
}
