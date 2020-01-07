package com.github.linkeer8802.octopus.spring.cache;

import com.github.linkeer8802.octopus.core.AggregateRoot;
import com.github.linkeer8802.octopus.core.cache.AggregateCacheManager;
import com.github.linkeer8802.octopus.core.cache.Cache;
import com.github.linkeer8802.octopus.spring.listener.CallbackContext;
import com.github.linkeer8802.octopus.spring.listener.DomainServiceTransactionListener;

import java.io.Serializable;

/**
 * 聚合根缓存监听器，刷新事务中的聚合根到外部缓存中。
 * @author weird
 */
public class AggregateRootCacheListener implements DomainServiceTransactionListener {

    private Cache<Object, AggregateRoot<? extends Serializable>> cache;

    public AggregateRootCacheListener(Cache<Object, AggregateRoot<? extends Serializable>> cache) {
        this.cache = cache;
    }

    @Override
    public void onActive(CallbackContext context) {
        AggregateCacheManager.init(cache);
    }

    @Override
    public void onAfterCommit(CallbackContext context) {
        AggregateCacheManager.flush();
    }

    @Override
    public void onAfterCompletion(CallbackContext context) {
        AggregateCacheManager.clear();
    }
}
