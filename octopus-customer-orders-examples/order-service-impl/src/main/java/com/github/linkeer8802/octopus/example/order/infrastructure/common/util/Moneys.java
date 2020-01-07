package com.github.linkeer8802.octopus.example.order.infrastructure.common.util;

import org.javamoney.moneta.Money;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.util.Locale;

/**
 * @author weird
 * @date 2019/12/2
 */
public final class Moneys {

    public static MonetaryAmount ZERO = of(0);

    public static MonetaryAmount of(Number number) {
        return Money.of(number, Monetary.getCurrency(Locale.getDefault()));
    }

    public static BigDecimal toBigDecimal(MonetaryAmount money) {
        return money.getNumber().numberValue(BigDecimal.class);
    }
}
