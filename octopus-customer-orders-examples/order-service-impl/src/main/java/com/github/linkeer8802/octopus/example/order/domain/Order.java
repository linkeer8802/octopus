package com.github.linkeer8802.octopus.example.order.domain;

import com.github.linkeer8802.octopus.core.EventSourcingAggregateRoot;
import com.github.linkeer8802.octopus.core.eventbus.OnEvent;
import com.github.linkeer8802.octopus.example.order.OrderState;
import com.github.linkeer8802.octopus.example.order.event.OrderApprovedEvent;
import com.github.linkeer8802.octopus.example.order.event.OrderCancelledEvent;
import com.github.linkeer8802.octopus.example.order.event.OrderCreatedEvent;
import com.github.linkeer8802.octopus.example.order.event.OrderRejectedEvent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.money.MonetaryAmount;

/**
 * Order聚合根
 * @author weird
 * @date 2019/12/24
 */
@Slf4j
@Getter
public class Order extends EventSourcingAggregateRoot<Order, String> {
    private OrderState state;
    private String customerId;
    private MonetaryAmount total;

    private Order() {}

    public void noteCreditReserved() {
        publishEvent(new OrderApprovedEvent(getId(), OrderState.APPROVED));
    }

    public void noteCreditReservationFailed() {
        publishEvent(new OrderRejectedEvent(getId(), OrderState.REJECTED));
    }

    @OnEvent
    private void onOrderCreated(OrderCreatedEvent event) {
        this.id = event.getId();
        this.customerId = event.getCustomerId();
        this.total = event.getTotal();
        this.state = event.getState();
    }


    @OnEvent
    private void onOrderApproved(OrderApprovedEvent event) {
        this.state = event.getState();
    }

    @OnEvent
    private void onOrderRejected(OrderRejectedEvent event) {
        this.state = event.getState();
    }

    @OnEvent
    public void cancel(OrderCancelledEvent event) {
        switch (state) {
            case PENDING:
//                throw new PendingOrderCantBeCancelledException();
            case APPROVED:
                this.state = OrderState.CANCELLED;
                return;
            default:
                throw new UnsupportedOperationException("Can't cancel in this state: " + state);
        }
    }
}
