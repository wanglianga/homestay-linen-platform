package com.homestay.linen.service;

import com.homestay.linen.common.Result;
import com.homestay.linen.entity.LinenType;
import com.homestay.linen.repository.LinenTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LinenTypeService {

    private final LinenTypeRepository linenTypeRepository;

    public Result<LinenType> create(LinenType linenType) {
        try {
            LinenType saved = linenTypeRepository.save(linenType);
            return Result.success("创建布草类型成功", saved);
        } catch (Exception e) {
            return Result.error("创建布草类型失败: " + e.getMessage());
        }
    }

    public Result<LinenType> update(Long id, LinenType linenType) {
        try {
            if (!linenTypeRepository.existsById(id)) {
                return Result.error("布草类型不存在");
            }
            linenType.setId(id);
            LinenType saved = linenTypeRepository.save(linenType);
            return Result.success("更新布草类型成功", saved);
        } catch (Exception e) {
            return Result.error("更新布草类型失败: " + e.getMessage());
        }
    }

    public Result<Void> delete(Long id) {
        try {
            if (!linenTypeRepository.existsById(id)) {
                return Result.error("布草类型不存在");
            }
            linenTypeRepository.deleteById(id);
            return Result.<Void>success();
        } catch (Exception e) {
            return Result.error("删除布草类型失败: " + e.getMessage());
        }
    }

    public Result<LinenType> getById(Long id) {
        try {
            LinenType linenType = linenTypeRepository.findById(id).orElse(null);
            if (linenType == null) {
                return Result.error("布草类型不存在");
            }
            return Result.success(linenType);
        } catch (Exception e) {
            return Result.error("查询布草类型失败: " + e.getMessage());
        }
    }

    public Result<List<LinenType>> list() {
        try {
            List<LinenType> linenTypes = linenTypeRepository.findAll();
            return Result.success(linenTypes);
        } catch (Exception e) {
            return Result.error("查询布草类型列表失败: " + e.getMessage());
        }
    }
}
