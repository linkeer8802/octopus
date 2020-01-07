package com.github.linkeer8802.octopus.example.customer.application;

import com.github.linkeer8802.octopus.core.AbstractDomainService;
import com.github.linkeer8802.octopus.core.AggregateRootFactory;
import com.github.linkeer8802.octopus.core.AggregateRootContainer;
import com.github.linkeer8802.octopus.core.eventbus.OnEvent;
import com.github.linkeer8802.octopus.core.exception.DomainRuntimeException;
import com.github.linkeer8802.octopus.example.customer.domain.Customer;
import com.github.linkeer8802.octopus.example.customer.datamodel.CustomerModel;
import com.github.linkeer8802.octopus.example.customer.event.CustomerCreatedEvent;
import com.github.linkeer8802.octopus.example.customer.event.CustomerCreditReservedEvent;
import com.github.linkeer8802.octopus.example.customer.event.CustomerCreditUnreservedEvent;
import com.github.linkeer8802.octopus.example.customer.exception.CustomerValidationException;
import com.github.linkeer8802.octopus.example.customer.infrastructure.common.util.Moneys;
import com.github.linkeer8802.octopus.example.customer.infrastructure.repository.CustomerCreditReservationEntityRepository;
import com.github.linkeer8802.octopus.example.customer.infrastructure.repository.CustomerEntityRepository;
import com.github.linkeer8802.octopus.example.customer.entity.CustomerCreditReservationEntity;
import com.github.linkeer8802.octopus.example.customer.entity.CustomerEntity;
import com.github.linkeer8802.octopus.example.customer.vo.CustomerVo;
import com.github.linkeer8802.octopus.spring.annotation.DomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.money.MonetaryAmount;

/**
 * @author weird
 * @date 2019/12/25
 */
@Slf4j
@DomainService
public class CustomerService extends AbstractDomainService<Customer, CustomerModel> {

    @Resource
    private CustomerEntityRepository customerRepository;
    @Resource
    private CustomerCreditReservationEntityRepository customerCreditReservationRepository;

    public CustomerService(@Qualifier("customerFactory") AggregateRootFactory<Customer, CustomerModel> factory) {
        super(factory);
    }

    @Transactional
    public CustomerVo createCustomer(CustomerModel dto) {
        Customer customer = create(dto);
        return new CustomerVo(customer.getId(), customer.getName(), Moneys.toBigDecimal(customer.getCreditLimit()));
    }

    @Transactional(rollbackFor = Exception.class)
    public void reserveCredit(String customerId, String orderId, MonetaryAmount total) {
        AggregateRootContainer<Customer> result = load(customerId);
        if (result.isEmpty()) {
            result.throwException(new CustomerValidationException("Customer不存在", orderId));
        } else {
            try {
                result.get().reserveCredit(orderId, total);
            } catch (DomainRuntimeException e) {
                result.throwException(e);
            }
        }
    }

    @OnEvent
    private void onCustomerCreated(CustomerCreatedEvent event) {
        customerRepository.insert(new CustomerEntity(event.getId(), event.getName(),
                Moneys.toBigDecimal(event.getCreditLimit()), event.getAggregateRootVersion()));
    }

    @OnEvent
    private void onCreditReserved(CustomerCreditReservedEvent event) {
        customerCreditReservationRepository.insert(new CustomerCreditReservationEntity(
                event.getId(), event.getCustomerId(), event.getOrderId(), Moneys.toBigDecimal(event.getAmount())));
    }

    @OnEvent
    private void onCustomerCreditUnreserved(CustomerCreditUnreservedEvent event) {
        customerCreditReservationRepository.delete(event.getCreditReservationId());
    }
}
