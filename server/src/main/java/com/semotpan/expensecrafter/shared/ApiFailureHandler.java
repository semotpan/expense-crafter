package com.semotpan.expensecrafter.shared;

import com.semotpan.expensecrafter.shared.Failure.FieldViolation;
import com.semotpan.expensecrafter.shared.Failure.NotFoundFailure;
import com.semotpan.expensecrafter.shared.Failure.ValidationFailure;
import org.springframework.http.ResponseEntity;

import java.util.Collection;
import java.util.List;

import static com.semotpan.expensecrafter.shared.ApiErrorResponse.notFound;
import static com.semotpan.expensecrafter.shared.ApiErrorResponse.unprocessableEntity;

public final class ApiFailureHandler {

    public ResponseEntity<?> handle(Failure failure) {
        return switch (failure) {
            case NotFoundFailure(var message) -> notFound(message);
            case ValidationFailure(var msg, var fieldViolations) -> unprocessableEntity(map(fieldViolations), msg);
            default -> throw new IllegalArgumentException("No handler found!");
        };
    }

    private List<ApiErrorResponse.ApiErrorField> map(Collection<FieldViolation> fieldViolations) {
        return fieldViolations.stream()
                .map(f -> new ApiErrorResponse.ApiErrorField(f.field(), f.message(), f.rejectedValue()))
                .toList();
    }
}
