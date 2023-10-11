package com.semotpan.expensecrafter.expense;

import com.semotpan.expensecrafter.account.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Categories extends CrudRepository<Category, Category.CategoryIdentifier> {

    List<Category> findByAccount(Account.AccountIdentifier account);

}
