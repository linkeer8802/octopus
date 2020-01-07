package com.github.linkeer8802.octopus.example.book.infrastructure.common.model.datamodel;

import com.github.linkeer8802.octopus.example.book.domain.book.BookStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author weird
 * @date 2019/12/16
 */
@Data
@AllArgsConstructor
public class BookModel {
    private String id;
    private String name;
    private String isbn;
    private String publisher;
    private String author;
    private BookStatus status;
}
