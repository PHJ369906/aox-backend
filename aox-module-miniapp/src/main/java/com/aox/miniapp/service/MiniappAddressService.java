package com.aox.miniapp.service;

import com.aox.common.exception.BusinessException;
import com.aox.common.security.context.SecurityContextHolder;
import com.aox.miniapp.domain.dto.CreateAddressDTO;
import com.aox.miniapp.domain.entity.BizUserAddress;
import com.aox.miniapp.domain.vo.MiniappAddressDetailVO;
import com.aox.miniapp.domain.vo.MiniappAddressVO;
import com.aox.miniapp.mapper.BizUserAddressMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 小程序地址服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MiniappAddressService {

    private final BizUserAddressMapper addressMapper;

    /**
     * 查询地址列表
     */
    public List<MiniappAddressVO> getAddressList() {
        Long userId = getCurrentUserId();

        LambdaQueryWrapper<BizUserAddress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BizUserAddress::getUserId, userId)
                .orderByDesc(BizUserAddress::getIsDefault)
                .orderByDesc(BizUserAddress::getUpdateTime);

        List<BizUserAddress> list = addressMapper.selectList(wrapper);
        return list.stream().map(this::toAddressVO).collect(Collectors.toList());
    }

    /**
     * 查询地址详情
     */
    public MiniappAddressDetailVO getAddressDetail(Long addressId) {
        Long userId = getCurrentUserId();

        BizUserAddress address = addressMapper.selectById(addressId);
        if (address == null) {
            throw new BusinessException("地址不存在");
        }
        if (!userId.equals(address.getUserId())) {
            throw new BusinessException("无权查看该地址");
        }

        return MiniappAddressDetailVO.builder()
                .id(address.getAddressId())
                .receiverName(address.getReceiverName())
                .receiverPhone(address.getReceiverPhone())
                .province(address.getProvince())
                .city(address.getCity())
                .district(address.getDistrict())
                .detailAddress(address.getDetailAddress())
                .isDefault(Integer.valueOf(1).equals(address.getIsDefault()))
                .build();
    }

    /**
     * 新增地址
     */
    @Transactional(rollbackFor = Exception.class)
    public void createAddress(CreateAddressDTO dto) {
        Long userId = getCurrentUserId();

        long count = addressMapper.selectCount(
                new LambdaQueryWrapper<BizUserAddress>().eq(BizUserAddress::getUserId, userId)
        );

        boolean setDefault = Boolean.TRUE.equals(dto.getIsDefault()) || count == 0;
        if (setDefault) {
            clearDefault(userId);
        }

        BizUserAddress address = new BizUserAddress();
        address.setUserId(userId);
        address.setReceiverName(dto.getReceiverName());
        address.setReceiverPhone(dto.getReceiverPhone());
        address.setProvince(dto.getProvince());
        address.setCity(dto.getCity());
        address.setDistrict(dto.getDistrict());
        address.setDetailAddress(dto.getDetailAddress());
        address.setIsDefault(setDefault ? 1 : 0);

        int rows = addressMapper.insert(address);
        if (rows <= 0) {
            throw new BusinessException("新增地址失败");
        }
    }

    /**
     * 更新地址
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateAddress(Long addressId, CreateAddressDTO dto) {
        Long userId = getCurrentUserId();

        BizUserAddress address = addressMapper.selectById(addressId);
        if (address == null) {
            throw new BusinessException("地址不存在");
        }
        if (!userId.equals(address.getUserId())) {
            throw new BusinessException("无权操作该地址");
        }

        boolean setDefault = Boolean.TRUE.equals(dto.getIsDefault());
        if (setDefault) {
            clearDefault(userId);
        }

        BizUserAddress update = new BizUserAddress();
        update.setAddressId(addressId);
        update.setReceiverName(dto.getReceiverName());
        update.setReceiverPhone(dto.getReceiverPhone());
        update.setProvince(dto.getProvince());
        update.setCity(dto.getCity());
        update.setDistrict(dto.getDistrict());
        update.setDetailAddress(dto.getDetailAddress());
        update.setIsDefault(setDefault ? 1 : 0);

        int rows = addressMapper.updateById(update);
        if (rows <= 0) {
            throw new BusinessException("更新地址失败");
        }

        if (!setDefault) {
            ensureHasDefault(userId);
        }
    }

    /**
     * 设置默认地址
     */
    @Transactional(rollbackFor = Exception.class)
    public void setDefaultAddress(Long addressId) {
        Long userId = getCurrentUserId();

        BizUserAddress address = addressMapper.selectById(addressId);
        if (address == null) {
            throw new BusinessException("地址不存在");
        }
        if (!userId.equals(address.getUserId())) {
            throw new BusinessException("无权操作该地址");
        }

        clearDefault(userId);

        BizUserAddress update = new BizUserAddress();
        update.setAddressId(addressId);
        update.setIsDefault(1);
        addressMapper.updateById(update);
    }

    /**
     * 删除地址
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeAddress(Long addressId) {
        Long userId = getCurrentUserId();

        BizUserAddress address = addressMapper.selectById(addressId);
        if (address == null) {
            throw new BusinessException("地址不存在");
        }
        if (!userId.equals(address.getUserId())) {
            throw new BusinessException("无权删除该地址");
        }

        boolean wasDefault = Integer.valueOf(1).equals(address.getIsDefault());
        int rows = addressMapper.deleteById(addressId);
        if (rows <= 0) {
            throw new BusinessException("删除地址失败");
        }

        if (wasDefault) {
            ensureHasDefault(userId);
        }
    }

    private void clearDefault(Long userId) {
        LambdaUpdateWrapper<BizUserAddress> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(BizUserAddress::getUserId, userId)
                .set(BizUserAddress::getIsDefault, 0);
        addressMapper.update(null, updateWrapper);
    }

    private void ensureHasDefault(Long userId) {
        long defaultCount = addressMapper.selectCount(
                new LambdaQueryWrapper<BizUserAddress>()
                        .eq(BizUserAddress::getUserId, userId)
                        .eq(BizUserAddress::getIsDefault, 1)
        );
        if (defaultCount > 0) {
            return;
        }

        LambdaQueryWrapper<BizUserAddress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BizUserAddress::getUserId, userId)
                .orderByDesc(BizUserAddress::getUpdateTime)
                .last("LIMIT 1");
        BizUserAddress first = addressMapper.selectOne(wrapper);
        if (first != null) {
            BizUserAddress update = new BizUserAddress();
            update.setAddressId(first.getAddressId());
            update.setIsDefault(1);
            addressMapper.updateById(update);
        }
    }

    private MiniappAddressVO toAddressVO(BizUserAddress address) {
        String region = Arrays.asList(address.getProvince(), address.getCity(), address.getDistrict()).stream()
                .filter(item -> item != null && !item.isBlank())
                .collect(Collectors.joining(" "));

        return MiniappAddressVO.builder()
                .id(address.getAddressId())
                .name(address.getReceiverName())
                .phone(address.getReceiverPhone())
                .region(region)
                .detail(address.getDetailAddress())
                .isDefault(Integer.valueOf(1).equals(address.getIsDefault()))
                .build();
    }

    private Long getCurrentUserId() {
        Long userId = SecurityContextHolder.getUserId();
        if (userId == null) {
            log.warn("获取地址失败，用户未登录");
            throw new BusinessException(401, "未登录或token已过期");
        }
        return userId;
    }
}
