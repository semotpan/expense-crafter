package com.semotpan.expensecrafter.spendingplan;

import com.semotpan.expensecrafter.shared.Failure.FieldViolation;
import io.vavr.collection.Seq;
import io.vavr.control.Validation;

import java.math.BigDecimal;
import java.util.UUID;

import static com.semotpan.expensecrafter.spendingplan.PlanService.PlanCreateCommand;
import static com.semotpan.expensecrafter.spendingplan.PlanService.PlanCreateCommand.*;
import static io.vavr.API.Invalid;
import static io.vavr.API.Valid;
import static java.math.BigDecimal.ZERO;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.length;

final class PlanCreateCommandValidator {

    Validation<Seq<FieldViolation>, PlanCreateCommand> validate(PlanCreateCommand command) {
        return Validation.combine(
                validateName(command.name()),
                validateAccountId(command.accountId()),
                validateAmount(command.amount())
        ).ap((name, account, amount) -> command);
    }

    private Validation<FieldViolation, String> validateName(String name) {
        if (isBlank(name)) {
            return Invalid(FieldViolation.builder()
                    .field(FIELD_NAME)
                    .message("Name cannot be empty")
                    .rejectedValue(name)
                    .build());
        }

        if (length(name) > Plan.MAX_NAME_LENGTH) {
            return Invalid(FieldViolation.builder()
                    .field(FIELD_NAME)
                    .message("Name overflow, max length allowed '%d'".formatted(Plan.MAX_NAME_LENGTH))
                    .rejectedValue(name)
                    .build());
        }

        return Valid(name);
    }

    private Validation<FieldViolation, UUID> validateAccountId(UUID accountId) {
        if (nonNull(accountId))
            return Valid(accountId);

        return Invalid(FieldViolation.builder()
                .field(FIELD_ACCOUNT_ID)
                .message("AccountId cannot be null")
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
}
