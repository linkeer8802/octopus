package com.github.linkeer8802.octopus.example.customer.domain;

import com.github.linkeer8802.octopus.core.AggregateRootFactory;
import com.github.linkeer8802.octopus.core.InMemoryFactoryDecorator;
import com.github.linkeer8802.octopus.core.util.Identifiers;
import com.github.linkeer8802.octopus.example.customer.datamodel.CreditReservationModel;
import com.github.linkeer8802.octopus.example.customer.exception.CustomerCreditLimitExceededException;
import com.github.linkeer8802.octopus.example.customer.infrastructure.common.util.Moneys;
import com.github.linkeer8802.octopus.example.customer.datamodel.CustomerModel;
import org.junit.Assert;
import org.junit.Test;

import javax.money.MonetaryAmount;
import java.util.ArrayList;

/**
 * @author weird
 * @date 2019/12/24
 */
public class CustomerTest {

    private static AggregateRootFactory<Customer, CustomerModel> factory = new InMemoryFactoryDecorator<>(new CustomerFactory());

    @Test
    public void testCreateCustomer() {
        String id = Identifiers.uuid();
        String name = "张三";
        MonetaryAmount creditLimit = Moneys.of(1000.00);
        ArrayList<CreditReservationModel> creditReservations = new ArrayList<>(0);
        CustomerModel dto = new CustomerModel(id, name, creditLimit, creditReservations);
        Customer customer = factory.create(dto, Customer.class);

        Assert.assertEquals(customer.getId(), id);
        Assert.assertEquals(customer.getName(), name);
        Assert.assertEquals(customer.getCreditLimit(), creditLimit);
        Assert.assertEquals(customer.getVersion(), Long.valueOf(1L));
    }

    @Test
    public void testReserveCredit() {
        String customerId = createCustomer();
        String orderId = Identifiers.uuid();
        getCustomer(customerId).reserveCredit(orderId, Moneys.of(100));

        Assert.assertEquals(1, getCustomer(customerId).getCreditReservations().size());
        Assert.assertEquals(Moneys.of(900), getCustomer(customerId).availableCredit());
    }

    @Test(expected = CustomerCreditLimitExceededException.class)
    public void testReserveCreditWithException() {
        String customerId = createCustomer();
        String orderId = Identifiers.uuid();

        getCustomer(customerId).reserveCredit(orderId, Moneys.of(10000));
    }

    @Test
    public void testUnreserveCredit() {
        String customerId = createCustomer();
        String orderId = Identifiers.uuid();
        String otherOrderId = Identifiers.uuid();
        getCustomer(customerId).reserveCredit(orderId, Moneys.of(100));
        getCustomer(customerId).reserveCredit(otherOrderId, Moneys.of(200));

        getCustomer(customerId).unreserveCredit(orderId);
        Assert.assertEquals(1, getCustomer(customerId).getCreditReservations().size());
        Assert.assertEquals(Moneys.of(800), getCustomer(customerId).availableCredit());

        getCustomer(customerId).unreserveCredit(otherOrderId);
        Assert.assertEquals(0, getCustomer(customerId).getCreditReservations().size());
        Assert.assertEquals(Moneys.of(1000), getCustomer(customerId).availableCredit());
    }

    private String createCustomer() {
        String id = Identifiers.uuid();
        String name = "张三";
        MonetaryAmount creditLimit = Moneys.of(1000.00);
        ArrayList<CreditReservationModel> creditReservations = new ArrayList<>(0);
        CustomerModel dto = new CustomerModel(id, name, creditLimit, creditReservations);
        return factory.create(dto, Customer.class).getId();
    }

    private Customer getCustomer(String customerId) {
        return factory.load(customerId, Customer.class).get();
    }
}