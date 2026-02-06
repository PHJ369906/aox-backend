package com.aox.system.service;

import com.aox.system.domain.SysPost;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 岗位信息服务接口
 *
 * @author Aox Team
 */
public interface PostService {

    /**
     * 分页查询岗位
     */
    IPage<SysPost> getPostList(Integer current, Integer size, String postCode, String postName);

    /**
     * 获取所有岗位
     */
    List<SysPost> getAllPosts();

    /**
     * 根据ID查询岗位
     */
    SysPost getPostById(Long postId);

    /**
     * 根据岗位编码查询
     */
    SysPost getPostByCode(String postCode);

    /**
     * 创建岗位
     */
    void createPost(SysPost post);

    /**
     * 更新岗位
     */
    void updatePost(SysPost post);

    /**
     * 删除岗位
     */
    void deletePost(Long postId);

    /**
     * 批量删除岗位
     */
    void batchDeletePosts(List<Long> postIds);
}
