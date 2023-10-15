package com.semotpan.expensecrafter.expense;

import com.semotpan.expensecrafter.account.Account;
import com.semotpan.expensecrafter.shared.Failure;
import io.vavr.control.Either;

public interface ExpenseService {

    /**
     * Init default expense categories for current account on registration process
     *
     * @param accountIdentifier - the current account identifier
     */
    void initDefaultCategories(Account.AccountIdentifier accountIdentifier);

    /**
     * Create a new expense
     *
     * @param command - the request values to build an expense,
     * @return - Either.Left - Failure (validation schema, or not found), Either.Right - created expense id
     */
    Either<Failure, Expense.ExpenseIdentifier> createExpense(ExpenseCommandRequest command);

}
