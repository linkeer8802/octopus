package com.github.linkeer8802.octopus.example.transfer.domain;

import com.github.linkeer8802.octopus.core.EventSourcingAggregateRoot;
import com.github.linkeer8802.octopus.core.eventbus.OnEvent;
import com.github.linkeer8802.octopus.example.transfer.infrastructure.common.model.event.BankAccountCreatedEvent;
import com.github.linkeer8802.octopus.example.transfer.infrastructure.common.model.event.TransferredInEvent;
import com.github.linkeer8802.octopus.example.transfer.infrastructure.common.model.event.TransferredOutEvent;
import com.github.linkeer8802.octopus.example.transfer.infrastructure.common.model.exception.BalanceNotEnoughException;
import com.github.linkeer8802.octopus.example.transfer.infrastructure.common.util.Moneys;
import lombok.ToString;

import javax.money.MonetaryAmount;

@ToString(callSuper = true)
public class BankAccount extends EventSourcingAggregateRoot<BankAccount, String> {
    /**
     * 账户名
     */
    private String name;
    /**
     * 余额
     */
    private MonetaryAmount balance;

    private BankAccount() {}

    public void transferIn(MonetaryAmount amount) {
        publishEvent(new TransferredInEvent(getId(), amount));
    }

    public void transferOut(MonetaryAmount amount) {
        if (balance.subtract(amount).isNegative()) {
            throw new BalanceNotEnoughException(
                    String.format("账户余额不足：账户余额%.2f，待转出金额%.2f",
                    Moneys.toBigDecimal(balance), Moneys.toBigDecimal(amount)));
        }
        publishEvent(new TransferredOutEvent(getId(), amount));
    }

    public String getName() {
        return name;
    }

    public MonetaryAmount getBalance() {
        return balance;
    }

    @OnEvent
    private void onBankAccountCreated(BankAccountCreatedEvent event) {
        this.id = event.getId();
        this.name = event.getName();
        this.balance = event.getBalance();
    }

    @OnEvent
    private void onTransferredIn(TransferredInEvent event) {
        balance = balance.add(event.amount);
    }

    @OnEvent
    private void onTransferredOut(TransferredOutEvent event) {
        balance = balance.subtract(event.amount);
    }
}
