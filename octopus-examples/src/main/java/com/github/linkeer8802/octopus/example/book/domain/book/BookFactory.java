package com.github.linkeer8802.octopus.example.book.domain.book;

import com.github.linkeer8802.octopus.core.AbstractAggregateRootFactory;
import com.github.linkeer8802.octopus.core.DomainCreatedEvent;
import com.github.linkeer8802.octopus.core.ModelWithVersion;
import com.github.linkeer8802.octopus.example.book.infrastructure.common.model.event.BookCreatedEvent;
import com.github.linkeer8802.octopus.example.book.infrastructure.repository.BookEntityRepository;
import com.github.linkeer8802.octopus.example.book.infrastructure.common.model.datamodel.BookModel;
import com.github.linkeer8802.octopus.example.book.infrastructure.common.model.entity.BookEntity;
import com.github.linkeer8802.octopus.spring.annotation.Factory;

import javax.annotation.Resource;
import java.io.Serializable;

@Factory
public class BookFactory extends AbstractAggregateRootFactory<Book, BookModel> {

    @Resource
    private BookEntityRepository repository;

    @Override
    public DomainCreatedEvent createByModel(BookModel model) {
        return new BookCreatedEvent(model.getId(), model.getName(),
                model.getIsbn(), model.getPublisher(), model.getAuthor(), model.getStatus());
    }

    @Override
    public ModelWithVersion<BookModel> loadModel(Serializable id) {
        BookEntity entity = repository.findById(id);
        BookModel dto = new BookModel(entity.getId(), entity.getName(), entity.getIsbn(),
                entity.getPublisher(), entity.getAuthor(), BookStatus.valueOf(entity.getStatus()));
        return new ModelWithVersion<>(dto, entity.getVersion());
    }

    @Override
    public Integer updateVersion(Serializable id, Long oldVersion, Long newVersion) {
        return repository.updateVersion(id, oldVersion, newVersion);
    }
}

