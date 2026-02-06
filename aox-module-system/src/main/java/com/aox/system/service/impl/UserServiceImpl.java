package com.aox.system.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.aox.common.core.domain.PageResult;
import com.aox.common.core.utils.BeanConvertUtil;
import com.aox.system.domain.SysUser;
import com.aox.system.domain.request.UserCreateRequest;
import com.aox.system.domain.request.UserQueryRequest;
import com.aox.system.domain.request.UserUpdateRequest;
import com.aox.system.domain.vo.UserVO;
import com.aox.system.mapper.SysUserMapper;
import com.aox.system.mapper.SysUserRoleMapper;
import com.aox.system.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户服务实现
 *
 * @author Aox Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements UserService {

    private final SysUserMapper userMapper;
    private final SysUserRoleMapper userRoleMapper;

    @Override
    public PageResult<UserVO> listUsers(UserQueryRequest request) {
        // 构造查询条件
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                    .like(SysUser::getUsername, request.getKeyword())
                    .or()
                    .like(SysUser::getNickname, request.getKeyword()));
        }
        queryWrapper.like(request.getUsername() != null, SysUser::getUsername, request.getUsername())
                .like(request.getNickname() != null, SysUser::getNickname, request.getNickname())
                .like(request.getPhone() != null, SysUser::getPhone, request.getPhone())
                .eq(request.getStatus() != null, SysUser::getStatus, request.getStatus())
                .eq(request.getDeptId() != null, SysUser::getDeptId, request.getDeptId())
                .orderByDesc(SysUser::getCreateTime);

        // 分页查询
        Page<SysUser> page = new Page<>(request.getPageNum(), request.getPageSize());
        Page<SysUser> result = userMapper.selectPage(page, queryWrapper);

        // 转换为 VO
        return BeanConvertUtil.convertPage(result.getRecords(), UserVO.class,
                result.getTotal(), request.getPageNum(), request.getPageSize());
    }

    @Override
    public UserVO getUserById(Long userId) {
        SysUser user = userMapper.selectById(userId);
        return BeanConvertUtil.convert(user, UserVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createUser(UserCreateRequest request) {
        // 创建用户实体
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        // BCrypt 加密密码
        user.setPassword(BCrypt.hashpw(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setGender(request.getGender());
        user.setAvatar(request.getAvatar());
        user.setStatus(request.getStatus() != null ? request.getStatus() : 0);
        user.setDeptId(request.getDeptId());
        user.setRemark(request.getRemark());

        // 保存用户
        userMapper.insert(user);

        // 保存用户角色关联
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            userRoleMapper.batchInsert(user.getUserId(), request.getRoleIds());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(Long userId, UserUpdateRequest request) {
        // 更新用户信息
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setGender(request.getGender());
        user.setAvatar(request.getAvatar());
        user.setStatus(request.getStatus());
        user.setDeptId(request.getDeptId());
        user.setRemark(request.getRemark());

        userMapper.updateById(user);

        // 更新用户角色关联
        if (request.getRoleIds() != null) {
            // 先删除旧的角色关联
            userRoleMapper.deleteByUserId(userId);
            // 再插入新的角色关联
            if (!request.getRoleIds().isEmpty()) {
                userRoleMapper.batchInsert(userId, request.getRoleIds());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long userId) {
        // 删除用户
        userMapper.deleteById(userId);
        // 删除用户角色关联
        userRoleMapper.deleteByUserId(userId);
    }

    @Override
    public void updateUserStatus(Long userId, Integer status) {
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setStatus(status);
        userMapper.updateById(user);
    }

    @Override
    public void resetPassword(Long userId, String newPassword) {
        SysUser user = new SysUser();
        user.setUserId(userId);
        // BCrypt 加密新密码
        user.setPassword(BCrypt.hashpw(newPassword));
        userMapper.updateById(user);
    }
}
