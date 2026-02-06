package com.aox.system.service;

import cn.hutool.json.JSONObject;
import com.aox.system.domain.SysConfig;

import java.util.List;

/**
 * 系统配置服务接口
 *
 * @author Aox Team
 */
public interface ConfigService {

    /**
     * 根据配置键获取配置值
     */
    String getConfigValue(String configKey);

    /**
     * 根据配置键获取配置值（支持租户隔离）
     */
    String getConfigValueWithTenant(String configKey);

    /**
     * 根据配置键获取 JSON 配置对象
     */
    JSONObject getConfigJson(String configKey);

    /**
     * 更新配置值
     */
    void updateConfigValue(String configKey, String configValue);

    /**
     * 根据分组获取配置列表
     */
    List<SysConfig> getConfigByGroup(String configGroup);

    /**
     * 保存或更新配置
     */
    void saveOrUpdate(SysConfig config);
}
