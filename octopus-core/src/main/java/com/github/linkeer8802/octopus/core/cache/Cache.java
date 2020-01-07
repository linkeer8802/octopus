package com.github.linkeer8802.octopus.core.cache;

/**
 * 缓存抽象接口
 * @author wrd
 * @param <K> key类型
 * @param <V> value类型
 */
public interface Cache<K, V> {
    /**
     * 从缓存中获取指定key的value
     * @param key key
     * @return value
     */
    V get(K key);

    /**
     * 设置指定key的对象到缓存中
     * @param key key
     * @param value value
     */
    void put(K key, V value);

    /**
     * 清除ThreadLocal中的对象
     */
    void clear();
}
