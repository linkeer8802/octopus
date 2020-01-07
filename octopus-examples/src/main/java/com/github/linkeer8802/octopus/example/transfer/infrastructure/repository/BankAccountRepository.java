package com.github.linkeer8802.octopus.example.transfer.infrastructure.repository;

import com.github.linkeer8802.octopus.example.transfer.infrastructure.common.model.entity.BankAccountEntity;
import com.github.linkeer8802.octopus.example.transfer.infrastructure.common.util.Moneys;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.money.MonetaryAmount;
import java.io.Serializable;
import java.math.BigDecimal;

@Repository
public class BankAccountRepository {

    @Resource
    private DSLContext create;

    Table<Record> BANK_ACCOUNT = DSL.table("bank_account");
    Field<String> ID = DSL.field("id", String.class);
    Field<String> NAME = DSL.field("name", String.class);
    Field<BigDecimal> BALANCE = DSL.field("balance", BigDecimal.class);
    Field<Long> VERSION = DSL.field("version", Long.class);

    public int insert(BankAccountEntity entity) {
        return create.insertInto(BANK_ACCOUNT, ID, NAME, BALANCE, VERSION)
                     .values(entity.getId(), entity.getName(), entity.getBalance(), entity.getVersion()).execute();
    }

    public BankAccountEntity findById(Serializable id) {
        return create.selectFrom(BANK_ACCOUNT).where(ID.equal(id.toString())).fetchOneInto(BankAccountEntity.class);
    }

    public int addBalance(String id, MonetaryAmount amount) {
        return create.update(BANK_ACCOUNT).set(BALANCE, BALANCE.add(Moneys.toBigDecimal(amount))).where(ID.equal(id)).execute();
    }

    public int subtractBalance(String id, MonetaryAmount amount) {
        return create.update(BANK_ACCOUNT).set(BALANCE, BALANCE.subtract(Moneys.toBigDecimal(amount))).where(ID.equal(id)).execute();
    }

    public int updateVersion(Serializable id, Long oldVersion, Long newVersion) {
        return create.update(BANK_ACCOUNT).set(VERSION, newVersion).where(ID.equal((String) id).and(VERSION.equal(oldVersion))).execute();
    }
}
