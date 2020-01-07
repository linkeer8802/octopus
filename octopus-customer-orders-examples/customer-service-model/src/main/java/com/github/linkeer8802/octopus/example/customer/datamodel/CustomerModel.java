package com.github.linkeer8802.octopus.example.customer.datamodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.money.MonetaryAmount;
import java.util.List;

/**
 * @author weird
 * @date 2019/12/24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerModel {
    private String id;
    private String name;
    private MonetaryAmount creditLimit;
    private List<CreditReservationModel> creditReservations;
}
