package com.github.linkeer8802.octopus.example.book.domain.reader;

import com.github.linkeer8802.octopus.core.AggregateRootFactory;
import com.github.linkeer8802.octopus.core.InMemoryFactoryDecorator;
import com.github.linkeer8802.octopus.core.util.Identifiers;
import com.github.linkeer8802.octopus.example.book.domain.book.Book;
import com.github.linkeer8802.octopus.example.book.domain.book.BookFactory;
import com.github.linkeer8802.octopus.example.book.domain.book.BookStatus;
import com.github.linkeer8802.octopus.example.book.infrastructure.common.model.datamodel.BookModel;
import com.github.linkeer8802.octopus.example.book.infrastructure.common.model.datamodel.BorrowedRecordModel;
import com.github.linkeer8802.octopus.example.book.infrastructure.common.model.datamodel.ReaderModel;
import com.github.linkeer8802.octopus.example.transfer.infrastructure.common.util.Moneys;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReaderTest {

    private static AggregateRootFactory<Book, BookModel> bookFactory = new InMemoryFactoryDecorator<>(new BookFactory());
    private static AggregateRootFactory<Reader, ReaderModel> readerFactory = new InMemoryFactoryDecorator<>(new ReaderFactory());

    @Test
    public void testCreateReader() {
        String id = Identifiers.uuid();
        String name = "张三";
        List<BorrowedRecordModel> borrowingBookRecords = Collections.emptyList();
        ReaderModel dto = new ReaderModel(id, name, Moneys.ZERO, borrowingBookRecords);
        Reader reader = readerFactory.create(dto, Reader.class);

        Assert.assertEquals(id, reader.getId());
        Assert.assertEquals(name, reader.getName());
        Assert.assertEquals(borrowingBookRecords, reader.getBorrowingBookRecords());
    }

    @Test
    public void testBorrowBooks() {
        Book book = createBook(Identifiers.uuid());
        Reader reader = createReader();
        reader.borrowBooks(Lists.newArrayList(book), LocalDate.now());

        Assert.assertEquals(BookStatus.BORROWED, book.getStatus());
        Assert.assertEquals(1, reader.getBorrowingBookRecords().size());
        Assert.assertNotNull(reader.getBorrowingBookRecords().get(0).getRecordId());
        Assert.assertNotNull(reader.getBorrowingBookRecords().get(0).getBorrowDate());
        Assert.assertEquals(book.getId(), reader.getBorrowingBookRecords().get(0).getBookId());
        Assert.assertEquals(reader.getId(), reader.getBorrowingBookRecords().get(0).getReaderId());
        Assert.assertEquals(RecordStatus.BORROWED, reader.getBorrowingBookRecords().get(0).getStatus());
    }

    @Test(expected = IllegalStateException.class)
    public void testReturnBooksWithException() {
        Book book = createBook(Identifiers.uuid());
        Reader reader = createReader();
        reader.returnBooks(Lists.newArrayList(book), LocalDate.now());
    }

    @Test
    public void testReturnBooks() {
        String bookId = Identifiers.uuid();
        Book book = createBook(bookId);
        String otherBookId = Identifiers.uuid();
        Book otherBook = createBook(otherBookId);

        Reader reader = createReader();
        reader.borrowBooks(Lists.newArrayList(book, otherBook), LocalDate.now());
        Assert.assertEquals(2, reader.getBorrowingBookRecords().size());

        reader.returnBooks(Lists.newArrayList(book), LocalDate.now());
        Assert.assertEquals(1, reader.getBorrowingBookRecords().size());
        Assert.assertEquals(RecordStatus.BORROWED, reader.getBorrowingBookRecords().get(0).getStatus());
        Assert.assertEquals(otherBook.getId(), reader.getBorrowingBookRecords().get(0).getBookId());
    }

    @Test
    public void testOverdueReturnBooks() {
        String bookId = Identifiers.uuid();
        Book book = createBook(bookId);
        String otherBookId = Identifiers.uuid();
        Book otherBook = createBook(otherBookId);

        Reader reader = createReader();
        reader.borrowBooks(Lists.newArrayList(book, otherBook), LocalDate.now());

        //超期2天
        reader.returnBooks(Lists.newArrayList(book), LocalDate.now().plusDays(BorrowedRecord.DAY_OF_BORROW_PERIOD + 2));
        Assert.assertEquals(Moneys.of(0.1), reader.getUnpaidFine());
        Assert.assertEquals(1, reader.getBorrowingBookRecords().size());
        //超期32天
        reader.returnBooks(Lists.newArrayList(otherBook), LocalDate.now().plusDays(BorrowedRecord.DAY_OF_BORROW_PERIOD + 32));
        Assert.assertEquals(Moneys.of(0.1 + 1.7), reader.getUnpaidFine());
        Assert.assertEquals(0, reader.getBorrowingBookRecords().size());
    }

    private Reader createReader() {
        ReaderModel dto = new ReaderModel(Identifiers.uuid(), "张三", Moneys.ZERO, new ArrayList<>());
        return readerFactory.create(dto, Reader.class);
    }

    private Book createBook(String id) {
        BookModel dto = new BookModel(id, "Java入门", "155001455545", "人民邮电出版社", "wrd", BookStatus.BORROWABLE);
        return bookFactory.create(dto, Book.class);
    }
}