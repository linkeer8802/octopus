package com.github.linkeer8802.octopus.example.customer.application;

import com.github.linkeer8802.octopus.core.eventbus.OnReceived;
import com.github.linkeer8802.octopus.core.message.MessageSubscriber;
import com.github.linkeer8802.octopus.example.order.event.OrderCreatedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author weird
 * @date 2019/12/25
 */
@Component
public class CustomerEventHandler {

    @Resource
    private CustomerService customerService;

    static final String ORDER_CHANNEL = "com.github.linkeer8802.octopus.example.order.domain.Order";

    public CustomerEventHandler(MessageSubscriber messageSubscriber) {
        messageSubscriber.subscribe(this);
    }

    @OnReceived(channel = ORDER_CHANNEL)
    private void onOrderCreated(OrderCreatedEvent event) {
        customerService.reserveCredit(event.getCustomerId(), event.getId(), event.getTotal());
    }
}
