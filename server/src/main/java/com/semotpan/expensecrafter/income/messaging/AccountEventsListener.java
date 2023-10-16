package com.semotpan.expensecrafter.income.messaging;

import com.semotpan.expensecrafter.account.AccountCreated;
import com.semotpan.expensecrafter.income.DefaultIncomeSources;
import com.semotpan.expensecrafter.income.IncomeSource;
import com.semotpan.expensecrafter.income.IncomeSources;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.ApplicationModuleListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Component("incomeAccountEventsListener")
@RequiredArgsConstructor
class AccountEventsListener {

    private final IncomeSources incomeSources;

    @ApplicationModuleListener
    @Transactional(propagation = REQUIRES_NEW)
    public void on(AccountCreated event) {
        var values = DefaultIncomeSources.asList().stream()
                .map(is -> new IncomeSource(is, event.accountIdentifier()))
                .toList();

        incomeSources.saveAll(values);
    }
}
