package com.github.linkeer8802.octopus.example.order.application;

import com.github.linkeer8802.octopus.core.AbstractDomainService;
import com.github.linkeer8802.octopus.core.AggregateRootContainer;
import com.github.linkeer8802.octopus.core.AggregateRootFactory;
import com.github.linkeer8802.octopus.core.eventbus.OnEvent;
import com.github.linkeer8802.octopus.example.order.domain.Order;
import com.github.linkeer8802.octopus.example.order.datamodel.OrderModel;
import com.github.linkeer8802.octopus.example.order.event.OrderApprovedEvent;
import com.github.linkeer8802.octopus.example.order.event.OrderCreatedEvent;
import com.github.linkeer8802.octopus.example.order.event.OrderRejectedEvent;
import com.github.linkeer8802.octopus.example.order.infrastructure.common.util.Moneys;
import com.github.linkeer8802.octopus.example.order.infrastructure.repository.OrderEntityRepository;
import com.github.linkeer8802.octopus.example.order.entity.OrderEntity;
import com.github.linkeer8802.octopus.example.order.vo.OrderVO;
import com.github.linkeer8802.octopus.spring.annotation.DomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author weird
 * @date 2019/12/25
 */
@Slf4j
@DomainService
public class OrderService extends AbstractDomainService<Order, OrderModel> {

    @Resource
    private OrderEntityRepository orderRepository;

    public OrderService(@Qualifier("orderFactory") AggregateRootFactory<Order, OrderModel> factory) {
        super(factory);
    }

    @Transactional
    public OrderVO createOrder(OrderModel dto) {
        Order order = create(dto);
        return new OrderVO(order.getId(), order.getCustomerId(), Moneys.toBigDecimal(order.getTotal()), order.getState());
    }

    @Transactional(rollbackFor = Exception.class)
    public void approveOrder(String orderId) {
        Order order = load(orderId).get();
        order.noteCreditReserved();
    }

    @Transactional(rollbackFor = Exception.class)
    public void rejectOrder(String orderId) {
        AggregateRootContainer<Order> aggregateRootContainer = load(orderId);
        if (!aggregateRootContainer.isEmpty()) {
            aggregateRootContainer.get().noteCreditReservationFailed();
        }
    }

    @OnEvent
    private void onOrderCreated(OrderCreatedEvent event) {
        orderRepository.insert(new OrderEntity(event.getId(), event.getCustomerId(),
                Moneys.toBigDecimal(event.getTotal()), event.getState(), event.getAggregateRootVersion()));
    }

    @OnEvent
    private void onOrderApproved(OrderApprovedEvent event) {
        orderRepository.changeState(event.getOrderId(), event.getState());
    }

    @OnEvent
    private void onOrderRejected(OrderRejectedEvent event) {
        orderRepository.changeState(event.getOrderId(), event.getState());
    }
}
