package com.github.linkeer8802.octopus.example.customer.event;

import com.github.linkeer8802.octopus.core.DomainCreatedEvent;
import com.github.linkeer8802.octopus.example.customer.datamodel.CreditReservationModel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.money.MonetaryAmount;
import java.util.List;

/**
 * @author weird
 * @date 2019/12/24
 */
@Getter
@NoArgsConstructor
public class CustomerCreatedEvent extends DomainCreatedEvent<String> {
    private String id;
    private String name;
    private MonetaryAmount creditLimit;
    private List<CreditReservationModel> creditReservations;

    public CustomerCreatedEvent(String id, String name, MonetaryAmount creditLimit, List<CreditReservationModel> creditReservations) {
        this.id = id;
        this.name = name;
        this.creditLimit = creditLimit;
        this.creditReservations = creditReservations;
    }
}
