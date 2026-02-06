package com.aox.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件信息实体
 *
 * @author Aox Team
 */
@Data
@TableName("sys_file")
public class SysFile {

    /**
     * 文件ID
     */
    @TableId(type = IdType.AUTO)
    private Long fileId;

    /**
     * 原始文件名
     */
    private String fileName;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 存储路径
     */
    private String filePath;

    /**
     * 访问URL
     */
    private String fileUrl;

    /**
     * 文件大小(byte)
     */
    private Long fileSize;

    /**
     * 文件MD5
     */
    private String md5;

    /**
     * 存储类型
     */
    private String storageType;

    /**
     * 桶名称
     */
    private String bucketName;

    /**
     * 上传用户ID
     */
    private Long uploadUserId;

    /**
     * 上传用户名
     */
    private String uploadUserName;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 删除标志(0=正常,1=删除)
     */
    @TableLogic
    private Integer deleted;
}
