package com.github.linkeer8802.octopus.example.book.infrastructure.common.model.datamodel;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.linkeer8802.octopus.example.book.domain.reader.RecordStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @author weird
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BorrowedRecordModel {
    protected String recordId;
    protected String readerId;
    protected String bookId;
    protected RecordStatus status;
    /**
     * 借书日期
     */
    @JsonFormat(pattern="yyyy-MM-dd")
    protected LocalDate borrowDate;
    /**
     * 应还日期
     */
    @JsonFormat(pattern="yyyy-MM-dd")
    protected LocalDate shouldReturnDate;

    public BorrowedRecordModel(BorrowedRecordModel other) {
        this.recordId = other.getRecordId();
        this.bookId = other.getBookId();
        this.readerId = other.getReaderId();
        this.borrowDate = other.getBorrowDate();
        this.status = other.getStatus();
        this.shouldReturnDate = other.getShouldReturnDate();
    }
}
