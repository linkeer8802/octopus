package com.github.linkeer8802.octopus.example.order.controller;

import com.github.linkeer8802.octopus.core.util.Identifiers;
import com.github.linkeer8802.octopus.example.order.OrderState;
import com.github.linkeer8802.octopus.example.order.application.OrderService;
import com.github.linkeer8802.octopus.example.order.datamodel.OrderModel;
import com.github.linkeer8802.octopus.example.order.infrastructure.common.util.Moneys;
import com.github.linkeer8802.octopus.example.order.vo.OrderVO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @author weird
 * @date 2019/12/25
 */
@RestController
@RequestMapping("/orders")
public class OrderController {

    @Resource
    private OrderService orderService;

    @PostMapping("{customerId}/{orderTotal}")
    public OrderVO createOrder(@PathVariable String customerId, @PathVariable BigDecimal orderTotal) {
        return orderService.createOrder(new OrderModel(
                Identifiers.uuid(), customerId, Moneys.of(orderTotal), OrderState.PENDING));
    }
}
