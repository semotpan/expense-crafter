package com.semotpan.expensecrafter.income;

import com.semotpan.expensecrafter.income.Income.IncomeIdentifier;
import com.semotpan.expensecrafter.shared.Failure;
import io.vavr.control.Either;

public interface IncomeService {

    /**
     * Create a new {@link Income} and {@link IncomeCreated} event is raised
     *
     * @param command - the request values to build an income
     * @return -- Either.Left - Failure (validation schema, or not found), Either.Right - created income id
     */
    Either<Failure, IncomeIdentifier> createIncome(IncomeCommand command);

}
