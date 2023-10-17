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

    /**
     * Full update of an existing  {@link Income} and {@link IncomeUpdated} event is raised
     *
     * @param id      - income id to be updated
     * @param command - fields commands to be updated
     * @return - Either.Left - Failure (validation schema, or not found), Either.Right - Updated income
     */
    Either<Failure, Income> updateIncome(IncomeIdentifier id, IncomeCommand command);

    /**
     * Delete an {@link Income} by ID and {@link IncomeDeleted} event is raised
     *
     * @param id - income id to be deleted
     * @return - Either.Left - Failure (not found), Either.Right - no data (successfully deleted)
     */
    Either<Failure, Void> deleteIncome(IncomeIdentifier id);
}
