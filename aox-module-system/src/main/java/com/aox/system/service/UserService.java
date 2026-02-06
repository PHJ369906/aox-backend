package com.aox.system.service;

import com.aox.common.core.domain.PageResult;
import com.aox.system.domain.SysUser;
import com.aox.system.domain.request.UserCreateRequest;
import com.aox.system.domain.request.UserQueryRequest;
import com.aox.system.domain.request.UserUpdateRequest;
import com.aox.system.domain.vo.UserVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户服务接口
 *
 * @author Aox Team
 */
public interface UserService extends IService<SysUser> {

    /**
     * 分页查询用户列表
     *
     * @param request 查询参数
     * @return 分页结果
     */
    PageResult<UserVO> listUsers(UserQueryRequest request);

    /**
     * 根据ID查询用户
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    UserVO getUserById(Long userId);

    /**
     * 新增用户
     *
     * @param request 用户信息
     */
    void createUser(UserCreateRequest request);

    /**
     * 更新用户
     *
     * @param userId  用户ID
     * @param request 用户信息
     */
    void updateUser(Long userId, UserUpdateRequest request);

    /**
     * 删除用户
     *
     * @param userId 用户ID
     */
    void deleteUser(Long userId);

    /**
     * 更新用户状态
     *
     * @param userId 用户ID
     * @param status 状态
     */
    void updateUserStatus(Long userId, Integer status);

    /**
     * 重置密码
     *
     * @param userId      用户ID
     * @param newPassword 新密码
     */
    void resetPassword(Long userId, String newPassword);
}
