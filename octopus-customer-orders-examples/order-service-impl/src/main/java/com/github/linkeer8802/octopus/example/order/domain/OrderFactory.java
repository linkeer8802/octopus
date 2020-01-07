package com.github.linkeer8802.octopus.example.order.domain;

import com.github.linkeer8802.octopus.core.AbstractAggregateRootFactory;
import com.github.linkeer8802.octopus.core.DomainCreatedEvent;
import com.github.linkeer8802.octopus.core.ModelWithVersion;
import com.github.linkeer8802.octopus.example.order.datamodel.OrderModel;
import com.github.linkeer8802.octopus.example.order.event.OrderCreatedEvent;
import com.github.linkeer8802.octopus.example.order.infrastructure.common.util.Moneys;
import com.github.linkeer8802.octopus.example.order.infrastructure.repository.OrderEntityRepository;
import com.github.linkeer8802.octopus.example.order.entity.OrderEntity;
import com.github.linkeer8802.octopus.spring.annotation.Factory;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Optional;

/**
 * @author weird
 * @date 2019/12/24
 */
@Factory
public class OrderFactory extends AbstractAggregateRootFactory<Order, OrderModel> {

    @Resource
    private OrderEntityRepository orderEntityRepository;

    @Override
    protected DomainCreatedEvent createByModel(OrderModel model) {
        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(
                model.getId(), model.getCustomerId(), model.getTotal(), model.getState());

        return orderCreatedEvent;
    }

    @Override
    protected ModelWithVersion<OrderModel> loadModel(Serializable id) {
        Optional<OrderEntity> orderEntityOptional = orderEntityRepository.findById(id);
        if (orderEntityOptional.isPresent()) {
            OrderEntity orderEntity = orderEntityOptional.get();
            OrderModel orderModel = new OrderModel(orderEntity.getId(), orderEntity.getCustomerId(), Moneys.of(orderEntity.getTotal()), orderEntity.getState());
            return new ModelWithVersion<>(orderModel, orderEntity.getVersion());
        } else {
            return ModelWithVersion.empty();
        }
    }

    @Override
    public Integer updateVersion(Serializable id, Long oldVersion, Long newVersion) {
        return orderEntityRepository.updateVersion(id, oldVersion, newVersion);
    }
}
