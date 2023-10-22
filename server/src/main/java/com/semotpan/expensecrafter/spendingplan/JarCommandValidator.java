package com.semotpan.expensecrafter.spendingplan;

import com.semotpan.expensecrafter.shared.Failure.FieldViolation;
import com.semotpan.expensecrafter.spendingplan.PlanService.JarCreateCommand;
import io.vavr.collection.Seq;
import io.vavr.control.Validation;

import static com.semotpan.expensecrafter.spendingplan.PlanService.JarCreateCommand.FIELD_JAR_NAME;
import static com.semotpan.expensecrafter.spendingplan.PlanService.JarCreateCommand.FIELD_PERCENTAGE;
import static io.vavr.API.Invalid;
import static io.vavr.API.Valid;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.length;

final class JarCommandValidator {

    Validation<Seq<FieldViolation>, JarCreateCommand> validate(JarCreateCommand command) {
        return Validation.combine(
                validateName(command.name()),
                validatePercentage(command.percentage())
        ).ap((name, percentage) -> command);
    }

    private Validation<FieldViolation, String> validateName(String name) {
        if (isBlank(name)) {
            return Invalid(FieldViolation.builder()
                    .field(FIELD_JAR_NAME)
                    .message("Name cannot be empty")
                    .rejectedValue(name)
                    .build());
        }

        if (length(name) > Jar.MAX_NAME_LENGTH) {
            return Invalid(FieldViolation.builder()
                    .field(FIELD_JAR_NAME)
                    .message("Name overflow, max length allowed '%d'".formatted(Jar.MAX_NAME_LENGTH))
                    .rejectedValue(name)
                    .build());
        }

        return Valid(name);
    }

    private Validation<FieldViolation, Integer> validatePercentage(Integer percentage) {
        if (isNull(percentage))
            return Invalid(FieldViolation.builder()
                    .field(FIELD_PERCENTAGE)
                    .message("Percentage cannot be null")
                    .build());

        if (percentage < 0 || percentage > 100)
            return Invalid(FieldViolation.builder()
                    .field(FIELD_PERCENTAGE)
                    .message("Percentage value must be between 0 and 100")
                    .build());

        return Valid(percentage);
    }
}
