package com.github.linkeer8802.octopus.example.book.infrastructure.common.model.datamodel;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.linkeer8802.octopus.example.book.domain.reader.BorrowedRecord;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.money.MonetaryAmount;
import java.time.LocalDate;

/**
 * 还书记录数据模型
 * @author weird
 */
@Getter
@NoArgsConstructor
public class ReturnedRecordModel extends BorrowedRecordModel {
    /**
     * 还书日期
     */
    @JsonFormat(pattern="yyyy-MM-dd")
    protected LocalDate returnDate;
    /**
     * 预期天数
     */
    protected Integer overdueDays;
    /**
     * 预期费用（罚金）
     */
    protected MonetaryAmount overdueFee;

    public ReturnedRecordModel(BorrowedRecord borrowedRecord) {
        super(borrowedRecord);
    }

    public ReturnedRecordModel(ReturnedRecordModel other) {
        super(other);
        this.returnDate = other.returnDate;
        this.overdueDays = other.overdueDays;
        this.overdueFee = other.overdueFee;
    }
}
