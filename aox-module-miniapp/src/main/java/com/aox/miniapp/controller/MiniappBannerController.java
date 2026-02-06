package com.aox.miniapp.controller;

import com.aox.common.core.domain.R;
import com.aox.system.domain.Banner;
import com.aox.system.service.BannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 小程序-Banner控制器
 *
 * @author Aox Team
 * @since 2026-02-02
 */
@Tag(name = "小程序-Banner", description = "小程序Banner查询接口")
@RestController
@RequestMapping("/api/v1/miniapp/banners")
@RequiredArgsConstructor
public class MiniappBannerController {

    private final BannerService bannerService;

    @Operation(summary = "查询已上架Banner列表", description = "查询所有已上架的Banner，按排序升序")
    @GetMapping
    public R<List<Banner>> getPublishedBanners() {
        List<Banner> banners = bannerService.getPublishedBanners();
        return R.ok(banners);
    }
}
