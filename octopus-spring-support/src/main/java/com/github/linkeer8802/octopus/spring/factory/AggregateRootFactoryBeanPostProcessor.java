package com.github.linkeer8802.octopus.spring.factory;

import com.github.linkeer8802.octopus.core.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.env.Environment;

/**
 * 聚合根工厂BeanPostProcessor，根据配置选择增强聚合根工厂的相应功能
 * @author weird
 * @see CacheSupportFactoryDecorator
 * @see PersistentEventSupportFactoryDecorator
 */
@Slf4j
public class AggregateRootFactoryBeanPostProcessor implements BeanPostProcessor, BeanFactoryAware {

    private static final String EVENT_PERSISTENT_ENABLED = "octopus.event.persistent.enabled";
    private static final String AGGREGATE_ROOT_CACHE_ENABLED = "octopus.aggregateroot.cache.enabled";

    private BeanFactory beanFactory;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Environment environment = beanFactory.getBean(Environment.class);

        Boolean cacheEnabled = environment.getProperty(AGGREGATE_ROOT_CACHE_ENABLED, Boolean.class, false);
        if (bean instanceof AbstractAggregateRootFactory) {
            if (cacheEnabled) {
                if (log.isDebugEnabled()) {
                    log.debug("Wrapper {} with {}", bean.getClass().getName(), CacheSupportFactoryDecorator.class.getName());
                }
                bean = new CacheSupportFactoryDecorator<>((AggregateRootFactory<?, ?>) bean);
            }

            Boolean persistentEnabled = environment.getProperty(EVENT_PERSISTENT_ENABLED, Boolean.class, false);

            ObjectProvider<EventRepository> eventRepositoryProvider = beanFactory.getBeanProvider(EventRepository.class);
            if (persistentEnabled && eventRepositoryProvider.getIfUnique() != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Wrapper {} with {}", bean.getClass().getName(), PersistentEventSupportFactoryDecorator.class.getName());
                }
                bean = new PersistentEventSupportFactoryDecorator<>((AggregateRootFactory<?, ?>) bean, eventRepositoryProvider.getObject());
            }
        }
        return bean;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
