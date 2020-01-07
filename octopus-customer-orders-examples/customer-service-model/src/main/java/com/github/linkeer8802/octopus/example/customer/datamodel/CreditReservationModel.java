package com.github.linkeer8802.octopus.example.customer.datamodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.money.MonetaryAmount;

/**
 * 信用预留DTO
 * @author weird
 * @date 2019/12/24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditReservationModel {
    private String id;
    private String customerId;
    private String orderId;
    private MonetaryAmount amount;
}
