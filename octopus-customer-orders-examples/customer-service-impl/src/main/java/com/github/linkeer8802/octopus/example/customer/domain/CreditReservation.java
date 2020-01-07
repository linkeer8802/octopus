package com.github.linkeer8802.octopus.example.customer.domain;

import com.github.linkeer8802.octopus.example.customer.datamodel.CreditReservationModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.money.MonetaryAmount;

/**
 * 信用预留实体
 * @author weird
 * @date 2019/12/24
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreditReservation {
    private String id;
    private String customerId;
    private String orderId;
    private MonetaryAmount amount;

    public CreditReservation(CreditReservationModel dto) {
        this.id = dto.getId();
        this.customerId = dto.getCustomerId();
        this.orderId = dto.getOrderId();
        this.amount = dto.getAmount();
    }
}
