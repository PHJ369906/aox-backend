package com.aox.miniapp.controller;

import com.aox.common.core.domain.R;
import com.aox.miniapp.domain.dto.CreateAddressDTO;
import com.aox.miniapp.domain.vo.MiniappAddressDetailVO;
import com.aox.miniapp.domain.vo.MiniappAddressVO;
import com.aox.miniapp.service.MiniappAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 小程序地址控制器
 */
@Tag(name = "小程序-地址", description = "小程序地址管理接口")
@RestController
@RequestMapping("/api/v1/miniapp/addresses")
@RequiredArgsConstructor
public class MiniappAddressController {

    private final MiniappAddressService addressService;

    @Operation(summary = "查询地址列表", description = "查询当前用户的地址列表")
    @GetMapping
    public R<List<MiniappAddressVO>> getAddressList() {
        return R.ok(addressService.getAddressList());
    }

    @Operation(summary = "查询地址详情", description = "根据地址ID查询地址详情")
    @GetMapping("/{addressId}")
    public R<MiniappAddressDetailVO> getAddressDetail(@Parameter(description = "地址ID") @PathVariable Long addressId) {
        return R.ok(addressService.getAddressDetail(addressId));
    }

    @Operation(summary = "新增地址", description = "新增用户收货地址")
    @PostMapping
    public R<Void> createAddress(@Valid @RequestBody CreateAddressDTO dto) {
        addressService.createAddress(dto);
        return R.ok();
    }

    @Operation(summary = "更新地址", description = "根据地址ID更新地址信息")
    @PutMapping("/{addressId}")
    public R<Void> updateAddress(
            @Parameter(description = "地址ID") @PathVariable Long addressId,
            @Valid @RequestBody CreateAddressDTO dto
    ) {
        addressService.updateAddress(addressId, dto);
        return R.ok();
    }

    @Operation(summary = "设为默认地址", description = "将指定地址设为默认地址")
    @PutMapping("/{addressId}/default")
    public R<Void> setDefaultAddress(@Parameter(description = "地址ID") @PathVariable Long addressId) {
        addressService.setDefaultAddress(addressId);
        return R.ok();
    }

    @Operation(summary = "删除地址", description = "删除指定地址")
    @DeleteMapping("/{addressId}")
    public R<Void> removeAddress(@Parameter(description = "地址ID") @PathVariable Long addressId) {
        addressService.removeAddress(addressId);
        return R.ok();
    }
}
