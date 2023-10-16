package com.semotpan.expensecrafter.expense;

import com.semotpan.expensecrafter.expense.Category.CategoryIdentifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.semotpan.expensecrafter.account.Account.AccountIdentifier;

@Repository
public interface Categories extends JpaRepository<Category, CategoryIdentifier> {

    List<Category> findByAccount(AccountIdentifier account);

    boolean existsByIdAndAccount(CategoryIdentifier id, AccountIdentifier accountIdentifier);

}
