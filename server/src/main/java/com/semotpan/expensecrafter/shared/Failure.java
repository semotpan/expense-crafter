package com.semotpan.expensecrafter.shared;

import lombok.Builder;

import java.util.Collection;

import static java.util.Objects.requireNonNull;

public interface Failure {

    static Failure ofValidation(String message, Collection<FieldViolation> fieldViolations) {
        return new ValidationFailure(message, fieldViolations);
    }

    static Failure ofNotFound(String message) {
        return new NotFoundFailure(message);
    }

    static Failure ofConflict(String message) {
        return new ConflictFailure(message);
    }

    record ValidationFailure(String message, Collection<FieldViolation> fieldViolations) implements Failure {
        public ValidationFailure {
            requireNonNull(fieldViolations, "fieldViolations cannot be null");
        }
    }

    record NotFoundFailure(String message) implements Failure {}

    record ConflictFailure(String message) implements Failure {}

    @Builder
    record FieldViolation(String field, String message, Object rejectedValue) {}
}
