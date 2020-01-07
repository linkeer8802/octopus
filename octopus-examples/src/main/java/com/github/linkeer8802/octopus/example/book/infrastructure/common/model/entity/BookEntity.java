package com.github.linkeer8802.octopus.example.book.infrastructure.common.model.entity;

import lombok.Builder;
import lombok.Data;

/**
 * @author weird
 * @date 2019/11/29
 */
@Data
@Builder
public class BookEntity {
    private String id;
    private String name;
    private String isbn;
    private String publisher;
    private String author;
    private String status;
    private Long version;
}
