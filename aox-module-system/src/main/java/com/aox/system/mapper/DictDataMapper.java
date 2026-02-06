package com.aox.system.mapper;

import com.aox.system.domain.SysDictData;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 字典数据 Mapper
 *
 * @author Aox Team
 */
@Mapper
public interface DictDataMapper extends BaseMapper<SysDictData> {

    /**
     * 根据字典类型查询字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据列表
     */
    List<SysDictData> selectByDictType(@Param("dictType") String dictType);
}
