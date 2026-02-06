package com.aox.system.service;

import com.aox.system.domain.SysDictType;
import com.aox.system.domain.request.DictTypeQueryRequest;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 字典类型服务接口
 *
 * @author Aox Team
 */
public interface DictTypeService {

    /**
     * 分页查询字典类型
     */
    IPage<SysDictType> getDictTypeList(DictTypeQueryRequest request);

    /**
     * 获取所有字典类型
     */
    List<SysDictType> getAllDictTypes();

    /**
     * 根据ID查询字典类型
     */
    SysDictType getDictTypeById(Long dictId);

    /**
     * 根据字典类型查询
     */
    SysDictType getDictTypeByType(String dictType);

    /**
     * 创建字典类型
     */
    void createDictType(SysDictType dictType);

    /**
     * 更新字典类型
     */
    void updateDictType(SysDictType dictType);

    /**
     * 删除字典类型
     */
    void deleteDictType(Long dictId);

    /**
     * 批量删除字典类型
     */
    void batchDeleteDictTypes(List<Long> dictIds);
}
