package com.aox.system.service.impl;

import com.aox.common.exception.BusinessException;
import com.aox.system.domain.SysDictType;
import com.aox.system.domain.request.DictTypeQueryRequest;
import com.aox.system.mapper.DictTypeMapper;
import com.aox.system.service.DictTypeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 字典类型服务实现
 *
 * @author Aox Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DictTypeServiceImpl implements DictTypeService {

    private final DictTypeMapper dictTypeMapper;

    @Override
    public IPage<SysDictType> getDictTypeList(DictTypeQueryRequest request) {
        Page<SysDictType> page = new Page<>(request.getCurrent(), request.getSize());
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();

        wrapper.like(StringUtils.hasText(request.getDictName()), SysDictType::getDictName, request.getDictName())
                .like(StringUtils.hasText(request.getDictType()), SysDictType::getDictType, request.getDictType())
                .eq(SysDictType::getDeleted, 0)
                .orderByAsc(SysDictType::getDictId);

        return dictTypeMapper.selectPage(page, wrapper);
    }

    @Override
    public List<SysDictType> getAllDictTypes() {
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictType::getStatus, 0)
                .eq(SysDictType::getDeleted, 0)
                .orderByAsc(SysDictType::getDictId);
        return dictTypeMapper.selectList(wrapper);
    }

    @Override
    public SysDictType getDictTypeById(Long dictId) {
        return dictTypeMapper.selectById(dictId);
    }

    @Override
    public SysDictType getDictTypeByType(String dictType) {
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictType::getDictType, dictType)
                .eq(SysDictType::getDeleted, 0);
        return dictTypeMapper.selectOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createDictType(SysDictType dictType) {
        // 检查字典类型是否已存在
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictType::getDictType, dictType.getDictType())
                .eq(SysDictType::getDeleted, 0);
        SysDictType existDictType = dictTypeMapper.selectOne(wrapper);

        if (existDictType != null) {
            throw new BusinessException("字典类型已存在: " + dictType.getDictType());
        }

        dictTypeMapper.insert(dictType);
        log.info("创建字典类型成功: {}", dictType.getDictType());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDictType(SysDictType dictType) {
        dictTypeMapper.updateById(dictType);
        log.info("更新字典类型成功: {}", dictType.getDictId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictType(Long dictId) {
        SysDictType dictType = new SysDictType();
        dictType.setDictId(dictId);
        dictType.setDeleted(1);
        dictTypeMapper.updateById(dictType);
        log.info("删除字典类型成功: {}", dictId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteDictTypes(List<Long> dictIds) {
        dictIds.forEach(this::deleteDictType);
    }
}
