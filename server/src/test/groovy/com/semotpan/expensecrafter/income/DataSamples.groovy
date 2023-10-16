package com.semotpan.expensecrafter.income

import com.fasterxml.jackson.databind.json.JsonMapper
import com.semotpan.expensecrafter.account.Account
import groovy.json.JsonOutput

class DataSamples {

    static MAPPER = JsonMapper.builder()
            .findAndAddModules()
            .build()

    static INCOME_COMMAND = [
            accountId     : "e2709aa2-7907-4f78-98b6-0f36a0c1b5ca",
            incomeSourceId: "3b257779-a5db-4e87-9365-72c6f8d4977d",
            paymentType   : "Cash",
            amount        : 10,
            incomeDate    : "2023-10-16",
            description   : "Dividends"
    ]

    static INCOME_SOURCE = [
            id     : [id: "3b257779-a5db-4e87-9365-72c6f8d4977d"],
            account: [id: "e2709aa2-7907-4f78-98b6-0f36a0c1b5ca"],
            name   : "Business"
    ]

    static AMOUNT = [
            amount  : 10.0,
            currency: "EUR"
    ]

    static INCOME = [
            id               : [id: "3b257779-a5db-4e87-9365-72c6f8d4977d"],
            account          : [id: "e2709aa2-7907-4f78-98b6-0f36a0c1b5ca"],
            creationTimestamp: "2023-10-10T18:28:04.224870Z",
            incomeSource     : INCOME_SOURCE,
            amount           : AMOUNT,
            paymentType      : "CASH",
            incomeDate       : "2023-10-16",
            description      : "Dividends",
    ]

    static newSampleIncomeCommand(map = [:]) {
        MAPPER.readValue(JsonOutput.toJson(INCOME_COMMAND + map) as String, IncomeCommand.class)
    }

    static newSampleIncomeSource(map = [:]) {
        MAPPER.readValue(JsonOutput.toJson(INCOME_SOURCE + map) as String, IncomeSource.class)
    }

    static newSampleIncome(map = [:]) {
        MAPPER.readValue(JsonOutput.toJson(INCOME + map) as String, Income.class)
    }

    static newSampleDefaultIncomeSources(Account.AccountIdentifier account) {
        DefaultIncomeSources.asList().stream()
                .map { is -> new IncomeSource(is, account) }
                .toList()
    }
}
