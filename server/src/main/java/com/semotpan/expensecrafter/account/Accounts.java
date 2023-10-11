package com.semotpan.expensecrafter.account;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Accounts extends CrudRepository<Account, Account.AccountIdentifier> {
}
