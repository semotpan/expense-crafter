package com.semotpan.expensecrafter.expense;

import com.semotpan.expensecrafter.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Categories extends JpaRepository<Category, Category.CategoryIdentifier> {

    List<Category> findByAccount(Account.AccountIdentifier account);

    boolean existsByIdAndAccount(Category.CategoryIdentifier id, Account.AccountIdentifier accountIdentifier);

}
