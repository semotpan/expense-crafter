package com.semotpan.expensecrafter.spendingplan;

import com.semotpan.expensecrafter.account.Account.AccountIdentifier;
import com.semotpan.expensecrafter.shared.Failure;
import com.semotpan.expensecrafter.spendingplan.Plan.PlanIdentifier;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import org.javamoney.moneta.Money;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.semotpan.expensecrafter.shared.Currencies.EURO;

@Service
@Transactional
@RequiredArgsConstructor
class DefaultPlanService implements PlanService {

    private final PlanCreateCommandValidator validator = new PlanCreateCommandValidator();

    private final Plans plans;

    @Override
    public Either<Failure, PlanIdentifier> createPlan(PlanCreateCommand command) {
        var validation = validator.validate(command);
        if (validation.isInvalid()) {
            return Either.left(Failure.ofValidation("Failures on spending plan create request", validation.getError().toJavaList()));
        }

        if (plans.existsByNameAndAccount(command.name(), new AccountIdentifier(command.accountId()))) {
            return Either.left(Failure.ofConflict("Spending plan name already exists"));
        }

        var plan = Plan.builder()
                .name(command.name())
                .account(new AccountIdentifier(command.accountId()))
                .amount(Money.of(command.amount(), EURO))
                .description(command.description())
                .build();

        plans.save(plan);

        return Either.right(plan.getId());
    }
}
