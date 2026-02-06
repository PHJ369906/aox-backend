package com.aox.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 云存储配置实体
 *
 * @author Aox Team
 */
@Data
@TableName("sys_oss_config")
public class SysOssConfig {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long tenantId;

    private String storageType;

    private Integer isCurrent;

    private String endpoint;

    private String accessKey;

    private String secretKey;

    private String accessKeyId;

    private String accessKeySecret;

    private String region;

    private String bucketName;

    private String domain;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer deleted;
}
