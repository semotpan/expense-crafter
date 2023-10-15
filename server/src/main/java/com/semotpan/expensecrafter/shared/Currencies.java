package com.semotpan.expensecrafter.shared;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

/**
 * Interface to contain {@link CurrencyUnit} constants.
 *
 */
public interface Currencies {

    CurrencyUnit EURO = Monetary.getCurrency("EUR");

}
