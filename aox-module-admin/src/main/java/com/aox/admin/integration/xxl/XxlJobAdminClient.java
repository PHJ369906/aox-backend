package com.aox.admin.integration.xxl;

import cn.hutool.core.util.StrUtil;
import com.aox.infrastructure.job.XxlJobProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * XXL-Job 管理后台API客户端
 *
 * @author Aox Team
 * @since 2026-02-03
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class XxlJobAdminClient {

    private final XxlJobProperties xxlJobProperties;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    private volatile String loginCookie;

    public boolean isEnabled() {
        return Boolean.TRUE.equals(xxlJobProperties.getEnabled())
                && StrUtil.isNotBlank(xxlJobProperties.getAdminAddresses());
    }

    public XxlJobPageResult pageJobInfo(int start, int length, Integer jobGroup) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("start", String.valueOf(start));
        params.add("length", String.valueOf(length));
        if (jobGroup != null) {
            params.add("jobGroup", String.valueOf(jobGroup));
        }
        return postForm("/jobinfo/pageList", params);
    }

    public XxlJobPageResult pageJobLogs(int start,
                                       int length,
                                       Integer jobGroup,
                                       Integer jobId,
                                       Integer logStatus,
                                       String filterTime) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("start", String.valueOf(start));
        params.add("length", String.valueOf(length));
        if (jobGroup != null) {
            params.add("jobGroup", String.valueOf(jobGroup));
        }
        if (jobId != null) {
            params.add("jobId", String.valueOf(jobId));
        }
        if (logStatus != null) {
            params.add("logStatus", String.valueOf(logStatus));
        }
        if (StrUtil.isNotBlank(filterTime)) {
            params.add("filterTime", filterTime);
        }
        return postForm("/joblog/pageList", params);
    }

    public void triggerJob(Integer jobId) {
        if (jobId == null) {
            throw new IllegalArgumentException("jobId不能为空");
        }
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("id", String.valueOf(jobId));
        postForm("/jobinfo/trigger", params);
    }

    private XxlJobPageResult postForm(String path, MultiValueMap<String, String> params) {
        if (!isEnabled()) {
            return new XxlJobPageResult(Collections.emptyList(), 0L);
        }
        String url = buildUrl(path);
        ensureLogin();

        HttpHeaders headers = buildHeaders();
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        Map<String, Object> body = parseBody(response.getBody());
        if (body == null) {
            return new XxlJobPageResult(Collections.emptyList(), 0L);
        }

        Object data = body.get("data");
        Object recordsTotal = body.get("recordsTotal");
        long total = parseLong(recordsTotal, 0L);

        if (data instanceof List) {
            return new XxlJobPageResult((List<Map<String, Object>>) data, total);
        }

        return new XxlJobPageResult(Collections.emptyList(), total);
    }

    private Map<String, Object> parseBody(String body) {
        if (StrUtil.isBlank(body)) {
            return null;
        }
        try {
            return objectMapper.readValue(body, new TypeReference<Map<String, Object>>() {});
        } catch (Exception ex) {
            log.warn("解析XXL-Job响应失败: {}", ex.getMessage());
            return null;
        }
    }

    private void ensureLogin() {
        if (StrUtil.isBlank(xxlJobProperties.getAdminUsername())
                || StrUtil.isBlank(xxlJobProperties.getAdminPassword())) {
            return;
        }
        if (StrUtil.isNotBlank(loginCookie)) {
            return;
        }

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("userName", xxlJobProperties.getAdminUsername());
        params.add("password", xxlJobProperties.getAdminPassword());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.exchange(buildUrl("/login"), HttpMethod.POST, entity, String.class);

        List<String> cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
        if (cookies != null && !cookies.isEmpty()) {
            loginCookie = cookies.get(0);
        }
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        if (StrUtil.isNotBlank(loginCookie)) {
            headers.add(HttpHeaders.COOKIE, loginCookie);
        }
        if (StrUtil.isNotBlank(xxlJobProperties.getAccessToken())) {
            headers.add("XXL-JOB-ACCESS-TOKEN", xxlJobProperties.getAccessToken());
        }
        return headers;
    }

    private String buildUrl(String path) {
        String base = xxlJobProperties.getAdminAddresses();
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        return base + path;
    }

    private long parseLong(Object value, long defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        String text = String.valueOf(value);
        if (!StrUtil.isNumeric(text)) {
            return defaultValue;
        }
        try {
            return Long.parseLong(text);
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    public record XxlJobPageResult(List<Map<String, Object>> data, long total) {
    }
}
