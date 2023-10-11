package com.semotpan.expensecrafter.expense.messaging;

import com.semotpan.expensecrafter.account.AccountCreated;
import com.semotpan.expensecrafter.expense.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component("expenseAccountEventsListener")
@RequiredArgsConstructor
class AccountEventsListener {

    private final ExpenseService expenseService;

    @ApplicationModuleListener
    public void on(AccountCreated event) {
        expenseService.initDefaultCategories(event.accountIdentifier());
    }
}
