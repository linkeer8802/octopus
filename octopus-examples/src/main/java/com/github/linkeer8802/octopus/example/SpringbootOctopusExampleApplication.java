package com.github.linkeer8802.octopus.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableRetry(proxyTargetClass = true)
public class SpringbootOctopusExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootOctopusExampleApplication.class, args);
    }
}
