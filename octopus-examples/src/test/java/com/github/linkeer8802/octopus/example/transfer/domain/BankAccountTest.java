package com.github.linkeer8802.octopus.example.transfer.domain;

import com.github.linkeer8802.octopus.core.AggregateRootFactory;
import com.github.linkeer8802.octopus.core.InMemoryFactoryDecorator;
import com.github.linkeer8802.octopus.core.util.Identifiers;
import com.github.linkeer8802.octopus.example.transfer.infrastructure.common.model.exception.BalanceNotEnoughException;
import com.github.linkeer8802.octopus.example.transfer.infrastructure.common.util.Moneys;
import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;

import javax.money.MonetaryAmount;
import java.util.Map;
import java.util.UUID;

public class BankAccountTest {

    private static AggregateRootFactory<BankAccount, Map> factory = new InMemoryFactoryDecorator<>(new BankAccountFactory());

    @Test
    public void testBankAccountOpen() {
        String id = Identifiers.uuid();
        String name = "张三";
        MonetaryAmount balance = Moneys.of(1000.00);
        BankAccount bankAccount = factory.create(
                ImmutableMap.of("id", id, "name", name, "balance", balance), BankAccount.class);

        Assert.assertNotNull(bankAccount);
        Assert.assertEquals(id, bankAccount.getId());
        Assert.assertEquals(name, bankAccount.getName());
        Assert.assertEquals(balance, bankAccount.getBalance());
    }

    @Test(expected = IllegalStateException.class)
    public void testBankAccountOpenWithException() {
        String id = Identifiers.uuid();
        String name = "张三";
        MonetaryAmount balance = Moneys.of(-1.00);
        factory.create(ImmutableMap.of("id", id, "name", name, "balance", balance), BankAccount.class);
    }

    @Test
    public void testTransferIn() {
        String id = Identifiers.uuid();
        createBankAccount(id, "张三", Moneys.of(1000.00));
        factory.load(id, BankAccount.class).get().transferIn(Moneys.of(50.00));

        Assert.assertEquals(Moneys.of(1050.00), factory.load(id, BankAccount.class).get().getBalance());
    }

    @Test
    public void testTransferOut() {
        String id = Identifiers.uuid();
        createBankAccount(id, "张三", Moneys.of(1000.00));
        factory.load(id, BankAccount.class).get().transferOut(Moneys.of(50.00));

        Assert.assertEquals(Moneys.of(950.00), factory.load(id, BankAccount.class).get().getBalance());
    }

    @Test(expected = BalanceNotEnoughException.class)
    public void testTransferOutWithException() {
        String id = Identifiers.uuid();
        createBankAccount(id, "张三", Moneys.of(1000.00));
        factory.load(id, BankAccount.class).get().transferOut(Moneys.of(1050.00));
    }

    private void createBankAccount(String id, String name, MonetaryAmount balance) {
        factory.create(ImmutableMap.of("id", id, "name", name, "balance", balance), BankAccount.class);
    }
}