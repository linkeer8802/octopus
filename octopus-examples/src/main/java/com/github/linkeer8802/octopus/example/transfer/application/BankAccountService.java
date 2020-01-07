package com.github.linkeer8802.octopus.example.transfer.application;

import com.github.linkeer8802.octopus.core.AbstractDomainService;
import com.github.linkeer8802.octopus.core.AggregateRootFactory;
import com.github.linkeer8802.octopus.core.eventbus.OnEvent;
import com.github.linkeer8802.octopus.core.exception.ConflictingAggregateVersionException;
import com.github.linkeer8802.octopus.example.transfer.domain.BankAccount;
import com.github.linkeer8802.octopus.example.transfer.infrastructure.common.model.event.BankAccountCreatedEvent;
import com.github.linkeer8802.octopus.example.transfer.infrastructure.common.model.event.TransferredInEvent;
import com.github.linkeer8802.octopus.example.transfer.infrastructure.common.model.event.TransferredOutEvent;
import com.github.linkeer8802.octopus.example.transfer.infrastructure.repository.BankAccountRepository;
import com.github.linkeer8802.octopus.example.transfer.infrastructure.common.model.entity.BankAccountEntity;
import com.github.linkeer8802.octopus.example.transfer.infrastructure.common.util.Moneys;
import com.github.linkeer8802.octopus.spring.annotation.DomainService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.money.MonetaryAmount;
import java.util.Map;

@DomainService
public class BankAccountService extends AbstractDomainService<BankAccount, Map> {

    @Resource
    private BankAccountRepository repository;

    public BankAccountService(@Qualifier("bankAccountFactory") AggregateRootFactory factory) {
        super(factory);
    }

    /**
     * 开户
     * @param entity
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public String openBankAccount(Map entity) {
        return create(entity).getId();
    }

    /**
     * 转账
     * @param from
     * @param to
     * @param amount
     */
    @Retryable(value = ConflictingAggregateVersionException.class, maxAttempts = 10, backoff = @Backoff(multiplier = 2), listeners = "callRetryListener")
    @Transactional(rollbackFor = Exception.class)
    public void transferAccount(String from, String to, MonetaryAmount amount) {
        BankAccount source = load(from).get();
        BankAccount target = load(to).get();
        source.transferOut(amount);
        target.transferIn(amount);
    }

    @OnEvent
    private void onBankAccountCreated(BankAccountCreatedEvent event) {
        repository.insert(BankAccountEntity.builder()
                .id(event.getId()).name(event.getName()).balance(Moneys.toBigDecimal(event.getBalance()))
                .version(event.getAggregateRootVersion()).build());
    }

    @OnEvent
    private void onTransferredIn(TransferredInEvent event) {
        repository.addBalance(event.id, event.amount);
    }

    @OnEvent
    private void onTransferredOut(TransferredOutEvent event) {
        repository.subtractBalance(event.id, event.amount);
    }
}
