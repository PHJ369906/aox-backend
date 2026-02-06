package com.aox.system.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.aox.common.core.service.FileStorageService;
import com.aox.common.exception.BusinessException;
import com.aox.common.security.context.SecurityContextHolder;
import com.aox.system.domain.SysFile;
import com.aox.system.mapper.FileMapper;
import com.aox.system.service.FileService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件管理服务实现
 *
 * @author Aox Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileMapper fileMapper;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysFile uploadFile(MultipartFile file) throws IOException {
        // 计算文件MD5
        String md5 = DigestUtil.md5Hex(file.getInputStream());

        // 检查文件是否已存在（秒传）
        SysFile existFile = fileMapper.selectByMd5(md5);
        if (existFile != null) {
            log.info("文件已存在，秒传: {}", existFile.getFileName());
            return existFile;
        }

        // 上传文件到OSS
        String fileUrl = fileStorageService.upload(file);
        String storageType = fileStorageService.getCurrentStorageType();

        // 保存文件信息到数据库
        SysFile sysFile = new SysFile();
        sysFile.setFileName(file.getOriginalFilename());
        sysFile.setFileType(file.getContentType());
        sysFile.setFileSize(file.getSize());
        sysFile.setFileUrl(fileUrl);
        sysFile.setFilePath(extractFilePath(fileUrl));
        sysFile.setMd5(md5);
        sysFile.setStorageType(storageType);
        sysFile.setUploadUserId(SecurityContextHolder.getUserId());
        sysFile.setUploadUserName(SecurityContextHolder.getUsername());
        sysFile.setTenantId(SecurityContextHolder.getTenantId());

        fileMapper.insert(sysFile);

        log.info("文件上传成功: {} -> {}", sysFile.getFileName(), fileUrl);
        return sysFile;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<SysFile> batchUploadFiles(List<MultipartFile> files) throws IOException {
        return files.stream()
                .map(file -> {
                    try {
                        return uploadFile(file);
                    } catch (IOException e) {
                        log.error("文件上传失败: {}", file.getOriginalFilename(), e);
                        throw new BusinessException("文件上传失败: " + file.getOriginalFilename());
                    }
                })
                .toList();
    }

    @Override
    public IPage<SysFile> getFileList(Integer current, Integer size, String fileName,
                                      String fileType, Long uploadUserId) {
        Page<SysFile> page = new Page<>(current, size);
        LambdaQueryWrapper<SysFile> wrapper = new LambdaQueryWrapper<>();

        if (fileName != null && !fileName.isEmpty()) {
            wrapper.like(SysFile::getFileName, fileName);
        }
        if (fileType != null && !fileType.isEmpty()) {
            wrapper.eq(SysFile::getFileType, fileType);
        }
        if (uploadUserId != null) {
            wrapper.eq(SysFile::getUploadUserId, uploadUserId);
        }

        wrapper.eq(SysFile::getDeleted, 0)
                .orderByDesc(SysFile::getCreateTime);

        return fileMapper.selectPage(page, wrapper);
    }

    @Override
    public SysFile getFileById(Long fileId) {
        return fileMapper.selectById(fileId);
    }

    @Override
    public SysFile getFileByMd5(String md5) {
        return fileMapper.selectByMd5(md5);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFile(Long fileId) {
        SysFile file = fileMapper.selectById(fileId);
        if (file == null) {
            throw new BusinessException("文件不存在");
        }

        // 删除OSS文件
        try {
            fileStorageService.delete(file.getFilePath());
        } catch (Exception e) {
            log.warn("删除OSS文件失败: {}", file.getFilePath(), e);
        }

        // 软删除数据库记录
        SysFile updateFile = new SysFile();
        updateFile.setFileId(fileId);
        updateFile.setDeleted(1);
        fileMapper.updateById(updateFile);

        log.info("删除文件成功: {}", file.getFileName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteFiles(List<Long> fileIds) {
        fileIds.forEach(this::deleteFile);
    }

    @Override
    public Map<String, Object> getFileStatistics() {
        Long userId = SecurityContextHolder.getUserId();
        Long tenantId = SecurityContextHolder.getTenantId();

        Map<String, Object> statistics = new HashMap<>();

        // 用户上传文件总数
        LambdaQueryWrapper<SysFile> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(SysFile::getUploadUserId, userId)
                .eq(SysFile::getDeleted, 0);
        Long userFileCount = fileMapper.selectCount(userWrapper);
        statistics.put("userFileCount", userFileCount);

        // 用户上传文件总大小
        Long userTotalSize = fileMapper.sumFileSizeByUserId(userId);
        statistics.put("userTotalSize", userTotalSize);
        statistics.put("userTotalSizeMB", userTotalSize / 1024.0 / 1024.0);

        // 租户文件总数
        if (tenantId != null) {
            LambdaQueryWrapper<SysFile> tenantWrapper = new LambdaQueryWrapper<>();
            tenantWrapper.eq(SysFile::getTenantId, tenantId)
                    .eq(SysFile::getDeleted, 0);
            Long tenantFileCount = fileMapper.selectCount(tenantWrapper);
            statistics.put("tenantFileCount", tenantFileCount);

            // 租户文件总大小
            Long tenantTotalSize = fileMapper.sumFileSizeByTenantId(tenantId);
            statistics.put("tenantTotalSize", tenantTotalSize);
            statistics.put("tenantTotalSizeMB", tenantTotalSize / 1024.0 / 1024.0);
        }

        return statistics;
    }

    /**
     * 从URL中提取文件路径
     */
    private String extractFilePath(String fileUrl) {
        // 简单提取，实际可根据OSS类型优化
        int lastSlash = fileUrl.lastIndexOf('/');
        if (lastSlash > 0) {
            return fileUrl.substring(lastSlash + 1);
        }
        return fileUrl;
    }

    /**
     * 格式化文件大小
     */
    public static String formatFileSize(Long size) {
        if (size == null || size == 0) {
            return "0 B";
        }

        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return String.format("%.2f %s", size / Math.pow(1024, digitGroups), units[digitGroups]);
    }
}
