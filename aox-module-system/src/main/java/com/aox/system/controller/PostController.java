package com.aox.system.controller;

import com.aox.common.core.domain.R;
import com.aox.common.log.annotation.Log;
import com.aox.system.domain.SysPost;
import com.aox.system.service.PostService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 岗位管理控制器
 *
 * @author Aox Team
 */
@RestController
@RequestMapping("/api/v1/system/post")
@RequiredArgsConstructor
@Tag(name = "岗位管理", description = "岗位信息管理")
public class PostController {

    private final PostService postService;

    /**
     * 分页查询岗位
     */
    @GetMapping
    @Operation(summary = "分页查询岗位")
    public R<IPage<SysPost>> getPostList(
            @RequestParam(value = "current", defaultValue = "1") Integer current,
            @RequestParam(value = "size", defaultValue = "20") Integer size,
            @RequestParam(value = "postCode", required = false) String postCode,
            @RequestParam(value = "postName", required = false) String postName) {

        IPage<SysPost> page = postService.getPostList(current, size, postCode, postName);
        return R.ok(page);
    }

    /**
     * 获取所有岗位
     */
    @GetMapping("/all")
    @Operation(summary = "获取所有岗位")
    public R<List<SysPost>> getAllPosts() {
        List<SysPost> list = postService.getAllPosts();
        return R.ok(list);
    }

    /**
     * 获取岗位详情
     */
    @GetMapping("/{postId}")
    @Operation(summary = "获取岗位详情")
    public R<SysPost> getPostDetail(@PathVariable Long postId) {
        SysPost post = postService.getPostById(postId);
        return R.ok(post);
    }

    /**
     * 创建岗位
     */
    @PostMapping
    @Operation(summary = "创建岗位")
    @Log(module = "岗位管理", operation = "创建岗位")
    public R<Void> createPost(@RequestBody SysPost post) {
        postService.createPost(post);
        return R.ok();
    }

    /**
     * 更新岗位
     */
    @PutMapping("/{postId}")
    @Operation(summary = "更新岗位")
    @Log(module = "岗位管理", operation = "更新岗位")
    public R<Void> updatePost(@PathVariable Long postId, @RequestBody SysPost post) {
        post.setPostId(postId);
        postService.updatePost(post);
        return R.ok();
    }

    /**
     * 删除岗位
     */
    @DeleteMapping("/{postId}")
    @Operation(summary = "删除岗位")
    @Log(module = "岗位管理", operation = "删除岗位")
    public R<Void> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return R.ok();
    }

    /**
     * 批量删除岗位
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除岗位")
    @Log(module = "岗位管理", operation = "批量删除岗位")
    public R<Void> batchDeletePosts(@RequestBody List<Long> postIds) {
        postService.batchDeletePosts(postIds);
        return R.ok();
    }
}
