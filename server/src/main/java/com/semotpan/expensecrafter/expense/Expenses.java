package com.semotpan.expensecrafter.expense;

import com.semotpan.expensecrafter.expense.Expense.ExpenseIdentifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// TODO improve the implementation using - extends @HibernateRepository<Expense>
// TODO fix jpa#save anti pattern
public interface Expenses extends JpaRepository<Expense, ExpenseIdentifier> {
}
