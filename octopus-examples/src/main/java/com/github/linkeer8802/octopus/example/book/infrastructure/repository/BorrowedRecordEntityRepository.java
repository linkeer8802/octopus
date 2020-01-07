package com.github.linkeer8802.octopus.example.book.infrastructure.repository;

import com.github.linkeer8802.octopus.example.book.infrastructure.common.model.entity.BorrowedRecordEntity;
import com.github.linkeer8802.octopus.example.transfer.infrastructure.common.util.Moneys;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.money.MonetaryAmount;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @author weird
 * @date 2019/11/29
 */
@Repository
public class BorrowedRecordEntityRepository {

    @Resource
    private DSLContext create;

    Table<Record> BOOK_BORROWED_RECORD = DSL.table("book_borrowed_record");
    Field<String> ID = DSL.field("id", String.class);
    Field<String> READER_ID = DSL.field("reader_id", String.class);
    Field<String> BOOK_ID = DSL.field("book_id", String.class);
    Field<LocalDate> BORROWING_DATE = DSL.field("borrowing_date", LocalDate.class);
    Field<LocalDate> RETURNING_DATE = DSL.field("returning_date", LocalDate.class);
    Field<Integer> OVERDUE_DAY = DSL.field("overdue_day", Integer.class);
    Field<BigDecimal> OVERDUE_FEE = DSL.field("overdue_fee", BigDecimal.class);
    Field<String> STATUS = DSL.field("status", String.class);

    public int insert(BorrowedRecordEntity entity) {
       return create.insertInto(BOOK_BORROWED_RECORD, ID, READER_ID, BOOK_ID, BORROWING_DATE, STATUS)
                .values(entity.getId(), entity.getReaderId(), entity.getBookId(), entity.getBorrowDate(), entity.getStatus()).execute();
    }

    public int updateRecordToReturn(Serializable id, String status, LocalDate returnDate, Integer overdueDays, MonetaryAmount overdueFee) {
        return create.update(BOOK_BORROWED_RECORD)
                .set(RETURNING_DATE, returnDate)
                .set(STATUS, status)
                .set(OVERDUE_DAY, overdueDays)
                .set(OVERDUE_FEE, Moneys.toBigDecimal(overdueFee))
                .where(ID.equal((String) id)).execute();
    }

    public List<BorrowedRecordEntity> findBorrowingRecordByReaderId(String readerId, String status) {
        return create.selectFrom(BOOK_BORROWED_RECORD).where(READER_ID.eq(readerId).and(STATUS.equal(status))).fetchInto(BorrowedRecordEntity.class);
    }
}
