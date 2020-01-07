package com.github.linkeer8802.octopus.example.transfer.application;

import com.github.linkeer8802.octopus.core.AggregateRootFactory;
import com.github.linkeer8802.octopus.core.util.Identifiers;
import com.github.linkeer8802.octopus.example.transfer.domain.BankAccount;
import com.github.linkeer8802.octopus.example.transfer.infrastructure.common.util.Moneys;
import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import javax.money.MonetaryAmount;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.NONE)
public class BankAccountServiceTest {

    @Resource
    private BankAccountService bankAccountService;
    @Resource(name="bankAccountFactory")
    AggregateRootFactory<BankAccount, Map> bankAccountFactory;

    @Test
    public void openBankAccount() {
        String id = Identifiers.uuid();
        String name = "张三";
        MonetaryAmount balance = Moneys.of(1000.00);
        bankAccountService.openBankAccount(ImmutableMap.of("id", id, "name", name, "balance", balance));
    }

    @Test
    public void transferAccount() {
        String sourceId = Identifiers.uuid();
        createBankAccount(sourceId, "张三", Moneys.of(1000.00));
        String targetId = Identifiers.uuid();
        createBankAccount(targetId, "李四", Moneys.of(1000.00));
        bankAccountService.transferAccount(sourceId, targetId, Moneys.of(150.00));
    }

    private void createBankAccount(String id, String name, MonetaryAmount balance) {
        bankAccountService.openBankAccount(ImmutableMap.of("id", id, "name", name, "balance", balance));
    }

    @Test
    public void testConcurrentTransferAccount() {
        String sourceId = Identifiers.uuid();
        createBankAccount(sourceId, "张三", Moneys.of(10000.00));
        String targetId = Identifiers.uuid();
        createBankAccount(targetId, "李四", Moneys.of(10000.00));

        int count = 1000;
        List<CompletableFuture<?>> cfs = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            CompletableFuture future = CompletableFuture.completedFuture(null).thenRunAsync(() ->
                    bankAccountService.transferAccount(sourceId, targetId, Moneys.of(10.00)));
            cfs.add(future);
        }
        CompletableFuture.allOf(cfs.toArray(new CompletableFuture[0])).join();

        BankAccount sourceBankAccount = bankAccountFactory.load(sourceId, BankAccount.class).get();
        BankAccount targetBankAccount = bankAccountFactory.load(targetId, BankAccount.class).get();

        Assert.assertNotNull(sourceBankAccount);
        Assert.assertNotNull(targetBankAccount);
        Assert.assertEquals(Moneys.of(0.00), sourceBankAccount.getBalance());
        Assert.assertEquals(Moneys.of(20000.00), targetBankAccount.getBalance());
    }
}