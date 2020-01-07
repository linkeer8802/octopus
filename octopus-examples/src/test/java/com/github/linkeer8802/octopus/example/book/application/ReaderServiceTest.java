package com.github.linkeer8802.octopus.example.book.application;

import com.github.linkeer8802.octopus.core.AggregateRootFactory;
import com.github.linkeer8802.octopus.core.util.Identifiers;
import com.github.linkeer8802.octopus.example.book.domain.book.Book;
import com.github.linkeer8802.octopus.example.book.domain.book.BookStatus;
import com.github.linkeer8802.octopus.example.book.domain.reader.Reader;
import com.github.linkeer8802.octopus.example.book.domain.reader.RecordStatus;
import com.github.linkeer8802.octopus.example.book.infrastructure.common.model.datamodel.BookModel;
import com.github.linkeer8802.octopus.example.book.infrastructure.common.model.datamodel.BorrowedRecordModel;
import com.github.linkeer8802.octopus.example.book.infrastructure.common.model.datamodel.ReaderModel;
import com.github.linkeer8802.octopus.example.transfer.infrastructure.common.util.Moneys;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author weird
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.NONE)
public class ReaderServiceTest {

    @Resource
    private BookService bookService;
    @Resource
    private ReaderService readerService;

    @Resource
    private AggregateRootFactory<Book, BookModel> bookFactory;
    @Resource
    private AggregateRootFactory<Reader, ReaderModel> readerFactory;

    @Test
    public void testCreateReader() {
        String id = Identifiers.uuid();
        String name = "张三";
        List<BorrowedRecordModel> borrowingBookRecords = Collections.emptyList();
        ReaderModel readerModel = new ReaderModel(id, name, Moneys.ZERO, borrowingBookRecords);
        String readerId = readerService.createReader(readerModel);
        Reader reader = readerFactory.load(readerId, Reader.class).get();

        Assert.assertEquals(id, reader.getId());
        Assert.assertEquals(name, reader.getName());
        Assert.assertEquals(1L, reader.getVersion().longValue());
        Assert.assertEquals(borrowingBookRecords, reader.getBorrowingBookRecords());
    }

    @Test
    public void testBorrowBooks() {
        String bookId = createBook(Identifiers.uuid());
        String readerId = createReader();

        Book borrowingBook = bookFactory.load(bookId, Book.class).get();
        Reader borrower = readerFactory.load(readerId, Reader.class).get();
        borrower.borrowBooks(Lists.newArrayList(borrowingBook), LocalDate.now());

        Assert.assertEquals(BookStatus.BORROWED, borrowingBook.getStatus());
        Assert.assertEquals(1, borrower.getBorrowingBookRecords().size());
        Assert.assertNotNull(borrower.getBorrowingBookRecords().get(0).getRecordId());
        Assert.assertNotNull(borrower.getBorrowingBookRecords().get(0).getBorrowDate());
        Assert.assertEquals(bookId, borrower.getBorrowingBookRecords().get(0).getBookId());
        Assert.assertEquals(borrower.getId(), borrower.getBorrowingBookRecords().get(0).getReaderId());
        Assert.assertEquals(RecordStatus.BORROWED, borrower.getBorrowingBookRecords().get(0).getStatus());
    }

    @Test
    public void testReturnBooks() {
        String bookId = createBook(Identifiers.uuid());
        String otherBookId = createBook(Identifiers.uuid());
        String readerId = createReader();

        Reader borrower = readerFactory.load(readerId, Reader.class).get();
        Book book = bookFactory.load(bookId, Book.class).get();
        Book otherBook = bookFactory.load(otherBookId, Book.class).get();

        borrower.borrowBooks(Lists.newArrayList(book, otherBook), LocalDate.now());
        Assert.assertEquals(2, borrower.getBorrowingBookRecords().size());

        borrower.returnBooks(Lists.newArrayList(book), LocalDate.now());
        Assert.assertEquals(1, borrower.getBorrowingBookRecords().size());
        Assert.assertEquals(RecordStatus.BORROWED, borrower.getBorrowingBookRecords().get(0).getStatus());
        Assert.assertEquals(otherBook.getId(), borrower.getBorrowingBookRecords().get(0).getBookId());
    }

    private String createReader() {
        return readerService.createReader(new ReaderModel(Identifiers.uuid(), "张三", Moneys.ZERO, new ArrayList<>()));
    }

    private String createBook(String id) {
        return bookService.createNewBook(new BookModel(id, "Java入门", "155001455545", "人民邮电出版社", "wrd", BookStatus.BORROWABLE));
    }
}