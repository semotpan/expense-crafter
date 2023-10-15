package com.semotpan.expensecrafter.expense;

import com.semotpan.expensecrafter.shared.Failure.FieldViolation;
import com.semotpan.expensecrafter.shared.PaymentType;
import io.vavr.collection.Seq;
import io.vavr.control.Validation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static com.semotpan.expensecrafter.expense.ExpenseCommandRequest.*;
import static io.vavr.API.Invalid;
import static io.vavr.API.Valid;
import static java.math.BigDecimal.ZERO;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

final class ExpenseCommandRequestValidator {

    Validation<Seq<FieldViolation>, ExpenseCommandRequest> validate(ExpenseCommandRequest command) {
        return Validation.combine(
                validateAccountId(command.accountId()),
                validateCategoryId(command.categoryId()),
                validatePaymentType(command.paymentType()),
                validateAmount(command.amount()),
                validateExpenseDate(command.expenseDate())
        ).ap((accountId, categoryId, paymentType, amount, expenseDate) -> command);
    }

    private Validation<FieldViolation, UUID> validateAccountId(UUID accountId) {
        if (nonNull(accountId))
            return Valid(accountId);

        return Invalid(FieldViolation.builder()
                .field(FIELD_ACCOUNT_ID)
                .message("AccountId cannot be null")
                .build());
    }

    private Validation<FieldViolation, UUID> validateCategoryId(UUID categoryId) {
        if (nonNull(categoryId))
            return Valid(categoryId);

        return Invalid(FieldViolation.builder()
                .field(FIELD_CATEGORY_ID)
                .message("CategoryId cannot be null")
                .build());
    }

    private Validation<FieldViolation, String> validatePaymentType(String paymentType) {
        if (isNull(paymentType) || PaymentType.containsValue(paymentType))
            return Valid(paymentType);

        return Invalid(FieldViolation.builder()
                .field(FIELD_PAYMENT_TYPE)
                .message("PaymentType must be 'Cash' or 'Card'")
                .rejectedValue(paymentType)
                .build());
    }

    private Validation<FieldViolation, BigDecimal> validateAmount(BigDecimal amount) {
        if (nonNull(amount) && amount.compareTo(ZERO) > 0)
            return Valid(amount);

        var message = "Amount must be positive value";
        if (isNull(amount))
            message = "Amount cannot be null";

        return Invalid(FieldViolation.builder()
                .field(FIELD_AMOUNT)
                .message(message)
                .rejectedValue(amount)
                .build());
    }

    private Validation<FieldViolation, LocalDate> validateExpenseDate(LocalDate expenseDate) {
        if (nonNull(expenseDate))
            return Valid(expenseDate);

        return Invalid(FieldViolation.builder()
                .field(FIELD_EXPENSE_DATE)
                .message("ExpenseDate cannot be null")
                .build());
    }
}
