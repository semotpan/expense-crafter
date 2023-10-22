package com.semotpan.expensecrafter.spendingplan;

import com.semotpan.expensecrafter.shared.Failure.FieldViolation;
import com.semotpan.expensecrafter.spendingplan.PlanService.JarCreateCommand;
import io.vavr.collection.Seq;
import io.vavr.control.Validation;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    private final JarCommandValidator jarCommandValidator = new JarCommandValidator();

    Validation<Seq<FieldViolation>, PlanCreateCommand> validate(PlanCreateCommand command) {
        return Validation.combine(
                validateName(command.name()),
                validateAccountId(command.accountId()),
                validateAmount(command.amount()),
                validateJars(command.jars())
        ).ap((name, account, amount, jars) -> command);
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

    /**
     * * Validates each jar using {@link JarCommandValidator}
     * * Check for duplicate jars name
     * * Check if percentage total for all jars is less or equal to 100
     *
     * @param jars - contains a list of {@link JarCreateCommand}
     * @return - Validation result
     */
    private Validation<FieldViolation, List<JarCreateCommand>> validateJars(List<JarCreateCommand> jars) {
        var percentages = 0;
        var names = new HashSet<String>(jars.size(), 1.0F);
        for (var i = 0; i < jars.size(); ++i) {
            var jar = jars.get(i);
            var validation = jarCommandValidator.validate(jar);
            if (validation.isInvalid()) {
                var errors = validation.getError().asJava().stream()
                        .map(FieldViolation::message)
                        .collect(Collectors.joining("; "));

                return Invalid(FieldViolation.builder()
                        .field(FIELD_JARS)
                        .message("Jars '%d' has validation failures: '%s'".formatted(i + 1, errors))
                        .build());
            }

            if (names.contains(jar.name())) {
                return Invalid(FieldViolation.builder()
                        .field(FIELD_JARS)
                        .message("Jars name '%s' already exists".formatted(jar.name()))
                        .build());
            }
            names.add(jar.name());
            percentages += jar.percentage();
        }

        if (percentages > 100) {
            return Invalid(FieldViolation.builder()
                    .field(FIELD_JARS)
                    .message("Jars total percentage sum must be max 100")
                    .rejectedValue("%d".formatted(percentages))
                    .build());
        }

        return Valid(jars);
    }
}
