package com.github.linkeer8802.octopus.example.book.domain.book;

import com.github.linkeer8802.octopus.core.EventSourcingAggregateRoot;
import com.github.linkeer8802.octopus.core.eventbus.OnEvent;
import com.github.linkeer8802.octopus.example.book.infrastructure.common.model.event.BookBorrowedEvent;
import com.github.linkeer8802.octopus.example.book.infrastructure.common.model.event.BookCreatedEvent;
import com.github.linkeer8802.octopus.example.book.infrastructure.common.model.event.BookReturnedEvent;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class Book extends EventSourcingAggregateRoot<Book, String> {
    private String name;
    private String isbn;
    private String publisher;
    private String author;
    private BookStatus status;

    private Book(){}
    /**
     * 借出
     */
    public void borrowBook() {
        if (BookStatus.BORROWABLE.equals(status)) {
            publishEvent(new BookBorrowedEvent(BookStatus.BORROWED));
        } else {
            throw new IllegalStateException(String.format("图书已被借出。%s", this));
        }
    }

    /**
     * 归还
     */
    public void returnBook() {
        if (BookStatus.BORROWED.equals(status)) {
            publishEvent(new BookReturnedEvent(BookStatus.BORROWABLE));
        } else {
            throw new IllegalStateException(String.format("图书未被借出，无法归还。%s", this));
        }
    }

    @OnEvent
    private void onBookCreated(BookCreatedEvent event) {
        this.id = event.getId();
        this.isbn = event.isbn;
        this.name = event.name;
        this.status = event.status;
        this.author = event.author;
        this.publisher = event.publisher;
    }

    @OnEvent
    private void onBookBorrowed(BookBorrowedEvent event) {
        status = event.status;
    }

    @OnEvent
    private void onBookReturned(BookReturnedEvent event) {
        status = event.status;
    }
}
