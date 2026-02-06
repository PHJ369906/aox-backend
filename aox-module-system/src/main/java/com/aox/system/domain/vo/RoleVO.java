package com.aox.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色VO
 *
 * @author Aox Team
 */
@Data
public class RoleVO {

    private Long roleId;
    private String roleCode;
    private String roleName;
    private Integer roleSort;
    private Integer dataScope;
    private Integer status;
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
