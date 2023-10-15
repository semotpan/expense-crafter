package com.semotpan.expensecrafter.expense

import com.fasterxml.jackson.databind.json.JsonMapper
import com.semotpan.expensecrafter.account.Account
import groovy.json.JsonOutput

class DataSamples {

    static MAPPER = JsonMapper.builder()
            .findAndAddModules()
            .build()

    static EXPENSE_COMMAND_REQUEST = [
            accountId  : "e2709aa2-7907-4f78-98b6-0f36a0c1b5ca",
            categoryId : "3b257779-a5db-4e87-9365-72c6f8d4977d",
            paymentType: "Cash",
            amount     : 10.0,
            expenseDate: "2023-10-13",
            description: "Books buying",
    ]

    static EXPENSE_CATEGORY = [
            id     : [id: "3b257779-a5db-4e87-9365-72c6f8d4977d"],
            account: [id: "e2709aa2-7907-4f78-98b6-0f36a0c1b5ca"],
            name   : "Fun"
    ]

    static AMOUNT = [
            amount  : 10.0,
            currency: "EUR"
    ]

    static EXPENSE = [
            id               : [id: "3b257779-a5db-4e87-9365-72c6f8d4977d"],
            account          : [id: "e2709aa2-7907-4f78-98b6-0f36a0c1b5ca"],
            creationTimestamp: "2023-10-10T18:28:04.224870Z",
            category         : EXPENSE_CATEGORY,
            amount           : AMOUNT,
            paymentType      : "CASH",
            expenseDate      : "2023-10-13",
            description      : "Books buying",
    ]

    static newSampleDefaultCategories(Account.AccountIdentifier account) {
        DefaultCategories.asList().stream()
                .map { c -> new Category(c, account) }
                .toList()
    }

    static newSampleCategory(map = [:]) {
        MAPPER.readValue(JsonOutput.toJson(EXPENSE_CATEGORY + map) as String, Category.class)
    }

    static newSampleExpenseCommandRequest(map = [:]) {
        MAPPER.readValue(JsonOutput.toJson(EXPENSE_COMMAND_REQUEST + map) as String, ExpenseCommandRequest.class)
    }

    static newSampleExpense(map = [:]) {
        MAPPER.readValue(JsonOutput.toJson(EXPENSE + map) as String, Expense.class)
    }
}
