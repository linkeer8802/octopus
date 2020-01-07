package com.github.linkeer8802.octopus.example.order.application;

import com.github.linkeer8802.octopus.core.eventbus.OnReceived;
import com.github.linkeer8802.octopus.core.message.MessageSubscriber;
import com.github.linkeer8802.octopus.example.customer.event.CustomerCreditReservedEvent;
import com.github.linkeer8802.octopus.example.customer.exception.CustomerCreditLimitExceededException;
import com.github.linkeer8802.octopus.example.customer.exception.CustomerValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author weird
 * @date 2019/12/25
 */
@Slf4j
@Component
public class OrderEventHandler {

    @Resource
    private OrderService orderService;

    static final String CUSTOMER_CHANNEL = "com.github.linkeer8802.octopus.example.customer.domain.Customer";

    public OrderEventHandler(MessageSubscriber messageSubscriber) {
        messageSubscriber.subscribe(this);
    }

    @OnReceived(channel = CUSTOMER_CHANNEL)
    private void handle(CustomerCreditReservedEvent event) {
        log.info("Received event:{}", event);
        orderService.approveOrder(event.getOrderId());
    }

    @OnReceived(channel = CUSTOMER_CHANNEL)
    private void handle(CustomerCreditLimitExceededException exception) {
        log.info(exception.getMessage());
        orderService.rejectOrder(exception.getOrderId());
    }

    @OnReceived(channel = CUSTOMER_CHANNEL)
    private void handle(CustomerValidationException exception) {
        log.info(exception.getMessage());
        orderService.rejectOrder(exception.getOrderId());
    }
}
