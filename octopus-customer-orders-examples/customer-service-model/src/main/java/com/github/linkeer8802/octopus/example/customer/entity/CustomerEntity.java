package com.github.linkeer8802.octopus.example.customer.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author weird
 * @date 2019/12/25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerEntity {
    private String id;
    private String name;
    private BigDecimal creditLimit;
    private Long version;
}
