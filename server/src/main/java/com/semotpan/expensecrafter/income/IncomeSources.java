package com.semotpan.expensecrafter.income;

import com.semotpan.expensecrafter.account.Account.AccountIdentifier;
import com.semotpan.expensecrafter.income.IncomeSource.IncomeSourceIdentifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
// TODO fix save anti pattern
public interface IncomeSources extends JpaRepository<IncomeSource, IncomeSourceIdentifier> {

    List<IncomeSource> findByAccount(AccountIdentifier account);

}
