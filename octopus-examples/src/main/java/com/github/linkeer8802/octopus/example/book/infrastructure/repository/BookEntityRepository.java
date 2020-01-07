package com.github.linkeer8802.octopus.example.book.infrastructure.repository;

import com.github.linkeer8802.octopus.example.book.infrastructure.common.model.entity.BookEntity;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.io.Serializable;

/**
 * @author weird
 * @date 2019/11/29
 */
@Repository
public class BookEntityRepository {

    @Resource
    private DSLContext create;

    Table<Record> BOOK = DSL.table("book");
    Field<String> ID = DSL.field("id", String.class);
    Field<String> NAME = DSL.field("name", String.class);
    Field<String> ISBN = DSL.field("isbn", String.class);
    Field<String> PUBLISHER = DSL.field("publisher", String.class);
    Field<String> AUTHOR = DSL.field("author", String.class);
    Field<String> STATUS = DSL.field("status", String.class);
    Field<Long> VERSION = DSL.field("version", Long.class);

    public int insert(BookEntity entity) {
       return create.insertInto(
               BOOK, ID, NAME, ISBN,
               PUBLISHER, AUTHOR, STATUS, VERSION)
               .values(entity.getId(), entity.getName(),
                       entity.getIsbn(), entity.getPublisher(),
                       entity.getAuthor(), entity.getStatus(),
                       entity.getVersion()).execute();
    }

    public BookEntity findById(Serializable id) {
        return create.selectFrom(BOOK).where(ID.equal(id.toString())).fetchOneInto(BookEntity.class);
    }

    public int updateStatus(String id, String status) {
        return create.update(BOOK).set(STATUS, status).where(ID.equal(id)).execute();
    }

    public int updateVersion(Serializable id, Long oldVersion, Long newVersion) {
        return create.update(BOOK).set(VERSION, newVersion).where(ID.equal((String) id).and(VERSION.equal(oldVersion))).execute();
    }
}
