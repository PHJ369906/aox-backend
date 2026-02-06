package com.aox.system.service.impl;

import com.aox.system.domain.SysDictData;
import com.aox.system.domain.request.DictDataQueryRequest;
import com.aox.system.mapper.DictDataMapper;
import com.aox.system.service.DictDataService;
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
 * 字典数据服务实现
 *
 * @author Aox Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DictDataServiceImpl implements DictDataService {

    private final DictDataMapper dictDataMapper;

    @Override
    public IPage<SysDictData> getDictDataList(DictDataQueryRequest request) {
        Page<SysDictData> page = new Page<>(request.getCurrent(), request.getSize());
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(StringUtils.hasText(request.getDictType()), SysDictData::getDictType, request.getDictType())
                .like(StringUtils.hasText(request.getDictLabel()), SysDictData::getDictLabel, request.getDictLabel())
                .eq(SysDictData::getDeleted, 0)
                .orderByAsc(SysDictData::getDictSort);

        return dictDataMapper.selectPage(page, wrapper);
    }

    @Override
    public List<SysDictData> getDictDataByType(String dictType) {
        return dictDataMapper.selectByDictType(dictType);
    }

    @Override
    public SysDictData getDictDataById(Long dictCode) {
        return dictDataMapper.selectById(dictCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createDictData(SysDictData dictData) {
        dictDataMapper.insert(dictData);
        log.info("创建字典数据成功: {} - {}", dictData.getDictType(), dictData.getDictLabel());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDictData(SysDictData dictData) {
        dictDataMapper.updateById(dictData);
        log.info("更新字典数据成功: {}", dictData.getDictCode());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictData(Long dictCode) {
        SysDictData dictData = new SysDictData();
        dictData.setDictCode(dictCode);
        dictData.setDeleted(1);
        dictDataMapper.updateById(dictData);
        log.info("删除字典数据成功: {}", dictCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteDictData(List<Long> dictCodes) {
        dictCodes.forEach(this::deleteDictData);
    }
}
