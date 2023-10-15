package com.semotpan.expensecrafter.expense;

import com.semotpan.expensecrafter.account.Account.AccountIdentifier;
import com.semotpan.expensecrafter.expense.Category.CategoryIdentifier;
import com.semotpan.expensecrafter.expense.Expense.ExpenseIdentifier;
import com.semotpan.expensecrafter.shared.Failure;
import com.semotpan.expensecrafter.shared.PaymentType;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import org.javamoney.moneta.Money;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.semotpan.expensecrafter.shared.Currencies.EURO;

@Service
@Transactional
@RequiredArgsConstructor
class DefaultExpenseService implements ExpenseService {

    private final ExpenseCommandRequestValidator commandValidator = new ExpenseCommandRequestValidator();

    private final Categories categories;
    private final Expenses expenses;

    @Override
    public void initDefaultCategories(AccountIdentifier accountIdentifier) {
        var values = DefaultCategories.asList().stream()
                .map(c -> new Category(c, accountIdentifier))
                .toList();

        categories.saveAll(values);
    }

    @Override
    public Either<Failure, ExpenseIdentifier> createExpense(ExpenseCommandRequest command) {
        var validation = commandValidator.validate(command);

        if (validation.isInvalid()) {
            return Either.left(Failure.ofValidation("Failures on expense create request", validation.getError().toJavaList()));
        }

        if (!categories.existsByIdAndAccount(new CategoryIdentifier(command.categoryId()), new AccountIdentifier(command.accountId()))) {
            return Either.left(Failure.ofNotFound("Category or Account not found"));
        }

        var expense = Expense.builder()
                .account(new AccountIdentifier(command.accountId()))
                .amount(Money.of(command.amount(), EURO))
                .paymentType(command.paymentType() != null ? PaymentType.fromValue(command.paymentType()) : PaymentType.CARD)
                .expenseDate(command.expenseDate())
                .category(categories.getReferenceById(new CategoryIdentifier(command.categoryId())))
                .description(command.description())
                .build();

        expenses.save(expense);

        return Either.right(expense.getId());
    }

    @Override
    public Either<Failure, Expense> updateExpense(ExpenseIdentifier id, ExpenseCommandRequest command) {
        var validation = commandValidator.validate(command);

        if (validation.isInvalid()) {
            return Either.left(Failure.ofValidation("Failures on expense update request", validation.getError().toJavaList()));
        }

        if (!categories.existsByIdAndAccount(new CategoryIdentifier(command.categoryId()), new AccountIdentifier(command.accountId()))) {
            return Either.left(Failure.ofNotFound("Category or Account not found"));
        }

        var expense = expenses.findById(id);
        if (expense.isEmpty()) {
            return Either.left(Failure.ofNotFound("Expense not found"));
        }

        expense.get().update(
                Money.of(command.amount(), EURO),
                command.paymentType() != null ? PaymentType.fromValue(command.paymentType()) : PaymentType.CARD,
                command.expenseDate(),
                command.description(),
                categories.getReferenceById(new CategoryIdentifier(command.categoryId()))
        );

        expenses.save(expense.get());

        return Either.right(expense.get());
    }
}
