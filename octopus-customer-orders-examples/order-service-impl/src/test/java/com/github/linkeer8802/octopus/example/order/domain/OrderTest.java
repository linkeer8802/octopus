package com.github.linkeer8802.octopus.example.order.domain;

import com.github.linkeer8802.octopus.core.AggregateRootFactory;
import com.github.linkeer8802.octopus.core.InMemoryFactoryDecorator;
import com.github.linkeer8802.octopus.example.order.datamodel.OrderModel;
import org.junit.Test;

/**
 * @author weird
 * @date 2019/12/25
 */
public class OrderTest {

    private static AggregateRootFactory<Order, OrderModel> factory = new InMemoryFactoryDecorator<>(new OrderFactory());

    @Test
    public void noteCreditReserved() {
    }

    @Test
    public void noteCreditReservationFailed() {
    }

    @Test
    public void cancel() {
    }
}