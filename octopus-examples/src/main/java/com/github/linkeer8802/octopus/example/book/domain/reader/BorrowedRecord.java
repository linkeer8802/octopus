package com.github.linkeer8802.octopus.example.book.domain.reader;

import com.github.linkeer8802.octopus.example.book.infrastructure.common.model.datamodel.BorrowedRecordModel;

import java.time.LocalDate;

/**
 * 借书记录实体
 * @author weird
 */
public class BorrowedRecord extends BorrowedRecordModel {
    /**
     * 借书期限
     */
    public static final int DAY_OF_BORROW_PERIOD = 30;

    BorrowedRecord(BorrowedRecordModel borrowedRecordModel) {
       super(borrowedRecordModel);
    }

    BorrowedRecord(String recordId, String readerId, String bookId, LocalDate borrowDate) {
        this.recordId = recordId;
        this.bookId = bookId;
        this.readerId = readerId;
        this.borrowDate = borrowDate;
        this.status = RecordStatus.BORROWED;
        this.shouldReturnDate = shouldReturnDate();
    }

    /**
     * 根据借书日期计算应还日期
     */
    private LocalDate shouldReturnDate() {
        return borrowDate.plusDays(DAY_OF_BORROW_PERIOD);
    }
}
