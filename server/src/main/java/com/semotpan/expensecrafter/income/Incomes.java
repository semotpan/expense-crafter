package com.semotpan.expensecrafter.income;

import com.semotpan.expensecrafter.income.Income.IncomeIdentifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// TODO fix jpa repos anti-patterns
public interface Incomes extends JpaRepository<Income, IncomeIdentifier> {
}
