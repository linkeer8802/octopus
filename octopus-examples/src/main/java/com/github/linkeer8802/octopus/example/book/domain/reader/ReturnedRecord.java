package com.github.linkeer8802.octopus.example.book.domain.reader;

import com.github.linkeer8802.octopus.example.book.domain.reader.fee.LongTermOverdueFeeCalculateStrategy;
import com.github.linkeer8802.octopus.example.book.domain.reader.fee.OverdueFeeCalculateStrategy;
import com.github.linkeer8802.octopus.example.book.domain.reader.fee.ShortTermOverdueFeeCalculateStrategy;
import com.github.linkeer8802.octopus.example.book.infrastructure.common.model.datamodel.ReturnedRecordModel;
import com.github.linkeer8802.octopus.example.transfer.infrastructure.common.util.Moneys;

import javax.money.MonetaryAmount;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 还书记录实体
 * @author weird
 * @date 2019/12/16
 */
public class ReturnedRecord extends ReturnedRecordModel {

    private static List<OverdueFeeCalculateStrategy> calculateStrategies = new ArrayList<>();

    static {
        calculateStrategies.add(new ShortTermOverdueFeeCalculateStrategy());
        calculateStrategies.add(new LongTermOverdueFeeCalculateStrategy());
    }

    ReturnedRecord(BorrowedRecord borrowedRecord, LocalDate returnDate) {
        super(borrowedRecord);
        this.returnDate = returnDate;
        this.status = RecordStatus.RETURNED;

        if (isOverdue()) {
            this.overdueDays = overdueDays().intValue();
            this.overdueFee = overdueFee();
        } else {
            this.overdueDays = 0;
            this.overdueFee = Moneys.of(0);
        }
    }

    /**
     * 是否逾期
     */
    private Boolean isOverdue() {
        return returnDate.isAfter(getShouldReturnDate());
    }

    /**
     * 逾期天数
     */
    private Long overdueDays() {
        return Duration.between(getShouldReturnDate().atStartOfDay(), returnDate.atStartOfDay()).toDays();
    }

    /**
     * 逾期费用
     */
    private MonetaryAmount overdueFee() {
        MonetaryAmount amount = Moneys.of(0);
        for (OverdueFeeCalculateStrategy calculateStrategy : calculateStrategies) {
            if (calculateStrategy.isSatisfy(getOverdueDays())) {
                MonetaryAmount fee = calculateStrategy.calculateFee(getOverdueDays());
                amount = amount.add(fee);
            }
        }
        return amount;
    }
}
