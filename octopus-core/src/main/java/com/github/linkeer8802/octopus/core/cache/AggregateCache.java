package com.github.linkeer8802.octopus.core.cache;

import com.github.linkeer8802.octopus.core.AggregateRoot;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 聚合根缓存实现
 * @author wrd
 */
@Slf4j
public class AggregateCache implements Cache<Object, AggregateRoot<? extends Serializable>> {

    private Cache<Object, AggregateRoot<? extends Serializable>> externalCache;
    private Map<Object, AggregateRoot<? extends Serializable>> aggregateRoots;

    AggregateCache(Cache<Object, AggregateRoot<? extends Serializable>> externalCache) {
        this.externalCache = externalCache;
        this.aggregateRoots = new HashMap<>(0);
    }

    @Override
    public AggregateRoot<? extends Serializable> get(Object key) {
        AggregateRoot<? extends Serializable> aggregateRoot = aggregateRoots.get(key);
        if (aggregateRoot == null) {
            aggregateRoot = externalCache.get(key);
        }
        return aggregateRoot;
    }

    @Override
    public void put(Object key, AggregateRoot<? extends Serializable> value) {
        aggregateRoots.put(key, value);
    }

    void flush() {
        if (aggregateRoots != null ) {
            aggregateRoots.forEach(externalCache::put);
            if (!aggregateRoots.isEmpty() && log.isDebugEnabled()) {
                log.debug("Flush {} aggregateRoots to external cache.", aggregateRoots.size());
            }
        }
    }

    @Override
    public void clear() {
        if (aggregateRoots != null ) {
            aggregateRoots.clear();
            if (log.isDebugEnabled()) {
                log.debug("Clear aggregateRoot cache.");
            }
        }
    }
}
