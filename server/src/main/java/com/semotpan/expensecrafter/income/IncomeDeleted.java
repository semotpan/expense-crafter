package com.semotpan.expensecrafter.income;

import com.semotpan.expensecrafter.account.Account.AccountIdentifier;
import com.semotpan.expensecrafter.income.Income.IncomeIdentifier;
import com.semotpan.expensecrafter.income.IncomeSource.IncomeSourceIdentifier;

public record IncomeDeleted(IncomeIdentifier id,
                            AccountIdentifier accountId,
                            IncomeSourceIdentifier incomeSourceId) {
}
