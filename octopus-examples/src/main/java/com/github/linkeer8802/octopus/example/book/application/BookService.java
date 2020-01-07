package com.github.linkeer8802.octopus.example.book.application;

import com.github.linkeer8802.octopus.core.AbstractDomainService;
import com.github.linkeer8802.octopus.core.AggregateRootFactory;
import com.github.linkeer8802.octopus.core.eventbus.OnEvent;
import com.github.linkeer8802.octopus.example.book.domain.book.Book;
import com.github.linkeer8802.octopus.example.book.infrastructure.common.model.event.BookCreatedEvent;
import com.github.linkeer8802.octopus.example.book.infrastructure.repository.BookEntityRepository;
import com.github.linkeer8802.octopus.example.book.infrastructure.common.model.datamodel.BookModel;
import com.github.linkeer8802.octopus.example.book.infrastructure.common.model.entity.BookEntity;
import com.github.linkeer8802.octopus.spring.annotation.DomainService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author admin
 */
@DomainService
public class BookService extends AbstractDomainService<Book, BookModel> {

    @Resource
    private BookEntityRepository repository;

    public BookService(@Qualifier("bookFactory") AggregateRootFactory<Book, BookModel> factory) {
        super(factory);
    }

    @Transactional(rollbackFor = Exception.class)
    public String createNewBook(BookModel bookDto) {
        return create(bookDto).getId();
    }

    @OnEvent
    private void onBookCreated(BookCreatedEvent event) {
        repository.insert(BookEntity.builder()
                .id(event.getId()).name(event.name).isbn(event.isbn).publisher(event.publisher)
                .author(event.author).status(event.status.name()).version(event.getAggregateRootVersion()).build());
    }
}
