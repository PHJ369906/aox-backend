package com.aox.system.service.impl;

import com.aox.common.core.constant.Constants;
import com.aox.common.core.enums.ErrorCode;
import com.aox.common.exception.BusinessException;
import com.aox.common.log.service.AsyncLogService;
import com.aox.common.redis.service.RedisService;
import com.aox.common.security.utils.JwtTokenUtil;
import com.aox.system.domain.SysUser;
import com.aox.system.domain.request.LoginRequest;
import com.aox.system.domain.vo.LoginResponse;
import com.aox.system.domain.vo.UserVO;
import com.aox.system.service.AuthService;
import com.aox.system.service.PermissionService;
import com.aox.system.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;

/**
 * 认证服务实现
 *
 * @author Aox Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final PermissionService permissionService;
    private final AsyncLogService asyncLogService;
    private final JwtTokenUtil jwtTokenUtil;
    private final RedisService redisService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginResponse login(LoginRequest request, String ip, String userAgent) {
        String username = request.getUsername();

        try {
            // 1. 查询用户
            SysUser user = userService.getOne(
                new LambdaQueryWrapper<SysUser>()
                    .eq(SysUser::getUsername, username)
            );

            if (user == null) {
                // 保存登录失败日志
                asyncLogService.saveLoginLog(username, 1, 1, ip, userAgent, "用户不存在");
                throw new BusinessException(ErrorCode.USER_NOT_EXIST);
            }

            // 2. 校验密码
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                // 保存登录失败日志
                asyncLogService.saveLoginLog(username, 1, 1, ip, userAgent, "密码错误");
                throw new BusinessException(ErrorCode.PASSWORD_ERROR);
            }

            // 3. 校验用户状态
            if (Constants.STATUS_DISABLE.equals(user.getStatus())) {
                // 保存登录失败日志
                asyncLogService.saveLoginLog(username, 1, 1, ip, userAgent, "用户已被禁用");
                throw new BusinessException(ErrorCode.USER_DISABLED);
            }

            // 4. 生成 Token
            String token = jwtTokenUtil.generateToken(user.getUserId(), user.getUsername(), "admin");

            // 5. 缓存 Token
            String tokenKey = Constants.LOGIN_TOKEN_KEY + token;
            redisService.set(tokenKey, user.getUserId(), Constants.TOKEN_EXPIRATION);

            // 6. 更新登录信息
            user.setLastLoginTime(LocalDateTime.now());
            user.setLastLoginIp(ip);
            userService.updateById(user);

            // 7. 构造返回结果
            LoginResponse response = new LoginResponse();
            response.setToken(token);
            response.setRefreshToken(token);

            UserVO userVO = new UserVO();
            userVO.setUserId(user.getUserId());
            userVO.setUsername(user.getUsername());
            userVO.setNickname(user.getNickname());
            userVO.setAvatar(user.getAvatar());
            response.setUser(userVO);

            // 8. 查询用户权限和角色（从数据库动态查询）
            Set<String> permissionSet = permissionService.getPermissionCodesByUserId(user.getUserId());
            Set<String> roleSet = permissionService.getRoleCodesByUserId(user.getUserId());

            response.setPermissions(new ArrayList<>(permissionSet));
            response.setRoles(new ArrayList<>(roleSet));

            // 9. 缓存用户权限信息（供 JWT Filter 使用）
            permissionService.cacheUserPermissions(user.getUserId());

            // 10. 保存登录成功日志
            asyncLogService.saveLoginLog(username, 1, 0, ip, userAgent, null);

            log.info("用户 {} 登录成功，IP: {}", user.getUsername(), ip);
            return response;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            // 保存登录失败日志
            asyncLogService.saveLoginLog(username, 1, 1, ip, userAgent, e.getMessage());
            throw e;
        }
    }

    @Override
    public LoginResponse login(LoginRequest request, String ip) {
        return login(request, ip, null);
    }

    @Override
    public void logout(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (token != null) {
            String tokenKey = Constants.LOGIN_TOKEN_KEY + token;
            redisService.del(tokenKey);
        }
    }

    @Override
    public UserVO getCurrentUser(Long userId) {
        SysUser user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        UserVO userVO = new UserVO();
        userVO.setUserId(user.getUserId());
        userVO.setUsername(user.getUsername());
        userVO.setNickname(user.getNickname());
        userVO.setAvatar(user.getAvatar());
        userVO.setEmail(user.getEmail());
        userVO.setPhone(user.getPhone());
        userVO.setGender(user.getGender());
        userVO.setStatus(user.getStatus());

        return userVO;
    }
}
