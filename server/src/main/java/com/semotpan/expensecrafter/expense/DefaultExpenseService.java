package com.semotpan.expensecrafter.expense;

import com.semotpan.expensecrafter.account.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
class DefaultExpenseService implements ExpenseService {

    private final Categories categories;

    @Override
    public void initDefaultCategories(Account.AccountIdentifier accountIdentifier) {
        var values = DefaultCategories.asList().stream()
                .map(c -> new Category(c, accountIdentifier))
                .toList();

        categories.saveAll(values);
    }
}
