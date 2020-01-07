package com.github.linkeer8802.octopus.core.message.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.linkeer8802.octopus.core.message.Message;
import com.github.linkeer8802.octopus.core.message.MessageHandler;
import com.github.linkeer8802.octopus.core.message.MessageProducer;
import com.github.linkeer8802.octopus.core.serializer.EventMessageSerializer;
import com.github.linkeer8802.octopus.core.serializer.Serializer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.LogIfLevelEnabled;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author weird
 * @date 2019/12/27
 */
public class CanalKafkaMessageProducerImpl implements MessageProducer {

    private final EventMessageSerializer serializer;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ConsumerFactory<String, String> consumerFactory;
    private final ObjectMapper objectMapper;

    public CanalKafkaMessageProducerImpl(EventMessageSerializer serializer, KafkaTemplate<String, String> kafkaTemplate,
                                         ConsumerFactory<String, String> consumerFactory, ObjectMapper objectMapper) {
        this.serializer = serializer;
        this.kafkaTemplate = kafkaTemplate;
        this.consumerFactory = consumerFactory;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        ConcurrentMessageListenerContainer<String, String> container = createContainer("octopus_global_events");
        container.start();
    }

    private ConcurrentMessageListenerContainer<String, String> createContainer(String topic) {

        ContainerProperties containerProps = new ContainerProperties(topic);
        containerProps.setGroupId("group-cdc-server");
        containerProps.setAckMode(ContainerProperties.AckMode.BATCH);
        containerProps.setCommitLogLevel(LogIfLevelEnabled.Level.INFO);
        containerProps.setMissingTopicsFatal(false);
        containerProps.setMessageListener((MessageListener<String, String>) data -> {
            try {
                JsonNode jsonNode = objectMapper.readTree(data.value());
                JsonNode eventNode = jsonNode.get("data").get(0);
                String aggregateRootType = eventNode.get("aggregate_root_type").asText();
                Long timestamp = eventNode.get("timestamp").asLong();
                
                System.out.println("mq message cost:" + (Instant.now().toEpochMilli() - timestamp));
                
                String json = eventNode.get("data").asText();
                String type = jsonNode.get("type").asText();

                if (type.equalsIgnoreCase("INSERT") && json != null && aggregateRootType != null) {
                    String channel = aggregateRootType;
                    Map<String, Object> headers = new HashMap<>(2);
                    headers.put(com.github.linkeer8802.octopus.core.message.Message.CHANNEL, channel);
                    headers.put(com.github.linkeer8802.octopus.core.message.Message.PAYLOAD_TYPE,
                            com.github.linkeer8802.octopus.core.message.Message.TYPE_JSON_PAYLOAD);

                    send(aggregateRootType, new MessageImpl(headers, json), null, null);
                }

            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });

        ConcurrentMessageListenerContainer<String, String> container = new ConcurrentMessageListenerContainer<>(consumerFactory, containerProps);
        container.setBeanName("cdc-server-container-" + topic);
        container.setConcurrency(1);
        return container;
    }

    @Override
    public void send(String channel, Message message, Consumer<Object> onSuccess, Consumer<Throwable> onError) {
        String msg = new String(serializer.serialize(message), Serializer.CHARSET_UTF8);
        String key = (String) message.getHeader(Message.ID).orElse(null);
        kafkaTemplate.send(channel, key, msg);
    }
}
