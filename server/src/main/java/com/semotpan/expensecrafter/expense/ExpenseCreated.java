package com.semotpan.expensecrafter.expense;

import com.semotpan.expensecrafter.account.Account;
import com.semotpan.expensecrafter.shared.DomainEvent;
import com.semotpan.expensecrafter.shared.PaymentType;
import lombok.Builder;

import javax.money.MonetaryAmount;

@Builder
public record ExpenseCreated(Expense.ExpenseIdentifier expenseId,
                             Account.AccountIdentifier accountId,
                             MonetaryAmount amount,
                             PaymentType paymentType,
                             Category.CategoryIdentifier categoryId) implements DomainEvent {
}
