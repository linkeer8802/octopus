package com.github.linkeer8802.octopus.example.book.infrastructure.common.model.event;

import com.github.linkeer8802.octopus.core.DomainCreatedEvent;
import com.github.linkeer8802.octopus.example.book.domain.book.BookStatus;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class BookCreatedEvent extends DomainCreatedEvent<String> {
    public final String id;
    public final String name;
    public final String isbn;
    public final String publisher;
    public final String author;
    public final BookStatus status;

    public BookCreatedEvent(String id, String name, String isbn, String publisher, String author, BookStatus status) {
        this.id = id;
        this.name = name;
        this.isbn = isbn;
        this.publisher = publisher;
        this.author = author;
        this.status = status;
    }
}
