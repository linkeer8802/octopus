package com.github.linkeer8802.octopus.example.order.event;

import com.github.linkeer8802.octopus.core.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.money.MonetaryAmount;

@Getter
@NoArgsConstructor
public class OrderCancelledEvent extends DomainEvent {
    private String customerId;
    private MonetaryAmount total;

    public OrderCancelledEvent(String customerId, MonetaryAmount total) {
      this.customerId = customerId;
      this.total = total;
    }
}
