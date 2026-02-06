package com.aox.system.domain;

import com.aox.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统配置实体
 *
 * @author Aox Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_config")
public class SysConfig extends BaseEntity {

    /**
     * 配置ID
     */
    @TableId(type = IdType.AUTO)
    private Long configId;

    /**
     * 配置键
     */
    private String configKey;

    /**
     * 配置值
     */
    private String configValue;

    /**
     * 配置分组
     */
    private String configGroup;

    /**
     * 配置类型
     */
    private String configType;

    /**
     * 描述
     */
    private String description;

    /**
     * 状态 0正常 1禁用
     */
    private Integer status;

    /**
     * 租户ID
     */
    private Long tenantId;
}
