package com.github.linkeer8802.octopus.core;

import com.github.linkeer8802.octopus.core.cache.AggregateCacheManager;
import com.github.linkeer8802.octopus.core.eventbus.OnEvent;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;

/**
 * 支持缓存聚合根的AggregateRootFactory装饰器
 * @author weird
 * @param <T> 聚合根类型
 * @param <U> 传递给工厂创建聚合根的入参数据模型类型
 * @see AggregateRootFactory
 * @see AggregateRootRepository
 */
@Slf4j
public class CacheSupportFactoryDecorator<T extends EventSourcingAggregateRoot<T, ? extends Serializable>, U>
                                            implements AggregateRootFactory<T, U>, AggregateRootRepository<T> {

    private AggregateRootFactory<T, U> delegate;

    public CacheSupportFactoryDecorator(AggregateRootFactory<T, U> aggregateRootFactory) {
        this.delegate = aggregateRootFactory;
        //把自身添加到事件订阅器中
        getEventSubscribers().add(this);
    }

    @Override
    public T create(U entity, Class<T> clz) {
        return delegate.create(entity, clz);
    }

    @Override
    public <ID extends Serializable> AggregateRootContainer<T> load(ID id, Class<T> clz) {
        T aggregateRoot = loadFromCache(id, clz);
        if (aggregateRoot != null) {
            if (log.isDebugEnabled()) {
                log.debug("从缓存中加载聚合根[{}]:{}.", aggregateRoot.getClass().getName(), aggregateRoot);
            }
            aggregateRoot.register(getEventSubscribers().toArray());
            return new AggregateRootContainer<>(aggregateRoot);
        } else {
            return delegate.load(id, clz);
        }
    }

    @Override
    public List<Object> getEventSubscribers() {
        return delegate.getEventSubscribers();
    }

    private <ID extends Serializable> T loadFromCache(ID id, Class<T> clz) {
        T aggregateRoot = null;
        if (AggregateCacheManager.initialized()) {
            aggregateRoot = (T) AggregateCacheManager.get(id, clz.getSimpleName());
        }
        return aggregateRoot;
    }

    private void putAggregateRootToCache(AggregateRoot<? extends Serializable> aggregateRoot) {
        if (AggregateCacheManager.initialized()) {
            AggregateCacheManager.put(aggregateRoot);
            if (log.isDebugEnabled()) {
                log.debug("设置聚合根到缓存中[{}]:{}", aggregateRoot.getClass().getName(), aggregateRoot.toString());
            }
        }
    }

    @OnEvent
    private void onDomainEvent(DomainEvent event) {
        if (event instanceof DomainExceptionEvent) {
            return;
        }
        if (event.getTarget() instanceof EventSourcingAggregateRoot) {
            putAggregateRootToCache(event.getTarget());
        }
    }
}
