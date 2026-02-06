package com.aox.system.controller;

import com.aox.common.core.domain.R;
import com.aox.common.log.annotation.Log;
import com.aox.system.domain.SysDictData;
import com.aox.system.domain.request.DictDataQueryRequest;
import com.aox.system.service.DictDataService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典数据管理控制器
 *
 * @author Aox Team
 */
@RestController
@RequestMapping("/api/v1/system/dict/data")
@RequiredArgsConstructor
@Tag(name = "字典数据", description = "字典数据管理")
public class DictDataController {

    private final DictDataService dictDataService;

    /**
     * 分页查询字典数据
     */
    @GetMapping
    @Operation(summary = "分页查询字典数据")
    public R<IPage<SysDictData>> getDictDataList(@Valid DictDataQueryRequest request) {
        IPage<SysDictData> page = dictDataService.getDictDataList(request);
        return R.ok(page);
    }

    /**
     * 根据字典类型查询字典数据（用于下拉框等）
     */
    @GetMapping("/type/{dictType}")
    @Operation(summary = "根据字典类型查询字典数据")
    public R<List<SysDictData>> getDictDataByType(@PathVariable String dictType) {
        List<SysDictData> list = dictDataService.getDictDataByType(dictType);
        return R.ok(list);
    }

    /**
     * 获取字典数据详情
     */
    @GetMapping("/{dictCode}")
    @Operation(summary = "获取字典数据详情")
    public R<SysDictData> getDictDataDetail(@PathVariable Long dictCode) {
        SysDictData dictData = dictDataService.getDictDataById(dictCode);
        return R.ok(dictData);
    }

    /**
     * 创建字典数据
     */
    @PostMapping
    @Operation(summary = "创建字典数据")
    @Log(module = "字典管理", operation = "创建字典数据")
    public R<Void> createDictData(@RequestBody SysDictData dictData) {
        dictDataService.createDictData(dictData);
        return R.ok();
    }

    /**
     * 更新字典数据
     */
    @PutMapping("/{dictCode}")
    @Operation(summary = "更新字典数据")
    @Log(module = "字典管理", operation = "更新字典数据")
    public R<Void> updateDictData(@PathVariable Long dictCode, @RequestBody SysDictData dictData) {
        dictData.setDictCode(dictCode);
        dictDataService.updateDictData(dictData);
        return R.ok();
    }

    /**
     * 删除字典数据
     */
    @DeleteMapping("/{dictCode}")
    @Operation(summary = "删除字典数据")
    @Log(module = "字典管理", operation = "删除字典数据")
    public R<Void> deleteDictData(@PathVariable Long dictCode) {
        dictDataService.deleteDictData(dictCode);
        return R.ok();
    }

    /**
     * 批量删除字典数据
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除字典数据")
    @Log(module = "字典管理", operation = "批量删除字典数据")
    public R<Void> batchDeleteDictData(@RequestBody List<Long> dictCodes) {
        dictDataService.batchDeleteDictData(dictCodes);
        return R.ok();
    }
}
