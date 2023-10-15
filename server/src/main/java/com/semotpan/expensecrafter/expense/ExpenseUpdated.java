package com.semotpan.expensecrafter.expense;

import com.semotpan.expensecrafter.account.Account.AccountIdentifier;
import com.semotpan.expensecrafter.expense.Expense.ExpenseIdentifier;
import com.semotpan.expensecrafter.shared.DomainEvent;
import com.semotpan.expensecrafter.shared.PaymentType;
import lombok.Builder;

import javax.money.MonetaryAmount;

@Builder
public record ExpenseUpdated(ExpenseIdentifier expenseId,
                             AccountIdentifier accountId,
                             MonetaryAmount amount,
                             PaymentType paymentType,
                             Category.CategoryIdentifier categoryId) implements DomainEvent {
}
