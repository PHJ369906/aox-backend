package com.aox.system.controller;

import com.aox.common.core.domain.R;
import com.aox.common.log.annotation.Log;
import com.aox.system.domain.SysFile;
import com.aox.system.service.FileService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 文件管理控制器
 *
 * @author Aox Team
 */
@RestController
@RequestMapping("/api/v1/system/files")
@RequiredArgsConstructor
@Tag(name = "文件管理", description = "文件上传、下载、删除、查询")
public class FileController {

    private final FileService fileService;

    /**
     * 单文件上传
     */
    @PostMapping("/upload")
    @Operation(summary = "单文件上传")
    @Log(module = "文件管理", operation = "文件上传")
    public R<SysFile> upload(@RequestParam("file") MultipartFile file) {
        try {
            SysFile sysFile = fileService.uploadFile(file);
            return R.ok(sysFile);
        } catch (IOException e) {
            return R.fail("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 批量文件上传
     */
    @PostMapping("/upload/batch")
    @Operation(summary = "批量文件上传")
    @Log(module = "文件管理", operation = "批量文件上传")
    public R<List<SysFile>> batchUpload(@RequestParam("files") List<MultipartFile> files) {
        try {
            List<SysFile> sysFiles = fileService.batchUploadFiles(files);
            return R.ok(sysFiles);
        } catch (IOException e) {
            return R.fail("批量上传失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询文件列表
     */
    @GetMapping
    @Operation(summary = "分页查询文件列表")
    public R<IPage<SysFile>> getFileList(
            @RequestParam(value = "current", defaultValue = "1") Integer current,
            @RequestParam(value = "size", defaultValue = "20") Integer size,
            @RequestParam(value = "fileName", required = false) String fileName,
            @RequestParam(value = "fileType", required = false) String fileType,
            @RequestParam(value = "uploadUserId", required = false) Long uploadUserId) {

        IPage<SysFile> page = fileService.getFileList(current, size, fileName, fileType, uploadUserId);
        return R.ok(page);
    }

    /**
     * 获取文件详情
     */
    @GetMapping("/{fileId}")
    @Operation(summary = "获取文件详情")
    public R<SysFile> getFileDetail(@PathVariable Long fileId) {
        SysFile file = fileService.getFileById(fileId);
        return R.ok(file);
    }

    /**
     * 检查文件是否存在（秒传）
     */
    @GetMapping("/check-md5")
    @Operation(summary = "检查文件MD5（秒传）")
    public R<SysFile> checkFileMd5(@RequestParam String md5) {
        SysFile file = fileService.getFileByMd5(md5);
        return R.ok(file);
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/{fileId}")
    @Operation(summary = "删除文件")
    @Log(module = "文件管理", operation = "删除文件")
    public R<Void> deleteFile(@PathVariable Long fileId) {
        fileService.deleteFile(fileId);
        return R.ok();
    }

    /**
     * 批量删除文件
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除文件")
    @Log(module = "文件管理", operation = "批量删除文件")
    public R<Void> batchDeleteFiles(@RequestBody List<Long> fileIds) {
        fileService.batchDeleteFiles(fileIds);
        return R.ok();
    }

    /**
     * 获取文件统计信息
     */
    @GetMapping("/statistics")
    @Operation(summary = "获取文件统计信息")
    public R<Map<String, Object>> getFileStatistics() {
        Map<String, Object> statistics = fileService.getFileStatistics();
        return R.ok(statistics);
    }
}
