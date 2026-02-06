package com.aox.system.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 系统配置查询请求
 *
 * @author Aox Team
 */
@Data
@Schema(description = "系统配置查询请求")
public class ConfigQueryRequest {

    @Schema(description = "当前页", example = "1")
    private Integer current = 1;

    @Schema(description = "每页数量", example = "20")
    private Integer size = 20;

    @Schema(description = "配置键名")
    private String configKey;

    @Schema(description = "配置名称")
    private String configName;

    @Schema(description = "配置分组")
    private String configGroup;
}
