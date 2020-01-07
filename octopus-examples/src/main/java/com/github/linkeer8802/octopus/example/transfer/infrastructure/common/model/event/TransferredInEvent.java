package com.github.linkeer8802.octopus.example.transfer.infrastructure.common.model.event;

import com.github.linkeer8802.octopus.core.DomainEvent;

import javax.money.MonetaryAmount;

public class TransferredInEvent extends DomainEvent {

    public final String id;
    public final MonetaryAmount amount;

    public TransferredInEvent(String id, MonetaryAmount amount) {
        this.id = id;
        this.amount = amount;
    }
}
