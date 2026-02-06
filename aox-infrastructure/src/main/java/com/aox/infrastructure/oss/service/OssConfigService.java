package com.aox.infrastructure.oss.service;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.aox.common.security.context.SecurityContextHolder;
import com.aox.infrastructure.oss.client.OssClient;
import com.aox.infrastructure.oss.client.OssClientFactory;
import com.aox.system.domain.SysOssConfig;
import com.aox.system.mapper.OssConfigMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * OSS 配置服务
 * 从数据库 sys_config 表动态读取云存储配置
 *
 * @author Aox Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OssConfigService {

    private final OssClientFactory ossClientFactory;
    private final RedisTemplate<String, Object> redisTemplate;
    private final OssConfigMapper ossConfigMapper;

    /**
     * 配置缓存键
     */
    private static final String OSS_CONFIG_CACHE_KEY = "sys:config:oss:";

    /**
     * 缓存过期时间（秒）
     */
    private static final long CACHE_EXPIRE_TIME = 3600;

    /**
     * 获取当前生效的 OSS 客户端
     * 优先从 Redis 缓存读取配置，缓存未命中则从数据库读取
     *
     * @return OssClient 实例
     */
    public OssClient getCurrentOssClient() {
        // 1. 尝试从 Redis 缓存获取配置
        String storageType = (String) redisTemplate.opsForValue().get(getCacheKey());

        if (storageType != null) {
            log.debug("从缓存获取 OSS 配置，存储类型: {}", storageType);
            return ossClientFactory.createClient(storageType);
        }

        // 2. 缓存未命中，从数据库读取配置
        storageType = getStorageTypeFromDatabase();

        // 3. 缓存配置
        redisTemplate.opsForValue().set(getCacheKey(), storageType, CACHE_EXPIRE_TIME, TimeUnit.SECONDS);

        log.info("从数据库加载 OSS 配置，存储类型: {}", storageType);
        return ossClientFactory.createClient(storageType);
    }

    /**
     * 从数据库读取存储类型配置
     */
    private String getStorageTypeFromDatabase() {
        Long tenantId = getTenantId();
        LambdaQueryWrapper<SysOssConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysOssConfig::getTenantId, tenantId)
                .eq(SysOssConfig::getDeleted, 0)
                .eq(SysOssConfig::getIsCurrent, 1)
                .last("LIMIT 1");
        SysOssConfig config = ossConfigMapper.selectOne(wrapper);
        if (config != null && config.getStorageType() != null) {
            return config.getStorageType();
        }
        log.debug("未找到当前 OSS 配置，默认使用 minio");
        return "minio";
    }

    /**
     * 切换云存储类型
     * 更新数据库配置并刷新缓存
     *
     * @param storageType 存储类型
     */
    public void switchStorageType(String storageType) {
        log.info("切换云存储类型: {}", storageType);

        // 1. 验证存储类型是否支持
        try {
            ossClientFactory.createClient(storageType);
        } catch (Exception e) {
            throw new IllegalArgumentException("不支持的存储类型: " + storageType);
        }

        Long tenantId = getTenantId();

        // 2. 将当前配置置为非当前
        LambdaUpdateWrapper<SysOssConfig> clearCurrent = new LambdaUpdateWrapper<>();
        clearCurrent.eq(SysOssConfig::getTenantId, tenantId)
                .eq(SysOssConfig::getDeleted, 0)
                .set(SysOssConfig::getIsCurrent, 0);
        ossConfigMapper.update(null, clearCurrent);

        // 3. 设置目标配置为当前
        SysOssConfig target = getStorageConfigEntity(storageType);
        if (target == null) {
            target = new SysOssConfig();
            target.setTenantId(tenantId);
            target.setStorageType(storageType);
            target.setIsCurrent(1);
            target.setStatus(0);
            ossConfigMapper.insert(target);
        } else {
            target.setIsCurrent(1);
            ossConfigMapper.updateById(target);
        }

        // 4. 刷新缓存
        redisTemplate.opsForValue().set(getCacheKey(), storageType, CACHE_EXPIRE_TIME, TimeUnit.SECONDS);

        log.info("云存储类型切换成功: {}", storageType);
    }

    /**
     * 清除 OSS 配置缓存
     */
    public void clearCache() {
        redisTemplate.delete(getCacheKey());
        log.info("OSS 配置缓存已清除");
    }

    /**
     * 获取云存储配置（JSON 格式）
     *
     * @param storageType 存储类型
     * @return 配置 JSON
     */
    public JSONObject getStorageConfig(String storageType) {
        SysOssConfig config = getStorageConfigEntity(storageType);
        if (config == null) {
            return new JSONObject();
        }
        return toConfigJson(config);
    }

    /**
     * 更新云存储配置
     *
     * @param storageType 存储类型
     * @param configJson  配置 JSON
     */
    public void updateStorageConfig(String storageType, String configJson) {
        log.info("更新云存储配置，类型: {}, 配置: {}", storageType, configJson);

        // 1. 验证 JSON 格式
        if (!JSONUtil.isTypeJSON(configJson)) {
            throw new IllegalArgumentException("配置格式错误，必须为有效的 JSON");
        }

        // 2. 更新数据库配置
        saveStorageConfig(storageType, configJson);

        // 3. 清除缓存
        clearCache();

        log.info("云存储配置更新成功");
    }

    public java.util.List<SysOssConfig> listConfigs() {
        LambdaQueryWrapper<SysOssConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysOssConfig::getTenantId, getTenantId())
                .eq(SysOssConfig::getDeleted, 0)
                .orderByAsc(SysOssConfig::getStorageType);
        return ossConfigMapper.selectList(wrapper);
    }

    private Long getTenantId() {
        Long tenantId = SecurityContextHolder.getTenantId();
        return tenantId == null ? 0L : tenantId;
    }

    private String getCacheKey() {
        return OSS_CONFIG_CACHE_KEY + getTenantId();
    }

    private SysOssConfig getStorageConfigEntity(String storageType) {
        if (storageType == null) {
            return null;
        }
        String type = storageType.toLowerCase(Locale.ROOT);
        LambdaQueryWrapper<SysOssConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysOssConfig::getTenantId, getTenantId())
                .eq(SysOssConfig::getDeleted, 0)
                .eq(SysOssConfig::getStorageType, type)
                .last("LIMIT 1");
        return ossConfigMapper.selectOne(wrapper);
    }

    private void saveStorageConfig(String storageType, String configJson) {
        String type = storageType.toLowerCase(Locale.ROOT);
        JSONObject json = JSONUtil.parseObj(configJson);
        SysOssConfig config = getStorageConfigEntity(type);
        if (config == null) {
            config = new SysOssConfig();
            config.setTenantId(getTenantId());
            config.setStorageType(type);
            config.setIsCurrent(0);
            config.setStatus(0);
        }

        switch (type) {
            case "minio":
                config.setEndpoint(json.getStr("endpoint"));
                config.setAccessKey(json.getStr("accessKey"));
                config.setSecretKey(json.getStr("secretKey"));
                config.setBucketName(json.getStr("bucketName"));
                break;
            case "aliyun":
                config.setEndpoint(json.getStr("endpoint"));
                config.setAccessKeyId(json.getStr("accessKeyId"));
                config.setAccessKeySecret(json.getStr("accessKeySecret"));
                config.setBucketName(json.getStr("bucketName"));
                break;
            case "tencent":
                config.setRegion(json.getStr("region"));
                config.setAccessKey(json.getStr("secretId"));
                config.setSecretKey(json.getStr("secretKey"));
                config.setBucketName(json.getStr("bucketName"));
                break;
            case "qiniu":
                config.setAccessKey(json.getStr("accessKey"));
                config.setSecretKey(json.getStr("secretKey"));
                config.setBucketName(json.getStr("bucketName"));
                config.setDomain(json.getStr("domain"));
                break;
            default:
                throw new IllegalArgumentException("不支持的存储类型: " + storageType);
        }

        if (config.getId() == null) {
            ossConfigMapper.insert(config);
        } else {
            ossConfigMapper.updateById(config);
        }
    }

    private JSONObject toConfigJson(SysOssConfig config) {
        JSONObject json = new JSONObject();
        String type = config.getStorageType() == null ? "" : config.getStorageType().toLowerCase(Locale.ROOT);
        switch (type) {
            case "minio":
                json.set("endpoint", config.getEndpoint());
                json.set("accessKey", config.getAccessKey());
                json.set("secretKey", config.getSecretKey());
                json.set("bucketName", config.getBucketName());
                break;
            case "aliyun":
                json.set("endpoint", config.getEndpoint());
                json.set("accessKeyId", config.getAccessKeyId());
                json.set("accessKeySecret", config.getAccessKeySecret());
                json.set("bucketName", config.getBucketName());
                break;
            case "tencent":
                json.set("region", config.getRegion());
                json.set("secretId", config.getAccessKey());
                json.set("secretKey", config.getSecretKey());
                json.set("bucketName", config.getBucketName());
                break;
            case "qiniu":
                json.set("accessKey", config.getAccessKey());
                json.set("secretKey", config.getSecretKey());
                json.set("bucketName", config.getBucketName());
                json.set("domain", config.getDomain());
                break;
            default:
                break;
        }
        return json;
    }
}
