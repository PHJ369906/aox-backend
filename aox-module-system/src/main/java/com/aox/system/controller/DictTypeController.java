package com.aox.system.controller;

import com.aox.common.core.domain.R;
import com.aox.common.log.annotation.Log;
import com.aox.system.domain.SysDictType;
import com.aox.system.domain.request.DictTypeQueryRequest;
import com.aox.system.service.DictTypeService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典类型管理控制器
 *
 * @author Aox Team
 */
@RestController
@RequestMapping("/api/v1/system/dict/type")
@RequiredArgsConstructor
@Tag(name = "字典类型", description = "字典类型管理")
public class DictTypeController {

    private final DictTypeService dictTypeService;

    /**
     * 分页查询字典类型
     */
    @GetMapping
    @Operation(summary = "分页查询字典类型")
    public R<IPage<SysDictType>> getDictTypeList(@Valid DictTypeQueryRequest request) {
        IPage<SysDictType> page = dictTypeService.getDictTypeList(request);
        return R.ok(page);
    }

    /**
     * 获取所有字典类型
     */
    @GetMapping("/all")
    @Operation(summary = "获取所有字典类型")
    public R<List<SysDictType>> getAllDictTypes() {
        List<SysDictType> list = dictTypeService.getAllDictTypes();
        return R.ok(list);
    }

    /**
     * 获取字典类型详情
     */
    @GetMapping("/{dictId}")
    @Operation(summary = "获取字典类型详情")
    public R<SysDictType> getDictTypeDetail(@PathVariable Long dictId) {
        SysDictType dictType = dictTypeService.getDictTypeById(dictId);
        return R.ok(dictType);
    }

    /**
     * 创建字典类型
     */
    @PostMapping
    @Operation(summary = "创建字典类型")
    @Log(module = "字典管理", operation = "创建字典类型")
    public R<Void> createDictType(@RequestBody SysDictType dictType) {
        dictTypeService.createDictType(dictType);
        return R.ok();
    }

    /**
     * 更新字典类型
     */
    @PutMapping("/{dictId}")
    @Operation(summary = "更新字典类型")
    @Log(module = "字典管理", operation = "更新字典类型")
    public R<Void> updateDictType(@PathVariable Long dictId, @RequestBody SysDictType dictType) {
        dictType.setDictId(dictId);
        dictTypeService.updateDictType(dictType);
        return R.ok();
    }

    /**
     * 删除字典类型
     */
    @DeleteMapping("/{dictId}")
    @Operation(summary = "删除字典类型")
    @Log(module = "字典管理", operation = "删除字典类型")
    public R<Void> deleteDictType(@PathVariable Long dictId) {
        dictTypeService.deleteDictType(dictId);
        return R.ok();
    }

    /**
     * 批量删除字典类型
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除字典类型")
    @Log(module = "字典管理", operation = "批量删除字典类型")
    public R<Void> batchDeleteDictTypes(@RequestBody List<Long> dictIds) {
        dictTypeService.batchDeleteDictTypes(dictIds);
        return R.ok();
    }
}
