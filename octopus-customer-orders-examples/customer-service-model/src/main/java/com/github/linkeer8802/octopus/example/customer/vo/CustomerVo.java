package com.github.linkeer8802.octopus.example.customer.vo;

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
public class CustomerVo {
    private String id;
    private String name;
    private BigDecimal creditLimit;
}
