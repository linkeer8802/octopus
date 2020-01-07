package com.github.linkeer8802.octopus.example.book.application;

import com.github.linkeer8802.octopus.core.AbstractDomainService;
import com.github.linkeer8802.octopus.core.AggregateRootFactory;
import com.github.linkeer8802.octopus.core.eventbus.OnEvent;
import com.github.linkeer8802.octopus.example.book.domain.book.Book;
import com.github.linkeer8802.octopus.example.book.domain.reader.Reader;
import com.github.linkeer8802.octopus.example.book.infrastructure.common.model.event.BookBorrowRecordCreatedEvent;
import com.github.linkeer8802.octopus.example.book.infrastructure.common.model.event.BookReturnRecordCreatedEvent;
import com.github.linkeer8802.octopus.example.book.infrastructure.common.model.event.ReaderCreatedEvent;
import com.github.linkeer8802.octopus.example.book.infrastructure.repository.BorrowedRecordEntityRepository;
import com.github.linkeer8802.octopus.example.book.infrastructure.repository.ReaderEntityRepository;
import com.github.linkeer8802.octopus.example.book.infrastructure.common.model.datamodel.BookModel;
import com.github.linkeer8802.octopus.example.book.infrastructure.common.model.datamodel.ReaderModel;
import com.github.linkeer8802.octopus.example.book.infrastructure.common.model.entity.BorrowedRecordEntity;
import com.github.linkeer8802.octopus.example.book.infrastructure.common.model.entity.ReaderEntity;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author admin
 */
@Service
public class ReaderService extends AbstractDomainService<Reader, ReaderModel> {

    @Resource
    private AggregateRootFactory<Book, BookModel> bookFactory;

    @Resource
    private ReaderEntityRepository readerEntityRepository;
    @Resource
    private BorrowedRecordEntityRepository borrowedRecordEntityRepository;

    public ReaderService(@Qualifier("readerFactory") AggregateRootFactory<Reader, ReaderModel> factory) {
        super(factory);
    }

    @Transactional(rollbackFor = Exception.class)
    public String createReader(ReaderModel dto) {
        return create(dto).getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public void borrowBooks(String readerId, List<String> bookIds, LocalDate borrowDate) {
        List<Book> books = bookIds.stream().map(bookId -> bookFactory.load(bookId, Book.class).get()).collect(Collectors.toList());
        load(readerId).get().borrowBooks(books, borrowDate);
    }

    @Transactional(rollbackFor = Exception.class)
    public void returnBooks(String readerId, List<String> bookIds, LocalDate returnDate) {
        List<Book> books = bookIds.stream().map(bookId -> bookFactory.load(bookId, Book.class).get()).collect(Collectors.toList());
        load(readerId).get().returnBooks(books, returnDate);
    }

    @OnEvent
    public void onReaderCreated(ReaderCreatedEvent event) {
        readerEntityRepository.insert(new ReaderEntity(event.getId(), event.getDataModel().getName(), event.getAggregateRootVersion()));
    }

    @OnEvent
    public void onBookBorrowRecordCreated(BookBorrowRecordCreatedEvent event) {
        event.getBorrowingRecords().forEach(record -> borrowedRecordEntityRepository.insert(
                new BorrowedRecordEntity(record.getRecordId(), record.getReaderId(),
                        record.getBookId(), record.getBorrowDate(), record.getStatus().name())));
    }

    @OnEvent
    public void onBookReturnRecordCreated(BookReturnRecordCreatedEvent event) {
        event.getReturnedRecords().forEach(record -> borrowedRecordEntityRepository
                .updateRecordToReturn(record.getRecordId(), record.getStatus().name(),
                        record.getReturnDate(), record.getOverdueDays(), record.getOverdueFee()));

        readerEntityRepository.updateUnpaidFine(event.getReturnedRecords().get(0).getReaderId(), event.getFine());
    }
}
