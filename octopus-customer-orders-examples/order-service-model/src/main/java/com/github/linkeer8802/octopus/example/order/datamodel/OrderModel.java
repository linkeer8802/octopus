package com.github.linkeer8802.octopus.example.order.datamodel;

import com.github.linkeer8802.octopus.example.order.OrderState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.money.MonetaryAmount;

/**
 * @author weird
 * @date 2019/12/25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderModel {
    private String id;
    private String customerId;
    private MonetaryAmount total;
    private OrderState state;
}
