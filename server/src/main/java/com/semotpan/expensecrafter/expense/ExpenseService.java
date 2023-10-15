package com.semotpan.expensecrafter.expense;

import com.semotpan.expensecrafter.account.Account.AccountIdentifier;
import com.semotpan.expensecrafter.expense.Expense.ExpenseIdentifier;
import com.semotpan.expensecrafter.shared.Failure;
import io.vavr.control.Either;

public interface ExpenseService {

    /**
     * Init default expense categories for current account on registration process
     *
     * @param accountIdentifier - the current account identifier
     */
    void initDefaultCategories(AccountIdentifier accountIdentifier);

    /**
     * Create a new expense
     *
     * @param command - the request values to build an expense,
     * @return - Either.Left - Failure (validation schema, or not found), Either.Right - created expense id
     */
    Either<Failure, ExpenseIdentifier> createExpense(ExpenseCommandRequest command);

    /**
     * Full update of an existing expense
     *
     * @param id      - expense id to be updated
     * @param command - fields commands to be updated
     * @return - - Either.Left - Failure (validation schema, or not found), Either.Right - Updated expense
     */
    Either<Failure, Expense> updateExpense(ExpenseIdentifier id, ExpenseCommandRequest command);

}
