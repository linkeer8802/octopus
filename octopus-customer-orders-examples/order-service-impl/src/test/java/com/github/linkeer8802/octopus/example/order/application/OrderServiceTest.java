package com.github.linkeer8802.octopus.example.order.application;

import com.github.linkeer8802.octopus.core.AggregateRootFactory;
import com.github.linkeer8802.octopus.core.util.Identifiers;
import com.github.linkeer8802.octopus.example.order.OrderState;
import com.github.linkeer8802.octopus.example.order.domain.Order;
import com.github.linkeer8802.octopus.example.order.datamodel.OrderModel;
import com.github.linkeer8802.octopus.example.order.infrastructure.common.util.Moneys;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import javax.money.MonetaryAmount;

/**
 * @author weird
 * @date 2019/12/25
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.NONE)
public class OrderServiceTest {

    @Resource
    private OrderService orderService;
    @Resource(name = "orderFactory")
    private AggregateRootFactory<Order, OrderModel> orderFactory;

    @Test
    public void createOrder() {
        String id = Identifiers.uuid();
        String customerId = Identifiers.uuid();
        MonetaryAmount total = Moneys.of(100.00);
        OrderState state = OrderState.PENDING;
        OrderModel dto = new OrderModel(id, customerId, total, state);
        String orderId = orderService.createOrder(dto).getId();
        Order order = orderFactory.load(orderId, Order.class).get();

        Assert.assertEquals(order.getId(), id);
        Assert.assertEquals(order.getCustomerId(), customerId);
        Assert.assertEquals(order.getTotal(), total);
        Assert.assertEquals(order.getState(), state);
        Assert.assertEquals(order.getVersion(), Long.valueOf(1L));
    }
}