package com.github.linkeer8802.octopus.example.book.domain.reader.fee;

import com.github.linkeer8802.octopus.example.book.domain.reader.BorrowedRecord;
import com.github.linkeer8802.octopus.example.transfer.infrastructure.common.util.Moneys;

import javax.money.MonetaryAmount;

/**
 * 超期超过一个月的逾期费用计算策略类
 * @author weird
 * @date 2019/12/17
 */
public class LongTermOverdueFeeCalculateStrategy implements OverdueFeeCalculateStrategy {

    @Override
    public Boolean isSatisfy(Integer overdueDays) {
        return overdueDays > 30;
    }

    @Override
    public MonetaryAmount calculateFee(Integer overdueDays) {
        return Moneys.of(BorrowedRecord.DAY_OF_BORROW_PERIOD * 0.05).add(
                Moneys.of((overdueDays - BorrowedRecord.DAY_OF_BORROW_PERIOD) * 0.1));
    }
}
