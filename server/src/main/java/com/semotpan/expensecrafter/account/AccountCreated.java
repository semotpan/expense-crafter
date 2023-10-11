package com.semotpan.expensecrafter.account;

import com.semotpan.expensecrafter.shared.DomainEvent;

import static java.util.Objects.requireNonNull;

public record AccountCreated(Account.AccountIdentifier accountIdentifier) implements DomainEvent {

    public AccountCreated {
        requireNonNull(accountIdentifier, "accountIdentifier cannot be null");
    }
}
