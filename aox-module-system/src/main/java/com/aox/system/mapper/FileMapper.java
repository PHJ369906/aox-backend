package com.aox.system.mapper;

import com.aox.system.domain.SysFile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 文件信息 Mapper
 *
 * @author Aox Team
 */
@Mapper
public interface FileMapper extends BaseMapper<SysFile> {

    /**
     * 根据MD5查询文件
     *
     * @param md5 文件MD5
     * @return 文件信息
     */
    SysFile selectByMd5(@Param("md5") String md5);

    /**
     * 统计用户上传文件总大小
     *
     * @param userId 用户ID
     * @return 总大小（字节）
     */
    Long sumFileSizeByUserId(@Param("userId") Long userId);

    /**
     * 统计租户文件总大小
     *
     * @param tenantId 租户ID
     * @return 总大小（字节）
     */
    Long sumFileSizeByTenantId(@Param("tenantId") Long tenantId);
}
