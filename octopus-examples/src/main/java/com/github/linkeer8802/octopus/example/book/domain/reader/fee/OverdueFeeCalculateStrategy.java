package com.github.linkeer8802.octopus.example.book.domain.reader.fee;

import com.google.common.collect.Lists;

import javax.money.MonetaryAmount;
import java.util.List;

/**
 * @author weird
 * @date 2019/12/17
 */
public interface OverdueFeeCalculateStrategy {
    /**
     * 是否满足策略
     * @param overdueDays 逾期天数
     * @return
     */
    Boolean isSatisfy(Integer overdueDays);
    /**
     * 计算预期费用
     * @param overdueDays 逾期天数
     * @return
     */
    MonetaryAmount calculateFee(Integer overdueDays);

    default OverdueFeeCalculateStrategy longTermOverdueFeeCalculateStrategy() {
        return new LongTermOverdueFeeCalculateStrategy();
    }

    default OverdueFeeCalculateStrategy shortTermOverdueFeeCalculateStrategy() {
        return new ShortTermOverdueFeeCalculateStrategy();
    }

    default List<OverdueFeeCalculateStrategy> allOverdueFeeCalculateStrategys() {
        return Lists.newArrayList(shortTermOverdueFeeCalculateStrategy(), longTermOverdueFeeCalculateStrategy());
    }
}
