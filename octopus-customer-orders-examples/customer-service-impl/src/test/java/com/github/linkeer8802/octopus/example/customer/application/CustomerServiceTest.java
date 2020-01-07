package com.github.linkeer8802.octopus.example.customer.application;

import com.github.linkeer8802.octopus.core.AggregateRootFactory;
import com.github.linkeer8802.octopus.core.util.Identifiers;
import com.github.linkeer8802.octopus.example.customer.datamodel.CreditReservationModel;
import com.github.linkeer8802.octopus.example.customer.datamodel.CustomerModel;
import com.github.linkeer8802.octopus.example.customer.domain.Customer;
import com.github.linkeer8802.octopus.example.customer.infrastructure.common.util.Moneys;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import javax.money.MonetaryAmount;
import java.util.ArrayList;

/**
 * @author weird
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.NONE)
public class CustomerServiceTest {

    @Resource
    private CustomerService customerService;

    @Resource(name = "customerFactory")
    private AggregateRootFactory<Customer, CustomerModel> customerFactory;

    @Test
    public void createCustomer() {
        String id = Identifiers.uuid();
        String name = "张三";
        MonetaryAmount creditLimit = Moneys.of(1000.00);
        ArrayList<CreditReservationModel> creditReservations = new ArrayList<>(0);
        CustomerModel dto = new CustomerModel(id, name, creditLimit, creditReservations);
        String customerId = customerService.createCustomer(dto).getId();
        Customer customer = customerFactory.load(customerId, Customer.class).get();

        Assert.assertEquals(customer.getId(), id);
        Assert.assertEquals(customer.getName(), name);
        Assert.assertEquals(customer.getCreditLimit(), creditLimit);
        Assert.assertEquals(customer.getVersion(), Long.valueOf(1L));
    }
}