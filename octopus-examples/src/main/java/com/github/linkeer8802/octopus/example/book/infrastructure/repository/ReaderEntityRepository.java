package com.github.linkeer8802.octopus.example.book.infrastructure.repository;

import com.github.linkeer8802.octopus.example.book.infrastructure.common.model.entity.ReaderEntity;
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

/**
 * @author weird
 * @date 2019/11/29
 */
@Repository
public class ReaderEntityRepository {

    @Resource
    private DSLContext create;

    Table<Record> READER = DSL.table("reader");
    Field<String> ID = DSL.field("id", String.class);
    Field<String> NAME = DSL.field("name", String.class);
    Field<BigDecimal> UNPAID_FINE = DSL.field("unpaid_fine", BigDecimal.class);
    Field<Long> VERSION = DSL.field("version", Long.class);

    public int insert(ReaderEntity entity) {
       return create.insertInto(READER, ID, NAME, VERSION)
                .values(entity.getId(), entity.getName(), entity.getVersion()).execute();
    }

    public ReaderEntity findById(Serializable id) {
        return create.selectFrom(READER).where(ID.equal(id.toString())).fetchOneInto(ReaderEntity.class);
    }

    public int updateVersion(Serializable id, Long oldVersion, Long newVersion) {
        return create.update(READER).set(VERSION, newVersion).where(ID.equal((String) id).and(VERSION.equal(oldVersion))).execute();
    }

    public int updateUnpaidFine(String readerId, MonetaryAmount fine) {
        return create.update(READER).set(UNPAID_FINE, Moneys.toBigDecimal(fine)).where(ID.equal(readerId)).execute();
    }
}
