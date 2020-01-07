package com.github.linkeer8802.octopus.example.customer.domain;

import com.github.linkeer8802.octopus.core.EventSourcingAggregateRoot;
import com.github.linkeer8802.octopus.core.eventbus.OnEvent;
import com.github.linkeer8802.octopus.core.util.Identifiers;
import com.github.linkeer8802.octopus.example.customer.event.CustomerCreatedEvent;
import com.github.linkeer8802.octopus.example.customer.event.CustomerCreditReservedEvent;
import com.github.linkeer8802.octopus.example.customer.event.CustomerCreditUnreservedEvent;
import com.github.linkeer8802.octopus.example.customer.exception.CustomerCreditLimitExceededException;
import com.github.linkeer8802.octopus.example.customer.infrastructure.common.util.Moneys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.money.MonetaryAmount;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Customer聚合根
 * @author weird
 * @date 2019/12/24
 */
@Slf4j
@Getter
public class Customer extends EventSourcingAggregateRoot<Customer, String> {
    private String name;
    private MonetaryAmount creditLimit;
    private List<CreditReservation> creditReservations;

    private Customer() {}

    /**
     * 预留信用额度
     * @param orderId
     * @param orderAmount
     */
    public void reserveCredit(String orderId, MonetaryAmount orderAmount) {
        if (availableCredit().isGreaterThanOrEqualTo(orderAmount)) {
            publishEvent(new CustomerCreditReservedEvent(Identifiers.uuid(), getId(), orderId, orderAmount));
        } else {
            throw new CustomerCreditLimitExceededException("订单的金额超过客户的可用信用额度", orderId);
        }
    }

    /**
     * 取消指定订单的信用额度预留
     * @param orderId
     */
    public void unreserveCredit(String orderId) {
        creditReservation(orderId).ifPresent(creditReservation ->
                publishEvent(new CustomerCreditUnreservedEvent(creditReservation.getId())));
    }

    MonetaryAmount availableCredit() {
        return creditLimit.subtract(creditReservations.stream().reduce(Moneys.ZERO, (a, b) -> b.getAmount().add(a), (a, b) -> null));
    }

    private Optional<CreditReservation> creditReservation(String orderId) {
        return creditReservations.stream().filter(creditReservation ->
                creditReservation.getOrderId().equals(orderId) && creditReservation.getCustomerId().equals(getId())).findFirst();
    }

    @OnEvent
    private void onCustomerCreated(CustomerCreatedEvent event) {
        this.id = event.getId();
        this.name = event.getName();
        this.creditLimit = event.getCreditLimit();
        this.creditReservations = event.getCreditReservations().stream().map(CreditReservation::new).collect(Collectors.toList());
    }

    @OnEvent
    private void onCustomerCreditUnreserved(CustomerCreditUnreservedEvent event) {
        creditReservations.removeIf(creditReservation -> creditReservation.getId().equals(event.getCreditReservationId()));
    }

    @OnEvent
    private void onCreditReserved(CustomerCreditReservedEvent event) {
        creditReservations.add(new CreditReservation(event.getId(), event.getCustomerId(), event.getOrderId(), event.getAmount()));
    }
}
