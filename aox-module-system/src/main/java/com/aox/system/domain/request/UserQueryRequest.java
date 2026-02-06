package com.aox.system.domain.request;

import com.aox.common.core.domain.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户查询请求
 *
 * @author Aox Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserQueryRequest extends BasePageRequest {

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 关键字（用户名/昵称模糊匹配）
     */
    private String keyword;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 部门ID
     */
    private Long deptId;
}
