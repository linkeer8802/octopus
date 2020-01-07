package com.github.linkeer8802.octopus.spring.cache;

import com.github.linkeer8802.octopus.core.AggregateRoot;
import com.github.linkeer8802.octopus.core.cache.Cache;
import com.github.linkeer8802.octopus.core.serializer.AggregateRootSerializer;
import com.github.linkeer8802.octopus.core.serializer.Serializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * 基于Redis实现的聚合根缓存实现
 */
public class RedisAggregateRootCacheImpl implements Cache<Serializable, AggregateRoot> {

    @Value("${dddframework.aggregateRoot.cache.expire:1800}")
    private Long expire;
    private AggregateRootSerializer serializer;
    private RedisTemplate<String, String> redisTemplate;

    public RedisAggregateRootCacheImpl(AggregateRootSerializer serializer, RedisTemplate<String, String> redisTemplate) {
        this.serializer = serializer;
        this.redisTemplate = redisTemplate;
        this.redisTemplate.setKeySerializer(RedisSerializer.string());
        this.redisTemplate.setHashKeySerializer(RedisSerializer.string());
        this.redisTemplate.setValueSerializer(RedisSerializer.string());
        this.redisTemplate.setHashValueSerializer(RedisSerializer.string());
    }

    @Override
    public AggregateRoot get(Serializable key) {
        String json = redisTemplate.opsForValue().get(key.toString());
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        return serializer.deserialize(json.getBytes(Serializer.CHARSET_UTF8));
    }

    @Override
    public void put(Serializable key, AggregateRoot value) {
        byte[] bytes = serializer.serialize(value);
        String json = new String(bytes, Serializer.CHARSET_UTF8);
        redisTemplate.opsForValue().set(key.toString(), json, expire, TimeUnit.SECONDS);
    }

    @Override
    public void clear() {}
}
