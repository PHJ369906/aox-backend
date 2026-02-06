package com.aox.system.service.impl;

import com.aox.common.exception.BusinessException;
import com.aox.system.domain.SysPost;
import com.aox.system.mapper.PostMapper;
import com.aox.system.service.PostService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 岗位信息服务实现
 *
 * @author Aox Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;

    @Override
    public IPage<SysPost> getPostList(Integer current, Integer size, String postCode, String postName) {
        Page<SysPost> page = new Page<>(current, size);
        LambdaQueryWrapper<SysPost> wrapper = new LambdaQueryWrapper<>();

        if (postCode != null && !postCode.isEmpty()) {
            wrapper.like(SysPost::getPostCode, postCode);
        }
        if (postName != null && !postName.isEmpty()) {
            wrapper.like(SysPost::getPostName, postName);
        }

        wrapper.eq(SysPost::getDeleted, 0)
                .orderByAsc(SysPost::getPostSort);

        return postMapper.selectPage(page, wrapper);
    }

    @Override
    public List<SysPost> getAllPosts() {
        LambdaQueryWrapper<SysPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysPost::getStatus, 0)
                .eq(SysPost::getDeleted, 0)
                .orderByAsc(SysPost::getPostSort);
        return postMapper.selectList(wrapper);
    }

    @Override
    public SysPost getPostById(Long postId) {
        return postMapper.selectById(postId);
    }

    @Override
    public SysPost getPostByCode(String postCode) {
        LambdaQueryWrapper<SysPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysPost::getPostCode, postCode)
                .eq(SysPost::getDeleted, 0);
        return postMapper.selectOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createPost(SysPost post) {
        // 检查岗位编码是否已存在
        LambdaQueryWrapper<SysPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysPost::getPostCode, post.getPostCode())
                .eq(SysPost::getDeleted, 0);
        SysPost existPost = postMapper.selectOne(wrapper);

        if (existPost != null) {
            throw new BusinessException("岗位编码已存在: " + post.getPostCode());
        }

        postMapper.insert(post);
        log.info("创建岗位成功: {}", post.getPostName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePost(SysPost post) {
        postMapper.updateById(post);
        log.info("更新岗位成功: {}", post.getPostId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePost(Long postId) {
        SysPost post = new SysPost();
        post.setPostId(postId);
        post.setDeleted(1);
        postMapper.updateById(post);
        log.info("删除岗位成功: {}", postId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeletePosts(List<Long> postIds) {
        postIds.forEach(this::deletePost);
    }
}
