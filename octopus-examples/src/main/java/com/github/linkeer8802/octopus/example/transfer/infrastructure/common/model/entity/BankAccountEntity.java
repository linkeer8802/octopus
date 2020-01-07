package com.github.linkeer8802.octopus.example.transfer.infrastructure.common.model.entity;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author weird
 * @date 2019/11/29
 */
@Data
@Builder
public class BankAccountEntity {
    private String id;
    private String name;
    private BigDecimal balance;
    private Long version;
}
