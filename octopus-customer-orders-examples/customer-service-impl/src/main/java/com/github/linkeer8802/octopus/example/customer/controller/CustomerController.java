package com.github.linkeer8802.octopus.example.customer.controller;

import com.github.linkeer8802.octopus.core.util.Identifiers;
import com.github.linkeer8802.octopus.example.customer.application.CustomerService;
import com.github.linkeer8802.octopus.example.customer.datamodel.CustomerModel;
import com.github.linkeer8802.octopus.example.customer.infrastructure.common.util.Moneys;
import com.github.linkeer8802.octopus.example.customer.vo.CustomerVo;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * @author weird
 * @date 2019/12/25
 */
@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Resource
    private CustomerService customerService;

    @PostMapping("{name}/{creditLimit}")
    public CustomerVo createCustomer(@PathVariable String name, @PathVariable BigDecimal creditLimit) {
        return customerService.createCustomer(new CustomerModel(
                Identifiers.uuid(), name, Moneys.of(creditLimit), new ArrayList<>(0)));
    }
}
