package com.aox.system.service;

import com.aox.system.domain.SysFile;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 文件管理服务接口
 *
 * @author Aox Team
 */
public interface FileService {

    /**
     * 文件上传
     * 支持MD5去重（秒传）
     */
    SysFile uploadFile(MultipartFile file) throws IOException;

    /**
     * 批量上传文件
     */
    List<SysFile> batchUploadFiles(List<MultipartFile> files) throws IOException;

    /**
     * 分页查询文件列表
     */
    IPage<SysFile> getFileList(Integer current, Integer size, String fileName,
                                String fileType, Long uploadUserId);

    /**
     * 获取文件详情
     */
    SysFile getFileById(Long fileId);

    /**
     * 根据MD5获取文件（秒传检查）
     */
    SysFile getFileByMd5(String md5);

    /**
     * 删除文件
     */
    void deleteFile(Long fileId);

    /**
     * 批量删除文件
     */
    void batchDeleteFiles(List<Long> fileIds);

    /**
     * 获取文件统计信息
     */
    Map<String, Object> getFileStatistics();
}
