package com.github.linkeer8802.octopus.example.book.domain.book;

import com.github.linkeer8802.octopus.core.AggregateRootFactory;
import com.github.linkeer8802.octopus.core.InMemoryFactoryDecorator;
import com.github.linkeer8802.octopus.core.util.Identifiers;
import com.github.linkeer8802.octopus.example.book.infrastructure.common.model.datamodel.BookModel;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

/**
 * @author weird
 * @date 2019/11/27
 */
public class BookTest {

    private static AggregateRootFactory<Book, BookModel> bookFactory = new InMemoryFactoryDecorator<>(new BookFactory());

    @Test
    public void testBookCreate() {
        String id = Identifiers.uuid();
        String name = "Java入门";
        String isbn = "155001455545";
        String author = "Tom";
        String publisher = "人民邮电出版社";
        BookStatus status = BookStatus.BORROWABLE;

        BookModel dto = new BookModel(id, name, isbn, publisher, author, status);
        Book book = bookFactory.create(dto, Book.class);

        Assert.assertEquals(book.getId(), id);
        Assert.assertEquals(book.getName(), name);
        Assert.assertEquals(book.getIsbn(), isbn);
        Assert.assertEquals(book.getPublisher(), publisher);
        Assert.assertEquals(book.getAuthor(), author);
        Assert.assertEquals(book.getStatus(), BookStatus.BORROWABLE);
        Assert.assertEquals(book.getVersion(), Long.valueOf(1L));
    }

    @Test
    public void testBorrow() {
        String id = Identifiers.uuid();
        createBook(id);
        getBook(id).borrowBook();
        Assert.assertEquals(getBook(id).getStatus(), BookStatus.BORROWED);
    }

    @Test(expected = IllegalStateException.class)
    public void testReturnException() {
        String id = Identifiers.uuid();
        createBook(id);
        getBook(id).returnBook();
    }

    @Test
    public void testReturn() {
        String id = Identifiers.uuid();
        createBook(id);
        getBook(id).borrowBook();
        getBook(id).returnBook();
        Assert.assertEquals(getBook(id).getStatus(), BookStatus.BORROWABLE);
    }

    private Book getBook(String id) {
        return bookFactory.load(id, Book.class).get();
    }

    private void createBook(String id) {
        BookModel dto = new BookModel(id, "Java入门", "155001455545", "人民邮电出版社", "wrd", BookStatus.BORROWABLE);
        bookFactory.create(dto, Book.class);
    }
}