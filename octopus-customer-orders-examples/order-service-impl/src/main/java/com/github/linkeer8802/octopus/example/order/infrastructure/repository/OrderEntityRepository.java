package com.github.linkeer8802.octopus.example.order.infrastructure.repository;

import com.github.linkeer8802.octopus.example.order.OrderState;
import com.github.linkeer8802.octopus.example.order.entity.OrderEntity;
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
 * @date 2019/12/25
 */
@Repository
public class OrderEntityRepository {
    @Resource
    private DSLContext create;

    Table<Record> ORDER = DSL.table("orders");
    Field<String> ID = DSL.field("id", String.class);
    Field<String> CUSTOMER_ID = DSL.field("customer_id", String.class);
    Field<BigDecimal> TOTAL = DSL.field("total", BigDecimal.class);
    Field<String> STATE = DSL.field("`state`", String.class);
    Field<Long> VERSION = DSL.field("version", Long.class);

    public int insert(OrderEntity po) {
        return create.insertInto(ORDER, ID, CUSTOMER_ID, TOTAL, STATE, VERSION)
                .values(po.getId(), po.getCustomerId(), po.getTotal(), po.getState().name(), po.getVersion()).execute();
    }

    public Optional<OrderEntity> findById(Serializable id) {
        return Optional.ofNullable(create.selectFrom(ORDER).where(ID.equal(id.toString())).fetchOneInto(OrderEntity.class));
    }

    public int updateVersion(Serializable id, Long oldVersion, Long newVersion) {
        return create.update(ORDER).set(VERSION, newVersion).where(ID.equal((String) id).and(VERSION.equal(oldVersion))).execute();
    }

    public int changeState(String orderId, OrderState state) {
        return create.update(ORDER).set(STATE, state.name()).where(ID.equal(orderId)).execute();
    }
}
