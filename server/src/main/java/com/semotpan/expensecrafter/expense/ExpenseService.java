package com.semotpan.expensecrafter.expense;

import com.semotpan.expensecrafter.account.Account;

public interface ExpenseService {

    /**
     * Init default expense categories for current account on registration process
     *
     * @param accountIdentifier - the current account identifier
     */
    void initDefaultCategories(Account.AccountIdentifier accountIdentifier);

}
