package com.aox.system.service;

import com.aox.system.domain.SysDictData;
import com.aox.system.domain.request.DictDataQueryRequest;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 字典数据服务接口
 *
 * @author Aox Team
 */
public interface DictDataService {

    /**
     * 分页查询字典数据
     */
    IPage<SysDictData> getDictDataList(DictDataQueryRequest request);

    /**
     * 根据字典类型查询字典数据
     */
    List<SysDictData> getDictDataByType(String dictType);

    /**
     * 根据ID查询字典数据
     */
    SysDictData getDictDataById(Long dictCode);

    /**
     * 创建字典数据
     */
    void createDictData(SysDictData dictData);

    /**
     * 更新字典数据
     */
    void updateDictData(SysDictData dictData);

    /**
     * 删除字典数据
     */
    void deleteDictData(Long dictCode);

    /**
     * 批量删除字典数据
     */
    void batchDeleteDictData(List<Long> dictCodes);
}
