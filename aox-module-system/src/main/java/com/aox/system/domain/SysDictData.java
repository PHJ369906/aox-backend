package com.aox.system.domain;

import com.aox.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典数据实体
 *
 * @author Aox Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dict_data")
public class SysDictData extends BaseEntity {

    /**
     * 字典编码
     */
    @TableId(type = IdType.AUTO)
    private Long dictCode;

    /**
     * 字典类型
     */
    private String dictType;

    /**
     * 字典标签
     */
    private String dictLabel;

    /**
     * 字典键值
     */
    private String dictValue;

    /**
     * 排序
     */
    private Integer dictSort;

    /**
     * 样式类名
     */
    private String cssClass;

    /**
     * 列表样式
     */
    private String listClass;

    /**
     * 是否默认 0否 1是
     */
    private Integer isDefault;

    /**
     * 状态 0正常 1禁用
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
