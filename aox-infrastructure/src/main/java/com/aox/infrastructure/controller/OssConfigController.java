package com.aox.infrastructure.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.aox.common.core.domain.R;
import com.aox.infrastructure.oss.config.OssProperties;
import com.aox.infrastructure.oss.service.OssConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 云存储配置控制器
 *
 * @author Aox Team
 */
@RestController
@RequestMapping("/api/v1/system/oss/config")
@RequiredArgsConstructor
@Tag(name = "云存储配置", description = "云存储类型与配置管理")
public class OssConfigController {

    private final OssConfigService ossConfigService;
    private final OssProperties ossProperties;

    @GetMapping("/current")
    @Operation(summary = "获取当前云存储类型")
    public R<Map<String, Object>> getCurrentStorageType() {
        String storageType = ossConfigService.getCurrentOssClient().getStorageType();
        Map<String, Object> data = new HashMap<>();
        data.put("storageType", storageType);
        return R.ok(data);
    }

    @GetMapping("/types")
    @Operation(summary = "获取支持的云存储类型")
    public R<List<Map<String, Object>>> getSupportedTypes() {
        return R.ok(buildSupportedTypes());
    }

    @GetMapping("/list")
    @Operation(summary = "获取云存储配置列表")
    public R<List<Map<String, Object>>> listConfigs() {
        List<Map<String, Object>> types = buildSupportedTypes();
        Map<String, com.aox.system.domain.SysOssConfig> configMap = new HashMap<>();
        for (com.aox.system.domain.SysOssConfig config : ossConfigService.listConfigs()) {
            if (config.getStorageType() != null) {
                configMap.put(config.getStorageType().toLowerCase(), config);
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> type : types) {
            String storageType = String.valueOf(type.get("value"));
            com.aox.system.domain.SysOssConfig config = configMap.get(storageType);
            Map<String, Object> defaults = getDefaultConfig(storageType);

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("storageType", storageType);
            row.put("label", type.get("label"));
            row.put("enabled", type.get("enabled"));
            row.put("description", type.get("description"));
            row.put("isCurrent", config != null ? config.getIsCurrent() : 0);
            row.put("status", config != null ? config.getStatus() : 0);
            row.put("endpoint", pickString(config != null ? config.getEndpoint() : null, defaults.get("endpoint")));
            row.put("region", pickString(config != null ? config.getRegion() : null, defaults.get("region")));
            row.put("bucketName", pickString(config != null ? config.getBucketName() : null, defaults.get("bucketName")));
            row.put("domain", pickString(config != null ? config.getDomain() : null, defaults.get("domain")));
            result.add(row);
        }

        return R.ok(result);
    }

    private List<Map<String, Object>> buildSupportedTypes() {
        List<Map<String, Object>> types = new ArrayList<>();
        types.add(type("minio", "MinIO", true, "已支持"));
        types.add(type("aliyun", "阿里云 OSS", true, "已支持"));
        types.add(type("tencent", "腾讯云 COS", false, "暂未实现"));
        types.add(type("qiniu", "七牛云 Kodo", false, "暂未实现"));
        return types;
    }

    @GetMapping("/{storageType}")
    @Operation(summary = "获取指定云存储配置")
    public R<Object> getStorageConfig(@PathVariable("storageType") String storageType) {
        JSONObject config = ossConfigService.getStorageConfig(storageType);
        if (config == null || config.isEmpty()) {
            return R.ok(getDefaultConfig(storageType));
        }
        return R.ok(config);
    }

    @PutMapping("/{storageType}")
    @Operation(summary = "更新指定云存储配置")
    public R<Void> updateStorageConfig(@PathVariable("storageType") String storageType,
                                       @RequestBody Map<String, Object> request) {
        String configJson = extractConfigJson(request);
        ossConfigService.updateStorageConfig(storageType, configJson);
        return R.ok();
    }

    @PostMapping("/switch")
    @Operation(summary = "切换云存储类型")
    public R<Void> switchStorageType(@RequestBody OssConfigSwitchRequest request) {
        ossConfigService.switchStorageType(request.getStorageType());
        return R.ok();
    }

    @PostMapping("/test")
    @Operation(summary = "测试云存储配置")
    public R<Map<String, Object>> testStorageConfig(@RequestBody Map<String, Object> request) {
        Map<String, Object> data = new HashMap<>();
        try {
            String configJson = extractConfigJson(request);
            if (!JSONUtil.isTypeJSON(configJson)) {
                throw new IllegalArgumentException("配置格式错误，必须为有效的 JSON");
            }
            data.put("success", true);
            data.put("message", "配置格式校验通过");
        } catch (Exception e) {
            data.put("success", false);
            data.put("message", e.getMessage());
        }
        return R.ok(data);
    }

    @DeleteMapping("/cache")
    @Operation(summary = "清除云存储配置缓存")
    public R<Void> clearCache() {
        ossConfigService.clearCache();
        return R.ok();
    }

    private Map<String, Object> type(String value, String label, boolean enabled, String description) {
        Map<String, Object> map = new HashMap<>();
        map.put("value", value);
        map.put("label", label);
        map.put("enabled", enabled);
        map.put("description", description);
        return map;
    }

    private String pickString(String value, Object fallback) {
        if (value != null && !value.isBlank()) {
            return value;
        }
        return fallback == null ? null : String.valueOf(fallback);
    }

    private Map<String, Object> getDefaultConfig(String storageType) {
        Map<String, Object> data = new LinkedHashMap<>();
        switch (storageType.toLowerCase()) {
            case "minio":
                OssProperties.MinioProperties minio = ossProperties.getMinio();
                data.put("endpoint", minio.getEndpoint());
                data.put("accessKey", minio.getAccessKey());
                data.put("secretKey", minio.getSecretKey());
                data.put("bucketName", minio.getBucketName());
                break;
            case "aliyun":
                OssProperties.AliyunProperties aliyun = ossProperties.getAliyun();
                data.put("endpoint", aliyun.getEndpoint());
                data.put("accessKeyId", aliyun.getAccessKeyId());
                data.put("accessKeySecret", aliyun.getAccessKeySecret());
                data.put("bucketName", aliyun.getBucketName());
                break;
            case "tencent":
                OssProperties.TencentProperties tencent = ossProperties.getTencent();
                data.put("region", tencent.getRegion());
                data.put("secretId", tencent.getSecretId());
                data.put("secretKey", tencent.getSecretKey());
                data.put("bucketName", tencent.getBucketName());
                break;
            case "qiniu":
                OssProperties.QiniuProperties qiniu = ossProperties.getQiniu();
                data.put("accessKey", qiniu.getAccessKey());
                data.put("secretKey", qiniu.getSecretKey());
                data.put("bucketName", qiniu.getBucketName());
                data.put("domain", qiniu.getDomain());
                break;
            default:
                break;
        }
        return data;
    }

    @Data
    public static class OssConfigSwitchRequest {
        private String storageType;
    }

    private String extractConfigJson(Map<String, Object> request) {
        if (request == null || request.isEmpty()) {
            return "{}";
        }
        Object configJson = request.get("configJson");
        if (configJson instanceof String && !((String) configJson).isBlank()) {
            return (String) configJson;
        }
        return JSONUtil.toJsonStr(request);
    }
}
