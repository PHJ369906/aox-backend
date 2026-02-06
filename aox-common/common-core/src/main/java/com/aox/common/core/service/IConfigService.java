package com.aox.common.core.service;

import cn.hutool.json.JSONObject;

/**
 * 配置服务接口
 * 用于解耦模块间的配置依赖
 *
 * @author Aox Team
 */
public interface IConfigService {

    /**
     * 获取配置值（支持租户隔离）
     *
     * @param configKey 配置键
     * @return 配置值
     */
    String getConfigValueWithTenant(String configKey);

    /**
     * 更新配置值
     *
     * @param configKey   配置键
     * @param configValue 配置值
     */
    void updateConfigValue(String configKey, String configValue);

    /**
     * 获取 JSON 格式配置
     *
     * @param configKey 配置键
     * @return JSON 对象
     */
    JSONObject getConfigJson(String configKey);
}
