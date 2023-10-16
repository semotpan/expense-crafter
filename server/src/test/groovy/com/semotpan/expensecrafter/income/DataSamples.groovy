package com.semotpan.expensecrafter.income

import com.semotpan.expensecrafter.account.Account

class DataSamples {

    static newSampleDefaultIncomeSources(Account.AccountIdentifier account) {
        DefaultIncomeSources.asList().stream()
                .map { is -> new IncomeSource(is, account) }
                .toList()
    }
}
