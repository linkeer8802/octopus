package com.github.linkeer8802.octopus.example.transfer.infrastructure.common.util;

import org.javamoney.moneta.Money;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.util.Locale;

/**
 * @author weird
 */
public class Moneys {

    public static final MonetaryAmount ZERO = Moneys.of(0.00);

    public static MonetaryAmount of(Number number) {
        return Money.of(number, Monetary.getCurrency(Locale.getDefault()));
    }

    public static BigDecimal toBigDecimal(MonetaryAmount money) {
        return money.getNumber().numberValue(BigDecimal.class);
    }
}
