package com.github.linkeer8802.octopus.core.cache;

import com.github.linkeer8802.octopus.core.AggregateRoot;

import java.io.Serializable;

/**
 * 聚合根缓存管理器
 * @author wrd
 */
public final class AggregateCacheManager {

    private static final String AGGREGATE_CACHE_PREFIX = "AggregateRoots:";

    private static final ThreadLocal<AggregateCache> AGGREGATE_ROOT_CACHE_HOLDER = new ThreadLocal<>();

    public static Boolean initialized() {
        return AGGREGATE_ROOT_CACHE_HOLDER.get() != null;
    }

    public static void init(Cache<Object, AggregateRoot<? extends Serializable>> externalCache) {
        AGGREGATE_ROOT_CACHE_HOLDER.set(new AggregateCache(externalCache));
    }

    public static AggregateRoot<? extends Serializable> get(Object aggregateRootId, String aggregateRootName) {
        return AGGREGATE_ROOT_CACHE_HOLDER.get().get(getCacheKey(aggregateRootId, aggregateRootName));
    }

    public static void put(AggregateRoot<? extends Serializable> aggregateRoot) {
        AGGREGATE_ROOT_CACHE_HOLDER.get().put(getCacheKey(aggregateRoot.getId(), aggregateRoot.getClass().getSimpleName()), aggregateRoot);
    }

    public static void flush() {
        if (AGGREGATE_ROOT_CACHE_HOLDER.get() != null) {
            AGGREGATE_ROOT_CACHE_HOLDER.get().flush();
        }

    }
    
    public static void clear() {
        if (AGGREGATE_ROOT_CACHE_HOLDER.get() != null) {
            AGGREGATE_ROOT_CACHE_HOLDER.get().clear();
        }
        AGGREGATE_ROOT_CACHE_HOLDER.remove();
    }

    private static String getCacheKey(Object aggregateRootId, String aggregateRootName) {
        return AGGREGATE_CACHE_PREFIX + aggregateRootName + ":" + aggregateRootId;
    }
}
