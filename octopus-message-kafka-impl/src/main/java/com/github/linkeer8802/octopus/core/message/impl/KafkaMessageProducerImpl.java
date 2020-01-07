package com.github.linkeer8802.octopus.core.message.impl;

import com.github.linkeer8802.octopus.core.message.Message;
import com.github.linkeer8802.octopus.core.message.MessageProducer;
import com.github.linkeer8802.octopus.core.serializer.EventMessageSerializer;
import com.github.linkeer8802.octopus.core.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.function.Consumer;

/**
 * @author weird
 */
@Slf4j
public class KafkaMessageProducerImpl implements MessageProducer {

    private final EventMessageSerializer serializer;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaMessageProducerImpl(EventMessageSerializer serializer, KafkaTemplate<String, String> kafkaTemplate) {
        this.serializer = serializer;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void send(String channel, Message message, Consumer<Object> onSuccess, Consumer<Throwable> onError) {
        String msg = new String(serializer.serialize(message), Serializer.CHARSET_UTF8);
        String key = (String) message.getHeader(Message.ID).orElse(null);
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(channel, key, msg);

        final Consumer<Object> onSuccessHandler = onSuccess == null ? result -> {} : onSuccess;
        final Consumer<Throwable> onErrorHandler = onError == null ?  ex -> log.error("消息发送出现异常：", ex) : onError;
        future.addCallback(result -> onSuccessHandler.accept(result), ex -> onErrorHandler.accept(ex));
//        future.completable().handle((result, ex) -> {
//            if (ex != null) {
//                onErrorHandler.accept(ex);
//                return ex;
//            } else {
//                onSuccessHandler.accept(result);
//                return result;
//            }
//        });
    }
}
