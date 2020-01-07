package com.github.linkeer8802.octopus.core;

import java.io.Serializable;

/**
 * 聚合根仓储Repository接口
 * @author wrd
 * @param <T> 聚合根类型
 * @see EventSourcingAggregateRoot
 */
public interface AggregateRootRepository<T extends EventSourcingAggregateRoot<T, ? extends Serializable>> {
    /**
     * 从仓储中加载聚合根
     * @param id 聚合根ID
     * @param clz 聚合根类型
     * @param <ID> 聚合根ID的类型
     * @return 聚合根容器对象
     */
    <ID extends Serializable> AggregateRootContainer<T> load(ID id, Class<T> clz);
}
