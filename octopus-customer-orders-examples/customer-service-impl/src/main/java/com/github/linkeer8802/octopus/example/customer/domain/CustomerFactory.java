package com.github.linkeer8802.octopus.example.customer.domain;

import com.github.linkeer8802.octopus.core.AbstractAggregateRootFactory;
import com.github.linkeer8802.octopus.core.DomainCreatedEvent;
import com.github.linkeer8802.octopus.core.ModelWithVersion;
import com.github.linkeer8802.octopus.example.customer.datamodel.CreditReservationModel;
import com.github.linkeer8802.octopus.example.customer.datamodel.CustomerModel;
import com.github.linkeer8802.octopus.example.customer.event.CustomerCreatedEvent;
import com.github.linkeer8802.octopus.example.customer.infrastructure.common.util.Moneys;
import com.github.linkeer8802.octopus.example.customer.infrastructure.repository.CustomerCreditReservationEntityRepository;
import com.github.linkeer8802.octopus.example.customer.infrastructure.repository.CustomerEntityRepository;
import com.github.linkeer8802.octopus.example.customer.entity.CustomerEntity;
import com.github.linkeer8802.octopus.spring.annotation.Factory;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author weird
 * @date 2019/12/24
 */
@Factory
public class CustomerFactory extends AbstractAggregateRootFactory<Customer, CustomerModel> {

    @Resource
    private CustomerEntityRepository customerEntityRepository;
    @Resource
    private CustomerCreditReservationEntityRepository customerCreditReservationEntityRepository;

    @Override
    protected DomainCreatedEvent createByModel(CustomerModel model) {
        if (model.getCreditLimit().isNegative()) {
            throw new IllegalStateException("信用额度必须大于0");
        }

        CustomerCreatedEvent customerCreatedEvent = new CustomerCreatedEvent(
                model.getId(), model.getName(), model.getCreditLimit(), model.getCreditReservations());

        return customerCreatedEvent;
    }

    @Override
    protected ModelWithVersion<CustomerModel> loadModel(Serializable id) {
        Optional<CustomerEntity> optionalCustomerEntity = customerEntityRepository.findById(id);
        if (!optionalCustomerEntity.isPresent()) {
            return ModelWithVersion.empty();
        }
        CustomerEntity customerEntity = optionalCustomerEntity.get();

        List<CreditReservationModel> creditReservations = customerCreditReservationEntityRepository
                .findByCustomerId(id.toString()).map(entities -> entities.stream().map(entity ->
                new CreditReservationModel(entity.getId(), entity.getCustomerId(), entity.getOrderId(),
                 Moneys.of(entity.getAmount()))).collect(Collectors.toList())).orElse(new ArrayList<>(0));

        CustomerModel customerModel = new CustomerModel(customerEntity.getId(),
                customerEntity.getName(), Moneys.of(customerEntity.getCreditLimit()), creditReservations);

        return new ModelWithVersion<>(customerModel, customerEntity.getVersion());
    }

    @Override
    public Integer updateVersion(Serializable id, Long oldVersion, Long newVersion) {
        return customerEntityRepository.updateVersion(id, oldVersion, newVersion);
    }
}
