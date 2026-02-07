package com.aox.miniapp.service;

import cn.hutool.core.util.StrUtil;
import com.aox.common.exception.BusinessException;
import com.aox.common.security.context.SecurityContextHolder;
import com.aox.miniapp.domain.entity.BizOrder;
import com.aox.miniapp.domain.vo.MiniappOrderStatsVO;
import com.aox.miniapp.domain.vo.MiniappOrderVO;
import com.aox.miniapp.mapper.BizOrderMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 小程序订单服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MiniappOrderService {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final BizOrderMapper orderMapper;

    /**
     * 分页查询订单列表
     */
    public Page<MiniappOrderVO> getOrderPage(Integer pageNum, Integer pageSize, String status) {
        Long userId = getCurrentUserId();

        LambdaQueryWrapper<BizOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BizOrder::getUserId, userId);
        if (StrUtil.isNotBlank(status) && !"all".equalsIgnoreCase(status)) {
            wrapper.eq(BizOrder::getOrderStatus, status);
        }
        wrapper.orderByDesc(BizOrder::getOrderTime)
                .orderByDesc(BizOrder::getCreateTime);

        Page<BizOrder> page = new Page<>(pageNum, pageSize);
        Page<BizOrder> result = orderMapper.selectPage(page, wrapper);

        Page<MiniappOrderVO> voPage = new Page<>();
        voPage.setCurrent(result.getCurrent());
        voPage.setSize(result.getSize());
        voPage.setTotal(result.getTotal());
        voPage.setPages(result.getPages());
        voPage.setRecords(result.getRecords().stream().map(this::toOrderVO).collect(Collectors.toList()));

        return voPage;
    }

    /**
     * 查询订单统计
     */
    public MiniappOrderStatsVO getOrderStats() {
        Long userId = getCurrentUserId();

        long all = countByStatus(userId, null);
        long pay = countByStatus(userId, "pay");
        long ship = countByStatus(userId, "ship");
        long receive = countByStatus(userId, "receive");
        long refund = countByStatus(userId, "refund");

        return MiniappOrderStatsVO.builder()
                .all(all)
                .pay(pay)
                .ship(ship)
                .receive(receive)
                .refund(refund)
                .build();
    }

    private long countByStatus(Long userId, String status) {
        LambdaQueryWrapper<BizOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BizOrder::getUserId, userId);
        if (StrUtil.isNotBlank(status)) {
            wrapper.eq(BizOrder::getOrderStatus, status);
        }
        return orderMapper.selectCount(wrapper);
    }

    private MiniappOrderVO toOrderVO(BizOrder order) {
        LocalDateTime orderTime = order.getOrderTime() != null ? order.getOrderTime() : order.getCreateTime();

        return MiniappOrderVO.builder()
                .id(order.getOrderNo())
                .status(order.getOrderStatus())
                .title(order.getOrderTitle())
                .amount(order.getOrderAmount())
                .time(orderTime == null ? "" : orderTime.format(TIME_FORMATTER))
                .build();
    }

    private Long getCurrentUserId() {
        Long userId = SecurityContextHolder.getUserId();
        if (userId == null) {
            log.warn("获取订单失败，用户未登录");
            throw new BusinessException(401, "未登录或token已过期");
        }
        return userId;
    }
}
