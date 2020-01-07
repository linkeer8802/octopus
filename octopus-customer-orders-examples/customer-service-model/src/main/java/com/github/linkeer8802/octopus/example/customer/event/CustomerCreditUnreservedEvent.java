package com.github.linkeer8802.octopus.example.customer.event;

import com.github.linkeer8802.octopus.core.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author weird
 * @date 2019/12/24
 */
@Getter
@NoArgsConstructor
public class CustomerCreditUnreservedEvent extends DomainEvent {

    private String creditReservationId;

    public CustomerCreditUnreservedEvent(String creditReservationId) {
        this.creditReservationId = creditReservationId;
    }
}
