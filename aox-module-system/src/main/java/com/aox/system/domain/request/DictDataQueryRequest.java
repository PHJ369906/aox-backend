package com.aox.system.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 字典数据查询请求
 *
 * @author Aox Team
 */
@Data
@Schema(description = "字典数据查询请求")
public class DictDataQueryRequest {

    @Schema(description = "当前页", example = "1")
    private Integer current = 1;

    @Schema(description = "每页数量", example = "20")
    private Integer size = 20;

    @Schema(description = "字典类型")
    private String dictType;

    @Schema(description = "字典标签")
    private String dictLabel;
}
