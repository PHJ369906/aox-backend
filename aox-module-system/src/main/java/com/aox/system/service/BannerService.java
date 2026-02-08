package com.aox.system.service;

import com.aox.common.exception.BusinessException;
import com.aox.common.security.context.SecurityContextHolder;
import com.aox.system.domain.Banner;
import com.aox.system.domain.dto.BannerDTO;
import com.aox.system.mapper.BannerMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Banner服务
 *
 * @author Aox Team
 * @since 2026-02-02
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BannerService {

    private final BannerMapper bannerMapper;

    /**
     * 分页查询Banner列表
     */
    public Page<Banner> getBannerList(Integer pageNum, Integer pageSize, Integer status) {
        log.info("分页查询Banner列表: pageNum={}, pageSize={}, status={}", pageNum, pageSize, status);

        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();

        // 状态筛选
        if (status != null) {
            wrapper.eq(Banner::getStatus, status);
        }

        // 按排序字段升序，创建时间降序
        wrapper.orderByAsc(Banner::getSortOrder)
               .orderByDesc(Banner::getCreateTime);

        Page<Banner> page = new Page<>(pageNum, pageSize);
        return bannerMapper.selectPage(page, wrapper);
    }

    /**
     * 查询Banner详情
     */
    public Banner getBannerDetail(Long id) {
        log.info("查询Banner详情: id={}", id);

        Banner banner = bannerMapper.selectById(id);
        if (banner == null) {
            throw new BusinessException("Banner不存在");
        }

        return banner;
    }

    /**
     * 创建Banner
     */
    @Transactional(rollbackFor = Exception.class)
    public void createBanner(BannerDTO dto) {
        log.info("创建Banner: dto={}", dto);

        Banner banner = new Banner();
        banner.setTitle(dto.getTitle());
        banner.setImageUrl(dto.getImageUrl());
        banner.setLinkUrl(dto.getLinkUrl());
        banner.setLinkType(dto.getLinkType());
        banner.setSortOrder(dto.getSortOrder());
        banner.setStatus(dto.getStatus());
        banner.setTenantId(SecurityContextHolder.getTenantId() == null ? 0L : SecurityContextHolder.getTenantId());

        int result = bannerMapper.insert(banner);
        if (result <= 0) {
            throw new BusinessException("创建Banner失败");
        }

        log.info("Banner创建成功: id={}", banner.getId());
    }

    /**
     * 更新Banner
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateBanner(Long id, BannerDTO dto) {
        log.info("更新Banner: id={}, dto={}", id, dto);

        Banner banner = bannerMapper.selectById(id);
        if (banner == null) {
            throw new BusinessException("Banner不存在");
        }

        banner.setTitle(dto.getTitle());
        banner.setImageUrl(dto.getImageUrl());
        banner.setLinkUrl(dto.getLinkUrl());
        banner.setLinkType(dto.getLinkType());
        banner.setSortOrder(dto.getSortOrder());
        banner.setStatus(dto.getStatus());

        int result = bannerMapper.updateById(banner);
        if (result <= 0) {
            throw new BusinessException("更新Banner失败");
        }

        log.info("Banner更新成功: id={}", id);
    }

    /**
     * 删除Banner
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteBanner(Long id) {
        log.info("删除Banner: id={}", id);

        Banner banner = bannerMapper.selectById(id);
        if (banner == null) {
            throw new BusinessException("Banner不存在");
        }

        int result = bannerMapper.deleteById(id);
        if (result <= 0) {
            throw new BusinessException("删除Banner失败");
        }

        log.info("Banner删除成功: id={}", id);
    }

    /**
     * 更新Banner状态
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        log.info("更新Banner状态: id={}, status={}", id, status);

        // 验证状态值
        if (status != 0 && status != 1) {
            throw new BusinessException("状态参数错误");
        }

        Banner banner = bannerMapper.selectById(id);
        if (banner == null) {
            throw new BusinessException("Banner不存在");
        }

        banner.setStatus(status);
        int result = bannerMapper.updateById(banner);
        if (result <= 0) {
            throw new BusinessException("更新状态失败");
        }

        log.info("Banner状态更新成功: id={}, status={}", id, status);
    }

    /**
     * 批量更新排序
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateSort(List<Banner> banners) {
        log.info("批量更新排序: count={}", banners.size());

        if (banners == null || banners.isEmpty()) {
            throw new BusinessException("排序列表不能为空");
        }

        int result = bannerMapper.batchUpdateSort(banners);
        log.info("排序更新成功: 影响行数={}", result);
    }

    /**
     * 查询已上架Banner列表（小程序使用）
     */
    public List<Banner> getPublishedBanners() {
        log.info("查询已上架Banner列表");

        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Banner::getStatus, 1); // 仅查询已上架
        wrapper.orderByAsc(Banner::getSortOrder); // 按排序升序

        return bannerMapper.selectList(wrapper);
    }
}
