package com.github.linkeer8802.octopus.example.transfer.controller;

import com.github.linkeer8802.octopus.core.util.Identifiers;
import com.github.linkeer8802.octopus.example.transfer.application.BankAccountService;
import com.github.linkeer8802.octopus.example.transfer.infrastructure.common.util.Moneys;
import com.google.common.collect.ImmutableMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.money.MonetaryAmount;
import java.util.UUID;

@RestController
@RequestMapping
public class BankAccountController {

    @Resource
    private BankAccountService bankAccountService;

    @GetMapping("/returnBook")
    public void returnBook() {
        String id = Identifiers.uuid();
        String name = "张三";
        MonetaryAmount balance = Moneys.of(1000.00);
        bankAccountService.openBankAccount(ImmutableMap.of("id", id, "name", name, "balance", balance));
    }
}
