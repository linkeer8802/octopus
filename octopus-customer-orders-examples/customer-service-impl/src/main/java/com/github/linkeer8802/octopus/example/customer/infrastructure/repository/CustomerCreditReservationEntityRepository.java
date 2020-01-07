package com.github.linkeer8802.octopus.example.customer.infrastructure.repository;

import com.github.linkeer8802.octopus.example.customer.entity.CustomerCreditReservationEntity;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * @author weird
 * @date 2019/12/25
 */
@Repository
public class CustomerCreditReservationEntityRepository {
    @Resource
    private DSLContext create;

    Table<Record> CUSTOMER_CREDIT_RESERVATION = DSL.table("customer_credit_reservation");
    Field<String> ID = DSL.field("id", String.class);
    Field<String> CUSTOMER_ID = DSL.field("customer_id", String.class);
    Field<String> ORDER_ID = DSL.field("order_id", String.class);
    Field<BigDecimal> AMOUNT = DSL.field("amount", BigDecimal.class);

    public int insert(CustomerCreditReservationEntity entity) {
        return create.insertInto(CUSTOMER_CREDIT_RESERVATION, ID, CUSTOMER_ID, ORDER_ID, AMOUNT)
                .values(entity.getId(), entity.getCustomerId(), entity.getOrderId(), entity.getAmount()).execute();
    }

    public Optional<List<CustomerCreditReservationEntity>> findByCustomerId(String customerId) {
        return Optional.ofNullable(create.selectFrom(CUSTOMER_CREDIT_RESERVATION)
                .where(CUSTOMER_ID.equal(customerId)).fetchInto(CustomerCreditReservationEntity.class));
    }

    public int delete(String id) {
        return create.delete(CUSTOMER_CREDIT_RESERVATION).where(ID.eq(id)).execute();
    }
}
