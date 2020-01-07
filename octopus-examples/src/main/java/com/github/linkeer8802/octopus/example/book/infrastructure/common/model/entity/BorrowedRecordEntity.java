package com.github.linkeer8802.octopus.example.book.infrastructure.common.model.entity;

import com.github.linkeer8802.octopus.example.book.domain.reader.RecordStatus;
import com.github.linkeer8802.octopus.example.book.infrastructure.common.model.datamodel.BorrowedRecordModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.money.MonetaryAmount;
import java.time.LocalDate;

/**
 * @author weird
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowedRecordEntity {
    private String id;
    private String readerId;
    private String bookId;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    /**
     * 预期天数
     */
    private Integer overdueDays;
    /**
     * 预期费用（罚金）
     */
    private MonetaryAmount overdueFee;
    private String status;

    public BorrowedRecordEntity(String id, String readerId, String bookId, LocalDate borrowDate, String status) {
        this.id = id;
        this.readerId = readerId;
        this.bookId = bookId;
        this.borrowDate = borrowDate;
        this.status = status;
    }

    public BorrowedRecordEntity(String id, String readerId, String bookId, LocalDate borrowDate,
                                Integer overdueDays, MonetaryAmount overdueFee, String status) {
        this.id = id;
        this.readerId = readerId;
        this.bookId = bookId;
        this.borrowDate = borrowDate;
        this.overdueDays = overdueDays;
        this.overdueFee = overdueFee;
        this.status = status;
    }

    public BorrowedRecordModel toDataModel() {
        return new BorrowedRecordModel(getId(),
                getReaderId(),
                getBookId(),
                (getStatus() == null || getStatus().isEmpty()) ? null : RecordStatus.valueOf(getStatus()),
                getBorrowDate(),
                getReturnDate());
    }
}
