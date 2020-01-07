package com.github.linkeer8802.octopus.example.customer.entity;

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
public class CustomerCreditReservationEntity {
    private String id;
    private String customerId;
    private String orderId;
    private BigDecimal amount;
}
