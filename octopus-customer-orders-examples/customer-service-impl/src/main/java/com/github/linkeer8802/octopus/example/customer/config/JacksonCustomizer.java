package com.github.linkeer8802.octopus.example.customer.config;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import org.zalando.jackson.datatype.money.MoneyModule;

/**
 * @author weird
 * @date 2019/12/5
 */
@Component
public class JacksonCustomizer implements Jackson2ObjectMapperBuilderCustomizer {
    @Override
    public void customize(Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder) {
        //MoneyModule
        jacksonObjectMapperBuilder.modules(new MoneyModule().withDefaultFormatting());
    }
}
