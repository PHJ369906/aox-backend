package com.aox.system.mapper;

import com.aox.system.domain.SysConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 系统配置 Mapper
 *
 * @author Aox Team
 */
@Mapper
public interface ConfigMapper extends BaseMapper<SysConfig> {

    /**
     * 根据配置键获取配置值
     *
     * @param configKey 配置键
     * @return 配置值
     */
    String getConfigValue(@Param("configKey") String configKey);

    /**
     * 根据配置键和租户ID获取配置值
     *
     * @param configKey 配置键
     * @param tenantId  租户ID
     * @return 配置值
     */
    String getConfigValueByTenant(@Param("configKey") String configKey, @Param("tenantId") Long tenantId);

    /**
     * 更新配置值
     *
     * @param configKey   配置键
     * @param configValue 配置值
     * @return 影响行数
     */
    int updateConfigValue(@Param("configKey") String configKey, @Param("configValue") String configValue);
}
