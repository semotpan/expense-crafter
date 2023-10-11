package com.semotpan.expensecrafter.account;

import com.semotpan.expensecrafter.shared.DomainEvent;

public record AccountCreated(Account.AccountIdentifier accountIdentifier) implements DomainEvent {
}
