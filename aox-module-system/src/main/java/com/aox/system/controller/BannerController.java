package com.aox.system.controller;

import com.aox.common.core.domain.R;
import com.aox.common.security.annotation.RequirePermission;
import com.aox.system.domain.Banner;
import com.aox.system.domain.dto.BannerDTO;
import com.aox.system.service.BannerService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 后台管理-Banner控制器
 *
 * @author Aox Team
 * @since 2026-02-02
 */
@Tag(name = "后台管理-Banner", description = "Banner轮播图管理接口")
@RestController
@RequestMapping("/api/v1/admin/banners")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    @Operation(summary = "分页查询Banner列表", description = "支持状态筛选")
    @GetMapping
    @RequirePermission("banner:list")
    public R<Page<Banner>> getBannerList(
            @Parameter(description = "页码") @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @Parameter(description = "状态：0-下架，1-上架") @RequestParam(value = "status", required = false) Integer status
    ) {
        Page<Banner> page = bannerService.getBannerList(pageNum, pageSize, status);
        return R.ok(page);
    }

    @Operation(summary = "查询Banner详情", description = "根据ID查询Banner详细信息")
    @GetMapping("/{id}")
    @RequirePermission("banner:detail")
    public R<Banner> getBannerDetail(
            @Parameter(description = "Banner ID") @PathVariable Long id
    ) {
        Banner banner = bannerService.getBannerDetail(id);
        return R.ok(banner);
    }

    @Operation(summary = "创建Banner", description = "添加新的Banner")
    @PostMapping
    @RequirePermission("banner:create")
    public R<Void> createBanner(@Valid @RequestBody BannerDTO dto) {
        bannerService.createBanner(dto);
        return R.ok();
    }

    @Operation(summary = "更新Banner", description = "更新Banner信息")
    @PutMapping("/{id}")
    @RequirePermission("banner:update")
    public R<Void> updateBanner(
            @Parameter(description = "Banner ID") @PathVariable Long id,
            @Valid @RequestBody BannerDTO dto
    ) {
        bannerService.updateBanner(id, dto);
        return R.ok();
    }

    @Operation(summary = "删除Banner", description = "删除指定Banner")
    @DeleteMapping("/{id}")
    @RequirePermission("banner:delete")
    public R<Void> deleteBanner(
            @Parameter(description = "Banner ID") @PathVariable Long id
    ) {
        bannerService.deleteBanner(id);
        return R.ok();
    }

    @Operation(summary = "更新Banner状态", description = "上架或下架Banner")
    @PutMapping("/{id}/status")
    @RequirePermission("banner:update")
    public R<Void> updateStatus(
            @Parameter(description = "Banner ID") @PathVariable Long id,
            @Parameter(description = "状态：0-下架，1-上架") @RequestParam Integer status
    ) {
        bannerService.updateStatus(id, status);
        return R.ok();
    }

    @Operation(summary = "批量更新排序", description = "拖拽排序后批量更新")
    @PutMapping("/sort")
    @RequirePermission("banner:update")
    public R<Void> batchUpdateSort(@RequestBody List<Banner> banners) {
        bannerService.batchUpdateSort(banners);
        return R.ok();
    }
}
