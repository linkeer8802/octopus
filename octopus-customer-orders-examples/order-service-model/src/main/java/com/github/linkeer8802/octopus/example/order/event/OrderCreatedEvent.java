package com.github.linkeer8802.octopus.example.order.event;

import com.github.linkeer8802.octopus.core.DomainCreatedEvent;
import com.github.linkeer8802.octopus.example.order.OrderState;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.money.MonetaryAmount;

@Getter
@NoArgsConstructor
public class OrderCreatedEvent extends DomainCreatedEvent<String> {
    private String id;
    private String customerId;
    private MonetaryAmount total;
    private OrderState state;

    public OrderCreatedEvent(String id, String customerId, MonetaryAmount total, OrderState state) {
    this.id = id;
    this.customerId = customerId;
    this.total = total;
    this.state = state;
    }
}
