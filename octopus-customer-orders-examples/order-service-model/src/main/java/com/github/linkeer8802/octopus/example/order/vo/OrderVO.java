package com.github.linkeer8802.octopus.example.order.vo;

import com.github.linkeer8802.octopus.example.order.OrderState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author weird
 * @date 2019/12/25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderVO {
    private String id;
    private String customerId;
    private BigDecimal total;
    private OrderState state;
}
