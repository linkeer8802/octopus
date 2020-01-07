package com.github.linkeer8802.octopus.example.book.application;

import com.github.linkeer8802.octopus.core.util.Identifiers;
import com.github.linkeer8802.octopus.example.book.domain.book.BookStatus;
import com.github.linkeer8802.octopus.example.book.infrastructure.common.model.datamodel.BookModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * @author weird
 * @date 2019/11/28
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.NONE)
public class BookServiceTest {

    @Resource
    private BookService bookService;

    @Test
    public void createNewBook() {
        String id = Identifiers.uuid();
        BookModel dto = new BookModel(id, "Java入门", "155001455545", "人民邮电出版社", "wrd", BookStatus.BORROWABLE);
        String newBookId = bookService.createNewBook(dto);
    }
}