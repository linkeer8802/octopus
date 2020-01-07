package com.github.linkeer8802.octopus.example.customer.event;

import com.github.linkeer8802.octopus.core.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.money.MonetaryAmount;

/**
 * @author weird
 * @date 2019/12/24
 */
@Getter
@NoArgsConstructor
public class CustomerCreditReservedEvent extends DomainEvent {
    private String id;
    private String customerId;
    private String orderId;
    private MonetaryAmount amount;

    public CustomerCreditReservedEvent(String id, String customerId, String orderId, MonetaryAmount amount) {
        this.id = id;
        this.customerId = customerId;
        this.orderId = orderId;
        this.amount = amount;
    }
}
