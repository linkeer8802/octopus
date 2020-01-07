package com.github.linkeer8802.octopus.example.order.event;

import com.github.linkeer8802.octopus.core.DomainEvent;
import com.github.linkeer8802.octopus.example.order.OrderState;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderApprovedEvent extends DomainEvent {
  private String orderId;
  private OrderState state;

  public OrderApprovedEvent(String orderId, OrderState state) {
    this.orderId = orderId;
    this.state = state;
  }
}
