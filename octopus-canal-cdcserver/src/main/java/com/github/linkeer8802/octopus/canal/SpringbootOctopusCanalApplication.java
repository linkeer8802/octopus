package com.github.linkeer8802.octopus.canal;

import com.github.linkeer8802.octopus.core.message.CDCServer;
import com.github.linkeer8802.octopus.core.message.MessageProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

@Slf4j
@SpringBootApplication
public class SpringbootOctopusCanalApplication implements CommandLineRunner {

    @Resource
    private MessageProducer messageProducer;
    @Resource
    private CDCServer canalCDCServer;

    public static void main(String[] args) {
        SpringApplication.run(SpringbootOctopusCanalApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        canalCDCServer.addMessageListener(message -> {
            String channel = (String) message.getHeader(com.github.linkeer8802.octopus.core.message.Message.CHANNEL).get();
            log.info("Send message to kafka, message:{}", message);
            messageProducer.send(channel, message, null, null);
        });
        canalCDCServer.start();
    }
}
