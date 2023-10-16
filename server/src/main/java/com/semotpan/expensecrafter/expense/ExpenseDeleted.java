package com.semotpan.expensecrafter.expense;

import com.semotpan.expensecrafter.account.Account.AccountIdentifier;
import com.semotpan.expensecrafter.expense.Category.CategoryIdentifier;
import com.semotpan.expensecrafter.expense.Expense.ExpenseIdentifier;
import com.semotpan.expensecrafter.shared.DomainEvent;
import lombok.Builder;

@Builder
public record ExpenseDeleted(ExpenseIdentifier expenseId,
                             AccountIdentifier accountId,
                             CategoryIdentifier categoryId) implements DomainEvent {
}
