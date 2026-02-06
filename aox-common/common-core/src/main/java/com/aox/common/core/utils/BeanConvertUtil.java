package com.aox.common.core.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Bean 转换工具类
 * 用于实体类（Entity）和视图对象（VO）之间的转换
 *
 * @author Aox Team
 */
public class BeanConvertUtil {

    private BeanConvertUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * 单个对象转换
     *
     * @param source 源对象
     * @param target 目标类型
     * @param <S>    源对象类型
     * @param <T>    目标对象类型
     * @return 转换后的对象
     */
    public static <S, T> T convert(S source, Class<T> target) {
        if (source == null) {
            return null;
        }
        return BeanUtil.copyProperties(source, target);
    }

    /**
     * 单个对象转换（使用 Supplier 创建目标对象）
     *
     * @param source   源对象
     * @param supplier 目标对象提供者
     * @param <S>      源对象类型
     * @param <T>      目标对象类型
     * @return 转换后的对象
     */
    public static <S, T> T convert(S source, Supplier<T> supplier) {
        if (source == null) {
            return null;
        }
        T target = supplier.get();
        BeanUtil.copyProperties(source, target);
        return target;
    }

    /**
     * 集合转换
     *
     * @param sourceList 源对象集合
     * @param target     目标类型
     * @param <S>        源对象类型
     * @param <T>        目标对象类型
     * @return 转换后的对象集合
     */
    public static <S, T> List<T> convertList(List<S> sourceList, Class<T> target) {
        if (CollUtil.isEmpty(sourceList)) {
            return Collections.emptyList();
        }
        return sourceList.stream()
                .map(source -> convert(source, target))
                .collect(Collectors.toList());
    }

    /**
     * 集合转换（使用 Supplier 创建目标对象）
     *
     * @param sourceList 源对象集合
     * @param supplier   目标对象提供者
     * @param <S>        源对象类型
     * @param <T>        目标对象类型
     * @return 转换后的对象集合
     */
    public static <S, T> List<T> convertList(List<S> sourceList, Supplier<T> supplier) {
        if (CollUtil.isEmpty(sourceList)) {
            return Collections.emptyList();
        }
        return sourceList.stream()
                .map(source -> convert(source, supplier))
                .collect(Collectors.toList());
    }

    /**
     * 分页结果转换
     *
     * @param sourceList 源对象集合
     * @param target     目标类型
     * @param total      总记录数
     * @param pageNum    当前页码
     * @param pageSize   每页大小
     * @param <S>        源对象类型
     * @param <T>        目标对象类型
     * @return 转换后的分页结果
     */
    public static <S, T> com.aox.common.core.domain.PageResult<T> convertPage(
            List<S> sourceList, Class<T> target, Long total, Integer pageNum, Integer pageSize) {
        List<T> targetList = convertList(sourceList, target);
        return com.aox.common.core.domain.PageResult.of(total, targetList, pageNum, pageSize);
    }
}
