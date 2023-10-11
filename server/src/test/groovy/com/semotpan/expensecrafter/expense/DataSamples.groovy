package com.semotpan.expensecrafter.expense

import com.semotpan.expensecrafter.account.Account

class DataSamples {

    static newSampleDefaultCategories(Account.AccountIdentifier account) {
        DefaultCategories.asList().stream()
                .map { c -> new Category(c, account) }
                .toList()
    }
}
