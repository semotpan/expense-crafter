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
class PlanServiceImpl implements PlanService {

    private final PlanCreateCommandValidator planValidator = new PlanCreateCommandValidator();

    private final Plans plans;

    @Override
    public Either<Failure, PlanIdentifier> createPlan(PlanCreateCommand command) {
        var validation = planValidator.validate(command);
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

        var jars = command.jars().stream()
                .map(cmd -> Jar.builder()
                        .name(cmd.name())
                        .percentage(cmd.percentage())
                        .description(cmd.description())
                        .plan(plan)
                        .build())
                .toList();

        plan.addJars(jars);

        plans.save(plan);

        return Either.right(plan.getId());
    }
}
