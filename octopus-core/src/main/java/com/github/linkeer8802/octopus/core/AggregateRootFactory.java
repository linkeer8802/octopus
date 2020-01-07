package com.github.linkeer8802.octopus.core;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 聚合根工厂接口
 * @author wrd
 * @param <T> 聚合根类型
 * @param <M> 传递给工厂创建聚合根的入参数据模型类型
 * @see AggregateRootRepository
 */
public interface AggregateRootFactory<T extends EventSourcingAggregateRoot<T, ? extends Serializable>, M> extends AggregateRootRepository<T> {
    /**
     * 从模型对象中创建聚合根
     * @param model 数据模型对象
     * @param clz 聚合根类型
     * @return 创建的聚合根，新创建的聚合根对象版本号为0。
     * @see EventSourcingAggregateRoot#INIT_VERSION
     */
    T create(M model, Class<T> clz);

    /**
     * 聚合根的事件订阅对象列表
     * @return 所有已订阅聚合根的事件对象列表
     */
    default List<Object> getEventSubscribers() {
        return Collections.emptyList();
    }
}
