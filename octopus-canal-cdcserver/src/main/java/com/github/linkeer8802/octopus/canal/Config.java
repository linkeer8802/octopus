package com.github.linkeer8802.octopus.canal;

import com.github.linkeer8802.octopus.core.message.CDCServer;
import com.github.linkeer8802.octopus.core.message.MessageProducer;
import com.github.linkeer8802.octopus.core.message.impl.KafkaMessageProducerImpl;
import com.github.linkeer8802.octopus.core.serializer.EventMessageSerializer;
import com.github.linkeer8802.octopus.core.serializer.EventSerializer;
import com.github.linkeer8802.octopus.core.serializer.Jackson2JsonSerializer;
import com.github.linkeer8802.octopus.core.serializer.Serializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.zalando.jackson.datatype.money.MoneyModule;

/**
 * @author weird
 * @date 2019/12/5
 */
@Configuration
public class Config {


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
    public CDCServer canalCDCServer() {
        return new CanalCDCServer();
    }

    @Bean
    public MessageProducer kafkaMessageProducer(KafkaTemplate kafkaTemplate) {
        return new KafkaMessageProducerImpl(new EventMessageSerializer(jackson2JsonSerializer()), kafkaTemplate);
    }

}
