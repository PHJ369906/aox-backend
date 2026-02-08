package com.aox.infrastructure.payment.service;

import cn.hutool.core.util.StrUtil;
import com.aox.common.exception.BusinessException;
import com.aox.infrastructure.payment.domain.PaymentOrder;
import com.aox.infrastructure.payment.domain.dto.PaymentOrderQueryDTO;
import com.aox.infrastructure.payment.domain.vo.PaymentOrderExportVO;
import com.aox.infrastructure.payment.domain.vo.PaymentOrderVO;
import com.aox.infrastructure.payment.domain.vo.PaymentStatisticsVO;
import com.aox.infrastructure.payment.mapper.PaymentOrderMapper;
import com.aox.system.domain.SysUser;
import com.aox.system.mapper.SysUserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import com.alibaba.excel.EasyExcel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 后台管理-支付订单服务
 *
 * @author Aox Team
 * @since 2026-02-02
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminPaymentService {

    private final PaymentOrderMapper paymentOrderMapper;
    private final SysUserMapper userMapper;

    /**
     * 分页查询订单列表
     */
    public Page<PaymentOrderVO> getPaymentOrderList(PaymentOrderQueryDTO query) {
        log.info("分页查询支付订单: query={}", query);

        // 如果提供了手机号，先查询用户ID列表
        List<Long> userIds = resolveUserIdsByPhone(query.getPhone());
        if (userIds != null && userIds.isEmpty()) {
            Page<PaymentOrderVO> emptyPage = new Page<>(query.getPageNum(), query.getPageSize());
            emptyPage.setRecords(List.of());
            emptyPage.setTotal(0);
            return emptyPage;
        }

        // 构建查询条件
        LambdaQueryWrapper<PaymentOrder> wrapper = buildQueryWrapper(query, userIds);

        // 分页查询
        Page<PaymentOrder> page = new Page<>(query.getPageNum(), query.getPageSize());
        Page<PaymentOrder> orderPage = paymentOrderMapper.selectPage(page, wrapper);

        // 批量查询用户信息
        List<Long> orderUserIds = orderPage.getRecords().stream()
                .map(PaymentOrder::getUserId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, SysUser> userMap = Map.of();
        if (!orderUserIds.isEmpty()) {
            List<SysUser> users = userMapper.selectBatchIds(orderUserIds);
            userMap = users.stream().collect(Collectors.toMap(SysUser::getUserId, u -> u));
        }

        // 转换为VO
        Page<PaymentOrderVO> voPage = new Page<>();
        voPage.setCurrent(orderPage.getCurrent());
        voPage.setSize(orderPage.getSize());
        voPage.setTotal(orderPage.getTotal());
        voPage.setPages(orderPage.getPages());

        Map<Long, SysUser> finalUserMap = userMap;
        List<PaymentOrderVO> records = orderPage.getRecords().stream()
                .map(order -> convertToVO(order, finalUserMap.get(order.getUserId())))
                .collect(Collectors.toList());
        voPage.setRecords(records);

        return voPage;
    }

    /**
     * 查询订单详情
     */
    public PaymentOrderVO getPaymentOrderDetail(Long orderId) {
        log.info("查询订单详情: orderId={}", orderId);

        PaymentOrder order = paymentOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 查询用户信息
        SysUser user = null;
        if (order.getUserId() != null) {
            user = userMapper.selectById(order.getUserId());
        }

        return convertToVO(order, user);
    }

    /**
     * 查询订单统计数据
     */
    public PaymentStatisticsVO getPaymentStatistics() {
        log.info("查询订单统计数据");

        // 订单总数
        Long totalOrders = paymentOrderMapper.selectCount(new LambdaQueryWrapper<>());

        // 今日订单数
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        Long todayOrders = paymentOrderMapper.selectCount(
                new LambdaQueryWrapper<PaymentOrder>()
                        .ge(PaymentOrder::getCreateTime, todayStart)
        );

        // 订单总金额
        List<PaymentOrder> allOrders = paymentOrderMapper.selectList(new LambdaQueryWrapper<>());
        BigDecimal totalAmount = allOrders.stream()
                .map(PaymentOrder::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 今日订单金额
        List<PaymentOrder> todayOrderList = paymentOrderMapper.selectList(
                new LambdaQueryWrapper<PaymentOrder>()
                        .ge(PaymentOrder::getCreateTime, todayStart)
        );
        BigDecimal todayAmount = todayOrderList.stream()
                .map(PaymentOrder::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 成功订单数（已支付）
        Long successOrders = paymentOrderMapper.selectCount(
                new LambdaQueryWrapper<PaymentOrder>()
                        .eq(PaymentOrder::getStatus, 1)
        );

        // 待支付订单数
        Long pendingOrders = paymentOrderMapper.selectCount(
                new LambdaQueryWrapper<PaymentOrder>()
                        .eq(PaymentOrder::getStatus, 0)
        );

        // 已退款订单数
        Long refundOrders = paymentOrderMapper.selectCount(
                new LambdaQueryWrapper<PaymentOrder>()
                        .eq(PaymentOrder::getStatus, 2)
        );

        // 微信支付订单数
        Long wechatOrders = paymentOrderMapper.selectCount(
                new LambdaQueryWrapper<PaymentOrder>()
                        .eq(PaymentOrder::getPaymentType, "wechat")
        );

        // 支付宝订单数
        Long alipayOrders = paymentOrderMapper.selectCount(
                new LambdaQueryWrapper<PaymentOrder>()
                        .eq(PaymentOrder::getPaymentType, "alipay")
        );

        // 计算成功率
        Double successRate = 0.0;
        if (totalOrders > 0) {
            successRate = BigDecimal.valueOf(successOrders)
                    .divide(BigDecimal.valueOf(totalOrders), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        }

        return PaymentStatisticsVO.builder()
                .totalOrders(totalOrders)
                .todayOrders(todayOrders)
                .totalAmount(totalAmount)
                .todayAmount(todayAmount)
                .successOrders(successOrders)
                .pendingOrders(pendingOrders)
                .refundOrders(refundOrders)
                .wechatOrders(wechatOrders)
                .alipayOrders(alipayOrders)
                .successRate(successRate)
                .build();
    }

    /**
     * 导出订单列表
     */
    public void exportPaymentOrders(PaymentOrderQueryDTO query, HttpServletResponse response) {
        log.info("导出支付订单: query={}", query);

        List<Long> userIds = resolveUserIdsByPhone(query.getPhone());
        if (userIds != null && userIds.isEmpty()) {
            writeExportFile(response, List.of());
            return;
        }

        LambdaQueryWrapper<PaymentOrder> wrapper = buildQueryWrapper(query, userIds);
        List<PaymentOrder> orders = paymentOrderMapper.selectList(wrapper);

        Map<Long, SysUser> userMap = Map.of();
        List<Long> orderUserIds = orders.stream()
                .map(PaymentOrder::getUserId)
                .distinct()
                .collect(Collectors.toList());
        if (!orderUserIds.isEmpty()) {
            List<SysUser> users = userMapper.selectBatchIds(orderUserIds);
            userMap = users.stream().collect(Collectors.toMap(SysUser::getUserId, u -> u));
        }

        Map<Long, SysUser> finalUserMap = userMap;
        List<PaymentOrderExportVO> exportList = orders.stream()
                .map(order -> convertToExportVO(order, finalUserMap.get(order.getUserId())))
                .collect(Collectors.toList());

        writeExportFile(response, exportList);
    }

    /**
     * 转换为VO
     */
    private PaymentOrderVO convertToVO(PaymentOrder order, SysUser user) {
        return PaymentOrderVO.builder()
                .orderId(order.getOrderId())
                .orderNo(order.getOrderNo())
                .paymentNo(order.getPaymentNo())
                .paymentType(order.getPaymentType())
                .paymentMethod(order.getPaymentMethod())
                .amount(order.getAmount())
                .currency(order.getCurrency())
                .subject(order.getSubject())
                .body(order.getBody())
                .status(order.getStatus())
                .userId(order.getUserId())
                .userNickname(user != null ? user.getNickname() : null)
                .userPhone(user != null ? user.getPhone() : null)
                .payTime(order.getPayTime())
                .transactionId(order.getTransactionId())
                .tenantId(order.getTenantId())
                .createTime(order.getCreateTime())
                .updateTime(order.getUpdateTime())
                .build();
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<PaymentOrder> buildQueryWrapper(PaymentOrderQueryDTO query, List<Long> userIds) {
        LambdaQueryWrapper<PaymentOrder> wrapper = new LambdaQueryWrapper<>();

        if (StrUtil.isNotBlank(query.getOrderNo())) {
            wrapper.like(PaymentOrder::getOrderNo, query.getOrderNo());
        }

        if (userIds != null && !userIds.isEmpty()) {
            wrapper.in(PaymentOrder::getUserId, userIds);
        } else if (query.getUserId() != null) {
            wrapper.eq(PaymentOrder::getUserId, query.getUserId());
        }

        if (StrUtil.isNotBlank(query.getPaymentType())) {
            wrapper.eq(PaymentOrder::getPaymentType, query.getPaymentType());
        }

        if (query.getStatus() != null) {
            wrapper.eq(PaymentOrder::getStatus, query.getStatus());
        }

        if (query.getStartTime() != null) {
            wrapper.ge(PaymentOrder::getCreateTime, query.getStartTime());
        }
        if (query.getEndTime() != null) {
            wrapper.le(PaymentOrder::getCreateTime, query.getEndTime());
        }

        wrapper.orderByDesc(PaymentOrder::getCreateTime);
        return wrapper;
    }

    /**
     * 根据手机号查询用户ID列表
     */
    private List<Long> resolveUserIdsByPhone(String phone) {
        if (StrUtil.isBlank(phone)) {
            return null;
        }
        LambdaQueryWrapper<SysUser> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.like(SysUser::getPhone, phone);
        userWrapper.eq(SysUser::getDeleted, 0);
        List<SysUser> users = userMapper.selectList(userWrapper);
        return users.stream().map(SysUser::getUserId).collect(Collectors.toList());
    }

    /**
     * 写入导出文件
     */
    private void writeExportFile(HttpServletResponse response, List<PaymentOrderExportVO> exportList) {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("支付订单_" + LocalDate.now(), StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            EasyExcel.write(response.getOutputStream(), PaymentOrderExportVO.class)
                    .autoCloseStream(false)
                    .sheet("支付订单")
                    .doWrite(exportList);
        } catch (IOException e) {
            throw new BusinessException("导出失败", e);
        }
    }

    /**
     * 转换为导出VO
     */
    private PaymentOrderExportVO convertToExportVO(PaymentOrder order, SysUser user) {
        return PaymentOrderExportVO.builder()
                .orderId(order.getOrderId())
                .orderNo(order.getOrderNo())
                .userNickname(user != null ? user.getNickname() : "")
                .userPhone(user != null ? user.getPhone() : "")
                .subject(order.getSubject())
                .amount(order.getAmount())
                .paymentType(formatPaymentType(order.getPaymentType()))
                .status(formatStatus(order.getStatus()))
                .paymentMethod(order.getPaymentMethod())
                .transactionId(order.getTransactionId())
                .createTime(formatDateTime(order.getCreateTime()))
                .payTime(formatDateTime(order.getPayTime()))
                .build();
    }

    private String formatPaymentType(String paymentType) {
        if ("wechat".equalsIgnoreCase(paymentType)) {
            return "微信支付";
        }
        if ("alipay".equalsIgnoreCase(paymentType)) {
            return "支付宝";
        }
        return paymentType != null ? paymentType : "-";
    }

    private String formatStatus(Integer status) {
        if (status == null) {
            return "-";
        }
        return switch (status) {
            case 0 -> "待支付";
            case 1 -> "已支付";
            case 2 -> "已退款";
            case 3 -> "已关闭";
            default -> "未知";
        };
    }

    private String formatDateTime(LocalDateTime time) {
        if (time == null) {
            return "";
        }
        return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
