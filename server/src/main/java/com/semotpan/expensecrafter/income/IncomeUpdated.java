package com.semotpan.expensecrafter.income;

import com.semotpan.expensecrafter.account.Account.AccountIdentifier;
import com.semotpan.expensecrafter.income.Income.IncomeIdentifier;
import com.semotpan.expensecrafter.income.IncomeSource.IncomeSourceIdentifier;
import com.semotpan.expensecrafter.shared.DomainEvent;
import com.semotpan.expensecrafter.shared.PaymentType;
import lombok.Builder;

import javax.money.MonetaryAmount;

@Builder
public record IncomeUpdated(IncomeIdentifier incomeId,
                            AccountIdentifier accountId,
                            MonetaryAmount amount,
                            PaymentType paymentType,
                            IncomeSourceIdentifier incomeSourceId) implements DomainEvent {
}
