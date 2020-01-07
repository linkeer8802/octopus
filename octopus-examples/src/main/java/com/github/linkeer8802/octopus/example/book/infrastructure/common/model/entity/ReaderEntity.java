package com.github.linkeer8802.octopus.example.book.infrastructure.common.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author weird
 * @date 2019/12/16
 */
@Data
@AllArgsConstructor
public class ReaderEntity {
    private String id;
    private String name;
    private BigDecimal unpaidFine;
    private Long version;

    public ReaderEntity(String id, String name, Long version) {
        this.id = id;
        this.name = name;
        this.version = version;
    }
}
