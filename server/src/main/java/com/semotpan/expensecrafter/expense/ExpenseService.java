package com.semotpan.expensecrafter.expense;

import com.semotpan.expensecrafter.account.Account.AccountIdentifier;
import com.semotpan.expensecrafter.expense.Expense.ExpenseIdentifier;
import com.semotpan.expensecrafter.shared.Failure;
import io.vavr.control.Either;

public interface ExpenseService {

    /**
     * Init {@link DefaultCategories} expense categories {@link Category} for current account on registration process
     *
     * @param accountIdentifier - the current account identifier
     */
    void initDefaultCategories(AccountIdentifier accountIdentifier);

    /**
     * Create a new {@link Expense} and {@link ExpenseCreated} is raised
     *
     * @param command - the request values to build an expense,
     * @return - Either.Left - Failure (validation schema, or not found), Either.Right - created expense id
     */
    Either<Failure, ExpenseIdentifier> createExpense(ExpenseCommandRequest command);

    /**
     * Full update of an existing  {@link Expense} and {@link ExpenseUpdated} is raised
     *
     * @param id      - expense id to be updated
     * @param command - fields commands to be updated
     * @return - Either.Left - Failure (validation schema, or not found), Either.Right - Updated expense
     */
    Either<Failure, Expense> updateExpense(ExpenseIdentifier id, ExpenseCommandRequest command);

    /**
     * Delete an {@link Expense} by ID and {@link ExpenseDeleted} is raised
     *
     * @param id - expense Id to be deleted
     * @return - Either.Left - Failure (not found), Either.Right - no data (successfully deleted)
     */
    Either<Failure, Void> deleteExpense(ExpenseIdentifier id);
}
