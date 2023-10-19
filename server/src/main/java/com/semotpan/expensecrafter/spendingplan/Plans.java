package com.semotpan.expensecrafter.spendingplan;

import com.semotpan.expensecrafter.account.Account.AccountIdentifier;
import com.semotpan.expensecrafter.spendingplan.Plan.PlanIdentifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Plans extends JpaRepository<Plan, PlanIdentifier> {

    boolean existsByNameAndAccount(String name, AccountIdentifier accountId);

}
