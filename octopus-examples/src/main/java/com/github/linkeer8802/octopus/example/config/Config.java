package com.github.linkeer8802.octopus.example.config;

import com.github.linkeer8802.octopus.core.cache.Cache;
import com.github.linkeer8802.octopus.core.serializer.AggregateRootSerializer;
import com.github.linkeer8802.octopus.core.serializer.Jackson2JsonSerializer;
import com.github.linkeer8802.octopus.core.serializer.Serializer;
import com.github.linkeer8802.octopus.spring.cache.AggregateRootCacheListener;
import com.github.linkeer8802.octopus.spring.cache.RedisAggregateRootCacheImpl;
import com.github.linkeer8802.octopus.spring.factory.AggregateRootFactoryBeanPostProcessor;
import com.github.linkeer8802.octopus.spring.listener.CallRetryListener;
import com.github.linkeer8802.octopus.spring.listener.DomainServiceTransactionListener;
import com.github.linkeer8802.octopus.spring.service.DomainServiceTransactionInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.zalando.jackson.datatype.money.MoneyModule;

import java.util.List;

/**
 * @author weird
 * @date 2019/12/5
 */
@Configuration
public class Config {

    @Bean
    public Cache redisAggregateRootCache(RedisTemplate redisTemplate) {
        return new RedisAggregateRootCacheImpl(new AggregateRootSerializer(jackson2JsonSerializer()), redisTemplate);
    }

    @Bean
    public Serializer<Object> jackson2JsonSerializer() {
        return Jackson2JsonSerializer.builder()
                    .mapperCustomize(objectMapper -> {
                        //jackson-datatype-jdk8 support
                        objectMapper.findAndRegisterModules();
                        //Money
                        objectMapper.registerModule(new MoneyModule().withDefaultFormatting());
                    }).build();
    }

    @Bean
    public DomainServiceTransactionListener aggregateRootCacheListener(Cache cache) {
        return new AggregateRootCacheListener(cache);
    }

    @Bean
    public DomainServiceTransactionInterceptor domainServiceTransactionAspect(List<DomainServiceTransactionListener> listeners) {
        return new DomainServiceTransactionInterceptor(listeners);
    }

    @Bean
    public AggregateRootFactoryBeanPostProcessor aggregateRootFactoryBeanPostProcessor() {
        return new AggregateRootFactoryBeanPostProcessor();
    }

    @Bean
    public CallRetryListener callRetryListener() {
        return new CallRetryListener();
    }

    /*******************************message*****************************/
/*
    @Bean
    public EventRepository jdbcEventRepository(JdbcTemplate jdbcTemplate) {
        return new JDBCEventRepository(jackson2JsonSerializer(), jdbcTemplate);
    }

    @Bean
    public MessageProducer redisMessageProducer(RedisTemplate redisTemplate) {
        return new RedisMessageProducer(redisTemplate, jackson2JsonSerializer());
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory) {
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
        return redisMessageListenerContainer;
    }

    @Bean
    public MessageConsumer redisMessageConsumer(RedisMessageListenerContainer redisMessageListenerContainer) {
        return new RedisMessageConsumer(redisMessageListenerContainer, jackson2JsonSerializer());
    }

    @Bean
    public DomainEventPublishListener domainEventPublishListener(
            MessageProducer messageProducer, EventRepository eventRepository) {
        return new DomainEventPublishListener(messageProducer, eventRepository);
    }

    @Bean
    public MessageSubscriber messageSubscriber(MessageConsumer messageConsumer) {
        return new MessageSubscriberImpl(messageConsumer);
    }*/
}
