package com.aox.system.domain.request;

import com.aox.common.core.domain.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色查询请求
 *
 * @author Aox Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RoleQueryRequest extends BasePageRequest {

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 状态 0正常 1禁用
     */
    private Integer status;
}
