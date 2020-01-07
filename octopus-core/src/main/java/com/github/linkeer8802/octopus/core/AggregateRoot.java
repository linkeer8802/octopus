package com.github.linkeer8802.octopus.core;

import java.io.Serializable;

/**
 * 聚合根接口
 * @author wrd
 * @param <ID> 聚合根ID类型
 */
public interface AggregateRoot<ID extends Serializable> {
    /**
     * 获取聚合根的ID
     * @return 聚合根的ID
     */
    ID getId();
}
