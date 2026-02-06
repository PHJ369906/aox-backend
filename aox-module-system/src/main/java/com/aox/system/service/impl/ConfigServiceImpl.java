package com.aox.system.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.aox.common.security.context.SecurityContextHolder;
import com.aox.system.domain.SysConfig;
import com.aox.system.mapper.ConfigMapper;
import com.aox.system.service.ConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 系统配置服务实现
 *
 * @author Aox Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {

    private final ConfigMapper configMapper;

    @Override
    public String getConfigValue(String configKey) {
        return configMapper.getConfigValue(configKey);
    }

    @Override
    public String getConfigValueWithTenant(String configKey) {
        Long tenantId = SecurityContextHolder.getTenantId();
        if (tenantId == null || tenantId == 0) {
            return getConfigValue(configKey);
        }

        // 优先获取租户配置，如果不存在则获取全局配置
        String value = configMapper.getConfigValueByTenant(configKey, tenantId);
        if (value == null) {
            value = getConfigValue(configKey);
        }
        return value;
    }

    @Override
    public JSONObject getConfigJson(String configKey) {
        String configValue = getConfigValue(configKey);
        if (configValue == null) {
            return new JSONObject();
        }

        try {
            return JSONUtil.parseObj(configValue);
        } catch (Exception e) {
            log.error("解析配置 JSON 失败: {}", configKey, e);
            return new JSONObject();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateConfigValue(String configKey, String configValue) {
        int rows = configMapper.updateConfigValue(configKey, configValue);
        if (rows == 0) {
            log.warn("配置更新失败，配置键不存在: {}", configKey);
            throw new IllegalArgumentException("配置键不存在: " + configKey);
        }
        log.info("配置更新成功: {} = {}", configKey, configValue);
    }

    @Override
    public List<SysConfig> getConfigByGroup(String configGroup) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getConfigGroup, configGroup)
                .eq(SysConfig::getDeleted, 0)
                .orderByAsc(SysConfig::getConfigKey);
        return configMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(SysConfig config) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getConfigKey, config.getConfigKey())
                .eq(SysConfig::getDeleted, 0);

        SysConfig existConfig = configMapper.selectOne(wrapper);
        if (existConfig != null) {
            config.setConfigId(existConfig.getConfigId());
            configMapper.updateById(config);
            log.info("配置更新: {}", config.getConfigKey());
        } else {
            configMapper.insert(config);
            log.info("配置新增: {}", config.getConfigKey());
        }
    }
}
