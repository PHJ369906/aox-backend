package com.aox.miniapp.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.aox.miniapp.domain.dto.MiniappUserQueryDTO;
import com.aox.miniapp.domain.vo.MiniappUserVO;
import com.aox.miniapp.domain.vo.UserStatisticsVO;
import com.aox.system.domain.SysUser;
import com.aox.system.mapper.SysUserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 后台管理-小程序用户服务
 *
 * @author Aox Team
 * @since 2026-02-02
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminMiniappUserService {

    private final SysUserMapper userMapper;

    /**
     * 分页查询用户列表
     */
    public Page<MiniappUserVO> getUserList(MiniappUserQueryDTO query) {
        log.info("分页查询小程序用户: query={}", query);

        // 构建查询条件
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getDeleted, 0);

        // 手机号模糊查询
        if (StrUtil.isNotBlank(query.getPhone())) {
            wrapper.like(SysUser::getPhone, query.getPhone());
        }

        // 昵称模糊查询
        if (StrUtil.isNotBlank(query.getNickname())) {
            wrapper.like(SysUser::getNickname, query.getNickname());
        }

        // 状态精确查询
        if (query.getStatus() != null) {
            wrapper.eq(SysUser::getStatus, query.getStatus());
        }

        // 注册时间范围查询
        if (query.getStartTime() != null) {
            wrapper.ge(SysUser::getCreateTime, query.getStartTime());
        }
        if (query.getEndTime() != null) {
            wrapper.le(SysUser::getCreateTime, query.getEndTime());
        }

        // 按创建时间倒序
        wrapper.orderByDesc(SysUser::getCreateTime);

        // 分页查询
        Page<SysUser> page = new Page<>(query.getPageNum(), query.getPageSize());
        Page<SysUser> userPage = userMapper.selectPage(page, wrapper);

        // 转换为VO
        Page<MiniappUserVO> voPage = new Page<>();
        voPage.setCurrent(userPage.getCurrent());
        voPage.setSize(userPage.getSize());
        voPage.setTotal(userPage.getTotal());
        voPage.setPages(userPage.getPages());

        List<MiniappUserVO> records = userPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(records);

        return voPage;
    }

    /**
     * 查询用户详情
     */
    public MiniappUserVO getUserDetail(Long userId) {
        log.info("查询用户详情: userId={}", userId);

        SysUser user = userMapper.selectById(userId);
        if (user == null || user.getDeleted() == 1) {
            throw new RuntimeException("用户不存在");
        }

        return convertToVO(user);
    }

    /**
     * 更新用户状态
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateUserStatus(Long userId, Integer status) {
        log.info("更新用户状态: userId={}, status={}", userId, status);

        // 验证状态值
        if (status != 0 && status != 1) {
            throw new RuntimeException("状态参数错误");
        }

        SysUser user = userMapper.selectById(userId);
        if (user == null || user.getDeleted() == 1) {
            throw new RuntimeException("用户不存在");
        }

        user.setStatus(status);
        int result = userMapper.updateById(user);
        if (result <= 0) {
            throw new RuntimeException("更新用户状态失败");
        }

        log.info("用户状态更新成功: userId={}, status={}", userId, status);
    }

    /**
     * 查询用户统计数据
     */
    public UserStatisticsVO getUserStatistics() {
        log.info("查询用户统计数据");

        // 用户总数
        Long totalUsers = userMapper.selectCount(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getDeleted, 0)
        );

        // 今日新增用户数
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        Long todayNewUsers = userMapper.selectCount(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getDeleted, 0)
                        .ge(SysUser::getCreateTime, todayStart)
        );

        // 活跃用户数（最近7天登录）
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        Long activeUsers = userMapper.selectCount(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getDeleted, 0)
                        .ge(SysUser::getLastLoginTime, sevenDaysAgo)
        );

        // 禁用用户数
        Long disabledUsers = userMapper.selectCount(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getDeleted, 0)
                        .eq(SysUser::getStatus, 1)
        );

        // 微信注册用户数（假设username以wx_开头表示微信注册）
        Long wechatUsers = userMapper.selectCount(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getDeleted, 0)
                        .likeRight(SysUser::getUsername, "wx_")
        );

        // 手机号注册用户数（有手机号且非微信注册）
        Long phoneUsers = userMapper.selectCount(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getDeleted, 0)
                        .isNotNull(SysUser::getPhone)
                        .notLikeRight(SysUser::getUsername, "wx_")
        );

        return UserStatisticsVO.builder()
                .totalUsers(totalUsers)
                .todayNewUsers(todayNewUsers)
                .activeUsers(activeUsers != null ? activeUsers : 0L)
                .disabledUsers(disabledUsers)
                .wechatUsers(wechatUsers)
                .phoneUsers(phoneUsers)
                .build();
    }

    /**
     * 转换为VO
     */
    private MiniappUserVO convertToVO(SysUser user) {
        return MiniappUserVO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .gender(user.getGender())
                .phone(user.getPhone())
                .email(user.getEmail())
                .signature(user.getRemark())
                .status(user.getStatus())
                .registerSource(getRegisterSource(user.getUsername()))
                .wxOpenid(user.getUsername().startsWith("wx_") ? user.getUsername() : null)
                .createdAt(user.getCreateTime())
                .lastLoginTime(user.getLastLoginTime())
                .lastLoginIp(user.getLastLoginIp())
                .build();
    }

    /**
     * 根据用户名推断注册来源
     */
    private String getRegisterSource(String username) {
        if (username.startsWith("wx_")) {
            return "wechat";
        } else if (username.matches("^1[3-9]\\d{9}$")) {
            return "sms";
        } else {
            return "password";
        }
    }
}
