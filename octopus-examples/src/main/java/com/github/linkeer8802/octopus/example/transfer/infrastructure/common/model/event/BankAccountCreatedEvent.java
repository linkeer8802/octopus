package com.github.linkeer8802.octopus.example.transfer.infrastructure.common.model.event;

import com.github.linkeer8802.octopus.core.DomainCreatedEvent;
import lombok.Getter;
import lombok.Setter;

import javax.money.MonetaryAmount;

public class BankAccountCreatedEvent extends DomainCreatedEvent<String>{
    private String id;
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private MonetaryAmount balance;

    public BankAccountCreatedEvent() {}

    public BankAccountCreatedEvent(String id, String name, MonetaryAmount balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }

    @Override
    public String getId() {
        return this.id;
    }
}
