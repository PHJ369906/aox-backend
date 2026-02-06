package com.aox.system.controller;

import com.aox.common.core.domain.R;
import com.aox.common.exception.BusinessException;
import com.aox.common.log.annotation.Log;
import com.aox.system.domain.SysConfig;
import com.aox.system.domain.request.ConfigQueryRequest;
import com.aox.system.mapper.ConfigMapper;
import com.aox.system.service.ConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统配置管理控制器
 *
 * @author Aox Team
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/system/config")
@RequiredArgsConstructor
@Tag(name = "系统配置", description = "系统参数配置管理")
public class ConfigController {

    private final ConfigService configService;
    private final ConfigMapper configMapper;

    /**
     * 分页查询配置列表
     */
    @GetMapping
    @Operation(summary = "分页查询配置列表")
    public R<IPage<SysConfig>> getConfigList(@Valid ConfigQueryRequest request) {
        Page<SysConfig> page = new Page<>(request.getCurrent(), request.getSize());
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();

        wrapper.like(StringUtils.hasText(request.getConfigKey()), SysConfig::getConfigKey, request.getConfigKey())
                .eq(StringUtils.hasText(request.getConfigGroup()), SysConfig::getConfigGroup, request.getConfigGroup())
                .eq(SysConfig::getDeleted, 0)
                .orderByAsc(SysConfig::getConfigGroup)
                .orderByAsc(SysConfig::getConfigKey);

        IPage<SysConfig> result = configMapper.selectPage(page, wrapper);
        return R.ok(result);
    }

    /**
     * 根据配置键获取配置值
     */
    @GetMapping("/key/{configKey}")
    @Operation(summary = "根据配置键获取配置值")
    public R<String> getConfigByKey(@PathVariable("configKey") String configKey) {
        String value = configService.getConfigValue(configKey);
        return R.ok(value);
    }

    /**
     * 获取配置详情
     */
    @GetMapping("/{configId}")
    @Operation(summary = "获取配置详情")
    public R<SysConfig> getConfigDetail(@PathVariable("configId") Long configId) {
        SysConfig config = configMapper.selectById(configId);
        return R.ok(config);
    }

    /**
     * 根据分组获取配置列表
     */
    @GetMapping("/group/{configGroup}")
    @Operation(summary = "根据分组获取配置列表")
    public R<List<SysConfig>> getConfigByGroup(@PathVariable("configGroup") String configGroup) {
        List<SysConfig> list = configService.getConfigByGroup(configGroup);
        return R.ok(list);
    }

    /**
     * 获取所有配置分组
     */
    @GetMapping("/groups")
    @Operation(summary = "获取所有配置分组")
    public R<List<String>> getConfigGroups() {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(SysConfig::getConfigGroup)
                .eq(SysConfig::getDeleted, 0)
                .groupBy(SysConfig::getConfigGroup);

        List<SysConfig> configs = configMapper.selectList(wrapper);
        List<String> groups = configs.stream()
                .map(SysConfig::getConfigGroup)
                .distinct()
                .collect(Collectors.toList());
        return R.ok(groups);
    }

    /**
     * 创建配置
     */
    @PostMapping
    @Operation(summary = "创建配置")
    @Log(module = "系统配置", operation = "创建配置")
    public R<Void> createConfig(@RequestBody SysConfig config) {
        // 检查配置键是否已存在
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getConfigKey, config.getConfigKey())
                .eq(SysConfig::getDeleted, 0);
        SysConfig existConfig = configMapper.selectOne(wrapper);

        if (existConfig != null) {
            return R.fail("配置键已存在: " + config.getConfigKey());
        }

        configMapper.insert(config);
        return R.ok();
    }

    /**
     * 更新配置
     */
    @PutMapping("/{configId}")
    @Operation(summary = "更新配置")
    @Log(module = "系统配置", operation = "更新配置")
    public R<Void> updateConfig(@PathVariable Long configId, @RequestBody SysConfig config) {
        config.setConfigId(configId);
        configMapper.updateById(config);
        return R.ok();
    }

    /**
     * 批量更新配置值
     */
    @PutMapping("/batch")
    @Operation(summary = "批量更新配置值")
    @Log(module = "系统配置", operation = "批量更新配置")
    public R<Void> batchUpdateConfig(@RequestBody Map<String, String> configMap) {
        configMap.forEach((key, value) -> {
            try {
                configService.updateConfigValue(key, value);
            } catch (Exception e) {
                throw new BusinessException("更新配置失败: " + key);
            }
        });
        return R.ok();
    }

    /**
     * 删除配置
     */
    @DeleteMapping("/{configId}")
    @Operation(summary = "删除配置")
    @Log(module = "系统配置", operation = "删除配置")
    public R<Void> deleteConfig(@PathVariable Long configId) {
        SysConfig config = new SysConfig();
        config.setConfigId(configId);
        config.setDeleted(1);
        configMapper.updateById(config);
        return R.ok();
    }

    /**
     * 批量删除配置
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除配置")
    @Log(module = "系统配置", operation = "批量删除配置")
    public R<Void> batchDeleteConfigs(@RequestBody List<Long> configIds) {
        configIds.forEach(configId -> {
            SysConfig config = new SysConfig();
            config.setConfigId(configId);
            config.setDeleted(1);
            configMapper.updateById(config);
        });
        return R.ok();
    }

    /**
     * 刷新配置缓存
     */
    @PostMapping("/refresh")
    @Operation(summary = "刷新配置缓存")
    @Log(module = "系统配置", operation = "刷新配置缓存")
    public R<Void> refreshCache() {
        // 注意：如果使用了Redis缓存配置，需要实现缓存刷新逻辑
        // 示例: redisTemplate.delete("config:*");
        log.warn("【开发模式】配置缓存刷新功能未实现");
        log.info("配置缓存刷新请求");
        return R.ok();
    }
}
