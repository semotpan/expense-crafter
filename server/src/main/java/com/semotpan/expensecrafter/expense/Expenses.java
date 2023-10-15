package com.semotpan.expensecrafter.expense;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Expenses extends CrudRepository<Expense, Expense.ExpenseIdentifier> {
}
