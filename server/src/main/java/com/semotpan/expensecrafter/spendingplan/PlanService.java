package com.semotpan.expensecrafter.spendingplan;

import com.semotpan.expensecrafter.shared.Failure;
import com.semotpan.expensecrafter.spendingplan.Plan.PlanIdentifier;
import io.vavr.control.Either;

import java.math.BigDecimal;
import java.util.UUID;

public interface PlanService {

    /**
     * Create a new {@link Plan}
     *
     * @param command - request value to build the spending plan
     * @return - Either.Left - Failure (validation schema, or duplicate plan name), Either.Right - created plan id
     */

    Either<Failure, PlanIdentifier> createPlan(PlanCreateCommand command);


    record PlanCreateCommand(UUID accountId,
                             BigDecimal amount,
                             String name,
                             String description) {

        public static final String FIELD_ACCOUNT_ID = "accountId";
        public static final String FIELD_AMOUNT = "amount";
        public static final String FIELD_NAME = "name";

    }
}
