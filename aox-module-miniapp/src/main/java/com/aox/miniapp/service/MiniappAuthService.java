package com.aox.miniapp.service;

import cn.hutool.crypto.digest.BCrypt;
import com.aox.common.exception.BusinessException;
import com.aox.common.security.utils.JwtTokenUtil;
import com.aox.infrastructure.sms.service.SmsService;
import com.aox.miniapp.domain.dto.PasswordLoginDTO;
import com.aox.miniapp.domain.dto.SmsLoginDTO;
import com.aox.miniapp.domain.dto.WxLoginDTO;
import com.aox.miniapp.domain.vo.LoginVO;
import com.aox.system.domain.SysUser;
import com.aox.system.mapper.SysUserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 小程序认证服务
 *
 * @author Aox Team
 * @since 2026-02-02
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MiniappAuthService {

    private final SysUserMapper userMapper;
    private final SmsService smsService;
    private final JwtTokenUtil jwtTokenUtil;

    /**
     * 账号密码登录
     */
    public LoginVO passwordLogin(PasswordLoginDTO dto) {
        log.info("小程序账号密码登录: username={}", dto.getUsername());

        // 1. 查询用户（支持手机号或用户名）
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(SysUser::getUsername, dto.getUsername())
                         .or()
                         .eq(SysUser::getPhone, dto.getUsername()));
        wrapper.eq(SysUser::getDeleted, 0);

        SysUser user = userMapper.selectOne(wrapper);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 2. 验证密码
        if (!BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("密码错误");
        }

        // 3. 检查用户状态
        if (user.getStatus() != 0) {
            throw new BusinessException("账号已被禁用");
        }

        // 4. 生成 Token
        String token = jwtTokenUtil.generateToken(user.getUserId(), user.getUsername(), "miniapp");

        log.info("账号密码登录成功: userId={}, username={}", user.getUserId(), user.getUsername());

        return buildLoginVO(user, token);
    }

    /**
     * 短信验证码登录
     */
    public LoginVO smsLogin(SmsLoginDTO dto) {
        log.info("小程序短信验证码登录: phone={}", dto.getPhone());

        // 1. 验证验证码
        boolean valid = smsService.verifyCode(dto.getPhone(), dto.getCode());
        if (!valid) {
            throw new BusinessException("验证码错误或已过期");
        }

        // 2. 查询用户
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getPhone, dto.getPhone());
        wrapper.eq(SysUser::getDeleted, 0);

        SysUser user = userMapper.selectOne(wrapper);

        // 3. 如果用户不存在，自动注册
        if (user == null) {
            user = registerByPhone(dto.getPhone());
            log.info("新用户自动注册: userId={}, phone={}", user.getUserId(), dto.getPhone());
        }

        // 4. 检查用户状态
        if (user.getStatus() != 0) {
            throw new BusinessException("账号已被禁用");
        }

        // 5. 生成 Token
        String token = jwtTokenUtil.generateToken(user.getUserId(), user.getUsername(), "miniapp");

        log.info("短信验证码登录成功: userId={}, phone={}", user.getUserId(), dto.getPhone());

        return buildLoginVO(user, token);
    }

    /**
     * 微信授权登录
     */
    public LoginVO wechatLogin(WxLoginDTO dto) {
        log.info("小程序微信授权登录: code={}", dto.getCode());

        // 1. 调用微信接口获取 openid 和 session_key
        // 注意：需要配置小程序的 appId 和 appSecret
        // WxLoginResult wxResult = getWxOpenid(dto.getCode());
        // String openid = wxResult.getOpenid();

        // 开发模式：模拟 openid
        String openid = "mock_openid_" + System.currentTimeMillis();
        log.warn("【开发模式】使用模拟 openid: {}", openid);

        // 2. 根据 openid 查询用户
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        // 假设在 SysUser 表中有 openid 字段
        // wrapper.eq(SysUser::getOpenid, openid);
        wrapper.eq(SysUser::getDeleted, 0);
        wrapper.last("LIMIT 1");

        SysUser user = userMapper.selectOne(wrapper);

        // 3. 如果用户不存在，自动注册
        if (user == null) {
            user = registerByWechat(openid, dto.getUserInfo());
            log.info("微信用户自动注册: userId={}, openid={}", user.getUserId(), openid);
        }

        // 4. 生成 Token
        String token = jwtTokenUtil.generateToken(user.getUserId(), user.getUsername(), "miniapp");

        log.info("微信授权登录成功: userId={}, openid={}", user.getUserId(), openid);

        return buildLoginVO(user, token);
    }

    /**
     * 发送短信验证码
     */
    public void sendSmsCode(String phone) {
        log.info("发送短信验证码: phone={}", phone);

        // 验证手机号格式
        if (!phone.matches("^1[3-9]\\d{9}$")) {
            throw new BusinessException("手机号格式不正确");
        }

        // 调用短信服务发送验证码
        boolean success = smsService.sendCode(phone);
        if (!success) {
            throw new BusinessException("验证码发送失败");
        }

        log.info("验证码发送成功: phone={}", phone);
    }

    /**
     * 通过手机号注册新用户
     */
    private SysUser registerByPhone(String phone) {
        SysUser user = new SysUser();
        user.setUsername("user_" + phone);
        user.setPhone(phone);
        user.setNickname("用户" + phone.substring(7));
        user.setPassword(BCrypt.hashpw("123456", BCrypt.gensalt())); // 默认密码
        user.setStatus(0);
        user.setDeleted(0);

        userMapper.insert(user);
        return user;
    }

    /**
     * 通过微信注册新用户
     */
    private SysUser registerByWechat(String openid, Object userInfo) {
        SysUser user = new SysUser();
        user.setUsername("wx_" + openid.substring(0, 10));
        user.setNickname("微信用户");
        user.setPassword(BCrypt.hashpw("123456", BCrypt.gensalt())); // 默认密码
        user.setStatus(0);
        user.setDeleted(0);
        // user.setOpenid(openid);

        userMapper.insert(user);
        return user;
    }

    /**
     * 构建登录响应
     */
    private LoginVO buildLoginVO(SysUser user, String token) {
        return LoginVO.builder()
                .token(token)
                .user(LoginVO.UserInfoVO.builder()
                        .userId(user.getUserId())
                        .nickname(user.getNickname())
                        .avatar(user.getAvatar())
                        .phone(user.getPhone())
                        .build())
                .build();
    }
}
