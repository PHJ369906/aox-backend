package com.aox.miniapp.controller;

import com.aox.common.core.domain.R;
import com.aox.miniapp.domain.vo.MiniappFavoriteVO;
import com.aox.miniapp.service.MiniappFavoriteService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 小程序收藏控制器
 */
@Tag(name = "小程序-收藏", description = "小程序收藏相关接口")
@RestController
@RequestMapping("/api/v1/miniapp/favorites")
@RequiredArgsConstructor
public class MiniappFavoriteController {

    private final MiniappFavoriteService favoriteService;

    @Operation(summary = "分页查询收藏", description = "查询当前用户收藏内容")
    @GetMapping
    public R<Page<MiniappFavoriteVO>> getFavoritePage(
            @Parameter(description = "页码") @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize
    ) {
        return R.ok(favoriteService.getFavoritePage(pageNum, pageSize));
    }

    @Operation(summary = "取消收藏", description = "根据收藏ID取消收藏")
    @DeleteMapping("/{favoriteId}")
    public R<Void> removeFavorite(@PathVariable Long favoriteId) {
        favoriteService.removeFavorite(favoriteId);
        return R.ok();
    }
}
