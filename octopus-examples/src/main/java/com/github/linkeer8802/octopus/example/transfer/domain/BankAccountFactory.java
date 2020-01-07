package com.github.linkeer8802.octopus.example.transfer.domain;

import com.github.linkeer8802.octopus.core.AbstractAggregateRootFactory;
import com.github.linkeer8802.octopus.core.DomainCreatedEvent;
import com.github.linkeer8802.octopus.core.ModelWithVersion;
import com.github.linkeer8802.octopus.example.transfer.infrastructure.common.model.event.BankAccountCreatedEvent;
import com.github.linkeer8802.octopus.example.transfer.infrastructure.repository.BankAccountRepository;
import com.github.linkeer8802.octopus.example.transfer.infrastructure.common.model.entity.BankAccountEntity;
import com.github.linkeer8802.octopus.example.transfer.infrastructure.common.util.Moneys;
import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.money.MonetaryAmount;
import java.io.Serializable;
import java.util.Map;

@Component
public class BankAccountFactory extends AbstractAggregateRootFactory<BankAccount, Map> {

    @Resource
    private BankAccountRepository repository;

    @Override
    protected DomainCreatedEvent createByModel(Map model) {
        String id = (String) model.get("id");
        String name = (String) model.get("name");
        MonetaryAmount balance = (MonetaryAmount) model.get("balance");
        if (balance.isNegative()) {
            throw new IllegalStateException("账户余额必须大于0");
        }
        return new BankAccountCreatedEvent(id, name, balance);
    }

    @Override
    protected ModelWithVersion<Map> loadModel(Serializable id) {
        BankAccountEntity entity = repository.findById(id);
        return new ModelWithVersion<>(ImmutableMap.of("id", entity.getId(),
                "name", entity.getName(), "balance", Moneys.of(entity.getBalance())), entity.getVersion());
    }

    @Override
    public Integer updateVersion(Serializable id, Long oldVersion, Long newVersion) {
        return repository.updateVersion(id, oldVersion, newVersion);
    }
}
