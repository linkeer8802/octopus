package com.github.linkeer8802.octopus.example.customer.infrastructure.repository;

import com.github.linkeer8802.octopus.example.customer.entity.CustomerEntity;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Optional;

/**
 * @author weird
 */
@Repository
public class CustomerEntityRepository {
    @Resource
    private DSLContext create;

    Table<Record> CUSTOMER = DSL.table("customer");
    Field<String> ID = DSL.field("id", String.class);
    Field<String> NAME = DSL.field("name", String.class);
    Field<BigDecimal> CREDIT_LIMIT = DSL.field("credit_limit", BigDecimal.class);
    Field<Long> VERSION = DSL.field("version", Long.class);

    public int insert(CustomerEntity entity) {
        return create.insertInto(CUSTOMER, ID, NAME, CREDIT_LIMIT, VERSION)
                .values(entity.getId(), entity.getName(), entity.getCreditLimit(), entity.getVersion()).execute();
    }

    public Optional<CustomerEntity> findById(Serializable id) {
        return Optional.ofNullable(create.selectFrom(CUSTOMER).where(ID.equal(id.toString())).fetchOneInto(CustomerEntity.class));
    }

    public int updateVersion(Serializable id, Long oldVersion, Long newVersion) {
        return create.update(CUSTOMER).set(VERSION, newVersion).where(ID.equal((String) id).and(VERSION.equal(oldVersion))).execute();
    }
}
