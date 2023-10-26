package com.semotpan.expensecrafter.spendingplan;

import com.semotpan.expensecrafter.shared.Failure;
import com.semotpan.expensecrafter.spendingplan.Plan.PlanIdentifier;
import io.vavr.control.Either;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface PlanService {

    /**
     * Create a new {@link Plan}
     *
     * @param command - request value to build the spending plan
     * @return - Either.Left - Failure (validation schema, or duplicate plan name), Either.Right - created plan id
     */

    Either<Failure, PlanIdentifier> createPlan(PlanCreateCommand command);

    @Builder
    record PlanCreateCommand(UUID accountId,
                             BigDecimal amount,
                             String name,
                             String description,
                             List<JarCreateCommand> jars) {

        public static final String FIELD_ACCOUNT_ID = "accountId";
        public static final String FIELD_AMOUNT = "amount";
        public static final String FIELD_NAME = "name";
        public static final String FIELD_JARS = "jars";

        public List<JarCreateCommand> jars() {
            return jars == null ? List.of() : jars;
        }
    }

    record JarCreateCommand(String name,
                            Integer percentage,
                            String description) {

        public static final String FIELD_JAR_NAME = "name";
        public static final String FIELD_PERCENTAGE = "percentage";

    }
}
