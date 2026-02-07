package com.aox.miniapp.service;

import com.aox.common.exception.BusinessException;
import com.aox.common.security.context.SecurityContextHolder;
import com.aox.miniapp.domain.entity.BizUserFavorite;
import com.aox.miniapp.domain.vo.MiniappFavoriteVO;
import com.aox.miniapp.mapper.BizUserFavoriteMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

/**
 * 小程序收藏服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MiniappFavoriteService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final BizUserFavoriteMapper favoriteMapper;

    /**
     * 分页查询收藏列表
     */
    public Page<MiniappFavoriteVO> getFavoritePage(Integer pageNum, Integer pageSize) {
        Long userId = getCurrentUserId();

        LambdaQueryWrapper<BizUserFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BizUserFavorite::getUserId, userId)
                .orderByDesc(BizUserFavorite::getFavoriteTime)
                .orderByDesc(BizUserFavorite::getCreateTime);

        Page<BizUserFavorite> page = new Page<>(pageNum, pageSize);
        Page<BizUserFavorite> result = favoriteMapper.selectPage(page, wrapper);

        Page<MiniappFavoriteVO> voPage = new Page<>();
        voPage.setCurrent(result.getCurrent());
        voPage.setSize(result.getSize());
        voPage.setTotal(result.getTotal());
        voPage.setPages(result.getPages());
        voPage.setRecords(result.getRecords().stream().map(this::toFavoriteVO).collect(Collectors.toList()));

        return voPage;
    }

    /**
     * 取消收藏
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeFavorite(Long favoriteId) {
        Long userId = getCurrentUserId();

        BizUserFavorite favorite = favoriteMapper.selectById(favoriteId);
        if (favorite == null) {
            throw new BusinessException("收藏记录不存在");
        }
        if (!userId.equals(favorite.getUserId())) {
            throw new BusinessException("无权删除该收藏");
        }

        int rows = favoriteMapper.deleteById(favoriteId);
        if (rows <= 0) {
            throw new BusinessException("取消收藏失败");
        }
    }

    private MiniappFavoriteVO toFavoriteVO(BizUserFavorite favorite) {
        LocalDateTime favoriteTime = favorite.getFavoriteTime() != null ? favorite.getFavoriteTime() : favorite.getCreateTime();

        return MiniappFavoriteVO.builder()
                .id(favorite.getFavoriteId())
                .title(favorite.getFavoriteTitle())
                .desc(favorite.getFavoriteDesc())
                .time(favoriteTime == null ? "" : favoriteTime.format(DATE_FORMATTER))
                .image(favorite.getImageUrl())
                .build();
    }

    private Long getCurrentUserId() {
        Long userId = SecurityContextHolder.getUserId();
        if (userId == null) {
            log.warn("获取收藏失败，用户未登录");
            throw new BusinessException(401, "未登录或token已过期");
        }
        return userId;
    }
}
