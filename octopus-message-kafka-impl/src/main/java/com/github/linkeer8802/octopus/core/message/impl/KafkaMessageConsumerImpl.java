package com.github.linkeer8802.octopus.core.message.impl;

import com.github.linkeer8802.octopus.core.message.Message;
import com.github.linkeer8802.octopus.core.message.MessageConsumer;
import com.github.linkeer8802.octopus.core.message.MessageHandler;
import com.github.linkeer8802.octopus.core.serializer.EventMessageSerializer;
import com.github.linkeer8802.octopus.core.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.listener.*;
import org.springframework.kafka.support.LogIfLevelEnabled;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author weird
 * @date 2019/12/27
 */
@Slf4j
public class KafkaMessageConsumerImpl implements MessageConsumer, ApplicationListener, BeanFactoryAware {

    @Value("${octopus.message.kafka.concurrency:3}")
    private Integer concurrency;
    @Value("${octopus.message.kafka.partitions:3}")
    private Integer partitions;
    @Value("${octopus.message.kafka.topicreplicas:1}")
    private Integer topicReplicas;

    private KafkaAdmin kafkaAdmin;
    private EventMessageSerializer serializer;
    private BeanFactory beanFactory;
    private ConsumerFactory<String, String> consumerFactory;
    private Set<ConcurrentMessageListenerContainer<String, String>> containers;

    public KafkaMessageConsumerImpl(EventMessageSerializer serializer, ConsumerFactory<String, String> consumerFactory, KafkaAdmin kafkaAdmin) {
        this.kafkaAdmin = kafkaAdmin;
        this.serializer = serializer;
        this.consumerFactory = consumerFactory;
        this.containers = new CopyOnWriteArraySet<>();
    }

    @Override
    public void handle(String channel, MessageHandler handler) {
        containers.add(createContainer(handler, channel));
    }

    private ConcurrentMessageListenerContainer<String, String> createContainer(MessageHandler handler, String topic) {

        registerNewTopicBeanDefinition(topic);

        ContainerProperties containerProps = new ContainerProperties(topic);
        containerProps.setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        containerProps.setCommitLogLevel(LogIfLevelEnabled.Level.INFO);
        containerProps.setMissingTopicsFatal(false);
        containerProps.setMessageListener((AcknowledgingMessageListener<String, String>) (data, acknowledgment) -> {
            Message message = serializer.deserialize(data.value().getBytes(Serializer.CHARSET_UTF8));
            message.setHeader(Message.PARTITION_ID, data.partition());
            handler.process(message, result-> acknowledgment.acknowledge(), ex -> log.error("消息处理异常：", ex));
        });

        ConcurrentMessageListenerContainer<String, String> container = new ConcurrentMessageListenerContainer<>(consumerFactory, containerProps);
        container.setBeanName("container-" + topic);
        container.setConcurrency(concurrency);
        return container;
    }

    private void registerNewTopicBeanDefinition(String channel) {
        NewTopic newTopic = TopicBuilder.name(channel).partitions(partitions).replicas(topicReplicas).build();
        ((SingletonBeanRegistry) beanFactory).registerSingleton("topic-" + channel, newTopic);
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            log.info("Initialize topics");
            //创建topics
            kafkaAdmin.initialize();
            log.info("Start containers:" + containers.size());
            containers.forEach(AbstractMessageListenerContainer::start);
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
