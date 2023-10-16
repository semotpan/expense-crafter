package com.semotpan.expensecrafter.income;

import com.semotpan.expensecrafter.income.Income.IncomeIdentifier;
import com.semotpan.expensecrafter.shared.Failure;
import com.semotpan.expensecrafter.shared.PaymentType;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import org.javamoney.moneta.Money;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.semotpan.expensecrafter.account.Account.AccountIdentifier;
import static com.semotpan.expensecrafter.income.IncomeSource.IncomeSourceIdentifier;
import static com.semotpan.expensecrafter.shared.Currencies.EURO;

@Service
@Transactional
@RequiredArgsConstructor
class DefaultIncomeService implements IncomeService {

    private final IncomeCommandValidator validator = new IncomeCommandValidator();

    private final IncomeSources incomeSources;
    private final Incomes incomes;

    @Override
    public Either<Failure, IncomeIdentifier> createIncome(IncomeCommand command) {
        var validation = validator.validate(command);

        if (validation.isInvalid()) {
            return Either.left(Failure.ofValidation("Failures on income create request", validation.getError().toJavaList()));
        }

        if (!incomeSources.existsByIdAndAccount(new IncomeSourceIdentifier(command.incomeSourceId()), new AccountIdentifier(command.accountId()))) {
            return Either.left(Failure.ofNotFound("Income Source or Account not found"));
        }

        var income = Income.builder()
                .account(new AccountIdentifier(command.accountId()))
                .amount(Money.of(command.amount(), EURO))
                .paymentType(command.paymentType() != null ? PaymentType.fromValue(command.paymentType()) : PaymentType.CARD)
                .incomeDate(command.incomeDate())
                .description(command.description())
                .incomeSource(incomeSources.getReferenceById(new IncomeSourceIdentifier(command.incomeSourceId())))
                .build();

        incomes.save(income);

        return Either.right(income.getId());
    }
}
