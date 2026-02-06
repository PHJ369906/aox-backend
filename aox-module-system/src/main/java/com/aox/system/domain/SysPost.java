package com.aox.system.domain;

import com.aox.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 岗位实体
 *
 * @author Aox Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_post")
public class SysPost extends BaseEntity {

    /**
     * 岗位ID
     */
    @TableId(type = IdType.AUTO)
    private Long postId;

    /**
     * 岗位编码
     */
    private String postCode;

    /**
     * 岗位名称
     */
    private String postName;

    /**
     * 排序
     */
    private Integer postSort;

    /**
     * 状态 0正常 1禁用
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 租户ID
     */
    private Long tenantId;
}
