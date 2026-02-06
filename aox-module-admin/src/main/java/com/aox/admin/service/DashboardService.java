package com.aox.admin.service;

import com.aox.admin.domain.vo.DashboardStatisticsVO;
import com.aox.admin.domain.vo.PaymentMethodVO;
import com.aox.admin.domain.vo.PaymentTrendVO;
import com.aox.admin.domain.vo.UserTrendVO;
import com.aox.common.log.domain.SysLoginLog;
import com.aox.common.log.mapper.SysLoginLogMapper;
import com.aox.infrastructure.payment.domain.PaymentOrder;
import com.aox.infrastructure.payment.mapper.PaymentOrderMapper;
import com.aox.system.domain.SysUser;
import com.aox.system.mapper.SysUserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Dashboard统计服务
 *
 * @author Aox Team
 * @since 2026-02-02
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final SysUserMapper userMapper;
    private final PaymentOrderMapper paymentOrderMapper;
    private final SysLoginLogMapper loginLogMapper;

    /**
     * 获取综合统计数据
     */
    public DashboardStatisticsVO getStatistics() {
        log.info("查询Dashboard综合统计数据");

        // 今日开始时间
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        // 昨日开始时间
        LocalDateTime yesterdayStart = todayStart.minusDays(1);

        // 用户统计
        Long totalUsers = userMapper.selectCount(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getDeleted, 0)
        );

        Long todayNewUsers = userMapper.selectCount(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getDeleted, 0)
                        .ge(SysUser::getCreateTime, todayStart)
        );

        Long yesterdayNewUsers = userMapper.selectCount(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getDeleted, 0)
                        .ge(SysUser::getCreateTime, yesterdayStart)
                        .lt(SysUser::getCreateTime, todayStart)
        );

        Double userGrowthRate = calculateGrowthRate(todayNewUsers, yesterdayNewUsers);

        // 活跃用户（最近7天登录）
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        Long activeUsers = userMapper.selectCount(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getDeleted, 0)
                        .ge(SysUser::getLastLoginTime, sevenDaysAgo)
        );

        // 订单统计
        Long totalOrders = paymentOrderMapper.selectCount(new LambdaQueryWrapper<>());

        Long todayOrders = paymentOrderMapper.selectCount(
                new LambdaQueryWrapper<PaymentOrder>()
                        .ge(PaymentOrder::getCreateTime, todayStart)
        );

        Long yesterdayOrders = paymentOrderMapper.selectCount(
                new LambdaQueryWrapper<PaymentOrder>()
                        .ge(PaymentOrder::getCreateTime, yesterdayStart)
                        .lt(PaymentOrder::getCreateTime, todayStart)
        );

        Double orderGrowthRate = calculateGrowthRate(todayOrders, yesterdayOrders);

        // 订单金额统计
        List<PaymentOrder> allOrders = paymentOrderMapper.selectList(new LambdaQueryWrapper<>());
        BigDecimal totalAmount = allOrders.stream()
                .map(PaymentOrder::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<PaymentOrder> todayOrderList = paymentOrderMapper.selectList(
                new LambdaQueryWrapper<PaymentOrder>()
                        .ge(PaymentOrder::getCreateTime, todayStart)
        );
        BigDecimal todayAmount = todayOrderList.stream()
                .map(PaymentOrder::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<PaymentOrder> yesterdayOrderList = paymentOrderMapper.selectList(
                new LambdaQueryWrapper<PaymentOrder>()
                        .ge(PaymentOrder::getCreateTime, yesterdayStart)
                        .lt(PaymentOrder::getCreateTime, todayStart)
        );
        BigDecimal yesterdayAmount = yesterdayOrderList.stream()
                .map(PaymentOrder::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Double amountGrowthRate = calculateGrowthRate(
                todayAmount.doubleValue(),
                yesterdayAmount.doubleValue()
        );

        // 登录统计（仅统计成功登录）
        Long totalLogins = loginLogMapper.selectCount(
                new LambdaQueryWrapper<SysLoginLog>()
                        .eq(SysLoginLog::getStatus, 0)
        );

        Long todayLogins = loginLogMapper.selectCount(
                new LambdaQueryWrapper<SysLoginLog>()
                        .eq(SysLoginLog::getStatus, 0)
                        .ge(SysLoginLog::getLoginTime, todayStart)
        );

        return DashboardStatisticsVO.builder()
                .totalUsers(totalUsers)
                .todayNewUsers(todayNewUsers)
                .userGrowthRate(userGrowthRate)
                .totalOrders(totalOrders)
                .todayOrders(todayOrders)
                .orderGrowthRate(orderGrowthRate)
                .totalAmount(totalAmount)
                .todayAmount(todayAmount)
                .amountGrowthRate(amountGrowthRate)
                .totalLogins(totalLogins)
                .todayLogins(todayLogins)
                .activeUsers(activeUsers != null ? activeUsers : 0L)
                .build();
    }

    /**
     * 获取用户趋势数据（最近30天）
     */
    public List<UserTrendVO> getUserTrend() {
        log.info("查询用户趋势数据（最近30天）");

        List<UserTrendVO> trendList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        for (int i = 29; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();

            // 当天新增用户
            Long newUsers = userMapper.selectCount(
                    new LambdaQueryWrapper<SysUser>()
                            .eq(SysUser::getDeleted, 0)
                            .ge(SysUser::getCreateTime, dayStart)
                            .lt(SysUser::getCreateTime, dayEnd)
            );

            // 当天活跃用户
            Long activeUsers = userMapper.selectCount(
                    new LambdaQueryWrapper<SysUser>()
                            .eq(SysUser::getDeleted, 0)
                            .ge(SysUser::getLastLoginTime, dayStart)
                            .lt(SysUser::getLastLoginTime, dayEnd)
            );

            trendList.add(UserTrendVO.builder()
                    .date(date.format(formatter))
                    .newUsers(newUsers)
                    .activeUsers(activeUsers != null ? activeUsers : 0L)
                    .build());
        }

        return trendList;
    }

    /**
     * 获取支付趋势数据（最近30天）
     */
    public List<PaymentTrendVO> getPaymentTrend() {
        log.info("查询支付趋势数据（最近30天）");

        List<PaymentTrendVO> trendList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        for (int i = 29; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();

            // 当天订单数
            Long orderCount = paymentOrderMapper.selectCount(
                    new LambdaQueryWrapper<PaymentOrder>()
                            .ge(PaymentOrder::getCreateTime, dayStart)
                            .lt(PaymentOrder::getCreateTime, dayEnd)
            );

            // 当天订单金额
            List<PaymentOrder> dayOrders = paymentOrderMapper.selectList(
                    new LambdaQueryWrapper<PaymentOrder>()
                            .ge(PaymentOrder::getCreateTime, dayStart)
                            .lt(PaymentOrder::getCreateTime, dayEnd)
            );
            BigDecimal amount = dayOrders.stream()
                    .map(PaymentOrder::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            trendList.add(PaymentTrendVO.builder()
                    .date(date.format(formatter))
                    .orderCount(orderCount)
                    .amount(amount)
                    .build());
        }

        return trendList;
    }

    /**
     * 获取支付方式分布
     */
    public List<PaymentMethodVO> getPaymentMethods() {
        log.info("查询支付方式分布");

        // 总订单数
        Long totalOrders = paymentOrderMapper.selectCount(new LambdaQueryWrapper<>());

        // 微信支付订单数
        Long wechatCount = paymentOrderMapper.selectCount(
                new LambdaQueryWrapper<PaymentOrder>()
                        .eq(PaymentOrder::getPaymentType, "wechat")
        );

        // 支付宝订单数
        Long alipayCount = paymentOrderMapper.selectCount(
                new LambdaQueryWrapper<PaymentOrder>()
                        .eq(PaymentOrder::getPaymentType, "alipay")
        );

        Double wechatPercentage = totalOrders > 0
                ? BigDecimal.valueOf(wechatCount)
                .divide(BigDecimal.valueOf(totalOrders), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue()
                : 0.0;

        Double alipayPercentage = totalOrders > 0
                ? BigDecimal.valueOf(alipayCount)
                .divide(BigDecimal.valueOf(totalOrders), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue()
                : 0.0;

        List<PaymentMethodVO> methodList = new ArrayList<>();
        methodList.add(PaymentMethodVO.builder()
                .method("微信支付")
                .count(wechatCount)
                .percentage(wechatPercentage)
                .build());
        methodList.add(PaymentMethodVO.builder()
                .method("支付宝")
                .count(alipayCount)
                .percentage(alipayPercentage)
                .build());

        return methodList;
    }

    /**
     * 计算增长率
     */
    private Double calculateGrowthRate(Long current, Long previous) {
        if (previous == null || previous == 0) {
            return current > 0 ? 100.0 : 0.0;
        }
        return BigDecimal.valueOf(current - previous)
                .divide(BigDecimal.valueOf(previous), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    /**
     * 计算增长率（Double版本）
     */
    private Double calculateGrowthRate(Double current, Double previous) {
        if (previous == null || previous == 0) {
            return current > 0 ? 100.0 : 0.0;
        }
        return BigDecimal.valueOf(current - previous)
                .divide(BigDecimal.valueOf(previous), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }
}
