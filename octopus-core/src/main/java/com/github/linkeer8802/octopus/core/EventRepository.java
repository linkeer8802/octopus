package com.github.linkeer8802.octopus.core;

/**
 * 领域事件的外键仓储接口
 * @author weird
 */
public interface EventRepository {
    int EVENT_UNPUBLISHED = 0;
    int EVENT_PUBLISHED = 1;

    /**
     * 保存领域事件到数据库中
     * @param event 待保存的领域事件
     */
    void save(DomainEvent event);
}
