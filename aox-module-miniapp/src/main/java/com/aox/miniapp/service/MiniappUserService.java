package com.aox.miniapp.service;

import cn.hutool.core.util.StrUtil;
import com.aox.common.exception.BusinessException;
import com.aox.infrastructure.sms.service.SmsService;
import com.aox.miniapp.domain.dto.UpdateUserInfoDTO;
import com.aox.miniapp.domain.vo.UserInfoVO;
import com.aox.system.domain.SysUser;
import com.aox.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 小程序用户服务
 *
 * @author Aox Team
 * @since 2026-02-02
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MiniappUserService {

    private final SysUserMapper userMapper;
    private final SmsService smsService;

    /**
     * 获取用户信息
     */
    public UserInfoVO getUserInfo(Long userId) {
        log.info("获取用户信息: userId={}", userId);

        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        return UserInfoVO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .gender(user.getGender())
                .phone(user.getPhone())
                .email(user.getEmail())
                .signature(user.getRemark()) // 使用 remark 字段存储个性签名
                .build();
    }

    /**
     * 更新用户信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateUserInfo(Long userId, UpdateUserInfoDTO dto) {
        log.info("更新用户信息: userId={}, dto={}", userId, dto);

        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 更新昵称
        if (StrUtil.isNotBlank(dto.getNickname())) {
            // 验证昵称长度
            if (dto.getNickname().length() > 20) {
                throw new BusinessException("昵称长度不能超过20个字符");
            }
            user.setNickname(dto.getNickname());
        }

        // 更新头像
        if (StrUtil.isNotBlank(dto.getAvatar())) {
            user.setAvatar(dto.getAvatar());
        }

        // 更新性别
        if (dto.getGender() != null) {
            if (dto.getGender() < 0 || dto.getGender() > 2) {
                throw new BusinessException("性别参数错误");
            }
            user.setGender(dto.getGender());
        }

        // 更新个性签名（使用 remark 字段）
        if (dto.getSignature() != null) {
            if (dto.getSignature().length() > 100) {
                throw new BusinessException("个性签名不能超过100个字符");
            }
            user.setRemark(dto.getSignature());
        }

        int result = userMapper.updateById(user);
        if (result <= 0) {
            throw new BusinessException("更新用户信息失败");
        }

        log.info("用户信息更新成功: userId={}", userId);
    }

    /**
     * 绑定手机号
     */
    @Transactional(rollbackFor = Exception.class)
    public void bindPhone(Long userId, String phone, String code) {
        log.info("绑定手机号: userId={}, phone={}", userId, phone);

        // 验证手机号格式
        if (!phone.matches("^1[3-9]\\d{9}$")) {
            throw new BusinessException("手机号格式不正确");
        }

        // 验证验证码
        boolean valid = smsService.verifyCode(phone, code);
        if (!valid) {
            throw new BusinessException("验证码错误或已过期");
        }

        // 检查手机号是否已被其他用户绑定
        SysUser existUser = userMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getPhone, phone)
                        .eq(SysUser::getDeleted, 0)
        );

        if (existUser != null && !existUser.getUserId().equals(userId)) {
            throw new BusinessException("该手机号已被其他用户绑定");
        }

        // 更新手机号
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        user.setPhone(phone);
        int result = userMapper.updateById(user);
        if (result <= 0) {
            throw new BusinessException("绑定手机号失败");
        }

        log.info("手机号绑定成功: userId={}, phone={}", userId, phone);
    }
}
